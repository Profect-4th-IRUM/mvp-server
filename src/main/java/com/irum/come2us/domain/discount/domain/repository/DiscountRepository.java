package com.irum.come2us.domain.discount.domain.repository;

import com.irum.come2us.domain.discount.domain.entity.Discount;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, UUID> {}
