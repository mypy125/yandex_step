package com.mygitgor.product_service.controller;

import com.mygitgor.product_service.dto.ProductDto;
import com.mygitgor.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{productId}/exist")
    public ResponseEntity<Boolean>existProductById(@PathVariable String productId){
        Boolean existById = productService.existProductById(UUID.fromString(productId));
        return ResponseEntity.ok(existById);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto>getProductById(@PathVariable String productId){
        ProductDto product = productService.getProductById(UUID.fromString(productId));
        return ResponseEntity.ok(product);
    }
}
