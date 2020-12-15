package com.pea.mqtransaction.test.service;

import com.pea.mqtransaction.test.dao.entity.User;

/**
 * @author caojingui
 */
public interface UserService {
    /**
     * 注册
     *
     * @param user 用户
     * @return true：成功
     */
    boolean register(User user);

    /**
     * 注册
     *
     * @param user     用户
     * @param password 密码
     * @return true：成功
     */
    boolean register(User user, String password);
}
