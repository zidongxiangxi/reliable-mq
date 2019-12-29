package com.zidongxiangxi.reliabelmq.api.exception;

/**
 * 可靠mq异常
 *
 * @author chenxudong
 * @date 2019/08/30
 */
public class ReliableMqException extends RuntimeException {
    public ReliableMqException(String message) {
        super(message);
    }
}
