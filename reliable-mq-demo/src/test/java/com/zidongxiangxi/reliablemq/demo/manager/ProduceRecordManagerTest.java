package com.zidongxiangxi.reliablemq.demo.manager;

import com.alibaba.fastjson.JSON;
import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.entity.enums.MessageTypeEnum;
import com.zidongxiangxi.reliabelmq.api.manager.ProduceRecordManager;
import com.zidongxiangxi.reliabelmq.api.manager.ProduceSequenceRecordManager;
import com.zidongxiangxi.reliablemq.demo.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.UUID;

/**
 * 发送manager测试类
 *
 * @author chenxudong
 * @date 2019/12/23
 */
public class ProduceRecordManagerTest extends BaseTest {
    @Value("${reliable-mq.application}")
    private String application;
    private String virtualHost = "/";
    private String exchange = "test.exchange.fanout";

    @Autowired
    private ProduceRecordManager<RabbitProducer> produceRecordManager;
    @Autowired
    private ProduceSequenceRecordManager sequenceRecordManager;

    @Test
    public void testSaveImmediateMqProducer() {
        RabbitProducer producer = saveImmediateProducer();
        Assert.assertNotNull(producer);
    }

    @Test
    public void testSaveSequenceMqProducer() {
        String groupName = UUID.randomUUID().toString();
        RabbitProducer producer1 = saveSequenceProducer(groupName);
        Assert.assertNotNull(producer1);
        RabbitProducer producer2 = saveSequenceProducer(groupName);
        Assert.assertNotNull(producer2);
        String previousMessageId = sequenceRecordManager.getPreviousMessageId(producer2.getApplication(),
            producer2.getMessageId());
        Assert.assertNotNull(previousMessageId);
        Assert.assertEquals(producer1.getMessageId(), previousMessageId);
    }

    @Test
    public void testFailSendMq() {
        RabbitProducer producer = saveImmediateProducer();
        boolean result = produceRecordManager.failToSend(producer.getApplication(), producer.getMessageId());
        Assert.assertTrue(result);
    }

    @Test
    public void testDeleteMq() {
        RabbitProducer producer = saveImmediateProducer();
        boolean result = produceRecordManager.deleteRecord(producer.getApplication(), producer.getMessageId());
        Assert.assertTrue(result);
    }

    @Test
    public void testListSendingMq() throws InterruptedException {
        RabbitProducer producer = saveImmediateProducer();
        Thread.sleep(25000);
        List<RabbitProducer> producers = produceRecordManager.listSendingRecord(producer.getApplication(), 0, 1);
        Assert.assertTrue(producers.size() > 0);
        System.out.println(JSON.toJSONString(producers));
    }

    private RabbitProducer saveImmediateProducer() {
        RabbitProducer producer = new RabbitProducer();
        producer.setApplication(application);
        producer.setType(MessageTypeEnum.IMMEDIATE.getValue());
        producer.setVirtualHost(virtualHost);
        producer.setExchange(exchange);
        producer.setMessageId(UUID.randomUUID().toString());
        producer.setBody("11111");
        boolean result = produceRecordManager.saveRecord(producer);
        Assert.assertTrue(result);
        return producer;
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
