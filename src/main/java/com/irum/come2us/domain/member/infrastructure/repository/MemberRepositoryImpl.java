package com.irum.come2us.domain.member.infrastructure.repository;

import static com.irum.come2us.domain.member.domain.entity.QMember.member;

import com.irum.come2us.domain.member.domain.repository.MemberRepositoryCustom;
import com.irum.come2us.domain.member.presentation.dto.response.MemberInfoResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberInfoResponse> findMembersByCursor(Long lastMemberId, int pageSize) {
        return queryFactory
                .select(
                        Projections.constructor(
                                MemberInfoResponse.class,
                                member.memberId,
                                member.email,
                                member.name,
                                member.contact,
                                member.role))
                .from(member)
                .where(cursorCondition(lastMemberId))
                .orderBy(member.memberId.asc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression cursorCondition(Long lastMemberId) {
        return lastMemberId == null ? null : member.memberId.gt(lastMemberId);
    }
}
