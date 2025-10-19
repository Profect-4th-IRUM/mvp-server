package com.irum.come2us.domain.product.domain.repository;

import com.irum.come2us.domain.product.domain.entity.Product;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID>, ProductRepositoryCustom {}
