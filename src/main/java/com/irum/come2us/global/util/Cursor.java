package com.irum.come2us.global.util;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Deprecated
@AllArgsConstructor
public class Cursor {
    private LocalDateTime createdAt;
    private UUID id;

    public static Cursor fromBase64(String base64) {
        String decoded = new String(Base64.getDecoder().decode(base64));
        // createdAt|id
        String[] parts = decoded.split("\\|");
        return new Cursor(LocalDateTime.parse(parts[0]), UUID.fromString(parts[1]));
    }

    @Override
    public String toString() {
        String cursor = createdAt + "|" + id;
        return Base64.getEncoder().encodeToString(cursor.toString().getBytes());
    }
}
