package com.zidongxiangxi.reliablemq.producer.mapper;

import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * rabbitMq的消息映射
 *
 * @author chenxudong
 * @date 2019/09/14
 */
public class RabbitProducerMapper implements RowMapper<RabbitProducer> {
    @Override
    public RabbitProducer mapRow(ResultSet rs, int rowNum) throws SQLException {
        RabbitProducer producer = new RabbitProducer();
        producer.setId(rs.getLong("id"));
        producer.setType(rs.getInt("type"));
        producer.setApplication(rs.getString("application"));
        producer.setVirtualHost(rs.getString("virtual_host"));
        producer.setExchange(rs.getString("exchange"));
        producer.setRoutingKey(rs.getString("routing_key"));
        producer.setMessageId(rs.getString("message_id"));
        producer.setBody(rs.getString("body"));
        producer.setGroupName(rs.getString("group_name"));
        producer.setSendStatus(rs.getInt("send_status"));
        producer.setRetryTimes(rs.getInt("retry_times"));
        producer.setMaxRetryTimes(rs.getInt("max_retry_times"));
        producer.setNextRetryTime(rs.getDate("next_retry_time"));
        producer.setCreateTime(rs.getDate("create_time"));
        producer.setUpdateTime(rs.getDate("update_time"));
        return producer;
    }
}
