package com.zidongxiangxi.reliablemq.producer.transaction.listener;

import com.alibaba.fastjson.JSON;
import com.zidongxiangxi.reliabelmq.api.manager.ProduceRecordManager;
import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.producer.RabbitProducerService;
import com.zidongxiangxi.reliablemq.producer.transaction.RabbitProducerTransactionMessageHolder;
import com.zidongxiangxi.reliablemq.producer.transaction.RabbitTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * 本地事务类型的rabbitMq发送的事务监听
 *
 * @author chenxudong
 * @date 2019/08/30
 */
@Slf4j
public class DatabaseRabbitProducerTransactionListener extends AbstractRabbitProducerTransactionListener {
    private ProduceRecordManager<RabbitProducer> producerManager;
    private RabbitProducerService rabbitProducerService;

    public DatabaseRabbitProducerTransactionListener(
        ProduceRecordManager<RabbitProducer> producerManager,
        RabbitProducerService rabbitProducerService
    ) {
        this.producerManager = producerManager;
        this.rabbitProducerService = rabbitProducerService;
    }

    @Override
    public void beforeCommit() {
        RabbitProducerTransactionMessageHolder messageHolder = RabbitTransactionContext.getMessageHolder();
        if (Objects.isNull(messageHolder) || CollectionUtils.isEmpty(messageHolder.getQueue())) {
            return;
        }
        List<RabbitProducer> list = messageHolder.getQueue();
        for (RabbitProducer producer : list) {
            try {
                producerManager.saveRecord(producer);
            } catch (Throwable e) {
                log.error("fail to save rabbit producer: " + JSON.toJSONString(producer), e);
            }
        }
    }

    @Override
    public void beforeCompletion() {
        //TODO NOTHING
    }

    @Override
    public void afterCommit() {
        RabbitProducerTransactionMessageHolder messageHolder = remove();
        if (Objects.isNull(messageHolder) || CollectionUtils.isEmpty(messageHolder.getQueue())) {
            return;
        }
        for (RabbitProducer producer : messageHolder.getQueue()) {
            try {
                rabbitProducerService.send(producer);
            } catch (Throwable t) {
                log.error("fail to send message, [{}[", producer.toString(), t);
            }
        }
    }

    @Override
    public void afterCompletion() {
        RabbitProducerTransactionMessageHolder messageHolder = remove();
        if (Objects.isNull(messageHolder) || CollectionUtils.isEmpty(messageHolder.getQueue())) {
            return;
        }
        for (RabbitProducer producer : messageHolder.getQueue()) {
            log.info("fail to commit transaction, message({}) is ignored. exchange:{}. routingKey:{}", producer.getMessageId(),
                producer.getExchange(), producer.getRoutingKey());
        }
    }
}
