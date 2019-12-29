package com.zidongxiangxi.reliablemq.consumer.rely;

import com.alibaba.fastjson.JSON;
import com.zidongxiangxi.reliabelmq.api.alarm.Alarm;
import com.zidongxiangxi.reliabelmq.api.entity.ConsumeFailRecord;
import com.zidongxiangxi.reliabelmq.api.manager.ConsumeFailRecordManager;
import com.zidongxiangxi.reliabelmq.api.util.RabbitUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Objects;

/**
 * rabbitMq数据库消息恢复
 *
 * @author chenxudong
 * @date 2019/12/25
 */
@Slf4j
public class RabbitDatabaseMessageRecover implements MessageRecoverer {
    private ConsumeFailRecordManager consumeFailRecordManager;
    private Alarm alarm;

    public RabbitDatabaseMessageRecover(ConsumeFailRecordManager consumeFailRecordManager,
        ObjectProvider<Alarm> alarmProvider) {
        this.consumeFailRecordManager = consumeFailRecordManager;
        if (Objects.nonNull(alarmProvider)) {
            try {
                this.alarm = alarmProvider.getIfUnique();
            } catch (Throwable ignore) {}
        }
    }

    @Override
    public void recover(Message message, Throwable cause) {
        ConsumeFailRecord record = RabbitUtils.generateConsumeFailRecord(message, cause);
        if (Objects.isNull(record)) {
            return;
        }
        if (Objects.nonNull(alarm)) {
            alarm.failWhenConsume(record.getBody(), cause);
        }
        boolean success = consumeFailRecordManager.saveRecord(record);
        if (!success) {
            log.warn("[RabbitDatabaseMessageRecover] fail to save consume fail record:{}", JSON.toJSONString(record));
        }
    }
}
