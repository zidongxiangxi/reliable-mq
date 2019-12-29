package com.zidongxiangxi.reliablemq.producer.transaction.sql;

import com.zidongxiangxi.reliabelmq.api.transaction.ProducerSqlProvider;
import org.springframework.util.StringUtils;

/**
 * 默认的事务mq的sql提供者
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public class DefaultRabbitProducerSqlProvider implements ProducerSqlProvider {
    private static final String DEFAULT_TABLE_NAME = "rabbit_producer";
    private static final String INSERT_SQL = "insert into %s (application, group_name, virtual_host, type, exchange, routing_key, "
        + "message_id, body, send_status, max_retry_times, next_retry_time) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_SQL = "select  * from %s where application=? and message_id=?";
    private static final String SENDING_SQL = "update %s set send_status=1, retry_times=?, next_retry_time=? where "
        + "application=? and message_id=?";
    private static final String FAIL_SQL = "update %s set send_status=2, retry_times=max_retry_times where application=? "
        + "and message_id=?";
    private static final String DELETE_SQL = "delete from %s where application=? and message_id=?";
    private static final String LIST_SENDING_SQL = "select * from %s where send_status=1 and next_retry_time < now() "
        + "and application=? order by next_retry_time limit ?, ?";
    private static final String LIST_SQL = "select * from %s where send_status=? order by id asc limit ?,"
        + " ?";

    private String insertSql, selectSql, sendingSql, failSql, deleteSql, listSendingSql, listSql;

    public DefaultRabbitProducerSqlProvider() {
        initSql(DEFAULT_TABLE_NAME);
    }

    public DefaultRabbitProducerSqlProvider(String tableName) {
        tableName = StringUtils.isEmpty(tableName) ? DEFAULT_TABLE_NAME : tableName;
        initSql(tableName);
    }

    private void initSql(String tableName) {
        insertSql = String.format(INSERT_SQL, tableName);
        selectSql = String.format(SELECT_SQL, tableName);
        sendingSql = String.format(SENDING_SQL, tableName);
        failSql = String.format(FAIL_SQL, tableName);
        deleteSql = String.format(DELETE_SQL, tableName);
        listSendingSql = String.format(LIST_SENDING_SQL, tableName);
        listSql = String.format(LIST_SQL, tableName);
    }


    @Override
    public String getInsertMqSql() {
        return insertSql;
    }

    @Override
    public String getSelectMqSql() {
        return selectSql;
    }

    @Override
    public String getSendingMqSql() {
        return sendingSql;
    }

    @Override
    public String getFailMqSql() {
        return failSql;
    }

    @Override
    public String getDeleteMqSql() {
        return deleteSql;
    }

    @Override
    public String getListSendingMqSql() {
        return listSendingSql;
    }

    @Override
    public String getListMqSql() {
        return listSql;
    }
}
