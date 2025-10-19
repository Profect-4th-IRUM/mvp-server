package com.irum.come2us.domain.review.domain.entity;

import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Table(name = "p_review_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_review_image SET deleted_at = NOW() WHERE review_image_id = ?")
@Where(clause = "deleted_at IS NULL")
public class ReviewImage extends BaseEntity {

    @Id
    @Column(name = "review_image_id", length = 50, updatable = false, nullable = false)
    private String id;

    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Builder(access = AccessLevel.PRIVATE)
    private ReviewImage(String id, String imageUrl, Review review) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.review = review;
    }

    public static ReviewImage create(String id, String imageUrl, Review review) {
        return ReviewImage.builder().id(id).imageUrl(imageUrl).review(review).build();
    }

    public void changeReview(Review review) {
        this.review = review;
    }
}
