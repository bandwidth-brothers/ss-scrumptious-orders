package com.ss.scrumptious_orders.dao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ss.scrumptious.common_entities.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

}