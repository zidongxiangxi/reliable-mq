package com.zidongxiangxi.reliablemq.consumer.interceptor;

import com.rabbitmq.client.Channel;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.core.Message;

/**
 * 抽象的消费者拦截器
 *
 * @author chenxudong
 * @date 2019/09/18
 */
public abstract class AbstractRabbitOperationsInterceptor implements MethodInterceptor {
    protected Message getMessage(MethodInvocation methodInvocation) {
        Object[] args = methodInvocation.getArguments();
        for (Object arg : args) {
            if (arg instanceof Message) {
                return (Message)arg;
            }
        }
        return null;
    }

    protected Channel getChannel(MethodInvocation methodInvocation) {
        Object[] args = methodInvocation.getArguments();
        for (Object arg : args) {
            if (arg instanceof Channel) {
                return (Channel)arg;
            }
        }
        return null;
    }
}
