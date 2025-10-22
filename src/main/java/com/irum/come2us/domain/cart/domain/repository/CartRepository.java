package com.irum.come2us.domain.cart.domain.repository;

import com.irum.come2us.domain.cart.domain.entity.Cart;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    // 특정 회원이 같은 옵션 상품을 장바구니에 담은 내역 찾기
    @Query(
            """
           SELECT c
           FROM Cart c
           WHERE c.member.memberId = :memberId
           AND c.optionValue.id = :optionValueId
           """)
    Cart findByMemberIdAndOptionValueId(
            @Param("memberId") @NotNull(message = "회원 ID는 필수 입력값입니다.") Long memberId,
            @Param("optionValueId") UUID optionValueId);

    // 특정 회원의 전체 장바구니 목록 조회
    @Query(
            """
           SELECT c
           FROM Cart c
           WHERE c.member.memberId = :memberId
           """)
    List<Cart> findAllByMemberId(@Param("memberId") Long memberId);
}
