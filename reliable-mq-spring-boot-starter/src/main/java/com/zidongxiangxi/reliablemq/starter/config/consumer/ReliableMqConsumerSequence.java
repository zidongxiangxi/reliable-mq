package com.zidongxiangxi.reliablemq.starter.config.consumer;

import lombok.Getter;
import lombok.Setter;

/**
 * 顺序消费配置
 *
 * @author chenxudong
 * @date 2019/12/24
 */
@Getter
@Setter
public class ReliableMqConsumerSequence {
    /**
     * 是否开启顺序消费
     */
    private boolean enabled;

    /**
     * 消费失败后的延迟应答时间，单位：毫秒
     * 设置小于0的值，表示不延迟
     */
    private int consumeFailDelay = 1000;

    /**
     * 容错时间，顺序消息在faultTolerantTime后还没被消费，允许强制消费，单位：毫秒
     * 默认-1，不容错
     */
    private int faultTolerantTime = -1;
}
