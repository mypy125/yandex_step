package com.mygitgor.seller_service.repository;

import com.mygitgor.seller_service.domain.Seller;
import com.mygitgor.seller_service.domain.SellerReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SellerReportRepository extends JpaRepository<SellerReport, UUID> {
    Optional<SellerReport> findBySeller(Seller seller);
    Optional<SellerReport> findBySellerId(UUID sellerId);
}
