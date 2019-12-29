package com.zidongxiangxi.reliable.starter.processor.rabbit.consumer;

import com.zidongxiangxi.reliablemq.consumer.interceptor.SequenceOperationsInterceptor;
import com.zidongxiangxi.reliablemq.consumer.interceptor.TransactionIdempotentOperationsInterceptor;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * rabbit监听容器的后置处理类
 * 用于自己定义的RabbitListenerContainer
 *
 * @author chenxudong
 * @date 2019/12/27
 */
public class RabbitListenerContainerBeanPostProcessor implements BeanPostProcessor, Ordered, BeanFactoryAware {
    private BeanFactory beanFactory;
    private SequenceOperationsInterceptor sequenceInterceptor = null;
    private RetryOperationsInterceptor retryInterceptor = null;
    private TransactionIdempotentOperationsInterceptor idempotentInterceptor = null;

    public RabbitListenerContainerBeanPostProcessor(
        ObjectProvider<SequenceOperationsInterceptor> sequenceInterceptorProvider,
        ObjectProvider<RetryOperationsInterceptor> retryInterceptorProvider,
        ObjectProvider<TransactionIdempotentOperationsInterceptor> idempotentInterceptorProvider
    ) {
        try {
            sequenceInterceptor = sequenceInterceptorProvider.getIfUnique();
        } catch (Throwable ignore) {}
        try {
            retryInterceptor = retryInterceptorProvider.getIfUnique();
        } catch (Throwable ignore) {}
        try {
            idempotentInterceptor = idempotentInterceptorProvider.getIfUnique();
        } catch (Throwable ignore) {}
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 1;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (Objects.isNull(idempotentInterceptor) && Objects.isNull(sequenceInterceptor) && Objects.isNull(retryInterceptor)) {
            return bean;
        }
        if (!(bean instanceof AbstractMessageListenerContainer)) {
            return bean;
        }
        Field adviceChainField = null;
        try {
            adviceChainField = AbstractMessageListenerContainer.class.getDeclaredField("adviceChain");
            adviceChainField.setAccessible(true);
        } catch (NoSuchFieldException ignore) {}
        if (Objects.isNull(adviceChainField)) {
            return bean;
        }
        Advice[] advices = (Advice[])ReflectionUtils.getField(adviceChainField, bean);
        boolean needSetIdempotent = Objects.nonNull(idempotentInterceptor);
        boolean needSetSequence = Objects.nonNull(sequenceInterceptor);
        boolean needSetRetry = Objects.nonNull(retryInterceptor);
        if (Objects.nonNull(advices)) {
            for (Advice advice : advices) {
                if (needSetIdempotent && advice instanceof TransactionIdempotentOperationsInterceptor) {
                    needSetIdempotent = false;
                } else if (needSetSequence && advice instanceof SequenceOperationsInterceptor) {
                    needSetSequence = false;
                } else if (needSetRetry && advice instanceof RetryOperationsInterceptor) {
                    needSetRetry = false;
                }
            }
        }
        List<Advice> adviceList = new LinkedList<>();
        if (needSetSequence) {
            adviceList.add(sequenceInterceptor);
        }
        if (Objects.nonNull(advices) && advices.length > 0) {
            Collections.addAll(adviceList, advices);
        }
        if (needSetRetry) {
            adviceList.add(retryInterceptor);
        }
        if (needSetIdempotent) {
            adviceList.add(idempotentInterceptor);
        }
        AbstractMessageListenerContainer messageListenerContainer = (AbstractMessageListenerContainer)bean;
        // 强制消费失败是返回队列，除非走到了MessageRecover
        messageListenerContainer.setDefaultRequeueRejected(true);
        messageListenerContainer.setAdviceChain(adviceList.toArray(new Advice[0]));
        return bean;
    }
}
