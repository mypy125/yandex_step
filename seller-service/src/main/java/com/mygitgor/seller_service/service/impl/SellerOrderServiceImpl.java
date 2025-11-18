package com.mygitgor.seller_service.service.impl;

import com.mygitgor.seller_service.client.OrderClient;
import com.mygitgor.seller_service.dto.client.order.OrderDto;
import com.mygitgor.seller_service.dto.client.order.OrderStatus;
import com.mygitgor.seller_service.service.SellerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerOrderServiceImpl implements SellerOrderService {
    private final OrderClient orderClient;

    @Override
    public List<OrderDto> getSellerOrders(String sellerId) {
        return orderClient.getSellerOrders(sellerId);
    }

    @Override
    public boolean confirmOrder(String orderId) {
        return orderClient.updateOrderStatus(orderId, OrderStatus.CONFIRMED);
    }

    @Override
    public boolean shipOrder(String orderId, OrderStatus orderStatus) {
        return orderClient.updateOrderStatus(orderId, OrderStatus.SHIPPED);
    }
}
