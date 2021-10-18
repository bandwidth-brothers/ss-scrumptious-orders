package com.ss.scrumptious_orders.service;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import com.ss.scrumptious_orders.dao.CustomerRepository;
import com.ss.scrumptious_orders.dao.MenuitemOrderRepository;
import com.ss.scrumptious_orders.dao.MenuitemRepository;
import com.ss.scrumptious_orders.dao.OrderRepository;
import com.ss.scrumptious_orders.dao.RestaurantRepository;
import com.ss.scrumptious_orders.dto.CreateMenuitemOrderDto;
import com.ss.scrumptious_orders.dto.CreateOrderDto;
import com.ss.scrumptious_orders.dto.UpdateOrderDto;
import com.ss.scrumptious_orders.entity.Customer;
import com.ss.scrumptious_orders.entity.Menuitem;
import com.ss.scrumptious_orders.entity.MenuitemOrder;
import com.ss.scrumptious_orders.entity.MenuitemOrderKey;
import com.ss.scrumptious_orders.entity.Order;
import com.ss.scrumptious_orders.exception.NoSuchCustomerException;
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
    private final MenuitemRepository menuitemRepository;
    private final MenuitemOrderRepository menuitemOrderRepository;

    @Override
    public List<Order> getAllOrders() {
        log.trace("getAllOrders");
        return orderRepository.findAll();
    }

    @Transactional
    @Override
    public Order createNewOrder(CreateOrderDto createOrderDto) {
        log.trace("createNewOrder");

        // setting values that must exist
        Customer customer = customerRepository.findById(createOrderDto.getCustomerId())
                .orElseThrow(() -> new NoSuchCustomerException(createOrderDto.getCustomerId()));

        Order order = Order.builder()
                .customer(customer)
                .build();

        // setting values if they exist in the json
        if (createOrderDto.getRestaurantId() != null) {
            order.setRestaurant(restaurantRepository.findById(createOrderDto.getRestaurantId())
                .orElseThrow(() -> new NoSuchRestaurantException(createOrderDto.getRestaurantId())));
        }
        if (createOrderDto.getConfirmationCode() != null) {
            order.setConfirmationCode(createOrderDto.getConfirmationCode());
        }
        if (createOrderDto.getOrderDiscount() != null) {
            order.setOrderDiscount(createOrderDto.getOrderDiscount());
        }
        if (createOrderDto.getPreparationStatus() != null) {
            order.setPreparationStatus(createOrderDto.getPreparationStatus());
        }
        if (createOrderDto.getRequestedDeliveryTime() != null) {
            order.setRequestedDeliveryTime(createOrderDto.getRequestedDeliveryTime());
        }
        if (createOrderDto.getSubmittedAt() != null) {
            order.setSubmitedAt(createOrderDto.getSubmittedAt());
        }

        // need to save here before adding the menuitems because menuitems need a valid orderId
        order = orderRepository.saveAndFlush(order);

        if(createOrderDto.getMenuitems() != null) {
            for (CreateMenuitemOrderDto createMenuitemOrderDto : createOrderDto.getMenuitems()) {
                addItemToOrder(order.getId(), createMenuitemOrderDto);
            }
        }

        return order;
    }

    @Override
    public Order getOrderById(Long orderId) {
        log.trace("getOrderById orderId = " + orderId);
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new NoSuchOrderException(orderId));
    }

    @Override
    public List<Order> getOrdersByCustomerId(UUID customerId) {
        log.trace("getOrderByCustomerId customerId = " + customerId);
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new NoSuchCustomerException(customerId));
        return orderRepository.findByCustomer(customer);
    }

    @Transactional
    @Override
    public Order updateOrder(Long orderId, UpdateOrderDto updateOrderDto) {
        log.trace("updateOrder orderId = " + orderId);
        Order order = getOrderById(orderId);

        if (updateOrderDto.getCustomerId() != null) {
            order.setCustomer(customerRepository.findById(updateOrderDto.getCustomerId())
                    .orElseThrow(() -> new NoSuchCustomerException(updateOrderDto.getCustomerId())));
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

        if(updateOrderDto.getMenuitems() != null) {
            for (CreateMenuitemOrderDto createMenuitemOrderDto : updateOrderDto.getMenuitems()) {
                editItemQuantity(orderId, createMenuitemOrderDto.getMenuitemId(), 
                        createMenuitemOrderDto.getQuantity());
            }
        }

        return orderRepository.saveAndFlush(order);
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

        MenuitemOrder menuitemOrder = MenuitemOrder.builder().id(new MenuitemOrderKey(menuitem.getId(), order.getId()))
                .order(order).menuitem(menuitem).quantity(Long.valueOf(1)).build();

        if (createMenuitemOrderDto.getQuantity() != null) {
            menuitemOrder.setQuantity(createMenuitemOrderDto.getQuantity());
        }

        return menuitemOrderRepository.saveAndFlush(menuitemOrder);
    }

    @Override
    public MenuitemOrder editItemQuantity(Long orderId, Long menuitemId, Long quantity) {
        log.trace("editItemQuantity orderId = " + orderId + "menuitemId = " + menuitemId);

        MenuitemOrder menuitemOrder = menuitemOrderRepository.findById(new MenuitemOrderKey(menuitemId, orderId))
                .orElseThrow(() -> new NoSuchMenuitemOrderException(new MenuitemOrderKey(menuitemId, orderId)));
        menuitemOrder.setQuantity(quantity);

        return menuitemOrderRepository.saveAndFlush(menuitemOrder);
    }

    @Override
    public void removeItemFromOrder(Long orderId, Long menuitemId) {
        log.trace("removeItemFromOrder orderId = " + orderId + "menuitemId = " + menuitemId);

        menuitemOrderRepository.findById(new MenuitemOrderKey(menuitemId, orderId))
                .ifPresent(menuitemOrderRepository::delete);
    }

    @Transactional
    @Override
    public void removeAllItemsFromOrder(Long orderId) {
        log.trace("removeItemFromOrder orderId = " + orderId);

        // no need to get the entire order since jpa only looks for id here
        Order order = new Order();
        order.setId(orderId);
        menuitemOrderRepository.deleteByOrder(order);
    }

}
