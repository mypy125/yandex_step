package com.mygitgor.seller_service.service.impl;

import com.mygitgor.seller_service.client.ProductClient;
import com.mygitgor.seller_service.dto.client.CreateProductRequest;
import com.mygitgor.seller_service.dto.client.ProductDto;
import com.mygitgor.seller_service.repository.SellerRepository;
import com.mygitgor.seller_service.service.SellerProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellerProductServiceImpl implements SellerProductService {
    private final ProductClient productClient;
    private final SellerRepository sellerRepository;

    @Override
    public List<ProductDto> getSellerProducts(String sellerId) {

        try {
            log.debug("Retrieving products for seller: {}", sellerId);
            return productClient.getProductBySellerId(sellerId);
        } catch (Exception e) {
            log.error("Error retrieving seller products: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve seller products");
        }
    }

    @Override
    public ProductDto createProduct(CreateProductRequest request, String sellerId) {

        try {
            log.info("Creating product for seller: {}", sellerId);
            return productClient.createProduct(request, sellerId);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage());
            throw new RuntimeException("Failed to create product");
        }
    }

    @Override
    public ProductDto updateProduct(String productId, ProductDto productDto, String sellerId) {
        try {
            log.info("Updating product {} for seller: {}", productId, sellerId);
            ProductDto existingProduct = productClient.getProductById(productId);
            if (!existingProduct.getSellerId().toString().equals(sellerId)) {
                throw new RuntimeException("Product does not belong to seller");
            }

            productDto.setSellerId(UUID.fromString(sellerId));

            return productClient.updateProduct(productId, productDto);
        } catch (Exception e) {
            log.error("Error updating product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to update product");
        }
    }

    @Override
    public Boolean deleteProduct(String productId, String sellerId) {
        try {
            log.info("Deleting product {} for seller: {}", productId, sellerId);
            ProductDto existingProduct = productClient.getProductById(productId);
            if (!existingProduct.getSellerId().toString().equals(sellerId)) {
                throw new RuntimeException("Product does not belong to seller");
            }

            return productClient.deleteProduct(productId);
        } catch (Exception e) {
            log.error("Error deleting product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to delete product");
        }
    }

    @Override
    public ProductDto getProductById(String productId, String sellerId) {
        try {
            log.debug("Retrieving product {} for seller: {}", productId, sellerId);

            ProductDto product = productClient.getProductById(productId);

            if (!product.getSellerId().toString().equals(sellerId)) {
                throw new RuntimeException("Product does not belong to seller");
            }

            return product;
        } catch (Exception e) {
            log.error("Error retrieving product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to retrieve product");
        }
    }
}
