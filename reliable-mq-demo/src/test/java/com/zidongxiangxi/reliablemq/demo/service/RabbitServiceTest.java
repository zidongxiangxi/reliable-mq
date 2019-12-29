package com.zidongxiangxi.reliablemq.demo.service;

import com.zidongxiangxi.reliablemq.demo.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 顺序消息发送测试类
 *
 * @author chenxudong
 * @date 2019/12/23
 */
public class RabbitServiceTest extends BaseTest {
    @Autowired
    private RabbitService service;

    @Test
    public void testCommitNormal() throws InterruptedException {
        service.commitNormal();
        Thread.sleep(3000L);
    }

    @Test
    public void testRollbackNormal() {
        try {
            service.rollbackNormal();
        } catch (RuntimeException e) {}
    }

    @Test
    public void testCommitSequence() throws InterruptedException {
        service.commitSequence();
        Thread.sleep(3000L);
    }

    @Test
    public void testRollbackSequence() {
        try {
            service.rollbackSequence();
        } catch (RuntimeException e) {}
    }
}
