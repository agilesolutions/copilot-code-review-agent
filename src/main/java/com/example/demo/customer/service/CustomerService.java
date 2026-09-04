package com.example.demo.customer.service;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.dto.CustomerRequest;
import com.example.demo.customer.dto.CustomerResponse;
import com.example.demo.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new DuplicateCustomerException("Customer with email already exists: " + request.getEmail());
        }

        Customer customer = new Customer(request.getFirstName(), request.getLastName(), request.getEmail());
        Customer saved = repository.save(customer);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + id));
        return toResponse(customer);
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + id));

        if (repository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateCustomerException("Customer with email already exists: " + request.getEmail());
        }

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());

        return toResponse(repository.save(customer));
    }

    public void deleteCustomer(Long id) {
        if (!repository.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found: " + id);
        }
        repository.deleteById(id);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCreatedAt());
    }
}
