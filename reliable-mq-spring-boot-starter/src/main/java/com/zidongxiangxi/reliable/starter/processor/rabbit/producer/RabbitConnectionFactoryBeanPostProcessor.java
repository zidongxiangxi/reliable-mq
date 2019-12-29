package com.zidongxiangxi.reliable.starter.processor.rabbit.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * rabbit连接工厂bean的后置加工
 *
 * @author chenxudong
 * @date 2019/12/27
 */
@Slf4j
public class RabbitConnectionFactoryBeanPostProcessor implements BeanPostProcessor, Ordered {

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 2;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (!(bean instanceof CachingConnectionFactory)) {
            return bean;
        }
        Field publisherConfirmsField = null;
        try {
            publisherConfirmsField = CachingConnectionFactory.class.getDeclaredField("publisherConfirms");
            publisherConfirmsField.setAccessible(true);
        } catch (NoSuchFieldException ignore) {}
        if (Objects.isNull(publisherConfirmsField)) {
            return bean;
        }
        try {
            publisherConfirmsField.set(bean, true);
        } catch (IllegalAccessException e) {
            log.warn("RabbitConnectionFactoryBeanPostProcessor fail to set publisherConfirms attribute to true value");
        }
        return bean;
    }
}
