package com.mygitgor.seller_service.service.impl;

import com.mygitgor.seller_service.domain.Seller;
import com.mygitgor.seller_service.dto.SellerAuthInfo;
import com.mygitgor.seller_service.dto.SellerCreateRequest;
import com.mygitgor.seller_service.dto.SellerDto;
import com.mygitgor.seller_service.mapping.SellerMapper;
import com.mygitgor.seller_service.repository.SellerRepository;
import com.mygitgor.seller_service.service.SellerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class sellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;

    @Override
    public boolean existsByEmail(String email) {
        return sellerRepository.findByEmail(email).isPresent();
    }

    @Override
    public SellerAuthInfo getAuthInfo(String email) {
        Seller seller = getSellerByEmail(email);
        return sellerMapper.toSellerAuthInfo(seller);
    }

    @Override
    public void createSeller(SellerCreateRequest request) {

    }

    @Override
    public SellerDto createSeller(SellerDto sellerDto) {
        Seller seller = sellerMapper.toSeller(sellerDto);
        Seller savedSeller = sellerRepository.save(seller);
        return sellerMapper.toSellerDto(savedSeller);
    }

    @Override
    public void verifyEmail(String email) {
        Seller seller = getSellerByEmail(email);

        if (!seller.isEmailVerified()) {
            seller.setEmailVerified(true);
            sellerRepository.save(seller);
        }
    }

    @Override
    public Seller getSellerByEmail(String email) {
        return sellerRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("seller not found with email " + email));
    }

    @Override
    public SellerDto updateSeller(String email, SellerDto sellerDto) {
        Seller seller = getSellerByEmail(email);
        sellerMapper.updateSellerFromDto(sellerDto, seller);
        Seller updatedSeller = sellerRepository.save(seller);

        return sellerMapper.toSellerDto(updatedSeller);
    }
}
