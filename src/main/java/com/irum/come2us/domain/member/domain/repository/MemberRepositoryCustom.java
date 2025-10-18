package com.irum.come2us.domain.member.domain.repository;

import com.irum.come2us.domain.member.presentation.dto.response.MemberInfoResponse;
import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberInfoResponse> findMembersByCursor(Long lastMemberId, int pageSize);
}
