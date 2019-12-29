package com.zidongxiangxi.reliabelmq.api.manager;

import java.util.Date;
import java.util.List;

/**
 * mq消息消费manager
 *
 * @author chenxudong
 * @date 2019/09/03
 */
public interface ConsumeRecordManager {
    /**
     * 查询消费记录的主键
     *
     * @param beforeTime 在这个时间之前的记录
     * @param size 数量
     * @return 主键列表
     */
    List<Long> listPrimaryKey(Date beforeTime, int size);

    /**
     * 判断消息我是否已经被消费
     *
     * @param application 应用名
     * @param messageId 消息id
     * @return 是否已消费
     */
    boolean isConsumed(String application, String messageId);

    /**
     * 插入消费记录
     *
     * @param application 应用名
     * @param messageId 消息id
     * @return 是否成功
     */
    boolean insertConsumeRecord(String application, String messageId);

    /**
     * 删除消费记录
     *
     * @param ids 主键id列表
     * @return 是否成功
     */
    boolean deleteConsumeRecord(List<Long> ids);
}
