package com.pea.mqtransaction.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import com.pea.mqtransaction.dao.TransactionalMessageDO;
import com.pea.mqtransaction.dao.TransactionalMessageDao;
import com.pea.mqtransaction.dao.impl.TransactionalMessageDaoImpl;
import com.pea.mqtransaction.exception.TxMessageException;
import com.pea.mqtransaction.util.StringUtil;
import com.pea.mqtransaction.TransactionalMessageManager;
import com.pea.mqtransaction.message.TransactionalMessage;
import com.pea.mqtransaction.message.TxMessageType;

/**
 * @author caojingui
 */
public abstract class AbstractTransactionalMessageManager implements TransactionalMessageManager, InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final TransactionalMessageDao transactionalMessageDao;

    private static final ExecutorService executorService = new ThreadPoolExecutor(5, 200,
            30L, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(200), Executors.defaultThreadFactory(), new CallerRunsPolicy());

    public AbstractTransactionalMessageManager(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        this.transactionalMessageDao = new TransactionalMessageDaoImpl(jdbcTemplate);
    }

    /**
     * 发送事务消息
     *
     * @param message 消息内容
     * @return 消息ID
     */
    @Override
    public Long sendTransactionMessage(final TransactionalMessage message) {
        logger.debug("send transaction message {}", message);
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new TxMessageException("transaction is not active, can not send transaction message");
        }
        Date now = new Date();
        final TransactionalMessageDO messageDO = new TransactionalMessageDO();
        messageDO.setUpdateTime(now);
        messageDO.setCreateTime(now);
        messageDO.setExchangeName(message.getExchangeName());
        messageDO.setRoutingKey(message.getRoutingKey());
        messageDO.setBusinessKey(message.getBusinessKey());
        messageDO.setMessageBody(message.getMessageBody());
        messageDO.setMessageHeader(StringUtil.mapToString(message.getMessageHeader()));
        messageDO.setMessageType(TxMessageType.RABBITMQ.name());

        final long id = transactionalMessageDao.insert(messageDO);
        messageDO.setId(id);
        logger.info("send transaction message id={}", id);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {

            @Override
            public void afterCommit() {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendMessage(messageDO);
                        } catch (Exception e) {
                            logger.error("send TransactionalMessage error for [{}]", message, e);
                        }
                    }
                });
            }
        });
        return id;
    }

    /**
     * 发送非事务消息
     *
     * @param message 消息内容
     */
    public abstract void sendMessage(TransactionalMessageDO message);

    /**
     * 子类初始化
     */
    protected void init() {}

    /**
     * 之类销毁
     */
    protected void close() {}

    @Override
    public void destroy() throws Exception {
        logger.info("shutdown transaction message executor");
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);
        close();
    }

    @Override
    public void afterPropertiesSet() {
        init();
    }

}
