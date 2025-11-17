package com.mygitgor.seller_service.controller;

import com.mygitgor.seller_service.client.OrderClient;
import com.mygitgor.seller_service.config.JwtUtils;
import com.mygitgor.seller_service.dto.client.order.OrderDto;
import com.mygitgor.seller_service.dto.client.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sellers/orders")
@RequiredArgsConstructor
public class SellerOrderController {
    private final OrderClient orderClient;
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrdersHandler(@RequestHeader("Authorization")
                                                                String jwt
    ) {
        String sellerId = jwtUtils.extractUserId(jwt);
        List<OrderDto> orders = orderClient.getSellerOrders(sellerId);
        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{orderId}/status/{orderStatus}")
    public ResponseEntity<Boolean> updateOrderHandler(@PathVariable String orderId,
                                                       @PathVariable OrderStatus orderStatus,
                                                       @RequestHeader("Authorization")
                                                   String jwt
    ) {
        String validId = jwtUtils.extractUserId(jwt);
        Boolean order = orderClient.updateOrderStatus(orderId,orderStatus);
        return new ResponseEntity<>(order,HttpStatus.ACCEPTED);
    }
}
