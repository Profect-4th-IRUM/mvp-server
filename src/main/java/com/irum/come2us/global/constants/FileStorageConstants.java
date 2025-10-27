package com.irum.come2us.global.constants;

public final class FileStorageConstants {

    private FileStorageConstants() {}

    /** 허용 이미지 확장자 정규식 (대소문자 무시) */
    public static final String IMAGE_EXTENSION_REGEX =
            "^[\\p{L}\\p{N}_\\-.]+\\.(?i)(jpg|jpeg|png|gif|bmp|webp)$";

    /** 최대 업로드 허용 크기 (10MB) */
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
}
