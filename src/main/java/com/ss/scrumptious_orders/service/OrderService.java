package com.ss.scrumptious_orders.service;

import java.util.List;

import com.ss.scrumptious_orders.dto.CreateMenuitemOrderDto;
import com.ss.scrumptious_orders.dto.CreateOrderDto;
import com.ss.scrumptious_orders.dto.UpdateMenuitemOrderDto;
import com.ss.scrumptious_orders.dto.UpdateOrderDto;
import com.ss.scrumptious_orders.entity.MenuitemOrder;
import com.ss.scrumptious_orders.entity.Order;

public interface OrderService {

    List<Order> getAllOrders();

    Order getOrderById(Long orderId);

    Order createNewOrder(CreateOrderDto createOrderDto);

    void updateOrder(Long orderId, UpdateOrderDto updateOrderDto);

    void deleteOrder(Long orderId);

    MenuitemOrder addItemToOrder(Long orderId, CreateMenuitemOrderDto createMenuitemOrderDto);

    void editItemQuantity(Long orderId, Long menuitemId, UpdateMenuitemOrderDto updateMenuitemOrderDto);

    void removeItemFromOrder(Long orderId, Long menuitemId);

}
