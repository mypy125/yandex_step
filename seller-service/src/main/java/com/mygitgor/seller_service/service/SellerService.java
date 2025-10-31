package com.mygitgor.seller_service.service;

import com.mygitgor.seller_service.domain.Seller;
import com.mygitgor.seller_service.dto.SellerAuthInfo;
import com.mygitgor.seller_service.dto.SellerCreateRequest;
import com.mygitgor.seller_service.dto.SellerDto;

public interface SellerService{
    boolean existsByEmail(String email);
    SellerAuthInfo getAuthInfo(String email);
    void createSeller(SellerCreateRequest request);
    SellerDto createSeller(SellerDto dto);
    void verifyEmail(String email);
    Seller getSellerByEmail(String email);
    SellerDto updateSeller(String email, SellerDto sellerDto);

}
