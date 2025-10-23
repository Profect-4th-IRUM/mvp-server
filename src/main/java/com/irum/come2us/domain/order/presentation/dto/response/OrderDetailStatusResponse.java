package com.irum.come2us.domain.order.presentation.dto.response;

import com.irum.come2us.domain.deliveryaddress.domain.entity.Address;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;

public record OrderDetailStatusResponse(
	String productName,
	int quantity,
	String optionName,
	AddressResponse address,
	String recipientName,
	OrderStatus orderStatus,
	Integer trackingNumber,
	String deliveryRequest
) {
	public record AddressResponse(
		String postalCode,
		String city,
		String sigungu,
		String roadname,
		String addressDetail
	){
		public static AddressResponse from(Address address){
			return new AddressResponse(
				address.getPostalCode(),
				address.getCity(),
				address.getSigungu(),
				address.getRoadName(),
				address.getAddressDetail()
			);
		}
	}
	public static OrderDetailStatusResponse from(OrderDetail orderDetail){
		return new OrderDetailStatusResponse(
			orderDetail.getProductName(),
			orderDetail.getQuantity(),
			orderDetail.getOptionName(),
			AddressResponse.from(orderDetail.getOrder().getDeliveryAddress().getAddress()),
			orderDetail.getOrder().getDeliveryAddress().getRecipientName(),
			orderDetail.getOrderStatusIndi(),
			orderDetail.getTrackingNumber(),
			orderDetail.getOrder().getDeliveryRequest()
		);
	}
}
