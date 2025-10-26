package com.irum.come2us.domain.product.application.service;

import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductImageErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class FileStorageService {

    private static final String UPLOAD_DIR = "uploads/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Pattern IMAGE_PATTERN =
            Pattern.compile("(?i).*(\\.jpg|\\.jpeg|\\.png)$"); // 대소문자 무시 확장자 검사

    public String save(MultipartFile file) {
        validateFile(file);

        try {
            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + uniqueName);

            Files.createDirectories(path.getParent());
            file.transferTo(path.toFile());

            log.info("파일 저장 완료: {}", path.toAbsolutePath());
            return path.toString(); // S3 시에는 URL로 대체
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new CommonException(ProductImageErrorCode.FILE_SAVE_FAILED);
        }
    }

    public void delete(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            log.info("파일 삭제 완료: {}", path.toAbsolutePath());
        } catch (IOException e) {
            log.warn("파일 삭제 실패: {}", filePath);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CommonException(ProductImageErrorCode.INVALID_FILE);
        }

        if (!IMAGE_PATTERN.matcher(file.getOriginalFilename()).matches()) {
            throw new CommonException(ProductImageErrorCode.INVALID_FILE_TYPE);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CommonException(ProductImageErrorCode.FILE_TOO_LARGE);
        }
    }
}
