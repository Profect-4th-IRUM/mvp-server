package com.irum.come2us.domain.refund.domain.entity;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundReason;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "p_refund")
public class Refund {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name="refund_id",columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID refund_id;

    private RefundReason reason;

    private String description;

    private Integer price;

    private RefundStatus refundStatus;

    @OneToOne
    @JoinColumn(name="order_id")
    private Order order;

}
