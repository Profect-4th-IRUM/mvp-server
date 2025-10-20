package com.irum.come2us.domain.cart.domain.entity;

// import com.irum.come2us.domain.option.domain.entity.OptionValue;
import com.irum.come2us.domain.member.domain.entity.Member;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Table(name = "p_cart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "cart_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // TODO: OptionValue 엔티티 생성 후 주석 해제
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "option_value_id", nullable = false)
    // private OptionValue optionValue;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Builder(access = AccessLevel.PRIVATE)
    private Cart(Member member, /* OptionValue optionValue, */ int quantity) {
        this.member = member;
        // this.optionValue = optionValue;
        this.quantity = quantity;
    }

    public static Cart createCart(Member member, /* OptionValue optionValue, */ int quantity) {
        return Cart.builder()
                .member(member)
                // .optionValue(optionValue)
                .quantity(quantity)
                .build();
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
