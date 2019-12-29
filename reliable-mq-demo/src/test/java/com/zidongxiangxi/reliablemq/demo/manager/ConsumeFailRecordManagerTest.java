package com.zidongxiangxi.reliablemq.demo.manager;

import com.alibaba.fastjson.JSON;
import com.zidongxiangxi.reliabelmq.api.entity.ConsumeFailRecord;
import com.zidongxiangxi.reliabelmq.api.manager.ConsumeFailRecordManager;
import com.zidongxiangxi.reliablemq.demo.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * 消费失败记录manager测试类
 *
 * @author chenxudong
 * @date 2019/12/29
 */
public class ConsumeFailRecordManagerTest extends BaseTest {
    @Autowired
    private ConsumeFailRecordManager consumeFailRecordManager;

    @Test
    public void testSaveRecord() {
        generateToDb();
    }

    @Test
    public void testSelect() {
        ConsumeFailRecord record = generateToDb();
        record = consumeFailRecordManager.getRecord(record.getApplication(), record.getMessageId());
        Assert.assertNotNull(record);
        System.out.println(JSON.toJSONString(record));
    }

    private ConsumeFailRecord generateToDb() {
        ConsumeFailRecord record = new ConsumeFailRecord();
        record.setQueue(UUID.randomUUID().toString());
        record.setApplication(UUID.randomUUID().toString());
        record.setMessageId(UUID.randomUUID().toString());
        record.setHeaders(UUID.randomUUID().toString());
        record.setBody(UUID.randomUUID().toString());
        record.setErrorStack(UUID.randomUUID().toString());
        boolean result = consumeFailRecordManager.saveRecord(record);
        Assert.assertTrue(result);
        return record;
    }
}
