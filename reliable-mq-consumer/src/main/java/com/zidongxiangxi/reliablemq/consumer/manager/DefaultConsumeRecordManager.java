package com.zidongxiangxi.reliablemq.consumer.manager;

import com.zidongxiangxi.reliabelmq.api.manager.ConsumeRecordManager;
import com.zidongxiangxi.reliabelmq.api.transaction.ConsumeRecordSqlProvider;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mq消息消费记录manager
 *
 * @author chenxudong
 * @date 2019/09/03
 */
public class DefaultConsumeRecordManager implements ConsumeRecordManager {
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private ConsumeRecordSqlProvider sqlProvider;

    public DefaultConsumeRecordManager(JdbcTemplate jdbcTemplate, ConsumeRecordSqlProvider sqlProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.sqlProvider = sqlProvider;
    }

    @Override
    public List<Long> listPrimaryKey(Date beforeTime, int size) {
        return jdbcTemplate.queryForList(sqlProvider.getListPrimaryKeySql(), Long.class, beforeTime, size);
    }

    @Override
    public boolean isConsumed(String application, String messageId) {
        List<Integer> result = jdbcTemplate.queryForList(sqlProvider.getSelectSql(), Integer.class, application,
            messageId);
        return !CollectionUtils.isEmpty(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertConsumeRecord(String application, String messageId) {
        if (isConsumed(application, messageId)) {
            return false;
        }
        try {
            return jdbcTemplate.update(sqlProvider.getInsertSql(), application, messageId) > 0;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteConsumeRecord(List<Long> ids) {
        Map<String, Object> args = new HashMap<>(1);
        args.put("ids", ids);
        return namedParameterJdbcTemplate.update(sqlProvider.getDeleteSql(), args) > 0;
    }
}
