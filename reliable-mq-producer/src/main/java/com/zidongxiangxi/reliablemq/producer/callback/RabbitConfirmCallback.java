package com.zidongxiangxi.reliablemq.producer.callback;

import com.alibaba.fastjson.JSON;
import com.zidongxiangxi.reliabelmq.api.alarm.Alarm;
import com.zidongxiangxi.reliabelmq.api.entity.rabbit.RabbitCorrelationId;
import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.exception.ReliableMqException;
import com.zidongxiangxi.reliabelmq.api.manager.ProduceRecordManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * rabbitMq消息发送确认
 *
 * @author chenxudong
 * @date 2019/08/31
 */
@Slf4j
public class RabbitConfirmCallback implements RabbitTemplate.ConfirmCallback {
    private ProduceRecordManager<RabbitProducer> producerManager;
    private Alarm alarm;

    public RabbitConfirmCallback(ProduceRecordManager<RabbitProducer> producerManager, Alarm alarm) {
        this.producerManager = producerManager;
        this.alarm = alarm;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        try {
            if (Objects.nonNull(correlationData) && !StringUtils.isEmpty(correlationData.getId())) {
                String correlationDataJsonString = JSON.toJSONString(correlationData);
                log.info("receive publish confirm, correlationData: {}", correlationDataJsonString);
                RabbitCorrelationId id = RabbitCorrelationId.parseRabbitCorrelationId(correlationData.getId());
                if (Objects.isNull(id)) {
                    log.info("fail to parse correlationId:{}", correlationData.getId());
                    return;
                }
                if (ack) {
                    log.info("success to send message, correlationId:{}", correlationData.getId());
                    producerManager.deleteRecord(id.getApplication(), id.getMessageId());
                } else {
                    throw new ReliableMqException("fail to send message, correlationId:" + correlationData.getId());
                }
            } else {
                if (ack) {
                    log.warn("success to send message, but lack of message id");
                } else {
                    throw new ReliableMqException("fail to send message and lack of message id");
                }
            }
        } catch (ReliableMqException e) {
            log.warn("send mq confirm fail", e);
            if (Objects.nonNull(alarm)) {
                alarm.failWhenProduce(e);
            }
        }
    }
}
