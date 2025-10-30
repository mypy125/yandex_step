package com.mygitgor.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitConfig {
    public static final String NOTIFICATION_EXCHANGE = "notification-exchange";
    public static final String DEAD_LETTER_EXCHANGE = "notification-dlx";

    public static final String EMAIL_OTP_QUEUE = "email.otp.queue";
    public static final String EMAIL_NOTIFICATION_QUEUE = "email.notification.queue";
    public static final String SMS_NOTIFICATION_QUEUE = "sms.notification.queue";
    public static final String PUSH_NOTIFICATION_QUEUE = "push.notification.queue";

    public static final String EMAIL_OTP_DLQ = "email.otp.dlq";
    public static final String EMAIL_NOTIFICATION_DLQ = "email.notification.dlq";

    public static final String ROUTING_KEY_EMAIL_OTP = "email.otp";
    public static final String ROUTING_KEY_EMAIL_NOTIFICATION = "email.notification";
    public static final String ROUTING_KEY_SMS_NOTIFICATION = "sms.notification";
    public static final String ROUTING_KEY_PUSH_NOTIFICATION = "push.notification";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Queue emailOtpQueue() {
        return QueueBuilder.durable(EMAIL_OTP_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", EMAIL_OTP_DLQ)
                .withArgument("x-message-ttl", 60000)
                .build();
    }

    @Bean
    public Queue emailOtpDeadLetterQueue() {
        return QueueBuilder.durable(EMAIL_OTP_DLQ)
                .withArgument("x-message-ttl", 86400000)
                .build();
    }

    @Bean
    public Binding emailOtpBinding() {
        return BindingBuilder.bind(emailOtpQueue())
                .to(notificationExchange())
                .with(ROUTING_KEY_EMAIL_OTP);
    }

    @Bean
    public Binding emailOtpDlqBinding() {
        return BindingBuilder.bind(emailOtpDeadLetterQueue())
                .to(deadLetterExchange())
                .with(EMAIL_OTP_DLQ);
    }

    @Bean
    public Queue emailNotificationQueue() {
        return QueueBuilder.durable(EMAIL_NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", EMAIL_NOTIFICATION_DLQ)
                .build();
    }

    @Bean
    public Queue emailNotificationDeadLetterQueue() {
        return QueueBuilder.durable(EMAIL_NOTIFICATION_DLQ)
                .withArgument("x-message-ttl", 86400000)
                .build();
    }

    @Bean
    public Binding emailNotificationBinding() {
        return BindingBuilder.bind(emailNotificationQueue())
                .to(notificationExchange())
                .with(ROUTING_KEY_EMAIL_NOTIFICATION);
    }

    @Bean
    public Binding emailNotificationDlqBinding() {
        return BindingBuilder.bind(emailNotificationDeadLetterQueue())
                .to(deadLetterExchange())
                .with(EMAIL_NOTIFICATION_DLQ);
    }

    @Bean
    public Queue smsNotificationQueue() {
        return QueueBuilder.durable(SMS_NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Binding smsNotificationBinding() {
        return BindingBuilder.bind(smsNotificationQueue())
                .to(notificationExchange())
                .with(ROUTING_KEY_SMS_NOTIFICATION);
    }

    @Bean
    public Queue pushNotificationQueue() {
        return QueueBuilder.durable(PUSH_NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Binding pushNotificationBinding() {
        return BindingBuilder.bind(pushNotificationQueue())
                .to(notificationExchange())
                .with(ROUTING_KEY_PUSH_NOTIFICATION);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonMessageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(1);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
