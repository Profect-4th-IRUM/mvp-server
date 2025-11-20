package com.irum.come2us.domain.refund.domain.entity;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundReason;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
@NoArgsConstructor
@Builder
@Getter
@Table(name = "p_refund")
public class Refund extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "refund_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID refundId;

    @Enumerated(EnumType.STRING)
    private RefundReason reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus refundStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public static Refund create(RefundReason reason, String description, Order order) {
        return Refund.builder()
                .reason(reason)
                .description(description)
                .price(order.getPayment().getAmount())
                .refundStatus(RefundStatus.REQUESTED)
                .order(order)
                .build();
    }

    public void updateStatus(RefundStatus newStatus) {
        this.refundStatus = newStatus;
    }
}
