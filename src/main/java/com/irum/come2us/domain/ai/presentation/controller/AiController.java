package com.irum.come2us.domain.ai.presentation.controller;

import com.irum.come2us.domain.ai.application.service.AiService;
import com.irum.come2us.domain.ai.presentation.dto.request.AiRequest;
import com.irum.come2us.domain.ai.presentation.dto.response.AiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/generate")
    public ResponseEntity<AiResponse> generateDescription(@Valid @RequestBody AiRequest request) {
        AiResponse response = aiService.generateDescription(request.prompt(), request.productId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{productId}")
    public ResponseEntity<List<AiResponse>> getAiHistory(@PathVariable String productId) {
        List<AiResponse> history = aiService.getAiHistoryByProduct(productId);
        return ResponseEntity.ok(history);
    }
}
