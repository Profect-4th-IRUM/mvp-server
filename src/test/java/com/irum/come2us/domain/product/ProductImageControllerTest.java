package com.irum.come2us.domain.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.product.application.service.ProductImageService;
import com.irum.come2us.domain.product.presentation.controller.ProductImageController;
import com.irum.come2us.domain.product.presentation.dto.response.ProductImageResponse;
import com.irum.come2us.global.config.SecurityTestConfig;
import com.irum.come2us.global.config.TestConfig;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductImageController.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, TestConfig.class})
public class ProductImageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ProductImageService productImageService;
    @Autowired private ObjectMapper objectMapper;

    private final UUID mockProductId = UUID.randomUUID();
    private final UUID mockImageId = UUID.randomUUID();

    @Test
    @DisplayName("상품 이미지 업로드 API (사장님 권한)")
    void uploadProductImagesApiTest() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(
                        "images", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy".getBytes());

        doNothing().when(productImageService).uploadProductImages(eq(mockProductId), any());

        mockMvc.perform(
                        multipart("/products/{productId}/images", mockProductId)
                                .file(file)
                                .with(csrf())
                                .with(user("100").roles("OWNER")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andDo(
                        document(
                                "product-image-upload",
                                pathParameters(parameterWithName("productId").description("상품 ID")),
                                requestParts(
                                        partWithName("images").description("업로드할 상품 이미지 파일들"))));
    }

    @Test
    @DisplayName("대표 이미지 변경 API (사장님 권한)")
    void changeDefaultImageApiTest() throws Exception {
        doNothing()
                .when(productImageService)
                .changeDefaultImage(eq(mockProductId), eq(mockImageId));

        mockMvc.perform(
                        patch(
                                        "/products/{productId}/images/{imageId}/default",
                                        mockProductId,
                                        mockImageId)
                                .with(csrf())
                                .with(user("100").roles("OWNER")))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "product-image-change-default",
                                pathParameters(
                                        parameterWithName("productId").description("상품 ID"),
                                        parameterWithName("imageId")
                                                .description("대표로 변경할 이미지 ID"))));
    }

    @Test
    @DisplayName("상품 이미지 삭제 API (사장님 권한)")
    void deleteProductImageApiTest() throws Exception {
        doNothing()
                .when(productImageService)
                .deleteProductImage(eq(mockProductId), eq(mockImageId));

        mockMvc.perform(
                        delete("/products/{productId}/images/{imageId}", mockProductId, mockImageId)
                                .with(csrf())
                                .with(user("100").roles("OWNER")))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "product-image-delete",
                                pathParameters(
                                        parameterWithName("productId").description("상품 ID"),
                                        parameterWithName("imageId").description("삭제할 이미지 ID"))));
    }

    @Test
    @DisplayName("상품 이미지 목록 조회 API")
    void getProductImagesApiTest() throws Exception {
        List<ProductImageResponse> responseList =
                List.of(
                        new ProductImageResponse(
                                mockImageId, "https://cdn.example.com/img.jpg", true));

        when(productImageService.getProductImages(eq(mockProductId))).thenReturn(responseList);

        mockMvc.perform(
                        get("/products/{productId}/images", mockProductId)
                                .with(csrf())
                                .with(user("1").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(mockImageId.toString()))
                .andExpect(jsonPath("$.data[0].isDefault").value(true))
                .andDo(
                        document(
                                "product-image-list-get",
                                pathParameters(parameterWithName("productId").description("상품 ID")),
                                responseFields(
                                        fieldWithPath("success").description("요청 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("data[].id").description("상품 이미지 식별 ID"),
                                        fieldWithPath("data[].imageUrl").description("이미지 URL"),
                                        fieldWithPath("data[].isDefault").description("대표 이미지 여부"),
                                        fieldWithPath("timestamp").description("응답 시간"))));
    }
}
