package com.zidongxiangxi.reliablemq.starter.config.consumer;

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
     * 消费记录表名
     */
    private String recordTableName = "consume_record";

    /**
     * 消费失败记录表名
     */
    private String failRecordTableName = "consume_fail_record";

    /**
     * 消费者清理消费记录的配置
     */
    private final ReliableMqConsumerClear clear = new ReliableMqConsumerClear();

    /**
     * rabbit的消费配置
     */
    private final ReliableMqConsumerRabbit rabbit = new ReliableMqConsumerRabbit();
}
