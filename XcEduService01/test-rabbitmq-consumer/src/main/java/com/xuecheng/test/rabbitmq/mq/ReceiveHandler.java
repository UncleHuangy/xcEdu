package com.xuecheng.test.rabbitmq.mq;


import com.rabbitmq.client.Channel;
import com.xuecheng.test.rabbitmq.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
public class ReceiveHandler {

    @RabbitListener(queues = {RabbitConfig.QUEUE_INFORM_EMAIL})
    public void receive(String msg, Message message, Channel channel){
        System.out.println("receive message is:"+msg);
    }

}
