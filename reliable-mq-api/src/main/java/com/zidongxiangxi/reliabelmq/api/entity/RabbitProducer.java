package com.zidongxiangxi.reliabelmq.api.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * rabbitMq的消息
 *
 * @author chenxudong
 * @date 2019/08/30
 */
@Data
public class RabbitProducer implements Serializable {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 消息类型 0=即时消息；1=顺序消息
     */
    private Integer type;

    /**
     * 应用名称
     */
    private String application;

    /**
     * 虚拟主机
     */
    private String virtualHost;

    /**
     * 交换器
     */
    private String exchange;

    /**
     * 路由key
     */
    private String routingKey;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 消息内容
     */
    private String body;

    /**
     * 消息分组，同分组内，消息序号按发送顺序递增
     */
    private String groupName;

    /**
     * 发送状态， 0=预提交，1=发送中，2=发送是啊比
     */
    private Integer sendStatus;

    /**
     * 重试次数
     */
    private Integer retryTimes;

    /**
     * 最大重试次数
     */
    private Integer maxRetryTimes;

    /**
     * 尝试重发的时间
     */
    private Date nextRetryTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
