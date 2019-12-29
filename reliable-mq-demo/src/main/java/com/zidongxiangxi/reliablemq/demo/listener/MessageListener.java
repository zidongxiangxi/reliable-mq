package com.zidongxiangxi.reliablemq.demo.listener;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消息监听测试类
 *
 * @author chenxudong
 * @date 2019/12/29
 */
@Component
public class MessageListener {
    @RabbitListener(queuesToDeclare = @Queue(value = "queue.testSuccessConsume", durable = "false", ignoreDeclarationExceptions = "true"))
    public void successConsume(String messageStr) {
        System.out.println(messageStr);
    }

    @RabbitListener(queuesToDeclare = @Queue(value = "queue.testFailConsume", durable = "false", ignoreDeclarationExceptions = "true"))
    public void failConsume(String messageStr) {
        System.out.println(messageStr);
        throw new RuntimeException("fail");
    }
}
