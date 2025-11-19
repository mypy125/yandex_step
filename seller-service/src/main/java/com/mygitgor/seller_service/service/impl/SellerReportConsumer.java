package com.mygitgor.seller_service.service.impl;

import com.mygitgor.seller_service.dto.event.OrderCanceledEvent;
import com.mygitgor.seller_service.dto.event.OrderCreatedEvent;
import com.mygitgor.seller_service.service.SellerReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellerReportConsumer {
    private final SellerReportService sellerReportService;

    @RabbitListener(queues = "order.canceled.queue")
    @Transactional
    public void handleOrderCanceled(OrderCanceledEvent event) {
        try {
            log.info("Processing order canceled event for seller: {}, order: {}",
                    event.getSellerId(), event.getOrderId());

            sellerReportService.updateSellerReport(
                    event.getSellerId(),
                    event.getTotalAmount()
            );

        } catch (Exception e) {
            log.error("Error processing order canceled event for order {}: {}",
                    event.getOrderId(), e.getMessage());
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    @RabbitListener(queues = "order.created.queue")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Processing order created event for seller: {}, order: {}",
                    event.getSellerId(), event.getOrderId());

            sellerReportService.updateSellerReport(
                    event.getSellerId(),
                    event.getTotalSellingPrice()
            );

        } catch (Exception e) {
            log.error("Error processing order created event for order {}: {}",
                    event.getOrderId(), e.getMessage());
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
