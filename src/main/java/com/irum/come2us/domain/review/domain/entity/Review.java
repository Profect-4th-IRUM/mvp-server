package com.irum.come2us.domain.review.domain.entity;

import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.*;

@Getter
@Entity
@Table(name = "p_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_review SET deleted_at = NOW() WHERE review_id = ?")
@Where(clause = "deleted_at IS NULL")
@Check(constraints = "rate BETWEEN 1 AND 5")
public class Review extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "review_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "rate", nullable = false, columnDefinition = "SmallInt")
    private Short rate;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Builder(access = AccessLevel.PRIVATE)
    private Review(String content, Integer rate, Long memberId, UUID productId) {
        this.content = content;
        this.rate = rate != null ? rate.shortValue() : null;
        this.memberId = memberId;
        this.productId = productId;
    }

    public static Review createReview(String content, Integer rate, Long memberId, UUID productId) {
        return Review.builder()
                .content(content)
                .rate(rate)
                .memberId(memberId)
                .productId(productId)
                .build();
    }

    public void updateReview(String content, Integer rate) {
        if (content != null) this.content = content;
        if (rate != null) this.rate = rate.shortValue();
    }
}
