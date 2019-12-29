package com.zidongxiangxi.reliablemq.producer.manager;

import com.zidongxiangxi.reliabelmq.api.constant.ProducerConstants;
import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.entity.enums.MessageSendStatusEnum;
import com.zidongxiangxi.reliabelmq.api.entity.enums.MessageTypeEnum;
import com.zidongxiangxi.reliabelmq.api.manager.ProduceRecordManager;
import com.zidongxiangxi.reliabelmq.api.transaction.ProduceRecordSqlProvider;
import com.zidongxiangxi.reliabelmq.api.transaction.ProduceSequenceRecordSqlProvider;
import com.zidongxiangxi.reliablemq.producer.mapper.RabbitProducerMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * mq消息生产记录manager
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public class RabbitProduceRecordManagerImpl implements ProduceRecordManager<RabbitProducer> {
    private JdbcTemplate jdbcTemplate;
    private ProduceRecordSqlProvider recordSqlProvider;
    private ProduceSequenceRecordSqlProvider sequenceRecordSqlProvider;

    public RabbitProduceRecordManagerImpl(JdbcTemplate jdbcTemplate, ProduceRecordSqlProvider recordSqlProvider) {
        this(jdbcTemplate, recordSqlProvider, null);
    }

    public RabbitProduceRecordManagerImpl(JdbcTemplate jdbcTemplate, ProduceRecordSqlProvider recordSqlProvider,
                                          ProduceSequenceRecordSqlProvider sequenceRecordSqlProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.recordSqlProvider = recordSqlProvider;
        this.sequenceRecordSqlProvider = sequenceRecordSqlProvider;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRecord(RabbitProducer producer) {
        if (Objects.isNull(producer.getMaxRetryTimes()) || producer.getMaxRetryTimes() > ProducerConstants.MAX_RETRY_TIMES) {
            producer.setMaxRetryTimes(ProducerConstants.MAX_RETRY_TIMES);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 20);
        int rows = jdbcTemplate.update(recordSqlProvider.getInsertSql(), producer.getApplication(),
            producer.getGroupName(), producer.getVirtualHost(), producer.getType(), producer.getExchange(),
            producer.getRoutingKey(), producer.getMessageId(), producer.getBody(),
            MessageSendStatusEnum.SENDING.getValue(), producer.getMaxRetryTimes(), calendar.getTime());
        if (rows < 1) {
            return false;
        }
        if (Objects.equals(producer.getType(), MessageTypeEnum.SEQUENCE.getValue())
            && Objects.nonNull(sequenceRecordSqlProvider)) {
            jdbcTemplate.update(sequenceRecordSqlProvider.getInsertSql(), producer.getMessageId(), producer.getApplication(),
                producer.getGroupName());
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean failToSend(String application, String messageId) {
        RabbitProducer producer = null;
        try {
            producer = jdbcTemplate.queryForObject(recordSqlProvider.getSelectSql(), new RabbitProducerMapper(),
                application, messageId);
        } catch (EmptyResultDataAccessException ignore) {}
        if (Objects.isNull(producer)) {
            return false;
        }
        int retryTimes = producer.getRetryTimes() + 1;
        retryTimes = Math.max(retryTimes, 1);
        int rows;
        if (retryTimes >= producer.getMaxRetryTimes()) {
            rows = jdbcTemplate.update(recordSqlProvider.getUpdateStatusSql(), application, messageId);
        } else {
            int delaySecondIndex = Math.min(retryTimes, ProducerConstants.DELAY_SECONDS.length);
            delaySecondIndex = delaySecondIndex - 1;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.SECOND, ProducerConstants.DELAY_SECONDS[delaySecondIndex]);
            rows = jdbcTemplate.update(recordSqlProvider.getUpdateRetrySql(), retryTimes, calendar.getTime(), application, messageId);
        }
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRecord(String application, String messageId) {
        int rows = jdbcTemplate.update(recordSqlProvider.getDeleteSql(), application, messageId);
        return rows > 0;
    }

    @Override
    public List<RabbitProducer> listSendingRecord(String application, int start, int limit) {
        return jdbcTemplate.query(recordSqlProvider.getListSendingSql(), new RabbitProducerMapper(), application, start,
            limit);
    }
}
