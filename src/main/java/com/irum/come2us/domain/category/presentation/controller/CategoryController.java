package com.irum.come2us.domain.category.presentation.controller;

import com.irum.come2us.domain.category.application.service.CategoryService;
import com.irum.come2us.domain.category.presentation.dto.request.CategoryCreateRequest;
import com.irum.come2us.domain.category.presentation.dto.request.CategoryUpdateRequest;
import com.irum.come2us.domain.category.presentation.dto.response.CategoryInfoResponse;
import com.irum.come2us.domain.category.presentation.dto.response.CategoryResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryInfoResponse> getAllCategories(
            @RequestParam(required = false) UUID parentId) {
        if (parentId != null) {
            return categoryService.findByParentId(parentId);
        }
        return categoryService.findRootCategories();
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable UUID id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/tree")
    public List<CategoryResponse> getCategoryTree() {
        return categoryService.findCategoryTree();
    }

    @PostMapping
    public CategoryResponse createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        return categoryService.createCategory(request);
    }

    @PatchMapping("/{id}")
    public CategoryResponse updateCategory(
            @PathVariable UUID id, @Valid @RequestBody CategoryUpdateRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
    }
}
