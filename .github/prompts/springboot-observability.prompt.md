---
mode: agent
description: Add observability, logging, and resilience to a Spring Boot microservice.
---

You are improving a Spring Boot microservice for production observability and resilience.

Implement the needed operational concerns while keeping the code clean and maintainable.

Requirements:

- Add structured logging with meaningful correlation IDs and request metadata.
- Expose health, readiness, and metrics endpoints with Spring Boot Actuator.
- Add tracing/logging hooks where appropriate for downstream calls.
- Include retry, circuit breaker, or timeout strategies for external dependencies if relevant.
- Keep exception handling consistent with API error semantics.
- Suggest service-level logging and metrics for key business operations.

Provide:
- configuration changes,
- code changes for observability,
- resilience policies,
- tests or checks for runtime behavior,
- a short production operations note.
