package com.springeasystock.easystock;

import com.springeasystock.easystock.model.Role;
import com.springeasystock.easystock.record.ItemDTO;
import com.springeasystock.easystock.record.RoleDTO;
import com.springeasystock.easystock.repo.OrderListRepository;
import com.springeasystock.easystock.repo.RoleRepository;
import com.springeasystock.easystock.service.impl.RoleServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class RoleServiceIntegrationTest {
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withDatabaseName("easystock")
            .withUsername("postgres")
            .withPassword("user");

    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private RoleRepository roleRepository;
    @BeforeAll
    public static void setUp() throws InterruptedException {
        postgresContainer.start();
        // Wait for the database container to be ready
        while (!postgresContainer.isRunning()) {
            Thread.sleep(1000); // sleep for 1 second before checking again
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
    public void testCreateRole() {
        RoleDTO roleDTO = new RoleDTO(1L, "Admin", null);
        RoleDTO createdRole = roleService.createRole(roleDTO);
        assertNotNull(createdRole);
        assertEquals(roleDTO.name(), createdRole.name());
    }

    @Test
    public void testGetRoleById() {
        RoleDTO roleDTO = new RoleDTO(1L, "User", null);
        RoleDTO createdRole = roleService.createRole(roleDTO);
        RoleDTO fetchedRole = roleService.getRoleById(createdRole.id());
        assertNotNull(fetchedRole);
        assertEquals(createdRole.id(), fetchedRole.id());
    }

    @Test
    public void testDeleteRole() {
        RoleDTO roleDTO = new RoleDTO(2L, "Manager", null);
        RoleDTO createdRole = roleService.createRole(roleDTO);
        roleService.deleteRole(createdRole.id());
        assertThrows(CustomNotFoundException.class, () -> roleService.getRoleById(createdRole.id()));
    }

    @Test
    public void testUpdateRole() {
        RoleDTO roleDTO = new RoleDTO(1L, "Editor", null);
        RoleDTO createdRole = roleService.createRole(roleDTO);
        RoleDTO updatedRole = new RoleDTO(1L, "SuperEditor", null);
        RoleDTO updatedRoleResult = roleService.updateRole(createdRole.id(), updatedRole);
        assertNotNull(updatedRoleResult);
        assertEquals(updatedRole.name(), updatedRoleResult.name());
    }
    @Test
    public void testGetAllRoles() {
        RoleDTO roleDTO = new RoleDTO(1L, "Admin", null);
        RoleDTO roleDTO2 = new RoleDTO(1L, "Admin", null);
        roleService.createRole(roleDTO);
        roleService.createRole(roleDTO2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoleDTO> roles = roleService.getAllRoles(pageable);
        assertNotNull(roles);
    }
    @Test
    public void testExistsById() {
        Role role = new Role();
        role.setName("Admin");
        roleRepository.save(role);
        assertTrue(roleService.existsById(role.getId()));
        assertFalse(roleService.existsById(999L));
    }
}
