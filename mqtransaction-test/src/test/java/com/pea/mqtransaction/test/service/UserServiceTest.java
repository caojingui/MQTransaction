package com.pea.mqtransaction.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import com.pea.mqtransaction.test.dao.entity.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/application.xml")
public class UserServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceTest.class);

    @Resource
    private UserService userService;

    @Test
    public void register() {
        User userDTO = new User();
        userDTO.setUsername("张三");
        userDTO.setAge(20);
        userDTO.setSex(1);
        boolean result = userService.register(userDTO);
        LOGGER.info("result={}", result);
    }

    @Test
    public void register1() {
        User userDTO = new User();
        userDTO.setUsername("张三");
        userDTO.setAge(20);
        userDTO.setSex(1);
        boolean result = userService.register(userDTO, "123");
        LOGGER.info("result={}", result);
    }
}