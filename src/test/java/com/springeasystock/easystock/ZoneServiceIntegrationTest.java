package com.springeasystock.easystock;

import com.springeasystock.easystock.model.Zone;
import com.springeasystock.easystock.record.WaveDTO;
import com.springeasystock.easystock.record.ZoneDTO;
import com.springeasystock.easystock.repo.WaveRepository;
import com.springeasystock.easystock.repo.ZoneRepository;
import com.springeasystock.easystock.service.impl.ZoneServiceImpl;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ZoneServiceIntegrationTest {
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withDatabaseName("easystock")
            .withUsername("postgres")
            .withPassword("user");
    @Autowired
    private ZoneServiceImpl zoneService;
    @Autowired
    private ZoneRepository zoneRepository;
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
    public void testCreateZone() {
        ZoneDTO zoneDTO = new ZoneDTO(1L, null, "Storage", "A", null, 1);
        ZoneDTO createdZone = zoneService.createZones(zoneDTO);
        assertNotNull(createdZone);
        assertEquals(zoneDTO.name(), createdZone.name());
        assertEquals(zoneDTO.type(), createdZone.type());
        assertEquals(zoneDTO.itemCount(), createdZone.itemCount());
    }
    @Test
    public void testGetZoneById() {
        ZoneDTO zoneDTO = new ZoneDTO(1L, null, "Storage", "A", null, 1);
        ZoneDTO createdZone = zoneService.createZones(zoneDTO);
        ZoneDTO fetchedZone = zoneService.getZoneById(createdZone.id());
        assertNotNull(fetchedZone);
        assertEquals(createdZone.id(), fetchedZone.id());
    }
    @Test
    public void testDeleteZone() {
        ZoneDTO zoneDTO = new ZoneDTO(2L, null, "Storage", "A", null, 1);
        ZoneDTO createdZone = zoneService.createZones(zoneDTO);
        zoneService.deleteZone(createdZone.id());
        assertThrows(CustomNotFoundException.class, () -> zoneService.getZoneById(createdZone.id()));
    }
    @Test
    public void testUpdateZone() {
        ZoneDTO zoneDTO = new ZoneDTO(1L, null, "Storage", "A", null, 1);
        ZoneDTO createdZone = zoneService.createZones(zoneDTO);
        ZoneDTO updatedZone = new ZoneDTO(1L, null, "D", "B", null, 1);
        ZoneDTO updatedZoneResult = zoneService.updateZone(createdZone.id(), updatedZone);
        assertNotNull(updatedZoneResult);
        assertEquals(updatedZone.name(), updatedZoneResult.name());
        assertEquals(updatedZone.type(), updatedZoneResult.type());
        assertEquals(updatedZone.itemCount(), updatedZoneResult.itemCount());
    }
    @Test
    public void testGetAllZones() {
        ZoneDTO zoneDTO = new ZoneDTO(1L, null, "Storage", "A", null, 1);
        ZoneDTO zoneDTO2 = new ZoneDTO(2L, null, "Storage", "A", null, 1);
        zoneService.createZones(zoneDTO);
        zoneService.createZones(zoneDTO2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<ZoneDTO> zones = zoneService.getAllZones(pageable);
        assertNotNull(zones);
    }
    @Test
    public void testExistsById() {
        Zone zone = new Zone();
        zone.setName("Zone A");
        zoneRepository.save(zone);
        assertTrue(zoneService.existsById(zone.getId()));
        assertFalse(zoneService.existsById(999L));
    }
}
