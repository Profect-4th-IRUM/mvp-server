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

    /** 로컬 디렉토리에 파일 저장 */
    public String save(MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename();
            if (originalName == null) {
                throw new CommonException(ProductImageErrorCode.INVALID_FILE_FORMAT);
            }

            String uniqueName = UUID.randomUUID() + "_" + originalName;
            Path path = Paths.get(UPLOAD_DIR, uniqueName);

            Files.createDirectories(path.getParent());
            file.transferTo(path.toFile());

            log.info("파일 저장 완료: {}", path.toAbsolutePath());
            return path.toString();
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new CommonException(ProductImageErrorCode.FILE_SAVE_FAILED);
        }
    }

    /** 파일 삭제 */
    public void delete(String filePath) {
        try {
            if (filePath == null || filePath.isBlank()) return;
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            log.info("파일 삭제 완료: {}", path.toAbsolutePath());
        } catch (IOException e) {
            log.warn("파일 삭제 실패: {}", filePath, e);
        }
    }
}
