package com.zidongxiangxi.reliable.starter.config.consumer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消费配置类
 *
 * @author chenxudong
 * @date 2019/12/23
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "reliable-mq.consumer")
public class ReliableMqConsumer {
    /**
     * 是否需要开启消费端相关功能
     */
    private boolean enabled = false;

    /**
     * 是否可以清理消息消费记录
     */
    private boolean enabledClearRecord = false;

    /**
     * 消费记录表名
     */
    private String consumeRecordTableName = "consume_record";

    /**
     * 幂等消费配置
     */
    private final ReliableMqConsumerIdempotent idempotent = new ReliableMqConsumerIdempotent();

    /**
     * 顺序消费配置
     */
    private final ReliableMqConsumerSequence sequence = new ReliableMqConsumerSequence();

    /**
     * 可靠消费配置
     */
    private final ReliableMqConsumerRely rely = new ReliableMqConsumerRely();

    /**
     * 消费者清理消费记录的配置
     */
    private final ReliableMqConsumerClear clear = new ReliableMqConsumerClear();
}
