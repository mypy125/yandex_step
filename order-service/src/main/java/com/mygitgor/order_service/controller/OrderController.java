package com.mygitgor.order_service.controller;

import com.mygitgor.order_service.config.JwtUtils;
import com.mygitgor.order_service.dto.CreateOrderRequest;
import com.mygitgor.order_service.dto.OrderDto;
import com.mygitgor.order_service.dto.PaymentLinkResponse;
import com.mygitgor.order_service.dto.PaymentMethod;
import com.mygitgor.order_service.dto.clientDto.AddressDto;
import com.mygitgor.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final JwtUtils jwtUtils;

    @PostMapping("/create")
    public ResponseEntity<Set<OrderDto>> createOrderHandler(@RequestBody CreateOrderRequest request,
                                                                  @RequestHeader("Authorization") String jwt
    ){
        String userId = jwtUtils.extractUserIdFromJwt(jwt);

        Set<OrderDto> order = orderService.createOrder(userId, request);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
