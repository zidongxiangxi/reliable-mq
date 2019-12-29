package com.zidongxiangxi.reliabelmq.api.alarm;

import org.springframework.util.StringUtils;

import java.util.concurrent.CompletableFuture;

/**
 * 基础的告警类
 *
 * @author chenxudong
 * @date 2019/12/25
 */
public abstract class AbstractAlarm implements Alarm {
    private static final String FAIL_TO_PRODUCE_MSG = "消息发送出现异常：";
    private static final String FAIL_TO_CONSUME_MSG = "消息处理出现异常：";

    @Override
    public void failWhenProduce(Throwable cause) {
        failWhenProduce("", cause);
    }

    @Override
    public void failWhenProduce(String message, Throwable cause) {
        CompletableFuture.runAsync(() -> sendAlarmMessage(getFailToProduceMsg(message), cause));
    }

        @Override
    public void failWhenConsume(Throwable cause) {
        failWhenConsume("", cause);
    }

    @Override
    public void failWhenConsume(String message, Throwable cause) {
        CompletableFuture.runAsync(() -> sendAlarmMessage(getFailToConsumeMsg(message), cause));
    }

    protected String getFailToProduceMsg(String message) {
        if (StringUtils.isEmpty(message)) {
            return FAIL_TO_PRODUCE_MSG;
        }
        return FAIL_TO_PRODUCE_MSG + "\n" + message;
    }

    protected String getFailToConsumeMsg(String message) {
        if (StringUtils.isEmpty(message)) {
            return FAIL_TO_CONSUME_MSG;
        }
        return FAIL_TO_CONSUME_MSG + "\n" + message;
    }

    protected abstract void sendAlarmMessage(String message, Throwable cause);
}
