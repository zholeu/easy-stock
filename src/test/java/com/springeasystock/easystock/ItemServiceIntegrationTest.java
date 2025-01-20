package com.springeasystock.easystock;

import com.springeasystock.easystock.exception.CustomNotFoundException;
import com.springeasystock.easystock.record.ItemDTO;
import com.springeasystock.easystock.record.RoleDTO;
import com.springeasystock.easystock.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemServiceIntegrationTest {

    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withDatabaseName("easystock")
            .withUsername("postgres")
            .withPassword("user");

    @Autowired
    private ItemServiceImpl itemService;

    @BeforeAll
    public static void setUp() {
        postgresContainer.start();
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
    public void testCreateItem() {
        // Create a dummy order list to associate with the item
        ItemDTO itemDTO = new ItemDTO(
                null, "Item A", "Supplier A", 5.0f, 10.5f, 1, 1, 1, new HashSet<>()
        );

        ItemDTO createdItem = itemService.createItem(itemDTO);

        assertNotNull(createdItem);
        assertEquals(itemDTO.name(), createdItem.name());
        assertEquals(itemDTO.supplier(), createdItem.supplier());
        assertEquals(itemDTO.size(), createdItem.size());
        assertEquals(itemDTO.price(), createdItem.price());
        assertEquals(itemDTO.asile(), createdItem.asile());
        assertEquals(itemDTO.rack(), createdItem.rack());
        assertEquals(itemDTO.shelf(), createdItem.shelf());
    }

    @Test
    public void testGetItemById() {
        ItemDTO itemDTO = new ItemDTO(
                null, "Item B", "Supplier B", 7.5f, 15.0f, 2, 2, 2, new HashSet<>()
        );
        ItemDTO createdItem = itemService.createItem(itemDTO);

        ItemDTO fetchedItem = itemService.getItemById(createdItem.id());

        assertNotNull(fetchedItem);
        assertEquals(createdItem.id(), fetchedItem.id());
    }

    @Test
    public void testDeleteItem() {
        ItemDTO itemDTO = new ItemDTO(
                null, "Item C", "Supplier C", 6.0f, 20.0f, 3, 3, 3, new HashSet<>()
        );
        ItemDTO createdItem = itemService.createItem(itemDTO);

        itemService.deleteItem(createdItem.id());

        assertThrows(CustomNotFoundException.class, () -> itemService.getItemById(createdItem.id()));
    }

}
