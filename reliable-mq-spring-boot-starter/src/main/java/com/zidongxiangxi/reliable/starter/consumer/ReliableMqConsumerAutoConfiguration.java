package com.zidongxiangxi.reliable.starter.consumer;

import com.zidongxiangxi.reliabelmq.api.manager.ConsumeFailRecordManager;
import com.zidongxiangxi.reliabelmq.api.manager.ConsumeRecordManager;
import com.zidongxiangxi.reliable.starter.config.consumer.*;
import com.zidongxiangxi.reliable.starter.consumer.rabbit.ReliableMqRabbitConsumerConfiguration;
import com.zidongxiangxi.reliablemq.consumer.manager.DefaultConsumeFailRecordManager;
import com.zidongxiangxi.reliablemq.consumer.manager.DefaultConsumeRecordManager;
import com.zidongxiangxi.reliablemq.consumer.sheduler.ConsumeRecordClearJob;
import com.zidongxiangxi.reliablemq.consumer.transaction.DefaultConsumeFailRecordSqlProvider;
import com.zidongxiangxi.reliablemq.consumer.transaction.DefaultConsumeRecordSqlProvider;
import com.xxl.job.core.handler.IJobHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 消费者配置
 *
 * @author chenxudong
 * @date 2019/12/23
 */
@Configuration
@EnableConfigurationProperties({ReliableMqConsumer.class, ReliableMqConsumerClear.class,
    ReliableMqConsumerRabbit.class})
@Import({ReliableMqRabbitConsumerConfiguration.class})
public class ReliableMqConsumerAutoConfiguration {
    /**
     * 消费记录manager
     *
     * @param jdbcTemplate jdbcTemplate
     * @param consumer 消费者配置信息
     * @return 消费记录manager
     */
    @Bean
    @ConditionalOnMissingBean(ConsumeRecordManager.class)
    public ConsumeRecordManager consumeRecordManager(JdbcTemplate jdbcTemplate, ReliableMqConsumer consumer) {
        return new DefaultConsumeRecordManager(jdbcTemplate,
            new DefaultConsumeRecordSqlProvider(consumer.getRecordTableName()));
    }

    /**
     * 定义消息消费失败记录manager
     *
     * @param jdbcTemplate jdbcTemplate
     * @param consumer 消费者配置信息
     * @return 消息消费失败记录manager
     */
    @Bean
    @ConditionalOnMissingBean(ConsumeFailRecordManager.class)
    public ConsumeFailRecordManager consumeFailRecordManager(JdbcTemplate jdbcTemplate, ReliableMqConsumer consumer) {
        return new DefaultConsumeFailRecordManager(jdbcTemplate,
                new DefaultConsumeFailRecordSqlProvider(consumer.getFailRecordTableName()));
    }

    /**
     * mq消费相关的定时任务
     */
    @ConditionalOnClass(IJobHandler.class)
    @ConditionalOnProperty(prefix = "reliable-mq.consumer.clear", name = "enabled", havingValue = "true")
    protected static class ReliableMqConsumerJobConfiguration {
        @Bean
        @ConditionalOnMissingBean(ConsumeRecordClearJob.class)
        public ConsumeRecordClearJob consumeRecordClearJob(ConsumeRecordManager consumeRecordManager, ReliableMqConsumerClear clear) {
            return new ConsumeRecordClearJob(consumeRecordManager, clear.getRetentionPeriod(), clear.getBatchSize());
        }
    }
}
