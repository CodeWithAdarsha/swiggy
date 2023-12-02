package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.port.input.message.listner.payment.PaymentResponseMessageLister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class PaymentResponseMessageListerImpl implements PaymentResponseMessageLister {
  @Override
  public void paymentCompleted(PaymentResponse paymentResponse) {}

  @Override
  public void paymentCancelled(PaymentResponse paymentResponse) {}
}
