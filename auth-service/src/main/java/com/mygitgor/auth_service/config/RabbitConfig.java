package com.mygitgor.auth_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mygitgor.auth_service.dto.massaging.EmailNotificationMessage;
import com.mygitgor.auth_service.dto.massaging.OtpNotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
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
        return QueueBuilder.durable(SMS_NOTIFICATION_QUEUE)
                .build();
    }

    @Bean
    public Binding smsNotificationBinding() {
        return BindingBuilder.bind(smsNotificationQueue())
                .to(notificationExchange())
                .with(ROUTING_KEY_SMS_NOTIFICATION);
    }

    @Bean
    public Queue pushNotificationQueue() {
        return QueueBuilder.durable(PUSH_NOTIFICATION_QUEUE)
                .build();
    }

    @Bean
    public Binding pushNotificationBinding() {
        return BindingBuilder.bind(pushNotificationQueue())
                .to(notificationExchange())
                .with(ROUTING_KEY_PUSH_NOTIFICATION);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonMessageConverter());

        rabbitTemplate.setRetryTemplate(retryTemplate());

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("Message failed to reach exchange: {}", cause);
            }
        });

        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("Message returned: {}", returned.toString());
        });

        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setClassMapper(classMapper());
        return converter;
    }

    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("com.mygitgor.auth_service.dto.massaging");
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("emailNotification", EmailNotificationMessage.class);
        idClassMapping.put("otpNotification", OtpNotificationMessage.class);
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(10000);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);

        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
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
