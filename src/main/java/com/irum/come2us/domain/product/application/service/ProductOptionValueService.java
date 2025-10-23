package com.irum.come2us.domain.product.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.product.domain.entity.ProductOptionValue;
import com.irum.come2us.domain.product.domain.repository.ProductOptionValueRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductOptionValueService {
	private final OrderDetailRepository orderDetailRepository;
	private final ProductOptionValueRepository productOptionValueRepository;


	/**
 	* 주문에 포함된 모든 상품의 재고를 다시 늘립니다. (보상 트랜잭션)
	 * 추후에 SAGA패턴으로 변경 가능성 있음
 	*/
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void rollbackStockForOrder(Order order){
		List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrder(order);

		for (OrderDetail detail : orderDetailList) {
			// 비관적락 걸기
			ProductOptionValue option = productOptionValueRepository.findByIdWithPessimisticLock(detail.getProductOptionValue().getId())
				.orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

			// 재고 되돌리기
			option.increaseStockQuantity(detail.getQuantity());
		}
	}
}
