package com.zidongxiangxi.reliablemq.consumer.mapper;

import com.zidongxiangxi.reliabelmq.api.entity.ConsumeFailRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 消息消费失败记录映射
 *
 * @author chenxudong
 * @date 2019/12/24
 */
public class ConsumeFailRecordMapper implements RowMapper<ConsumeFailRecord> {
    @Override
    public ConsumeFailRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        ConsumeFailRecord record = new ConsumeFailRecord();
        record.setId(rs.getLong("id"));
        record.setQueue(rs.getString("queue"));
        record.setApplication(rs.getString("application"));
        record.setMessageId(rs.getString("message_id"));
        record.setHeaders(rs.getString("headers"));
        record.setBody(rs.getString("body"));
        record.setErrorStack(rs.getString("error_stack"));
        record.setCreateTime(rs.getDate("create_time"));
        return record;
    }
}
