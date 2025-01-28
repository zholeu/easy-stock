package com.springeasystock.easystock;
import com.springeasystock.easystock.exception.CustomNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import com.springeasystock.easystock.service.impl.CustomerServiceImpl;
import com.springeasystock.easystock.record.CustomerDTO;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomerServiceIntegrationTest {

    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withDatabaseName("easystock")
            .withUsername("postgres")
            .withPassword("user");

    @Autowired
    private CustomerServiceImpl customerService;

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
    public void testCreateCustomer() {
        CustomerDTO customerDTO = new CustomerDTO(1L,"Jan", "Doe", "john.doe@example.com", "123 Main St");

        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);

        assertNotNull(createdCustomer);
        assertEquals(customerDTO.name(), createdCustomer.name());
        assertEquals(customerDTO.surname(), createdCustomer.surname());
        assertEquals(customerDTO.email(), createdCustomer.email());
    }

    @Test
    public void testGetCustomerById() {
        CustomerDTO customerDTO = new CustomerDTO(1L,"Jane", "Doe", "jane.doe@example.com", "456 Elm St");
        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);

        CustomerDTO fetchedCustomer = customerService.getCustomerById(createdCustomer.id());

        assertNotNull(fetchedCustomer);
        assertEquals(createdCustomer.id(), fetchedCustomer.id());
    }

    @Test
    public void testDeleteCustomer() {
        CustomerDTO customerDTO = new CustomerDTO(19L,"Tom", "Smith", "tom.smith@example.com", "789 Pine St");
        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);

        customerService.deleteCustomer(createdCustomer.id());

        assertThrows(CustomNotFoundException.class, () -> customerService.getCustomerById(createdCustomer.id()));
    }

    @Test
    public void testUpdateCustomer() {
        CustomerDTO customerDTO = new CustomerDTO(1L,"Alice", "Johnson", "alice.johnson@example.com", "101 Maple St");
        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
        CustomerDTO updatedCustomer = new CustomerDTO(1L,"Alice", "Smith", "alice.smith@example.com", "101 Maple St");
        CustomerDTO updatedCustomerResult = customerService.updateCustomer(createdCustomer.id(), updatedCustomer);
        assertNotNull(updatedCustomerResult);
        assertEquals(updatedCustomer.name(), updatedCustomerResult.name());
        assertEquals(updatedCustomer.surname(), updatedCustomerResult.surname());
        assertEquals(updatedCustomer.email(), updatedCustomerResult.email());
    }

    @Test
    public void testGetAllCustomers() {
        CustomerDTO customer1 = new CustomerDTO(1L, "John", "Doe", "john.doe@example.com", "123 Main St");
        CustomerDTO customer2 = new CustomerDTO(2L, "Jane", "Smith", "jane.smith@example.com", "456 Elm St");
        customerService.createCustomer(customer1);
        customerService.createCustomer(customer2);
        Pageable pageable = PageRequest.of(0, 10); // First page with 10 items
        Page<CustomerDTO> customerPage = customerService.getAllCustomers(pageable);
        assertNotNull(customerPage);
    }
}

