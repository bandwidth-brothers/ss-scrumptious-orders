package com.ss.scrumptious_orders.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ss.scrumptious.common_entities.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
