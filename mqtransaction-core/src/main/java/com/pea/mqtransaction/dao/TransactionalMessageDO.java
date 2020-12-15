package com.pea.mqtransaction.dao;

import java.io.Serializable;
import java.util.Date;

/**
 * @author caojingui
 */
public class TransactionalMessageDO implements Serializable {
    private Long id;
    private Date createTime;
    private Date updateTime;
    private String exchangeName;
    private String routingKey;
    private String businessKey;
    private String messageBody;
    private String messageHeader;
    private String messageType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getMessageHeader() {
        return messageHeader;
    }

    public void setMessageHeader(String messageHeader) {
        this.messageHeader = messageHeader;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "TransactionalMessageDO{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", exchangeName='" + exchangeName + '\'' +
                ", routingKey='" + routingKey + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", messageBody='" + messageBody + '\'' +
                ", messageHeader='" + messageHeader + '\'' +
                ", messageType='" + messageType + '\'' +
                '}';
    }
}
