package com.zidongxiangxi.reliable.starter.config.consumer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 幂等消费配置
 *
 * @author chenxudong
 * @date 2019/12/24
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "reliable-mq.consumer.idempotent")
public class ReliableMqConsumerIdempotent {
    /**
     * 是否开启幂等消费
     */
    private boolean enabled = false;

}
