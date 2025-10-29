package com.irum.come2us.domain.product.application.service;

import com.irum.come2us.global.constants.FileStorageConstants;
import com.irum.come2us.global.infrastructure.properties.FileProperties;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductImageErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    private final FileProperties fileProperties;

    /** 파일 저장 */
    public String save(MultipartFile file) {
        try {
            String cleanFilename =
                    StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            if (!Pattern.matches(FileStorageConstants.IMAGE_EXTENSION_REGEX, cleanFilename)) {
                throw new CommonException(ProductImageErrorCode.INVALID_FILE_FORMAT);
            }

            if (file.getSize() > FileStorageConstants.MAX_FILE_SIZE) {
                throw new CommonException(ProductImageErrorCode.FILE_TOO_LARGE);
            }

            String uniqueName = UUID.randomUUID() + "_" + cleanFilename;

            /*
            if (fileProperties.storage().equalsIgnoreCase("s3")) {
                // TODO: S3Uploader 로직 추가 예정
                return fileProperties.s3().baseUrl() + uniqueName;
            }
            */

            Path uploadDir = Paths.get(fileProperties.uploadDir());
            Files.createDirectories(uploadDir);

            Path targetPath = uploadDir.resolve(uniqueName).normalize();
            file.transferTo(targetPath.toFile());

            log.info("파일 저장 완료: {}", targetPath.toAbsolutePath());
            return targetPath.toString();

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
