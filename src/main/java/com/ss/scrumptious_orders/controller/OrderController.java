package com.ss.scrumptious_orders.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.ss.scrumptious_orders.dto.CreateMenuitemOrderDto;
import com.ss.scrumptious_orders.dto.CreateOrderDto;
import com.ss.scrumptious_orders.dto.UpdateMenuitemOrderDto;
import com.ss.scrumptious_orders.dto.UpdateOrderDto;
import com.ss.scrumptious_orders.entity.MenuitemOrder;
import com.ss.scrumptious_orders.entity.Order;
import com.ss.scrumptious_orders.service.OrderService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("GET Order all");
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }

    @GetMapping(value = "/{orderId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        log.info("Get Order id = " + orderId);
        return ResponseEntity.of(Optional.ofNullable(orderService.getOrderById(orderId)));
    }

    // @EmployeeOnlyPermission
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<Order> createNewOrder(@Valid @RequestBody CreateOrderDto createOrderDto) {
        log.info("POST Order");
        Order order = orderService.createNewOrder(createOrderDto);
        return ResponseEntity.ok(order);
    }

    @PutMapping(value = "/{orderId}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<Void> updateExistingOrder(@Valid @RequestBody UpdateOrderDto updateOrderDto,
            @PathVariable Long orderId) {
        log.info("PUT Order id = " + orderId);
        orderService.updateOrder(orderId, updateOrderDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{orderId}")
    public ResponseEntity<Void> deletOrder(@PathVariable Long orderId) {
        log.info("DELETE Order id = " + orderId);
        orderService.deleteOrder(orderId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{orderId}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<MenuitemOrder> createNewMenuitemOrder(
            @Valid @RequestBody CreateMenuitemOrderDto createMenuitemOrderDto, @PathVariable Long orderId) {
        log.info("POST menuitemOrder");
        MenuitemOrder menuitemOrder = orderService.addItemToOrder(orderId, createMenuitemOrderDto);
        return ResponseEntity.ok(menuitemOrder);
    }

    @PutMapping(value = "/{orderId}/menuitems/{menuitemId}", consumes = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<MenuitemOrder> updateExistingMenuitemOrder(
            @Valid @RequestBody UpdateMenuitemOrderDto updateMenuitemOrderDto, @PathVariable Long orderId,
            @PathVariable Long menuitemId) {
        log.info("POST menuitemOrder orderId = " + orderId + ", menuitemId = " + menuitemId);

        orderService.editItemQuantity(orderId, menuitemId, updateMenuitemOrderDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{orderId}/menuitems/{menuitemId}")
    public ResponseEntity<Void> deletOrder(@PathVariable Long orderId, @PathVariable Long menuitemId) {
        log.info("DELETE menuitemOrder orderId = " + orderId + ", menuitemId = " + menuitemId);
        orderService.removeItemFromOrder(orderId, menuitemId);

        return ResponseEntity.noContent().build();
    }
}