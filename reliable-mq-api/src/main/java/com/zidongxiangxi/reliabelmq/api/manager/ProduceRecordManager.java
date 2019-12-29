package com.zidongxiangxi.reliabelmq.api.manager;

import java.util.List;

/**
 * mq生产者manager接口
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public interface ProduceRecordManager<T> {
    /**
     * 保存发送记录
     *
     * @param producer 消息体
     * @return 是否成功
     */
    boolean saveRecord(T producer);

    /**
     * 消息发送失败，修改记录的状态
     *
     * @param application 应用名称
     * @param messageId 消息id
     * @return 是否成功
     */
    boolean failToSend(String application, String messageId);

    /**
     * 删除消息
     *
     * @param application 应用名称
     * @param messageId 消息id
     * @return 是否成功
     */
    boolean deleteRecord(String application, String messageId);

    /**
     * 查询发送中的消息
     *
     * @param application 应用名称
     * @param start 起始
     * @param limit 数量
     * @return 消息列表
     */
    List<T> listSendingRecord(String application, int start, int limit);
}
