package com.pea.mqtransaction.test.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pea.mqtransaction.test.dao.entity.User;
import com.pea.mqtransaction.TransactionalMessageManager;
import com.pea.mqtransaction.message.TransactionalMessage;
import com.pea.mqtransaction.test.dao.mapper.UserMapper;
import com.pea.mqtransaction.test.service.UserService;

/**
 * @author caojingui
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;

    private final TransactionTemplate transactionTemplate;

    @Resource(name = "transactionMq")
    private TransactionalMessageManager transactionalMessageManager;

    public UserServiceImpl(UserMapper userMapper, TransactionTemplate transactionTemplate) {
        this.userMapper = userMapper;
        this.transactionTemplate = transactionTemplate;
    }

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

    private TransactionalMessage getMessage(User user) {
        TransactionalMessage message = new TransactionalMessage();
        message.setBusinessKey("test");
        message.setExchangeName("");
        message.setRoutingKey("test.caojingui");
        ObjectMapper objectMapper = new ObjectMapper();
        String body = null;
        try {
            body = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        message.setMessageBody(body);
        message.setMessageHeader(null);
        System.out.println(body);
        return message;
    }

}
