package com.irum.come2us.domain.category.presentation.controller;

import com.irum.come2us.domain.category.application.service.CategoryService;
import com.irum.come2us.domain.category.presentation.dto.request.CategoryCreateRequest;
import com.irum.come2us.domain.category.presentation.dto.response.CategoryResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ------------------- 전체 조회 -------------------
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','OWNER','MANAGER','MASTER')")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    // ------------------- 단일 조회 -------------------
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','OWNER','MANAGER','MASTER')")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable UUID id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    // ------------------- 트리 조회 -------------------
    @GetMapping("/tree")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','MASTER')")
    public ResponseEntity<List<CategoryResponse>> getCategoryTree() {
        return ResponseEntity.ok(categoryService.findCategoryTree());
    }

    // ------------------- 생성 -------------------
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','MASTER')")
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody CategoryCreateRequest request) {
        var category = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryResponse.fromEntity(category));
    }

    // ------------------- 수정 -------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','MASTER')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID id, @RequestBody Map<String, String> request) {
        CategoryResponse response = categoryService.updateCategory(id, request.get("name"));
        return ResponseEntity.ok(response);
    }

    // ------------------- 삭제 -------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
