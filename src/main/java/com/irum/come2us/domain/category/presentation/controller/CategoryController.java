package com.irum.come2us.domain.category.presentation.controller;

import com.irum.come2us.domain.category.application.service.CategoryService;
import com.irum.come2us.domain.category.presentation.dto.response.CategoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 전체 카테고리 조회
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    // 카테고리 생성
    //    @PostMapping
    //    public ResponseEntity<CategoryResponse> createCategory(
    //            @RequestBody CategoryCreateRequest request) {
    //        var category = categoryService.create(request);
    //        return ResponseEntity.status(HttpStatus.CREATED)
    //                .body(CategoryResponse.fromEntity(category));
    //    }

    // TODO: exeption 카테고리 도메인 pr 승인 후 재개발
}
