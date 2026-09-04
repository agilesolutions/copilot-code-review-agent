package com.example.demo.customer.service;

// Employee service implementation, covering all CRUD operations and business logic related to Employee entity

import com.example.demo.customer.dto.Employee;
import com.example.demo.customer.repository.EmployeeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public Employee createEmployee(Employee employee) {
        com.example.demo.customer.entity.Employee savedEmployee =
                employeeRepository.save(toEntity(employee));
        return toDto(savedEmployee);
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        return employeeRepository.findById(id)
                .map(existingEmployee -> {
                    BeanUtils.copyProperties(updatedEmployee, existingEmployee, "id");
                    existingEmployee.setId(id);
                    return toDto(employeeRepository.save(existingEmployee));
                })
                .orElse(null);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    private Employee toDto(com.example.demo.customer.entity.Employee employee) {
        if (employee == null) {
            return null;
        }

        return new Employee(employee.getId(), employee.getFirstName(), employee.getEmail());
    }

    private com.example.demo.customer.entity.Employee toEntity(Employee employee) {
        if (employee == null) {
            return null;
        }

        com.example.demo.customer.entity.Employee entity =
                new com.example.demo.customer.entity.Employee();
        BeanUtils.copyProperties(employee, entity);
        return entity;
    }
}