package com.mygitgor.product_service.service;

import com.mygitgor.product_service.dto.ProductDto;

import java.util.UUID;

public interface ProductService {
    Boolean existProductById(UUID productId);
    ProductDto getProductById(UUID productId);
}
