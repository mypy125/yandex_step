package com.mygitgor.seller_service.client;

import com.mygitgor.seller_service.dto.client.order.OrderDto;
import com.mygitgor.seller_service.dto.client.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderClient {
    private final RestTemplate restTemplate;

    public List<OrderDto> getSellerOrders(String sellerId){
        return null;
    }

    public Boolean updateOrderStatus(String orderId, OrderStatus status){
        return false;
    }
}
