package com.mygitgor.order_service.client;

import com.mygitgor.order_service.dto.clientDto.ProductDto;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    public ProductDto getProductById(String id){
        try {
            String url = "http://localhost:8086/api/products"+ id;
            ResponseEntity<ProductDto> response = restTemplate.getForEntity(url, ProductDto.class);
            log.debug("Retrieved product: {}", id);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error retrieving product {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to retrieve product: " + e.getMessage());
        }
    }

    private ProductDto getProductByIdFallback(String id, Exception e) {
        log.warn("Using fallback for product: {}, error: {}", id, e.getMessage());
        ProductDto fallbackProduct = new ProductDto();
        fallbackProduct.setId(UUID.fromString(id));
        fallbackProduct.setDescription("Product unavailable");
        fallbackProduct.setInStock(false);
        return fallbackProduct;
    }
}
