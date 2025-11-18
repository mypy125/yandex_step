package com.mygitgor.seller_service.client;

import com.mygitgor.seller_service.dto.client.order.OrderDto;
import com.mygitgor.seller_service.dto.client.order.OrderStatus;
import com.mygitgor.seller_service.dto.client.order.UpdateOrderStatusRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderClient {
    private final RestTemplate restTemplate;

    @Value("${order.service.url:http://localhost:8085/api/orders}")
    private String orderServiceUrl;

    @CircuitBreaker(name = "orderService", fallbackMethod = "getSellerOrdersFallback")
    @Retry(name = "orderService", fallbackMethod = "getSellerOrdersFallback")
    @RateLimiter(name = "orderService")
    public List<OrderDto> getSellerOrders(String sellerId) {
        try {
            String url = orderServiceUrl + "/seller/" + sellerId;
            ResponseEntity<OrderDto[]> response = restTemplate.getForEntity(url, OrderDto[].class);
            log.debug("Retrieved orders for seller: {}", sellerId);
            return Arrays.asList(Objects.requireNonNull(response.getBody()));
        } catch (Exception e) {
            log.error("Error retrieving orders for seller {}: {}", sellerId, e.getMessage());
            throw new RuntimeException("Failed to retrieve seller orders: " + e.getMessage());
        }
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "updateOrderStatusFallback")
    @Retry(name = "orderService", fallbackMethod = "updateOrderStatusFallback")
    public Boolean updateOrderStatus(String orderId, OrderStatus orderStatus) {
        try {
            String url = orderServiceUrl + "/" + orderId + "/status";

            UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
            request.setStatus(orderStatus);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UpdateOrderStatusRequest> requestEntity = new HttpEntity<>(request, headers);

            ResponseEntity<Boolean> response = restTemplate.exchange(
                    url,
                    HttpMethod.PATCH,
                    requestEntity,
                    Boolean.class
            );

            log.debug("Updated order {} status to: {}", orderId, orderStatus);
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            log.error("Error updating order {} status to {}: {}", orderId, orderStatus, e.getMessage());
            throw new RuntimeException("Failed to update order status: " + e.getMessage());
        }
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderByIdFallback")
    @Retry(name = "orderService", fallbackMethod = "getOrderByIdFallback")
    public OrderDto getOrderById(String orderId) {
        try {
            String url = orderServiceUrl + "/" + orderId;
            ResponseEntity<OrderDto> response = restTemplate.getForEntity(url, OrderDto.class);
            log.debug("Retrieved order: {}", orderId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error retrieving order {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Failed to retrieve order: " + e.getMessage());
        }
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "getUserOrdersFallback")
    @Retry(name = "orderService", fallbackMethod = "getUserOrdersFallback")
    public List<OrderDto> getUserOrders(String userId) {
        try {
            String url = orderServiceUrl + "/user/" + userId;
            ResponseEntity<OrderDto[]> response = restTemplate.getForEntity(url, OrderDto[].class);
            log.debug("Retrieved orders for user: {}", userId);
            return Arrays.asList(Objects.requireNonNull(response.getBody()));
        } catch (Exception e) {
            log.error("Error retrieving orders for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to retrieve user orders: " + e.getMessage());
        }
    }


    private List<OrderDto> getSellerOrdersFallback(String sellerId, Exception e) {
        log.warn("Using fallback for seller orders: {}, error: {}", sellerId, e.getMessage());
        return Collections.emptyList();
    }

    private Boolean updateOrderStatusFallback(String orderId, OrderStatus orderStatus, Exception e) {
        log.warn("Using fallback for updating order status: {}, status: {}, error: {}",
                orderId, orderStatus, e.getMessage());
        return false;
    }

    private OrderDto getOrderByIdFallback(String orderId, Exception e) {
        log.warn("Using fallback for order: {}, error: {}", orderId, e.getMessage());
        throw new RuntimeException("Order service unavailable for order: " + orderId);
    }

    private List<OrderDto> getUserOrdersFallback(String userId, Exception e) {
        log.warn("Using fallback for user orders: {}, error: {}", userId, e.getMessage());
        return Collections.emptyList();
    }
}
