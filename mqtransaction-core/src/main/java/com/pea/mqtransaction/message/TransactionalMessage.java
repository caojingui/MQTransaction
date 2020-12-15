package com.pea.mqtransaction.message;

import java.io.Serializable;
import java.util.Map;

/**
 * @author caojingui
 */
public class TransactionalMessage implements Serializable {
    private String exchangeName;
    private String routingKey;
    private String businessKey;
    private String messageBody;
    private Map<String, String> messageHeader;

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

    public Map<String, String> getMessageHeader() {
        return messageHeader;
    }

    public void setMessageHeader(Map<String, String> messageHeader) {
        this.messageHeader = messageHeader;
    }

    @Override
    public String toString() {
        return "TransactionalMessage{" +
                "exchangeName='" + exchangeName + '\'' +
                ", routingKey='" + routingKey + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", messageBody='" + messageBody + '\'' +
                ", messageHeader=" + messageHeader +
                '}';
    }
}
