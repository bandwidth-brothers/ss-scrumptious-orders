package com.ss.scrumptious_orders.security;

import com.ss.scrumptious_orders.dao.OrderRepository;
import com.ss.scrumptious_orders.dto.CreateOrderDto;
import com.ss.scrumptious_orders.dto.UpdateOrderDto;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerAuthenticationManager {
    
    private final OrderRepository orderRepository;

    public boolean customerIdMatches(Authentication authentication, CreateOrderDto createOrderDto) {
        try {
            JwtPrincipalModel principal = (JwtPrincipalModel) authentication.getPrincipal();
            return principal.getUserId().equals(createOrderDto.getCustomerId());
          } catch (ClassCastException ex) {
            return false;
          }
    }

    public boolean customerIdMatches(Authentication authentication, UpdateOrderDto updateOrderDto) {
        try {
            JwtPrincipalModel principal = (JwtPrincipalModel) authentication.getPrincipal();
            return principal.getUserId().equals(updateOrderDto.getCustomerId());
          } catch (ClassCastException ex) {
            return false;
          }
    }

    public boolean customerIdMatches(Authentication authentication, Long orderId) {
        try {
            JwtPrincipalModel principal = (JwtPrincipalModel) authentication.getPrincipal();
            return orderRepository.findById(orderId).map(o -> o.getCustomer().getId().equals(principal.getUserId())).orElse(false);
          } catch (ClassCastException ex) {
            return false;
          }
    }
}
