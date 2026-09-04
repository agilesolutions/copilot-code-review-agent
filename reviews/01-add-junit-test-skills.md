## Code Review Summary

**Overall:** CHANGES REQUESTED

### Files added in this branch

- A  src/main/java/com/example/demo/customer/service/CustomerValidationService.java
- A  src/main/java/com/example/demo/customer/service/EmployeeService.java
- A  .github/skills/unit-test-review/SKILL.md
- A  reviews/01-add-junit-test-skills.md (review artifact)

### Other changed files (context)

- M  src/main/java/com/example/demo/customer/entity/Employee.java
- R  src/main/java/com/example/demo/customer/controller/CustomerController.java (renamed)
- M  .github/agents/code-reviewer.agent.md (test-detection rules added)

### Findings

| Severity | Location | Finding |
|---|---|---|
| HIGH | `src/main/java/com/example/demo/customer/service/EmployeeService.java` | DTO ↔ entity mapping mismatch and brittle BeanUtils usage; returns null for missing entity |
| MEDIUM | `src/main/java/com/example/demo/customer/service/CustomerValidationService.java` | Redundant/unused validation service; uses IllegalArgumentException (not mapped to 400) |
| MEDIUM | `src/main/java/com/example/demo/customer/entity/Employee.java` | Lombok @Data on JPA entity; createdAt not initialized (@PrePersist missing) |
| LOW | `src/main/java/com/example/demo/customer/controller/CustomerController.java` | Rename OK, but ensure no stale references; run full build

### Details

#### [HIGH] DTO ↔ Entity mapping & mapping strategy

**Location:** `src/main/java/com/example/demo/customer/service/EmployeeService.java`

**Issue:**
The service maps between dto.Employee (record: id, name, email) and the entity (firstName, lastName, email) using BeanUtils and manual constructors. BeanUtils.copyProperties(updatedEmployee, existingEmployee, "id") will not map "name" to firstName/lastName, so updates and create may drop or mis-handle name parts. toDto constructs the DTO with firstName only, dropping lastName.

**Why it matters:**
Leads to data loss, inconsistent API responses, and hidden bugs. BeanUtils is brittle when DTO and entity property names differ.

**Recommendation:**
Implement explicit mapping: either split/join firstName/lastName intentionally or change DTO to carry firstName/lastName. Use a mapper (MapStruct) or explicit setters/getters rather than BeanUtils. Also consider returning Optional or throwing a NotFound exception instead of returning null when an entity is missing.

#### [MEDIUM] Validation service is unused and throws IllegalArgumentException

**Location:** `src/main/java/com/example/demo/customer/service/CustomerValidationService.java`

**Issue:**
The new service validates a Customer entity and throws IllegalArgumentException on invalid input. It is not referenced elsewhere in the committed codebase.

**Why it matters:**
Redundant validation duplicates existing Jakarta Validation on DTOs (CustomerRequest uses @NotBlank/@Email). Throwing IllegalArgumentException will result in 500 responses unless the global handler maps it to 400.

**Recommendation:**
Remove the service if unused, or modify it to validate DTOs using Jakarta Validation and/or throw a custom BadRequestException handled by RestExceptionHandler. If the service must exist, add unit tests and wire it into the create/update flow explicitly.

#### [MEDIUM] Lombok @Data on JPA entity; createdAt handling

**Location:** `src/main/java/com/example/demo/customer/entity/Employee.java`

**Issue:**
@Entity annotated class uses Lombok @Data which generates equals/hashCode and toString over all fields. createdAt is declared but not set on persist (no @PrePersist or initialization), despite updatable=false.

**Why it matters:**
Equals/hashCode risks breaking identity semantics for entities and toString may trigger lazy loading. Null createdAt can violate DB constraints or confuse API consumers.

**Recommendation:**
Replace @Data with @Getter/@Setter (or explicit methods), implement equals/hashCode focusing on a stable identifier, and set createdAt on persist (via constructor or @PrePersist method).

#### [LOW] Controller rename — sanity check

**Location:** `src/main/java/com/example/demo/customer/controller/CustomerController.java`

**Issue:**
Controller file was renamed from customer.logic. Source looks consistent.

**Recommendation:**
Run full build and integration tests to ensure no wiring/package-scan issues. No further action unless tests fail.

### Positive Observations

- New agent test-detection rule was added to help prevent missing unit tests in future changes.
- Code uses constructor injection and Spring idioms consistently.

## Conclusion

The branch adds employee-related service code and a validation helper but introduces risky mapping and validation choices. Fix explicit DTO↔entity mapping, remove or adapt the validation service to use Jakarta Validation or mapped exceptions, and address entity Lombok/createdAt concerns. After fixes, add unit tests (Agent now checks for missing tests) and run the test suite.
