package com.ss.scrumptious_orders.dao;

import com.ss.scrumptious.common_entities.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ss.scrumptious.common_entities.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrder(Order order);
}
