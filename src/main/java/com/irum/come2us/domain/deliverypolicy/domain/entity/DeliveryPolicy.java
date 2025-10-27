package com.irum.come2us.domain.deliverypolicy.domain.entity;

import com.irum.come2us.domain.store.domain.entity.Store;
import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
@Table(name = "p_delivery_policy")
@SQLDelete(sql = "UPDATE p_delivery_policy SET deleted_at = NOW() WHERE delivery_policy_id = ?")
@Where(clause = "deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryPolicy extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "delivery_policy_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "default_delivery_fee", nullable = false)
    @Min(0)
    private int defaultDeliveryFee;

    @Column(name = "minimum_quantity", nullable = false)
    @Min(1)
    private int minQuantity;

    @Column(name = "minimum_amount", nullable = false)
    @Min(0)
    private int minAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    public void setStore(Store store) {
        this.store = store;
        if (store != null && store.getDeliveryPolicy() != this) {
            store.setDeliveryPolicy(this);
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    private DeliveryPolicy(int defaultDeliveryFee, int minQuantity, int minAmount, Store store) {
        this.defaultDeliveryFee = defaultDeliveryFee;
        this.minQuantity = minQuantity;
        this.minAmount = minAmount;
        this.store = store;
    }

    public static DeliveryPolicy createPolicy(
            int defaultDeliveryFee, int minQuantity, int minAmount, Store store) {
        return DeliveryPolicy.builder()
                .defaultDeliveryFee(defaultDeliveryFee)
                .minQuantity(minQuantity)
                .minAmount(minAmount)
                .store(store)
                .build();
    }

    public void updateFee(int fee) {
        this.defaultDeliveryFee = fee;
    }

    public void updateQuantity(int qty) {
        this.minQuantity = qty;
    }

    public void updateAmount(int amt) {
        this.minAmount = amt;
    }
}
