package com.pea.mqtransaction.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.pea.mqtransaction.util.StringUtil;
import com.pea.mqtransaction.dao.TransactionalMessageDO;
import com.pea.mqtransaction.message.TxMessageType;

/**
 * rabbitmq 事务消息
 *
 * @author caojingui
 */
public class RabbitMqTransactionalMessageManager extends AbstractTransactionalMessageManager implements ConfirmCallback {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String MESSAGE_ID_PREFIX = "TXMQ_" + TxMessageType.RABBITMQ.name() + "_";

    private final ConnectionFactory connectionFactory;

    private RabbitTemplate rabbitTemplate;

    public RabbitMqTransactionalMessageManager(DataSource dataSource,
                                               ConnectionFactory connectionFactory) {
        super(dataSource);
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void sendFailedMessage() {
        List<TransactionalMessageDO> list = transactionalMessageDao.queryNeedResend(TxMessageType.RABBITMQ.name(), 3 * 60 * 1000);
        for (TransactionalMessageDO messageDO : list) {
            try {
                if (transactionalMessageDao.getById(messageDO.getId()) != null) {
                    sendMessage(messageDO);
                }
            } catch (Exception e) {
                logger.error("send rabbitTransactionalMq error message={}", messageDO.getId(), e);
            }
        }
    }

    @Override
    public TxMessageType managerType() {
        return TxMessageType.RABBITMQ;
    }

    @Override
    public void sendMessage(final TransactionalMessageDO message) {
        logger.info("send rabbitTransactionMq [{}]", message);
        final String id = MESSAGE_ID_PREFIX + message.getId();
        rabbitTemplate.convertAndSend(message.getExchangeName(), message.getRoutingKey(), message.getMessageBody(),
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message msg) throws AmqpException {
                        msg.getMessageProperties().setCorrelationIdString(id);
                        Map<String, String> map = StringUtil.stringToMap(message.getMessageHeader());
                        for (String key : map.keySet()) {
                            msg.getMessageProperties().setHeader(key, map.get(key));
                        }
                        return msg;
                    }
                }, new CorrelationData(id));

    }

    @Override
    public void init() {
        this.rabbitTemplate = new RabbitTemplate(connectionFactory);
        this.rabbitTemplate.setConfirmCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            String messageId = correlationData.getId();
            logger.info("rabbitTransactionalMq send success id={}", correlationData.getId());
            if (messageId.startsWith(MESSAGE_ID_PREFIX)) {
                transactionalMessageDao.delete(Long.valueOf(messageId.replace(MESSAGE_ID_PREFIX, "")));
            }
        } else {
            logger.error("rabbitTransactionalMq send failed id={}, cause={}", correlationData.getId(), cause);
        }
    }
}
