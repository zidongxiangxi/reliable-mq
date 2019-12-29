package com.zidongxiangxi.reliable.starter.config.consumer;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

/**
 * 可靠消费配置
 *
 * @author chenxudong
 * @date 2019/12/24
 */
@Getter
@Setter
public class ReliableMqConsumerRely {
    /**
     * 是否开启可靠消费
     */
    private boolean enabled = false;

    /**
     * 最大重试次数
     */
    private int maxAttempts = 1;

    /**
     * 初始的失败间隔
     */
    private Duration initialInterval = Duration.ofMillis(1000);

    /**
     * 间隔时间的增长因子
     */
    private double multiplier = 1.0;

    /**
     * 最大的时间间隔
     */
    private Duration maxInterval = Duration.ofMillis(10000);
}
