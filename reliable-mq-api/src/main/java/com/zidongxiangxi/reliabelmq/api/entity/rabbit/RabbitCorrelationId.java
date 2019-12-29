package com.zidongxiangxi.reliabelmq.api.entity.rabbit;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;

/**
 * rabbit消息的CorrelationId
 *
 * @author chenxudong
 * @date 2019/09/19
 */
@Data
public class RabbitCorrelationId implements Serializable {
    /**
     * 应用名
     */
    private String application;

    /**
     * 消息id
     */
    private String messageId;

    public static RabbitCorrelationId parseRabbitCorrelationId(String content) {
        RabbitCorrelationId id = null;
        try {
            id = JSON.parseObject(content, RabbitCorrelationId.class);
        } catch (Throwable ignore) {}
        return id;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
