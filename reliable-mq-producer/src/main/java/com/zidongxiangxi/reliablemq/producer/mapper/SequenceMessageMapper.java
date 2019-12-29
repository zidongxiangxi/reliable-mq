package com.zidongxiangxi.reliablemq.producer.mapper;

import com.zidongxiangxi.reliabelmq.api.entity.SequenceMessage;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 顺序mq的发送记录映射
 *
 * @author chenxudong
 * @date 2019/12/23
 */
public class SequenceMessageMapper implements RowMapper<SequenceMessage> {
    @Override
    public SequenceMessage mapRow(ResultSet resultSet, int i) throws SQLException {
        SequenceMessage message = new SequenceMessage();
        message.setId(resultSet.getLong("id"));
        message.setMessageId(resultSet.getString("message_id"));
        message.setApplication(resultSet.getString("application"));
        message.setGroupName(resultSet.getString("group_name"));
        message.setCreateTime(resultSet.getDate("create_time"));
        return message;
    }
}
