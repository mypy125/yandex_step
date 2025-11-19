package com.mygitgor.seller_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_CANCELED_QUEUE = "order.canceled.queue";
    public static final String ORDER_CANCELED_ROUTING_KEY = "order.canceled";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderCanceledQueue() {
        return new Queue(ORDER_CANCELED_QUEUE, true);
    }

    @Bean
    public Binding orderCanceledBinding(Queue orderCanceledQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCanceledQueue)
                .to(orderExchange)
                .with(ORDER_CANCELED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
