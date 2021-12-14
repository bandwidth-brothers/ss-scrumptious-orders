package com.ss.scrumptious_orders.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ss.scrumptious.common_entities.entity.MenuitemOrder;
import com.ss.scrumptious.common_entities.entity.MenuitemOrderKey;
import com.ss.scrumptious.common_entities.entity.Order;

@Repository
public interface MenuitemOrderRepository extends JpaRepository<MenuitemOrder, MenuitemOrderKey> {

    void deleteByOrder(Order order);
}