package com.mygitgor.product_service.dto;

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
    private String brand;
    private String sku;
    private String weight;
    private String dimensions;
    private Boolean active;
    private Boolean approved;
    private Boolean inStock;
    private Boolean featured;
    private Integer minOrderQuantity;
    private Integer maxOrderQuantity;
    private String material;
    private String warranty;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isAvailable() {
        return Boolean.TRUE.equals(active) &&
                Boolean.TRUE.equals(approved) &&
                Boolean.TRUE.equals(inStock);
    }

    public boolean canOrderQuantity(Integer requestedQuantity) {
        if (requestedQuantity == null || requestedQuantity <= 0) {
            return false;
        }
        return requestedQuantity >= minOrderQuantity &&
                requestedQuantity <= maxOrderQuantity &&
                (quantity == null || requestedQuantity <= quantity);
    }
}
