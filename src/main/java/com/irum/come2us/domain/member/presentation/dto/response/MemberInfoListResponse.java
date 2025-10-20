package com.irum.come2us.domain.member.presentation.dto.response;

import java.util.List;

public record MemberInfoListResponse(
        List<MemberInfoResponse> memberInfoList, Long nextCursor, boolean hasNext) {}
