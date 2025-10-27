package com.irum.come2us.domain.review.domain.repository;

import com.irum.come2us.domain.review.domain.entity.Review;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findAllByMember_MemberId(Long memberId, Pageable pageable);

    Page<Review> findAllByProduct_Id(UUID productId, Pageable pageable);

    @Query(
            """
        SELECT COALESCE(AVG(r.rate), 0)
        FROM Review r
        WHERE r.product.id = :productId
        """)
    Double findAverageByProductId(@Param("productId") UUID productId);

    @Query(
            """
        SELECT COUNT(r)
        FROM Review r
        WHERE r.product.id = :productId
        """)
    Integer findCountByProductId(@Param("productId") UUID productId);
}
