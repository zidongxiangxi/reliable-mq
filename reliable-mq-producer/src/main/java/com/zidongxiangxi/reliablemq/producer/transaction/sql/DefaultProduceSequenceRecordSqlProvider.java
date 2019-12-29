package com.zidongxiangxi.reliablemq.producer.transaction.sql;

import com.zidongxiangxi.reliabelmq.api.transaction.ProduceSequenceRecordSqlProvider;
import org.springframework.util.StringUtils;

/**
 * 默认顺序消息语句提供者
 *
 * @author chenxudong
 * @date 2019/10/10
 */
public class DefaultProduceSequenceRecordSqlProvider implements ProduceSequenceRecordSqlProvider {
    private static final String DEFAULT_TABLE_NAME = "produce_sequence_record";
    private static final String INSERT_SQL = "insert into %s (message_id, application, group_name) values (?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "select * from %s where id=?";
    private static final String SELECT_BY_MESSAGE_ID_SQL = "select * from %s where application=? and message_id=?";
    private static final String SELECT_PREVIOUS_ID_SQL = "select max(id) from %s where application=? and group_name=? "
        + "and id < ?";
    private static final String LIST_SQL = "select * from %s where create_time < ? order by create_time "
        + "limit ?";
    private static final String DELETE_SQL = "delete from %s where id in (:ids)";

    private String insertSql, selectByIdSql, selectByMessageId, selectPreviousId, listSql, deleteSql;

    public DefaultProduceSequenceRecordSqlProvider() {
        this(DEFAULT_TABLE_NAME);
    }

    public DefaultProduceSequenceRecordSqlProvider(String tableName) {
        tableName = StringUtils.isEmpty(tableName) ? DEFAULT_TABLE_NAME : tableName;
        insertSql = String.format(INSERT_SQL, tableName);
        selectByIdSql = String.format(SELECT_BY_ID_SQL, tableName);
        selectByMessageId = String.format(SELECT_BY_MESSAGE_ID_SQL, tableName);
        selectPreviousId = String.format(SELECT_PREVIOUS_ID_SQL, tableName);
        listSql = String.format(LIST_SQL, tableName);
        deleteSql = String.format(DELETE_SQL, tableName);
    }


    @Override
    public String getInsertSql() {
        return insertSql;
    }

    @Override
    public String getSelectByIdSql() {
        return selectByIdSql;
    }

    @Override
    public String getSelectByMessageIdSql() {
        return selectByMessageId;
    }

    @Override
    public String getSelectPreviousIdSql() {
        return selectPreviousId;
    }

    @Override
    public String getListSql() {
        return listSql;
    }

    @Override
    public String getDeleteSql() {
        return deleteSql;
    }
}
