package com.ss.scrumptious_orders.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ss.scrumptious_orders.entity.Customer;
import com.ss.scrumptious_orders.entity.Order;
import com.ss.scrumptious_orders.entity.Restaurant;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds orders by using the customer as a search criteria.
     * @param customer
     * @return  A list of orders whose customer is an exact match with the given customer.
     *          If no orders are found, this method returns null.
     */
    List<Order> findByCustomer(Customer customer);

    List<Order> findByRestaurant(Restaurant restaurant);
}