package com.irum.come2us.domain.product.domain.repository;

import com.irum.come2us.domain.product.domain.entity.ProductOptionValue;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, UUID> {}
