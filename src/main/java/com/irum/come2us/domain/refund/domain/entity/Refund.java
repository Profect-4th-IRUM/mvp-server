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
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_refund SET deleted_at = NOW() WHERE refund_id=?")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor
@Builder
@Table(name = "p_refund")
public class Refund extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "refund_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID refundId;

    @Enumerated(EnumType.STRING)
    private RefundReason reason;

    private String description;

    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus refundStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
