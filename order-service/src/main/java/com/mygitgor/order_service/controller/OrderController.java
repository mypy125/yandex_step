package com.mygitgor.order_service.controller;

import com.mygitgor.order_service.client.PaymentClient;
import com.mygitgor.order_service.config.JwtUtils;
import com.mygitgor.order_service.domain.OrderStatus;
import com.mygitgor.order_service.dto.*;
import com.mygitgor.order_service.dto.clientDto.AddressDto;
import com.mygitgor.order_service.dto.clientDto.PaymentOrderDto;
import com.mygitgor.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final PaymentClient paymentClient;
    private final JwtUtils jwtUtils;

    @PostMapping("/create")
    public ResponseEntity<PaymentLinkResponse> createOrderHandler(@RequestBody CreateOrderRequest request,
                                                                  @RequestParam PaymentMethod paymentMethod,
                                                                  @RequestHeader("Authorization") String jwt
    ) {
        try {
            String userId = jwtUtils.extractUserId(jwt);

            Set<OrderDto> orders = orderService.createOrder(userId, request);

            PaymentOrderDto paymentOrder = paymentClient.createPaymentOrder(userId, orders, paymentMethod);
            PaymentLinkResponse paymentLink = createPaymentLink(userId, paymentOrder, paymentMethod);
            return new ResponseEntity<>(paymentLink, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.error("Validation error in create order: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(PaymentLinkResponse.builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PaymentLinkResponse.builder()
                            .error("Failed to create order: " + e.getMessage())
                            .build());
        }

    }

    private PaymentLinkResponse createPaymentLink(String userId, PaymentOrderDto paymentOrder,
                                                  PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case PAYPAL -> paymentClient.createPaypalPaymentLink(userId, paymentOrder);
            case STRIPE -> paymentClient.createStripePaymentLink(userId, paymentOrder);
            default -> throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        };
    }


    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<OrderDto>> getSellerOrders(@PathVariable String sellerId,
                                                          @RequestParam(required = false) OrderStatus status
    ) {
        try {
            log.debug("Retrieving orders for seller: {}", sellerId);
            List<OrderDto> orders = orderService.getSellerOrders(UUID.fromString(sellerId), status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error retrieving orders for seller {}: {}", sellerId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable String orderId,
                                                      @RequestBody UpdateOrderStatusRequest request) {
        try {
            log.info("Updating order {} status to {}", orderId, request.getStatus());
            OrderDto order = orderService.updateOrderStatus(UUID.fromString(orderId), request.getStatus());
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error updating order {} status: {}", orderId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable String orderId) {
        try {
            OrderDto order = orderService.findOrderById(UUID.fromString(orderId));
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error retrieving order {}: {}", orderId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
