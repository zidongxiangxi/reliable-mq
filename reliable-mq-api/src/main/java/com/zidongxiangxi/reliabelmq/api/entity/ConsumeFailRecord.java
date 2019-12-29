package com.zidongxiangxi.reliabelmq.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 消费失败记录
 *
 * @author chenxudong
 * @date 2019/11/30
 */
 @Data
public class ConsumeFailRecord implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 消息来自哪个队列
     */
    private String queue;

    /**
     * 发送消息的服务名称
     */
    private String application;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * mq消息的headers
     */
    private String headers;

    /**
     * 消息体
     */
    private String body;

    /**
     * 错误堆栈信息
     */
    private String errorStack;

    /**
     * 创建时间
     */
    private Date createTime;
}
