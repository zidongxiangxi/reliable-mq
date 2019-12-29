package com.zidongxiangxi.reliabelmq.api.manager;

import com.zidongxiangxi.reliabelmq.api.entity.SequenceMessage;

import java.util.Date;
import java.util.List;

/**
 * 顺序消息manager接口
 *
 * @author chenxudong
 * @date 2019/12/24
 */
public interface ProduceSequenceRecordManager {
    /**
     * 获取顺序消息的上一个消息id
     *
     * @param application 应用名称
     * @param messageId 消息id
     * @return 上一个顺序消息的id
     */
    String getPreviousMessageId(String application, String messageId);

    /**
     * 查询顺序消息记录列表
     *
     * @param beforeTime 之前的时间
     * @param size 数量
     * @return 顺序消息记录列表
     */
    List<SequenceMessage> listRecord(Date beforeTime, int size);

    /**
     * 根据主键删除顺序消息记录
     *
     * @param ids 主键列表
     * @return 是否删除成功
     */
    boolean deleteRecordByIds(List<Long> ids);
}
