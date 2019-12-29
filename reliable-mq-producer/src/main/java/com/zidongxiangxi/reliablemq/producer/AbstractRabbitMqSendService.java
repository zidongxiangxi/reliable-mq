package com.zidongxiangxi.reliablemq.producer;

import com.alibaba.fastjson.JSON;
import com.zidongxiangxi.reliabelmq.api.constant.ProducerConstants;
import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.entity.enums.MessageTypeEnum;
import com.zidongxiangxi.reliabelmq.api.producer.RabbitMqSendService;
import com.zidongxiangxi.reliabelmq.api.producer.RabbitService;
import com.zidongxiangxi.reliablemq.producer.transaction.RabbitProducerTransactionMessageHolder;
import com.zidongxiangxi.reliablemq.producer.transaction.RabbitTransactionContext;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;

/**
 * rabbitMq消息发送接口抽象类
 *
 * @author chenxudong
 * @date 2019/09/17
 */
public abstract class AbstractRabbitMqSendService implements RabbitMqSendService {
    private TransactionSynchronization transactionSynchronization;
    private String application;

    protected RabbitService rabbitService;

    protected AbstractRabbitMqSendService(
        TransactionSynchronization transactionSynchronization,
        String application
    ) {
        this.transactionSynchronization = transactionSynchronization;
        this.application = application;
    }

    @Override
    public void sendToExchange(String exchange, Object msgBody) {
        send(exchange, null, msgBody);
    }

    @Override
    public void sendToQueue(String queue, Object msgBody) {
        send(null, queue, msgBody);
    }

    @Override
    public void send(String exchange, String routingKey, Object msgBody) {
        send(null, exchange, routingKey, msgBody);
    }

    @Override
    public void sendToExchangeSequentially(String group, String exchange, Object msgBody) {
        sendSequentially(group, exchange, null, msgBody);
    }

    @Override
    public void sendToQueueSequentially(String group, String queue, Object msgBody) {
        sendSequentially(group, null, queue, msgBody);
    }

    @Override
    public void sendSequentially(String group, String exchange, String routingKey, Object msgBody) {
        sendSequentially(null, group, exchange, routingKey, msgBody);
    }

    protected boolean canUseTransactionSynchronization() {
        return Objects.nonNull(transactionSynchronization) && TransactionSynchronizationManager.isSynchronizationActive() && TransactionSynchronizationManager.isActualTransactionActive();
    }

    protected void registerTransactionSynchronization() {
        TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);
    }

    protected void stashProducer(RabbitProducer producer) {
        RabbitProducerTransactionMessageHolder messageHolder = RabbitTransactionContext.getMessageHolder();
        if (messageHolder == null) {
            messageHolder = new RabbitProducerTransactionMessageHolder();
            RabbitTransactionContext.setMessageHolder(messageHolder);
        }
        messageHolder.add(producer);
    }

    protected RabbitProducer generateImmediateProducer(String messageId, String virtualHost,
        String exchange, String routingKey, Object msgBody) {
        RabbitProducer producer = generateProducer(messageId, virtualHost, exchange, routingKey, msgBody);
        producer.setType(MessageTypeEnum.IMMEDIATE.getValue());
        return producer;
    }

    protected RabbitProducer generateSequenceProducer(String messageId, String groupName,
        String virtualHost, String exchange, String routingKey, Object msgBody) {
        RabbitProducer producer = generateProducer(messageId, virtualHost, exchange, routingKey, msgBody);
        producer.setType(MessageTypeEnum.SEQUENCE.getValue());
        producer.setGroupName(groupName);
        return producer;
    }

    private RabbitProducer generateProducer(String messageId, String virtualHost, String exchange, String routingKey, Object msgBody) {
        RabbitProducer producer = new RabbitProducer();
        producer.setMessageId(messageId);
        producer.setApplication(application);
        producer.setVirtualHost(virtualHost);
        producer.setExchange(exchange);
        producer.setRoutingKey(routingKey);
        producer.setBody(msgBody instanceof String ? (String) msgBody : JSON.toJSONString(msgBody));
        producer.setMaxRetryTimes(ProducerConstants.MAX_RETRY_TIMES);
        return producer;
    }
}
