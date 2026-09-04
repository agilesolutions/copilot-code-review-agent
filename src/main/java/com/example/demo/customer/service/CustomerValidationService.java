package com.example.demo.customer.service;

// Service class for validating customer data
import com.example.demo.customer.entity.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomerValidationService {

    // Method to validate customer data
    public void validateCustomer(Customer customer) {
        if (customer.getFirstName() == null || customer.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (customer.getLastName() == null || customer.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        // Additional validation logic can be added here
    }
}