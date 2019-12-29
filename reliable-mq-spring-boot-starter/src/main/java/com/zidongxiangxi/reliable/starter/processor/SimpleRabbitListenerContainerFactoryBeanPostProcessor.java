package com.zidongxiangxi.reliable.starter.processor;

import com.zidongxiangxi.reliablemq.consumer.interceptor.RabbitSequenceOperationsInterceptor;
import com.zidongxiangxi.reliablemq.consumer.interceptor.RabbitIdempotentOperationsInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * rabbit相关bean的后置加工
 * 用于rabbitmq的stater产生的RabbitListenerContainer
 *
 * @author chenxudong
 * @date 2019/09/09
 */
@Slf4j
public class SimpleRabbitListenerContainerFactoryBeanPostProcessor
    implements BeanPostProcessor, Ordered, BeanFactoryAware {
    private BeanFactory beanFactory;
    private RabbitSequenceOperationsInterceptor sequenceInterceptor = null;
    private RetryOperationsInterceptor retryInterceptor = null;
    private RabbitIdempotentOperationsInterceptor idempotentInterceptor = null;

    public SimpleRabbitListenerContainerFactoryBeanPostProcessor(
        ObjectProvider<RabbitSequenceOperationsInterceptor> sequenceInterceptorProvider,
        ObjectProvider<RetryOperationsInterceptor> retryInterceptorProvider,
        ObjectProvider<RabbitIdempotentOperationsInterceptor> idempotentInterceptorProvider
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
        return LOWEST_PRECEDENCE - 2;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (Objects.isNull(idempotentInterceptor) && Objects.isNull(sequenceInterceptor) && Objects.isNull(retryInterceptor)) {
            return bean;
        }
        if (!(bean instanceof SimpleRabbitListenerContainerFactory)
            || !Objects.equals("rabbitListenerContainerFactory", beanName)) {
            return bean;
        }

        SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory =
            (SimpleRabbitListenerContainerFactory) bean;
        Advice[] advices = rabbitListenerContainerFactory.getAdviceChain();
        boolean needSetIdempotent = Objects.nonNull(idempotentInterceptor);
        boolean needSetSequence = Objects.nonNull(sequenceInterceptor);
        boolean needSetRetry = Objects.nonNull(retryInterceptor);
        if (Objects.nonNull(advices)) {
            for (Advice advice : advices) {
                if (needSetIdempotent && advice instanceof RabbitIdempotentOperationsInterceptor) {
                    needSetIdempotent = false;
                } else if (needSetSequence && advice instanceof RabbitSequenceOperationsInterceptor) {
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
        rabbitListenerContainerFactory.setAdviceChain(adviceList.toArray(new Advice[0]));
        return bean;
    }
}
