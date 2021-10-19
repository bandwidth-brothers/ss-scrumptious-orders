package com.ss.scrumptious_orders.dao;

import com.ss.scrumptious_orders.entity.Restaurant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

}