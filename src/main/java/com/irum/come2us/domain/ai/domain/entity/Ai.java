package com.irum.come2us.domain.ai.domain.entity;

import com.irum.come2us.domain.product.domain.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "p_ai")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Ai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 연관관계 편의 메서드
    //    public void setProduct(Product product) {
    //        this.product = product;
    //
    //        // Product ↔ Ai 양방향
    //        if (!product.getAiList().contains(this)) {
    //            product.getAiList().add(this);
    //
    //        // Ai → Product 단방향
    //        product.getAiList().add(this);
    //    }
    //
    //    public static Ai of(String question, String answer, Product product) {
    //        Ai ai = Ai.builder()
    //                .question(question)
    //                .answer(answer)
    //                .build();
    //        ai.setProduct(product); // 연관관계 편의 메서드 사용
    //        return ai;
    //    }
}
