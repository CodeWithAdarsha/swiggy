package com.food.ordering.system.order.service.domain.port.input.message.listner.restaurantaproval;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;

public interface RestaurantApprovalResponseMessageListener {
	  void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse);
	  void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse);
}
