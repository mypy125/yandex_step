package com.mygitgor.order_service.repository;

import com.mygitgor.order_service.domain.Order;
import com.mygitgor.order_service.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(UUID userId);
    List<Order>findBySellerId(UUID sellerId);
    List<Order> findBySellerIdAndOrderStatus(UUID sellerId, OrderStatus orderStatus);
}
