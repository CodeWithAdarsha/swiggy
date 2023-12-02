package com.food.ordering.system.order.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.port.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.port.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.port.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.port.output.repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

  private final UUID CUSTOMER_ID = UUID.randomUUID();
  private final UUID RESTAURT_ID = UUID.randomUUID();
  private final UUID PRODUCT_ID = UUID.randomUUID();
  private final UUID ORDER_ID = UUID.randomUUID();
  private final BigDecimal price = new BigDecimal("200.00");
  @Autowired OrderApplicationService orderApplicationService;
  @Autowired OrderDataMapper orderDataMapper;
  @Autowired OrderRepository orderRepository;
  @Autowired RestaurantRepository restaurantRepository;
  @Autowired CustomerRepository customerRepository;
  private CreateOrderCommand createOrderCommand;
  private CreateOrderCommand createOrderCommandWrongPrice;

  @BeforeAll
  public void initialize() {
    createOrderCommand =
        CreateOrderCommand.builder()
            .customerId(CUSTOMER_ID)
            .restaurantId(RESTAURT_ID)
            .address(OrderAddress.builder().street("123").postalCode("10002").city("IL").build())
            .price(price)
            .items(
                List.of(
                    OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(1)
                        .price(new BigDecimal("100.00"))
                        .subTotal(new BigDecimal("100.00"))
                        .build(),
                    OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(1)
                        .price(new BigDecimal("100.00"))
                        .subTotal(new BigDecimal("100.00"))
                        .build()))
            .build();

    createOrderCommandWrongPrice =
        CreateOrderCommand.builder()
            .customerId(CUSTOMER_ID)
            .restaurantId(RESTAURT_ID)
            .address(OrderAddress.builder().street("123").postalCode("10002").city("IL").build())
            .price(price)
            .items(
                List.of(
                    OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(1)
                        .price(new BigDecimal("100.00"))
                        .subTotal(new BigDecimal("100.00"))
                        .build()))
            .build();
    Customer customer = new Customer();
    customer.setId(new CustomerId(CUSTOMER_ID));

    Restaurant restaurantResponse =
        Restaurant.builder()
            .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
            .products(
                List.of(
                    new Product(
                        new ProductId(PRODUCT_ID), "Product_1", new Money(new BigDecimal("50.00"))),
                    new Product(
                        new ProductId(PRODUCT_ID),
                        "Product_1",
                        new Money(new BigDecimal("100.00")))))
            .active(true)
            .build();

    Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
    order.setId(new OrderId(ORDER_ID));

    when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
    when(restaurantRepository.findByRestaurantInformation(
            orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
        .thenReturn(Optional.of(restaurantResponse));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
  }

  @Test
  public void testCreateOrder() {
    CreateOrderResponse createOrderResponse =
        orderApplicationService.createOrder(createOrderCommand);
    assertEquals(createOrderResponse.getOrderStatus(), OrderStatus.PENDING);
  }

  @Test
  void testOrderWithWrongPrice() {
    OrderDomainException orderDomainException =
        assertThrows(
            OrderDomainException.class,
            () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
    assertEquals(
        orderDomainException.getMessage(),
        "Total price : 200.00 is equal to Order items total 100.00!");
  }

  @Test
  void testCreateOrderWithPassiveRestaurant() {
    Restaurant restaurant =
        Restaurant.builder()
            .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
            .products(
                List.of(
                    new Product(
                        new ProductId(PRODUCT_ID),
                        "Product_1",
                        new Money(new BigDecimal("50.00")))))
            .active(false)
            .build();
    when(restaurantRepository.findByRestaurantInformation(
            orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
        .thenReturn(Optional.of(restaurant));

    OrderDomainException orderDomainException =
        assertThrows(
            OrderDomainException.class,
            () -> orderApplicationService.createOrder(createOrderCommand));
    assertEquals(
        orderDomainException.getMessage(), "Restaurant with Id" + RESTAURT_ID + " is not active");
  }
}
