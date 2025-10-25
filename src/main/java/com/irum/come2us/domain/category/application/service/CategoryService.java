package com.irum.come2us.domain.category.application.service;

import com.irum.come2us.domain.category.domain.entity.Category;
import com.irum.come2us.domain.category.domain.repository.CategoryRepository;
import com.irum.come2us.domain.category.presentation.dto.request.CategoryCreateRequest;
import com.irum.come2us.domain.category.presentation.dto.request.CategoryUpdateRequest;
import com.irum.come2us.domain.category.presentation.dto.response.CategoryInfoResponse;
import com.irum.come2us.domain.category.presentation.dto.response.CategoryResponse;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.CategoryErrorCode;
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

    @Transactional(readOnly = true)
    public List<CategoryInfoResponse> findRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(CategoryInfoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryInfoResponse> findByParentId(UUID parentId) {
        return categoryRepository.findChildrenByParentId(parentId).stream()
                .map(CategoryInfoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {
        Category category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new CommonException(CategoryErrorCode.CATEGORY_NOT_FOUND));
        return CategoryResponse.fromEntity(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findCategoryTree() {
        List<Category> roots = categoryRepository.findByParentIsNull();
        return roots.stream()
                .map(CategoryResponse::fromEntityWithChildren)
                .collect(Collectors.toList());
    }

    public CategoryResponse createCategory(CategoryCreateRequest request) {
        Category category;

        if (request.parentId() == null) {
            category = Category.createRootCategory(request.name());
        } else {
            Category parent =
                    categoryRepository
                            .findById(request.parentId())
                            .orElseThrow(
                                    () ->
                                            new CommonException(
                                                    CategoryErrorCode.CATEGORY_NOT_FOUND));
            category = Category.createSubCategory(request.name(), parent);
        }

        Category saved = categoryRepository.save(category);
        return CategoryResponse.fromEntity(saved);
    }

    public CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request) {
        Category category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new CommonException(CategoryErrorCode.CATEGORY_NOT_FOUND));
        category.updateName(request.name());
        return CategoryResponse.fromEntity(category);
    }

    public void deleteCategory(UUID id) {
        Category category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new CommonException(CategoryErrorCode.CATEGORY_NOT_FOUND));
        categoryRepository.delete(category);
    }
}
