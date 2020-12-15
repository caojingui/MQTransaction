package com.pea.mqtransaction.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import com.pea.mqtransaction.manager.RabbitMqTransactionalMessageManager;

/**
 * @author caojingui
 */
public class RabbitMqManagerParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return RabbitMqTransactionalMessageManager.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        builder.addConstructorArgReference(element.getAttribute("datasource"));
        builder.addConstructorArgReference(element.getAttribute("connection-factory"));
    }
}
