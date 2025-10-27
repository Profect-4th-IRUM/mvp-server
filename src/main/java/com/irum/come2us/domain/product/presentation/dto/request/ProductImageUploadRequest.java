package com.irum.come2us.domain.product.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record ProductImageUploadRequest(
        @NotEmpty(message = "하나 이상의 이미지를 업로드해야 합니다.") List<MultipartFile> images) {}
