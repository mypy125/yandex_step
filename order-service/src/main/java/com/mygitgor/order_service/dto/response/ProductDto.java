package com.mygitgor.order_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private UUID id;
    private String title;
    private String description;
    private Integer mrpPrice;
    private Integer sellingPrice;
    private Integer discountPercent;
    private String color;
    private List<String> images;
    private String size;
    private Integer quantity;
    private UUID sellerId;
}
