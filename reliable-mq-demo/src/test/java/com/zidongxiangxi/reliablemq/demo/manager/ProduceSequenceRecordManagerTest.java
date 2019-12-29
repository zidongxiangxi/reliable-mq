package com.zidongxiangxi.reliablemq.demo.manager;

import com.alibaba.fastjson.JSON;
import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.entity.ProduceSequenceRecord;
import com.zidongxiangxi.reliabelmq.api.entity.enums.MessageTypeEnum;
import com.zidongxiangxi.reliabelmq.api.manager.ProduceRecordManager;
import com.zidongxiangxi.reliabelmq.api.manager.ProduceSequenceRecordManager;
import com.zidongxiangxi.reliablemq.demo.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 顺序消息记录manager测试类
 *
 * @author chenxudong
 * @date 2019/12/24
 */
public class ProduceSequenceRecordManagerTest extends BaseTest {
    @Value("${reliable-mq.application}")
    private String application;
    private String virtualHost = "/";
    private String exchange = "test.exchange.fanout";
    @Autowired
    private ProduceRecordManager<RabbitProducer> produceRecordManager;
    @Autowired
    private ProduceSequenceRecordManager produceSequenceRecordManager;

    @Test
    public void testGetPreviousMessageId() {
        String groupName = UUID.randomUUID().toString();
        RabbitProducer producer1 = saveSequenceProducer(groupName);
        RabbitProducer producer2 = saveSequenceProducer(groupName);
        String previousMessageId = produceSequenceRecordManager.getPreviousMessageId(producer2.getApplication(),
            producer2.getMessageId());
        Assert.assertEquals(producer1.getMessageId(), previousMessageId);
    }

    @Test
    public void testList() throws InterruptedException {
        saveSequenceProducer(UUID.randomUUID().toString());
        Thread.sleep(1000L);
        Date beforeTime = new Date();
        List<ProduceSequenceRecord> list = produceSequenceRecordManager.listRecord(beforeTime, 20);
        Assert.assertFalse(CollectionUtils.isEmpty(list));
        System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void testDelete() throws InterruptedException {
        saveSequenceProducer(UUID.randomUUID().toString());
        Thread.sleep(1000L);
        List<ProduceSequenceRecord> list = produceSequenceRecordManager.listRecord(new Date(), 20);
        Assert.assertFalse(CollectionUtils.isEmpty(list));
        List<Long> ids = list.stream().map(ProduceSequenceRecord::getId).collect(Collectors.toList());
        produceSequenceRecordManager.deleteRecordByIds(ids);
        list = produceSequenceRecordManager.listRecord(new Date(), 20);
        Set<Long> idSet = list.stream().map(ProduceSequenceRecord::getId).collect(Collectors.toSet());
        for (Long id : ids) {
            Assert.assertFalse(idSet.contains(id));
        }
    }

    private RabbitProducer saveSequenceProducer(String groupName) {
        RabbitProducer producer = new RabbitProducer();
        producer.setApplication(application);
        producer.setType(MessageTypeEnum.SEQUENCE.getValue());
        producer.setGroupName(groupName);
        producer.setVirtualHost(virtualHost);
        producer.setExchange(exchange);
        producer.setMessageId(UUID.randomUUID().toString());
        producer.setBody("11111");
        boolean result = produceRecordManager.saveRecord(producer);
        Assert.assertTrue(result);
        return producer;
    }
}
