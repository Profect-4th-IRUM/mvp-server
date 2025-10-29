package com.irum.come2us.domain.review.presentation.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record ReviewListResponse<T>(List<T> reviewList, PageInfo pageInfo) {
    public static <T> ReviewListResponse<T> from(Page<T> page) {
        return new ReviewListResponse<>(page.getContent(), PageInfo.from(page));
    }

    public record PageInfo(
            int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
        public static PageInfo from(Page<?> page) {
            return new PageInfo(
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.isLast());
        }
    }
}
