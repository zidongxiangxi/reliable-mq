package com.zidongxiangxi.reliablemq.producer.scheduler;

import com.zidongxiangxi.reliabelmq.api.entity.SequenceMessage;
import com.zidongxiangxi.reliabelmq.api.manager.SequenceManager;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 顺序消息记录清理任务
 *
 * @author chenxudong
 * @date 2019/12/24
 */
@Slf4j
@JobHandler(value = "sequenceClearJobHandler")
public class SequenceClearJob extends IJobHandler {
    private static final int MAX_BATCH_SIZE = 100;
    private SequenceManager manager;
    private int retentionPeriod;
    private int batchSize;
    private int loopCount = 1;

    public SequenceClearJob(SequenceManager manager, int retentionPeriod, int batchSize) {
        this.manager = manager;
        this.retentionPeriod = retentionPeriod;
        if (batchSize > MAX_BATCH_SIZE) {
            this.loopCount = batchSize / MAX_BATCH_SIZE + (batchSize % MAX_BATCH_SIZE > 0 ? 1 : 0);
            this.batchSize = MAX_BATCH_SIZE;
        } else {
            this.batchSize = batchSize;
        }
    }
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Date beforeTime = new Date(System.currentTimeMillis() - retentionPeriod * 86400 * 1000);
        int i = 0;
        while (i < loopCount) {
            int size = clearRecord(beforeTime, batchSize);
            if (size < 1) {
                break;
            }
            i++;
        }
        return SUCCESS;
    }

    private int clearRecord(Date beforeTime, int batchSize) {
        List<SequenceMessage> sequenceMessages = manager.list(beforeTime, batchSize);
        if (CollectionUtils.isEmpty(sequenceMessages)) {
            return 0;
        }
        List<Long> ids = sequenceMessages.stream().map(SequenceMessage::getId).collect(Collectors.toList());
        manager.deleteByIds(ids);
        return ids.size();
    }
}
