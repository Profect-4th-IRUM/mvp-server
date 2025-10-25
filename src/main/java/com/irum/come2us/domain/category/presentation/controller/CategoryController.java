package com.irum.come2us.domain.category.presentation.controller;

import com.irum.come2us.domain.category.application.service.CategoryService;
import com.irum.come2us.domain.category.presentation.dto.request.CategoryCreateRequest;
import com.irum.come2us.domain.category.presentation.dto.response.CategoryResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ------------------- 전체 조회 -------------------
    @GetMapping
    public List<CategoryResponse> getAllCategories(@RequestParam(required = false) UUID parentId) {
        if (parentId != null) {
            return categoryService.findByParentId(parentId);
        }
        return categoryService.findRootCategories();
    }

    // ------------------- 단일 조회 -------------------
    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable UUID id) {
        return categoryService.getCategoryById(id);
    }

    // ------------------- 트리 조회 -------------------
    @GetMapping("/tree")
    public List<CategoryResponse> getCategoryTree() {
        return categoryService.findCategoryTree();
    }

    // ------------------- 생성 -------------------
    @PostMapping
    public CategoryResponse createCategory(@RequestBody CategoryCreateRequest request) {
        return categoryService.createCategory(request);
    }

    // ------------------- 수정 -------------------
    @PatchMapping("/{id}")
    public CategoryResponse updateCategory(
            @PathVariable UUID id, @RequestBody Map<String, String> request) {
        return categoryService.updateCategory(id, request.get("name"));
    }

    // ------------------- 삭제 -------------------
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
    }
}
