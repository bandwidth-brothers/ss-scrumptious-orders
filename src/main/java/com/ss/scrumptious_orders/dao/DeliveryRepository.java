package com.ss.scrumptious_orders.dao;

import com.ss.scrumptious_orders.entity.Delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

}