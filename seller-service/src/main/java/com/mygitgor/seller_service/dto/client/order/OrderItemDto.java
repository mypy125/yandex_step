package com.mygitgor.seller_service.dto.client.order;

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
    private Integer quantity;
    private Integer mrpPrice;
    private Integer sellingPrice;
    private UUID userId;
}
