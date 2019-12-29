package com.zidongxiangxi.reliable.starter.config.consumer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 可靠消费配置
 *
 * @author chenxudong
 * @date 2019/12/24
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "reliable-mq.consumer.rely")
public class ReliableMqConsumerRely {
    /**
     * 是否开启可靠消费
     */
    private boolean enabled = false;

    /**
     * 可靠消费的表名
     */
    private String consumeFailRecordTableName = "consume_fail_record";
}
