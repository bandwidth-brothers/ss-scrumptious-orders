package com.ss.scrumptious_orders.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "menuitem_order")
@Builder
public class MenuitemOrder {

    @JsonIgnore
    @EmbeddedId
    MenuitemOrderKey id;

    @ManyToOne
    @MapsId("menuitemId")
    @JoinColumn(name = "menuitem_id")
    Menuitem menuitem;

    @JsonIgnore
    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    Order order;

    Long quantity;
}