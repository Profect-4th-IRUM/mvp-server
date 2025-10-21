package com.irum.come2us.domain.review.presentation.dto.request;

import java.util.List;

public record ReviewUpdateRequest(String content, Integer rate, List<String> imageUrls) {}
