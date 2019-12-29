package com.zidongxiangxi.reliabelmq.api.transaction;

/**
 * 顺序消息语句提供接口
 *
 * @author chenxudong
 * @date 2019/10/10
 */
public interface SequenceSqlProvider {
    /**
     * 获取插入顺序消息记录的语句
     *
     * @return sql语句
     */
    String getInsertSql();

    /**
     * 获取根据记录id查询顺序消息记录的语句
     *
     * @return sql语句
     */
    String getSelectByIdSql();

    /**
     * 获取根据消息id查询顺序消息记录的语句
     *
     * @return sql语句
     */
    String getSelectByMessageIdSql();

    /**
     * 获取查询上一个顺序消息记录id的语句
     *
     * @return sql语句
     */
    String getSelectPreviousIdSql();

    /**
     * 获取查询顺序消息记录列表的语句
     *
     * @return sql语句
     */
    String getListSql();

    /**
     * 获取删除顺序消息记录的语句
     *
     * @return sql语句
     */
    String getDeleteSql();
}
