package com.irum.come2us.domain.order.domain.entity;

import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.payment.domain.entity.Payment;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentMethod;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_order SET deleted_at = NOW() WHERE order_id=?")
@SQLRestriction("deleted_at is null")
@Table(name = "p_order")
public class Order {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name="order_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID orderId;

    @Column(nullable = false)
    private Integer orderNum;

    private Integer totalPrice;

    private Integer deliveryFee;

    private String deliveryRequest;

    private OrderStatus orderStatusAll;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @OneToOne
    @JoinColumn(name="payment_id")
    private Payment payment;

    //TODO: 상점, 회원, 배송지주소 Many to one

}
