package com.zidongxiangxi.reliabelmq.api.entity.enums;

import lombok.Getter;

/**
 * 消息发送状态
 *
 * @author chenxudong
 * @date 2019/09/12
 */
@Getter
public enum MessageSendStatusEnum {
    /**
     * 预提交
     * 采用数据库的方式，不会有预提交的数据
     */
    @Deprecated
    PRE_COMMITTED(0),
    /**
     * 发送中
     */
    SENDING(1),
    /**
     * 发送失败
     */
    FAIL(2);

    private Integer value;

    MessageSendStatusEnum(Integer value) {
        this.value = value;
    }
}
