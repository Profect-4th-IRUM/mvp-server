package com.irum.come2us.domain.category.presentation.controller;

import com.irum.come2us.domain.category.application.service.CategoryService;
import com.irum.come2us.domain.category.domain.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ✅ 전체 카테고리 조회
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // ✅ 단일 카테고리 조회
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") String id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 새 카테고리 생성
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    // ✅ 카테고리 수정
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable("id") String id,
            @RequestBody Category category
    ) {
        // 요청된 카테고리의 ID를 명시적으로 설정 (엔티티에서 사용)
        category.setId(UUID.fromString(id));
        return ResponseEntity.ok(categoryService.updateCategory(category));
    }

    // ✅ 카테고리 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
