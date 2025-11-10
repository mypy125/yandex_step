package com.mygitgor.order_service.service;

import com.mygitgor.order_service.domain.Order;
import com.mygitgor.order_service.domain.OrderStatus;
import com.mygitgor.order_service.dto.CreateOrderRequest;
import com.mygitgor.order_service.dto.OrderDto;
import com.mygitgor.order_service.dto.OrderItemDto;
import com.mygitgor.order_service.dto.PaymentLinkResponse;
import com.mygitgor.order_service.dto.clientDto.CartDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OrderService {
    Set<OrderDto> createOrder(String userId, CreateOrderRequest request);
    OrderDto findOrderById(UUID orderId) throws Exception;
    List<OrderDto>usersOrderHistory(UUID userId);
    List<OrderDto> sellersOrder(UUID sellerId);
    OrderDto updateOrderStatus(UUID orderId, OrderStatus status) throws Exception;
    OrderDto cancelOrder(UUID orderId, UUID userId) throws Exception;
    OrderItemDto getOrderItemById(UUID orderItemId) throws Exception;
}
