package com.zidongxiangxi.reliabelmq.api.constant;

/**
 * rabbit消息的消息头
 *
 * @author chenxudong
 * @date 2019/09/14
 */
public interface MessageHeaderConstants {
    /**
     * 消息来自哪个应用的消息头
     */
    String MESSAGE_APPLICATION_HEADER = "message_application";

    /**
     * 消息类型的消息头
     */
    String MESSAGE_TYPE_HEADER = "message_type";

    /**
     * 顺序消息的上一个消息id的消息头
     */
    String MESSAGE_PREVIOUS_ID_HEADER = "message_previous_id";
}
