package com.ss.scrumptious_orders.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderDto {

    // none of these are required

    private UUID customerId;
    private Long deliveryId;
    private Long restaurantId;

    private String confirmationCode;
    private Float orderDiscount;
    private String preparationStatus;
    private ZonedDateTime requestedDeliveryTime;
    private ZonedDateTime submittedAt;

}
