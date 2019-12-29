package com.zidongxiangxi.reliable.starter.config.consumer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消费者清理消费记录的配置
 *
 * @author chenxudong
 * @date 2019/12/24
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "reliable-mq.consumer.clear")
public class ReliableMqConsumerClear {
    /**
     * 是否开启清理消费记录
     */
    private boolean enabled = false;

    /**
     * 保留时长，单位：天
     */
    private int retentionPeriod = 30;

    /**
     * 每次定时任务清理的消费记录数量
     */
    private int batchSize = 20;
}
