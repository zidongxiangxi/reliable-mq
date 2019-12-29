package com.zidongxiangxi.reliablemq.starter.config.producer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 发送配置类
 *
 * @author chenxudong
 * @date 2019/12/23
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "reliable-mq.producer")
public class ReliableMqProducer {
    /**
     * 是否需要开启发送端相关功能
     */
    private boolean enabled = false;

    /**
     * rabbit发送配置
     */
    private final ReliableMqProducerRabbit rabbit = new ReliableMqProducerRabbit();

    /**
     * 顺序配置
     */
    private final ReliableMqProducerSequence sequence = new ReliableMqProducerSequence();
}
