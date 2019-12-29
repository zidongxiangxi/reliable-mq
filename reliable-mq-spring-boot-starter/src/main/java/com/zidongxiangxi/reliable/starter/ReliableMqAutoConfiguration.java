package com.zidongxiangxi.reliable.starter;

import com.zidongxiangxi.reliable.starter.config.ReliableMqProperties;
import com.zidongxiangxi.reliable.starter.consumer.ReliableMqConsumerAutoConfiguration;
import com.zidongxiangxi.reliable.starter.producer.ReliableMqProducerAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * reliable-mq自动配置类
 *
 * @author chenxudong
 * @date 2019/12/23
 */
@Configuration
@AutoConfigureAfter({DataSourceAutoConfiguration.class, RabbitAutoConfiguration.class})
@ConditionalOnClass({JdbcTemplate.class})
@EnableConfigurationProperties({ReliableMqProperties.class})
@Import({ReliableMqProducerAutoConfiguration.class, ReliableMqConsumerAutoConfiguration.class})
public class ReliableMqAutoConfiguration {
    /**
     * 定义jdbcTemplate
     *
     * @param dataSource 数据源
     * @return jdbcTemplate
     */
    @Bean
    @ConditionalOnMissingBean(JdbcTemplate.class)
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * 定义事务管理器
     *
     * @param dataSource 数据源
     * @return 事务管理器
     */
    @Bean
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    @ConditionalOnSingleCandidate(DataSource.class)
    public DataSourceTransactionManager reliableMqTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 定义事务template
     *
     * @param reliableMqTransactionManager 事务管理器
     * @return 事务template
     */
    @Bean
    @ConditionalOnMissingBean(TransactionTemplate.class)
    @ConditionalOnSingleCandidate(DataSourceTransactionManager.class)
    public TransactionTemplate reliableMqTransactionTemplate(DataSourceTransactionManager reliableMqTransactionManager) {
        return new TransactionTemplate(reliableMqTransactionManager);
    }

}
