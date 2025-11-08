package com.mygitgor.product_catalog_service.controller;

import com.mygitgor.product_catalog_service.dto.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping("/{productId}/exist")
    public ResponseEntity<Boolean>existProductById(PathVariable productId){
        return null;
    }

    @GetMapping("/{productId}/exist")
    public ResponseEntity<ProductDto>getProductById(PathVariable productId){
        return null;
    }
}
