package com.zidongxiangxi.reliablemq.producer.service;

import com.alibaba.fastjson.JSON;
import com.zidongxiangxi.reliabelmq.api.alarm.Alarm;
import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.entity.enums.MessageTypeEnum;
import com.zidongxiangxi.reliabelmq.api.manager.SequenceManager;
import com.zidongxiangxi.reliabelmq.api.producer.RabbitService;
import com.zidongxiangxi.reliabelmq.api.util.RabbitUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Objects;

/**
 * 异步的rabbit服务
 *
 * @author chenxudong
 * @date 2019/09/12
 */
@Slf4j
public class ClientRabbitServiceImpl implements RabbitService {
    private SequenceManager sequenceManager;
    private RabbitTemplate rabbitTemplate;
    private Alarm alarm;

    public ClientRabbitServiceImpl(SequenceManager sequenceManager, RabbitTemplate rabbitTemplate,
        ObjectProvider<Alarm> alarmProvider) {
        this.sequenceManager = sequenceManager;
        this.rabbitTemplate = rabbitTemplate;
        if (Objects.nonNull(alarmProvider)) {
            try {
                this.alarm = alarmProvider.getIfUnique();
            } catch (Throwable ignore) {}
        }
    }

    public ClientRabbitServiceImpl(SequenceManager sequenceManager, RabbitTemplate rabbitTemplate, Alarm alarm) {
        this.sequenceManager = sequenceManager;
        this.rabbitTemplate = rabbitTemplate;
        this.alarm = alarm;
    }


    @Async
    @Override
    public void send(List<RabbitProducer> producerList) {
        producerList.forEach(producer -> {
            try {
                send(producer);
            } catch (Throwable t) {
                log.error("fail to invoke rabbitTemplate to send message", t);
            }
        });
    }

    @Async
    @Override
    public void send(RabbitProducer producer) {
        try {
            Message message;
            if (Objects.nonNull(sequenceManager)
                && Objects.equals(producer.getType(), MessageTypeEnum.SEQUENCE.getValue())) {
                String previousMessageId = sequenceManager.getPreviousMessageId(producer.getMessageId(),
                    producer.getApplication());
                message = RabbitUtils.generateMessage(producer, previousMessageId);
            } else {
                message = RabbitUtils.generateMessage(producer);
            }
            CorrelationData correlationData = RabbitUtils.generateCorrelationData(producer);
            rabbitTemplate.send(producer.getExchange(), producer.getRoutingKey(), message, correlationData);
        } catch (Throwable throwable) {
            log.error("send message failed producer={}", producer.toString(), throwable);
            if (Objects.nonNull(alarm)) {
                alarm.failWhenProduce(JSON.toJSONString(producer), throwable);
            }
        }
    }

    @Override
    public String getVirtualHost() {
        return rabbitTemplate.getConnectionFactory().getVirtualHost();
    }
}
