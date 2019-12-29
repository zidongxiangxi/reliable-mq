package com.zidongxiangxi.reliablemq.producer.transaction.sql;

import com.zidongxiangxi.reliabelmq.api.transaction.ProduceRecordSqlProvider;
import org.springframework.util.StringUtils;

/**
 * 默认的事务mq的sql提供者
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public class RabbitProduceRecordSqlProvider implements ProduceRecordSqlProvider {
    private static final String DEFAULT_TABLE_NAME = "rabbit_produce_record";
    private static final String INSERT_SQL = "insert into %s (application, group_name, virtual_host, type, exchange, routing_key, "
        + "message_id, body, send_status, max_retry_times, next_retry_time) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_SQL = "select  * from %s where application=? and message_id=?";
    private static final String UPDATE_RETRY_SQL = "update %s set send_status=1, retry_times=?, next_retry_time=? where "
        + "application=? and message_id=?";
    private static final String UPDATE_STATUS_SQL = "update %s set send_status=2, retry_times=max_retry_times where application=? "
        + "and message_id=?";
    private static final String DELETE_SQL = "delete from %s where application=? and message_id=?";
    private static final String LIST_SENDING_SQL = "select * from %s where send_status=1 and next_retry_time < now() "
        + "and application=? order by next_retry_time limit ?, ?";

    private String insertSql, selectSql, updateRetrySql, updateStatusSql, deleteSql, listSendingSql;

    public RabbitProduceRecordSqlProvider() {
        initSql(DEFAULT_TABLE_NAME);
    }

    public RabbitProduceRecordSqlProvider(String tableName) {
        tableName = StringUtils.isEmpty(tableName) ? DEFAULT_TABLE_NAME : tableName;
        initSql(tableName);
    }

    private void initSql(String tableName) {
        insertSql = String.format(INSERT_SQL, tableName);
        selectSql = String.format(SELECT_SQL, tableName);
        updateRetrySql = String.format(UPDATE_RETRY_SQL, tableName);
        updateStatusSql = String.format(UPDATE_STATUS_SQL, tableName);
        deleteSql = String.format(DELETE_SQL, tableName);
        listSendingSql = String.format(LIST_SENDING_SQL, tableName);
    }


    @Override
    public String getInsertSql() {
        return insertSql;
    }

    @Override
    public String getSelectSql() {
        return selectSql;
    }

    @Override
    public String getUpdateRetrySql() {
        return updateRetrySql;
    }

    @Override
    public String getUpdateStatusSql() {
        return updateStatusSql;
    }

    @Override
    public String getDeleteSql() {
        return deleteSql;
    }

    @Override
    public String getListSendingSql() {
        return listSendingSql;
    }
}
