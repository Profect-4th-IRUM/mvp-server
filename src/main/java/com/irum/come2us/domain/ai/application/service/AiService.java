package com.irum.come2us.domain.ai.application.service;

import com.irum.come2us.domain.ai.domain.entity.Ai;
import com.irum.come2us.domain.ai.domain.repository.AiRepository;
import com.irum.come2us.domain.ai.infrastructure.dto.GeminiRequest;
import com.irum.come2us.domain.ai.infrastructure.dto.GeminiResponse;
import com.irum.come2us.domain.ai.presentation.dto.response.AiResponse;
import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.AiErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductErrorCode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiService {

    private final AiRepository aiRepository;
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;


    @Value("${google.gemini.api.url}")
    private String geminiUrl;

    /** AI를 사용하여 상품 설명 생성 */
    @Transactional
    public AiResponse generateDescription(String prompt, String productId) {
        try {
            // 1. 상품 조회
            Product product =
                    productRepository
                            .findById(UUID.fromString(productId))
                            .orElseThrow(
                                    () -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

            // 2. Gemini API 호출
            String generatedDescription = callGeminiApi(prompt);

            // 3. AI 요청/응답 저장
            Ai ai = Ai.create(prompt, generatedDescription, product);
            Ai savedAi = aiRepository.save(ai);

            // 4. 응답 반환
            return AiResponse.from(savedAi);

        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI 설명 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new CommonException(AiErrorCode.AI_PROCESS_ERROR);
        }
    }

    /** 특정 상품의 AI 생성 이력 조회 */
    public List<AiResponse> getAiHistoryByProduct(String productId) {
        List<Ai> aiList = aiRepository.findByProductProductId(UUID.fromString(productId));
        return aiList.stream().map(AiResponse::from).toList();
    }

    /** Gemini API 호출 */
    private String callGeminiApi(String prompt) {
        try {

            // 요청 객체 생성
            GeminiRequest request = GeminiRequest.of(prompt);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // HTTP 엔티티 생성
            HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

            // API 호출
            ResponseEntity<GeminiResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, GeminiResponse.class);

            // 응답 처리
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String generatedText = response.getBody().getGeneratedText();
                if (generatedText.isEmpty()) {
                    throw new CommonException(AiErrorCode.EMPTY_RESPONSE);
                }
                return generatedText;
            }

            throw new CommonException(AiErrorCode.API_REQUEST_FAILED);

        } catch (RestClientException e) {
            log.error("Gemini API 호출 실패: {}", e.getMessage());
            throw new CommonException(AiErrorCode.API_REQUEST_FAILED);
        }
    }
}
