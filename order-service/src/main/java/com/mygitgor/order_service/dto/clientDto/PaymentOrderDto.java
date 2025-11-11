package com.mygitgor.order_service.dto.clientDto;

import com.mygitgor.order_service.dto.PaymentMethod;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class PaymentOrderDto {
    private Long amount;
    private PaymentMethod paymentMethod;
    private String userId;
    private Set<UUID> orderIds;
}
