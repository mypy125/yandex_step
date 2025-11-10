package com.mygitgor.order_service.service;

import com.mygitgor.order_service.client.CartClient;
import com.mygitgor.order_service.client.ProductClient;
import com.mygitgor.order_service.domain.Order;
import com.mygitgor.order_service.domain.OrderItem;
import com.mygitgor.order_service.domain.OrderStatus;
import com.mygitgor.order_service.dto.*;
import com.mygitgor.order_service.dto.clientDto.*;
import com.mygitgor.order_service.mapping.OrderMapper;
import com.mygitgor.order_service.repository.OrderItemRepository;
import com.mygitgor.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Override
    public Set<OrderDto> createOrder(String userId, CreateOrderRequest request) {
        CartDto cart = cartClient.getCartByUserId(userId);

        validateCartForOrder(cart);

        Map<UUID, List<CartItemDto>> itemsBySeller = groupItemsBySeller(cart.getCartItems());

        Set<Order> orders = new HashSet<>();
        for (Map.Entry<UUID, List<CartItemDto>> entry : itemsBySeller.entrySet()) {
            Order order = createOrderForSeller(userId, entry.getKey(), request, entry.getValue());
            orders.add(order);
        }

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
