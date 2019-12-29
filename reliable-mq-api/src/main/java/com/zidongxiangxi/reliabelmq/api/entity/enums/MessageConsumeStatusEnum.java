package com.zidongxiangxi.reliabelmq.api.entity.enums;

import lombok.Getter;

/**
 * 消息消费状态
 *
 * @author chenxudong
 * @date 2019/09/12
 */
@Getter
public enum MessageConsumeStatusEnum {
    /**
     * 消费中
     */
    CONSUMING(0),

    /**
     * 已消费
     */
    CONSUMED(1),

    /**
     * 消费失败
     */
    FAIL(2);

    private Integer value;

    MessageConsumeStatusEnum(Integer value) {
        this.value = value;
    }
}
