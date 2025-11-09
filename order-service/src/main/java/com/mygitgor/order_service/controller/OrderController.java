package com.mygitgor.order_service.controller;

import com.mygitgor.order_service.dto.PaymentLinkResponse;
import com.mygitgor.order_service.dto.PaymentMethod;
import com.mygitgor.order_service.dto.clientDto.AddressDto;
import com.mygitgor.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService cartService;

    @PostMapping("/create")
    public ResponseEntity<PaymentLinkResponse> createOrderHandler(@RequestBody AddressDto shippingAddress,
                                                                  @RequestParam PaymentMethod paymentMethod,
                                                                  @RequestHeader String userId
    ){
        return null;
    }
}
