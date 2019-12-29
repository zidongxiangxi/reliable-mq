package com.zidongxiangxi.reliabelmq.api.constant;

/**
 * mq发送相关的常量
 *
 * @author chenxudong
 * @date 2019/09/17
 */
public interface ProducerConstants {
    /**
     * 失败后延迟重试的时间，单位秒
     */
    int[] DELAY_SECONDS = new int[] {5, 10, 30, 60, 300};

    /**
     * 最大重试次数
     */
    int MAX_RETRY_TIMES = DELAY_SECONDS.length;
}
