package com.zidongxiangxi.reliable.starter.config;

import com.zidongxiangxi.reliable.starter.config.consumer.ReliableMqConsumer;
import com.zidongxiangxi.reliable.starter.config.producer.ReliableMqProducer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * reliable-mq配置类
 *
 * @author chenxudong
 * @date 2019/12/23
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "reliable-mq")
public class ReliableMqProperties {
    /**
     * 应用名
     */
    private String application = "";

    /**
     * 发送配置
     */
    private final ReliableMqProducer producer = new ReliableMqProducer();

    /**
     * 消费配置
     */
    private final ReliableMqConsumer consumer = new ReliableMqConsumer();
}
