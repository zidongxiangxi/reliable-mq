package com.zidongxiangxi.reliabelmq.api.manager;

import com.zidongxiangxi.reliabelmq.api.entity.ConsumeFailRecord;

/**
 * 消费失败manager
 *
 * @author chenxudong
 * @date 2019/12/2
 */
public interface ConsumeFailRecordManager {
    /**
     * 保存消费失败记录
     *
     * @param record 消费失败记录
     * @return 保存是否成功
     */
    boolean saveRecord(ConsumeFailRecord record);

    /**
     * 查询消费失败记录
     *
     * @param application 应用名称
     * @param messageId 消息id
     * @return 失败记录
     */
    ConsumeFailRecord getRecord(String application, String messageId);
}
