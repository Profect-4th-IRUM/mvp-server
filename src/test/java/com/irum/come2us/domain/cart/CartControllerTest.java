package com.irum.come2us.domain.cart;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.cart.application.service.CartService;
import com.irum.come2us.domain.cart.presentation.controller.CartController;
import com.irum.come2us.domain.cart.presentation.dto.request.CartCreateRequest;
import com.irum.come2us.domain.cart.presentation.dto.request.CartUpdateRequest;
import com.irum.come2us.domain.cart.presentation.dto.response.CartResponse;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartController.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, TestConfig.class})
class CartControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private CartService cartService;
    @Autowired private ObjectMapper objectMapper;

    private final UUID mockCartId = UUID.randomUUID();
    private final UUID mockOptionValueId = UUID.randomUUID();

    @Test
    @DisplayName("장바구니 추가 API (CUSTOMER)")
    void createCartApiTest() throws Exception {
        CartCreateRequest request = new CartCreateRequest(1L, mockOptionValueId, 2);
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing().when(cartService).createCart(any(CartCreateRequest.class));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/carts")
                                .with(csrf())
                                .with(user("1").roles("CUSTOMER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "cart-create",
                                requestFields(
                                        fieldWithPath("memberId").description("회원 ID"),
                                        fieldWithPath("optionValueId").description("옵션 값 ID"),
                                        fieldWithPath("quantity").description("상품 수량"))));
    }

    @Test
    @DisplayName("장바구니 목록 조회 API (CUSTOMER)")
    void getCartListApiTest() throws Exception {
        CartResponse response =
                new CartResponse(
                        mockCartId,
                        mockOptionValueId,
                        "테스트 상품",
                        "옵션A",
                        "https://example.com/image.jpg",
                        2,
                        10000,
                        500,
                        10500,
                        21000);

        when(cartService.getCartListByMember()).thenReturn(List.of(response));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/carts")
                                .with(csrf().asHeader())
                                .with(user("1").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "cart-get-list",
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("data[].cartId").description("장바구니 ID"),
                                        fieldWithPath("data[].optionValueId")
                                                .description("옵션 값 ID"),
                                        fieldWithPath("data[].productName").description("상품 이름"),
                                        fieldWithPath("data[].optionValueName")
                                                .description("옵션 이름"),
                                        fieldWithPath("data[].imageUrl").description("대표 이미지 URL"),
                                        fieldWithPath("data[].quantity").description("수량"),
                                        fieldWithPath("data[].basePrice").description("상품 기본가"),
                                        fieldWithPath("data[].extraPrice").description("옵션 추가금"),
                                        fieldWithPath("data[].unitPrice").description("단가"),
                                        fieldWithPath("data[].lineTotal").description("총 금액"))));
    }

    @Test
    @DisplayName("장바구니 수정 API (CUSTOMER)")
    void updateCartApiTest() throws Exception {
        CartUpdateRequest request = new CartUpdateRequest(5);
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing().when(cartService).updateCart(eq(mockCartId), any(CartUpdateRequest.class));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/carts/{cartId}", mockCartId)
                                .with(csrf())
                                .with(user("1").roles("CUSTOMER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "cart-update",
                                pathParameters(
                                        parameterWithName("cartId").description("수정할 장바구니 ID")),
                                requestFields(fieldWithPath("quantity").description("변경할 수량"))));
    }

    @Test
    @DisplayName("장바구니 삭제 API (CUSTOMER)")
    void deleteCartApiTest() throws Exception {
        doNothing().when(cartService).deleteCart(eq(mockCartId));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/carts/{cartId}", mockCartId)
                                .with(csrf())
                                .with(user("1").roles("CUSTOMER")))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "cart-delete",
                                pathParameters(
                                        parameterWithName("cartId").description("삭제할 장바구니 ID"))));
    }
}
