## 基于消息中间件可复用的事务消息组件

> 为了降低代码的入侵性，事务消息需要借助Spring的编程式事务或者声明式事务。编程式事务一般依赖于TransactionTemplate，而声明式事务依托于AOP模块，依赖于注解@Transactional。

## 使用方法

* jar包引用

```
<dependency>
   <groupId>com.pea.mqtransaction</groupId>
   <artifactId>mqtransaction-core</artifactId>
   <version>1.0.0-SNAPSHOT</version>
</dependency>
```

* 配置

```
// namespace xmlns:txmq="http://www.springframework.org/schema/txmq"

<txmq:rabbit id="transactionMq" datasource="dataSource" connection-factory="rabbitmqConnectionFactory"/>

// publisher-confirms="true" 开启发送确认机制
<rabbit:connection-factory id="rabbitmqConnectionFactory" addresses="127.0.0.1:5672"
                           username="test" password="test" publisher-confirms="true"
                           virtual-host="/" channel-cache-size="5"/>
```

* 表初始化

```
DROP TABLE IF EXISTS transactional_message;
CREATE TABLE transactional_message (
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
```

3. 使用示例
> 在spring事物代码内部构件消息对象，调用TransactionalMessageTemplate.sendTransactionMessage方法

```
@Resource(name = "transactionMq")
private TransactionalMessageManager transactionalMessageManager;

@Override
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
public boolean register(User user) {
    TransactionalMessage message = getMessage(user);
    transactionalMessageManager.sendTransactionMessage(message);

    user.setStatus(1);
    user.setCreateTm(new Date());

    int ret = userMapper.insert(user);
    LOGGER.info("insert result ={}, id={}", ret, user.getId());

    // 事务测试，加上这句代码则数据不会保存到数据库中
    if (ret == Integer.SIZE) {
        throw new RuntimeException("insert error");
    }

    return ret == 1;
}

@Override
public boolean register(final User user, final String password) {
    return transactionTemplate.execute(new TransactionCallback<Boolean>() {
        @Override
        public Boolean doInTransaction(TransactionStatus status) {
            user.setStatus(1);
            user.setPassword(password);
            user.setCreateTm(new Date());
            int ret = userMapper.insert(user);
            LOGGER.info("insert result ={}, id={}", ret, user.getId());
            TransactionalMessage message = getMessage(user);
            transactionalMessageManager.sendTransactionMessage(message);
            return true;
        }
    });
}
```

### 失败消息补偿

需要自行调用或者定时任务调用

```
transactionalMessageManager.sendFailedMessage();
```

### 实现原理
在spring事物提交的同时基于本地事务将消息写入db，事务提交之后异步发送消息到消息中间件，消息中间件确认消息发送回执将消息从db中删除。发送失败基于任务调度补偿。保证消息能发送成功。

1. 数据库本地事务
2. rabbitmq发送确认
3. spring编程式事务&声明式事务

### feature
1. 基于指数退避算法补偿发送失败的消息
2. 其他消息中间件支持