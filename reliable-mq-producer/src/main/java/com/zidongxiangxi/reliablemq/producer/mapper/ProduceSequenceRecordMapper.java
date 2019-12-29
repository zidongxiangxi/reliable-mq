package com.zidongxiangxi.reliablemq.producer.mapper;

import com.zidongxiangxi.reliabelmq.api.entity.ProduceSequenceRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 顺序mq的发送记录映射
 *
 * @author chenxudong
 * @date 2019/12/23
 */
public class ProduceSequenceRecordMapper implements RowMapper<ProduceSequenceRecord> {
    @Override
    public ProduceSequenceRecord mapRow(ResultSet resultSet, int i) throws SQLException {
        ProduceSequenceRecord record = new ProduceSequenceRecord();
        record.setId(resultSet.getLong("id"));
        record.setMessageId(resultSet.getString("message_id"));
        record.setApplication(resultSet.getString("application"));
        record.setGroupName(resultSet.getString("group_name"));
        record.setCreateTime(resultSet.getDate("create_time"));
        return record;
    }
}
