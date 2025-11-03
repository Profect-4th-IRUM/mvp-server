package com.irum.come2us.domain.review.domain.entity;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Table(name = "p_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@Check(constraints = "rate BETWEEN 1 AND 5")
public class Review extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "review_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "rate", nullable = false, columnDefinition = "SMALLINT")
    private Short rate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder
    private Review(String content, Short rate, Member member, Product product) {
        this.content = content;
        this.rate = rate;
        this.member = member;
        this.product = product;
    }

    public static Review createReview(String content, Short rate, Member member, Product product) {
        return Review.builder().content(content).rate(rate).member(member).product(product).build();
    }

    public void updateReview(String content, Short rate) {
        if (content != null) {
            this.content = content;
        }
        if (rate != null) {
            this.rate = rate;
        }
    }
}
