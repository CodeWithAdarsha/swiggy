package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.event.OrderCreateEvent;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.port.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCreateCommandHandler {

  private final OrderCreateHelper orderCreateHelper;
  private final OrderCreatedPaymentRequestMessagePublisher
      orderCreatedPaymentRequestMessagePublisher;
  private final OrderDataMapper orderDataMapper;

  public OrderCreateCommandHandler(
      OrderCreateHelper orderCreateHelper,
      OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher,
      OrderDataMapper orderDataMapper) {
    this.orderCreateHelper = orderCreateHelper;
    this.orderCreatedPaymentRequestMessagePublisher = orderCreatedPaymentRequestMessagePublisher;
    this.orderDataMapper = orderDataMapper;
  }

  public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
    OrderCreateEvent orderCreateEvent = orderCreateHelper.persistenceOrder(createOrderCommand);
    orderCreatedPaymentRequestMessagePublisher.publish(orderCreateEvent);
    log.info(
        "Order created successfully with id: {} ", orderCreateEvent.getOrder().getId().getValue());
    return orderDataMapper.orderToCreateOrderResponse(
        orderCreateEvent.getOrder(), "Order created successfully");
  }
}
