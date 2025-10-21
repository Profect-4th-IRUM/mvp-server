package com.irum.come2us.domain.order.domain.entity;

import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.*;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_order_detail SET deleted_at = NOW() WHERE order_detail_id=?")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor
@Table(name = "p_order_detail")
public class OrderDetail extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(
            name = "order_detail_id",
            columnDefinition = "uuid",
            nullable = false,
            updatable = false)
    private UUID orderDetailId;

    private String optionName;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatusIndi;

    private Integer trackingNumber;

    private LocalDateTime arrivedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Order order;

    // TODO: 옵션 many to one

    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "option_value_id")
    //    private OptionValue optionValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public void updateStatusToPreparing() {
        this.orderStatusIndi = OrderStatus.PREPARING;
    }

    public void updateStatusToShipped() {
        this.orderStatusIndi = OrderStatus.SHIPPED;
    }

    public void updateStatusToDelivered() {
        this.orderStatusIndi = OrderStatus.DELIVERED;
    }
}
