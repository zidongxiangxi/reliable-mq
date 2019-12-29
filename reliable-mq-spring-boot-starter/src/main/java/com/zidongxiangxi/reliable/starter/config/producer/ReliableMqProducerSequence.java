package com.zidongxiangxi.reliable.starter.config.producer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 顺序mq配置
 *
 * @author chenxudong
 * @date 2019/12/24
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "reliable-mq.producer.sequence")
public class ReliableMqProducerSequence {
    /**
     * 是否可以清理顺序消息记录
     */
    private boolean enabledClear = false;

    /**
     * 保留时长，单位：天
     */
    private int retentionPeriod = 10;

    /**
     * 每次执行定时任务，清理的数量
     */
    private int batchSize = 20;
}
