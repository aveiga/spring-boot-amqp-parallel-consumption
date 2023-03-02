package com.aveiga.parallelconsumption;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Random;

import static java.lang.Thread.sleep;


@Component
public class AmqpConsumer {

    @Bean
    public ApplicationRunner runner(AmqpTemplate template) {
        Random r = new Random();
        int low = 1;
        int high = 11;

        return args -> {
            while (true) {
                int hashId = r.nextInt(high-low) + low;

                template.convertAndSend("com.aveiga.consistent-hash.myexchange", "" + hashId, "foo " + hashId, message -> {
                    message.getMessageProperties().getHeaders().put("hash-on", hashId);
                    return message;
                });
                sleep(2000);
            }
        };
    }

    @RabbitListener(queues = "${QUEUE}")
    public void listen1(String in) {
        System.out.println("Got message: " + in);
    }

//    @RabbitListener(queues = "com.aveiga.consistent-hash.myqueue2")
//    public void listen2(String in) {
//        System.out.println("Got message: " + in);
//    }
//
//    @RabbitListener(queues = "com.aveiga.consistent-hash.myqueue" + instance)
//    public void listen3(String in) {
//        System.out.println("Got message: " + in);
//    }
}
