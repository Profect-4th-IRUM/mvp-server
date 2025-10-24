package com.irum.come2us.domain.product.domain.entity;

import static lombok.AccessLevel.*;

import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_product_option_value")
@SQLDelete(sql = "UPDATE p_product_option_value SET deleted_at = NOW() WHERE option_value_id = ?")
@Where(clause = "deleted_at IS NULL")
public class ProductOptionValue extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "option_value_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_group_id", nullable = false)
    private ProductOptionGroup optionGroup;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "extra_price")
    private Integer extraPrice;

    @Builder(access = AccessLevel.PRIVATE)
    private ProductOptionValue(
            ProductOptionGroup optionGroup, String name, int stockQuantity, int extraPrice) {
        this.optionGroup = optionGroup;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.extraPrice = extraPrice;
    }

    public static ProductOptionValue createOptionValue(
            ProductOptionGroup group, String name, int stockQuantity, int extraPrice) {
        ProductOptionValue value =
                ProductOptionValue.builder()
                        .optionGroup(group)
                        .name(name)
                        .stockQuantity(stockQuantity)
                        .extraPrice(extraPrice)
                        .build();
        group.addOptionValue(value);
        return value;
    }

    public void decreaseStock(Integer quantity) {
        this.stockQuantity -= quantity;
    }

    protected void setOptionGroup(ProductOptionGroup optionGroup) {
        this.optionGroup = optionGroup;
    }

    public void increaseStock(Integer quantity) {
        this.stockQuantity += quantity;
    }

    public void updateOptionValue(String name, int stockQuantity, Integer extraPrice) {
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.extraPrice = extraPrice;
    }
}
