package com.mygitgor.order_service.repository;

import com.mygitgor.order_service.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart,UUID> {
    Cart findByUserId(UUID id);
}
