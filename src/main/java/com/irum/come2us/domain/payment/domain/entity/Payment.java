package com.irum.come2us.domain.payment.domain.entity;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.payment.application.client.dto.TossPaymentsResponse;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentCorp;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentMethod;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_payment SET deleted_at = NOW() WHERE payment_id=?")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor
@Entity
@Getter
@Table(name = "p_payment")
public class Payment extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "payment_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private int amount;

    private int totalDiscountAmount;

    private String tossPaymentKey;

    private String tossOrderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentCorp paymentCorp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void updateStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void updateToPaid(PaymentStatus ps, TossPaymentsResponse res) {
        this.paymentStatus = ps;
        this.tossPaymentKey = res.paymentKey();
        this.tossOrderId = res.orderId();
        this.paymentMethod = PaymentMethod.from(res.method());
    }
}
