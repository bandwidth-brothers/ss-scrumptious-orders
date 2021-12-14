package com.ss.scrumptious_orders.dao;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ss.scrumptious.common_entities.entity.RestaurantOwner;

@Repository
public interface RestaurantOwnerRepository extends JpaRepository<RestaurantOwner, UUID>{

    Optional<RestaurantOwner> findByEmail(String email);
}
