package com.mygitgor.seller_service.dto.client;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    @NotBlank
    private String title;
    private String description;
    private int quantity;
    private int mrpPrice;
    private int sellingPrice;
    private String color;
    private List<String> images;
    private String category;
    private String category2;
    private String category3;
    private String size;
    private String categoryName;
    private String brand;
    private String sku;
    private String weight;
    private String dimensions;
    private Boolean active;
    private Boolean inStock;
    private Boolean featured;
    private Integer minOrderQuantity;
    private Integer maxOrderQuantity;
    private String material;
    private String warranty;
}
