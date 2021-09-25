package com.ss.scrumptious_orders.service;

import java.time.ZonedDateTime;
import java.util.List;

import com.ss.scrumptious_orders.dao.CustomerRepository;
import com.ss.scrumptious_orders.dao.DeliveryRepository;
import com.ss.scrumptious_orders.dao.MenuitemOrderRepository;
import com.ss.scrumptious_orders.dao.MenuitemRepository;
import com.ss.scrumptious_orders.dao.OrderRepository;
import com.ss.scrumptious_orders.dao.RestaurantRepository;
import com.ss.scrumptious_orders.dto.CreateMenuitemOrderDto;
import com.ss.scrumptious_orders.dto.CreateOrderDto;
import com.ss.scrumptious_orders.dto.UpdateMenuitemOrderDto;
import com.ss.scrumptious_orders.dto.UpdateOrderDto;
import com.ss.scrumptious_orders.entity.Customer;
import com.ss.scrumptious_orders.entity.Menuitem;
import com.ss.scrumptious_orders.entity.MenuitemOrder;
import com.ss.scrumptious_orders.entity.MenuitemOrderKey;
import com.ss.scrumptious_orders.entity.Order;
import com.ss.scrumptious_orders.entity.Restaurant;
import com.ss.scrumptious_orders.exception.NoSuchCustomerException;
import com.ss.scrumptious_orders.exception.NoSuchDeliveryException;
import com.ss.scrumptious_orders.exception.NoSuchMenuitemException;
import com.ss.scrumptious_orders.exception.NoSuchMenuitemOrderException;
import com.ss.scrumptious_orders.exception.NoSuchOrderException;
import com.ss.scrumptious_orders.exception.NoSuchRestaurantException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final DeliveryRepository deliveryRepository;
    private final MenuitemRepository menuitemRepository;
    private final MenuitemOrderRepository menuitemOrderRepository;

    @Override
    public List<Order> getAllOrders() {
        log.trace("getAllOrders");
        return orderRepository.findAll();
    }

    @Override
    public Order createNewOrder(CreateOrderDto createOrderDto) {
        log.trace("createNewOrder");
        Customer customer = customerRepository.findById(createOrderDto.getCustomerId())
                .orElseThrow(() -> new NoSuchCustomerException(createOrderDto.getCustomerId()));

        Restaurant restaurant = restaurantRepository.findById(createOrderDto.getRestaurantId())
                .orElseThrow(() -> new NoSuchRestaurantException(createOrderDto.getRestaurantId()));

        ZonedDateTime deliveryTime;
        if (createOrderDto.getRequestedDeliveryTime() == null) {
            deliveryTime = ZonedDateTime.now().plusHours(1);
        } else {
            deliveryTime = createOrderDto.getRequestedDeliveryTime();
        }

        Order order = Order.builder().customer(customer).restaurant(restaurant).requestedDeliveryTime(deliveryTime)
                .preparationStatus("Preparing").build();

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        log.trace("getOrderById orderId = " + orderId);
        return orderRepository.findById(orderId).orElseThrow(() -> new NoSuchOrderException(orderId));
    }

    @Override
    public void updateOrder(Long orderId, UpdateOrderDto updateOrderDto) {
        log.trace("updateOrder orderId = " + orderId);
        Order order = orderRepository.getById(orderId);

        if (updateOrderDto.getCustomerId() != null) {
            order.setCustomer(customerRepository.findById(updateOrderDto.getCustomerId())
                    .orElseThrow(() -> new NoSuchCustomerException(updateOrderDto.getCustomerId())));
        }
        if (updateOrderDto.getDeliveryId() != null) {
            order.setDelivery(deliveryRepository.findById(updateOrderDto.getDeliveryId())
                    .orElseThrow(() -> new NoSuchDeliveryException(updateOrderDto.getDeliveryId())));
        }
        if (updateOrderDto.getRestaurantId() != null) {
            order.setRestaurant(restaurantRepository.findById(updateOrderDto.getRestaurantId())
                    .orElseThrow(() -> new NoSuchRestaurantException(updateOrderDto.getRestaurantId())));
        }
        if (updateOrderDto.getConfirmationCode() != null) {
            order.setConfirmationCode(updateOrderDto.getConfirmationCode());
        }
        if (updateOrderDto.getOrderDiscount() != null) {
            order.setOrderDiscount(updateOrderDto.getOrderDiscount());
        }
        if (updateOrderDto.getPreparationStatus() != null) {
            order.setPreparationStatus(updateOrderDto.getPreparationStatus());
        }
        if (updateOrderDto.getRequestedDeliveryTime() != null) {
            order.setRequestedDeliveryTime(updateOrderDto.getRequestedDeliveryTime());
        }
        if (updateOrderDto.getSubmittedAt() != null) {
            order.setSubmitedAt(updateOrderDto.getSubmittedAt());
        }
        orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        log.trace("deleteOrder orderId = " + orderId);

        orderRepository.findById(orderId).ifPresent(orderRepository::delete);

    }

    @Override
    public MenuitemOrder addItemToOrder(Long orderId, CreateMenuitemOrderDto createMenuitemOrderDto) {
        log.trace("addItemToOrder orderId = " + orderId + "menuitemId = " + createMenuitemOrderDto.getMenuitemId());

        Order order = getOrderById(orderId);
        Menuitem menuitem = menuitemRepository.findById(createMenuitemOrderDto.getMenuitemId())
                .orElseThrow(() -> new NoSuchMenuitemException(createMenuitemOrderDto.getMenuitemId()));
        Long quantity;
        if (createMenuitemOrderDto.getQuantity() != null) {
            quantity = createMenuitemOrderDto.getQuantity();
        } else {
            quantity = Long.valueOf(1);
        }

        MenuitemOrder menuitemOrder = MenuitemOrder.builder().id(new MenuitemOrderKey(menuitem.getId(), order.getId()))
                .order(order).menuitem(menuitem).quantity(quantity).build();

        return menuitemOrderRepository.save(menuitemOrder);
    }

    @Override
    public void editItemQuantity(Long orderId, Long menuitemId, UpdateMenuitemOrderDto updateMenuitemOrderDto) {
        log.trace("editItemQuantity orderId = " + orderId + "menuitemId = " + menuitemId);

        MenuitemOrder menuitemOrder = menuitemOrderRepository.findById(new MenuitemOrderKey(menuitemId, orderId))
                .orElseThrow(() -> new NoSuchMenuitemOrderException(new MenuitemOrderKey(menuitemId, orderId)));
        menuitemOrder.setQuantity(updateMenuitemOrderDto.getQuantity());
        menuitemOrderRepository.save(menuitemOrder);
    }

    @Override
    public void removeItemFromOrder(Long orderId, Long menuitemId) {
        log.trace("removeItemFromOrder orderId = " + orderId + "menuitemId = " + menuitemId);

        menuitemOrderRepository.findById(new MenuitemOrderKey(menuitemId, orderId))
                .ifPresent(menuitemOrderRepository::delete);
    }

}
