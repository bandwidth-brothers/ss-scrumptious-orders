package com.ss.scrumptious_orders.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ss.scrumptious_orders.dao.OrderRepository;
import com.ss.scrumptious_orders.dao.RestaurantRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OwnerAuthenticationManager {
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    
    public boolean ownerIdMatches(Authentication authentication, Long restaurantId, UUID ownerId) {
	      try {
	        JwtPrincipalModel principal = (JwtPrincipalModel) authentication.getPrincipal();
	        return restaurantRepository.findById(restaurantId)
					.map(r -> (principal.getUserId().equals(r.getOwner().getId()) && principal.getUserId().equals(ownerId)) )
					.orElse(false);
	      } catch (ClassCastException ex) {
	        return false;
	      }
	  }
    
	public boolean ownerIdMatches(Authentication authentication, UUID ownerId) {
	      try {
	        JwtPrincipalModel principal = (JwtPrincipalModel) authentication.getPrincipal();
	        return principal.getUserId().equals(ownerId);
	      } catch (ClassCastException ex) {
	        return false;
	      }
	  }
	
	public boolean ownerIdMatches(Authentication authentication, Long orderId) {
        try {
            JwtPrincipalModel principal = (JwtPrincipalModel) authentication.getPrincipal();
            return orderRepository.findById(orderId).map(o -> o.getRestaurant().getOwner().getId().equals(principal.getUserId())).orElse(false);
          } catch (ClassCastException ex) {
            return false;
          }
    }
}
