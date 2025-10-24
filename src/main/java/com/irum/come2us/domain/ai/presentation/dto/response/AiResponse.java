package com.irum.come2us.domain.ai.presentation.dto.response;

import com.irum.come2us.domain.ai.domain.entity.Ai;

public record AiResponse(String aiId, String question, String answer, String productId) {
    public static AiResponse from(Ai ai) {
        return new AiResponse(
                ai.getAiId().toString(),
                ai.getQuestion(),
                ai.getAnswer(),
                ai.getProduct().getId().toString());
    }
}
