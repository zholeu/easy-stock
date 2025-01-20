package com.springeasystock.easystock.controller;

import com.springeasystock.easystock.record.EmployeeDTO;
import com.springeasystock.easystock.model.Employee;
import com.springeasystock.easystock.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.validation.Valid;


@AllArgsConstructor
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO){
        EmployeeDTO savedCustomer = employeeService.createEmployee(employeeDTO);
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployee(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeDTO> cmployeePage = employeeService.getAllEmployee(pageable);
        long totalElements = cmployeePage.getTotalElements();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(totalElements));
        return ResponseEntity.ok()
                .headers(headers)
                .body(cmployeePage.getContent());
    }

    @PutMapping("/{employeeId}/role/{roleId}")
    public Employee assignItemToOrderList(
            @PathVariable Long employeeId,
            @PathVariable Long roleId
    ){
        return employeeService.assignItemToOrderList(employeeId, roleId);
    }

    @GetMapping("{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable("id") Long employeeId){
        EmployeeDTO savedEmployee = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(savedEmployee);

    }

    @PutMapping("{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable("id")Long employeeId,
                                                      @RequestBody EmployeeDTO updatedEmployee){
        EmployeeDTO employeeDTO = employeeService.updateEmployee(employeeId, updatedEmployee);
        return ResponseEntity.ok(employeeDTO);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id")Long employeeId){
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok("Deleted Successfully!");
    }

    @DeleteMapping("/{employeeId}/role/{roleId}")
    public ResponseEntity<String> unassignRoleFromEmployee(
            @PathVariable Long employeeId,
            @PathVariable Long roleId
    ) {
        employeeService.unassignRoleFromEmployee(roleId, employeeId);
        return ResponseEntity.ok("Role with ID " + roleId + " was successfully unassigned from Employee with ID " + employeeId + "!");
    }
}
