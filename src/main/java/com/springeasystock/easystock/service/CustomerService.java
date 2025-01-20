package com.springeasystock.easystock.service;

import com.springeasystock.easystock.model.Customer;
import com.springeasystock.easystock.record.CustomerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    public CustomerDTO createCustomer(CustomerDTO customerDTO);
    CustomerDTO getCustomerById(Long customerId);

//    List<CustomerDTO> getAllCustomers();
    public Page<CustomerDTO> getAllCustomers(Pageable pageable);
    void deleteCustomer(Long customerId);
    public CustomerDTO updateCustomer(Long customerId, CustomerDTO updatedCustomer);
    public boolean existsById(Long id);
    public Optional<Customer> existsByIdOpt(Long id);
    public List<CustomerDTO> getLimitedCustomers(int limit);

}
