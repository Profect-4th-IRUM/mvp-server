package com.irum.come2us.domain.cart.domain.entity;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.product.domain.entity.ProductOptionValue;
import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
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
@Table(name = "p_cart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
public class Cart extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "cart_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_value_id", nullable = false)
    private ProductOptionValue optionValue;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Builder(access = AccessLevel.PRIVATE)
    private Cart(Member member, ProductOptionValue optionValue, int quantity) {
        this.member = member;
        this.optionValue = optionValue;
        this.quantity = quantity;
    }

    public static Cart createCart(Member member, ProductOptionValue optionValue, int quantity) {
        return Cart.builder().member(member).optionValue(optionValue).quantity(quantity).build();
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
