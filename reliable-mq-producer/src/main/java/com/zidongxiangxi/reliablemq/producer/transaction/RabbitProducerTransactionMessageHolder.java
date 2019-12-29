package com.zidongxiangxi.reliablemq.producer.transaction;

import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;

import java.util.LinkedList;
import java.util.List;

/**
 * rabbitMq事务消息holder
 *
 * @author chenxudong
 * @date 2019/08/31
 */
public class RabbitProducerTransactionMessageHolder {
    /**
     * mq消息列表
     */
    private List<RabbitProducer> queue = new LinkedList<>();
    /**
     * mq是否提交
     */
    private boolean mqCommitted = false;

    public void add(RabbitProducer producer) {
        queue.add(producer);
    }

    public List<RabbitProducer> getQueue() {
        return queue;
    }

    public boolean isMqCommitted() {
        return mqCommitted;
    }

    public void setMqCommitted(boolean mqCommitted) {
        this.mqCommitted = mqCommitted;
    }
}
