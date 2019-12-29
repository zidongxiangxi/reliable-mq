package com.zidongxiangxi.reliabelmq.api.alarm;

/**
 * 告警接口
 *
 * @author chenxudong
 * @date 2019/12/25
 */
public interface Alarm {
    /**
     * 发送mq失败告警
     *
     * @param cause 告警原因
     */
    void failWhenProduce(Throwable cause);

    /**
     * 发送告警
     *
     * @param message 告警消息
     * @param cause 告警原因
     */
    void failWhenProduce(String message, Throwable cause);

    /**
     * 消费失败告警
     *
     * @param cause 告警原因
     */
    void failWhenConsume(Throwable cause);

    /**
     * 消费失败告警
     *
     * @param message 告警消息
     * @param cause 告警原因
     */
    void failWhenConsume(String message, Throwable cause);
}
