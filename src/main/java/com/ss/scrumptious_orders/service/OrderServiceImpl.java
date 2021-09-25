package com.ss.scrumptious_orders.service;

import java.time.ZonedDateTime;
import java.util.List;

import javax.transaction.Transactional;

import com.ss.scrumptious_orders.dao.CustomerRepository;
import com.ss.scrumptious_orders.dao.OrderRepository;
import com.ss.scrumptious_orders.dao.RestaurantRepository;
import com.ss.scrumptious_orders.dto.CreateOrderDto;
import com.ss.scrumptious_orders.entity.Customer;
import com.ss.scrumptious_orders.entity.Order;
import com.ss.scrumptious_orders.entity.Restaurant;
import com.ss.scrumptious_orders.exception.NoSuchCustomerException;

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

    @Override
    public List<Order> getAllOrders() {
        log.trace("getAllOrders");
        return orderRepository.findAll();
    }

    @Override
    @Transactional
    public Order createNewOrder(CreateOrderDto createOrderDto) {
        log.trace("createNewOrder");
        Customer customer = customerRepository.findById(createOrderDto.getCustomerId())
                .orElseThrow(() -> new NoSuchCustomerException(createOrderDto.getCustomerId()));

        // TODO: add specific NoSuchRestaurantException
        Restaurant restaurant = restaurantRepository.findById(createOrderDto.getRestaurantId()).orElseThrow(null);

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addItemToOrder(Long orderId, Long menuitemId, Long quanity) {
        // TODO Auto-generated method stub

    }

}
