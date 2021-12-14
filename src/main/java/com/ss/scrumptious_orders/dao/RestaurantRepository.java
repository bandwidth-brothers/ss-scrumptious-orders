package com.ss.scrumptious_orders.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ss.scrumptious.common_entities.entity.Restaurant;
import com.ss.scrumptious.common_entities.entity.RestaurantOwner;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

	List<Restaurant> findByOwner(RestaurantOwner owner);

}