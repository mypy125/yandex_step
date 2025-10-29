package com.mygitgor.seller_service.controller;

import com.mygitgor.seller_service.domain.Seller;
import com.mygitgor.seller_service.dto.SellerAuthInfo;
import com.mygitgor.seller_service.dto.SellerCreateRequest;
import com.mygitgor.seller_service.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @GetMapping("/{email}/exists")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email)
    {
        boolean exists = sellerService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{email}/auth-info")
    public ResponseEntity<SellerAuthInfo> getAuthInfo(@PathVariable String email)
    {
        SellerAuthInfo authInfo = sellerService.getAuthInfo(email);
        return ResponseEntity.ok(authInfo);
    }

    @PostMapping
    public ResponseEntity<Void> createSeller(@RequestBody SellerCreateRequest request)
    {
        sellerService.createSeller(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{email}/verify")
    public ResponseEntity<Void> verifyEmail(@PathVariable String email)
    {
        sellerService.verifyEmail(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{email}")
    public ResponseEntity<Seller> getSellerByEmail(@PathVariable String email)
    {
        Seller seller = sellerService.getSellerByEmail(email);
        return ResponseEntity.ok(seller);
    }

    @PutMapping("/{email}")
    public ResponseEntity<Seller> updateSeller(@PathVariable String email,
                                               @RequestBody Seller seller)
    {
        Seller updatedSeller = sellerService.updateSeller(email, seller);
        return ResponseEntity.ok(updatedSeller);
    }
}
