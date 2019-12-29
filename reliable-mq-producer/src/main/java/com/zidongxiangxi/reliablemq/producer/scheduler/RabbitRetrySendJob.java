package com.zidongxiangxi.reliablemq.producer.scheduler;

import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.manager.ProduceRecordManager;
import com.zidongxiangxi.reliabelmq.api.producer.RabbitProducerService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * rabbitMq发送任务
 *
 * @author chenxudong
 * @date 2019/09/12
 */
@Slf4j
@JobHandler(value = "rabbitRetrySendJobHandler")
public class RabbitRetrySendJob extends IJobHandler {
    private static final int MAX_BATCH_SIZE = 100;
    private ProduceRecordManager<RabbitProducer> producerManager;
    private RabbitProducerService rabbitService;
    private String application;
    private int batchSize;
    private int loopCount = 1;

    public RabbitRetrySendJob(
            ProduceRecordManager<RabbitProducer> producerManager,
            RabbitProducerService rabbitService,
            String application,
            int batchSize) {
        this.producerManager = producerManager;
        this.rabbitService = rabbitService;
        this.application = application;
        if (batchSize > MAX_BATCH_SIZE) {
            this.loopCount = batchSize / MAX_BATCH_SIZE + (batchSize % MAX_BATCH_SIZE > 0 ? 1 : 0);
            this.batchSize = MAX_BATCH_SIZE;
        } else {
            this.batchSize = batchSize;
        }
    }

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        int i = 0;
        while (i < loopCount) {
            int size = clearRecord(batchSize);
            if (size < 1) {
                break;
            }
            i++;
        }
        return SUCCESS;
    }

    private int clearRecord(int batchSize) {
        List<RabbitProducer> list = producerManager.listSendingRecord(application, 0, batchSize);;
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        for (RabbitProducer producer : list) {
            try {
                producerManager.failToSend(application, producer.getMessageId());
                rabbitService.send(producer);
            } catch (Throwable ignore) {}
        }
        return list.size();
    }
}
