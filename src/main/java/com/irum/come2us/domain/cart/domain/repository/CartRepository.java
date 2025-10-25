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
            @Param("memberId") @NotNull Long memberId, @Param("optionValueId") UUID optionValueId);

    // 특정 회원의 장바구니 + 상품/옵션 통합 조회
    // TODO: 이미지 조인 추후 추가 예정
    @Query(
            """
        SELECT DISTINCT c
        FROM Cart c
        JOIN FETCH c.optionValue ov
        JOIN FETCH ov.optionGroup og
        JOIN FETCH og.product p
        WHERE c.member.memberId = :memberId
    """)
    List<Cart> findAllWithProductByMemberId(@Param("memberId") Long memberId);
}
