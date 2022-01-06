package com.ss.scrumptious_orders.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.scrumptious.common_entities.entity.Customer;
import com.ss.scrumptious.common_entities.entity.Menuitem;
import com.ss.scrumptious.common_entities.entity.MenuitemOrder;
import com.ss.scrumptious.common_entities.entity.Order;
import com.ss.scrumptious_orders.dao.OrderRepository;
import com.ss.scrumptious_orders.dto.CreateMenuitemOrderDto;
import com.ss.scrumptious_orders.dto.CreateOrderDto;
import com.ss.scrumptious_orders.security.SecurityConstants;
import com.ss.scrumptious_orders.service.OrderService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class OrderControllerTests {

    @Mock
    static SecurityConstants securityConstants;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    OrderService orderService;

    @MockBean
    OrderRepository orderRepository;

        // setup Order objs
    private Customer mockCustomer = Customer.builder()
        .id(MockUser.MATCH_CUSTOMER.id)
        .firstName("John")
        .lastName("Doe")
        .email(MockUser.MATCH_CUSTOMER.email)
        .phone("555-555-5555")
        .build();

    private Order mockOrder = Order.builder()
        .id(Long.valueOf(1))
        .customer(mockCustomer)
        .preparationStatus("testing")
        .build();

    private Menuitem mockMenuitem = Menuitem.builder()
        .id(Long.valueOf(1))
        .name("MockBurger")
        .build();

    private MenuitemOrder mockMenuitemOrder = MenuitemOrder.builder()
        .menuitem(mockMenuitem)
        .order(mockOrder)
        .quantity(Long.valueOf(1))
        .build();

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
          .webAppContextSetup(context)
          .apply(springSecurity())
          .build();

        Mockito.reset(orderService);
        Mockito.reset(orderRepository);

        when(securityConstants.getSECRET()).thenReturn("MercuryExploration");
        when(securityConstants.getTOKEN_PREFIX()).thenReturn("Bearer ");
        when(securityConstants.getHEADER_STRING()).thenReturn("Authorization");
        when(securityConstants.getAUTHORITY_CLAIM_KEY()).thenReturn("Authorities");
        when(securityConstants.getUSER_ID_CLAIM_KEY()).thenReturn("UserId");
    }

    String getJwt(MockUser mockUser) {

        String jwt = JWT.create()
            .withSubject(mockUser.email)
            .withClaim(securityConstants.getUSER_ID_CLAIM_KEY(), mockUser.id.toString())
            .withClaim(securityConstants.getAUTHORITY_CLAIM_KEY(), mockUser.getAuthority())
            .sign(Algorithm.HMAC512(securityConstants.getSECRET()));
        return securityConstants.getTOKEN_PREFIX() + jwt;
    }

    @Test
    public void getAllOrdersAuthorizationTest() throws Exception {
        List<Order> orders = new ArrayList<>();
        orders.add(mockOrder);
        when(orderService.getAllOrders()).thenReturn(orders);

        MockUser[] authed = {MockUser.ADMIN};

        for (MockUser user : authed) {
            mvc.perform(get("/orders").header(securityConstants.getHEADER_STRING(), getJwt(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]preparationStatus").value(mockOrder.getPreparationStatus()));
        }

        MockUser[] unauthed = {MockUser.DEFAULT,
            MockUser.DRIVER,
            MockUser.MATCH_CUSTOMER,
            MockUser.OWNER,
            MockUser.UNMATCH_CUSTOMER};

        for (MockUser user : unauthed) {
            mvc.perform(get("/orders").header(securityConstants.getHEADER_STRING(), getJwt(user)))
                .andExpect(status().isForbidden());
        }

    }

    @Test
    public void getAllOrdersEmptyTest() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        mvc.perform(get("/orders").header(securityConstants.getHEADER_STRING(), getJwt(MockUser.ADMIN)))
        .andExpect(status().isNoContent());
    }

	/*
	 * @Test void getOrderByIdAuthorizationTest() throws Exception { //
	 * orderRepository.findById() is called when looking at authorization for
	 * orderService.getOrderById()
	 * when(orderRepository.findById(mockOrder.getId())).thenReturn(Optional.
	 * ofNullable(mockOrder));
	 * when(orderService.getOrderById(mockOrder.getId())).thenReturn(mockOrder);
	 *
	 * MockUser[] authed = {MockUser.ADMIN, MockUser.MATCH_CUSTOMER};
	 *
	 * for (MockUser user : authed) { mvc.perform(get("/orders/" +
	 * mockOrder.getId()).header(securityConstants.getHEADER_STRING(),
	 * getJwt(user))) .andExpect(status().isOk())
	 * .andExpect(jsonPath("$.preparationStatus").value(mockOrder.
	 * getPreparationStatus())); }
	 *
	 * MockUser[] unauthed = {MockUser.DEFAULT, MockUser.DRIVER, MockUser.OWNER,
	 * MockUser.UNMATCH_CUSTOMER};
	 *
	 * for (MockUser user : unauthed) { mvc.perform(get("/orders/" +
	 * mockOrder.getId()).header(securityConstants.getHEADER_STRING(),
	 * getJwt(user))) .andExpect(status().isForbidden()); } }
	 */

    @Test
    void getOrderByBadIdTest() throws Exception {
        mvc.perform(get("/orders/99999").header(securityConstants.getHEADER_STRING(), getJwt(MockUser.ADMIN)))
            .andExpect(status().isNotFound());
    }

    @Test
    void getOrderByCustomerIdAuthorizationTest() throws Exception {
        List<Order> orders = new ArrayList<>();
        orders.add(mockOrder);
        when(orderService.getOrdersByCustomerId(mockCustomer.getId())).thenReturn(orders);

        MockUser[] authed = {MockUser.ADMIN, MockUser.MATCH_CUSTOMER};

        for (MockUser user : authed) {
            mvc.perform(get("/orders/customers/" + mockCustomer.getId()).header(securityConstants.getHEADER_STRING(), getJwt(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]preparationStatus").value(mockOrder.getPreparationStatus()));
        }

        MockUser[] unauthed = {MockUser.DEFAULT,
            MockUser.DRIVER,
            MockUser.OWNER,
            MockUser.UNMATCH_CUSTOMER};

        for (MockUser user : unauthed) {
            mvc.perform(get("/orders/customers/" + mockCustomer.getId()).header(securityConstants.getHEADER_STRING(), getJwt(user)))
                .andExpect(status().isForbidden());
        }
    }

    @Test
    void getOrderByCustomerIdEmptyTest() throws Exception {
        when(orderService.getOrdersByCustomerId(mockCustomer.getId())).thenReturn(Collections.emptyList());

        mvc.perform(get("/orders/customers/" + mockCustomer.getId()).header(securityConstants.getHEADER_STRING(), getJwt(MockUser.ADMIN)))
            .andExpect(status().isNoContent());
    }

    @Test
    void createOrderAuthorizationTest() throws Exception {
        CreateOrderDto mockDto = CreateOrderDto.builder()
        .customerId(mockCustomer.getId())
        .preparationStatus("testing")
        .build();
        when(orderService.createNewOrder(mockDto)).thenReturn(mockOrder);

        MockUser[] authed = {MockUser.ADMIN, MockUser.MATCH_CUSTOMER};

        for (MockUser user : authed) {
            MvcResult mvcResult = mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockDto)).header(securityConstants.getHEADER_STRING(), getJwt(user)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andReturn();
            assertTrue(mvcResult.getResponse().containsHeader("Location"));

        }

        MockUser[] unauthed = {MockUser.DEFAULT,
            MockUser.DRIVER,
            MockUser.OWNER,
            MockUser.UNMATCH_CUSTOMER};

        for (MockUser user : unauthed) {
            mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockDto)).header(securityConstants.getHEADER_STRING(), getJwt(user)))
                .andExpect(status().isForbidden());
        }
    }

	/*
	 * @Test void updateOrderAuthorizationTest() throws Exception {
	 * when(orderRepository.findById(mockOrder.getId())).thenReturn(Optional.
	 * ofNullable(mockOrder));
	 *
	 * UpdateOrderDto mockDto = UpdateOrderDto.builder() .build();
	 *
	 * MockUser[] authed = {MockUser.ADMIN, MockUser.MATCH_CUSTOMER};
	 *
	 * for (MockUser user : authed) { mvc.perform(put("/orders/" +
	 * mockOrder.getId()) .contentType(MediaType.APPLICATION_JSON) .content(new
	 * ObjectMapper().writeValueAsString(mockDto)).header(securityConstants.
	 * getHEADER_STRING(), getJwt(user))) .andExpect(status().isNoContent());
	 *
	 * }
	 *
	 * MockUser[] unauthed = {MockUser.DEFAULT, MockUser.DRIVER, MockUser.OWNER,
	 * MockUser.UNMATCH_CUSTOMER};
	 *
	 * for (MockUser user : unauthed) { mvc.perform(put("/orders/" +
	 * mockOrder.getId()) .contentType(MediaType.APPLICATION_JSON) .content(new
	 * ObjectMapper().writeValueAsString(mockDto)).header(securityConstants.
	 * getHEADER_STRING(), getJwt(user))) .andExpect(status().isForbidden()); } }
	 */

    @Test
    void deleteOrderAuthorizationTest() throws Exception {
        when(orderRepository.findById(mockOrder.getId())).thenReturn(Optional.ofNullable(mockOrder));

        MockUser[] authed = {MockUser.ADMIN, MockUser.MATCH_CUSTOMER};

        for (MockUser user : authed) {
            mvc.perform(delete("/orders/" + mockOrder.getId())
                .header(securityConstants.getHEADER_STRING(), getJwt(user)))
                    .andExpect(status().isNoContent());
        }

        MockUser[] unauthed = {MockUser.DEFAULT,
            MockUser.DRIVER,
            MockUser.OWNER,
            MockUser.UNMATCH_CUSTOMER};

        for (MockUser user : unauthed) {
            mvc.perform(delete("/orders/" + mockOrder.getId())
                .header(securityConstants.getHEADER_STRING(), getJwt(user)))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    void createMenuitemOrderAuthorizationTest() throws Exception {
        CreateMenuitemOrderDto mockDto = CreateMenuitemOrderDto.builder()
            .menuitemId(mockMenuitem.getId())
            .build();
        when(orderRepository.findById(mockOrder.getId())).thenReturn(Optional.ofNullable(mockOrder));
        when(orderService.addItemToOrder(mockOrder.getId(), mockDto)).thenReturn(mockMenuitemOrder);

        MockUser[] authed = {MockUser.ADMIN, MockUser.MATCH_CUSTOMER};

        for (MockUser user : authed) {
            MvcResult mvcResult = mvc.perform(post("/orders/" + mockOrder.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(mockDto)).header(securityConstants.getHEADER_STRING(), getJwt(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
        assertTrue(mvcResult.getResponse().containsHeader("Location"));
        }

        MockUser[] unauthed = {MockUser.DEFAULT,
            MockUser.DRIVER,
            MockUser.OWNER,
            MockUser.UNMATCH_CUSTOMER};

        for (MockUser user : unauthed) {
            mvc.perform(post("/orders/" + mockOrder.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(mockDto)).header(securityConstants.getHEADER_STRING(), getJwt(user)))
                .andExpect(status().isForbidden());
        }
    }

    @Test
    void updateMenuitemOrderAuthorizationTest() throws Exception {
        Long mockQuantity = Long.valueOf(1);
        when(orderRepository.findById(mockOrder.getId())).thenReturn(Optional.ofNullable(mockOrder));

        MockUser[] authed = {MockUser.ADMIN, MockUser.MATCH_CUSTOMER};

        for (MockUser user : authed) {
            mvc.perform(put("/orders/" + mockOrder.getId() + "/menuitems/" + mockMenuitem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(mockQuantity)).header(securityConstants.getHEADER_STRING(), getJwt(user)))
                .andExpect(status().isNoContent());
        }

        MockUser[] unauthed = {MockUser.DEFAULT,
            MockUser.DRIVER,
            MockUser.OWNER,
            MockUser.UNMATCH_CUSTOMER};

        for (MockUser user : unauthed) {
            mvc.perform(put("/orders/" + mockOrder.getId() + "/menuitems/" + mockMenuitem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(mockQuantity)).header(securityConstants.getHEADER_STRING(), getJwt(user)))
                .andExpect(status().isForbidden());
        }
    }

    @Test
    void deleteMenuitemOrderAuthorizationTest() throws Exception {
        when(orderRepository.findById(mockOrder.getId())).thenReturn(Optional.ofNullable(mockOrder));

        MockUser[] authed = {MockUser.ADMIN, MockUser.MATCH_CUSTOMER};

        for (MockUser user : authed) {
            mvc.perform(delete("/orders/" + mockOrder.getId() + "/menuitems/" + mockMenuitem.getId())
            .header(securityConstants.getHEADER_STRING(), getJwt(user)))
                .andExpect(status().isNoContent());
        }

        MockUser[] unauthed = {MockUser.DEFAULT,
            MockUser.DRIVER,
            MockUser.OWNER,
            MockUser.UNMATCH_CUSTOMER};

        for (MockUser user : unauthed) {
            mvc.perform(delete("/orders/" + mockOrder.getId() + "/menuitems/" + mockMenuitem.getId())
            .header(securityConstants.getHEADER_STRING(), getJwt(user)))
                .andExpect(status().isForbidden());
        }
    }

    @Test
    void deleteAllMenuitemOrdersAuthorizationTest() throws Exception {
        when(orderRepository.findById(mockOrder.getId())).thenReturn(Optional.ofNullable(mockOrder));

        MockUser[] authed = {MockUser.ADMIN, MockUser.MATCH_CUSTOMER};

        for (MockUser user : authed) {
            mvc.perform(delete("/orders/" + mockOrder.getId() + "/menuitems")
            .header(securityConstants.getHEADER_STRING(), getJwt(user)))
                .andExpect(status().isNoContent());
        }

        MockUser[] unauthed = {MockUser.DEFAULT,
            MockUser.DRIVER,
            MockUser.OWNER,
            MockUser.UNMATCH_CUSTOMER};

        for (MockUser user : unauthed) {
            mvc.perform(delete("/orders/" + mockOrder.getId() + "/menuitems")
            .header(securityConstants.getHEADER_STRING(), getJwt(user)))
                .andExpect(status().isForbidden());
        }
    }

    enum MockUser {
        DEFAULT("default@test.com", "ROLE_DEFAULT", UUID.randomUUID()),
        MATCH_CUSTOMER("test@test.com", "ROLE_CUSTOMER", UUID.fromString("a4a9feca-bfe7-4c45-8319-7cb6cdd359db")),
        UNMATCH_CUSTOMER("someOtherCustomer@test.com", "ROLE_CUSTOMER", UUID.randomUUID()),
        OWNER("employee@test.com", "ROLE_OWNER", UUID.randomUUID()),
        DRIVER("travel_agent@test.com", "ROLE_DRIVER", UUID.randomUUID()),
        ADMIN("admin@test.com", "ROLE_ADMIN", UUID.randomUUID());


        final String email;
        final GrantedAuthority grantedAuthority;
        final UUID id;

        MockUser(String email, String grantedAuthority, UUID id) {
          this.email = email;
          this.grantedAuthority = new SimpleGrantedAuthority(grantedAuthority);
          this.id = id;
        }

        public String getAuthority() {
          return grantedAuthority.getAuthority();
        }
      }

      @Test
      void refundOrderTest() throws Exception{

          when(orderRepository.findById(mockOrder.getId())).thenReturn(Optional.ofNullable(mockOrder));
          MockUser[] authed = {MockUser.ADMIN, MockUser.MATCH_CUSTOMER};
          for (MockUser user : authed) {
              mvc.perform(put("/orders/" + mockOrder.getId() + "/refund")
                      .header(securityConstants.getHEADER_STRING(), getJwt(user)))
                      .andExpect(status().isNoContent());
          }

          MockUser[] unauthed = {MockUser.DEFAULT,
                  MockUser.DRIVER,
                  MockUser.OWNER,
                  MockUser.UNMATCH_CUSTOMER};

          for (MockUser user : unauthed) {
              mvc.perform(put("/orders/" + mockOrder.getId() + "/refund")
                      .header(securityConstants.getHEADER_STRING(), getJwt(user)))
                      .andExpect(status().isForbidden());
          }
      }

}
