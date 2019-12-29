package com.zidongxiangxi.reliablemq.producer.manager;

import com.zidongxiangxi.reliabelmq.api.entity.ProduceSequenceRecord;
import com.zidongxiangxi.reliabelmq.api.manager.ProduceSequenceRecordManager;
import com.zidongxiangxi.reliabelmq.api.transaction.ProduceSequenceRecordSqlProvider;
import com.zidongxiangxi.reliablemq.producer.mapper.ProduceSequenceRecordMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认的顺序消息manager
 *
 * @author chenxudong
 * @date 2019/12/24
 */
public class DefaultProduceSequenceRecordManager implements ProduceSequenceRecordManager {
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private ProduceSequenceRecordSqlProvider sequenceSqlProvider;

    public DefaultProduceSequenceRecordManager(JdbcTemplate jdbcTemplate, ProduceSequenceRecordSqlProvider sequenceSqlProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.sequenceSqlProvider = sequenceSqlProvider;
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public String getPreviousMessageId(String application, String messageId) {
        List<ProduceSequenceRecord> sequenceMessageList =
            jdbcTemplate.query(sequenceSqlProvider.getSelectByMessageIdSql(),
                new ProduceSequenceRecordMapper(), application, messageId);
        if (CollectionUtils.isEmpty(sequenceMessageList)) {
            return null;
        }
        ProduceSequenceRecord sequenceMessage = sequenceMessageList.get(0);
        List<Long> previousIdList = jdbcTemplate.queryForList(sequenceSqlProvider.getSelectPreviousIdSql(), Long.class,
            sequenceMessage.getApplication(), sequenceMessage.getGroupName(), sequenceMessage.getId());
        if (CollectionUtils.isEmpty(previousIdList)) {
            return null;
        }
        Long previousId = previousIdList.get(0);
        sequenceMessageList = jdbcTemplate.query(sequenceSqlProvider.getSelectByIdSql(),
            new ProduceSequenceRecordMapper(), previousId);
        return CollectionUtils.isEmpty(sequenceMessageList) ? null : sequenceMessageList.get(0).getMessageId();
    }

    @Override
    public List<ProduceSequenceRecord> listRecord(Date beforeTime, int size) {
        return jdbcTemplate.query(sequenceSqlProvider.getListSql(), new ProduceSequenceRecordMapper(), beforeTime, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRecordByIds(List<Long> ids) {
        Map<String, Object> args = new HashMap<>(1);
        args.put("ids", ids);
        return namedParameterJdbcTemplate.update(sequenceSqlProvider.getDeleteSql(), args) > 0;
    }
}
