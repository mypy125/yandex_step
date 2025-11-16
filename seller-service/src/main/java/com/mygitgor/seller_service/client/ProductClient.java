package com.mygitgor.seller_service.client;

import com.mygitgor.seller_service.dto.client.CreateProductRequest;
import com.mygitgor.seller_service.dto.client.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductClient {
    private final RestTemplate restTemplate;

    public List<ProductDto> getProductBySellerId(String sellerId){
        return null;
    }

    public ProductDto createProduct(CreateProductRequest request, String sellerId){
        return null;
    }

    public ProductDto updateProduct(String productId, ProductDto productDto){
        return null;
    }
}
