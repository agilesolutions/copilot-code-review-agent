package com.example.demo.customer.repository;

// EmployeeRepository interface for CRUD operations on Employee entity
import com.example.demo.customer.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Custom query method to find an employee by email
    Employee findByEmail(String email);
}