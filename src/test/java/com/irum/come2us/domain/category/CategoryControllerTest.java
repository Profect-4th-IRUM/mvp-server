package com.irum.come2us.domain.category;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.category.application.service.CategoryService;
import com.irum.come2us.domain.category.presentation.controller.CategoryController;
import com.irum.come2us.domain.category.presentation.dto.request.CategoryCreateRequest;
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
    @DisplayName("카테고리 생성 API 문서 생성")
    void categoryCreateApiTest() throws Exception {
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
                                                .description("상위 카테고리명 ID (루트면 null)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("categoryId").description("생성된 카테고리 ID"),
                                        fieldWithPath("name").description("카테고리명"),
                                        fieldWithPath("depth")
                                                .description("카테고리 깊이 (1=대, 2=중, 3=소)"),
                                        fieldWithPath("parentId")
                                                .description("상위 카테고리명 ID (루트면 null)")
                                                .optional(),
                                        fieldWithPath("children")
                                                .description("하위 카테고리 리스트 (기본 [])"))));
    }
}
