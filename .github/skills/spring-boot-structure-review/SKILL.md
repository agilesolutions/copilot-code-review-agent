---
name: spring-boot-structure-review
description: Review a Spring Boot application for project structure, architectural organization, Spring Boot conventions, package boundaries, configuration, dependency management, testing structure, and maintainability standards. Use when assessing an existing Spring Boot application rather than reviewing only a specific PR.
---

# Spring Boot Structure & Standards Review

## Purpose

Review an existing Spring Boot application and assess whether its structure, organization, configuration, dependencies, and implementation conventions follow good Spring Boot engineering standards.

The review is intended for:

- Java Spring Boot applications
- Kotlin Spring Boot applications
- REST APIs
- Microservices
- Modular Spring Boot applications
- Backend services

The review should identify structural problems, architectural risks, inconsistencies, and opportunities for improvement.

Do not modify source code unless explicitly requested.

---

# Review Principles

Evaluate the application based on these principles:

1. Prefer simple, conventional Spring Boot structures.
2. Keep responsibilities separated.
3. Make architectural boundaries visible in the package structure.
4. Avoid unnecessary framework complexity.
5. Prefer dependency inversion at architectural boundaries.
6. Keep configuration externalized.
7. Keep business logic independent from infrastructure where practical.
8. Prefer constructor injection.
9. Keep controllers thin.
10. Keep persistence concerns out of domain/business logic.
11. Make integration boundaries explicit.
12. Make testing strategy visible from the project structure.
14. Avoid premature abstraction.

---

# Review Scope

Inspect the complete application where possible.

Prioritize:

- `build.gradle`
- `settings.gradle`
- `src/main/java`
- `src/main/resources`
- `src/test`
- configuration files
- Docker/container configuration
- Kubernetes manifests
- Helm charts
- CI/CD configuration
- dependency management
- application modules

Also inspect:

- package structure
- Spring configuration
- application startup classes
- REST controllers
- services
- repositories
- entities
- DTOs
- mappers
- clients
- messaging components
- exception handling
- security configuration
- observability configuration
- database migrations
- integration tests

---

# 1. Project Structure

Assess whether the project structure is understandable and consistent.

Look for:

- clear separation of production and test code
- sensible Maven/Gradle structure
- consistent package naming
- appropriate root package
- logical grouping of application components
- unnecessary package nesting
- overly large packages
- misplaced classes
- architectural boundaries visible in the structure

Example conventional structure:

    com.example.application
    ├── Application.java
    ├── config
    ├── controller
    ├── service
    ├── repository
    ├── domain
    ├── dto
    ├── mapper
    ├── client
    └── exception

For more complex applications, also consider feature-oriented organization:

    com.example.application
    ├── customer
    │   ├── api
    │   ├── application
    │   ├── domain
    │   └── infrastructure
    └── order
        ├── api
        ├── application
        ├── domain
        └── infrastructure

Do not automatically classify either approach as superior.

Assess whether the chosen structure is appropriate for the application's complexity.

---

# 2. Spring Boot Conventions

Check for appropriate use of Spring Boot.

Review:

- `@SpringBootApplication`
- component scanning
- configuration classes
- profiles
- configuration properties
- dependency injection
- bean definitions
- auto-configuration usage
- Spring Boot starters
- actuator configuration

Identify:

- unnecessary manual configuration
- duplicated configuration
- excessive use of `@Configuration`
- inappropriate component scanning
- unnecessary `@Bean` definitions
- field injection
- static Spring dependencies
- service locator patterns

Preferred:

```java
@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }
}