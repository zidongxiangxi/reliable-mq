package com.zidongxiangxi.reliable.starter.producer;

import com.zidongxiangxi.reliabelmq.api.manager.ProduceSequenceRecordManager;
import com.zidongxiangxi.reliablemq.producer.manager.DefaultProduceSequenceRecordManager;
import com.zidongxiangxi.reliablemq.producer.scheduler.SequenceRecordClearJob;
import com.zidongxiangxi.reliablemq.producer.transaction.sql.DefaultProduceSequenceRecordSqlProvider;
import com.zidongxiangxi.reliable.starter.config.producer.ReliableMqProducer;
import com.zidongxiangxi.reliable.starter.config.producer.ReliableMqProducerSequence;
import com.zidongxiangxi.reliable.starter.producer.rabbit.ReliableMqRabbitProducerConfiguration;
import com.xxl.job.core.handler.IJobHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 发送者配置
 *
 * @author chenxudong
 * @date 2019/12/23
 */
@EnableAsync
@Configuration
@EnableConfigurationProperties({ReliableMqProducer.class, ReliableMqProducerSequence.class})
@Import({ReliableMqRabbitProducerConfiguration.class})
public class ReliableMqProducerAutoConfiguration {
    /**
     * 定义顺序消息记录的manager
     *
     * @param jdbcTemplate jdbcTemplate实例
     * @param sequence 顺序消息相关配置
     * @return 顺序消息记录的manager
     */
    @Bean
    @ConditionalOnBean(JdbcTemplate.class)
    public ProduceSequenceRecordManager produceSequenceRecordManager(JdbcTemplate jdbcTemplate, ReliableMqProducerSequence sequence) {
        return new DefaultProduceSequenceRecordManager(jdbcTemplate,
            new DefaultProduceSequenceRecordSqlProvider(sequence.getRecordTaleName()));
    }

    /**
     * mq发送相关的定时任务
     */
    @ConditionalOnClass(IJobHandler.class)
    protected static class ReliableMqProducerJobConfiguration {
        /**
         * 定义xxl-job的清理顺序消息记录任务
         *
         * @param sequenceManager 顺序消息记录manager
         * @param sequence 顺序消息相关配置
         * @return 重试发送任务
         */
        @Bean
        @ConditionalOnMissingBean(SequenceRecordClearJob.class)
        @ConditionalOnProperty(prefix = "reliable-mq.producer.sequence", name = "enabled-clear", havingValue = "true")
        public SequenceRecordClearJob sequenceRecordClearJob(ProduceSequenceRecordManager sequenceManager, ReliableMqProducerSequence sequence) {
            return new SequenceRecordClearJob(sequenceManager, sequence.getRetentionPeriod(),
                    sequence.getClearBatchSize());
        }
    }
}
