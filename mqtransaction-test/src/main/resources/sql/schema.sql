CREATE TABLE transactional_message (
    id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    exchange_name  VARCHAR(64) NOT NULL COMMENT '交换器',
    routing_key    VARCHAR(64) NOT NULL COMMENT '路由键',
    business_key   VARCHAR(64) NOT NULL COMMENT '业务标志',
    message_type   VARCHAR(16) NOT NULL COMMENT '消息类别',
    message_body   TEXT        NOT NULL COMMENT '消息内容',
    message_header TEXT        NOT NULL COMMENT '消息head',
    create_time    TIMESTAMP   NOT NULL ,
    update_time    TIMESTAMP   NOT NULL
) ;
CREATE TABLE user (
    id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(64) NOT NULL,
    password    VARCHAR(64) default NULL,
    sex   int NOT NULL ,
    age   int NOT NULL ,
    status   int        NOT NULL,
    create_tm    TIMESTAMP   NOT NULL
);


INSERT INTO transactional_message(exchange_name, routing_key, business_key, message_type, message_body, message_header,  create_time,  update_time)
VALUES ('ex','r','bk','type','xx','xx',CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP())