package com.irum.come2us.domain.order.domain.entity;

import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="p_order_datail")
public class OrderDetail {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name="order_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID orderDetailId;

    private String optionName;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private OrderStatus orderStatusIndi;

    private Integer tracking_number;

    private LocalDateTime arrivedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Order order;


    // TODO: 옵션, 상품 many to one
}
