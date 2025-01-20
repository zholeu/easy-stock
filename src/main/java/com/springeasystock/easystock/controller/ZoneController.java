package com.springeasystock.easystock.controller;

import com.springeasystock.easystock.record.ZoneDTO;
import com.springeasystock.easystock.service.ZoneService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private ZoneService zoneService;

    @PostMapping
    public ResponseEntity<ZoneDTO> createZone (@RequestBody ZoneDTO zoneDTO){
        ZoneDTO savedZone = zoneService.createZones(zoneDTO);
        return new ResponseEntity<>(savedZone, HttpStatus.CREATED);

    }

    @GetMapping
    public ResponseEntity<List<ZoneDTO>> getAllZones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ZoneDTO> zonePage = zoneService.getAllZones(pageable);
        long totalElements = zonePage.getTotalElements();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(totalElements));
        return ResponseEntity.ok()
                .headers(headers)
                .body(zonePage.getContent());
    }

    @GetMapping("{id}")
    public ResponseEntity<ZoneDTO> getZoneById(@PathVariable("id") Long zoneId){
        ZoneDTO zoneDTO = zoneService.getZoneById(zoneId);
        return ResponseEntity.ok(zoneDTO);

    }

    @PutMapping("{id}")
    public ResponseEntity<ZoneDTO> updateZone(@PathVariable("id")Long zoneId,
                                              @RequestBody ZoneDTO updatedZone){
        ZoneDTO zoneDTO = zoneService.updateZone(zoneId, updatedZone);
        return ResponseEntity.ok(zoneDTO);

    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteZone(@PathVariable("id")Long zoneId){
        zoneService.deleteZone(zoneId);
        return ResponseEntity.ok("Deleted Successfully!");

    }
}
