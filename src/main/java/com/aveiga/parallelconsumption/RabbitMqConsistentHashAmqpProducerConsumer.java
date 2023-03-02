package com.aveiga.parallelconsumption;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.AmqpTemplate;

import java.util.Random;

import static java.lang.Thread.sleep;


@Component
public class RabbitMqConsistentHashAmqpProducerConsumer {

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
