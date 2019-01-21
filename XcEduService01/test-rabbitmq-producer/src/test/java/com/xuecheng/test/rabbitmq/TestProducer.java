package com.xuecheng.test.rabbitmq;


import com.xuecheng.test.rabbitmq.config.RabbitConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testConsumer(){

        for (int i = 0; i < 5; i++) {

            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_TOPICS_INFORM,"inform.sms.email","sadfasdfasdfasd");

        }


    }


}
