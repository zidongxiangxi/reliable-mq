package com.zidongxiangxi.reliabelmq.api.transaction;

/**
 * 消费失败记录的sql语句提供接口
 *
 * @author chenxudong
 * @date 2019/12/24
 */
public interface ConsumeFailRecordSqlProvider {
    /**
     * 获取插入语句
     *
     * @return sql语句
     */
    String getInsertSql();

    /**
     * 获取查询单个记录的语句
     *
     * @return sql语句
     */
    String getSelectSql();
}
