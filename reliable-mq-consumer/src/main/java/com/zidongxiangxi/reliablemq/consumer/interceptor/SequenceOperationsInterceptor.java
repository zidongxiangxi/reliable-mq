package com.zidongxiangxi.reliablemq.consumer.interceptor;

import com.zidongxiangxi.reliabelmq.api.constant.MessageHeaderConstants;
import com.zidongxiangxi.reliabelmq.api.entity.enums.MessageTypeEnum;
import com.zidongxiangxi.reliabelmq.api.manager.ConsumeRecordManager;
import com.zidongxiangxi.reliabelmq.api.util.RabbitUtils;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.ImmediateRequeueAmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * 顺序消费拦截器
 *
 * @author chenxudong
 * @date 2019/09/18
 */
@Slf4j
public class SequenceOperationsInterceptor extends AbstractConsumerOperationsInterceptor {
    private ConsumeRecordManager consumeRecordManager;
    private int consumeFailDelay;
    private int faultTolerantTime;

    public SequenceOperationsInterceptor(ConsumeRecordManager consumeRecordManager, int consumeFailDelay,
        int faultTolerantTime) {
        this.consumeRecordManager = consumeRecordManager;
        this.consumeFailDelay = consumeFailDelay;
        if (faultTolerantTime < 0) {
            this.faultTolerantTime = Integer.MAX_VALUE;
        } else {
            this.faultTolerantTime = faultTolerantTime;
        }
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Message message = getMessage(methodInvocation);
        if (Objects.isNull(message)
            || Objects.isNull(message.getMessageProperties())
            || Objects.isNull(message.getMessageProperties().getMessageId())
            || CollectionUtils.isEmpty(message.getMessageProperties().getHeaders())) {
            return methodInvocation.proceed();
        }
        String application = RabbitUtils.getApplication(message);
        String messageId = RabbitUtils.getMessageId(message);
        if (StringUtils.isEmpty(application) || StringUtils.isEmpty(messageId)) {
            return methodInvocation.proceed();
        }
        // 不是顺序消息，直接执行
        if (Objects.equals(RabbitUtils.getMessageType(message), MessageTypeEnum.SEQUENCE.getValue())) {
            return methodInvocation.proceed();
        }
        // 没有分组内的上一个消息id，直接执行
        Object previousMessageIdObject =
            message.getMessageProperties().getHeaders().get(MessageHeaderConstants.MESSAGE_PREVIOUS_ID_HEADER);
        if (Objects.isNull(previousMessageIdObject)
            || !(previousMessageIdObject instanceof String)
            || StringUtils.isEmpty(previousMessageIdObject)) {
            return methodInvocation.proceed();
        }
        String previousMessageId = (String) previousMessageIdObject;
        Date msgTimestamp = RabbitUtils.getTimestamp(message);
        // 判断已经可以消费
        if (!isCanConsume(application, messageId, previousMessageId, msgTimestamp)) {
            if (consumeFailDelay > 0) {
                try {
                    Thread.sleep(consumeFailDelay);
                } catch (InterruptedException ignore) {}
            } else {
                throw new ImmediateRequeueAmqpException("can not consume this message yet");
            }
        }
        // 延迟后还不能消费，就要丢回队列了
        if (!isCanConsume(application, messageId, previousMessageId, msgTimestamp)) {
            throw new ImmediateRequeueAmqpException("can not consume this message yet");
        }
        Object result = methodInvocation.proceed();
        saveRecord(application, messageId);
        return result;
    }

    private boolean isCanConsume(String application, String messageId, String previousMessageId, Date msgTimestamp) {
        boolean canConsume = false;
        try {
            canConsume = consumeRecordManager.isConsumed(application, previousMessageId);
        } catch (Throwable e) {
            log.error("fail to check sequence is can consume, message id: {}", messageId, e);
        }
        if (!canConsume
            && Objects.nonNull(msgTimestamp)
            && Math.abs(msgTimestamp.getTime() - System.currentTimeMillis()) >= faultTolerantTime) {
            canConsume = true;
        }
        return canConsume;
    }

    private void saveRecord(String application, String messageId) {
        // 成功执行，尝试添加消费记录，catch所有异常，不影响mq的消费
        // 如果开启了幂等消费，则由幂等拦截器添加消费记录，下面语句不会添加成功
        try {
            consumeRecordManager.insertConsumeRecord(application, messageId);
        } catch (Throwable e) {
            log.error("fail to save consume record", e);
        }
    }
}
