package com.irum.come2us.domain.deliveryadress;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.deliveryaddress.application.service.DeliveryAddressService;
import com.irum.come2us.domain.deliveryaddress.domain.entity.Address;
import com.irum.come2us.domain.deliveryaddress.presentation.controller.DeliveryAddressController;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.request.AddressDetailUpdateRequest;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.request.DeliveryAddressRegisterRequest;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.request.RecipientUpdateRequest;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.response.DeliveryAddressInfoListResponse;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.response.DeliveryAddressInfoResponse;
import com.irum.come2us.global.config.SecurityTestConfig;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.DeliveryAddressErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DeliveryAddressController.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class DeliveryAddressControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private DeliveryAddressService deliveryAddressService;
    @Autowired private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public DeliveryAddressService deliveryAddressService() {
            return Mockito.mock(DeliveryAddressService.class);
        }
    }

    @Test
    @DisplayName("배송지 주소 등록 API 테스트")
    void deliveryAddressRegisterApiTest() throws Exception {
        DeliveryAddressRegisterRequest request =
                new DeliveryAddressRegisterRequest(
                        "12345", "서울특별시", "강남구", "테헤란로 145", "더피나클 역삼2", "회원1", "010-1234-5678");
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing()
                .when(deliveryAddressService)
                .createDeliveryAddress(any(DeliveryAddressRegisterRequest.class));
        mockMvc.perform(
                        post("/address")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andDo(
                        document(
                                "delivery-address-register",
                                requestFields(
                                        fieldWithPath("postalCode").description("우편번호"),
                                        fieldWithPath("city").description("시/도명"),
                                        fieldWithPath("sigungu").description("시/군/구"),
                                        fieldWithPath("roadName").description("도로명"),
                                        fieldWithPath("addressDetail").description("상세 주소"),
                                        fieldWithPath("recipientName").description("수취인 이름"),
                                        fieldWithPath("recipientContact").description("수취인 연락처"))));
    }

    @Test
    @DisplayName("배송지 주소 조회 API 테스트")
    void deliveryAddressInfoGetApiTest() throws Exception {
        UUID deliveryAddressId = UUID.randomUUID();
        Address address = Address.create("12345", "서울특별시", "강남구", "테헤란로 145", "더피나클 역삼2");
        DeliveryAddressInfoResponse response =
                new DeliveryAddressInfoResponse(
                        deliveryAddressId, address, "회원1", "010-1234-5678", true);
        when(deliveryAddressService.findDeliveryAddress(eq(deliveryAddressId)))
                .thenReturn(response);

        mockMvc.perform(get("/address/{deliveryAddressId}/info", deliveryAddressId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(
                        jsonPath("$.data.deliveryAddressId")
                                .value(response.deliveryAddressId().toString()))
                .andExpect(
                        jsonPath("$.data.address.postalCode")
                                .value(response.address().getPostalCode()))
                .andExpect(jsonPath("$.data.address.city").value(response.address().getCity()))
                .andExpect(
                        jsonPath("$.data.address.sigungu").value(response.address().getSigungu()))
                .andExpect(
                        jsonPath("$.data.address.roadName").value(response.address().getRoadName()))
                .andExpect(
                        jsonPath("$.data.address.addressDetail")
                                .value(response.address().getAddressDetail()))
                .andExpect(jsonPath("$.data.recipientName").value(response.recipientName()))
                .andExpect(jsonPath("$.data.recipientContact").value(response.recipientContact()))
                .andExpect(jsonPath("$.data.isDefault").value(response.isDefault()))
                .andDo(
                        document(
                                "delivery-address-info-get",
                                pathParameters(
                                        parameterWithName("deliveryAddressId")
                                                .description("배송지 주소 ID")),
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("data.deliveryAddressId")
                                                .description("배송지 주소 아이디"),
                                        fieldWithPath("data.address.postalCode")
                                                .description("우편번호"),
                                        fieldWithPath("data.address.city").description("시/도명"),
                                        fieldWithPath("data.address.sigungu").description("시/군/구"),
                                        fieldWithPath("data.address.roadName").description("도로명"),
                                        fieldWithPath("data.address.addressDetail")
                                                .description("상세 주소"),
                                        fieldWithPath("data.recipientName").description("수취인 이름"),
                                        fieldWithPath("data.recipientContact")
                                                .description("수취인 연락처"),
                                        fieldWithPath("data.isDefault").description("기본배송지 여부"))));
    }

    @Test
    @DisplayName("배송지 주소 목록 조회 API 테스트")
    void deliveryAddressInfoListGetApiTest() throws Exception {
        UUID deliveryAddressId1 = UUID.randomUUID();
        UUID deliveryAddressId2 = UUID.randomUUID();

        Address address1 = Address.create("12345", "서울특별시", "강남구", "테헤란로 145", "더피나클 역삼1");
        Address address2 = Address.create("12345", "서울특별시", "강남구", "테헤란로 145", "더피나클 역삼2");

        List<DeliveryAddressInfoResponse> addressInfoList =
                List.of(
                        new DeliveryAddressInfoResponse(
                                deliveryAddressId2, address2, "회원1", "010-1234-5678", true),
                        new DeliveryAddressInfoResponse(
                                deliveryAddressId1, address1, "회원1", "010-1234-5678", true));

        DeliveryAddressInfoListResponse response =
                new DeliveryAddressInfoListResponse(addressInfoList, null, false);
        when(deliveryAddressService.findDeliveryAddressList(any(), eq(10))).thenReturn(response);

        mockMvc.perform(get("/address").param("size", "10").with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.nextCursor").isEmpty())
                .andExpect(
                        jsonPath("$.data.deliveryAddressInfoList[0].deliveryAddressId")
                                .value(deliveryAddressId2.toString()))
                .andDo(
                        document(
                                "delivery-address-info-list-get",
                                queryParameters(
                                        parameterWithName("lastId")
                                                .description("마지막으로 조회된 배송지 ID (첫 페이지는 생략)")
                                                .optional(),
                                        parameterWithName("size")
                                                .description("조회할 페이지 크기 (기본값 10)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("data.deliveryAddressInfoList[]")
                                                .description("배송지 주소 목록"),
                                        fieldWithPath(
                                                        "data.deliveryAddressInfoList[].deliveryAddressId")
                                                .description("배송지 주소 아이디"),
                                        fieldWithPath(
                                                        "data.deliveryAddressInfoList[].address.postalCode")
                                                .description("우편번호"),
                                        fieldWithPath("data.deliveryAddressInfoList[].address.city")
                                                .description("시/도명"),
                                        fieldWithPath(
                                                        "data.deliveryAddressInfoList[].address.sigungu")
                                                .description("시/군/구"),
                                        fieldWithPath(
                                                        "data.deliveryAddressInfoList[].address.roadName")
                                                .description("도로명"),
                                        fieldWithPath(
                                                        "data.deliveryAddressInfoList[].address.addressDetail")
                                                .description("상세 주소"),
                                        fieldWithPath(
                                                        "data.deliveryAddressInfoList[].recipientName")
                                                .description("수취인 이름"),
                                        fieldWithPath(
                                                        "data.deliveryAddressInfoList[].recipientContact")
                                                .description("수취인 연락처"),
                                        fieldWithPath("data.deliveryAddressInfoList[].isDefault")
                                                .description("기본배송지 여부"),
                                        fieldWithPath("data.nextCursor")
                                                .description(
                                                        "다음 페이지 조회를 위한 커서 ID (다음 페이지 없으면 null)"),
                                        fieldWithPath("data.hasNext")
                                                .description("다음 페이지 존재 여부"))));
    }

    @Test
    @DisplayName("수취인 정보 수정 API 테스트")
    void recipientInfoUpdateApiTest() throws Exception {
        UUID deliveryAddressId = UUID.randomUUID();
        RecipientUpdateRequest request = new RecipientUpdateRequest("새 수취인", "010-9999-9999");
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing()
                .when(deliveryAddressService)
                .changeRecipientInfo(eq(deliveryAddressId), any(RecipientUpdateRequest.class));

        mockMvc.perform(
                        patch("/address/{deliveryAddressId}/recipient", deliveryAddressId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "delivery-address-recipient-update",
                                pathParameters(
                                        parameterWithName("deliveryAddressId")
                                                .description("수정할 배송지 ID")),
                                requestFields(
                                        fieldWithPath("newRecipientName").description("변경할 수취인 이름"),
                                        fieldWithPath("newRecipientContact")
                                                .description("변경할 수취인 연락처"))));
    }

    @Test
    @DisplayName("상세 주소 수정 API 테스트")
    void addressDetailUpdateApiTest() throws Exception {
        UUID deliveryAddressId = UUID.randomUUID();
        AddressDetailUpdateRequest request = new AddressDetailUpdateRequest("새로운 상세 주소");
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing()
                .when(deliveryAddressService)
                .changeAddressDetail(eq(deliveryAddressId), any(AddressDetailUpdateRequest.class));

        mockMvc.perform(
                        patch("/address/{deliveryAddressId}/detail", deliveryAddressId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "delivery-address-detail-update",
                                pathParameters(
                                        parameterWithName("deliveryAddressId")
                                                .description("수정할 배송지 ID")),
                                requestFields(
                                        fieldWithPath("newAddressDetail")
                                                .description("변경할 상세 주소"))));
    }

    @Test
    @DisplayName("기본 배송지 설정 API 테스트")
    void defaultDeliveryAddressSetApiTest() throws Exception {
        UUID deliveryAddressId = UUID.randomUUID();

        doNothing()
                .when(deliveryAddressService)
                .changeDefaultDeliveryAddress(eq(deliveryAddressId));

        mockMvc.perform(
                        patch("/address/{deliveryAddressId}/default", deliveryAddressId)
                                .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "delivery-address-default-set",
                                pathParameters(
                                        parameterWithName("deliveryAddressId")
                                                .description("기본 배송지로 설정할 ID"))));
    }

    @Test
    @DisplayName("배송지 주소 삭제 API 테스트")
    void deliveryAddressDeleteApiTest() throws Exception {
        UUID deliveryAddressId = UUID.randomUUID();

        doNothing().when(deliveryAddressService).removeDeliveryAddress(eq(deliveryAddressId));

        mockMvc.perform(delete("/address/{deliveryAddressId}", deliveryAddressId).with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "delivery-address-delete",
                                pathParameters(
                                        parameterWithName("deliveryAddressId")
                                                .description("삭제할 배송지 ID"))));
    }

    @Test
    @DisplayName("배송지 주소 등록 실패 - 우편번호 누락")
    void deliveryAddressRegisterApiTest_Fail_PostalCodeMissing() throws Exception {
        DeliveryAddressRegisterRequest request =
                new DeliveryAddressRegisterRequest(
                        "", "서울특별시", "강남구", "테헤란로 145", "더피나클 역삼2", "회원1", "010-1234-5678");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/address")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("우편번호는 필수 입력값입니다."))
                .andDo(
                        document(
                                "delivery-address-register-fail-postal-code-missing",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }

    @Test
    @DisplayName("배송지 주소 조회 실패 - 존재하지 않는 주소 ID")
    void deliveryAddressInfoGetApiTest_Fail_NotFound() throws Exception {
        UUID notFoundId = UUID.randomUUID();
        doThrow(new CommonException(DeliveryAddressErrorCode.DELIVERY_ADDRESS_NOT_FOUND))
                .when(deliveryAddressService)
                .findDeliveryAddress(eq(notFoundId));

        mockMvc.perform(get("/address/{deliveryAddressId}/info", notFoundId).with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(
                        jsonPath("$.status")
                                .value(
                                        DeliveryAddressErrorCode.DELIVERY_ADDRESS_NOT_FOUND
                                                .getHttpStatus()
                                                .value()))
                .andExpect(
                        jsonPath("$.data.errorClassName")
                                .value(DeliveryAddressErrorCode.DELIVERY_ADDRESS_NOT_FOUND.name()))
                .andExpect(
                        jsonPath("$.data.message")
                                .value(
                                        DeliveryAddressErrorCode.DELIVERY_ADDRESS_NOT_FOUND
                                                .getMessage()))
                .andDo(
                        document(
                                "delivery-address-info-get-fail-not-found",
                                pathParameters(
                                        parameterWithName("deliveryAddressId")
                                                .description("조회할 배송지 ID")),
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("에러 상태 코드 (404)"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName")
                                                .description("에러 코드 (DELIVERY_ADDRESS_NOT_FOUND)"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }

    @Test
    @DisplayName("수취인 정보 수정 실패 - 존재하지 않는 주소 ID")
    void recipientInfoUpdateApiTest_Fail_NotFound() throws Exception {
        UUID notFoundId = UUID.randomUUID();
        RecipientUpdateRequest request = new RecipientUpdateRequest("새 수취인", "010-9999-9999");
        String requestJson = objectMapper.writeValueAsString(request);

        doThrow(new CommonException(DeliveryAddressErrorCode.DELIVERY_ADDRESS_NOT_FOUND))
                .when(deliveryAddressService)
                .changeRecipientInfo(eq(notFoundId), any(RecipientUpdateRequest.class));

        mockMvc.perform(
                        patch("/address/{deliveryAddressId}/recipient", notFoundId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(
                        jsonPath("$.status")
                                .value(
                                        DeliveryAddressErrorCode.DELIVERY_ADDRESS_NOT_FOUND
                                                .getHttpStatus()
                                                .value()))
                .andExpect(
                        jsonPath("$.data.errorClassName")
                                .value(DeliveryAddressErrorCode.DELIVERY_ADDRESS_NOT_FOUND.name()))
                .andExpect(
                        jsonPath("$.data.message")
                                .value(
                                        DeliveryAddressErrorCode.DELIVERY_ADDRESS_NOT_FOUND
                                                .getMessage()))
                .andDo(
                        document(
                                "delivery-address-recipient-update-fail-not-found",
                                pathParameters(
                                        parameterWithName("deliveryAddressId")
                                                .description("수정할 배송지 ID")),
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("에러 상태 코드 (404)"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName")
                                                .description("에러 코드 (DELIVERY_ADDRESS_NOT_FOUND)"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }

    @Test
    @DisplayName("배송지 주소 삭제 실패 - 다른 회원의 주소 삭제 시도")
    void deliveryAddressDeleteApiTest_Fail_NoPermission() throws Exception {
        UUID otherUserIdAddressId = UUID.randomUUID();

        doThrow(new CommonException(MemberErrorCode.UNAUTHORIZED_ACCESS))
                .when(deliveryAddressService)
                .removeDeliveryAddress(eq(otherUserIdAddressId));

        mockMvc.perform(delete("/address/{deliveryAddressId}", otherUserIdAddressId).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(
                        jsonPath("$.status")
                                .value(MemberErrorCode.UNAUTHORIZED_ACCESS.getHttpStatus().value()))
                .andExpect(
                        jsonPath("$.data.errorClassName")
                                .value(MemberErrorCode.UNAUTHORIZED_ACCESS.name()))
                .andExpect(
                        jsonPath("$.data.message")
                                .value(MemberErrorCode.UNAUTHORIZED_ACCESS.getMessage()))
                .andDo(
                        document(
                                "delivery-address-delete-fail-no-permission",
                                pathParameters(
                                        parameterWithName("deliveryAddressId")
                                                .description("삭제 시도할 배송지 ID")),
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("에러 상태 코드 (403)"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName")
                                                .description("에러 코드 (NO_PERMISSION_TO_RESOURCE)"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }
}
