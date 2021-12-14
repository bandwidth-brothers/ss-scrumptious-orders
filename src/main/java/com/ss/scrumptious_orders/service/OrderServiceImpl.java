package com.ss.scrumptious_orders.service;



import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.money.Monetary;
import javax.transaction.Transactional;

import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import com.ss.scrumptious.common_entities.entity.Customer;
import com.ss.scrumptious.common_entities.entity.Menuitem;
import com.ss.scrumptious.common_entities.entity.MenuitemOrder;
import com.ss.scrumptious.common_entities.entity.MenuitemOrderKey;
import com.ss.scrumptious.common_entities.entity.Order;
import com.ss.scrumptious.common_entities.entity.Payment;
import com.ss.scrumptious.common_entities.entity.Restaurant;
import com.ss.scrumptious.common_entities.entity.RestaurantOwner;
import com.ss.scrumptious_orders.dao.CustomerRepository;
import com.ss.scrumptious_orders.dao.MenuitemOrderRepository;
import com.ss.scrumptious_orders.dao.MenuitemRepository;
import com.ss.scrumptious_orders.dao.OrderRepository;
import com.ss.scrumptious_orders.dao.PaymentRepository;
import com.ss.scrumptious_orders.dao.RestaurantOwnerRepository;
import com.ss.scrumptious_orders.dao.RestaurantRepository;
import com.ss.scrumptious_orders.dto.CreateMenuitemOrderDto;
import com.ss.scrumptious_orders.dto.CreateOrderDto;
import com.ss.scrumptious_orders.dto.UpdateOrderDto;
import com.ss.scrumptious_orders.exception.NoSuchCustomerException;
import com.ss.scrumptious_orders.exception.NoSuchMenuitemException;
import com.ss.scrumptious_orders.exception.NoSuchMenuitemOrderException;
import com.ss.scrumptious_orders.exception.NoSuchOrderException;
import com.ss.scrumptious_orders.exception.NoSuchRestaurantException;
import com.ss.scrumptious_orders.payment.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

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

    private final RestaurantOwnerRepository restaurantOwnerRepository;

    private final StripeService stripeService;
    private final PaymentRepository paymentRepository;


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
            order.setDiscount(createOrderDto.getOrderDiscount());
        }
        if (createOrderDto.getPreparationStatus() != null) {
            order.setPreparationStatus(createOrderDto.getPreparationStatus());
        }
        if (createOrderDto.getRequestedDeliveryTime() != null) {
            order.setRequestedDeliveryTime(createOrderDto.getRequestedDeliveryTime());
        }
        if (createOrderDto.getSubmittedAt() != null) {
            order.setSubmittedAt(createOrderDto.getSubmittedAt());
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
            order.setDiscount(updateOrderDto.getOrderDiscount());
        }
        if (updateOrderDto.getPreparationStatus() != null) {
            order.setPreparationStatus(updateOrderDto.getPreparationStatus());
        }
        if (updateOrderDto.getRequestedDeliveryTime() != null) {
            order.setRequestedDeliveryTime(updateOrderDto.getRequestedDeliveryTime());
        }
        if (updateOrderDto.getSubmittedAt() != null) {
            order.setSubmittedAt(updateOrderDto.getSubmittedAt());
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


	@Override
	public List<Order> getAllOrdersByOwner(UUID ownerId) {
		// Get Owner
		RestaurantOwner owner = restaurantOwnerRepository.findById(ownerId).orElseThrow(null);
		log.info("Owner" + owner.getFirstName());
		// Get All Restaurants owned by Owner
		List<Restaurant> restaurants = restaurantRepository.findByOwner(owner);
		log.info("Restaurants Size" +restaurants.size());
		List<Order> orders = new ArrayList<Order>();
		// Loop through all restaurants and Append all orders from each restaurant
		restaurants.stream().forEach(restaurant -> {
			log.info("Restaurant Name = " + restaurant.getName());
			orders.addAll(orderRepository.findByRestaurant(restaurant));
		});
		
		
		return orders;
	}

	@Override
	public List<Order> getAllOrdersByRestaurant(Long restaurantId) {
		Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(null);
		List<Order> orders = orderRepository.findByRestaurant(restaurant);
		return orders;
	}

    @Transactional
    @Override
    public String placeOrder(Long orderId, String paymentToken) {
        String confirmationCode = "";
        try{
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new NoSuchOrderException(orderId));
            Integer cost = (int) (getTotalCost(order) * (1 - order.getDiscount()) * 100) ;
            Charge charge = stripeService.charge(orderId, cost, paymentToken);
            log.info("charge: " + charge.getPaymentMethod() + " id: " + charge.getId());
            if (charge.getId() != null){

                Payment pay = Payment.builder().customer(order.getCustomer()).name(charge.getPaymentMethod()).stripeId(charge.getId()).paymentStatus("paid").build();
                paymentRepository.save(pay);
                order.setConfirmationCode(UUID.randomUUID().toString());
                order.setSubmittedAt(ZonedDateTime.now());
                order.setPreparationStatus("ORDER PLACED ");
                orderRepository.save(order);
            }
            confirmationCode = order.getConfirmationCode();
            return confirmationCode;

        }catch (StripeException stripeException){
            log.error("orderId: " + orderId + " --stripeException: " + stripeException);
            return confirmationCode;
        }
    }

    public double getTotalCost(Order order){

        double sum = order.getMenuitemOrder().stream().mapToDouble(o ->
                o.getMenuitem().getPrice().floatValue() * (1 - o.getMenuitem().getDiscount()) * o.getQuantity()
        ).sum();
        return  sum;
    }

}
