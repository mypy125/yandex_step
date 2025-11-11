package com.mygitgor.order_service.client;

import com.mygitgor.order_service.dto.clientDto.CartDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartClient {
    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "cartService", fallbackMethod = "getCartByUserIdFallback")
    @Retry(name = "cartService", fallbackMethod = "getCartByUserIdFallback")
    @RateLimiter(name = "cartService")
    public CartDto getCartByUserId(String userId){
        try {
            String url = "http://localhost:8084/api/carts/user/" + userId;
            ResponseEntity<CartDto> response = restTemplate.getForEntity(url, CartDto.class);
            log.debug("Retrieved cart for user: {}", userId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error retrieving cart for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to retrieve cart: " + e.getMessage());
        }
    }

    @CircuitBreaker(name = "cartService", fallbackMethod = "clearCartFallback")
    @Retry(name = "cartService", fallbackMethod = "clearCartFallback")
    public void clearCart(String cartId){
        try {
            String url = "http://localhost:8084/api/carts/" + cartId + "/clear";
            restTemplate.postForEntity(url, null, Void.class);
            log.debug("Cleared cart: {}", cartId);
        } catch (Exception e) {
            log.error("Error clearing cart {}: {}", cartId, e.getMessage());
            throw new RuntimeException("Failed to clear cart: " + e.getMessage());
        }
    }

    private CartDto getCartByUserIdFallback(String userId, Exception e) {
        log.warn("Using fallback for cart retrieval for user: {}, error: {}", userId, e.getMessage());
        return new CartDto();
    }

    private void clearCartFallback(String cartId, Exception e) {
        log.warn("Using fallback for clearing cart: {}, error: {}", cartId, e.getMessage());
    }
}
