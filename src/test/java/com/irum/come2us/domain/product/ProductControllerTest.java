package com.irum.come2us.domain.product;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.category.presentation.dto.response.CategoryInfoResponse;
import com.irum.come2us.domain.product.application.service.ProductService;
import com.irum.come2us.domain.product.presentation.controller.ProductController;
import com.irum.come2us.domain.product.presentation.dto.request.*;
import com.irum.come2us.domain.product.presentation.dto.response.ProductDetailResponse;
import com.irum.come2us.domain.product.presentation.dto.response.ProductOptionGroupResponse;
import com.irum.come2us.domain.product.presentation.dto.response.ProductOptionValueResponse;
import com.irum.come2us.domain.product.presentation.dto.response.ProductResponse;
import com.irum.come2us.domain.store.presentation.dto.response.*;
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

@WebMvcTest(ProductController.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProductService productService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProductService productService() {
            return Mockito.mock(ProductService.class);
        }
    }

    // 1 상품등록
    @Test
    @DisplayName("상품 등록 API")
    void createProductTest() throws Exception {
        // given
        ProductOptionValueRequest value1 = new ProductOptionValueRequest("빨강", 15, 0);
        ProductOptionValueRequest value2 = new ProductOptionValueRequest("파랑", 12, 0);

        ProductOptionGroupRequest optionGroups =
                new ProductOptionGroupRequest("색상 변경", List.of(value1, value2));

        UUID categoryId = UUID.randomUUID();

        ProductCreateRequest request =
                new ProductCreateRequest(
                        "라운드 반팔 티셔츠",
                        "여름용 반팔 티셔츠",
                        "부드러운 면 소재의 라운드 반팔 티셔츠입니다.",
                        true,
                        15000,
                        categoryId,
                        List.of(optionGroups));

        ProductResponse response =
                new ProductResponse(
                        UUID.randomUUID(),
                        "라운드 반팔 티셔츠",
                        "여름용 반팔 티셔츠",
                        "부드러운 면 소재의 라운드 반팔 티셔츠입니다.",
                        15000,
                        true,
                        4.5,
                        12,
                        categoryId,
                        "상의");

        Mockito.when(productService.createProduct(any(ProductCreateRequest.class)))
                .thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        post("/products")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("라운드 반팔 티셔츠"))
                .andExpect(jsonPath("$.data.price").value(15000))
                .andExpect(jsonPath("$.data.isPublic").value(true))
                .andDo(
                        document(
                                "product-create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("name").description("상품명"),
                                        fieldWithPath("description").description("상품 요약 설명"),
                                        fieldWithPath("detailDescription").description("상품 상세 설명"),
                                        fieldWithPath("isPublic").description("상품 공개 여부"),
                                        fieldWithPath("price").description("기본 상품 가격"),
                                        fieldWithPath("categoryId").description("카테고리 ID"),
                                        fieldWithPath("optionGroups").description("상품 옵션 그룹 리스트"),
                                        fieldWithPath("optionGroups[].name").description("옵션 그룹명"),
                                        fieldWithPath("optionGroups[].optionValues")
                                                .description("옵션 값 리스트"),
                                        fieldWithPath("optionGroups[].optionValues[].name")
                                                .description("옵션명"),
                                        fieldWithPath("optionGroups[].optionValues[].stockQuantity")
                                                .description("재고 수량"),
                                        fieldWithPath("optionGroups[].optionValues[].extraPrice")
                                                .description("추가 금액")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.id").description("상품 ID"),
                                        fieldWithPath("data.name").description("상품명"),
                                        fieldWithPath("data.description").description("상품 요약 설명"),
                                        fieldWithPath("data.detailDescription")
                                                .description("상품 상세 설명"),
                                        fieldWithPath("data.price").description("기본 가격"),
                                        fieldWithPath("data.isPublic").description("공개 여부"),
                                        fieldWithPath("data.avgRating").description("평균 평점"),
                                        fieldWithPath("data.reviewCount").description("리뷰 수"),
                                        fieldWithPath("data.categoryId").description("카테고리 ID"),
                                        fieldWithPath("data.categoryName").description("카테고리명"))));
    }

    // 2상품수정.
    @Test
    @DisplayName("상품 수정 API")
    void updateProductTest() throws Exception {
        // given
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        ProductUpdateRequest request =
                new ProductUpdateRequest(
                        "수정된 반팔 티셔츠", "가볍고 시원한 여름 반팔", "면 100% 소재, 통기성 좋은 반팔 티셔츠", true, 17000);

        ProductResponse response =
                new ProductResponse(
                        productId,
                        request.name(),
                        request.description(),
                        request.detailDescription(),
                        request.price(),
                        request.isPublic(),
                        4.7,
                        25,
                        categoryId,
                        "상의");

        Mockito.when(productService.updateProduct(any(UUID.class), any(ProductUpdateRequest.class)))
                .thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        patch("/products/{productId}", productId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("수정된 반팔 티셔츠"))
                .andExpect(jsonPath("$.data.price").value(17000))
                .andDo(
                        document(
                                "product-update",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("productId").description("수정할 상품의 ID")),
                                requestFields(
                                        fieldWithPath("name").description("변경할 상품명 (nullable)"),
                                        fieldWithPath("description")
                                                .description("변경할 상품 요약 설명 (nullable)"),
                                        fieldWithPath("detailDescription")
                                                .description("변경할 상품 상세 설명 (nullable)"),
                                        fieldWithPath("isPublic").description("공개 여부 (nullable)"),
                                        fieldWithPath("price").description("상품 가격 (nullable)")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.id").description("상품 ID"),
                                        fieldWithPath("data.name").description("상품명"),
                                        fieldWithPath("data.description").description("상품 요약 설명"),
                                        fieldWithPath("data.detailDescription")
                                                .description("상품 상세 설명"),
                                        fieldWithPath("data.price").description("가격"),
                                        fieldWithPath("data.isPublic").description("공개 여부"),
                                        fieldWithPath("data.avgRating").description("평균 평점"),
                                        fieldWithPath("data.reviewCount").description("리뷰 수"),
                                        fieldWithPath("data.categoryId").description("카테고리 ID"),
                                        fieldWithPath("data.categoryName").description("카테고리명"))));
    }

    // 3. 상품공개상태변경
    @Test
    @DisplayName("상품 공개 상태 변경 API")
    void updateProductPublicStatusTest() throws Exception {
        // given
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        // 요청 DTO
        ProductPublicUpdateRequest request = new ProductPublicUpdateRequest(true);

        // 응답 DTO (ProductResponse)
        ProductResponse response =
                new ProductResponse(
                        productId,
                        "라운드 반팔 티셔츠",
                        "여름용 반팔 티셔츠",
                        "면 100% 소재, 통기성 좋은 반팔 티셔츠",
                        15000,
                        true,
                        4.5,
                        12,
                        categoryId,
                        "상의");

        // Mocking
        Mockito.when(
                        productService.updateProductPublicStatus(
                                any(UUID.class), any(ProductPublicUpdateRequest.class)))
                .thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        patch("/products/{productId}/public", productId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isPublic").value(true))
                .andDo(
                        document(
                                "product-public-update",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("productId")
                                                .description("공개 상태를 변경할 상품 ID")),
                                requestFields(
                                        fieldWithPath("isPublic")
                                                .description("상품 공개 여부 (true = 공개, false = 비공개)")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.id").description("상품 ID"),
                                        fieldWithPath("data.name").description("상품명"),
                                        fieldWithPath("data.description").description("상품 요약 설명"),
                                        fieldWithPath("data.detailDescription")
                                                .description("상품 상세 설명"),
                                        fieldWithPath("data.price").description("가격"),
                                        fieldWithPath("data.isPublic").description("공개 여부"),
                                        fieldWithPath("data.avgRating").description("평균 평점"),
                                        fieldWithPath("data.reviewCount").description("리뷰 수"),
                                        fieldWithPath("data.categoryId").description("카테고리 ID"),
                                        fieldWithPath("data.categoryName").description("카테고리명"))));
    }

    // 4상품 카테고리 변경
    @Test
    @DisplayName("상품 카테고리 변경 API")
    void updateProductCategoryTest() throws Exception {
        // given
        UUID productId = UUID.randomUUID();
        UUID newCategoryId = UUID.randomUUID();

        // 요청 DTO
        ProductCategoryUpdateRequest request = new ProductCategoryUpdateRequest(newCategoryId);

        // 응답 DTO (ProductResponse)
        ProductResponse response =
                new ProductResponse(
                        productId,
                        "라운드 반팔 티셔츠",
                        "여름용 반팔 티셔츠",
                        "면 100% 소재, 통기성 좋은 반팔 티셔츠",
                        15000,
                        true,
                        4.8,
                        21,
                        newCategoryId,
                        "상의");

        // Mocking
        Mockito.when(
                        productService.updateProductCategory(
                                any(UUID.class), any(ProductCategoryUpdateRequest.class)))
                .thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        patch("/products/{productId}/categories", productId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryId").value(newCategoryId.toString()))
                .andDo(
                        document(
                                "product-category-update",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("productId")
                                                .description("카테고리를 변경할 상품 ID")),
                                requestFields(
                                        fieldWithPath("categoryId")
                                                .description("새로 지정할 카테고리 ID (필수)")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.id").description("상품 ID"),
                                        fieldWithPath("data.name").description("상품명"),
                                        fieldWithPath("data.description").description("상품 요약 설명"),
                                        fieldWithPath("data.detailDescription")
                                                .description("상품 상세 설명"),
                                        fieldWithPath("data.price").description("상품 가격"),
                                        fieldWithPath("data.isPublic").description("공개 여부"),
                                        fieldWithPath("data.avgRating").description("평균 평점"),
                                        fieldWithPath("data.reviewCount").description("리뷰 수"),
                                        fieldWithPath("data.categoryId").description("변경된 카테고리 ID"),
                                        fieldWithPath("data.categoryName").description("카테고리명"))));
    }

    // 5. 상품목록조회
    @Test
    @DisplayName("상품 목록 조회 API")
    void getProductListTest() throws Exception {
        // given
        UUID categoryId = UUID.randomUUID();

        ProductResponse product1 =
                new ProductResponse(
                        UUID.randomUUID(),
                        "반팔 티셔츠",
                        "가벼운 여름용 반팔",
                        "통기성 좋은 면 소재 반팔입니다.",
                        15000,
                        true,
                        4.5,
                        12,
                        categoryId,
                        "상의");

        ProductResponse product2 =
                new ProductResponse(
                        UUID.randomUUID(),
                        "후드티",
                        "따뜻한 기모 후드티",
                        "겨울용 보온성 높은 기모 후드입니다.",
                        35000,
                        true,
                        4.8,
                        31,
                        categoryId,
                        "상의");

        ProductCursorResponse response =
                new ProductCursorResponse(List.of(product1, product2), product2.id());

        Mockito.when(productService.getProductList(any(), any(), any(), any()))
                .thenReturn(response);

        // when & then
        mockMvc.perform(
                        get("/products")
                                .param("categoryId", categoryId.toString())
                                .param("size", "10")
                                .param("keyword", "티셔츠")
                                .with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.products").isArray())
                .andExpect(jsonPath("$.data.products[0].name").value("반팔 티셔츠"))
                .andDo(
                        document(
                                "product-list",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                queryParameters(
                                        parameterWithName("categoryId")
                                                .description("카테고리 ID (선택)")
                                                .optional(),
                                        parameterWithName("cursor")
                                                .description("다음 페이지 커서 (선택)")
                                                .optional(),
                                        parameterWithName("size")
                                                .description(
                                                        "페이지 크기 (선택, 기본값 10 / 허용값: 10, 30, 50)")
                                                .optional(),
                                        parameterWithName("keyword")
                                                .description("검색어 (선택)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.products[].id").description("상품 ID"),
                                        fieldWithPath("data.products[].name").description("상품명"),
                                        fieldWithPath("data.products[].description")
                                                .description("요약 설명"),
                                        fieldWithPath("data.products[].detailDescription")
                                                .description("상세 설명"),
                                        fieldWithPath("data.products[].price").description("가격"),
                                        fieldWithPath("data.products[].isPublic")
                                                .description("공개 여부"),
                                        fieldWithPath("data.products[].avgRating")
                                                .description("평균 평점"),
                                        fieldWithPath("data.products[].reviewCount")
                                                .description("리뷰 수"),
                                        fieldWithPath("data.products[].categoryId")
                                                .description("카테고리 ID"),
                                        fieldWithPath("data.products[].categoryName")
                                                .description("카테고리명"),
                                        fieldWithPath("data.nextCursor")
                                                .description("다음 페이지 커서 (없을 경우 null)"))));
    }

    // 6 상품 상세 조회
    @Test
    @DisplayName("상품 상세 조회 API")
    void getProductDetailTest() throws Exception {
        // given
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();

        // 하위 객체 생성
        StoreInfoResponse store =
                new StoreInfoResponse(
                        storeId,
                        "맛있는 상점",
                        "010-1234-5678",
                        "서울특별시 강남구 테헤란로 1",
                        "1234567890",
                        "9876543210");

        CategoryInfoResponse category = new CategoryInfoResponse(categoryId, "상의", 3);

        ProductOptionGroupResponse optionGroup =
                new ProductOptionGroupResponse(
                        UUID.randomUUID(),
                        "색상",
                        List.of(
                                new ProductOptionValueResponse(UUID.randomUUID(), "빨강", 10, 0),
                                new ProductOptionValueResponse(UUID.randomUUID(), "파랑", 5, 0)));

        ProductDetailResponse response =
                new ProductDetailResponse(
                        productId,
                        "라운드 반팔 티셔츠",
                        "여름용 반팔 티셔츠",
                        "면 100% 소재, 통기성 좋은 반팔입니다.",
                        15000,
                        true,
                        4.7,
                        32,
                        store,
                        category,
                        List.of(optionGroup));

        Mockito.when(productService.getProductById(productId)).thenReturn(response);

        // when & then
        mockMvc.perform(
                        get("/products/{productId}", productId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("라운드 반팔 티셔츠"))
                .andDo(
                        document(
                                "product-detail",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("productId").description("상세 조회할 상품 ID")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),

                                        // Product
                                        fieldWithPath("data.id").description("상품 ID"),
                                        fieldWithPath("data.name").description("상품명"),
                                        fieldWithPath("data.description").description("상품 요약 설명"),
                                        fieldWithPath("data.detailDescription")
                                                .description("상품 상세 설명"),
                                        fieldWithPath("data.price").description("가격"),
                                        fieldWithPath("data.isPublic").description("공개 여부"),
                                        fieldWithPath("data.avgRating").description("평균 평점"),
                                        fieldWithPath("data.reviewCount").description("리뷰 수"),

                                        // Store
                                        fieldWithPath("data.store.id").description("상점 ID"),
                                        fieldWithPath("data.store.name").description("상점명"),
                                        fieldWithPath("data.store.contact").description("연락처"),
                                        fieldWithPath("data.store.address").description("주소"),
                                        fieldWithPath("data.store.businessRegistrationNumber")
                                                .description("사업자등록번호"),
                                        fieldWithPath("data.store.telemarketingRegistrationNumber")
                                                .description("통신판매업번호"),

                                        // Category
                                        fieldWithPath("data.category.categoryId")
                                                .description("카테고리 ID"),
                                        fieldWithPath("data.category.name").description("카테고리명"),
                                        fieldWithPath("data.category.depth")
                                                .description("카테고리 깊이 (1=대분류, 2=중분류, 3=소분류)"),

                                        // Option Groups
                                        fieldWithPath("data.optionGroups[].id")
                                                .description("옵션 그룹 ID"),
                                        fieldWithPath("data.optionGroups[].name")
                                                .description("옵션 그룹명"),
                                        fieldWithPath("data.optionGroups[].optionValues[].id")
                                                .description("옵션 값 ID"),
                                        fieldWithPath("data.optionGroups[].optionValues[].name")
                                                .description("옵션명"),
                                        fieldWithPath(
                                                        "data.optionGroups[].optionValues[].stockQuantity")
                                                .description("재고 수량"),
                                        fieldWithPath(
                                                        "data.optionGroups[].optionValues[].extraPrice")
                                                .description("추가 금액"))));
    }

    // 7. 상품 삭제
    @Test
    @DisplayName("상품 삭제 API")
    void deleteProductTest() throws Exception {
        // given
        UUID productId = UUID.randomUUID();

        Mockito.doNothing().when(productService).deleteProduct(productId);

        // when & then
        mockMvc.perform(delete("/products/{productId}", productId).with(csrf().asHeader()))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "product-delete",
                                preprocessRequest(prettyPrint()),
                                pathParameters(
                                        parameterWithName("productId").description("삭제할 상품의 ID"))));
    }

    // 8 상품 옵션 그룹 생성
    @Test
    @DisplayName("상품 옵션 그룹 생성 API")
    void createProductOptionGroupTest() throws Exception {
        UUID productId = UUID.randomUUID();

        ProductOptionValueRequest value1 = new ProductOptionValueRequest("빨강", 10, 0);
        ProductOptionValueRequest value2 = new ProductOptionValueRequest("파랑", 5, 0);

        ProductOptionGroupRequest request =
                new ProductOptionGroupRequest("색상", List.of(value1, value2));

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/products/{productId}/options", productId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "product-option-group-create",
                                pathParameters(
                                        parameterWithName("productId")
                                                .description("옵션 그룹을 추가할 상품 ID")),
                                requestFields(
                                        fieldWithPath("name").description("옵션 그룹명 (예: 색상, 사이즈 등)"),
                                        fieldWithPath("optionValues")
                                                .description("옵션 값 목록 (예: 빨강, 파랑 등)"),
                                        fieldWithPath("optionValues[].name").description("옵션명"),
                                        fieldWithPath("optionValues[].stockQuantity")
                                                .description("재고 수량"),
                                        fieldWithPath("optionValues[].extraPrice")
                                                .description("추가 금액"))));
    }

    // 9 상품 옵션 그룹 수정
    @Test
    @DisplayName("상품 옵션 그룹 수정 API")
    void updateProductOptionGroupTest() throws Exception {
        UUID optionGroupId = UUID.randomUUID();

        ProductOptionValueRequest value1 = new ProductOptionValueRequest("빨강", 15, 0);
        ProductOptionValueRequest value2 = new ProductOptionValueRequest("파랑", 12, 0);

        ProductOptionGroupRequest request =
                new ProductOptionGroupRequest("색상 변경", List.of(value1, value2));

        ProductOptionGroupResponse response =
                new ProductOptionGroupResponse(
                        optionGroupId,
                        "색상 변경",
                        List.of(
                                new ProductOptionValueResponse(UUID.randomUUID(), "빨강", 15, 0),
                                new ProductOptionValueResponse(UUID.randomUUID(), "파랑", 12, 0)));

        Mockito.when(productService.updateProductOptionGroup(optionGroupId, request))
                .thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        patch("/products/options/{optionGroupId}", optionGroupId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "product-option-group-update",
                                pathParameters(
                                        parameterWithName("optionGroupId")
                                                .description("수정할 옵션 그룹 ID")),
                                requestFields(
                                        fieldWithPath("name").description("옵션 그룹명"),
                                        fieldWithPath("optionValues").description("옵션 값 리스트"),
                                        fieldWithPath("optionValues[].name").description("옵션명"),
                                        fieldWithPath("optionValues[].stockQuantity")
                                                .description("재고 수량"),
                                        fieldWithPath("optionValues[].extraPrice")
                                                .description("추가 금액")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.id").description("옵션 그룹 ID"),
                                        fieldWithPath("data.name").description("옵션 그룹명"),
                                        fieldWithPath("data.optionValues[].id")
                                                .description("옵션 값 ID"),
                                        fieldWithPath("data.optionValues[].name")
                                                .description("옵션명"),
                                        fieldWithPath("data.optionValues[].stockQuantity")
                                                .description("재고 수량"),
                                        fieldWithPath("data.optionValues[].extraPrice")
                                                .description("추가 금액"))));
    }

    // 10 상품 그룹 삭제
    @Test
    @DisplayName("상품 옵션 그룹 삭제 API")
    void deleteProductOptionGroupTest() throws Exception {
        UUID optionGroupId = UUID.randomUUID();

        Mockito.doNothing().when(productService).deleteProductOptionGroup(optionGroupId);

        mockMvc.perform(
                        delete("/products/options/{optionGroupId}", optionGroupId)
                                .with(csrf().asHeader()))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "product-option-group-delete",
                                pathParameters(
                                        parameterWithName("optionGroupId")
                                                .description("삭제할 옵션 그룹 ID"))));
    }

    // 11 상품 옵션 값 생성
    @Test
    @DisplayName("상품 옵션 값 추가 API")
    void createProductOptionValueTest() throws Exception {
        UUID optionGroupId = UUID.randomUUID();

        ProductOptionValueRequest request = new ProductOptionValueRequest("검정", 8, 500);
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/products/options/{optionGroupId}/values", optionGroupId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "product-option-value-create",
                                pathParameters(
                                        parameterWithName("optionGroupId")
                                                .description("옵션 값을 추가할 옵션 그룹 ID")),
                                requestFields(
                                        fieldWithPath("name").description("옵션명"),
                                        fieldWithPath("stockQuantity").description("재고 수량"),
                                        fieldWithPath("extraPrice").description("추가 금액"))));
    }

    // 12 상품 값 수정
    @Test
    @DisplayName("상품 옵션 값 수정 API")
    void updateProductOptionValueTest() throws Exception {
        UUID optionValueId = UUID.randomUUID();

        ProductOptionValueUpdateRequest request =
                new ProductOptionValueUpdateRequest("검정", 30, 2000);

        ProductOptionValueResponse response =
                new ProductOptionValueResponse(optionValueId, "검정", 30, 2000);

        Mockito.when(productService.updateProductOptionValue(optionValueId, request))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/products/options/values/{optionValueId}", optionValueId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "product-option-value-update",
                                pathParameters(
                                        parameterWithName("optionValueId")
                                                .description("수정할 옵션 값 ID")),
                                requestFields(
                                        fieldWithPath("name").description("옵션명"),
                                        fieldWithPath("stockQuantity").description("재고 수량"),
                                        fieldWithPath("extraPrice").description("추가 금액")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.id").description("옵션 값 ID"),
                                        fieldWithPath("data.name").description("옵션명"),
                                        fieldWithPath("data.stockQuantity").description("재고 수량"),
                                        fieldWithPath("data.extraPrice").description("추가 금액"))));
    }

    @Test
    @DisplayName("상품 옵션 값 삭제 API")
    void deleteProductOptionValueTest() throws Exception {
        UUID optionValueId = UUID.randomUUID();

        Mockito.doNothing().when(productService).deleteProductOptionValue(optionValueId);

        mockMvc.perform(
                        delete("/products/options/values/{optionValueId}", optionValueId)
                                .with(csrf().asHeader()))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "product-option-value-delete",
                                pathParameters(
                                        parameterWithName("optionValueId")
                                                .description("삭제할 옵션 값 ID"))));
    }
}
