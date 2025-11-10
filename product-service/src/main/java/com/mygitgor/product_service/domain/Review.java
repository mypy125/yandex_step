package com.mygitgor.product_service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reviews")
@ToString(callSuper = true, exclude = "product")
@EqualsAndHashCode(callSuper = true, exclude = "product")
public class Review extends BaseEntity {

    @Column(nullable = false)
    private String reviewText;

    @Column(nullable = false)
    private Double rating;

    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url")
    private List<String> productImages = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String userId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private String userName;
    private String userAvatar;

    private String title;
    private Boolean verifiedPurchase = false;

    private Integer likes = 0;
    private Integer dislikes = 0;

    private Boolean approved = true;
    private Boolean edited = false;

    private LocalDateTime updatedAt;
    private LocalDateTime moderatedAt;

    private String moderatorNotes;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.ACTIVE;

    @ElementCollection
    @CollectionTable(name = "review_helpful_votes", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "user_id")
    private Set<String> helpfulVotes = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.rating == null) {
            throw new IllegalArgumentException("Rating is required");
        }
        if (this.rating < 1.0 || this.rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.edited = true;
    }


    public void markHelpful(String userId) {
        if (userId != null && !helpfulVotes.contains(userId)) {
            helpfulVotes.add(userId);
            likes = helpfulVotes.size();
        }
    }

    public void unmarkHelpful(String userId) {
        if (userId != null && helpfulVotes.contains(userId)) {
            helpfulVotes.remove(userId);
            likes = helpfulVotes.size();
        }
    }

    public boolean isHelpfulByUser(String userId) {
        return userId != null && helpfulVotes.contains(userId);
    }

    public Integer getHelpfulCount() {
        return helpfulVotes.size();
    }

    public void approve(String moderatorNotes) {
        this.status = ReviewStatus.ACTIVE;
        this.approved = true;
        this.moderatedAt = LocalDateTime.now();
        this.moderatorNotes = moderatorNotes;
    }

    public void reject(String moderatorNotes) {
        this.status = ReviewStatus.REJECTED;
        this.approved = false;
        this.moderatedAt = LocalDateTime.now();
        this.moderatorNotes = moderatorNotes;
    }

    public void report() {
        this.status = ReviewStatus.REPORTED;
    }

    public boolean canBeEdited() {
        return status == ReviewStatus.ACTIVE || status == ReviewStatus.PENDING;
    }

    public boolean isVisible() {
        return status == ReviewStatus.ACTIVE && Boolean.TRUE.equals(approved);
    }

    public static Review create(String reviewText, Double rating, String userId,
                                String userName, Product product) {
        Review review = new Review();
        review.setReviewText(reviewText);
        review.setRating(rating);
        review.setUserId(userId);
        review.setUserName(userName);
        review.setProduct(product);
        review.setCreatedAt(LocalDateTime.now());
        review.setStatus(ReviewStatus.PENDING);

        return review;
    }

    public static Review createWithTitle(String title, String reviewText, Double rating,
                                         String userId, String userName, Product product) {
        Review review = create(reviewText, rating, userId, userName, product);
        review.setTitle(title);
        return review;
    }
}