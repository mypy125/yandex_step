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
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.persistence.EntityNotFoundException;
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
    void existsByEmail_ShouldReturnTrue_WhenSellerExists() {
        when(sellerRepository.findByEmail("john@seller.com")).thenReturn(Optional.of(testSeller));

        boolean result = sellerService.existsByEmail("john@seller.com");

        assertTrue(result);
        verify(sellerRepository).findByEmail("john@seller.com");
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenSellerNotExists() {
        when(sellerRepository.findByEmail("notfound@seller.com")).thenReturn(Optional.empty());

        boolean result = sellerService.existsByEmail("notfound@seller.com");

        assertFalse(result);
    }

    @Test
    void getAuthInfo_ShouldReturnAuthInfo_WhenSellerExists() {
        when(sellerRepository.findByEmail("john@seller.com")).thenReturn(Optional.of(testSeller));
        when(sellerMapper.toSellerAuthInfo(testSeller)).thenReturn(authInfo);

        SellerAuthInfo result = sellerService.getAuthInfo("john@seller.com");

        assertNotNull(result);
        assertEquals("john@seller.com", result.getEmail());
        assertEquals("John Seller", result.getFullName());
        verify(sellerRepository).findByEmail("john@seller.com");
        verify(sellerMapper).toSellerAuthInfo(testSeller);
    }

    @Test
    void getAuthInfo_ShouldThrowException_WhenSellerNotFound() {
        when(sellerRepository.findByEmail("missing@seller.com")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                sellerService.getAuthInfo("missing@seller.com")
        );

        assertEquals("seller not found with email missing@seller.com", ex.getMessage());
    }

    @Test
    void createSeller_ShouldCreateSeller_WhenEmailNotExists() {
        when(sellerRepository.findByEmail(sellerDto.getEmail())).thenReturn(Optional.empty());
        when(sellerMapper.toSeller(sellerDto)).thenReturn(testSeller);
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);
        when(sellerMapper.toSellerDto(testSeller)).thenReturn(sellerDto);

        SellerDto result = sellerService.createSeller(sellerDto);

        assertNotNull(result);
        assertEquals("john@seller.com", result.getEmail());
        verify(passwordEncoder).encode("plainPass");
        verify(sellerRepository).save(any(Seller.class));
        verify(sellerMapper).toSellerDto(testSeller);
    }

    @Test
    void createSeller_ShouldThrowDuplicateRequestException_WhenEmailExists() {
        when(sellerRepository.findByEmail(sellerDto.getEmail())).thenReturn(Optional.of(testSeller));

        DuplicateRequestException ex = assertThrows(DuplicateRequestException.class, () ->
                sellerService.createSeller(sellerDto)
        );

        assertEquals("seller with email 'john@seller.com' already exists", ex.getMessage());
        verify(sellerRepository, never()).save(any());
    }

    @Test
    void verifyEmail_ShouldSetEmailVerifiedTrue_WhenNotVerified() {
        testSeller.setEmailVerified(false);
        when(sellerRepository.findByEmail("john@seller.com")).thenReturn(Optional.of(testSeller));

        sellerService.verifyEmail("john@seller.com");

        assertTrue(testSeller.getEmailVerified());
        verify(sellerRepository).save(testSeller);
    }

    @Test
    void verifyEmail_ShouldNotSave_WhenAlreadyVerified() {
        testSeller.setEmailVerified(true);
        when(sellerRepository.findByEmail("john@seller.com")).thenReturn(Optional.of(testSeller));

        sellerService.verifyEmail("john@seller.com");

        verify(sellerRepository, never()).save(any());
    }

    @Test
    void getSellerByEmail_ShouldReturnSeller_WhenExists() {
        when(sellerRepository.findByEmail("john@seller.com")).thenReturn(Optional.of(testSeller));

        Seller result = sellerService.getSellerByEmail("john@seller.com");

        assertNotNull(result);
        assertEquals("john@seller.com", result.getEmail());
    }

    @Test
    void getSellerByEmail_ShouldThrowException_WhenNotFound() {
        when(sellerRepository.findByEmail("missing@seller.com")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                sellerService.getSellerByEmail("missing@seller.com")
        );

        assertEquals("seller not found with email missing@seller.com", ex.getMessage());
    }

    @Test
    void updateSeller_ShouldUpdateSeller_WhenExists() {
        when(sellerRepository.findByEmail("john@seller.com")).thenReturn(Optional.of(testSeller));
        doNothing().when(sellerMapper).updateSellerFromDto(sellerDto, testSeller);
        when(sellerRepository.save(testSeller)).thenReturn(testSeller);
        when(sellerMapper.toSellerDto(testSeller)).thenReturn(sellerDto);

        SellerDto result = sellerService.updateSeller("john@seller.com", sellerDto);

        assertNotNull(result);
        assertEquals("john@seller.com", result.getEmail());
        verify(sellerMapper).updateSellerFromDto(sellerDto, testSeller);
        verify(sellerRepository).save(testSeller);
    }

}
