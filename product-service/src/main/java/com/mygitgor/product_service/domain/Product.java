package com.mygitgor.product_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
@ToString(callSuper = true, exclude = {"category","reviews"})
@EqualsAndHashCode(callSuper = true, exclude = {"category","reviews"})
public class Product extends BaseEntity {
    private String title;
    private String description;
    private Integer quantity;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    private Integer mrpPrice;
    private Integer sellingPrice;
    private Integer discountPercent;

    private String color;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> images = new ArrayList<>();

    private Integer numRatings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private UUID sellerId;

    //    @ElementCollection
    private String size;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Review>reviews = new ArrayList<>();

    private String brand;
    private String sku;

    private String weight;
    private String dimensions;

    private Boolean active = true;
    private Boolean approved = false;

    private Integer minOrderQuantity = 1;
    private Integer maxOrderQuantity = 10;

    private String material;
    private String warranty;

    private String metaTitle;
    private String metaDescription;
    private List<String> tags = new ArrayList<>();

    private Double averageRating = 0.0;
    private Integer reviewCount = 0;

    private Boolean featured = false;
    private Boolean inStock = true;

    private String shippingInfo;
    private String returnPolicy;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.inStock = this.quantity != null && this.quantity > 0;

        if (this.discountPercent == null && this.mrpPrice != null && this.sellingPrice != null && this.mrpPrice > 0) {
            double discount = ((double) (this.mrpPrice - this.sellingPrice) / this.mrpPrice) * 100;
            this.discountPercent = (int) Math.round(discount);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.inStock = this.quantity != null && this.quantity > 0;

        if (this.sku == null) {
            this.sku = "SKU_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

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
                requestedQuantity <= quantity;
    }

    public void updateStock(Integer soldQuantity) {
        if (soldQuantity != null && soldQuantity > 0) {
            this.quantity -= soldQuantity;
            this.inStock = this.quantity > 0;
        }
    }

    public void addReview(Review review) {
        this.reviews.add(review);
        review.setProduct(this);
        updateRatingStats();
    }

    public void removeReview(Review review) {
        this.reviews.remove(review);
        review.setProduct(null);
        updateRatingStats();
    }

    private void updateRatingStats() {
        if (!this.reviews.isEmpty()) {
            this.reviewCount = this.reviews.size();
            this.averageRating = this.reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
        } else {
            this.reviewCount = 0;
            this.averageRating = 0.0;
        }
    }

    public static Product create(String title, String description, Integer quantity,
                                 Integer mrpPrice, Integer sellingPrice, UUID sellerId,
                                 String brand, String color, String size) {
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setMrpPrice(mrpPrice);
        product.setSellingPrice(sellingPrice);
        product.setSellerId(sellerId);
        product.setBrand(brand);
        product.setColor(color);
        product.setSize(size);
        product.setActive(true);
        product.setInStock(quantity != null && quantity > 0);

        return product;
    }
}