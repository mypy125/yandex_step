package com.mygitgor.seller_service.repository;

import com.mygitgor.seller_service.domain.AccountStatus;
import com.mygitgor.seller_service.domain.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SellerRepository extends JpaRepository<Seller, UUID> {
    Optional<Seller> findByEmail(String email);
    List<Seller> findByAccountStatus(AccountStatus status);
}
