package com.mygitgor.order_service.controller;

import com.mygitgor.order_service.client.PaymentClient;
import com.mygitgor.order_service.config.JwtUtils;
import com.mygitgor.order_service.dto.CreateOrderRequest;
import com.mygitgor.order_service.dto.OrderDto;
import com.mygitgor.order_service.dto.PaymentLinkResponse;
import com.mygitgor.order_service.dto.PaymentMethod;
import com.mygitgor.order_service.dto.clientDto.AddressDto;
import com.mygitgor.order_service.dto.clientDto.PaymentOrderDto;
import com.mygitgor.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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


}
