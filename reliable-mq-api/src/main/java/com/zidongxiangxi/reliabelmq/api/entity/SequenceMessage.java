package com.zidongxiangxi.reliabelmq.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 顺序mq的发送记录实体类
 *
 * @author chenxudong
 * @date 2019/12/23
 */
@Data
public class SequenceMessage implements Serializable {
    /**
     * 消息id
     */
    private Long id;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 应用名称
     */
    private String application;

    /**
     * 消息分组，同分组内，消息序号按发送顺序递增
     */
    private String groupName;

    /**
     * 创建时间
     */
    private Date createTime;

}
