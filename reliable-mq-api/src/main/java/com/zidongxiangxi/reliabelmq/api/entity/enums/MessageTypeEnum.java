package com.zidongxiangxi.reliabelmq.api.entity.enums;

import lombok.Getter;

/**
 *  消息类型枚举
 *
 * @author chenxudong
 * @date 2019/09/12
 */
@Getter
public enum MessageTypeEnum {

    /**
     * 即时消息
     */
    IMMEDIATE(0),
    /**
     * 顺序消息
     */
    SEQUENCE(1);

    private Integer value;

    MessageTypeEnum(Integer value) {
        this.value = value;
    }
}
