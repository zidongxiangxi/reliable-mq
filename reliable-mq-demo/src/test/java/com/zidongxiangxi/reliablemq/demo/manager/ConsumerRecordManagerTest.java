package com.zidongxiangxi.reliablemq.demo.manager;

import com.zidongxiangxi.reliabelmq.api.manager.ConsumeRecordManager;
import com.zidongxiangxi.reliablemq.demo.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * mq消息消费manager
 *
 * @author chenxudong
 * @date 2019/12/29
 */
public class ConsumerRecordManagerTest extends BaseTest {
    @Autowired
    private ConsumeRecordManager consumeRecordManager;

    @Test
    public void testDelete() throws InterruptedException {
        generateToDb();
        Thread.sleep(2000);
        List<Long> ids = consumeRecordManager.listPrimaryKey(new Date(), 10);
        Assert.assertFalse(CollectionUtils.isEmpty(ids));
        boolean result = consumeRecordManager.deleteConsumeRecord(ids);
        Assert.assertTrue(result);
        List<Long> newIds = consumeRecordManager.listPrimaryKey(new Date(), 10);
        Set<Long> idSet = new HashSet<>(newIds);
        for (Long id : newIds) {
            Assert.assertFalse(idSet.contains(id));
        }
    }

    private void generateToDb() {
        boolean result = consumeRecordManager.insertConsumeRecord(UUID.randomUUID().toString(),
            UUID.randomUUID().toString());
        Assert.assertTrue(result);
    }
}
