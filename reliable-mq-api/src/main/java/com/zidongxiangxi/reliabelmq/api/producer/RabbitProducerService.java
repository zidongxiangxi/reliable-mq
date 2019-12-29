package com.zidongxiangxi.reliabelmq.api.producer;

import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;

import java.util.List;

/**
 * rabbitMq消息发送接口
 *
 * @author chenxudong
 * @date 2019/12/24
 */
public interface RabbitProducerService {
    /**
     * 批量发送mq消息
     *
     * @param producerList mq消息列表
     */
    void send(List<RabbitProducer> producerList);

    /**
     * 发送mq消息
     *
     * @param producer mq消息
     */
    void send(RabbitProducer producer);

    /**
     * 获取虚拟主机
     *
     * @return 虚拟主机
     */
    String getVirtualHost();
}
