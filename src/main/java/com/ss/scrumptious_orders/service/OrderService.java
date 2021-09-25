package com.ss.scrumptious_orders.service;

import java.util.List;

import com.ss.scrumptious_orders.dto.CreateOrderDto;
import com.ss.scrumptious_orders.dto.UpdateOrderDto;
import com.ss.scrumptious_orders.entity.Order;

public interface OrderService {

    List<Order> getAllOrders();

    Order getOrderById(Long id);

    Order createNewOrder(CreateOrderDto createOrderDto);

    void updateOrder(Long id, UpdateOrderDto updateOrderDto);

    void addItemToOrder(Long orderId, Long menuitemId, Long quanity);

}
