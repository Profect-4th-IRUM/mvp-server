package com.irum.come2us.domain.review.domain.entity;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Table(name = "p_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_review SET deleted_at = NOW() WHERE review_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Review extends BaseEntity {

    @Id
    @Column(name = "review_id", length = 50, updatable = false, nullable = false)
    private String id;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "rate", nullable = false)
    private Integer rate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder(access = AccessLevel.PRIVATE)
    private Review(String id, String content, Integer rate, Member member, Product product) {
        this.id = id;
        this.content = content;
        this.rate = rate;
        this.member = member;
        this.product = product;
    }

    public static Review createReview(String id, String content, Integer rate, Member member, Product product) {
        return Review.builder()
                .id(id)
                .content(content)
                .rate(rate)
                .member(member)
                .product(product)
                .build();
    }

    public void updateReview(String content, Integer rate) {
        if (content != null) this.content = content;
        if (rate != null) this.rate = rate;
    }
}