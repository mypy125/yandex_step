package com.mygitgor.product_service.service;

import com.mygitgor.product_service.domain.Product;
import com.mygitgor.product_service.dto.ProductDto;
import com.mygitgor.product_service.mapper.ProductMapper;
import com.mygitgor.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Boolean existProductById(UUID productId) {
        return productRepository.existsById(productId);
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        if(productRepository.findById(productId).isEmpty()){
            throw new RuntimeException(String.format(
                    "product with id '%s' not found",productId
            ));
        }
        Product product = productRepository.findById(productId).get();
        return productMapper.toDto(product);
    }


}
