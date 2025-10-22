package com.irum.come2us.domain.discount.domain.entity;

import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Table(name = "p_discount")
@SQLDelete(sql = "UPDATE p_discount SET deleted_at = NOW() WHERE discount_id = ?")
@Where(clause = "deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Discount extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "discount_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "amount", nullable = false)
    @Min(0)
    private int amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder(access = AccessLevel.PRIVATE)
    private Discount(String name, int amount, Product product) {
        this.name = name;
        this.amount = amount;
        this.product = product;
    }

    public static Discount create(String name, int amount, Product product) {
        return Discount.builder().name(name).amount(amount).product(product).build();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateAmount(int amt) {
        this.amount = amt;
    }
}
