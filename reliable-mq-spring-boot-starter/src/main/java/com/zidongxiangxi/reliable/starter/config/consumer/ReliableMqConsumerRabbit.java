package com.zidongxiangxi.reliable.starter.config.consumer;


/**
 * rabbit消费配置类
 *
 * @author chenxudong
 * @date 2019/12/23
 */

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "reliable-mq.consumer.rabbit")
public class ReliableMqConsumerRabbit {
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
}
