package com.ss.scrumptious_orders.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import com.ss.scrumptious_orders.dto.CreateMenuitemOrderDto;
import com.ss.scrumptious_orders.dto.CreateOrderDto;
import com.ss.scrumptious_orders.dto.UpdateOrderDto;
import com.ss.scrumptious_orders.entity.MenuitemOrder;
import com.ss.scrumptious_orders.entity.Order;
import com.ss.scrumptious_orders.service.OrderService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("GET Order all");
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('ADMIN')"
            + " OR @ownerAuthenticationManager.ownerIdMatches(authentication, #ownerId)")
    @GetMapping(value = "owners/{ownerId}/restaurants", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<List<Order>> getAllOrdersByOwner(@PathVariable UUID ownerId){
    	List<Order> orders = orderService.getAllOrdersByOwner(ownerId);
    	if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }
    
    @PreAuthorize("hasRole('ADMIN')"
            + " OR @ownerAuthenticationManager.ownerIdMatches(authentication, #restaurantId, #ownerId)")
    @GetMapping(value = "owners/{ownerId}/restaurants/{restaurantId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<List<Order>> getAllOrdersByRestaurant(@PathVariable Long restaurantId, @PathVariable UUID ownerId){
    	List<Order> orders = orderService.getAllOrdersByRestaurant(restaurantId);
    	if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }
    
    @PreAuthorize("hasRole('ADMIN')"
        + " OR @customerAuthenticationManager.customerIdMatches(authentication, #orderId)"
        + " OR @ownerAuthenticationManager.ownerIdMatches(authentication, #orderId)")
    @GetMapping(value = "/{orderId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        log.info("Get Order id = " + orderId);
        return ResponseEntity.of(Optional.ofNullable(orderService.getOrderById(orderId)));
    }

    @PreAuthorize("hasRole('ADMIN')"
        + " OR @customerAuthenticationManager.customerIdMatches(authentication, #customerId)")
    @GetMapping(value = "customers/{customerId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable UUID customerId) {
        log.info("GET Order all");
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('ADMIN')"
    + " OR @customerAuthenticationManager.customerIdMatches(authentication, #createOrderDto)")
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<Void> createNewOrder(@Valid @RequestBody CreateOrderDto createOrderDto) {
        log.info("POST Order");
        Order order = orderService.createNewOrder(createOrderDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{orderId}")
        .buildAndExpand(order.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PreAuthorize("hasRole('ADMIN')"
    + " OR @customerAuthenticationManager.customerIdMatches(authentication, #orderId)"
    + " OR @ownerAuthenticationManager.ownerIdMatches(authentication, #orderId)")
    @PutMapping(value = "/{orderId}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<Void> updateExistingOrder(@Valid @RequestBody UpdateOrderDto updateOrderDto,
            @PathVariable Long orderId) {
        log.info("PUT Order id = " + orderId);
        orderService.updateOrder(orderId, updateOrderDto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')"
    + " OR @customerAuthenticationManager.customerIdMatches(authentication, #orderId)")
    @DeleteMapping(value = "/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        log.info("DELETE Order id = " + orderId);
        orderService.deleteOrder(orderId);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')"
    + " OR @customerAuthenticationManager.customerIdMatches(authentication, #orderId)")
    @PostMapping(value = "/{orderId}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<Void> createNewMenuitemOrder(
            @Valid @RequestBody CreateMenuitemOrderDto createMenuitemOrderDto, @PathVariable Long orderId) {
        log.info("POST menuitemOrder");
        MenuitemOrder menuitemOrder = orderService.addItemToOrder(orderId, createMenuitemOrderDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{menuitemOrderId}")
        .buildAndExpand(menuitemOrder.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PreAuthorize("hasRole('ADMIN')"
    + " OR @customerAuthenticationManager.customerIdMatches(authentication, #orderId)")
    @PutMapping(value = "/{orderId}/menuitems/{menuitemId}", consumes = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<MenuitemOrder> updateExistingMenuitemOrder(
            @Valid @RequestBody Long quantity, @PathVariable Long orderId,
            @PathVariable Long menuitemId) {
        log.info("POST menuitemOrder orderId = " + orderId + ", menuitemId = " + menuitemId);

        orderService.editItemQuantity(orderId, menuitemId, quantity);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')"
    + " OR @customerAuthenticationManager.customerIdMatches(authentication, #orderId)")
    @DeleteMapping(value = "/{orderId}/menuitems/{menuitemId}")
    public ResponseEntity<Void> deleteMenuitemOrder(@PathVariable Long orderId, @PathVariable Long menuitemId) {
        log.info("DELETE menuitemOrder orderId = " + orderId + ", menuitemId = " + menuitemId);
        orderService.removeItemFromOrder(orderId, menuitemId);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')"
    + " OR @customerAuthenticationManager.customerIdMatches(authentication, #orderId)")
    @DeleteMapping(value = "/{orderId}/menuitems")
    public ResponseEntity<Void> deleteAllMenuitemOrder(@PathVariable Long orderId) {
        log.info("DELETE all menuitemOrder orderId = " + orderId);
        orderService.removeAllItemsFromOrder(orderId);

        return ResponseEntity.noContent().build();
    }
}