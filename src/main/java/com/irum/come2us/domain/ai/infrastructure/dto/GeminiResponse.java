package com.irum.come2us.domain.ai.infrastructure.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GeminiResponse {
    private List<Candidate> candidates;

    @Getter
    @NoArgsConstructor
    public static class Candidate {
        private Content content;
    }

    @Getter
    @NoArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Getter
    @NoArgsConstructor
    public static class Part {
        private String text;
    }

    public String getGeneratedText() {
        if (candidates != null && !candidates.isEmpty()) {
            Content content = candidates.get(0).getContent();
            if (content != null && content.getParts() != null && !content.getParts().isEmpty()) {
                return content.getParts().get(0).getText().trim();
            }
        }
        return "";
    }
}
