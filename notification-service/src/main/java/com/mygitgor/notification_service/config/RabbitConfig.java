package com.mygitgor.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EMAIL_QUEUE = "email-notification-queue";
    public static final String EXCHANGE = "notification-exchange";
    public static final String ROUTING_KEY = "email.notification";

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue emailQueue, DirectExchange exchange) {
        return BindingBuilder.bind(emailQueue).to(exchange).with(ROUTING_KEY);
    }
}

