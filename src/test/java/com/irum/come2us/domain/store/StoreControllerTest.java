package com.irum.come2us.domain.store;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.product.presentation.dto.request.ProductCursorResponse;
import com.irum.come2us.domain.product.presentation.dto.response.ProductResponse;
import com.irum.come2us.domain.store.application.service.StoreService;
import com.irum.come2us.domain.store.presentation.controller.StoreController;
import com.irum.come2us.domain.store.presentation.dto.request.StoreCreateRequest;
import com.irum.come2us.domain.store.presentation.dto.request.StoreUpdateRequest;
import com.irum.come2us.domain.store.presentation.dto.response.StoreCreateResponse;
import com.irum.come2us.domain.store.presentation.dto.response.StoreInfoResponse;
import com.irum.come2us.domain.store.presentation.dto.response.StoreListResponse;
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

@WebMvcTest(StoreController.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class StoreControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private StoreService storeService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public StoreService storeService() {
            return Mockito.mock(StoreService.class);
        }
    }

    // 1. 상점 생성
    @Test
    @DisplayName("상점 생성 API")
    void createStoreTest() throws Exception {
        // given
        StoreCreateRequest request =
                new StoreCreateRequest(
                        "맛있는상점", "010-1234-5678", "서울특별시 강남구 테헤란로 1", "1234567890", "9876543210");

        UUID storeId = UUID.randomUUID();
        StoreCreateResponse response = new StoreCreateResponse(storeId);

        Mockito.when(storeService.createStore(any(StoreCreateRequest.class))).thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        post("/stores")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.storeId").value(storeId.toString()))
                .andDo(
                        document(
                                "store-create",
                                requestFields(
                                        fieldWithPath("name").description("상점명"),
                                        fieldWithPath("contact").description("연락처"),
                                        fieldWithPath("address").description("주소"),
                                        fieldWithPath("businessRegistrationNumber")
                                                .description("사업자등록번호"),
                                        fieldWithPath("telemarketingRegistrationNumber")
                                                .description("통신판매업번호"))));
    }

    // 2. 상점 수정
    @Test
    @DisplayName("상점 수정 API")
    void updateStoreTest() throws Exception {
        UUID storeId = UUID.randomUUID();
        StoreUpdateRequest request =
                new StoreUpdateRequest("새로운상점", "010-9999-9999", "서울시 중구 세종대로 10");
        String requestJson = objectMapper.writeValueAsString(request);

        Mockito.doNothing()
                .when(storeService)
                .changeStore(any(UUID.class), any(StoreUpdateRequest.class));

        mockMvc.perform(
                        patch("/stores/{storeId}", storeId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "store-update",
                                pathParameters(
                                        parameterWithName("storeId").description("수정할 상점 ID")),
                                requestFields(
                                        fieldWithPath("name").description("상점명"),
                                        fieldWithPath("contact").description("연락처"),
                                        fieldWithPath("address").description("주소"))));
    }

    // 3. 상점 삭제
    @Test
    @DisplayName("상점 삭제 API")
    void deleteStoreTest() throws Exception {
        UUID storeId = UUID.randomUUID();
        Mockito.doNothing().when(storeService).withdrawStore(storeId);

        mockMvc.perform(delete("/stores/{storeId}", storeId).with(csrf().asHeader()))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "store-delete",
                                pathParameters(
                                        parameterWithName("storeId").description("삭제할 상점 ID"))));
    }

    // 4. 상점 목록 조회
    @Test
    @DisplayName("상점 목록 조회 API")
    void getStoreListTest() throws Exception {
        List<StoreListResponse> storeList =
                List.of(
                        new StoreListResponse(UUID.randomUUID(), "상점1", "010-1111-1111", "서울시 강남구"),
                        new StoreListResponse(
                                UUID.randomUUID(), "상점2", "010-2222-2222", "서울시 종로구"));

        Mockito.when(storeService.findStoreList(any(), anyInt())).thenReturn(storeList);

        mockMvc.perform(get("/stores").param("size", "10").with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andDo(
                        document(
                                "store-list",
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data[].id").description("상점 식별자"),
                                        fieldWithPath("data[].name").description("상점명"),
                                        fieldWithPath("data[].contact").description("연락처"),
                                        fieldWithPath("data[].address").description("주소"))));
    }

    // 5. 상점 상세 조회
    @Test
    @DisplayName("상점 상세 조회 API")
    void getStoreDetailTest() throws Exception {
        UUID storeId = UUID.randomUUID();
        StoreInfoResponse response =
                new StoreInfoResponse(
                        storeId, "상점1", "010-1234-5678", "서울특별시 강남구", "1234567890", "9876543210");

        Mockito.when(storeService.findStoreInfo(storeId)).thenReturn(response);

        mockMvc.perform(get("/stores/{storeId}", storeId).with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(storeId.toString()))
                .andExpect(jsonPath("$.data.name").value("상점1"))
                .andDo(
                        document(
                                "store-detail",
                                pathParameters(
                                        parameterWithName("storeId").description("조회할 상점 ID")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.id").description("상점 식별자"),
                                        fieldWithPath("data.name").description("상점명"),
                                        fieldWithPath("data.contact").description("연락처"),
                                        fieldWithPath("data.address").description("주소"),
                                        fieldWithPath("data.businessRegistrationNumber")
                                                .description("사업자등록번호"),
                                        fieldWithPath("data.telemarketingRegistrationNumber")
                                                .description("통신판매업번호"))));
    }

    // 6. 내 상점 상품 목록 조회
    @Test
    @DisplayName("내 상점 상품 목록 조회 API")
    void getMyStoreProductsTest() throws Exception {
        ProductResponse product =
                new ProductResponse(
                        UUID.randomUUID(),
                        "상품1",
                        "짧은설명",
                        "상세설명",
                        10000,
                        true,
                        4.5,
                        10,
                        UUID.randomUUID(),
                        "카테고리A");

        ProductCursorResponse response = new ProductCursorResponse(List.of(product), null);

        Mockito.when(storeService.getMyStoreProducts(any(), anyInt())).thenReturn(response);

        mockMvc.perform(get("/stores/products").param("size", "10").with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "store-my-products",
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.products[].id").description("상품 식별자"),
                                        fieldWithPath("data.products[].name").description("상품명"),
                                        fieldWithPath("data.products[].description")
                                                .description("간단 설명"),
                                        fieldWithPath("data.products[].detailDescription")
                                                .description("상세 설명"),
                                        fieldWithPath("data.products[].price").description("가격"),
                                        fieldWithPath("data.products[].isPublic")
                                                .description("공개 여부"),
                                        fieldWithPath("data.products[].avgRating")
                                                .description("평균 평점"),
                                        fieldWithPath("data.products[].reviewCount")
                                                .description("리뷰 개수"),
                                        fieldWithPath("data.products[].categoryId")
                                                .description("카테고리 ID"),
                                        fieldWithPath("data.products[].categoryName")
                                                .description("카테고리 이름"),
                                        fieldWithPath("data.nextCursor")
                                                .description("다음 페이지 커서 (없으면 null)"))));
    }

    // 7. 특정 상점 상품 목록 조회
    @Test
    @DisplayName("특정 상점 상품 목록 조회 API")
    void getStoreProductsTest() throws Exception {
        UUID storeId = UUID.randomUUID();
        ProductResponse product =
                new ProductResponse(
                        UUID.randomUUID(),
                        "상품2",
                        "짧은설명2",
                        "상세설명2",
                        12000,
                        true,
                        4.8,
                        5,
                        UUID.randomUUID(),
                        "카테고리B");

        ProductCursorResponse response = new ProductCursorResponse(List.of(product), null);
        Mockito.when(storeService.getStoreProducts(any(), any(), anyInt())).thenReturn(response);

        mockMvc.perform(
                        get("/stores/{storeId}/products", storeId)
                                .param("size", "10")
                                .with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "store-products",
                                pathParameters(
                                        parameterWithName("storeId")
                                                .description("상품 목록 조회할 특정 상점 ID")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.products[].id").description("상품 식별자"),
                                        fieldWithPath("data.products[].name").description("상품명"),
                                        fieldWithPath("data.products[].description")
                                                .description("간단 설명"),
                                        fieldWithPath("data.products[].detailDescription")
                                                .description("상세 설명"),
                                        fieldWithPath("data.products[].price").description("가격"),
                                        fieldWithPath("data.products[].isPublic")
                                                .description("공개 여부"),
                                        fieldWithPath("data.products[].avgRating")
                                                .description("평균 평점"),
                                        fieldWithPath("data.products[].reviewCount")
                                                .description("리뷰 개수"),
                                        fieldWithPath("data.products[].categoryId")
                                                .description("카테고리 ID"),
                                        fieldWithPath("data.products[].categoryName")
                                                .description("카테고리 이름"),
                                        fieldWithPath("data.nextCursor")
                                                .description("다음 페이지 커서 (없으면 null)"))));
    }

    // 상점명 누락
    @Test
    @DisplayName("상점 생성 실패 - 상점명 누락")
    void registerStoreApiTest_Fail_BlankStoreName() throws Exception {
        StoreCreateRequest request =
                new StoreCreateRequest(
                        "", // 이름 누락
                        "010-1234-5678",
                        "서울특별시 강남구 테헤란로 1",
                        "1234567890",
                        "9876543210");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/stores")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("상점명은 필수 입력값입니다."))
                .andDo(
                        document(
                                "store-create-fail-blank-name",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }

    // 연락처 누락
    @Test
    @DisplayName("상점 생성 실패 - 연락처 누락")
    void registerStoreApiTest_Fail_BlankContact() throws Exception {
        StoreCreateRequest request =
                new StoreCreateRequest("맛있는상점", "", "서울특별시 강남구 테헤란로 1", "1234567890", "9876543210");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/stores")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("연락처는 필수 입력값입니다."))
                .andDo(
                        document(
                                "store-create-fail-blank-contact",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }

    // 주소 누락
    @Test
    @DisplayName("상점 생성 실패 - 주소 누락")
    void registerStoreApiTest_Fail_BlankAddress() throws Exception {
        StoreCreateRequest request =
                new StoreCreateRequest("맛있는상점", "010-1234-5678", "", "1234567890", "9876543210");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/stores")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("주소는 필수 입력값입니다."))
                .andDo(
                        document(
                                "store-create-fail-blank-address",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }

    // 사업자등록번호 누락
    @Test
    @DisplayName("상점 생성 실패 - 사업자등록번호 누락")
    void registerStoreApiTest_Fail_BlankBusinessNumber() throws Exception {
        StoreCreateRequest request =
                new StoreCreateRequest(
                        "맛있는상점", "010-1234-5678", "서울특별시 강남구 테헤란로 1", "", "9876543210");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/stores")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("사업자등록번호는 필수 입력값입니다."))
                .andDo(
                        document(
                                "store-create-fail-blank-business-number",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }

    // 통신판매업번호 누락
    @Test
    @DisplayName("상점 생성 실패 - 통신판매업번호 누락")
    void registerStoreApiTest_Fail_BlankTelemarketingNumber() throws Exception {
        StoreCreateRequest request =
                new StoreCreateRequest(
                        "맛있는상점", "010-1234-5678", "서울특별시 강남구 테헤란로 1", "1234567890", "");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/stores")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("통신판매업번호는 필수 입력값입니다."))
                .andDo(
                        document(
                                "store-create-fail-blank-telemarketing-number",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }
}
