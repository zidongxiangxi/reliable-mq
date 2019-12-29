package com.zidongxiangxi.reliablemq.producer;

import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.manager.ProducerManager;
import com.zidongxiangxi.reliabelmq.api.producer.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.UUID;

/**
 * 本地事务的rabbitMq消息发送接口
 *
 * @author chenxudong
 * @date 2019/09/14
 */
@Slf4j
public class DatabaseRabbitMqSendService extends AbstractRabbitMqSendService {
    private ProducerManager<RabbitProducer> producerManager;
    private String virtualHost;

    public DatabaseRabbitMqSendService(
        TransactionSynchronization transactionSynchronization,
        String application,
        RabbitService rabbitService,
        ProducerManager<RabbitProducer> producerManager
    ) {
        super(transactionSynchronization, application);
        this.rabbitService = rabbitService;
        this.virtualHost = rabbitService.getVirtualHost();
        this.producerManager = producerManager;
    }

    @Override
    public void send(String messageId, String exchange, String routingKey, Object msgBody) {
        messageId = StringUtils.isEmpty(messageId) ? UUID.randomUUID().toString() : messageId;
        RabbitProducer producer = generateImmediateProducer(messageId, virtualHost, exchange, routingKey, msgBody);
        if (canUseTransactionSynchronization()) {
            registerTransactionSynchronization();
            stashProducer(producer);
        } else if (Objects.nonNull(producerManager)) {
            try {
                producerManager.saveMqProducer(producer);
            } catch (Exception e) {
                log.error("fai to save message, [{}]", producer.toString(), e);
            }
            rabbitService.send(producer);
        } else {
            rabbitService.send(producer);
        }
    }

    /**
     * 发送顺序消息
     *
     * @param messageId 消息id
     * @param groupName 消息分组，同个分组内的消息顺序消费
     * @param exchange 交换器
     * @param routingKey 路由key
     * @param msgBody 消息体
     */
    @Override
    public void sendSequentially(String messageId, String groupName, String exchange, String routingKey,
        Object msgBody) {
        messageId = StringUtils.isEmpty(messageId) ? UUID.randomUUID().toString() : messageId;
        RabbitProducer producer = generateSequenceProducer(messageId, groupName, virtualHost, exchange, routingKey,
            msgBody);
        // 降级兜底机制
        // 1、优先使用事务同步器，将mq消息跟随业务数据一起落库
        // 2、如果不支持事务同步器，则直接单独保存mq消息
        // 3、如果不支持保存mq消息到数据库，则退化为普通的mq发送
        if (canUseTransactionSynchronization()) {
            registerTransactionSynchronization();
            stashProducer(producer);
        } else if (Objects.nonNull(producerManager)) {
            try {
                producerManager.saveMqProducer(producer);
            } catch (Exception e) {
                log.error("fai to save sequence message, [{}]", producer.toString(), e);
            }
            rabbitService.send(producer);
        } else {
            rabbitService.send(producer);
        }
    }
}
