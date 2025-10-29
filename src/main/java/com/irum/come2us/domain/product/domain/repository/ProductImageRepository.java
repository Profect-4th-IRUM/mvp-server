package com.irum.come2us.domain.product.domain.repository;

import com.irum.come2us.domain.product.domain.entity.ProductImage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    /** 상품별 전체 이미지 조회 */
    List<ProductImage> findByProductId(UUID productId);

    /** 상품의 최신 이미지 1개 조회 (대표 삭제 시 대체용) */
    Optional<ProductImage> findTopByProductIdOrderByCreatedAtDesc(UUID productId);

    /** 해당 상품의 대표 이미지 존재 여부 확인 */
    boolean existsByProductIdAndIsDefaultTrue(UUID productId);

    boolean existsByProductId(UUID productId);
}
