package com.ss.scrumptious_orders.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateMenuitemOrderDto {

    @NotNull
    private Long menuitemId;

    private Long quantity;
}
