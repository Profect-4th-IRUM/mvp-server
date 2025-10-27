package com.irum.come2us.domain.category.domain.repository;

import com.irum.come2us.domain.category.domain.entity.Category;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByParentIsNull();

    @Query("SELECT c FROM Category c WHERE c.parent.categoryId = :parentId")
    List<Category> findChildrenByParentId(@Param("parentId") UUID parentId);
}
