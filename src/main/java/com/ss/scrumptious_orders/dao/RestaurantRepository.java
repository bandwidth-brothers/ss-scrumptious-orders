package com.ss.scrumptious_orders.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ss.scrumptious_orders.entity.Restaurant;
import com.ss.scrumptious_orders.entity.RestaurantOwner;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

	List<Restaurant> findByOwner(RestaurantOwner owner);

}