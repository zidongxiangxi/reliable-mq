package com.zidongxiangxi.reliablemq.starter.config.consumer;

import lombok.Getter;
import lombok.Setter;

/**
 * 幂等消费配置
 *
 * @author chenxudong
 * @date 2019/12/24
 */
@Getter
@Setter
public class ReliableMqConsumerIdempotent {
    /**
     * 是否开启幂等消费
     */
    private boolean enabled = false;

}
