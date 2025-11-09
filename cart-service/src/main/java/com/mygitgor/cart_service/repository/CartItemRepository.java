package com.mygitgor.cart_service.repository;

import com.mygitgor.cart_service.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

}
