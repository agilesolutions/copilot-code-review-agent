---
mode: agent
description: Add persistence, repository patterns, and migration support for a Spring Boot microservice.
---

You are implementing the data access layer for a Spring Boot microservice.

Use the following approach:

- Configure JPA/Hibernate with Spring Data repositories.
- Add database migration support with Flyway or Liquibase as appropriate.
- Use PostgreSQL or the required database driver for the environment.
- Keep entity mappings explicit and domain-oriented.
- Add indexes, constraints, and naming conventions suitable for production services.
- Implement repository queries that match the use cases without leaking persistence details into the controller layer.
- Preserve transaction boundaries for write-heavy operations.

Also include:
- application configuration for datasource and migration settings,
- a basic schema migration script,
- integration tests or repository tests covering the data layer,
- a short explanation of design choices and constraints.
