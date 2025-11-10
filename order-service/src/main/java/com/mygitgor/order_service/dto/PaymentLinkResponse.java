package com.mygitgor.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentLinkResponse {
    private String payment_link_url;
    private String payment_link_id;
    private LocalDateTime expiresAt;
    private String error;

    public static PaymentLinkResponse success(String url, String paymentId, LocalDateTime expiresAt) {
        return PaymentLinkResponse.builder()
                .payment_link_url(url)
                .payment_link_id(paymentId)
                .expiresAt(expiresAt)
                .build();
    }

    public static PaymentLinkResponse error(String errorMessage) {
        return PaymentLinkResponse.builder()
                .error(errorMessage)
                .build();
    }
}
