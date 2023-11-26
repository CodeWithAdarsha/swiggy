package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCreateEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.port.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.port.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.port.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderCreateHelper {

  private final OrderDomainService orderDomainService;
  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;

  private final RestaurantRepository restaurantRepository;

  private final OrderDataMapper orderDataMapper;

  public OrderCreateHelper(
      OrderDomainService orderDomainService,
      OrderRepository orderRepository,
      CustomerRepository customerRepository,
      RestaurantRepository restaurantRepository,
      OrderDataMapper orderDataMapper) {
    this.orderDomainService = orderDomainService;
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.restaurantRepository = restaurantRepository;
    this.orderDataMapper = orderDataMapper;
  }

  public OrderCreateEvent persistenceOrder(CreateOrderCommand createOrderCommand) {
    checkCustomer(createOrderCommand.getCustomerId());
    Restaurant restaurant = checkRestaurant(createOrderCommand);
    var order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
    var orderCreateEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
    saveOrder(orderCreateEvent.getOrder());
    log.info("Order created with id: {} ", orderCreateEvent.getOrder().getId().getValue());

    return orderCreateEvent;
  }

  private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
    Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
    Optional<Restaurant> optionalRestaurant = restaurantRepository.findByRestaurantId(restaurant);
    if (optionalRestaurant.isEmpty()) {
      log.warn("Could not find restaurant with id " + createOrderCommand.getRestaurantId());
      throw new OrderDomainException(
          "Could not find restaurant with id " + createOrderCommand.getRestaurantId());
    }

    return optionalRestaurant.get();
  }

  private void checkCustomer(UUID customerId) {
    Optional<Customer> customer = customerRepository.findCustomer(customerId);
    if (customer.isEmpty()) {
      log.warn("Could not find customer with id " + customerId);
      throw new OrderDomainException("Could not find customer with id " + customerId);
    }
  }

  private Order saveOrder(Order order) {
    var orderResult = orderRepository.save(order);
    if (orderResult == null) {
      log.warn("Could not save order");
      throw new OrderDomainException("Could not save order");
    }
    log.info("Order saved successfully with id {}", orderResult.getId());
    return orderResult;
  }
}
