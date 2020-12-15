package com.pea.mqtransaction;

import com.pea.mqtransaction.message.TransactionalMessage;
import com.pea.mqtransaction.message.TxMessageType;

/**
 * @author caojingui
 */
public interface TransactionalMessageManager {

    /**
     * 发送事务消息
     *
     * @param message 消息内容
     * @return 消息ID
     */
    Long sendTransactionMessage(TransactionalMessage message);

    /**
     * 补偿发送失败消息
     */
    void sendFailedMessage();

    /**
     * 支持的mq类型
     *
     * @return 类别
     */
    TxMessageType managerType();

}
