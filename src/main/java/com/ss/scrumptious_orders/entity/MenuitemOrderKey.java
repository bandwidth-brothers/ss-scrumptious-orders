package com.ss.scrumptious_orders.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class MenuitemOrderKey implements Serializable {

    @Column(name = "menuitem_id")
    Long menuitemId;

    @Column(name = "order_id")
    Long orderId;
}