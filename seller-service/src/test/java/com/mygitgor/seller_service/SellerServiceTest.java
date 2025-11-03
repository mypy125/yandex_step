package com.mygitgor.seller_service;

import com.mygitgor.seller_service.domain.AccountStatus;
import com.mygitgor.seller_service.domain.Address;
import com.mygitgor.seller_service.domain.Seller;
import com.mygitgor.seller_service.domain.details.BankDetails;
import com.mygitgor.seller_service.dto.SellerAuthInfo;
import com.mygitgor.seller_service.dto.SellerDto;
import com.mygitgor.seller_service.dto.USER_ROLE;
import com.mygitgor.seller_service.mapping.SellerMapper;
import com.mygitgor.seller_service.repository.SellerRepository;
import com.mygitgor.seller_service.service.impl.sellerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SellerServiceTest {
    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private SellerMapper sellerMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private sellerServiceImpl sellerService;

    private Seller testSeller;
    private SellerDto sellerDto;
    private SellerAuthInfo authInfo;

    @BeforeEach
    void setUp() {
        testSeller = new Seller();
        testSeller.setId(UUID.randomUUID());
        testSeller.setSellerName("John Seller");
        testSeller.setEmail("john@seller.com");
        testSeller.setPassword("encodedPass");
        testSeller.setMobile("099123456");
        testSeller.setRole(USER_ROLE.ROLE_SELLER);
        testSeller.setEmailVerified(false);
        testSeller.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
        testSeller.setBankDetails(new BankDetails());
        testSeller.setBankDetails(new BankDetails());
        testSeller.setPickupAddress(new Address());

        sellerDto = SellerDto.builder()
                .id(testSeller.getId())
                .sellerName("John Seller")
                .email("john@seller.com")
                .password("plainPass")
                .mobile("099123456")
                .role(USER_ROLE.ROLE_SELLER)
                .build();

        authInfo = new SellerAuthInfo();
        authInfo.setId(testSeller.getId());
        authInfo.setEmail(testSeller.getEmail());
        authInfo.setFullName(testSeller.getSellerName());
        authInfo.setRole(testSeller.getRole());
        authInfo.setEmailVerified(testSeller.getEmailVerified());
    }

    @Test
    public void existsByEmail_ShouldReturnTrue_WhenSellerExists() {
        when(sellerRepository.findByEmail("john@seller.com")).thenReturn(Optional.of(testSeller));

        boolean result = sellerService.existsByEmail("john@seller.com");

        assertTrue(result);
        verify(sellerRepository).findByEmail("john@seller.com");
    }

}
