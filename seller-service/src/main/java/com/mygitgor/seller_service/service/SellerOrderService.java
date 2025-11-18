package com.mygitgor.seller_service.service;

import com.mygitgor.seller_service.dto.client.order.OrderDto;
import com.mygitgor.seller_service.dto.client.order.OrderStatus;

import java.util.List;

public interface SellerOrderService {
    List<OrderDto> getSellerOrders(String sellerId);
    boolean confirmOrder(String orderId);
    boolean shipOrder(String orderId, OrderStatus orderStatus);
}
