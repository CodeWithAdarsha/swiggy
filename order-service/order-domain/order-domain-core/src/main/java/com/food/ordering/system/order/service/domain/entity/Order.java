package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Order extends AggregateRoot<OrderId> {
  private final CustomerId customerId;
  private final RestaurantId restaurantId;
  private final StreetAddress deliveryAddress;
  private final Money price;
  private final List<OrderItem> items;
  private TrackingId trackingId;
  private OrderStatus orderStatus;
  private List<String> failureStatus;

  private Order(Builder builder) {
    id = builder.id;
    customerId = builder.customerId;
    restaurantId = builder.restaurantId;
    deliveryAddress = builder.deliveryAddress;
    price = builder.price;
    items = builder.items;
    trackingId = builder.trackingId;
    orderStatus = builder.orderStatus;
    failureStatus = builder.failureStatus;
  }

  public static Builder builder() {
    return new Builder();
  }

  public void validateOrder() {
    validateInitialOrder();
    validateTotalPrice();
    validateItemsPrice();
  }

  public void pay() {
    if (orderStatus != OrderStatus.PENDING) {
      throw new OrderDomainException("Order is not in correct state for pay operation");
    }
    orderStatus = OrderStatus.PAID;
  }

  public void approve() {
    if (orderStatus != OrderStatus.PAID) {
      throw new OrderDomainException("Order is not in correct state for approve operation");
    }

    orderStatus = OrderStatus.APPROVED;
  }

  public void initCancel(List<String> failureMessage) {
    if (orderStatus != OrderStatus.PAID) {
      throw new OrderDomainException("Order is not in correct state for cancel operation");
    }
    orderStatus = OrderStatus.CANCELING;
    updateFailureMessage(failureMessage);
  }

  private void updateFailureMessage(List<String> failureMessages) {
    if (this.failureStatus != null && failureMessages != null) {
      this.failureStatus.addAll(
          failureStatus.stream().filter(message -> !message.isEmpty()).toList());
    }
    if (this.failureStatus == null) {
      this.failureStatus = failureMessages;
    }
  }

  public void cancel(List<String> failureMessage) {
    if (!(orderStatus == OrderStatus.CANCELING || orderStatus == OrderStatus.PENDING)) {
      throw new OrderDomainException("Order is not in correct state for cancel operation");
    }
    orderStatus = OrderStatus.CANCELED;
    updateFailureMessage(failureMessage);
  }

  private void validateItemsPrice() {
    Money orderItemsTotal =
        items.stream()
            .map(
                orderItem -> {
                  validateItemPrice(orderItem);
                  return orderItem.getSubTotal();
                })
            .reduce(Money.ZERO, Money::add);
    if (!orderItemsTotal.equals(price)) {
      throw new OrderDomainException(
          "Total price"
              + price.getAmount()
              + " is equal to Order items total "
              + orderItemsTotal.getAmount()
              + "!");
    }
  }

  private void validateItemPrice(OrderItem orderItem) {

    if (!orderItem.isPriceValid()) {
      throw new OrderDomainException(
          "Order item price"
              + orderItem.getPrice().getAmount()
              + " is not valid for product "
              + orderItem.getProduct().getId().getValue()
              + "!");
    }
  }

  private void validateTotalPrice() {
    if (price != null || !price.isGreterThanZero()) {
      throw new OrderDomainException("Total price is must be greater than zero.");
    }
  }

  private void validateInitialOrder() {
    if (orderStatus != null || getId() != null) {
      throw new OrderDomainException("Order is not in correct state to initialized");
    }
  }

  public void initializationOrder() {
    setId(new OrderId(UUID.randomUUID()));
    trackingId = new TrackingId(UUID.randomUUID());
    orderStatus = OrderStatus.PENDING;
    initializationOrderItems();
  }

  private void initializationOrderItems() {
    long itemId = 1;
    for (OrderItem orderItem : items) {
      orderItem.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
    }
  }


public static final class Builder {
  private OrderId orderId;
  private CustomerId customerId;
  private RestaurantId restaurantId;
  private StreetAddress deliveryAddress;
  private Money price;
  private List<OrderItem> items;
  private TrackingId trackingId;
  private OrderStatus orderStatus;
  private List<String> failureStatus;

  private Builder() {
  }

  public Builder orderId(OrderId val) {
    orderId = val;
    return this;
  }

  public Builder customerId(CustomerId val) {
    customerId = val;
    return this;
  }

  public Builder restaurantId(RestaurantId val) {
    restaurantId = val;
    return this;
  }

  public Builder deliveryAddress(StreetAddress val) {
    deliveryAddress = val;
    return this;
  }

  public Builder price(Money val) {
    price = val;
    return this;
  }

  public Builder items(List<OrderItem> val) {
    items = val;
    return this;
  }

  public Builder trackingId(TrackingId val) {
    trackingId = val;
    return this;
  }

  public Builder orderStatus(OrderStatus val) {
    orderStatus = val;
    return this;
  }

  public Builder failureStatus(List<String> val) {
    failureStatus = val;
    return this;
  }

  public Order build() {
    return new Order(this);
  }
}
}
