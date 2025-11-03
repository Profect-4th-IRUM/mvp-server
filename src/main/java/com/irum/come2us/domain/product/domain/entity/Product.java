package com.irum.come2us.domain.product.domain.entity;

import com.irum.come2us.domain.category.domain.entity.Category;
import com.irum.come2us.domain.store.domain.entity.Store;
import com.irum.come2us.global.domain.BaseEntity;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.CategoryErrorCode;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder(access = AccessLevel.PRIVATE)
    private Product(
            Store store,
            Category category,
            String name,
            String description,
            boolean isPublic,
            String detailDescription,
            int price) {
        this.store = store;
        this.category = category;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.detailDescription = detailDescription;
        this.price = price;
    }

    public static Product createProduct(
            Store store,
            Category category,
            String name,
            String description,
            String detailDescription,
            int price,
            boolean isPublic) {

        if (category.getDepth() != 3) {
            throw new CommonException(CategoryErrorCode.INVALID_CATEGORY_DEPTH);
        }

        return Product.builder()
                .store(store)
                .category(category)
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

    public void updateCategory(Category category) {
        if (category.getDepth() != 3) {
            throw new CommonException(CategoryErrorCode.INVALID_CATEGORY_DEPTH);
        }
        this.category = category;
    }

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    // TODO: 리뷰 매핑
}
