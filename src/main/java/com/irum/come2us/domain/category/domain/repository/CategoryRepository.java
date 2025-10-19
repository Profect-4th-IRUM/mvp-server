package com.irum.come2us.domain.category.domain.repository;

import com.irum.come2us.domain.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
