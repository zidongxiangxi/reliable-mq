package com.zidongxiangxi.reliabelmq.api.transaction;

/**
 * 幂等消费mq的sql提供者
 *
 * @author chenxudong
 * @date 2019/09/01
 */
public interface ConsumeRecordSqlProvider {
    /**
     * 获取查询列表sql
     *
     * @return sql语句
     */
    String getListPrimaryKeySql();

    /**
     * 获取查询sql
     *
     * @return sql语句
     */
    String getSelectSql();

    /**
     * 获取插入sql
     *
     * @return sql语句
     */
    String getInsertSql();

    /**
     * 获取删除sql
     *
     * @return sql语句
     */
    String getDeleteSql();
}
