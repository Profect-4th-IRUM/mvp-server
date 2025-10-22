package com.irum.come2us.domain.product.domain.repository;

import com.irum.come2us.domain.product.domain.entity.ProductOptionGroup;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionGroupRepository extends JpaRepository<ProductOptionGroup, UUID> {}
