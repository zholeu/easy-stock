package com.springeasystock.easystock;

import com.springeasystock.easystock.model.Item;
import com.springeasystock.easystock.model.OrderList;
import com.springeasystock.easystock.record.ItemDTO;
import com.springeasystock.easystock.record.OrderListDTO;
import com.springeasystock.easystock.repo.EmployeeRepository;
import com.springeasystock.easystock.repo.ItemRepository;
import com.springeasystock.easystock.repo.OrderListRepository;
import com.springeasystock.easystock.repo.RoleRepository;
import com.springeasystock.easystock.service.impl.OrderListServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import com.springeasystock.easystock.exception.CustomNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderListServiceIntegrationTest {

    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withDatabaseName("easystock")
            .withUsername("postgres")
            .withPassword("user");

    @Autowired
    private OrderListServiceImpl orderListService;
    @Autowired
    private OrderListRepository orderListRepository;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeAll
    public static void setUp() throws InterruptedException {
        postgresContainer.start();
        while (!postgresContainer.isRunning()) {
            Thread.sleep(1000);
        }
        System.out.println("PostgreSQL container started:");
        System.out.println("URL: " + postgresContainer.getJdbcUrl());
        System.out.println("Username: " + postgresContainer.getUsername());
        System.out.println("Password: " + postgresContainer.getPassword());
    }

    @AfterAll
    public static void tearDown() {
        postgresContainer.stop();
    }

    @Test
    public void testCreateOrderList() {
        OrderListDTO orderListDTO = new OrderListDTO(
                1L,
                null,
                "PENDING",
                100.0,
                null,
                null,
                null
        );

        OrderListDTO createdOrderList = orderListService.createOrderList(orderListDTO);

        assertNotNull(createdOrderList);
        assertEquals(orderListDTO.orderStatus(), createdOrderList.orderStatus());
        assertEquals(orderListDTO.totalPrice(), createdOrderList.totalPrice());
    }

    @Test
    public void testGetOrderListById() {
        OrderListDTO orderListDTO = new OrderListDTO(
                1L,
                null,
                "PENDING",
                200.0,
                null,
                null, null
        );
        OrderListDTO createdOrderList = orderListService.createOrderList(orderListDTO);

        OrderListDTO fetchedOrderList = orderListService.getOrderListById(createdOrderList.id());

        assertNotNull(fetchedOrderList);
        assertEquals(createdOrderList.id(), fetchedOrderList.id());
    }

    @Test
    public void testDeleteOrderList() {
        OrderListDTO orderListDTO = new OrderListDTO(
                1L,
                null,
                "PENDING",
                150.0,
                null,
                null, null
        );
        OrderListDTO createdOrderList = orderListService.createOrderList(orderListDTO);

        orderListService.deleteOrderList(createdOrderList.id());

        assertThrows(CustomNotFoundException.class, () -> orderListService.getOrderListById(createdOrderList.id()));
    }

    @Test
    public void testUpdateOrderList() {
        OrderListDTO orderListDTO = new OrderListDTO(
                1L,
                null,
                "PENDING",
                120.0,
                null,
                null, null
        );
        OrderListDTO createdOrderList = orderListService.createOrderList(orderListDTO);

        OrderListDTO updatedOrderList = new OrderListDTO(
                1L,
                null,
                "SHIPPED",
                130.0,
                null,
                null, null
        );
        OrderListDTO updatedOrderListResult = orderListService.updateOrderList(createdOrderList.id(), updatedOrderList);
        assertNotNull(updatedOrderListResult);
        assertEquals(updatedOrderList.orderStatus(), updatedOrderListResult.orderStatus());
        assertEquals(updatedOrderList.totalPrice(), updatedOrderListResult.totalPrice());
    }

    @Test
    public void testGetAllIOrderLists() {
        OrderListDTO orderListDTO = new OrderListDTO(
                1L,
                null,
                "PENDING",
                100.0,
                null,
                null,
                null
        );
        OrderListDTO orderListDTO2 = new OrderListDTO(
                2L,
                null,
                "PENDING",
                100.0,
                null,
                null,
                null
        );
        orderListService.createOrderList(orderListDTO);
        orderListService.createOrderList(orderListDTO2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderListDTO> orderLists = orderListService.getAllOrderLists(pageable);
        assertNotNull(orderLists);
    }
    @Test
    @Transactional
    public void testAssignItemToOrderList() {
        OrderList orderList = new OrderList();
        orderList.setOrderStatus("Pending");
        orderListRepository.save(orderList);
        Item item = new Item();
        item.setName("Laptop");
        itemRepository.save(item);
        OrderList updatedOrderList = orderListService.assignItemToOrderList(orderList.getId(), item.getId());
        assertNotNull(updatedOrderList);
        assertTrue(updatedOrderList.getItemIds().contains(item), "Item should be assigned to the order list");
        OrderList savedOrderList = orderListRepository.findById(orderList.getId()).get();
        assertTrue(savedOrderList.getItemIds().contains(item), "Item should be found in the saved order list");
    }
    @Test
    @Transactional
    public void testUnassignItemFromOrderList() {
        OrderList orderList = new OrderList();
        orderList.setOrderStatus("Pending");
        orderListRepository.save(orderList);
        Item item = new Item();
        item.setName("Smartphone");
        itemRepository.save(item);
        OrderList updatedOrderList = orderListService.assignItemToOrderList(orderList.getId(), item.getId());
        assertNotNull(updatedOrderList);
        assertTrue(updatedOrderList.getItemIds().contains(item), "Item should be assigned to the order list");
        OrderList unassignedOrderList = orderListService.unassignItemFromOrderList(orderList.getId(), item.getId());
        assertNotNull(unassignedOrderList);
        assertFalse(unassignedOrderList.getItemIds().contains(item), "Item should be unassigned from the order list");
        OrderList savedOrderList = orderListRepository.findById(orderList.getId()).get();
        assertFalse(savedOrderList.getItemIds().contains(item), "Item should not be found in the saved order list");
    }
    @Test
    public void testExistsById() {
        OrderList orderList = new OrderList();
        orderList.setOrderStatus("Test Order List");
        orderListRepository.save(orderList);
        boolean exists = orderListService.existsById(orderList.getId());
        assertTrue(exists, "OrderList should exist with the given ID");
        boolean nonExistingExists = orderListService.existsById(999L);
        assertFalse(nonExistingExists, "OrderList should not exist with a non-existent ID");
    }

}
