package com.zidongxiangxi.reliabelmq.api.producer;

/**
 * rabbitMq消息发送接口，支持发送顺序mq
 *
 * @author chenxudong
 * @date 2019/09/17
 */
public interface RabbitMqProduceClient {
    /**
     * 发送消息到exchange
     *
     * @param exchange 交换器
     * @param msgBody 消息体
     * @return 消息id
     */
    void sendToExchange(String exchange, Object msgBody);

    /**
     * 发送消息到queue
     *
     * @param queue 队列
     * @param msgBody 消息体
     * @return 消息id
     */
    void sendToQueue(String queue, Object msgBody);

    /**
     * 发送消息
     *
     * @param exchange 交换器
     * @param routingKey 路由key
     * @param msgBody 消息体
     * @return 消息id
     */
    void send(String exchange, String routingKey, Object msgBody);

    /**
     * 发送消息
     *
     * @param messageId 消息id
     * @param exchange 交换器
     * @param routingKey 路由key
     * @param msgBody 消息体
     * @return 消息id
     */
    void send(String messageId, String exchange, String routingKey, Object msgBody);

    /**
     * 发送消息到exchange
     *
     * @param groupName 消息分组，同个分组内的消息顺序消费
     * @param exchange 交换器
     * @param msgBody 消息体
     * @return 消息id
     */
    void sendToExchangeSequentially(String groupName, String exchange, Object msgBody);

    /**
     * 发送消息到queue
     *
     * @param groupName 消息分组，同个分组内的消息顺序消费
     * @param queue 队列
     * @param msgBody 消息体
     * @return 消息id
     */
    void sendToQueueSequentially(String groupName, String queue, Object msgBody);

    /**
     * 发送消息
     *
     * @param groupName 消息分组，同个分组内的消息顺序消费
     * @param exchange 交换器
     * @param routingKey 路由key
     * @param msgBody 消息体
     * @return 消息id
     */
    void sendSequentially(String groupName, String exchange, String routingKey, Object msgBody);

    /**
     * 发送消息
     *
     * @param messageId 消息id
     * @param groupName 消息分组，同个分组内的消息顺序消费
     * @param exchange 交换器
     * @param routingKey 路由key
     * @param msgBody 消息体
     * @return 消息id
     */
    void sendSequentially(String messageId, String groupName, String exchange, String routingKey, Object msgBody);
}
