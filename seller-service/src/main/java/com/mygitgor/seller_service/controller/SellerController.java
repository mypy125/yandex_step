package com.mygitgor.seller_service.controller;

import com.mygitgor.seller_service.domain.Seller;
import com.mygitgor.seller_service.dto.SellerAuthInfo;
import com.mygitgor.seller_service.dto.SellerDto;
import com.mygitgor.seller_service.mapping.SellerMapper;
import com.mygitgor.seller_service.service.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;
    private final SellerMapper sellerMapper;

    @GetMapping("/{email}/exists")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email
    ) {
        try{
            boolean exists = sellerService.existsByEmail(email);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking seller existence: {}", email, e);
            return ResponseEntity.ok(false);
        }

    }

    @GetMapping("/{email}/auth-info")
    public ResponseEntity<SellerAuthInfo> getAuthInfo(@PathVariable String email
    ) {
        try{
            SellerAuthInfo authInfo = sellerService.getAuthInfo(email);
            return ResponseEntity.ok(authInfo);
        } catch (Exception e) {
            log.error("Error getting seller auth info: {}", email, e);
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping
    public ResponseEntity<SellerDto> createSeller(@RequestBody SellerDto request
    ) {
        try{
            SellerDto createdSeller = sellerService.createSeller(request);
            log.info("Seller created successfully: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSeller);
        } catch (Exception e) {
            log.error("Failed to create seller: {}", request.getEmail(), e);
            return ResponseEntity.badRequest().build();
        }

    }

    @PatchMapping("/{email}/verify")
    public ResponseEntity<Boolean> verifyEmail(@PathVariable String email
    ) {
        try{
            sellerService.verifyEmail(email);
            log.info("Seller email verified: {}", email);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to verify seller email: {}", email, e);
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/{email}")
    public ResponseEntity<SellerDto> getSellerByEmail(@PathVariable String email
    ) {
        try{
            Seller seller = sellerService.getSellerByEmail(email);
            return ResponseEntity.ok(sellerMapper.toSellerDto(seller));
        } catch (Exception e) {
            log.error("Error getting seller: {}", email, e);
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/{email}")
    public ResponseEntity<SellerDto> updateSeller(@PathVariable String email,
                                               @RequestBody SellerDto seller
    ) {
        try{
            SellerDto updatedSeller = sellerService.updateSeller(email, seller);
            return ResponseEntity.ok(updatedSeller);
        } catch (Exception e) {
            log.error("Error getting seller: {}", email, e);
            return ResponseEntity.badRequest().build();
        }

    }
}
