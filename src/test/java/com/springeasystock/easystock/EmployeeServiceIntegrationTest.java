package com.springeasystock.easystock;

import com.springeasystock.easystock.exception.CustomNotFoundException;
import com.springeasystock.easystock.model.Employee;
import com.springeasystock.easystock.model.Role;
import com.springeasystock.easystock.record.EmployeeDTO;
import com.springeasystock.easystock.repo.EmployeeRepository;
import com.springeasystock.easystock.repo.RoleRepository;
import com.springeasystock.easystock.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import java.util.HashSet;
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
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RoleRepository roleRepository;

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

    @Test
    @Transactional
    public void testAssignRoleToEmployee() {
        Employee employee = new Employee();
        employee.setUsername("john_doe");
        employeeRepository.save(employee);
        Role role = new Role();
        role.setName("Admin");
        roleRepository.save(role);
        Employee updatedEmployee = employeeService.assignItemToOrderList(employee.getId(), role.getId());
        assertNotNull(updatedEmployee);
        assertTrue(updatedEmployee.getRoles().contains(role), "Role should be assigned to the employee");
        Employee savedEmployee = employeeRepository.findById(employee.getId()).get();
        assertTrue(savedEmployee.getRoles().contains(role), "Role should be found in the saved employee's roles");
    }
    @Test
    @Transactional
    public void testUnassignRoleFromEmployee() {
        Employee employee = new Employee();
        employee.setUsername("jane_doe");
        employeeRepository.save(employee);
        Role role = new Role();
        role.setName("Manager");
        roleRepository.save(role);
        Employee updatedEmployee = employeeService.assignItemToOrderList(employee.getId(), role.getId());
        assertNotNull(updatedEmployee);
        assertTrue(updatedEmployee.getRoles().contains(role), "Role should be assigned to the employee");
        Employee unassignedEmployee = employeeService.unassignRoleFromEmployee(role.getId(), employee.getId());
        assertNotNull(unassignedEmployee);
        assertFalse(unassignedEmployee.getRoles().contains(role), "Role should be unassigned from the employee");
        Employee savedEmployee = employeeRepository.findById(employee.getId()).get();
        assertFalse(savedEmployee.getRoles().contains(role), "Role should not be found in the saved employee's roles");
    }
    @Test
    @Transactional
    public void testAssignRoleToEmployeeRollbackOnException() {
        Employee employee = new Employee();
        employee.setUsername("jane_doe");
        employee.setRoles(new HashSet<>());
        employeeRepository.save(employee);
        Role role = new Role();
        role.setName("Editor");
        roleRepository.save(role);
        try {
            employeeService.assignItemToOrderList(employee.getId(), -1L);
            fail("Expected exception to be thrown");
        } catch (Exception e) {
            Employee savedEmployee = employeeRepository.findById(employee.getId()).get();
            assertTrue(savedEmployee.getRoles().isEmpty(), "Roles should be empty since rolled back");
            assertFalse(savedEmployee.getRoles().contains(role), "Role should not be assigned to the employee");
        }
    }
    @Test
    public void testGetAllEmployee() {
        EmployeeDTO employeeDTO = new EmployeeDTO(1L, "john_doe", "ADMIN", null, null, null);
        EmployeeDTO employeeDTO2 = new EmployeeDTO(1L, "john_doe", "ADMIN", null, null, null);
        employeeService.createEmployee(employeeDTO);
        employeeService.createEmployee(employeeDTO2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmployeeDTO> customerPage = employeeService.getAllEmployee(pageable);
        assertNotNull(customerPage);
    }
}
