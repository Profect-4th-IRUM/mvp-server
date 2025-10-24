package com.irum.come2us.domain.ai.infrastructure.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GeminiRequest {
    private List<Content> contents;

    @Getter
    @Builder
    public static class Content {
        private List<Part> parts;
    }

    @Getter
    @Builder
    public static class Part {
        private String text;
    }

    public static GeminiRequest of(String prompt) {
        // 프롬프트 길이 제한 (500자)
        if (prompt.length() > 500) {
            prompt = prompt.substring(0, 500);
        }

        // 답변 길이 제한 문구 추가
        String finalPrompt = prompt + " 답변을 최대한 간결하게 50자 이하로";

        return GeminiRequest.builder()
                .contents(
                        List.of(
                                Content.builder()
                                        .parts(List.of(Part.builder().text(finalPrompt).build()))
                                        .build()))
                .build();
    }
}
