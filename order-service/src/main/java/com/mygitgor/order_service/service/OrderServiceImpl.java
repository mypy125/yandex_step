package com.mygitgor.order_service.service;

import com.mygitgor.order_service.domain.OrderStatus;
import com.mygitgor.order_service.dto.OrderDto;
import com.mygitgor.order_service.dto.OrderItemDto;
import com.mygitgor.order_service.dto.clientDto.AddressDto;
import com.mygitgor.order_service.dto.clientDto.CartDto;
import com.mygitgor.order_service.mapping.OrderItemMapper;
import com.mygitgor.order_service.mapping.OrderMapper;
import com.mygitgor.order_service.repository.OrderItemRepository;
import com.mygitgor.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public Set<OrderDto> createOrder(String userId, AddressDto shippingAddress, CartDto cart) {
        return Set.of();
    }

    @Override
    public OrderDto findOrderById(UUID orderId) throws Exception {
        return null;
    }

    @Override
    public List<OrderDto> usersOrderHistory(UUID userId) {
        return List.of();
    }

    @Override
    public List<OrderDto> sellersOrder(UUID sellerId) {
        return List.of();
    }

    @Override
    public OrderDto updateOrderStatus(UUID orderId, OrderStatus status) throws Exception {
        return null;
    }

    @Override
    public OrderDto cancelOrder(UUID orderId, UUID userId) throws Exception {
        return null;
    }

    @Override
    public OrderItemDto getOrderItemById(UUID orderItemId) throws Exception {
        return null;
    }
}
