package com.zidongxiangxi.reliable.starter.producer.rabbit;

import com.zidongxiangxi.reliabelmq.api.alarm.Alarm;
import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.manager.ProducerManager;
import com.zidongxiangxi.reliabelmq.api.manager.SequenceManager;
import com.zidongxiangxi.reliabelmq.api.producer.RabbitMqSendService;
import com.zidongxiangxi.reliabelmq.api.producer.RabbitService;
import com.zidongxiangxi.reliabelmq.api.transaction.ProducerSqlProvider;
import com.zidongxiangxi.reliabelmq.api.transaction.SequenceSqlProvider;
import com.zidongxiangxi.reliabelmq.api.transaction.TransactionListener;
import com.zidongxiangxi.reliable.starter.config.ReliableMqProperties;
import com.zidongxiangxi.reliablemq.producer.DatabaseRabbitMqSendService;
import com.zidongxiangxi.reliablemq.producer.manager.RabbitProducerManagerImpl;
import com.zidongxiangxi.reliablemq.producer.processor.RabbitConnectionFactoryBeanPostProcessor;
import com.zidongxiangxi.reliablemq.producer.processor.RabbitTemplateBeanPostProcessor;
import com.zidongxiangxi.reliablemq.producer.scheduler.RabbitSendMqJob;
import com.zidongxiangxi.reliablemq.producer.service.ClientRabbitServiceImpl;
import com.zidongxiangxi.reliablemq.producer.transaction.DefaultTransactionSynchronization;
import com.zidongxiangxi.reliablemq.producer.transaction.listener.DatabaseRabbitProducerTransactionListener;
import com.zidongxiangxi.reliablemq.producer.transaction.sql.DefaultRabbitProducerSqlProvider;
import com.zidongxiangxi.reliablemq.producer.transaction.sql.DefaultSequenceSqlProvider;
import com.zidongxiangxi.reliable.starter.config.producer.ReliableMqProducer;
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
@ConditionalOnProperty(prefix = "reliable-mq.producer", name = "enabled", havingValue = "true")
public class ReliableMqRabbitProducerConfiguration {
    /**
     * 定义mq消息的manager
     *
     * @param jdbcTemplate jdbcTemplate实例
     * @return mq消息的数据库manager
     */
    @Bean
    @ConditionalOnMissingBean(name = {"rabbitProducerManager"})
    public ProducerManager<RabbitProducer> rabbitProducerManager(JdbcTemplate jdbcTemplate, ReliableMqProducer producer) {
        ProducerSqlProvider producerSqlProvider =
            new DefaultRabbitProducerSqlProvider(producer.getProducerTableName());
        SequenceSqlProvider sequenceSqlProvider = new DefaultSequenceSqlProvider(producer.getSequenceTableName());
        return new RabbitProducerManagerImpl(jdbcTemplate, producerSqlProvider, sequenceSqlProvider);
    }

    /**
     * 定义rabbitTemplate后置加工
     *
     * @param producerManager 消息生产manager
     * @return rabbitTemplate后置处理
     */
    @Bean
    public RabbitTemplateBeanPostProcessor rabbitTemplateBeanPostProcessor(
        ProducerManager<RabbitProducer> producerManager,
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
    @ConditionalOnMissingBean(RabbitMqSendService.class)
    public RabbitMqSendService rabbitMqSendService(
        ProducerManager<RabbitProducer> producerManager,
        RabbitService rabbitService,
        ReliableMqProperties properties
    ) {
        TransactionListener transactionListener = new DatabaseRabbitProducerTransactionListener(producerManager, rabbitService);
        TransactionSynchronization synchronization = new DefaultTransactionSynchronization(transactionListener);
        return new DatabaseRabbitMqSendService(synchronization, properties.getApplication(), rabbitService,
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
    @ConditionalOnMissingBean(RabbitService.class)
    public RabbitService rabbitService(
        SequenceManager sequenceManager,
        RabbitTemplate rabbitTemplate,
        ObjectProvider<Alarm> alarmProvider
    ) {
        return new ClientRabbitServiceImpl(sequenceManager, rabbitTemplate, alarmProvider);
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
        @ConditionalOnMissingBean(RabbitSendMqJob.class)
        @ConditionalOnProperty(prefix = "reliable-mq.producer.rely", name = "enabled", havingValue = "true", matchIfMissing = true)
        public RabbitSendMqJob rabbitSendMqJob(ProducerManager<RabbitProducer> producerManager,
            RabbitService rabbitService, ReliableMqProperties properties) {
            return new RabbitSendMqJob(producerManager, rabbitService, properties.getApplication(),
                properties.getProducer().getRely().getBatchSize());
        }
    }
}
