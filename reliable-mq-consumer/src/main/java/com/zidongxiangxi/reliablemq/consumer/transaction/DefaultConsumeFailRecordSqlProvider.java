package com.zidongxiangxi.reliablemq.consumer.transaction;

import com.zidongxiangxi.reliabelmq.api.transaction.ConsumeFailRecordSqlProvider;
import org.springframework.util.StringUtils;

/**
 * rabbitMq消费失败记录的sql语句提供接口
 *
 * @author chenxudong
 * @date 2019/12/24
 */
public class DefaultConsumeFailRecordSqlProvider implements ConsumeFailRecordSqlProvider {
    private static final String DEFAULT_TABLE_NAME = "consume_fail_record";
    private static final String INSERT_SQL = "insert into %s (queue, application, message_id, headers, body, "
        + "error_stack) values (?, ?, ?, ?, ?,?)";
    private static final String SELECT_SQL = "select * from %s where application=? and message_id=? limit 1";

    private String insertSql, selectSql;
    public DefaultConsumeFailRecordSqlProvider(String tableName) {
        tableName = StringUtils.isEmpty(tableName) ? DEFAULT_TABLE_NAME : tableName;
        insertSql = String.format(INSERT_SQL, tableName);
        selectSql = String.format(SELECT_SQL, tableName);
    }

    @Override
    public String getInsertSql() {
        return insertSql;
    }

    @Override
    public String getSelectSql() {
        return selectSql;
    }
}
