package com.ss.scrumptious_orders.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "delivery")
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estimated_delivery_time")
    private ZonedDateTime estimatedDeliveryTime;

    @Column(name = "delivery_status")
    private String deliveryStatus;

    @Column(name = "actual_delivery_time")
    private ZonedDateTime actualDeliveryTime;

}