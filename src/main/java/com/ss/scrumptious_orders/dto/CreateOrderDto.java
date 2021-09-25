package com.ss.scrumptious_orders.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderDto {

    @NotNull
    private UUID customerId;

    @NotNull
    private Long restaurantId;

    private ZonedDateTime requestedDeliveryTime;
}
