package com.springeasystock.easystock;

import com.springeasystock.easystock.record.OrderListDTO;
import com.springeasystock.easystock.service.impl.OrderListServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import com.springeasystock.easystock.exception.CustomNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderListServiceIntegrationTest {

    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withDatabaseName("easystock")
            .withUsername("postgres")
            .withPassword("user");

    @Autowired
    private OrderListServiceImpl orderListService;

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
}
