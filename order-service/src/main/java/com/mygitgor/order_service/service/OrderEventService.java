package com.mygitgor.order_service.service;

import com.mygitgor.order_service.config.RabbitConfig;
import com.mygitgor.order_service.dto.event.OrderCanceledEvent;
import com.mygitgor.order_service.dto.OrderDto;
import com.mygitgor.order_service.dto.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventService {
    private final AmqpTemplate amqpTemplate;

    public void sendOrderCanceledEvent(OrderDto canceledOrder) {
        OrderCanceledEvent event = OrderCanceledEvent.builder()
                .orderId(canceledOrder.getId())
                .sellerId(canceledOrder.getSellerId())
                .totalAmount(canceledOrder.getTotalSellingPrice())
                .canceledAt(LocalDateTime.now())
                .build();

        amqpTemplate.convertAndSend(
                "order.exchange",
                "order.canceled",
                event
        );

        log.info("Sent order canceled event for seller: {}", canceledOrder.getSellerId());
    }

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        amqpTemplate.convertAndSend(
                "order.exchange",
                "order.created",
                event
        );
        log.info("Sent order created event for order: {}, seller: {}",
                event.getOrderId(), event.getSellerId());
    }
}
