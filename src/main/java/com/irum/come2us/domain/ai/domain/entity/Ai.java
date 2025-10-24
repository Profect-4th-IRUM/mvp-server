package com.irum.come2us.domain.ai.domain.entity;

import com.irum.come2us.domain.product.domain.entity.Product;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Entity
@Table(name = "p_ai")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Ai {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "ai_id", updatable = false, nullable = false)
    private UUID aiId;

    @Lob
    @Column(nullable = false)
    private String question;

    @Lob
    @Column(nullable = false)
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

    // 정적 팩토리 매서드
    public static Ai create(String question, String answer, Product product) {
        return Ai.builder().question(question).answer(answer).product(product).build();
    }
}
