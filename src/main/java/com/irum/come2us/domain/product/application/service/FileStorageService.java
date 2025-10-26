package com.irum.come2us.domain.product.application.service;

import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductImageErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class FileStorageService {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    /** 파일 저장 */
    public String save(MultipartFile file) {
        try {
            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);

            // 디렉토리 없으면 생성
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(uniqueName);
            file.transferTo(filePath.toFile());

            log.info("파일 저장 완료: {}", filePath.toAbsolutePath());
            return filePath.toAbsolutePath().toString(); // 절대경로 반환 (S3 시 URL로 대체)
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new CommonException(ProductImageErrorCode.FILE_SAVE_FAILED);
        }
    }

    /** 파일 삭제 */
    public void delete(String filePath) {
        try {
            if (filePath == null) return;
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            log.info("파일 삭제 완료: {}", path.toAbsolutePath());
        } catch (IOException e) {
            log.warn("파일 삭제 실패: {}", filePath);
        }
    }
}
