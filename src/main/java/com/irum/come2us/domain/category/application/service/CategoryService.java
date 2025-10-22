package com.irum.come2us.domain.category.application.service;

import com.irum.come2us.domain.category.domain.entity.Category;
import com.irum.come2us.domain.category.domain.repository.CategoryRepository;
import com.irum.come2us.domain.category.presentation.dto.request.CategoryCreateRequest;
import com.irum.come2us.domain.category.presentation.dto.response.CategoryResponse;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // ------------------- 전체 조회 -------------------
    public List<CategoryResponse> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ------------------- 단일 조회 -------------------
    public CategoryResponse getCategoryById(UUID id) {
        Category category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Category not found: " + id));
        return CategoryResponse.fromEntity(category);
    }

    // ------------------- 트리 조회 -------------------
    public List<CategoryResponse> findCategoryTree() {
        List<Category> roots = categoryRepository.findByParentIsNull();
        return roots.stream()
                .map(CategoryResponse::fromEntityWithChildren)
                .collect(Collectors.toList());
    }

    // ------------------- 생성 -------------------
    public Category create(CategoryCreateRequest request) {
        Category category;

        if (request.parentId() == null) {
            category = Category.createRootCategory(request.name());
        } else {
            Category parent =
                    categoryRepository
                            .findById(request.parentId())
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "Parent category not found: "
                                                            + request.parentId()));
            category = Category.createSubCategory(request.name(), parent);
        }

        return categoryRepository.save(category);
    }

    // ------------------- 수정 -------------------
    public CategoryResponse updateCategory(UUID id, String newName) {
        Category category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Category not found: " + id));
        category.updateName(newName);
        return CategoryResponse.fromEntity(category);
    }

    // ------------------- 삭제 -------------------
    public void deleteCategory(UUID id) {
        Category category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Category not found: " + id));
        categoryRepository.delete(category);
    }
}
