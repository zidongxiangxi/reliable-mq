package com.zidongxiangxi.reliablemq.starter.producer.rabbit;

import com.zidongxiangxi.reliabelmq.api.alarm.Alarm;
import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.manager.ProduceRecordManager;
import com.zidongxiangxi.reliabelmq.api.manager.ProduceSequenceRecordManager;
import com.zidongxiangxi.reliabelmq.api.producer.RabbitMqProduceClient;
import com.zidongxiangxi.reliabelmq.api.producer.RabbitProducerService;
import com.zidongxiangxi.reliabelmq.api.transaction.ProduceRecordSqlProvider;
import com.zidongxiangxi.reliabelmq.api.transaction.ProduceSequenceRecordSqlProvider;
import com.zidongxiangxi.reliabelmq.api.transaction.TransactionListener;
import com.zidongxiangxi.reliablemq.starter.config.ReliableMqProperties;
import com.zidongxiangxi.reliablemq.producer.DatabaseRabbitMqProduceClient;
import com.zidongxiangxi.reliablemq.producer.manager.RabbitProduceRecordManagerImpl;
import com.zidongxiangxi.reliablemq.starter.producer.rabbit.processor.RabbitConnectionFactoryBeanPostProcessor;
import com.zidongxiangxi.reliablemq.starter.producer.rabbit.processor.RabbitTemplateBeanPostProcessor;
import com.zidongxiangxi.reliablemq.producer.scheduler.RabbitRetrySendJob;
import com.zidongxiangxi.reliablemq.producer.service.DefaultRabbitProducerServiceImpl;
import com.zidongxiangxi.reliablemq.producer.transaction.DefaultTransactionSynchronization;
import com.zidongxiangxi.reliablemq.producer.transaction.listener.DatabaseRabbitProducerTransactionListener;
import com.zidongxiangxi.reliablemq.producer.transaction.sql.RabbitProduceRecordSqlProvider;
import com.zidongxiangxi.reliablemq.producer.transaction.sql.DefaultProduceSequenceRecordSqlProvider;
import com.zidongxiangxi.reliablemq.starter.config.producer.ReliableMqProducer;
import com.xxl.job.core.handler.IJobHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronization;

/**
 * rabbitMq发送配置
 *
 * @author chenxudong
 * @date 2019/12/23
 */
@Configuration
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnProperty(prefix = "reliable-mq.producer.rabbit", name = "enabled", havingValue = "true")
public class ReliableMqRabbitProducerConfiguration {
    /**
     * 定义mq消息的manager
     *
     * @param jdbcTemplate jdbcTemplate实例
     * @param producer 发送配置
     * @return mq消息的数据库manager
     */
    @Bean
    public ProduceRecordManager<RabbitProducer> rabbitProduceRecordManager(JdbcTemplate jdbcTemplate, ReliableMqProducer producer) {
        ProduceRecordSqlProvider recordSqlProvider =
            new RabbitProduceRecordSqlProvider(producer.getRabbit().getRecordTableName());
        ProduceSequenceRecordSqlProvider sequenceRecordSqlProvider =
            new DefaultProduceSequenceRecordSqlProvider(producer.getSequence().getRecordTaleName());
        return new RabbitProduceRecordManagerImpl(jdbcTemplate, recordSqlProvider, sequenceRecordSqlProvider);
    }

    /**
     * 定义rabbitTemplate后置加工
     *
     * @param producerManager 消息生产manager
     * @return rabbitTemplate后置处理
     */
    @Bean
    public RabbitTemplateBeanPostProcessor rabbitTemplateBeanPostProcessor(
        ProduceRecordManager<RabbitProducer> producerManager,
        ObjectProvider<Alarm> alarmProvider
    ) {
        return new RabbitTemplateBeanPostProcessor(producerManager, alarmProvider);
    }

    /**
     * 定义rabbit连接工厂bean的后置加工
     *
     * @return rabbit连接工厂bean的后置加工
     */
    @Bean
    public RabbitConnectionFactoryBeanPostProcessor rabbitConnectionFactoryBeanPostProcessor() {
        return new RabbitConnectionFactoryBeanPostProcessor();
    }

    /**
     * 定义rabbit顺序消息发送类
     *
     * @param producerManager mq消息的manager
     * @param rabbitService 消息发送service
     * @return 消息发送类
     */
    @Bean
    @ConditionalOnMissingBean(RabbitMqProduceClient.class)
    public RabbitMqProduceClient rabbitMqSendClient(
        ProduceRecordManager<RabbitProducer> producerManager,
        RabbitProducerService rabbitService,
        ReliableMqProperties properties
    ) {
        TransactionListener transactionListener = new DatabaseRabbitProducerTransactionListener(producerManager, rabbitService);
        TransactionSynchronization synchronization = new DefaultTransactionSynchronization(transactionListener);
        return new DatabaseRabbitMqProduceClient(synchronization, properties.getApplication(), rabbitService,
            producerManager);
    }

    /**
     * 定义客户端发送消息的service
     *
     * @param sequenceManager mq消息的manager
     * @param rabbitTemplate rabbit消息发送模版
     * @return 异步消息发送service
     */
    @Bean
    @ConditionalOnMissingBean(RabbitProducerService.class)
    public RabbitProducerService rabbitProducerService(
        ProduceSequenceRecordManager sequenceManager,
        RabbitTemplate rabbitTemplate,
        ObjectProvider<Alarm> alarmProvider
    ) {
        return new DefaultRabbitProducerServiceImpl(sequenceManager, rabbitTemplate, alarmProvider);
    }

    /**
     * rabbitMq发送相关的定时任务
     */
    @ConditionalOnClass(IJobHandler.class)
    protected static class ReliableMqRabbitProducerJobConfiguration {
        /**
         * 定义xxl-job的重试发送任务
         *
         * @param producerManager mq消息的manager
         * @param rabbitService 客户端发送消息的service
         * @param properties 配置
         * @return 重试发送任务
         */
        @Bean
        @ConditionalOnMissingBean(RabbitRetrySendJob.class)
        @ConditionalOnProperty(prefix = "reliable-mq.producer.rabbit", name = "enabled-retry", havingValue = "true", matchIfMissing = true)
        public RabbitRetrySendJob rabbitRetrySendJob(ProduceRecordManager<RabbitProducer> producerManager,
            RabbitProducerService rabbitService, ReliableMqProperties properties) {
            return new RabbitRetrySendJob(producerManager, rabbitService, properties.getApplication(),
                properties.getProducer().getRabbit().getRetryBatchSize());
        }
    }
}
