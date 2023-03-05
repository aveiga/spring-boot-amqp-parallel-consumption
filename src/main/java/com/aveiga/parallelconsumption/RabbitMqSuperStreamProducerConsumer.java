package com.aveiga.parallelconsumption;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;

import static java.lang.Thread.sleep;

@Component
public class RabbitMqSuperStreamProducerConsumer {

    @Bean
    public ApplicationRunner streamRunner(RabbitStreamTemplate template) {
        Random r = new Random();
        int low = 1;
        int high = 11;

        return args -> {
            while (true) {
                int hashId = r.nextInt(high-low) + low;

                template.convertAndSend("foo " + hashId);
                sleep(500);
            }
        };
    }

    @RabbitListener(queues = "${QUEUE}", id = "Parallel Processor")
    void listen(String in) {
        System.out.println("Got stream message: " + in);
    }
}
