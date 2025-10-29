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
@Table(name = "p_product_image")
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
    private boolean isDefault;

    @Builder(access = AccessLevel.PRIVATE)
    private ProductImage(Product product, String imageUrl, Boolean isDefault) {
        this.product = product;
        this.imageUrl = imageUrl;
        this.isDefault = (isDefault != null) && isDefault;
    }

    public static ProductImage create(Product product, String imageUrl, Boolean isDefault) {
        return ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .isDefault(isDefault)
                .build();
    }

    /** 이미지 URL 변경 */
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /** 대표 이미지로 설정 */
    public void markAsDefault() {
        this.isDefault = true;
    }

    /** 대표 이미지 해제 */
    public void unmarkAsDefault() {
        this.isDefault = false;
    }
}
