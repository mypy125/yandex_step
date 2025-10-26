package com.mygitgor.auth_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "notification-exchange";
    public static final String ROUTING_KEY = "email.notification";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }
}
