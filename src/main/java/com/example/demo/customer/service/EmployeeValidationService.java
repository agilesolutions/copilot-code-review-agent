package com.example.demo.customer.service;

// validate employee data attributes
public class EmployeeValidationService {
    public boolean isValidEmployeeId(String employeeId) {
        // Check if employeeId is not null and matches a specific pattern (e.g., alphanumeric and 5-10 characters long)
        return employeeId != null && employeeId.matches("^[a-zA-Z0-9]{5,10}$");
    }
}
