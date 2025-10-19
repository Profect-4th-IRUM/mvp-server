package com.irum.come2us.domain.category.application.service;

import com.irum.come2us.domain.category.domain.entity.Category;
import com.irum.come2us.domain.category.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 전체 카테고리 조회
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 단일 카테고리 조회
    public Optional<Category> getCategoryById(String categoryId) {
        return categoryRepository.findById(categoryId);
    }

    // 부모 카테고리 기준 하위 카테고리 조회
    public List<Category> getSubCategories(String parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    // 카테고리 생성
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // 카테고리 수정
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }

    // 카테고리 삭제
    public void deleteCategory(String categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}