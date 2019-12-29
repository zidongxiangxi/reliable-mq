package com.zidongxiangxi.reliable.starter.config.producer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 发送重试配置
 *
 * @author chenxudong
 * @date 2019/12/24
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "reliable-mq.producer.rely")
public class ReliableMqProducerRely {
    /**
     * 是否重试发送
     */
    private boolean enabled = true;

    /**
     * 每次执行定时任务处理的mq数量
     */
    private int batchSize = 20;
}
