package com.pea.mqtransaction.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

import com.pea.mqtransaction.TransactionalMessageManager;
import com.pea.mqtransaction.test.dao.entity.User;
import com.pea.mqtransaction.test.service.UserService;

/**
 * @author caojingui
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/application.xml");
        UserService userService = context.getBean(UserService.class);
        User userDTO = new User();
        userDTO.setUsername("abc");
        userDTO.setAge(20);
        userDTO.setSex(1);
        // 保存一条记录
        userService.register(userDTO);
        System.out.println("xxx");

        TransactionalMessageManager manager = context.getBean(TransactionalMessageManager.class);
        manager.sendFailedMessage();

        TimeUnit.SECONDS.sleep(3);

        context.close();
    }
}
