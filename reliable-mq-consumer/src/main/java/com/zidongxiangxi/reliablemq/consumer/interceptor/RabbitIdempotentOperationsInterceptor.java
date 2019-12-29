package com.zidongxiangxi.reliablemq.consumer.interceptor;

import com.zidongxiangxi.reliabelmq.api.manager.ConsumeRecordManager;
import com.zidongxiangxi.reliabelmq.api.util.RabbitUtils;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.core.Message;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 支持事务的幂等拦截器
 *
 * @author chenxudong
 * @date 2019/10/11
 */
@Slf4j
public class RabbitIdempotentOperationsInterceptor extends AbstractRabbitOperationsInterceptor {
    private ConsumeRecordManager consumeRecordManager;
    private TransactionTemplate transactionTemplate;

    public RabbitIdempotentOperationsInterceptor(
            ConsumeRecordManager consumeRecordManager,
            TransactionTemplate transactionTemplate) {
        this.consumeRecordManager = consumeRecordManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Message message = getMessage(invocation);
        if (Objects.isNull(message)) {
            return invocation.proceed();
        }
        String messageId = RabbitUtils.getMessageId(message);
        if (StringUtils.isEmpty(messageId)) {
            return invocation.proceed();
        }

        String application = RabbitUtils.getApplication(message);
        if (StringUtils.isEmpty(application)) {
            return invocation.proceed();
        }

        if (consumeRecordManager.isConsumed(application, messageId)) {
            log.warn("message[{}, {}] is consumed, ignore it", application, message);
            return null;
        }
        return doBusiness(invocation, application, messageId);
    }

    private Object doBusiness(MethodInvocation invocation, String application, String messageId) throws RuntimeException {
        return transactionTemplate.execute((transactionStatus) -> {
            try {
                if (!consumeRecordManager.insertConsumeRecord(application, messageId)) {
                    return null;
                }
                return invocation.proceed();
            } catch (Throwable throwable) {
                log.error("fail to consume message", throwable);
                throw new RuntimeException(throwable);
            }
        });
    }

}
