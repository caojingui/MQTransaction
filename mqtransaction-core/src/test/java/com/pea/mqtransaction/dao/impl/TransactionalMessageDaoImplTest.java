package com.pea.mqtransaction.dao.impl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.Date;
import java.util.List;

import com.pea.mqtransaction.dao.TransactionalMessageDO;
import com.pea.mqtransaction.message.TxMessageType;

public class TransactionalMessageDaoImplTest {

    private static TransactionalMessageDaoImpl transactionalMessageDao;

    @BeforeClass
    public static void beforeClass() {
        String url = "jdbc:mysql://localhost:3306/test?characterEncoding=UTF8";
        String username = "root";
        String password = "123456";
        DriverManagerDataSource dataSource = new DriverManagerDataSource(url, username, password);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        transactionalMessageDao = new TransactionalMessageDaoImpl(jdbcTemplate);
    }

    @AfterClass
    public static void afterClass() {
    }

    @Test
    public void insert() {
        TransactionalMessageDO messageDO = new TransactionalMessageDO();
        Date now = new Date();
        messageDO.setUpdateTime(now);
        messageDO.setCreateTime(now);
        messageDO.setExchangeName("exchange");
        messageDO.setRoutingKey("routingKey");
        messageDO.setBusinessKey("businessKey");
        messageDO.setMessageBody("body");
        messageDO.setMessageHeader("");
        messageDO.setMessageType(TxMessageType.RABBITMQ.name());

        Long id = transactionalMessageDao.insert(messageDO);
        System.out.println(id);
        System.out.println(messageDO.getId());
        //Assert.assertEquals(id, messageDO.getId());
    }

    @Test
    public void delete() {
        transactionalMessageDao.delete(18L);
    }

    @Test
    public void queryNeedResend() {
        List<TransactionalMessageDO> list = transactionalMessageDao.queryNeedResend(TxMessageType.RABBITMQ.name(), 0);
        System.out.println(list);
    }
}