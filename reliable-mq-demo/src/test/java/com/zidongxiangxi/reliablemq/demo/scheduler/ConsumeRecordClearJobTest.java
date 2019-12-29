package com.zidongxiangxi.reliablemq.demo.scheduler;

import com.zidongxiangxi.reliablemq.consumer.sheduler.ConsumeRecordClearJob;
import com.zidongxiangxi.reliablemq.demo.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 消费记录清理任务测试类
 *
 * @author chenxudong
 * @date 2019/12/29
 */
public class ConsumeRecordClearJobTest extends BaseTest {
    @Autowired
    private ConsumeRecordClearJob consumeRecordClearJob;

    @Test
    public void testExecute() throws Exception {
        consumeRecordClearJob.execute(null);
    }
}
