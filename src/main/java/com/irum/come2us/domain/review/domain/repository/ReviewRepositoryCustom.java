package com.irum.come2us.domain.review.domain.repository;

import com.irum.come2us.domain.review.domain.entity.Review;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {
    Page<Review> findReviewsByCondition(UUID memberId, UUID productId, Pageable pageable);
}
