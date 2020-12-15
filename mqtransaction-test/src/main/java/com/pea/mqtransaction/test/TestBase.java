package com.pea.mqtransaction.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author caojingui
 */
@Configuration
@ComponentScan("com.pea.mqtransaction.test.service")
@ImportResource("classpath:application.xml")
public class TestBase {

}
