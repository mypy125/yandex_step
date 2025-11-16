package com.mygitgor.seller_service.service;

import com.mygitgor.seller_service.domain.AccountStatus;
import com.mygitgor.seller_service.domain.Seller;
import com.mygitgor.seller_service.dto.SellerAuthInfo;
import com.mygitgor.seller_service.dto.SellerDto;

import java.util.List;
import java.util.UUID;

public interface SellerService{
    SellerDto getSellerProfile(String jwt) throws Exception;
    boolean existsByEmail(String email);
    SellerAuthInfo getAuthInfo(String email);
    SellerDto createSeller(SellerDto dto);
    void verifyEmail(String email);
    Seller getSellerByEmail(String email);
    SellerDto getSellerById(UUID id);
    List<SellerDto> getAllSellers(AccountStatus status);
    SellerDto updateSeller(String email, SellerDto sellerDto);
    SellerDto updateSellerAccountStatus(UUID sellerId, AccountStatus status);
    void deleteSeller(UUID id);

}
