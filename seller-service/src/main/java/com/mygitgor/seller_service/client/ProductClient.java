package com.mygitgor.seller_service.client;

import com.mygitgor.seller_service.dto.client.CreateProductRequest;
import com.mygitgor.seller_service.dto.client.ProductDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductClient {
    private final RestTemplate restTemplate;

    @Value("${product.service.url:http://localhost:8086/api/products}")
    private String productServiceUrl;

    @CircuitBreaker(name = "productService", fallbackMethod = "getProductBySellerIdFallback")
    @Retry(name = "productService", fallbackMethod = "getProductBySellerIdFallback")
    @RateLimiter(name = "productService")
    public List<ProductDto> getProductBySellerId(String sellerId){
        try {
            String url = productServiceUrl + "/seller/" + sellerId;
            ResponseEntity<ProductDto[]> response = restTemplate.getForEntity(url, ProductDto[].class);
            log.debug("Retrieved products for seller: {}", sellerId);
            return Arrays.asList(Objects.requireNonNull(response.getBody()));
        } catch (Exception e) {
            log.error("Error retrieving products for seller {}: {}", sellerId, e.getMessage());
            throw new RuntimeException("Failed to retrieve seller products: " + e.getMessage());
        }
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "createProductFallback")
    @Retry(name = "productService", fallbackMethod = "createProductFallback")
    public ProductDto createProduct(CreateProductRequest request, String sellerId){
        try {
            String url = productServiceUrl;

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Seller-Id", sellerId);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CreateProductRequest> requestEntity = new HttpEntity<>(request, headers);

            ResponseEntity<ProductDto> response = restTemplate.postForEntity(url, requestEntity, ProductDto.class);
            log.info("Created product for seller: {}", sellerId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error creating product for seller {}: {}", sellerId, e.getMessage());
            throw new RuntimeException("Failed to create product: " + e.getMessage());
        }
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "updateProductFallback")
    @Retry(name = "productService", fallbackMethod = "updateProductFallback")
    public ProductDto updateProduct(String productId, ProductDto productDto){
        try {
            String url = productServiceUrl + "/" + productId;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ProductDto> requestEntity = new HttpEntity<>(productDto, headers);

            ResponseEntity<ProductDto> response = restTemplate.exchange(
                    url, HttpMethod.PUT, requestEntity, ProductDto.class);
            log.debug("Updated product: {}", productId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error updating product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to update product: " + e.getMessage());
        }
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "deleteProductFallback")
    @Retry(name = "productService", fallbackMethod = "deleteProductFallback")
    public Boolean deleteProduct(String productId) {
        try {
            String url = productServiceUrl + "/" + productId;
            restTemplate.delete(url);
            log.info("Deleted product: {}", productId);
            return true;
        } catch (Exception e) {
            log.error("Error deleting product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to delete product: " + e.getMessage());
        }
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "getProductByIdFallback")
    @Retry(name = "productService", fallbackMethod = "getProductByIdFallback")
    public ProductDto getProductById(String productId) {
        try {
            String url = productServiceUrl + "/" + productId;
            ResponseEntity<ProductDto> response = restTemplate.getForEntity(url, ProductDto.class);
            log.debug("Retrieved product: {}", productId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error retrieving product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to retrieve product: " + e.getMessage());
        }
    }

    private List<ProductDto> getProductBySellerIdFallback(String sellerId, Exception e) {
        log.warn("Using fallback for seller products: {}, error: {}", sellerId, e.getMessage());
        return Collections.emptyList();
    }

    private ProductDto createProductFallback(CreateProductRequest request, String sellerId, Exception e) {
        log.warn("Using fallback for product creation, seller: {}, error: {}", sellerId, e.getMessage());
        throw new RuntimeException("Product service unavailable for creation");
    }

    private ProductDto updateProductFallback(String productId, ProductDto productDto, Exception e) {
        log.warn("Using fallback for product update: {}, error: {}", productId, e.getMessage());
        throw new RuntimeException("Product service unavailable for update");
    }

    private Boolean deleteProductFallback(String productId, Exception e) {
        log.warn("Using fallback for product deletion: {}, error: {}", productId, e.getMessage());
        return false;
    }

    private ProductDto getProductByIdFallback(String productId, Exception e) {
        log.warn("Using fallback for product: {}, error: {}", productId, e.getMessage());
        throw new RuntimeException("Product service unavailable for product: " + productId);
    }
}
