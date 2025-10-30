package com.mygitgor.seller_service.service;

import com.mygitgor.seller_service.domain.Seller;
import com.mygitgor.seller_service.dto.SellerAuthInfo;
import com.mygitgor.seller_service.dto.SellerCreateRequest;

public interface SellerService{
    boolean existsByEmail(String email);
    SellerAuthInfo getAuthInfo(String email);
    void createSeller(SellerCreateRequest request);
    void verifyEmail(String email);
    Seller getSellerByEmail(String email);
    Seller updateSeller(String email, Seller seller);

}
