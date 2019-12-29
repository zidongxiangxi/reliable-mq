package com.zidongxiangxi.reliabelmq.api.transaction;

/**
 * 事务mq的sql提供者
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public interface ProduceRecordSqlProvider {
    /**
     * 获取插入语句
     *
     * @return sql语句
     */
    String getInsertSql();

    /**
     * 获取单个查询语句
     *
     * @return sql语句
     */
    String getSelectSql();

    /**
     * 获取修改重试信息语句
     *
     * @return sql语句
     */
    String getUpdateRetrySql();

    /**
     * 获取修改状态语句
     *
     * @return sql语句
     */
    String getUpdateStatusSql();

    /**
     * 获取删除语句
     *
     * @return sql语句
     */
    String getDeleteSql();

    /**
     * 获取查询列表语句
     *
     * @return sql语句
     */
    String getListSendingSql();
}
