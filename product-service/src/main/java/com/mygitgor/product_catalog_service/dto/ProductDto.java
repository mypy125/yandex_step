package com.mygitgor.product_catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private UUID id;
    private String title;
    private String description;
    private Integer quantity;
    private Integer mrpPrice;
    private Integer sellingPrice;
    private Integer discountPercent;
    private String color;
    private List<String> images;
    private Integer numRatings;
    private String size;
    private UUID sellerId;
    private UUID categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
