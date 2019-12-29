CREATE TABLE `rabbit_producer_record` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `type` TINYINT(3) NOT NULL DEFAULT 0 COMMENT '消息类型，0=即时消息，1=顺序消息',
  `application` VARCHAR(50) NOT NULL COMMENT '应用名称',
  `virtual_host` VARCHAR(255) NOT NULL COMMENT '虚拟主机',
  `exchange` VARCHAR(255) NULL COMMENT '交换器',
  `routing_key` VARCHAR(255) NULL COMMENT '路由key',
  `message_id` VARCHAR(50) NOT NULL COMMENT '消息id',
  `body` TEXT NOT NULL COMMENT '消息内容',
  `group_name` VARCHAR(50) DEFAULT NULL COMMENT '消息分组，同分组内，消息序号按发送顺序递增',
  `send_status` TINYINT(3) NOT NULL DEFAULT 0 COMMENT '发送状态， 0=预提交，1=发送中，2=发送是啊比',
  `retry_times` SMALLINT(6) NOT NULL DEFAULT 0 COMMENT '重试次数',
  `max_retry_times` SMALLINT(6) NOT NULL COMMENT '最大重试次数',
  `next_retry_time` DATETIME DEFAULT NULL COMMENT '下一次重试时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_message_app` (`message_id`, `application`)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COMMENT = 'rabbitMq发送表';

CREATE TABLE `producer_sequence_record` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `message_id` VARCHAR(50) NOT NULL COMMENT '消息id',
  `application` VARCHAR(50) NOT NULL COMMENT '应用名称',
  `group_name` VARCHAR(50) NOT NULL COMMENT '消息分组，同分组内，消息序号按发送顺序递增',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_message_app` (`message_id`, `application`),
  KEY `idx_app_group` (`application`, `group_name`),
  KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COMMENT = '顺序mq的记录表';