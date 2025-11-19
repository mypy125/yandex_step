package com.mygitgor.order_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCanceledEvent implements Serializable {
    private UUID orderId;
    private UUID sellerId;
    private String userId;
    private Integer totalAmount;
    private LocalDateTime canceledAt;
}
