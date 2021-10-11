package com.ss.scrumptious_orders.dao;

import com.ss.scrumptious_orders.entity.MenuitemOrder;
import com.ss.scrumptious_orders.entity.MenuitemOrderKey;
import com.ss.scrumptious_orders.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuitemOrderRepository extends JpaRepository<MenuitemOrder, MenuitemOrderKey> {

    void deleteByOrder(Order order);
}