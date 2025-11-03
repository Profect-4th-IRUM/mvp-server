package com.irum.come2us.domain.review.domain.entity;

import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Table(name = "p_review_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
public class ReviewImage extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "review_image_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Builder(access = AccessLevel.PRIVATE)
    private ReviewImage(String imageUrl, Review review) {
        this.imageUrl = imageUrl;
        this.review = review;
    }

    public static ReviewImage create(String imageUrl, Review review) {
        return ReviewImage.builder().imageUrl(imageUrl).review(review).build();
    }
}
