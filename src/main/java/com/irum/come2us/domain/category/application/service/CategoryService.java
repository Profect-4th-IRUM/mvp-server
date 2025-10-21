package com.irum.come2us.domain.category.application.service;

import com.irum.come2us.domain.category.domain.repository.CategoryRepository;
import com.irum.come2us.domain.category.presentation.dto.response.CategoryResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // ✅ 전체 카테고리 조회
    public List<CategoryResponse> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ✅ 카테고리 생성
    //    public Category create(CategoryCreateRequest request) {
    //        Category category;
    //
    //        if (request.getParentId() == null) {
    //            category = Category.createRootCategory(request.getName());
    //        } else {
    //            Category parent =
    //                    categoryRepository
    //                            .findById(request.getParentId())
    //                            // 🔹 CustomException → CommonException 으로 변경
    //                            .orElseThrow(
    //                                    () ->
    //                                            new CommonException(
    //                                                    GlobalErrorCode.INTERNAL_SERVER_ERROR));
    //            category = Category.createSubCategory(request.getName(), parent);
    //        }
    //
    //        return categoryRepository.save(category);
    //    }
}
