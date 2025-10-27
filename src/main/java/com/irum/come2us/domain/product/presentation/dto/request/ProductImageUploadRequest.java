package com.irum.come2us.domain.product.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record ProductImageUploadRequest(
        @NotNull(message = "상품 ID는 필수 입력값입니다.") UUID productId,
        @NotEmpty(message = "하나 이상의 이미지를 업로드해야 합니다.") List<MultipartFile> images) {}
