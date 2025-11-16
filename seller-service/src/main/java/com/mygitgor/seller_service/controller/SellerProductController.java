package com.mygitgor.seller_service.controller;

import com.mygitgor.seller_service.client.ProductClient;
import com.mygitgor.seller_service.config.JwtUtils;
import com.mygitgor.seller_service.dto.client.CreateProductRequest;
import com.mygitgor.seller_service.dto.client.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/sellers/products")
@RequiredArgsConstructor
public class SellerProductController {
    private final ProductClient productClient;
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<List<ProductDto>>getProductBySellerId(@RequestHeader("Authorization")
                                                                String jwt
    ) {
        String sellerId = jwtUtils.extractUserId(jwt);
        List<ProductDto> products=productClient.getProductBySellerId(sellerId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductDto>createProduct(@RequestBody CreateProductRequest request,
                                                @RequestHeader("Authorization") String jwt
    ) {
        String sellerId = jwtUtils.extractUserId(jwt);
        ProductDto product = productClient.createProduct(request, sellerId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId
    ) {
        return null;
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto>updateProduct(@PathVariable String productId,
                                                @RequestBody ProductDto product
    ) {
        ProductDto updateProduct= productClient.updateProduct(productId, product);
        return new ResponseEntity<>(updateProduct,HttpStatus.OK);
    }
}
