package com.mygitgor.product_service.dto;

import com.mygitgor.product_service.domain.ReviewStatus;
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
public class ReviewDto {
    private UUID id;
    private String reviewText;
    private Double rating;
    private List<String> productImages;
    private String userId;
    private String userName;
    private String userAvatar;
    private String title;
    private Boolean verifiedPurchase;
    private Integer likes;
    private Integer dislikes;
    private Boolean approved;
    private Boolean edited;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ReviewStatus status;
    private Integer helpfulCount;
    private UUID productId;
    private String productTitle;

    public boolean isVisible() {
        return status == ReviewStatus.ACTIVE && Boolean.TRUE.equals(approved);
    }

    public boolean isHelpfulByCurrentUser(String currentUserId) {
        return false;
    }
}
