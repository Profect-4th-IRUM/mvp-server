package com.irum.come2us.domain.review.domain.repository;

import com.irum.come2us.domain.review.domain.entity.Review;
import com.irum.come2us.domain.review.domain.entity.ReviewImage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, UUID> {

    List<ReviewImage> findAllByReview(Review review);
}
