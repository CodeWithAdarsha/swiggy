package com.food.ordering.system.order.service.domain.port.output.message.publisher.restaurantaproval;

import com.food.ordering.system.domain.event.DomainEventPublisher;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {}
