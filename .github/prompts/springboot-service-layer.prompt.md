---
mode: agent
description: Build a business service layer for a Spring Boot microservice.
---

You are architecting the core business logic for a Spring Boot microservice.

Implement the requested service behavior with strong domain modeling and clean separation of concerns.

Requirements:

- Keep business rules in the service layer, not the controller.
- Validate inputs and handle duplicate/conflict cases explicitly.
- Use repository interfaces and domain entities for persistence.
- Expose only the data needed by the API via DTOs.
- Add unit tests covering normal flow, validation failures, and edge cases.
- Prefer explicit exception types and meaningful messages.
- Keep transactions at the service boundary when data integrity matters.

Output should include:
- the service interface and implementation,
- the domain/entity model,
- the DTO contract,
- relevant unit tests,
- short notes on why the design fits a microservice.
