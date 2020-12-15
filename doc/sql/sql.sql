CREATE TABLE IF NOT EXISTS transactional_message (
    id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    exchange_name  VARCHAR(64) NOT NULL COMMENT '交换器',
    routing_key    VARCHAR(64) NOT NULL COMMENT '路由键',
    business_key   VARCHAR(64) NOT NULL COMMENT '业务标志',
    message_type   VARCHAR(16) NOT NULL COMMENT '消息类别',
    message_body   TEXT        NOT NULL COMMENT '消息内容',
    message_header TEXT        NOT NULL COMMENT '消息head',
    create_time    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_create_time(create_time),
    INDEX idx_business_key(business_key)
) COMMENT '事务消息' CHARSET 'utf8';