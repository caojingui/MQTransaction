package com.pea.mqtransaction.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author caojingui
 */
public class TxmqNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("rabbit", new RabbitMqManagerParser());
    }
}
