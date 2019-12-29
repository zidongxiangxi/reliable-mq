package com.zidongxiangxi.reliable.starter.config.producer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 发送配置类
 *
 * @author chenxudong
 * @date 2019/12/23
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "reliable-mq.producer")
public class ReliableMqProducer {
    /**
     * 是否需要开启发送端相关功能
     */
    private boolean enabled = false;

    /**
     * rabbitMq发送表
     */
    private String producerTableName = "rabbit_produce_record";

    /**
     * 顺序消息表
     */
    private String sequenceTableName = "produce_sequence_record";

    /**
     * 重试配置
     */
    private final ReliableMqProducerRely rely = new ReliableMqProducerRely();

    /**
     * 顺序配置
     */
    private final ReliableMqProducerSequence sequence = new ReliableMqProducerSequence();
}
