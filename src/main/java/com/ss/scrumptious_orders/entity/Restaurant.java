package com.ss.scrumptious_orders.entity;

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
@Table(name = "restaurant")
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Float rating;

    // $, $$, or $$$
    @Column(name = "price_category", length = 3)
    private String priceCategory;

    private String phone;

    // without columnDefinition = "TINYINT" mysql will default to bit(1)
    @Column(name = "is_active", columnDefinition = "TINYINT")
    private Boolean isActive;

    private String picture;

}