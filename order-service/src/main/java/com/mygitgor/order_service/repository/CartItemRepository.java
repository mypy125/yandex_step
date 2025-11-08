package com.mygitgor.order_service.repository;

import com.mygitgor.order_service.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

}
