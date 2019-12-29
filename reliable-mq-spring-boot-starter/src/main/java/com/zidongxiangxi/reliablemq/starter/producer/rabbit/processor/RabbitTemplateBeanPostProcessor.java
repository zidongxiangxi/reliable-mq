package com.zidongxiangxi.reliablemq.starter.producer.rabbit.processor;

import com.zidongxiangxi.reliabelmq.api.alarm.Alarm;
import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.manager.ProduceRecordManager;
import com.zidongxiangxi.reliablemq.producer.callback.RabbitConfirmCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import java.util.Objects;

/**
 * rabbit相关bean的后置加工
 *
 * @author chenxudong
 * @date 2019/09/09
 */
@Slf4j
public class RabbitTemplateBeanPostProcessor implements BeanPostProcessor, Ordered {
    private ProduceRecordManager<RabbitProducer> producerManager;
    private Alarm alarm;

    public RabbitTemplateBeanPostProcessor(ProduceRecordManager<RabbitProducer> producerManager,
        ObjectProvider<Alarm> alarmProvider) {
        this.producerManager = producerManager;
        if (Objects.nonNull(alarmProvider)) {
            try {
                this.alarm = alarmProvider.getIfUnique();
            } catch (Throwable ignore) {}
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (Objects.isNull(producerManager) || !(bean instanceof RabbitTemplate)) {
            return bean;
        }
        RabbitTemplate rabbitTemplate = (RabbitTemplate) bean;
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitConfirmCallback(producerManager, alarm));
        return bean;
    }
}
