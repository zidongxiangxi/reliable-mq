package com.zidongxiangxi.reliablemq.consumer.sheduler;

import com.zidongxiangxi.reliabelmq.api.manager.ConsumeRecordManager;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 消费记录清理任务
 *
 * @author chenxudong
 * @date 2019/12/24
 */
@Slf4j
@JobHandler(value = "consumeRecordClearJobHandler")
public class ConsumeRecordClearJob extends IJobHandler {
    private static final int MAX_BATCH_SIZE = 100;
    private ConsumeRecordManager consumeRecordManager;
    private int retentionPeriod;
    private int batchSize;
    private int loopCount = 1;

    public ConsumeRecordClearJob(ConsumeRecordManager consumeRecordManager, int retentionPeriod, int batchSize) {
        this.consumeRecordManager = consumeRecordManager;
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
        CompletableFuture.runAsync(() -> {
            Date beforeTime = new Date(System.currentTimeMillis() - retentionPeriod * 86400 * 1000);
            int i = 0;
            while (i < loopCount) {
                int size = clearRecord(beforeTime, batchSize);
                if (size < 1) {
                    break;
                }
                i++;
            }
        });
        return SUCCESS;
    }

    private int clearRecord(Date beforeTime, int batchSize) {
        List<Long> ids = consumeRecordManager.listPrimaryKey(beforeTime, batchSize);
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        consumeRecordManager.deleteConsumeRecord(ids);
        return ids.size();
    }
}
