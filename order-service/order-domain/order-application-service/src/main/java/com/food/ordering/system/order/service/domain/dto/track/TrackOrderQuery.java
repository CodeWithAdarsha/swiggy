package com.food.ordering.system.order.service.domain.dto.track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.NonFinal;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class TrackOrderQuery {
  @NonFinal private final UUID orderTrackingId;
}
