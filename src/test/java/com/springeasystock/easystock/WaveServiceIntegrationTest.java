package com.springeasystock.easystock;
import com.springeasystock.easystock.model.OrderList;
import com.springeasystock.easystock.model.Wave;
import com.springeasystock.easystock.record.RoleDTO;
import com.springeasystock.easystock.record.WaveDTO;
import com.springeasystock.easystock.repo.RoleRepository;
import com.springeasystock.easystock.repo.WaveRepository;
import com.springeasystock.easystock.service.OrderListService;
import com.springeasystock.easystock.service.impl.WaveServiceImpl;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class WaveServiceIntegrationTest {
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withDatabaseName("easystock")
            .withUsername("postgres")
            .withPassword("user");

    @Autowired
    private WaveServiceImpl waveService;
    @Autowired
    private WaveRepository waveRepository;
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
    public void testCreateWave() {
        OrderList orderList = new OrderList();
        orderList.setId(1L);
        Wave wave = new Wave();
        wave.setWavePriority("1");
        wave.setWaveStatus("READY");
        WaveDTO waveDTO = new WaveDTO(1L, null, "1", "READY");
        WaveDTO createdWave = waveService.createWave(waveDTO);
        assertNotNull(createdWave);
        assertEquals(wave.getWavePriority(), createdWave.wavePriority());
        assertEquals(wave.getWaveStatus(), createdWave.waveStatus());
    }

    @Test
    public void testGetWaveById() {
        OrderList orderList = new OrderList();
        orderList.setId(1L);
        Wave wave = new Wave();
        wave.setWavePriority("Medium");
        wave.setWaveStatus("In Progress");
        wave.setOrderList(Set.of(orderList));
        WaveDTO waveDTO = new WaveDTO(1L, null, "1", "READY");
        WaveDTO createdWave = waveService.createWave(waveDTO);
        WaveDTO fetchedWave = waveService.getWaveById(createdWave.id());
        assertNotNull(fetchedWave);
        assertEquals(createdWave.id(), fetchedWave.id());
    }

    @Test
    public void testDeleteWave() {
        OrderList orderList = new OrderList();
        orderList.setId(1L);
        Wave wave = new Wave();
        wave.setWavePriority("Low");
        wave.setWaveStatus("Completed");
        wave.setOrderList(Set.of(orderList));
        WaveDTO waveDTO = new WaveDTO(1L, null, "1", "READY");
        WaveDTO createdWave = waveService.createWave(waveDTO);
        waveService.deleteWave(createdWave.id());
        assertThrows(CustomNotFoundException.class, () -> waveService.getWaveById(createdWave.id()));
    }
    @Test
    public void testUpdateRole() {
        WaveDTO waveDTO = new WaveDTO(2L, null, "1", "READY");
        WaveDTO createdWave = waveService.createWave(waveDTO);
        WaveDTO updatedWave = new WaveDTO(2L, null, "1", "READY");
        WaveDTO updatedWaveResult = waveService.updateWave(createdWave.id(), updatedWave);
        assertNotNull(updatedWaveResult);
        assertEquals(updatedWave.waveStatus(), updatedWaveResult.waveStatus());
        assertEquals(updatedWave.wavePriority(), updatedWaveResult.wavePriority());
    }
    @Test
    public void testGetAllWaves() {
        WaveDTO waveDTO = new WaveDTO(1L, null, "1", "READY");
        WaveDTO waveDTO2 = new WaveDTO(2L, null, "1", "READY");
        waveService.createWave(waveDTO);
        waveService.createWave(waveDTO2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<WaveDTO> waveDTOS = waveService.getAllWaves(pageable);
        assertNotNull(waveDTOS);
    }
    @Test
    public void testExistsById() {
        Wave wave = new Wave();
        wave.setWaveStatus("READY");
        waveRepository.save(wave);
        assertTrue(waveService.existsById(wave.getId()));
        assertFalse(waveService.existsById(999L));
    }
}

