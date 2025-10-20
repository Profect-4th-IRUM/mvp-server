package com.irum.come2us.domain.category.presentation.controller;

import com.irum.come2us.domain.category.application.service.CategoryService;
import com.irum.come2us.domain.category.domain.entity.Category;
import com.irum.come2us.domain.category.presentation.dto.request.CategoryCreateRequest;
import com.irum.come2us.domain.category.presentation.dto.response.CategoryResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 전체 카테고리 조회
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> responses =
                categoryService.findAll().stream()
                        .map(CategoryResponse::fromEntity)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // 카테고리 생성
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody CategoryCreateRequest request) {
        Category category = categoryService.create(request);
        return ResponseEntity.ok(CategoryResponse.fromEntity(category));
    }
}
