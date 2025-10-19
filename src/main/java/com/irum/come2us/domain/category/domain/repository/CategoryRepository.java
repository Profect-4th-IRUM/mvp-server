package com.irum.come2us.domain.category.domain.repository;

import com.irum.come2us.domain.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    // 부모 카테고리 기준 하위 카테고리 조회
    List<Category> findByParentId(String parentId);
}
