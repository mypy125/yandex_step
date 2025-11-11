package com.mygitgor.order_service.client;

import com.mygitgor.order_service.dto.OrderDto;
import com.mygitgor.order_service.dto.PaymentLinkResponse;
import com.mygitgor.order_service.dto.PaymentMethod;
import com.mygitgor.order_service.dto.clientDto.PaymentOrderDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentClient {
    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "paymentService", fallbackMethod = "createPaypalPaymentLinkFallback")
    @Retry(name = "paymentService", fallbackMethod = "createPaypalPaymentLinkFallback")
    @RateLimiter(name = "paymentService")
    public PaymentLinkResponse createPaypalPaymentLink(String userId, PaymentOrderDto paymentOrder){
        try {
            String url = "http://payment-service/api/payments/paypal/link";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", userId);
            HttpEntity<PaymentOrderDto> request = new HttpEntity<>(paymentOrder, headers);

            ResponseEntity<PaymentLinkResponse> response = restTemplate.postForEntity(url, request, PaymentLinkResponse.class);
            log.debug("Created PayPal payment link for user: {}", userId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error creating PayPal payment link for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to create PayPal payment link: " + e.getMessage());
        }
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "createStripePaymentLinkFallback")
    @Retry(name = "paymentService", fallbackMethod = "createStripePaymentLinkFallback")
    @RateLimiter(name = "paymentService")
    public PaymentLinkResponse createStripePaymentLink(String userId, PaymentOrderDto paymentOrder){
        try {
            String url = "http://payment-service/api/payments/stripe/link";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", userId);
            HttpEntity<PaymentOrderDto> request = new HttpEntity<>(paymentOrder, headers);

            ResponseEntity<PaymentLinkResponse> response = restTemplate.postForEntity(url, request, PaymentLinkResponse.class);
            log.debug("Created Stripe payment link for user: {}", userId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error creating Stripe payment link for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to create Stripe payment link: " + e.getMessage());
        }
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "createPaymentOrderFallback")
    @Retry(name = "paymentService", fallbackMethod = "createPaymentOrderFallback")
    public PaymentOrderDto createPaymentOrder(String userId, Set<OrderDto> orders, PaymentMethod paymentMethod){
        try {
            String url = "http://payment-service/api/payments/orders";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("userId", userId);
            requestBody.put("orders", orders);
            requestBody.put("paymentMethod", paymentMethod);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<PaymentOrderDto> response = restTemplate.postForEntity(url, request, PaymentOrderDto.class);
            log.debug("Created payment order for user: {}", userId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error creating payment order for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to create payment order: " + e.getMessage());
        }
    }

    private PaymentLinkResponse createPaypalPaymentLinkFallback(String userId, PaymentOrderDto paymentOrder, Exception e) {
        log.warn("Using fallback for PayPal payment link creation for user: {}, error: {}", userId, e.getMessage());
        throw new RuntimeException("Payment service unavailable for PayPal payments");
    }

    private PaymentLinkResponse createStripePaymentLinkFallback(String userId, PaymentOrderDto paymentOrder, Exception e) {
        log.warn("Using fallback for Stripe payment link creation for user: {}, error: {}", userId, e.getMessage());
        throw new RuntimeException("Payment service unavailable for Stripe payments");
    }

    private PaymentOrderDto createPaymentOrderFallback(String userId, Set<OrderDto> orders, PaymentMethod paymentMethod, Exception e) {
        log.warn("Using fallback for payment order creation for user: {}, error: {}", userId, e.getMessage());
        throw new RuntimeException("Payment service unavailable for order creation");
    }

}
