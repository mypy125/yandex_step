package com.mygitgor.order_service.service;

import com.mygitgor.order_service.client.CartClient;
import com.mygitgor.order_service.client.ProductClient;
import com.mygitgor.order_service.domain.Order;
import com.mygitgor.order_service.domain.OrderItem;
import com.mygitgor.order_service.domain.OrderStatus;
import com.mygitgor.order_service.dto.*;
import com.mygitgor.order_service.dto.clientDto.*;
import com.mygitgor.order_service.mapping.OrderItemMapper;
import com.mygitgor.order_service.mapping.OrderMapper;
import com.mygitgor.order_service.repository.OrderItemRepository;
import com.mygitgor.order_service.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartClient cartClient;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public Set<OrderDto> createOrder(String userId, CreateOrderRequest request) {
        CartDto cart = cartClient.getCartByUserId(userId);

        validateCartForOrder(cart);

        Map<UUID, List<CartItemDto>> itemsBySeller = groupItemsBySeller(cart.getCartItems());

        Set<Order> orders = new HashSet<>();
        for (Map.Entry<UUID, List<CartItemDto>> entry : itemsBySeller.entrySet()) {
            Order order = createOrderForSeller(userId, entry.getKey(), request, entry.getValue());

            Order savedOrder = orderRepository.save(order);
            orders.add(savedOrder);
        }
        cartClient.clearCart(cart.getId().toString());
        return orderMapper.toOrderDtoSet(orders);
    }

    private Order createOrderForSeller(String userId, UUID sellerId, CreateOrderRequest request,
                                       List<CartItemDto> cartItems) {
        Order order = Order.create(userId, UUID.fromString(request.getShippingAddress()), sellerId);

        for (CartItemDto cartItem : cartItems) {
            ProductDto product = productClient.getProductById(cartItem.getProductId().toString());

            OrderItem orderItem = OrderItem.createFromCartItem(cartItem, product);
            orderItem.validate();

            order.addOrderItem(orderItem);
        }

        order.updateTotals();
        Order savedOrder = orderRepository.save(order);

        log.info("Created order {} with {} items for seller {}",
                savedOrder.getOrderId(), cartItems.size(), sellerId);

        return savedOrder;
    }

    private Map<UUID, List<CartItemDto>> groupItemsBySeller(List<CartItemDto> cartItems) {
        Map<UUID, List<CartItemDto>> itemsBySeller = new HashMap<>();

        for (CartItemDto cartItem : cartItems) {
            ProductDto product = productClient.getProductById(cartItem.getProductId().toString());
            itemsBySeller.computeIfAbsent(product.getSellerId(), k -> new ArrayList<>())
                    .add(cartItem);
        }

        return itemsBySeller;
    }

    private void validateCartForOrder(CartDto cart) {
        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        for (CartItemDto item : cart.getCartItems()) {
            ProductDto product = productClient.getProductById(item.getProductId().toString());
            if (!product.getActive() || product.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException(
                        String.format("Product %s is unavailable", product.getTitle())
                );
            }
        }
    }

    @Override
    public OrderDto findOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("order with id '%s' not found",orderId)));

        return orderMapper.toOrderDto(order);
    }

    @Override
    public List<OrderDto> usersOrderHistory(UUID userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        if (orders.isEmpty()) {
            throw new EntityNotFoundException(String.format(
                    "order wit user id '%s' not found",userId
            ));
        }

        return orderMapper.toOrderDtoList(orders);
    }

    @Override
    public List<OrderDto> sellersOrder(UUID sellerId) {
        List<Order> orders = orderRepository.findBySellerId(sellerId);
        if (orders.isEmpty()) {
            throw new EntityNotFoundException(String.format(
                    "order wit seller id '%s' not found",sellerId
            ));
        }

        return orderMapper.toOrderDtoList(orders);
    }

    @Override
    public List<OrderDto> getSellerOrders(UUID sellerId, OrderStatus status) {
        List<Order> orders;
        if (status != null) {
            orders = orderRepository.findBySellerIdAndOrderStatus(sellerId, status);
        } else {
            orders = orderRepository.findBySellerId(sellerId);
        }
        return orderMapper.toOrderDtoList(orders);
    }

    @Override
    public OrderDto updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("order with id '%s' not found",orderId)));

        order.setOrderStatus(status);
        if (status == OrderStatus.DELIVERED) {
            order.setDeliverDate(LocalDateTime.now());
        }
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto cancelOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("order with id '%s' not found",orderId)));

        if (!UUID.fromString(order.getUserId()).equals(userId)) {
            throw new IllegalArgumentException("User cannot cancel another user's order");
        }

        if (order.getOrderStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("Order already cancelled");
        }

        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderItemDto getOrderItemById(UUID orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new EntityNotFoundException("Order item not found with id: " + orderItemId));

        return orderItemMapper.toOrderItemDto(orderItem);
    }


}
