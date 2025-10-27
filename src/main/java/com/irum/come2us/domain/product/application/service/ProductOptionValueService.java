package com.irum.come2us.domain.product.application.service;

import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.product.domain.entity.ProductOptionValue;
import com.irum.come2us.domain.product.domain.repository.ProductOptionValueRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductErrorCode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductOptionValueService {
    private final OrderDetailRepository orderDetailRepository;
    private final ProductOptionValueRepository productOptionValueRepository;

    /** 주문에 포함된 모든 상품의 재고를 다시 늘립니다. (보상 트랜잭션) 추후에 SAGA패턴으로 변경 가능성 있음 */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void rollbackStockForOrder(List<OrderDetail> orderDetailList) {

        // 재고를 되돌릴 ProductOptionValue ID 목록 추출
        List<UUID> optionIds =
                orderDetailList.stream()
                        .map(detail -> detail.getProductOptionValue().getId())
                        .distinct() // 중복 ID 제거
                        .toList();

        // 락 획득
        List<ProductOptionValue> options =
                productOptionValueRepository.findAllByIdInWithLock(optionIds);

        // <productOptionValueId , ProductOptionValue> 형태의 Map
        Map<UUID, ProductOptionValue> optionMap =
                options.stream().collect(Collectors.toMap(ProductOptionValue::getId, pov -> pov));

        // 재고 되돌리기
        for (OrderDetail detail : orderDetailList) {
            // 외래키 id만 호출시 쿼리 발생 x
            ProductOptionValue option = optionMap.get(detail.getProductOptionValue().getId());

            if (option == null) {
                throw new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND);
            }

            // 재고 되돌리기
            option.increaseStock(detail.getQuantity());
        }
    }
}
