package com.irum.come2us.domain.product.domain.entity;

import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "p_product_option_group")
@SQLDelete(sql = "UPDATE p_product_option_group SET deleted_at = NOW() WHERE option_group_id = ?")
@Where(clause = "deleted_at IS NULL")
public class ProductOptionGroup extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "option_group_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionValue> optionValues = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private ProductOptionGroup(Product product, String name) {
        this.product = product;
        this.name = name;
    }

    public static ProductOptionGroup createOptionGroup(Product product, String name) {
        return ProductOptionGroup.builder().product(product).name(name).build();
    }

    public void addOptionValue(ProductOptionValue value) {
        optionValues.add(value);
        value.setOptionGroup(this);
    }

    public void updateOptionGroupName(String name) {
        this.name = name;
    }
}
