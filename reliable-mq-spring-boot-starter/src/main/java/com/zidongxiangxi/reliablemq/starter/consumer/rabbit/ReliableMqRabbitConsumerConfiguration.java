package com.zidongxiangxi.reliablemq.starter.consumer.rabbit;

import com.zidongxiangxi.reliabelmq.api.alarm.Alarm;
import com.zidongxiangxi.reliabelmq.api.manager.ConsumeFailRecordManager;
import com.zidongxiangxi.reliabelmq.api.manager.ConsumeRecordManager;
import com.zidongxiangxi.reliablemq.starter.config.consumer.ReliableMqConsumerRabbit;
import com.zidongxiangxi.reliablemq.starter.config.consumer.ReliableMqConsumerRely;
import com.zidongxiangxi.reliablemq.consumer.constant.BeanNameConstants;
import com.zidongxiangxi.reliablemq.consumer.interceptor.RabbitSequenceOperationsInterceptor;
import com.zidongxiangxi.reliablemq.consumer.interceptor.RabbitIdempotentOperationsInterceptor;
import com.zidongxiangxi.reliablemq.starter.consumer.rabbit.processor.RabbitListenerContainerBeanPostProcessor;
import com.zidongxiangxi.reliablemq.starter.consumer.rabbit.processor.SimpleRabbitListenerContainerFactoryBeanPostProcessor;
import com.zidongxiangxi.reliablemq.consumer.rely.RabbitDatabaseMessageRecover;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@ConditionalOnClass(SimpleMessageListenerContainer.class)
@ConditionalOnProperty(prefix = "reliable-mq.consumer.rabbit", name = "enabled", havingValue = "true")
public class ReliableMqRabbitConsumerConfiguration {
    private ReliableMqConsumerRabbit rabbitProperties;
    public ReliableMqRabbitConsumerConfiguration(ReliableMqConsumerRabbit rabbitProperties) {
        this.rabbitProperties = rabbitProperties;
    }
    /**
     * 定义幂等消费的拦截器
     *
     * @param consumeRecordManager 消费记录manager
     * @param transactionTemplate spring事务template
     * @return 幂等消费的拦截器
     */
    @Bean(name = BeanNameConstants.INTERNAL_RABBIT_IDEMPOTENT_OPERATIONS_INTERCEPTOR)
    @ConditionalOnMissingBean(RabbitIdempotentOperationsInterceptor.class)
    @ConditionalOnProperty(prefix = "reliable-mq.consumer.rabbit.idempotent", name = "enabled", havingValue = "true")
    public RabbitIdempotentOperationsInterceptor idempotentOperationsInterceptor(
        ConsumeRecordManager consumeRecordManager, TransactionTemplate transactionTemplate) {
        return new RabbitIdempotentOperationsInterceptor(consumeRecordManager, transactionTemplate);
    }

    /**
     * 定义顺序消费拦截器
     *
     * @param consumeRecordManager 消费记录manager
     * @return 顺序消费拦截器
     */
    @Bean(name = BeanNameConstants.INTERNAL_RABBIT_SEQUENCE_OPERATIONS_INTERCEPTOR)
    @ConditionalOnMissingBean(RabbitSequenceOperationsInterceptor.class)
    @ConditionalOnProperty(prefix = "reliable-mq.consumer.rabbit.sequence", name = "enabled", havingValue = "true")
    public RabbitSequenceOperationsInterceptor sequenceOperationsInterceptor(ConsumeRecordManager consumeRecordManager) {
        return new RabbitSequenceOperationsInterceptor(consumeRecordManager, rabbitProperties.getSequence().getConsumeFailDelay(),
                rabbitProperties.getSequence().getFaultTolerantTime());
    }

    /**
     * rabbit可靠消费（消费失败告警和保存）的配置
     * 只有在mq消息消费失败后，不放回队列，才生效
     */
    @ConditionalOnProperty(prefix = "reliable-mq.consumer.rabbit.rely", name = "enabled", havingValue = "true")
    @EnableConfigurationProperties(ReliableMqConsumerRabbit.class)
    protected static class ReliableMqRabbitRelyConsumeConfiguration {
        private ReliableMqConsumerRabbit rabbitProperties;
        public ReliableMqRabbitRelyConsumeConfiguration(ReliableMqConsumerRabbit rabbitProperties) {
            this.rabbitProperties = rabbitProperties;
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
        @Bean(BeanNameConstants.INTERNAL_RABBIT_RETRY_OPERATIONS_INTERCEPTOR)
        @ConditionalOnMissingBean(RetryOperationsInterceptor.class)
        public RetryOperationsInterceptor retryOperationsInterceptor(
            ObjectProvider<MessageRecoverer> messageRecovererProvider
        ) {
            ReliableMqConsumerRely properties = rabbitProperties.getRely();
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

    /**
     * 定义rabbit的simple监听容器工厂bean的后置加工
     * 该加工厂将“幂等消费拦截器”、“顺序消费拦截器”和“消费失败告警和保存”的bean设置到工厂中
     *
     * @return rabbit的simple监听容器工厂bean的后置加工
     */
    @Bean
    public SimpleRabbitListenerContainerFactoryBeanPostProcessor simpleRabbitListenerContainerFactoryBeanPostProcessor(
            ObjectProvider<RabbitSequenceOperationsInterceptor> sequenceInterceptorProvider,
            ObjectProvider<RetryOperationsInterceptor> retryInterceptorProvider,
            ObjectProvider<RabbitIdempotentOperationsInterceptor> idempotentInterceptorProvider
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
            ObjectProvider<RabbitSequenceOperationsInterceptor> sequenceInterceptorProvider,
            ObjectProvider<RetryOperationsInterceptor> retryInterceptorProvider,
            ObjectProvider<RabbitIdempotentOperationsInterceptor> idempotentInterceptorProvider
    ) {
        return new RabbitListenerContainerBeanPostProcessor(sequenceInterceptorProvider, retryInterceptorProvider, idempotentInterceptorProvider);
    }

}
