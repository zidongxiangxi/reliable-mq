package com.zidongxiangxi.reliablemq.demo.scheduler;

import com.zidongxiangxi.reliablemq.demo.BaseTest;
import com.zidongxiangxi.reliablemq.producer.scheduler.RabbitRetrySendJob;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * rabbitMq发送任务测试类
 *
 * @author chenxudong
 * @date 2019/12/29
 */
public class RabbitRetrySendJobTest extends BaseTest {
    @Autowired
    private RabbitRetrySendJob rabbitRetrySendJob;

    @Test
    public void testExecute() throws Exception {
        rabbitRetrySendJob.execute(null);
    }
}
