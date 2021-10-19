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

    // only the customer is required

    @NotNull
    private UUID customerId;

    private Long restaurantId;

    private String confirmationCode;
    private Float orderDiscount;
    private String preparationStatus;
    private ZonedDateTime requestedDeliveryTime;
    private ZonedDateTime submittedAt;

    private CreateMenuitemOrderDto[] menuitems;
}
