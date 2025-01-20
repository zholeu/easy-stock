package com.springeasystock.easystock;

import com.springeasystock.easystock.exception.CustomNotFoundException;
import com.springeasystock.easystock.record.CustomerDTO;
import com.springeasystock.easystock.record.EmployeeDTO;
import com.springeasystock.easystock.service.impl.CustomerServiceImpl;
import com.springeasystock.easystock.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EmployeeServiceIntegrationTest {


    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withDatabaseName("easystock")
            .withUsername("postgres")
            .withPassword("user");

    @Autowired
    private EmployeeServiceImpl employeeService;

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
    public void testCreateEmployee() {
        EmployeeDTO employeeDTO = new EmployeeDTO(1L, "john_doe", "ADMIN", null, null, null);

        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);

        assertNotNull(createdEmployee);
        assertEquals(employeeDTO.username(), createdEmployee.username());
        assertEquals(employeeDTO.role(), createdEmployee.role());
    }

    @Test
    public void testGetEmployeeById() {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "jane_doe", "USER", null, null, null);
        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);

        EmployeeDTO fetchedEmployee = employeeService.getEmployeeById(createdEmployee.id());

        assertNotNull(fetchedEmployee);
        assertEquals(createdEmployee.id(), fetchedEmployee.id());
    }

    @Test
    public void testDeleteEmployee() {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "tom_smith", "ADMIN", null, null, null);
        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);

        employeeService.deleteEmployee(createdEmployee.id());

        assertThrows(CustomNotFoundException.class, () -> employeeService.getEmployeeById(createdEmployee.id()));
    }

    @Test
    public void testUpdateEmployee() {
        EmployeeDTO employeeDTO = new EmployeeDTO(null, "alice_johnson", "USER", null, null, null);
        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);

        EmployeeDTO updatedEmployee = new EmployeeDTO(createdEmployee.id(), "alice_smith", "ADMIN", null, null, null);

        EmployeeDTO updatedEmployeeResult = employeeService.updateEmployee(createdEmployee.id(), updatedEmployee);

        assertNotNull(updatedEmployeeResult);
        assertEquals(updatedEmployee.username(), updatedEmployeeResult.username());
        assertEquals(updatedEmployee.role(), updatedEmployeeResult.role());
    }
}
