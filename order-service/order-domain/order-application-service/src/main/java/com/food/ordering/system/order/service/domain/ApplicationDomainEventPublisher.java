package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.DomainEventPublisher;
import com.food.ordering.system.order.service.domain.event.OrderCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationDomainEventPublisher
    implements ApplicationEventPublisherAware, DomainEventPublisher<OrderCreateEvent> {

  private ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void publish(OrderCreateEvent domainEvent) {

    applicationEventPublisher.publishEvent(domainEvent);
    log.info(
        "OrderCreateEvent is published with orderId: " + domainEvent.getOrder().getId().getValue());
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }
}
