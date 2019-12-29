package com.zidongxiangxi.reliable.starter.consumer.rabbit;

import com.zidongxiangxi.reliabelmq.api.alarm.Alarm;
import com.zidongxiangxi.reliabelmq.api.manager.ConsumeFailRecordManager;
import com.zidongxiangxi.reliabelmq.api.manager.ConsumeRecordManager;
import com.zidongxiangxi.reliable.starter.config.consumer.ReliableMqConsumerSequence;
import com.zidongxiangxi.reliablemq.consumer.constant.BeanNameConstants;
import com.zidongxiangxi.reliablemq.consumer.interceptor.SequenceOperationsInterceptor;
import com.zidongxiangxi.reliablemq.consumer.interceptor.TransactionIdempotentOperationsInterceptor;
import com.zidongxiangxi.reliablemq.consumer.manager.DefaultConsumeFailRecordManager;
import com.zidongxiangxi.reliablemq.consumer.processor.RabbitListenerContainerBeanPostProcessor;
import com.zidongxiangxi.reliablemq.consumer.processor.SimpleRabbitListenerContainerFactoryBeanPostProcessor;
import com.zidongxiangxi.reliablemq.consumer.rely.RabbitDatabaseMessageRecover;
import com.zidongxiangxi.reliablemq.consumer.transaction.DefaultConsumeFailRecordSqlProvider;
import com.zidongxiangxi.reliable.starter.config.consumer.ReliableMqConsumerRely;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.Objects;

/**
 * rabbitMq消费者配置
 *
 * @author chenxudong
 * @date 2019/12/23
 */
@Configuration
@ConditionalOnClass(RabbitListener.class)
public class ReliableMqRabbitConsumerConfiguration {
    /**
     * 定义幂等消费的拦截器
     *
     * @param consumeRecordManager 消费记录manager
     * @param transactionTemplate spring事务template
     * @return 幂等消费的拦截器
     */
    @Bean(name = BeanNameConstants.INTERNAL_IDEMPOTENT_OPERATIONS_INTERCEPTOR)
    @ConditionalOnMissingBean(TransactionIdempotentOperationsInterceptor.class)
    @ConditionalOnProperty(prefix = "reliable-mq.consumer.idempotent", name = "enabled", havingValue = "true")
    public TransactionIdempotentOperationsInterceptor idempotentOperationsInterceptor(
        ConsumeRecordManager consumeRecordManager, TransactionTemplate transactionTemplate) {
        return new TransactionIdempotentOperationsInterceptor(consumeRecordManager, transactionTemplate);
    }

    /**
     * 定义顺序消费拦截器
     *
     * @param consumeRecordManager 消费记录manager
     * @param sequence 顺序消费配置
     * @return 顺序消费拦截器
     */
    @Bean(name = BeanNameConstants.INTERNAL_SEQUENCE_OPERATIONS_INTERCEPTOR)
    @ConditionalOnMissingBean(SequenceOperationsInterceptor.class)
    @ConditionalOnProperty(prefix = "reliable-mq.consumer.sequence", name = "enabled", havingValue = "true")
    public SequenceOperationsInterceptor sequenceOperationsInterceptor(ConsumeRecordManager consumeRecordManager,
        ReliableMqConsumerSequence sequence) {
        return new SequenceOperationsInterceptor(consumeRecordManager, sequence.getConsumeFailDelay(), sequence.getFaultTolerantTime());
    }

    /**
     * 定义rabbit的simple监听容器工厂bean的后置加工
     * 该加工厂将“幂等消费拦截器”、“顺序消费拦截器”和“消费失败告警和保存”的bean设置到工厂中
     *
     * @return rabbit的simple监听容器工厂bean的后置加工
     */
    @Bean
    public SimpleRabbitListenerContainerFactoryBeanPostProcessor simpleRabbitListenerContainerFactoryBeanPostProcessor(
        ObjectProvider<SequenceOperationsInterceptor> sequenceInterceptorProvider,
        ObjectProvider<RetryOperationsInterceptor> retryInterceptorProvider,
        ObjectProvider<TransactionIdempotentOperationsInterceptor> idempotentInterceptorProvider
    ) {
        return new SimpleRabbitListenerContainerFactoryBeanPostProcessor(sequenceInterceptorProvider,
            retryInterceptorProvider, idempotentInterceptorProvider);
    }

    /**
     * 定义rabbit的监听容器bean的后置加工
     * 该加工厂将“幂等消费拦截器”、“顺序消费拦截器”和“消费失败告警和保存”的bean设置到监听容器中
     *
     * @return rabbit的监听容器bean的后置加工
     */
    @Bean
    public RabbitListenerContainerBeanPostProcessor rabbitListenerContainerBeanPostProcessor(
        ObjectProvider<SequenceOperationsInterceptor> sequenceInterceptorProvider,
        ObjectProvider<RetryOperationsInterceptor> retryInterceptorProvider,
        ObjectProvider<TransactionIdempotentOperationsInterceptor> idempotentInterceptorProvider
    ) {
        return new RabbitListenerContainerBeanPostProcessor(sequenceInterceptorProvider, retryInterceptorProvider, idempotentInterceptorProvider);
    }

    /**
     * rabbit可靠消费（消费失败告警和保存）的配置
     * 只有在mq消息消费失败后，不放回队列，才生效
     */
    @ConditionalOnProperty(prefix = "reliable-mq.consumer.rely", name = "enabled", havingValue = "true")
    @EnableConfigurationProperties(RabbitProperties.class)
    protected static class ReliableMqRabbitRelyConsumeConfiguration {
        private RabbitProperties rabbitProperties;
        public ReliableMqRabbitRelyConsumeConfiguration(RabbitProperties rabbitProperties) {
            this.rabbitProperties = rabbitProperties;
        }

        /**
         * 定义消息消费失败记录manager
         *
         * @param jdbcTemplate jdbcTemplate
         * @param rely 可靠消费配置
         * @return 消息消费失败记录manager
         */
        @Bean
        @ConditionalOnMissingBean(ConsumeFailRecordManager.class)
        public ConsumeFailRecordManager rabbitConsumeFailManager(JdbcTemplate jdbcTemplate, ReliableMqConsumerRely rely) {
            return new DefaultConsumeFailRecordManager(jdbcTemplate,
                new DefaultConsumeFailRecordSqlProvider(rely.getConsumeFailRecordTableName()));
        }

        /**
         * 定义rabbitMq消费失败后的消息保存
         *
         * @param consumeFailRecordManager 消息消费失败记录manager
         * @param alarmProvider 告警bean提供者
         * @return rabbitMq消费失败后的消息保存
         */
        @Bean
        @ConditionalOnMissingBean(MessageRecoverer.class)
        @ConditionalOnProperty(prefix = "reliable-mq.consumer.rely", name = "enabled", havingValue = "true")
        public MessageRecoverer rabbitMessageRecover(ConsumeFailRecordManager consumeFailRecordManager,
            ObjectProvider<Alarm> alarmProvider) {
           return new RabbitDatabaseMessageRecover(consumeFailRecordManager, alarmProvider);
        }

        /**
         * 定义失败重试的interceptor
         *
         * @param messageRecovererProvider 消息恢复提供者
         * @return 失败重试的interceptor
         */
        @Bean(BeanNameConstants.INTERNAL_RETRY_OPERATIONS_INTERCEPTOR)
        @ConditionalOnMissingBean(RetryOperationsInterceptor.class)
        public RetryOperationsInterceptor retryOperationsInterceptor(
            ObjectProvider<MessageRecoverer> messageRecovererProvider
        ) {
            RabbitProperties.Retry properties = rabbitProperties.getListener().getSimple().getRetry();
            RetryInterceptorBuilder<RetryInterceptorBuilder.StatelessRetryInterceptorBuilder,
                RetryOperationsInterceptor> builder = RetryInterceptorBuilder.stateless();
            PropertyMapper map = PropertyMapper.get();
            RetryTemplate template = new RetryTemplate();
            SimpleRetryPolicy policy = new SimpleRetryPolicy();
            map.from(properties::getMaxAttempts).to(policy::setMaxAttempts);
            template.setRetryPolicy(policy);
            ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
            map.from(properties::getInitialInterval).whenNonNull().as(Duration::toMillis)
                .to(backOffPolicy::setInitialInterval);
            map.from(properties::getMultiplier).to(backOffPolicy::setMultiplier);
            map.from(properties::getMaxInterval).whenNonNull().as(Duration::toMillis)
                .to(backOffPolicy::setMaxInterval);
            template.setBackOffPolicy(backOffPolicy);

            builder.retryOperations(template);
            MessageRecoverer recoverer = null;
            try {
                recoverer = messageRecovererProvider.getIfUnique();
            } catch (Throwable e) {}
            recoverer = Objects.nonNull(recoverer) ? recoverer : new RejectAndDontRequeueRecoverer();
            builder.recoverer(recoverer);
            return builder.build();
        }
    }

}
