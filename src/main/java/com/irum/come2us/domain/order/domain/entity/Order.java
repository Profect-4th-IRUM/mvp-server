package com.irum.come2us.domain.order.domain.entity;

import com.irum.come2us.domain.deliveryaddress.domain.entity.DeliveryAddress;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.payment.domain.entity.Payment;
import com.irum.come2us.domain.store.domain.entity.Store;
import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
@Table(name = "p_order")
public class Order extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "order_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID orderId;

    @Column(nullable = false)
    private String orderNum;

    private Integer totalPrice;

    private Integer deliveryFee;

    private String deliveryRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatusAll;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // 한 주문당 여러 상품이 담겨있기 때문에 List 추가

    public void updateOrderStatus(OrderStatus os) {
        this.orderStatusAll = os;
    }
}
