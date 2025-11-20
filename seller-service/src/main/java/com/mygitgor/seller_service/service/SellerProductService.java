package com.mygitgor.seller_service.service;

import com.mygitgor.seller_service.dto.client.CreateProductRequest;
import com.mygitgor.seller_service.dto.client.ProductDto;

import java.util.List;

public interface SellerProductService {
    List<ProductDto>getSellerProducts(String sellerId);
    ProductDto createProduct(CreateProductRequest request, String sellerId);
    ProductDto updateProduct(String productId,ProductDto productDto, String sellerId);
    Boolean deleteProduct(String productId, String sellerId);
    ProductDto getProductById(String productId, String sellerId);
}
