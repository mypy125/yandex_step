package com.mygitgor.seller_service.service;

import com.mygitgor.seller_service.domain.SellerReport;

import java.util.UUID;

public interface SellerReportService {
    SellerReport getSellerReport(UUID sellerId);
    void updateSellerReport(UUID sellerId, Integer refundAmount);
}
