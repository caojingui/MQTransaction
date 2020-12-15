package com.pea.mqtransaction.dao;

import java.util.List;

/**
 * @author caojingui
 */
public interface TransactionalMessageDao {

    /**
     * 消息持久化
     *
     * @param message 消息对象
     * @return 消息ID
     */
    long insert(TransactionalMessageDO message);

    /**
     * 消息删除
     *
     * @param messageId 消息ID
     * @return 删除记录数
     */
    int delete(Long messageId);

    /**
     * 查询失败需要重新发送的消息
     *
     * @param messageType 消息类型
     * @param time 时间(毫秒)
     * @return 消息列表
     */
    List<TransactionalMessageDO> queryNeedResend(String messageType, long time);

    /**
     * 查询消息
     *
     * @param id 消息ID
     * @return 消息列表
     */
    TransactionalMessageDO getById(long id);
}
