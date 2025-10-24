package com.irum.come2us.domain.category.domain.repository;

import com.irum.come2us.domain.category.domain.entity.Category;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByParentIsNull(); // 루트 카테고리 조회
}
