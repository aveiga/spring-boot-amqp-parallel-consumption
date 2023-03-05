package com.aveiga.parallelconsumption;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.config.SuperStream;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;;

@Configuration
public class RabbitMqSuperStreamConfiguration {
    @Bean
    public CachingConnectionFactory connectionFactorySuperStream() {
        return new CachingConnectionFactory("localhost");
    }

    @Bean
    public RabbitAdmin amqpAdmin1() {
        return new RabbitAdmin(connectionFactorySuperStream());
    }

    /*@Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public StreamListenerContainer streamListenerContainer(Environment env, String name) {
        StreamListenerContainer container = new StreamListenerContainer(env);
        container.superStream("com.aveiga.superStream.parallel", "Parallel Processing Application", 3); // concurrency = 3
        container.setupMessageListener(msg -> {
            System.out.println("Got Stream Message: " + msg.getBody().toString());
        });
        container.setConsumerCustomizer((id, builder) -> builder.offset(OffsetSpecification.last()));
        return container;
    }*/

    @Bean
    RabbitListenerContainerFactory<StreamListenerContainer> rabbitStreamListenerContainerFactory(Environment env) {
        return new StreamRabbitListenerContainerFactory(env);
    }

    @Bean
    public RabbitStreamTemplate streamTemplate(Environment env) {
        RabbitStreamTemplate template = new RabbitStreamTemplate(env, "com.aveiga.superStream.parallel");
        template.setProducerCustomizer((name, builder) -> builder.name("aveiga Parallel Consumer"));
        template.setSuperStreamRouting(message -> {
            // some logic to return a String for the client's hashing algorithm
            return message.getBody().toString();
        });
        return template;
    }

    @Bean
    public SuperStream superStream() {
        return new SuperStream("com.aveiga.superStream.parallel", 3);
    }
}
