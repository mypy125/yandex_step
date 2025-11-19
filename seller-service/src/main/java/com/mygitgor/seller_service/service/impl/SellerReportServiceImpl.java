package com.mygitgor.seller_service.service.impl;

import com.mygitgor.seller_service.domain.Seller;
import com.mygitgor.seller_service.domain.SellerReport;
import com.mygitgor.seller_service.repository.SellerReportRepository;
import com.mygitgor.seller_service.repository.SellerRepository;
import com.mygitgor.seller_service.service.SellerReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerReportServiceImpl implements SellerReportService {
    private final SellerReportRepository sellerReportRepository;
    private final SellerRepository sellerRepository;

    @Override
    public SellerReport getSellerReport(UUID sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + sellerId));

        return sellerReportRepository.findBySeller(seller)
                .orElseGet(() -> createNewSellerReport(seller));
    }

    @Override
    public void updateSellerReport(UUID sellerId, Integer refundAmount) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + sellerId));

        SellerReport report = sellerReportRepository.findBySeller(seller)
                .orElseGet(() -> createNewSellerReport(seller));

        report.setCanceledOrders(report.getCanceledOrders() + 1);
        report.setTotalRefunds(report.getTotalRefunds() + refundAmount.longValue());

        recalculateNetEarnings(report);

        sellerReportRepository.save(report);
    }

    private SellerReport createNewSellerReport(Seller seller) {
        SellerReport report = new SellerReport();
        report.setSeller(seller);
        report.setTotalEarnings(0L);
        report.setTotalSales(0L);
        report.setTotalRefunds(0L);
        report.setTotalTax(0L);
        report.setNetEarnings(0L);
        report.setTotalOrders(0);
        report.setCanceledOrders(0);
        report.setTotalTransactions(0);
        return report;
    }

    private void recalculateNetEarnings(SellerReport report) {
        long netEarnings = report.getTotalEarnings() - report.getTotalRefunds() - report.getTotalTax();
        report.setNetEarnings(Math.max(0, netEarnings));
    }

}
