package com.irum.come2us.domain.product.domain.entity;

import com.irum.come2us.domain.store.domain.entity.Store;
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
@Table(name = "p_product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_product SET deleted_at = NOW() WHERE product_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Product extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "product_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "detail_description", columnDefinition = "TEXT", nullable = false)
    private String detailDescription;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "avg_rating")
    private Double avgRating;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "price", nullable = false)
    private int price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionGroup> optionGroups = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder(access = AccessLevel.PRIVATE)
    private Product(
            Store store,
            String name,
            String description,
            boolean isPublic,
            String detailDescription,
            int price) {
        this.store = store;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.detailDescription = detailDescription;
        this.price = price;
    }

    public static Product createProduct(
            Store store,
            String name,
            String description,
            String detailDescription,
            int price,
            boolean isPublic) {
        return Product.builder()
                .store(store)
                .name(name)
                .description(description)
                .detailDescription(detailDescription)
                .price(price)
                .isPublic(isPublic)
                .build();
    }

    public void updateProduct(
            String name,
            String description,
            String detailDescription,
            int price,
            boolean isPublic) {
        this.name = name;
        this.description = description;
        this.detailDescription = detailDescription;
        this.price = price;
        this.isPublic = isPublic;
    }

    public void updateRating(Double avgRating, Integer reviewCount) {
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
    }

    public void addOptionGroup(ProductOptionGroup group) {
        optionGroups.add(group);
    }

    // TODO: Category, 이미지 매핑
}
