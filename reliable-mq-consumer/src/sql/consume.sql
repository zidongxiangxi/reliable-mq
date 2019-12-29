CREATE TABLE `consume_record` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `message_id` VARCHAR(50) NOT NULL COMMENT '消息id',
  `application` VARCHAR(50) NOT NULL COMMENT '应用名称',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_message_app` (`message_id`, `application`),
  KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COMMENT = '消息消费记录';

CREATE TABLE `consume_fail_record` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `queue` VARCHAR(100) NOT NULL COMMENT '队列名称',
  `application` VARCHAR(100) NOT NULL COMMENT '应用名称',
  `message_id` VARCHAR(50) NOT NULL COMMENT '消息id',
  `headers` TEXT DEFAULT NULL COMMENT '消息头',
  `body` TEXT NOT NULL COMMENT '消息内容',
  `error_stack` TEXT NOT NULL COMMENT '失败原因的错误堆栈',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_message_app` (`message_id`, `application`)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COMMENT = '消息消费失败记录';