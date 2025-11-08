package com.mygitgor.order_service.client;

import com.mygitgor.order_service.dto.response.ProductDto;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductClient {
    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    @Retry(name = "productService", fallbackMethod = "getProductFallback")
    @RateLimiter(name = "productService")
    @Bulkhead(name = "productService")
    public ProductDto getProductById(UUID productId) {
        try {
            String url = "http://product-service/api/products/" + productId;
            ResponseEntity<ProductDto> response = restTemplate.getForEntity(url, ProductDto.class);
            log.debug("Retrieved product: {}", productId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error retrieving product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to retrieve product: " + e.getMessage());
        }
    }

    private ProductDto getProductFallback(UUID productId, Exception e) {
        log.warn("Using fallback for product: {}, error: {}", productId, e.getMessage());
        throw new RuntimeException("Product service unavailable for product: " + productId);
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "existsByIdFallback")
    @Retry(name = "productService", fallbackMethod = "existsByIdFallback")
    public boolean existsById(UUID productId) {
        try {
            String url = "http://product-service/api/products/" + productId + "/exists";
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            log.error("Error checking product existence {}: {}", productId, e.getMessage());
            return false;
        }
    }

    private boolean existsByIdFallback(UUID productId, Exception e) {
        log.warn("Using fallback for product existence check: {}", productId);
        return false;
    }
}
