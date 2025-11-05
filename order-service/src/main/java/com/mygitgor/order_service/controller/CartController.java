package com.mygitgor.order_service.controller;

import com.mygitgor.order_service.dto.CartDto;
import com.mygitgor.order_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<Void>createUserCartHandler(@PathVariable String userId){
        return null;
    }
}
