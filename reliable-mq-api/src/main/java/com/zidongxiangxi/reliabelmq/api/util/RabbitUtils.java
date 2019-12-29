package com.zidongxiangxi.reliabelmq.api.util;

import com.alibaba.fastjson.JSON;
import com.zidongxiangxi.reliabelmq.api.constant.MessageHeaderConstants;
import com.zidongxiangxi.reliabelmq.api.entity.ConsumeFailRecord;
import com.zidongxiangxi.reliabelmq.api.entity.rabbit.RabbitCorrelationId;
import com.zidongxiangxi.reliabelmq.api.entity.RabbitProducer;
import com.zidongxiangxi.reliabelmq.api.entity.enums.MessageTypeEnum;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * rabbit工具类
 *
 * @author chenxudong
 * @date 2019/09/14
 */
public class RabbitUtils {
    private static final String UNKNOWN_QUEUE = "unknown",
        UNKNOWN_APPLICATION = "unknown",
        LACK_MESSAGE_ID_PRE = "unknown_";
    public static Message generateMessage(RabbitProducer producer) {
        return generateMessage(producer, null);
    }

    public static Message generateMessage(RabbitProducer producer, String previousMessageId) {
        byte[] body = producer.getBody().getBytes(StandardCharsets.UTF_8);
        MessageProperties messageProperties = new MessageProperties();
        // 消息id
        messageProperties.setMessageId(producer.getMessageId());
        // 消息内容类型
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        // 持久化
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        // 时间戳
        messageProperties.setTimestamp(new Date());
        // 应用名
        messageProperties.setHeader(MessageHeaderConstants.MESSAGE_APPLICATION_HEADER, producer.getApplication());
        // 消息类型
        messageProperties.setHeader(MessageHeaderConstants.MESSAGE_TYPE_HEADER,
            Objects.equals(MessageTypeEnum.SEQUENCE.getValue(), producer.getType()) ? MessageTypeEnum.SEQUENCE.getValue() : MessageTypeEnum.IMMEDIATE.getValue());
        // 顺序消息的上一个消息id
        if (!StringUtils.isEmpty(previousMessageId)) {
            messageProperties.setHeader(MessageHeaderConstants.MESSAGE_PREVIOUS_ID_HEADER, previousMessageId);
        }
        return new Message(body, messageProperties);
    }

    public static CorrelationData generateCorrelationData(RabbitProducer producer) {
        if (Objects.isNull(producer)
            || StringUtils.isEmpty(producer.getApplication())
            || StringUtils.isEmpty(producer.getMessageId())) {
            return null;
        }
        RabbitCorrelationId rabbitCorrelationId = new RabbitCorrelationId();
        rabbitCorrelationId.setApplication(producer.getApplication());
        rabbitCorrelationId.setMessageId(producer.getMessageId());
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(rabbitCorrelationId.toString());
        return correlationData;
    }

    public static ConsumeFailRecord generateConsumeFailRecord(Message message, Throwable cause) {
        if (Objects.isNull(message)) {
            return null;
        }
        String consumerQueue = getConsumerQueue(message);
        consumerQueue = StringUtils.isEmpty(consumerQueue) ? UNKNOWN_QUEUE : consumerQueue;
        String application = getApplication(message);
        application = StringUtils.isEmpty(application) ? UNKNOWN_APPLICATION : application;
        String messageId = getMessageId(message);
        messageId = StringUtils.isEmpty(messageId) ? LACK_MESSAGE_ID_PRE + UUID.randomUUID().toString() : messageId;
        String body = getBodyAsString(message);
        if (StringUtils.isEmpty(body)) {
            return null;
        }
        String headers = getHeadersAsJsonString(message);
        String errorStack = "";
        if (Objects.nonNull(cause) && Objects.nonNull(cause.getStackTrace())) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            PrintStream ps = new PrintStream(byteArrayOutputStream);
            cause.printStackTrace(ps);
            errorStack = byteArrayOutputStream.toString();
        }
        ConsumeFailRecord record = new ConsumeFailRecord();
        record.setQueue(consumerQueue);
        record.setApplication(application);
        record.setMessageId(messageId);
        record.setHeaders(headers);
        record.setBody(body);
        record.setErrorStack(errorStack);
        return record;
    }

    public static String getApplication(Message message) {
        if (Objects.isNull(message)
            || Objects.isNull(message.getMessageProperties())
            || CollectionUtils.isEmpty(message.getMessageProperties().getHeaders())
            || !message.getMessageProperties().getHeaders().containsKey(MessageHeaderConstants.MESSAGE_APPLICATION_HEADER)) {
            return null;
        }
        return message.getMessageProperties().getHeaders().get(MessageHeaderConstants.MESSAGE_APPLICATION_HEADER).toString();
    }

    public static String getMessageId(Message message) {
        if (Objects.isNull(message)
            || Objects.isNull(message.getMessageProperties())) {
            return null;
        }
        return message.getMessageProperties().getMessageId();
    }

    public static Integer getMessageType(Message message) {
        if (Objects.isNull(message)
            || Objects.isNull(message.getMessageProperties())
            || CollectionUtils.isEmpty(message.getMessageProperties().getHeaders())
            || !message.getMessageProperties().getHeaders().containsKey(MessageHeaderConstants.MESSAGE_TYPE_HEADER)) {
            return null;
        }
        Object messageType = message.getMessageProperties().getHeaders().get(MessageHeaderConstants.MESSAGE_TYPE_HEADER);
        if (!(messageType instanceof Number)) {
            return null;
        }
        return ((Number)messageType).intValue();
    }

    public static Date getTimestamp(Message message) {
        if (Objects.isNull(message)
            || Objects.isNull(message.getMessageProperties())) {
            return null;
        }
        return message.getMessageProperties().getTimestamp();
    }

    private static String getConsumerQueue(Message message) {
        if (Objects.isNull(message)
            || Objects.isNull(message.getMessageProperties())) {
            return null;
        }
        return message.getMessageProperties().getConsumerQueue();
    }

    private static String getBodyAsString(Message message) {
        if (Objects.isNull(message) || Objects.isNull(message.getBody())) {
            return null;
        }
        return new String(message.getBody(), StandardCharsets.UTF_8);
    }

    private static String getHeadersAsJsonString(Message message) {
        if (Objects.isNull(message)
            || Objects.isNull(message.getMessageProperties())
            || Objects.isNull(message.getMessageProperties().getHeaders())) {
            return null;
        }
        return JSON.toJSONString(message.getMessageProperties().getHeaders());
    }
}
