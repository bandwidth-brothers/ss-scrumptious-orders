package com.ss.scrumptious_orders.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

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
import com.ss.scrumptious_orders.entity.Customer;
import com.ss.scrumptious_orders.entity.Menuitem;
import com.ss.scrumptious_orders.entity.MenuitemOrder;
import com.ss.scrumptious_orders.entity.MenuitemOrderKey;
import com.ss.scrumptious_orders.entity.Order;
import com.ss.scrumptious_orders.entity.Restaurant;
import com.ss.scrumptious_orders.payment.StripeService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class OrderServiceImplTests {
    
    OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    
    CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
    RestaurantRepository restaurantRepository = Mockito.mock(RestaurantRepository.class);
    MenuitemRepository menuitemRepository = Mockito.mock(MenuitemRepository.class);
    MenuitemOrderRepository menuitemOrderRepository = Mockito.mock(MenuitemOrderRepository.class);
    RestaurantOwnerRepository restaurantOwnerRepository = Mockito.mock(RestaurantOwnerRepository.class);
    StripeService stripeService = Mockito.mock(StripeService.class);
    PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);

    private static Customer mockCustomer;
    private static Restaurant mockRestaurant;
    private static Order mockMaxOrder;
    private static Order mockMinOrder;
    private static Menuitem mockMenuitem;
    private static MenuitemOrder mockMenuitemOrder;
    private static CreateMenuitemOrderDto mockCreateMenuitemOrderDto;

    private final OrderService orderService = new OrderServiceImpl(orderRepository, customerRepository, restaurantRepository, menuitemRepository, menuitemOrderRepository, restaurantOwnerRepository, stripeService, paymentRepository);

    @BeforeAll
    static void beforeAll() {
        // setup Order objs
        mockCustomer = Customer.builder()
            .id(UUID.randomUUID())
            .firstName("John")
            .lastName("Doe")
            .email("test@test.com")
            .phone("555-555-5555")
            .build();

        mockRestaurant = Restaurant.builder()
            .id(Long.valueOf(1))
            .name("Mockitos")
            .build();

            
            mockMaxOrder = Order.builder()
            .id(Long.valueOf(1))
            .customer(mockCustomer)
            .restaurant(mockRestaurant)
            .preparationStatus("testing")
            .confirmationCode("349398dfsjmgldk")
            .orderDiscount(0.10f)
            .submittedAt(ZonedDateTime.now())
            .requestedDeliveryTime(ZonedDateTime.now().plusHours(1))
            .build();
            
            mockMinOrder = Order.builder()
            .id(Long.valueOf(2))
            .customer(mockCustomer)
            .build();
            
            mockMenuitem = Menuitem.builder()
            .id(Long.valueOf(1))
            .name("MockBurger")
            .build();
            
            mockMenuitemOrder = MenuitemOrder.builder()
            .menuitem(mockMenuitem)
            .order(mockMaxOrder)
            .quantity(Long.valueOf(1))
            .build();
            
            mockCreateMenuitemOrderDto = CreateMenuitemOrderDto.builder()
                .menuitemId(mockMenuitem.getId())
                .quantity(Long.valueOf(1))
                .build();

    }

    @AfterEach
    void beforeEach() {
        Mockito.reset(orderRepository);
        Mockito.reset(customerRepository);
        Mockito.reset(restaurantRepository);
        Mockito.reset(menuitemRepository);
        Mockito.reset(menuitemOrderRepository);
    }

    @Test
    void getAllOrdersTest() {
        List<Order> expected = new ArrayList<>();
        expected.add(mockMaxOrder);
        expected.add(mockMinOrder);
        when(orderRepository.findAll()).thenReturn(expected);

        List<Order> actual = orderService.getAllOrders();

        assertEquals(expected, actual);
    }

    @Test
    void createOrderMaxDtoTest() {
        CreateMenuitemOrderDto[] mockCreateMenuitemOrderDtos = {mockCreateMenuitemOrderDto};
        CreateOrderDto mockCreateOrderDto = CreateOrderDto.builder()
            .customerId(mockCustomer.getId())
            .restaurantId(mockRestaurant.getId())
            .preparationStatus("testing")
            .confirmationCode("349398dfsjmgldk")
            .orderDiscount(0.10f)
            .submittedAt(ZonedDateTime.now())
            .requestedDeliveryTime(ZonedDateTime.now().plusHours(1))
            .menuitems(mockCreateMenuitemOrderDtos)
            .build();

        when(customerRepository.findById(mockCreateOrderDto.getCustomerId())).thenReturn(Optional.of(mockCustomer));
        when(restaurantRepository.findById(mockCreateOrderDto.getRestaurantId())).thenReturn(Optional.of(mockRestaurant));
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(mockMaxOrder);
        
        when(orderRepository.findById(mockMaxOrder.getId())).thenReturn(Optional.of(mockMaxOrder));
        when(menuitemRepository.findById(mockCreateMenuitemOrderDto.getMenuitemId())).thenReturn(Optional.of(mockMenuitem));
        when(menuitemOrderRepository.saveAndFlush(mockMenuitemOrder)).thenReturn(mockMenuitemOrder);

        Order order = orderService.createNewOrder(mockCreateOrderDto);

        assertEquals(mockMaxOrder, order);
    }

    @Test
    void createOrderMinDtoTest() {
        CreateOrderDto mockCreateOrderDto = CreateOrderDto.builder()
            .customerId(mockCustomer.getId())
            .build();
        when(customerRepository.findById(mockCreateOrderDto.getCustomerId())).thenReturn(Optional.of(mockCustomer));
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(mockMinOrder);

        Order order = orderService.createNewOrder(mockCreateOrderDto);

        assertEquals(mockMinOrder, order);
    }

    @Test
    void getOrderByIdTest() {
        when(orderRepository.findById(mockMaxOrder.getId())).thenReturn(Optional.of(mockMaxOrder));

        Order actual = orderService.getOrderById(mockMaxOrder.getId());

        assertEquals(mockMaxOrder, actual);
    }

    @Test
    void getOrdersByCustomerIdTest() {
        when(customerRepository.findById(mockCustomer.getId())).thenReturn(Optional.of(mockCustomer));
        List<Order> expectedOrders = new ArrayList<>();
        expectedOrders.add(mockMaxOrder);
        expectedOrders.add(mockMinOrder);
        when(orderRepository.findByCustomer(mockCustomer)).thenReturn(expectedOrders);
        
        List<Order> actual = orderService.getOrdersByCustomerId(mockCustomer.getId());

        assertEquals(expectedOrders, actual);
    }

    @Test
    void updateOrderMaxDtoTest() {
        CreateMenuitemOrderDto[] mockCreateMenuitemOrderDtos = {mockCreateMenuitemOrderDto};
        UpdateOrderDto mockUpdateOrderDto = UpdateOrderDto.builder()
            .customerId(mockCustomer.getId())
            .restaurantId(mockRestaurant.getId())
            .preparationStatus("testing")
            .confirmationCode("349398dfsjmgldk")
            .orderDiscount(0.10f)
            .submittedAt(ZonedDateTime.now())
            .requestedDeliveryTime(ZonedDateTime.now().plusHours(1))
            .menuitems(mockCreateMenuitemOrderDtos)
            .build();

        when(customerRepository.findById(mockUpdateOrderDto.getCustomerId())).thenReturn(Optional.of(mockCustomer));
        when(restaurantRepository.findById(mockUpdateOrderDto.getRestaurantId())).thenReturn(Optional.of(mockRestaurant));
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(mockMaxOrder);
        
        when(menuitemOrderRepository.findById(any(MenuitemOrderKey.class))).thenReturn(Optional.of(mockMenuitemOrder));
        when(orderRepository.findById(mockMaxOrder.getId())).thenReturn(Optional.of(mockMaxOrder));
        when(menuitemRepository.findById(mockCreateMenuitemOrderDto.getMenuitemId())).thenReturn(Optional.of(mockMenuitem));
        when(menuitemOrderRepository.saveAndFlush(any(MenuitemOrder.class))).thenReturn(mockMenuitemOrder);

        Order order = orderService.updateOrder(mockMaxOrder.getId(), mockUpdateOrderDto);

        assertEquals(mockMaxOrder, order);
    }

    @Test
    void updateOrderMinDtoTest() {
        UpdateOrderDto mockUpdateOrderDto = UpdateOrderDto.builder().build();

        when(orderRepository.findById(mockMinOrder.getId())).thenReturn(Optional.of(mockMinOrder));
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(mockMinOrder);

        Order order = orderService.updateOrder(mockMinOrder.getId(), mockUpdateOrderDto);

        assertEquals(mockMinOrder, order);
    }

    @Test
    void addItemToOrderTest() {
        
        when(menuitemOrderRepository.findById(any(MenuitemOrderKey.class))).thenReturn(Optional.of(mockMenuitemOrder));
        when(orderRepository.findById(mockMaxOrder.getId())).thenReturn(Optional.of(mockMaxOrder));
        when(menuitemRepository.findById(mockCreateMenuitemOrderDto.getMenuitemId())).thenReturn(Optional.of(mockMenuitem));
        when(menuitemOrderRepository.saveAndFlush(any(MenuitemOrder.class))).thenReturn(mockMenuitemOrder);

        MenuitemOrder actual = orderService.addItemToOrder(mockMaxOrder.getId(), mockCreateMenuitemOrderDto);

        assertEquals(mockMenuitemOrder, actual);
    }

    @Test
    void deleteOrderTest() {
        when(orderRepository.findById(mockMaxOrder.getId())).thenReturn(Optional.of(mockMinOrder));

        orderService.deleteOrder(mockMinOrder.getId());
    }
    
    @Test
    void addItemNoQuantityToOrderTest() {
        MenuitemOrder noQuantity = MenuitemOrder.builder()
            .menuitem(mockMenuitem)
            .order(mockMaxOrder)
            .build();

        CreateMenuitemOrderDto noQuantityDto = CreateMenuitemOrderDto.builder()
        .menuitemId(mockMenuitem.getId())
        .build();

        //when(menuitemOrderRepository.findById(any(MenuitemOrderKey.class))).thenReturn(Optional.of(noQuantity));
        when(orderRepository.findById(mockMaxOrder.getId())).thenReturn(Optional.of(mockMaxOrder));
        when(menuitemRepository.findById(mockCreateMenuitemOrderDto.getMenuitemId())).thenReturn(Optional.of(mockMenuitem));
        when(menuitemOrderRepository.saveAndFlush(any(MenuitemOrder.class))).thenReturn(noQuantity);

        MenuitemOrder actual = orderService.addItemToOrder(mockMaxOrder.getId(), noQuantityDto);

        assertEquals(noQuantity, actual);
    }

    @Test
    void editItemQuantityTest() {
        when(menuitemOrderRepository.findById(any(MenuitemOrderKey.class))).thenReturn(Optional.of(mockMenuitemOrder));
        when(menuitemOrderRepository.saveAndFlush(any(MenuitemOrder.class))).thenReturn(mockMenuitemOrder);

        MenuitemOrder actual = orderService.editItemQuantity(mockMaxOrder.getId(), mockMenuitem.getId(), mockMenuitemOrder.getQuantity());

        assertEquals(mockMenuitemOrder, actual);
    }

    @Test
    void removeItemFromOrderTest() {
        when(menuitemOrderRepository.findById(new MenuitemOrderKey(mockMenuitem.getId(), mockMaxOrder.getId()))).thenReturn(Optional.of(mockMenuitemOrder));
        orderService.removeItemFromOrder(mockMaxOrder.getId(), mockMenuitem.getId());
    }

    @Test
    void removeAllItemsFromOrderTest() {
        orderService.removeAllItemsFromOrder(mockMaxOrder.getId());
    }
}
