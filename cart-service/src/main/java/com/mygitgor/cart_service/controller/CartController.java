package com.mygitgor.cart_service.controller;

import com.mygitgor.cart_service.dto.CartDto;
import com.mygitgor.cart_service.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<Void> createUserCartHandler(@PathVariable String userId
    ){
        try {
            cartService.createUserCart(userId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            log.error("Error creating cart for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<CartDto> getUserCartHandler(@PathVariable String userId
    ){
        try {
            CartDto userCart = cartService.getUserCart(userId);
            return ResponseEntity.ok(userCart);
        } catch (RuntimeException e) {
            log.error("Error get cart wit user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
