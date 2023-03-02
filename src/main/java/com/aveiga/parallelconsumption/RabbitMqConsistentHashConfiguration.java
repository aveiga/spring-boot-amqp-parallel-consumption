package com.aveiga.parallelconsumption;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.propertyeditors.CustomMapEditor;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConsistentHashConfiguration {
    @Bean
    public CachingConnectionFactory connectionFactory() {
        return new CachingConnectionFactory("localhost");
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            CachingConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConsumerTagStrategy(q -> "Parallel Processing Application. Consuming from queue: " + q);
        return factory;
    }

    @Bean
    public RabbitAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public Queue myQueue1() {
        return new Queue("com.aveiga.consistent-hash.myqueue1");
    }

    @Bean
    public Queue myQueue2() {
        return new Queue("com.aveiga.consistent-hash.myqueue2");
    }

    @Bean
    public Queue myQueue3() {
        return new Queue("com.aveiga.consistent-hash.myqueue3");
    }

    @Bean
    public CustomExchange customExchange()  {
        CustomExchange customExchange = new CustomExchange("com.aveiga.consistent-hash.myexchange", "x-consistent-hash", true, false);
        customExchange.addArgument("hash-header", "hash-on");
        return customExchange;
    }

    @Bean
    public Binding binding1(Queue myQueue1, CustomExchange customExchange)    {
        return BindingBuilder.bind(myQueue1).to(customExchange).with("1").noargs();
    }

    @Bean
    public Binding binding2(Queue myQueue2, CustomExchange customExchange)    {
        return BindingBuilder.bind(myQueue2).to(customExchange).with("1").noargs();
    }

    @Bean
    public Binding binding3(Queue myQueue3, CustomExchange customExchange)    {
        return BindingBuilder.bind(myQueue3).to(customExchange).with("1").noargs();
    }
}
