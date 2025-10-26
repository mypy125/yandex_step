package com.mygitgor.user_management_service.controller;

import com.mygitgor.user_management_service.dto.VerificationRequest;
import com.mygitgor.user_management_service.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/verification")
@RequiredArgsConstructor
public class VerificationController {
    private final VerificationService verificationService;

    @PostMapping
    public ResponseEntity<Void> saveUserVerificationCode(@RequestBody VerificationRequest request)
    {
        verificationService.saveVerificationCode(request.email(),request.otp());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify")
    public ResponseEntity<Boolean> verifyOtp(@RequestParam String email, @RequestParam String otp)
    {
        boolean isValid = verificationService.verifyOtp(email, otp);
        return ResponseEntity.ok(isValid);
    }
}
