package com.mygitgor.seller_service.controller;

import com.mygitgor.seller_service.config.JwtUtils;
import com.mygitgor.seller_service.dto.client.order.OrderDto;
import com.mygitgor.seller_service.dto.client.order.OrderStatus;
import com.mygitgor.seller_service.service.SellerOrderService;
import com.mygitgor.seller_service.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sellers/orders")
@RequiredArgsConstructor
public class SellerOrderController {
    private final SellerOrderService sellerOrderService;
    private final SellerService sellerService;
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrdersHandler(@RequestHeader("Authorization")
                                                                String jwt
    ) {
        String sellerId = jwtUtils.extractUserId(jwt);
        List<OrderDto> orders = sellerOrderService.getSellerOrders(sellerId);
        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{orderId}/status/{orderStatus}")
    public ResponseEntity<Boolean> updateOrderHandler(@PathVariable String orderId,
                                                       @PathVariable OrderStatus orderStatus,
                                                       @RequestHeader("Authorization") String jwt
    ) {
        try{
            sellerService.getSellerProfile(jwt);
            Boolean order = sellerOrderService.updateOrderStatus(orderId,orderStatus);
            return new ResponseEntity<>(order,HttpStatus.ACCEPTED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
