package com.irum.come2us.domain.product.application.service;

import com.irum.come2us.global.constants.FileStorageConstants;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductImageErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class FileStorageService {

    /** 파일 저장 */
    public String save(MultipartFile file) {
        try {
            // 파일명 정화 (경로 탈출 방지)
            String originalName = file.getOriginalFilename();
            if (originalName == null || originalName.isBlank()) {
                throw new CommonException(ProductImageErrorCode.FILE_SAVE_FAILED);
            }

            String cleanFilename = StringUtils.cleanPath(originalName);
            String uniqueName = UUID.randomUUID() + "_" + cleanFilename;

            // 디렉토리 생성
            Path uploadPath = Paths.get(FileStorageConstants.UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 안전하게 경로 연결
            Path filePath = uploadPath.resolve(uniqueName).normalize();

            file.transferTo(filePath.toFile());
            log.info("파일 저장 완료: {}", filePath.toAbsolutePath());
            return filePath.toAbsolutePath().toString(); // S3 시 URL로 대체
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new CommonException(ProductImageErrorCode.FILE_SAVE_FAILED);
        }
    }

    /** 파일 삭제 */
    public void delete(String filePath) {
        try {
            if (filePath == null || filePath.isBlank()) return;
            Path path = Paths.get(filePath).normalize();
            Files.deleteIfExists(path);
            log.info("파일 삭제 완료: {}", path.toAbsolutePath());
        } catch (IOException e) {
            log.warn("파일 삭제 실패: {}", filePath);
        }
    }
}
