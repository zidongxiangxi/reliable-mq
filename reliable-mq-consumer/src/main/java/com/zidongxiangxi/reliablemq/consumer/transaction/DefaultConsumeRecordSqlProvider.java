package com.zidongxiangxi.reliablemq.consumer.transaction;

import com.zidongxiangxi.reliabelmq.api.transaction.ConsumeRecordSqlProvider;
import org.springframework.util.StringUtils;

/**
 * 默认的rabbitMq幂等消费mq的sql提供者
 *
 * @author chenxudong
 * @date 2019/09/01
 */
public class DefaultConsumeRecordSqlProvider implements ConsumeRecordSqlProvider {
    private static final String DEFAULT_TABLE_NAME = "consume_record";
    private static final String LIST_PRIMARY_KEY_SQL = "select id from %s where create_time < ? order by "
        + "create_time limit ?";
    private static final String SELECT_SQL = "select 1 from %s where application = ? and message_id = ?";
    private static final String INSERT_SQL = "insert into %s (application, message_id) values (?, ?)";
    private static final String DELETE_SQL = "delete from %s where id in (:ids)";

    private String listPrimaryKeySql, selectSql, insertSql, deleteSql;

    public DefaultConsumeRecordSqlProvider(String tableName) {
        tableName = StringUtils.isEmpty(tableName) ? DEFAULT_TABLE_NAME : tableName;
        listPrimaryKeySql = String.format(LIST_PRIMARY_KEY_SQL, tableName);
        selectSql = String.format(SELECT_SQL, tableName);
        insertSql = String.format(INSERT_SQL, tableName);
        deleteSql = String.format(DELETE_SQL, tableName);
    }

    @Override
    public String getListPrimaryKeySql() {
        return listPrimaryKeySql;
    }

    @Override
    public String getSelectSql() {
        return selectSql;
    }

    @Override
    public String getInsertSql() {
        return insertSql;
    }

    @Override
    public String getDeleteSql() {
        return deleteSql;
    }
}
