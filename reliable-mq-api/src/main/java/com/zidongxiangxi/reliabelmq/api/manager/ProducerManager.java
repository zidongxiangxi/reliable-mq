package com.zidongxiangxi.reliabelmq.api.manager;

import com.zidongxiangxi.reliabelmq.api.entity.enums.MessageSendStatusEnum;

import java.util.List;

/**
 * mq生产者manager接口
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public interface ProducerManager<T> {
    /**
     * 保存消息
     *
     * @param producer 消息体
     * @return 是否成功
     */
    boolean saveMqProducer(T producer);

    /**
     * 消息发送失败
     *
     * @param application 应用名称
     * @param messageId 消息id
     * @return 是否成功
     */
    boolean failSendMq(String application, String messageId);

    /**
     * 删除消息
     *
     * @param application 应用名称
     * @param messageId 消息id
     * @return 是否成功
     */
    boolean deleteMq(String application, String messageId);

    /**
     * 查询发送中的消息
     *
     * @param application 应用名称
     * @param start 起始
     * @param limit 数量
     * @return 消息列表
     */
    List<T> listSendingMq(String application, int start, int limit);

    /**
     * 根据状态查询全部应用的消息
     *
     * @param sendStatus 发送状态
     * @param start 起始
     * @param limit 数量
     * @return 消息列表
     */
    List<T> listAllApplicationMq(MessageSendStatusEnum sendStatus, int start, int limit);
}
