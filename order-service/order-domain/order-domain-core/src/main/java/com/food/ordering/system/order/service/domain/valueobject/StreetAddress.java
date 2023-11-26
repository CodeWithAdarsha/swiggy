package com.food.ordering.system.order.service.domain.valueobject;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"street", "postalCode", "city"})
public class StreetAddress {
    private final UUID id;
    private final String street;
    private final String postalCode;
    private final String city;
}
