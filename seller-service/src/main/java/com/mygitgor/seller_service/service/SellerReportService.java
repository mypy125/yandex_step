package com.mygitgor.seller_service.service;

import com.mygitgor.seller_service.domain.Seller;
import com.mygitgor.seller_service.domain.SellerReport;

public interface SellerReportService {
    SellerReport getSellerReport(Seller seller);
    SellerReport updateSellerReport(SellerReport sellerReport);
}
