package com.irum.come2us.domain.category.application.service;

import com.irum.come2us.domain.category.domain.entity.Category;
import com.irum.come2us.domain.category.domain.repository.CategoryRepository;
import com.irum.come2us.domain.category.presentation.dto.request.CategoryCreateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 전체 카테고리 조회
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    // 카테고리 생성
    public Category create(CategoryCreateRequest request) {
        Category category;

        if (request.getParentId() == null) {
            category = Category.createRootCategory(request.getName());
        } else {
            Category parent =
                    categoryRepository
                            .findById(request.getParentId())
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "Parent category not found"));
            category = Category.createSubCategory(request.getName(), parent);
        }

        return categoryRepository.save(category);
    }
}
