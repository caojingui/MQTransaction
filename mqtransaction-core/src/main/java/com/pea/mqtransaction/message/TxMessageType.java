package com.pea.mqtransaction.message;

/**
 * @author caojingui
 */

public enum TxMessageType {

    RABBITMQ;

    public static TxMessageType value(String messageType) {
        for (TxMessageType value : TxMessageType.values()) {
            if (value.name().equalsIgnoreCase(messageType)) {
                return value;
            }
        }
        return null;
    }

}
