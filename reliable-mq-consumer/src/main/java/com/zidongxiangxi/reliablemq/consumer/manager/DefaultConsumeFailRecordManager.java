package com.zidongxiangxi.reliablemq.consumer.manager;

import com.alibaba.fastjson.JSON;
import com.zidongxiangxi.reliabelmq.api.entity.ConsumeFailRecord;
import com.zidongxiangxi.reliabelmq.api.manager.ConsumeFailRecordManager;
import com.zidongxiangxi.reliabelmq.api.transaction.ConsumeFailRecordSqlProvider;
import com.zidongxiangxi.reliablemq.consumer.mapper.ConsumeFailRecordMapper;
import com.zidongxiangxi.reliablemq.consumer.transaction.DefaultConsumeFailRecordSqlProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * 消费失败记录manager
 *
 * @author chenxudong
 * @date 2019/12/24
 */
@Slf4j
public class DefaultConsumeFailRecordManager implements ConsumeFailRecordManager {
    private JdbcTemplate jdbcTemplate;
    private ConsumeFailRecordSqlProvider sqlProvider;
    private ConsumeFailRecordMapper mapper;

    public DefaultConsumeFailRecordManager(JdbcTemplate jdbcTemplate, DefaultConsumeFailRecordSqlProvider sqlProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlProvider = sqlProvider;
        this.mapper = new ConsumeFailRecordMapper();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRecord(ConsumeFailRecord record) {
        ConsumeFailRecord existRecord = getRecord(record.getApplication(), record.getMessageId());
        if (Objects.nonNull(existRecord)) {
            log.warn("[DefaultConsumeFailRecordManager] fail to save consume fail record, had same application and "
                    + "messageId, record:{}", JSON.toJSONString(record));
            return false;
        }

        Object[] columns = {record.getQueue(), record.getApplication(), record.getMessageId(), record.getHeaders(),
            record.getBody(), record.getErrorStack()};
        try {
            int rows = jdbcTemplate.update(sqlProvider.getInsertSql(), columns);
            return rows > 0;
        } catch (Exception e) {
            log.error("[DefaultConsumeFailRecordManager] fail to save consume fail record:" + JSON.toJSONString(record), e);
            return false;
        }
    }

    @Override
    public ConsumeFailRecord getRecord(String application, String messageId) {
        List<ConsumeFailRecord> list = jdbcTemplate.query(sqlProvider.getSelectSql(), mapper, application, messageId);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }
}
