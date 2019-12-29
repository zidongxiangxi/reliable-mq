package com.zidongxiangxi.reliablemq.producer.manager;

import com.zidongxiangxi.reliabelmq.api.entity.SequenceMessage;
import com.zidongxiangxi.reliabelmq.api.manager.SequenceManager;
import com.zidongxiangxi.reliabelmq.api.transaction.SequenceSqlProvider;
import com.zidongxiangxi.reliablemq.producer.mapper.SequenceMessageMapper;
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
public class DefaultSequenceManager implements SequenceManager {
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private SequenceSqlProvider sequenceSqlProvider;

    public DefaultSequenceManager(JdbcTemplate jdbcTemplate, SequenceSqlProvider sequenceSqlProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.sequenceSqlProvider = sequenceSqlProvider;
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public String getPreviousMessageId(String application, String messageId) {
        List<SequenceMessage> sequenceMessageList =
            jdbcTemplate.query(sequenceSqlProvider.getSelectByMessageIdSql(),
                new SequenceMessageMapper(), application, messageId);
        if (CollectionUtils.isEmpty(sequenceMessageList)) {
            return null;
        }
        SequenceMessage sequenceMessage = sequenceMessageList.get(0);
        List<Long> previousIdList = jdbcTemplate.queryForList(sequenceSqlProvider.getSelectPreviousIdSql(), Long.class,
            sequenceMessage.getApplication(), sequenceMessage.getGroupName(), sequenceMessage.getId());
        if (CollectionUtils.isEmpty(previousIdList)) {
            return null;
        }
        Long previousId = previousIdList.get(0);
        sequenceMessageList = jdbcTemplate.query(sequenceSqlProvider.getSelectByIdSql(),
            new SequenceMessageMapper(), previousId);
        return CollectionUtils.isEmpty(sequenceMessageList) ? null : sequenceMessageList.get(0).getMessageId();
    }

    @Override
    public List<SequenceMessage> list(Date beforeTime, int size) {
        return jdbcTemplate.query(sequenceSqlProvider.getListSql(), new SequenceMessageMapper(), beforeTime, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        Map<String, Object> args = new HashMap<>(1);
        args.put("ids", ids);
        return namedParameterJdbcTemplate.update(sequenceSqlProvider.getDeleteSql(), args) > 0;
    }
}
