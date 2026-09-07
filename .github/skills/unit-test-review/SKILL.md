---
name: unit-test-review
description: Review a Java Spring Boot application for unit test coverage, structure, and maintainability standards. Use when assessing an existing Spring Boot application rather than reviewing only a specific PR.
---

# Mandatory Unit Test Coverage Verification

## Purpose

Review an existing Java Spring Boot application and assess whether its unit test coverage, structure, and implementation conventions follow good engineering standards.

# Review Principles

1. Identify every newly added or substantially modified production class under `src/main/java`.
2. For each production class, determine whether it contains executable logic or behavior that should be unit tested.
3. Search the complete test source tree under:
    - `src/test/java`
4. Find tests corresponding to each production class.
5. Do not assume a test exists merely because a test package exists.
6. Verify that the test actually exercises the new or modified behavior.
7. Report a finding when a new production class containing meaningful business/application logic has no corresponding unit test.
8. Report a finding when an existing test class exists but does not test the newly introduced behavior.
9. Report a finding when important new branches, error handling, validation,
   or business rules have no meaningful test coverage.
10. Do not require unit tests for classes that are purely declarative,
    configuration-only, generated code, DTOs/records without behavior,
    or trivial framework wiring unless project standards explicitly require it.
11. Prefer behavioral coverage over a simple test-class existence check.
12. Report a finding when a test class exists but does not meaningfully exercise the new or modified behavior.