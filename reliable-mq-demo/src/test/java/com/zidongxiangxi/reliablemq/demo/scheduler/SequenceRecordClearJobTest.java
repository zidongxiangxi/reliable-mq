package com.zidongxiangxi.reliablemq.demo.scheduler;

import com.zidongxiangxi.reliablemq.demo.BaseTest;
import com.zidongxiangxi.reliablemq.producer.scheduler.SequenceRecordClearJob;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 顺序消息记录清理任务测试类
 *
 * @author chenxudong
 * @date 2019/12/24
 */
public class SequenceRecordClearJobTest extends BaseTest {
    @Autowired
    private SequenceRecordClearJob sequenceRecordClearJob;

    @Test
    public void testExecute() throws Exception {
        sequenceRecordClearJob.execute(null);
    }
}
