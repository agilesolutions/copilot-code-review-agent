package com.example.demo.customer.entity;

// Employee entity with fields id, firstName, lastName, email, createdAt
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "employees", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}