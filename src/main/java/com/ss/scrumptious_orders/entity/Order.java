package com.ss.scrumptious_orders.entity;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "\"order\"") // order is a keyword in mysql so we gotta do this lol
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id", referencedColumnName = "id")
    private Delivery delivery;

    @OneToMany(mappedBy = "order")
    List<MenuitemOrder> menuitemOrders;

    @Column(name = "confirmation_code")
    private String confirmationCode;

    @Column(name = "requested_delivery_time")
    private ZonedDateTime requestedDeliveryTime;

    @Column(name = "order_discount")
    private Float orderDiscount;

    @Column(name = "submited_at")
    private ZonedDateTime submitedAt;

    @Column(name = "preparation_status")
    private String preparationStatus;

}