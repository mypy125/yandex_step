package com.mygitgor.seller_service.controller;

import com.mygitgor.seller_service.client.ProductClient;
import com.mygitgor.seller_service.config.JwtUtils;
import com.mygitgor.seller_service.dto.client.CreateProductRequest;
import com.mygitgor.seller_service.dto.client.ProductDto;
import com.mygitgor.seller_service.service.SellerProductService;
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
    private final SellerProductService sellerProductService;
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<List<ProductDto>>getProductBySellerId(@RequestHeader("Authorization") String jwt
    ) {
        String sellerId = jwtUtils.extractUserId(jwt);
        List<ProductDto> products=sellerProductService.getSellerProducts(sellerId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductDto>createProduct(@RequestBody CreateProductRequest request,
                                                   @RequestHeader("Authorization") String jwt
    ) {
        String sellerId = jwtUtils.extractUserId(jwt);
        ProductDto product = sellerProductService.createProduct(request, sellerId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable String productId,
                                              @RequestHeader("Authorization") String jwt
    ) {
        String sellerId = jwtUtils.extractUserId(jwt);
        Boolean deleteProduct = sellerProductService.deleteProduct(productId, sellerId);
        return new ResponseEntity<>(deleteProduct,HttpStatus.OK);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto>updateProduct(@PathVariable String productId,
                                                   @RequestBody ProductDto product,
                                                   @RequestHeader("Authorization") String jwt
    ) {
        String sellerId = jwtUtils.extractUserId(jwt);
        ProductDto updateProduct= sellerProductService.updateProduct(productId, product, sellerId);
        return new ResponseEntity<>(updateProduct,HttpStatus.OK);
    }
}
