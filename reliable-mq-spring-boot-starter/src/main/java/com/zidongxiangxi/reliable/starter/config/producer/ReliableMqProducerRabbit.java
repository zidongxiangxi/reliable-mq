package com.zidongxiangxi.reliable.starter.config.producer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * rabbit发送配置类
 *
 * @author chenxudong
 * @date 2019/12/29
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "reliable-mq.producer.rabbit")
public class ReliableMqProducerRabbit {
    /**
     * 是否启用rabbit可靠发送
     */
    private boolean enabled = false;

    /**
     * 发送记录表
     */
    private String recordTableName = "rabbit_produce_record";

    /**
     * 发送失败是否定时任务重试
     */
    private boolean enabledRetry = true;

    /**
     * 每次定时任务重试的消息数量
     */
    private int retryBatchSize = 20;
}
