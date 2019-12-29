package com.zidongxiangxi.reliablemq.demo.service;

import com.zidongxiangxi.reliabelmq.api.producer.RabbitMqProduceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * rabbit消息发送
 *
 * @author chenxudong
 * @date 2019/12/29
 */
@Service
public class RabbitService {
    private String exchange = "test.exchange.fanout";

    @Autowired
    private RabbitMqProduceClient client;

    @Transactional(rollbackFor = Exception.class)
    public void commitNormal() {
        client.sendToExchange(exchange, "666");
    }

    @Transactional(rollbackFor = Exception.class)
    public void rollbackNormal() {
        client.sendToExchange(exchange, "666");
        throw new RuntimeException("rollback");
    }

    @Transactional(rollbackFor = Exception.class)
    public void commitSequence() {
        client.sendToExchangeSequentially(UUID.randomUUID().toString(), exchange, "666");
    }

    @Transactional(rollbackFor = Exception.class)
    public void rollbackSequence() {
        client.sendToExchangeSequentially(UUID.randomUUID().toString(), exchange, "666");
        throw new RuntimeException("rollback");
    }
}
