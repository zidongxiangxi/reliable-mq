package com.zidongxiangxi.reliablemq.producer.manager;

import com.zidongxiangxi.reliabelmq.api.constant.ProducerConstants;
import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.entity.enums.MessageSendStatusEnum;
import com.zidongxiangxi.reliabelmq.api.entity.enums.MessageTypeEnum;
import com.zidongxiangxi.reliabelmq.api.manager.ProducerManager;
import com.zidongxiangxi.reliabelmq.api.transaction.ProducerSqlProvider;
import com.zidongxiangxi.reliabelmq.api.transaction.SequenceSqlProvider;
import com.zidongxiangxi.reliablemq.producer.mapper.RabbitProducerMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * mq消息生产者manager
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public class RabbitProducerManagerImpl implements ProducerManager<RabbitProducer> {
    private JdbcTemplate jdbcTemplate;
    private ProducerSqlProvider producerSqlProvider;
    private SequenceSqlProvider sequenceSqlProvider;

    public RabbitProducerManagerImpl(JdbcTemplate jdbcTemplate, ProducerSqlProvider producerSqlProvider) {
        this(jdbcTemplate, producerSqlProvider, null);
    }

    public RabbitProducerManagerImpl(JdbcTemplate jdbcTemplate, ProducerSqlProvider producerSqlProvider,
        SequenceSqlProvider sequenceSqlProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.producerSqlProvider = producerSqlProvider;
        this.sequenceSqlProvider = sequenceSqlProvider;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveMqProducer(RabbitProducer producer) {
        if (Objects.isNull(producer.getMaxRetryTimes()) || producer.getMaxRetryTimes() > ProducerConstants.MAX_RETRY_TIMES) {
            producer.setMaxRetryTimes(ProducerConstants.MAX_RETRY_TIMES);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 20);
        int rows = jdbcTemplate.update(producerSqlProvider.getInsertMqSql(), producer.getApplication(),
            producer.getGroupName(), producer.getVirtualHost(), producer.getType(), producer.getExchange(),
            producer.getRoutingKey(), producer.getMessageId(), producer.getBody(),
            MessageSendStatusEnum.SENDING.getValue(), producer.getMaxRetryTimes(), calendar.getTime());
        if (rows < 1) {
            return false;
        }
        if (Objects.equals(producer.getType(), MessageTypeEnum.SEQUENCE.getValue())
            && Objects.nonNull(sequenceSqlProvider)) {
            jdbcTemplate.update(sequenceSqlProvider.getInsertSql(), producer.getMessageId(), producer.getApplication(),
                producer.getGroupName());
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean failSendMq(String application, String messageId) {
        RabbitProducer producer = null;
        try {
            producer = jdbcTemplate.queryForObject(producerSqlProvider.getSelectMqSql(), new RabbitProducerMapper(),
                application, messageId);
        } catch (EmptyResultDataAccessException ignore) {}
        if (Objects.isNull(producer)) {
            return false;
        }
        int retryTimes = producer.getRetryTimes() + 1;
        retryTimes = Math.max(retryTimes, 1);
        int rows;
        if (retryTimes >= producer.getMaxRetryTimes()) {
            rows = jdbcTemplate.update(producerSqlProvider.getFailMqSql(), application, messageId);
        } else {
            int delaySecondIndex = Math.min(retryTimes, ProducerConstants.DELAY_SECONDS.length);
            delaySecondIndex = delaySecondIndex - 1;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.SECOND, ProducerConstants.DELAY_SECONDS[delaySecondIndex]);
            rows = jdbcTemplate.update(producerSqlProvider.getSendingMqSql(), retryTimes, calendar.getTime(), application, messageId);
        }
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMq(String application, String messageId) {
        int rows = jdbcTemplate.update(producerSqlProvider.getDeleteMqSql(), application, messageId);
        return rows > 0;
    }

    @Override
    public List<RabbitProducer> listSendingMq(String application, int start, int limit) {
        return jdbcTemplate.query(producerSqlProvider.getListSendingMqSql(), new RabbitProducerMapper(), application, start,
            limit);
    }

    @Override
    public List<RabbitProducer> listAllApplicationMq(MessageSendStatusEnum sendStatus, int start, int limit) {
        return jdbcTemplate.query(producerSqlProvider.getListMqSql(), new RabbitProducerMapper(),
            sendStatus.getValue(), start, limit);
    }
}
