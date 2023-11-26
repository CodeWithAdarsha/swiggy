package com.food.ordering.system.order.service.domain.port.input.message.listner.payment;

import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;

public interface PaymentResponseMessageLister {
	  void paymentCompleted(PaymentResponse paymentResponse);
	  void paymentCancelled(PaymentResponse paymentResponse);
}
