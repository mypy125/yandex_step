package com.mygitgor.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private UUID id;
    private UUID productId;
    private String size;
    private int quantity;
    private Integer mrpPrice;
    private Integer sellingPrice;
    private UUID userId;
}
