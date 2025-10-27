package com.irum.come2us.domain.category;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.category.application.service.CategoryService;
import com.irum.come2us.domain.category.presentation.controller.CategoryController;
import com.irum.come2us.domain.category.presentation.dto.request.CategoryCreateRequest;
import com.irum.come2us.domain.category.presentation.dto.request.CategoryUpdateRequest;
import com.irum.come2us.domain.category.presentation.dto.response.CategoryInfoResponse;
import com.irum.come2us.domain.category.presentation.dto.response.CategoryResponse;
import com.irum.come2us.global.config.SecurityTestConfig;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
class CategoryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CategoryService categoryService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CategoryService categoryService() {
            return Mockito.mock(CategoryService.class);
        }
    }

    @Test
    @DisplayName("루트 카테고리 조회 API")
    void getAllRootCategories() throws Exception {
        // Given
        List<CategoryInfoResponse> mockResponses =
                List.of(
                        new CategoryInfoResponse(UUID.randomUUID(), "식품", 1),
                        new CategoryInfoResponse(UUID.randomUUID(), "가전", 1));

        when(categoryService.findRootCategories()).thenReturn(mockResponses);

        // When & Then
        mockMvc.perform(get("/categories").with(csrf()))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "categories/get-roots",
                                responseFields(
                                        fieldWithPath("[].categoryId").description("카테고리 ID"),
                                        fieldWithPath("[].name").description("카테고리명"),
                                        fieldWithPath("[].depth").description("카테고리 깊이"))));
    }

    @Test
    @DisplayName("특정 부모의 하위 카테고리 조회 API")
    void getSubCategories() throws Exception {
        // Given
        UUID parentId = UUID.randomUUID();
        List<CategoryInfoResponse> mockResponses =
                List.of(
                        new CategoryInfoResponse(UUID.randomUUID(), "음료", 2),
                        new CategoryInfoResponse(UUID.randomUUID(), "과자", 2));

        when(categoryService.findByParentId(parentId)).thenReturn(mockResponses);

        // When & Then
        mockMvc.perform(get("/categories").param("parentId", parentId.toString()).with(csrf()))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "categories/get-by-parent",
                                queryParameters(
                                        parameterWithName("parentId").description("부모 카테고리 ID")),
                                responseFields(
                                        fieldWithPath("[].categoryId").description("카테고리 ID"),
                                        fieldWithPath("[].name").description("카테고리명"),
                                        fieldWithPath("[].depth").description("카테고리 깊이"))));
    }

    @Test
    @DisplayName("특정 카테고리 조회 API")
    void getCategoryById() throws Exception {
        // Given
        UUID categoryId = UUID.randomUUID();
        CategoryResponse mockResponse =
                new CategoryResponse(categoryId, "음료", 2, UUID.randomUUID(), List.of());

        when(categoryService.getCategoryById(categoryId)).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/categories/{id}", categoryId).with(csrf()))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "categories/get-by-id",
                                pathParameters(parameterWithName("id").description("카테고리 ID")),
                                responseFields(
                                        fieldWithPath("categoryId").description("카테고리 ID"),
                                        fieldWithPath("name").description("카테고리명"),
                                        fieldWithPath("depth").description("카테고리 깊이"),
                                        fieldWithPath("parentId")
                                                .description("부모 카테고리 ID")
                                                .optional(),
                                        fieldWithPath("children").description("하위 카테고리 리스트"))));
    }

    @Test
    @DisplayName("카테고리 트리 조회 API")
    void getCategoryTree() throws Exception {
        // Given
        List<CategoryResponse> mockTree =
                List.of(
                        new CategoryResponse(
                                UUID.randomUUID(),
                                "식품",
                                1,
                                null,
                                List.of(
                                        new CategoryResponse(
                                                UUID.randomUUID(),
                                                "음료",
                                                2,
                                                UUID.randomUUID(),
                                                List.of()),
                                        new CategoryResponse(
                                                UUID.randomUUID(),
                                                "과자",
                                                2,
                                                UUID.randomUUID(),
                                                List.of()))));

        when(categoryService.findCategoryTree()).thenReturn(mockTree);

        // When & Then
        mockMvc.perform(get("/categories/tree").with(csrf()))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "categories/get-tree",
                                responseFields(
                                        fieldWithPath("[].categoryId").description("카테고리 ID"),
                                        fieldWithPath("[].name").description("카테고리명"),
                                        fieldWithPath("[].depth").description("카테고리 깊이"),
                                        fieldWithPath("[].parentId")
                                                .description("부모 카테고리 ID")
                                                .optional(),
                                        fieldWithPath("[].children").description("하위 카테고리 리스트"),
                                        fieldWithPath("[].children[].categoryId")
                                                .description("하위 카테고리 ID")
                                                .optional(),
                                        fieldWithPath("[].children[].name")
                                                .description("하위 카테고리명")
                                                .optional(),
                                        fieldWithPath("[].children[].depth")
                                                .description("하위 카테고리 깊이")
                                                .optional(),
                                        fieldWithPath("[].children[].parentId")
                                                .description("하위 카테고리의 부모 ID")
                                                .optional(),
                                        fieldWithPath("[].children[].children")
                                                .description("하위의 하위 카테고리")
                                                .optional())));
    }

    @Test
    @DisplayName("카테고리 생성 API")
    void createCategory() throws Exception {
        // Given
        UUID parentId = UUID.randomUUID();
        CategoryCreateRequest request = new CategoryCreateRequest("음료", parentId);
        String requestJson = objectMapper.writeValueAsString(request);

        CategoryResponse mockResponse =
                new CategoryResponse(UUID.randomUUID(), "음료", 2, parentId, List.of());

        when(categoryService.createCategory(any(CategoryCreateRequest.class)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(
                        post("/categories")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "categories/create",
                                requestFields(
                                        fieldWithPath("name").description("카테고리명"),
                                        fieldWithPath("parentId")
                                                .description("상위 카테고리 ID (루트면 null)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("categoryId").description("생성된 카테고리 ID"),
                                        fieldWithPath("name").description("카테고리명"),
                                        fieldWithPath("depth")
                                                .description("카테고리 깊이 (1=대, 2=중, 3=소)"),
                                        fieldWithPath("parentId")
                                                .description("상위 카테고리 ID (루트면 null)")
                                                .optional(),
                                        fieldWithPath("children")
                                                .description("하위 카테고리 리스트 (기본 [])"))));
    }

    @Test
    @DisplayName("카테고리 수정 API")
    void updateCategory() throws Exception {
        // Given
        UUID categoryId = UUID.randomUUID();
        CategoryUpdateRequest request = new CategoryUpdateRequest("변경된 카테고리명");
        String requestJson = objectMapper.writeValueAsString(request);

        CategoryResponse mockResponse =
                new CategoryResponse(categoryId, "변경된 카테고리명", 2, UUID.randomUUID(), List.of());

        when(categoryService.updateCategory(eq(categoryId), any(CategoryUpdateRequest.class)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(
                        patch("/categories/{id}", categoryId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "categories/update",
                                pathParameters(parameterWithName("id").description("수정할 카테고리 ID")),
                                requestFields(fieldWithPath("name").description("변경할 카테고리명")),
                                responseFields(
                                        fieldWithPath("categoryId").description("카테고리 ID"),
                                        fieldWithPath("name").description("변경된 카테고리명"),
                                        fieldWithPath("depth").description("카테고리 깊이"),
                                        fieldWithPath("parentId")
                                                .description("부모 카테고리 ID")
                                                .optional(),
                                        fieldWithPath("children").description("하위 카테고리 리스트"))));
    }

    @Test
    @DisplayName("카테고리 삭제 API")
    void deleteCategory() throws Exception {
        // Given
        UUID categoryId = UUID.randomUUID();
        doNothing().when(categoryService).deleteCategory(categoryId);

        // When & Then
        mockMvc.perform(delete("/categories/{id}", categoryId).with(csrf()))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "categories/delete",
                                pathParameters(
                                        parameterWithName("id").description("삭제할 카테고리 ID"))));
    }

    @Test
    @DisplayName("카테고리 생성 시 이름이 없으면 400 에러")
    void createCategory_WithoutName_Returns400() throws Exception {
        // Given
        CategoryCreateRequest request = new CategoryCreateRequest("", null);
        String requestJson = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(
                        post("/categories")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리 생성 시 이름이 null이면 400 에러")
    void createCategory_WithNullName_Returns400() throws Exception {
        // Given
        String requestJson = "{\"name\": null, \"parentId\": null}";

        // When & Then
        mockMvc.perform(
                        post("/categories")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("루트 카테고리 생성 API")
    void createRootCategory() throws Exception {
        // Given
        CategoryCreateRequest request = new CategoryCreateRequest("식품", null);
        String requestJson = objectMapper.writeValueAsString(request);

        CategoryResponse mockResponse =
                new CategoryResponse(UUID.randomUUID(), "식품", 1, null, List.of());

        when(categoryService.createCategory(any(CategoryCreateRequest.class)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(
                        post("/categories")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "categories/create-root",
                                requestFields(
                                        fieldWithPath("name").description("카테고리명"),
                                        fieldWithPath("parentId")
                                                .description("상위 카테고리 ID (루트면 null)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("categoryId").description("생성된 카테고리 ID"),
                                        fieldWithPath("name").description("카테고리명"),
                                        fieldWithPath("depth").description("카테고리 깊이 (1=루트)"),
                                        fieldWithPath("parentId")
                                                .description("상위 카테고리 ID (루트면 null)")
                                                .optional(),
                                        fieldWithPath("children")
                                                .description("하위 카테고리 리스트 (기본 [])"))));
    }
}
