package com.springeasystock.easystock.controller;

import com.springeasystock.easystock.exception.CustomNotFoundException;
import com.springeasystock.easystock.record.CustomerDTO;
import com.springeasystock.easystock.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO){
        CustomerDTO savedCustomer = customerService.createCustomer(customerDTO);
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable("id") Long customerId){
        CustomerDTO savedCustomer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(savedCustomer);
    }


    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);

    Page<CustomerDTO> customerPage = customerService.getAllCustomers(pageable);

    long totalElements = customerPage.getTotalElements();

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Total-Count", String.valueOf(totalElements));

    return ResponseEntity.ok()
            .headers(headers)
            .body(customerPage.getContent());
}


    @PutMapping("{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable("id") Long customerId,
                                                  @RequestBody CustomerDTO updatedCustomer) {
        CustomerDTO updated = customerService.updateCustomer(customerId, updatedCustomer);
        return ResponseEntity.ok(updated);
}

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable("id") Long customerId) {
        try {
            this.customerService.deleteCustomer(customerId);
            return ResponseEntity.ok("Customer deleted successfully");
        } catch (CustomNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
