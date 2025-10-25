package com.irum.come2us.domain.product.domain.entity;

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
@Table(
        name = "p_product_image",
        uniqueConstraints = {
            // 하나의 상품당 대표 이미지는 하나만 존재
            @UniqueConstraint(columnNames = {"product_id", "is_default"})
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_product_image SET deleted_at = NOW() WHERE product_image_id = ?")
@Where(clause = "deleted_at IS NULL")
public class ProductImage extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "product_image_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @Builder(access = AccessLevel.PRIVATE)
    private ProductImage(Product product, String imageUrl, Boolean isDefault) {
        this.product = product;
        this.imageUrl = imageUrl;
        this.isDefault = isDefault != null ? isDefault : false;
    }

    public static ProductImage create(Product product, String imageUrl, Boolean isDefault) {
        return ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .isDefault(isDefault)
                .build();
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    }
}
