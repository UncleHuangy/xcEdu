package com.xuecheng.manage_cms.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 由于不确定发送的站点ID 所以不配置队列信息  因为队列名称为PageID key为siteID
 */
@Configuration
public class RabbitmqConfig {


    //交换机的名称
    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";

    /**
     * 声明交换机
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EX_ROUTING_CMS_POSTPAGE(){
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

}
