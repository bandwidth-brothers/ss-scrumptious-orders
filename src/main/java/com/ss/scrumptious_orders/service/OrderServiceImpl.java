package com.ss.scrumptious_orders.service;

import java.time.ZonedDateTime;
import java.util.List;

import javax.transaction.Transactional;

import com.ss.scrumptious_orders.dao.CustomerRepository;
import com.ss.scrumptious_orders.dao.DeliveryRepository;
import com.ss.scrumptious_orders.dao.OrderRepository;
import com.ss.scrumptious_orders.dao.RestaurantRepository;
import com.ss.scrumptious_orders.dto.CreateOrderDto;
import com.ss.scrumptious_orders.dto.UpdateOrderDto;
import com.ss.scrumptious_orders.entity.Customer;
import com.ss.scrumptious_orders.entity.Order;
import com.ss.scrumptious_orders.entity.Restaurant;
import com.ss.scrumptious_orders.exception.NoSuchCustomerException;
import com.ss.scrumptious_orders.exception.NoSuchDeliveryException;
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

    @Override
    public List<Order> getAllOrders() {
        log.trace("getAllOrders");
        return orderRepository.findAll();
    }

    @Transactional
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
    public Order getOrderById(Long id) {
        log.trace("getOrderById = " + id);
        return orderRepository.findById(id).orElseThrow(() -> new NoSuchOrderException(id));
    }

    @Transactional
    @Override
    public void updateOrder(Long id, UpdateOrderDto updateOrderDto) {
        log.trace("updateOrder id = " + id);
        Order order = orderRepository.getById(id);

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
    public void addItemToOrder(Long orderId, Long menuitemId, Long quanity) {
        // TODO Auto-generated method stub

    }

}
