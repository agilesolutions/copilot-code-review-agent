---
name: spring-boot-unit-test-review
description: Review Java and Kotlin unit tests in Spring Boot applications for correctness, isolation, readability, maintainability, test design, mocking practices, assertions, edge cases, and meaningful behavioral coverage. Use when reviewing unit tests rather than integration or end-to-end tests.
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

# Spring Boot Unit Test Review Skill

## Purpose

Review unit tests in Java Spring Boot applications.

The objective is to determine whether the tests provide meaningful confidence in application behavior while remaining:

* isolated
* deterministic
* readable
* maintainable
* fast
* behavior-oriented
* resistant to unnecessary implementation changes

This skill is primarily intended for:

* JUnit 5
* Mockito
* AssertJ
* Spring Boot applications
* Java

Do not modify the application or tests.

This is a read-only review.

---

# Review Philosophy

A good unit test should:

1. Test observable behavior.
2. Have one clear reason to fail.
3. Be deterministic.
4. Be isolated from external infrastructure.
5. Clearly communicate the scenario being tested.
6. Contain meaningful assertions.
7. Avoid unnecessary mocking.
8. Avoid testing implementation details.
9. Be easy to understand when it fails.
10. Provide useful regression protection.

Do not judge test quality by code coverage alone.

High coverage does not necessarily mean high-quality tests.

---

# Review Scope

Inspect:

* `src/test/java`
* unit test classes
* test fixtures
* test utilities
* test data builders
* Mockito configuration
* JUnit configuration
* AssertJ usage

Identify which tests are actually unit tests.

Distinguish them from:

* Spring Boot integration tests
* `@SpringBootTest`
* `@WebMvcTest`
* `@DataJpaTest`
* Testcontainers tests
* repository integration tests
* contract tests
* end-to-end tests

Do not apply unit-test criteria to integration tests.

---

# 1. Test Classification

Determine whether each reviewed test is genuinely a unit test.

A unit test should normally:

* execute without starting the Spring application context
* avoid real databases
* avoid external services
* avoid real Kafka/RabbitMQ infrastructure
* avoid network communication
* execute quickly
* isolate the class under test

Examples of typical unit-test setup:

```java
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;
}
```

A test using:

```java
@SpringBootTest
```

should normally be classified as an integration test rather than a unit test.

Do not flag this as a defect merely because it is not a unit test.

Instead, determine whether the test is correctly classified and whether the test strategy is appropriate.

---

# 2. Test Naming

Evaluate whether test names clearly communicate:

* scenario
* condition
* expected behavior

Prefer names such as:

```java
shouldReturnCustomerWhenCustomerExists()
```

or:

```java
findCustomer_returnsCustomer_whenCustomerExists()
```

Avoid vague names:

```java
testCustomer()
testService()
shouldWork()
test1()
```

For parameterized tests, ensure the parameters and display names make failures understandable.

---

# 3. Arrange / Act / Assert

Assess whether tests have a clear structure:

```text
Arrange
Act
Assert
```

Example:

```java
// Arrange
var customer = new Customer("123");

when(repository.findById("123"))
    .thenReturn(Optional.of(customer));

// Act
var result = service.findCustomer("123");

// Assert
assertThat(result).isEqualTo(customer);
```

Flag tests where:

* setup dominates the test
* the actual action is difficult to locate
* assertions are buried inside complex logic
* unrelated behavior is tested in the same method

---

# 4. Test One Behavior

Determine whether each test focuses on one meaningful behavior.

Avoid tests such as:

```java
@Test
void testCustomerService() {
    // create customer
    // find customer
    // update customer
    // delete customer
    // verify notifications
}
```

Prefer separate tests for separate behaviors.

However, do not interpret "one behavior" as "one assertion".

Multiple assertions are acceptable when they collectively verify one behavior.

---

# 5. Assertions

Assess whether assertions verify meaningful outcomes.

Good:

```java
assertThat(result)
    .isNotNull()
    .extracting(Customer::getName)
    .isEqualTo("Robert");
```

Weak:

```java
assertThat(result).isNotNull();
```

when the important behavior is the returned content.

Look for:

* missing assertions
* overly weak assertions
* assertions against irrelevant implementation details
* excessive assertion duplication

Prefer AssertJ where it improves readability.

---

# 6. Exception Testing

Review tests for expected failure scenarios.

Prefer:

```java
var exception = assertThatThrownBy(
    () -> service.findCustomer("unknown")
)
    .isInstanceOf(CustomerNotFoundException.class)
    .hasMessageContaining("unknown");
```

Assess whether the test verifies:

* exception type
* important error information
* relevant side effects

Avoid asserting exact exception messages unless the message is part of the contract.

---

# 7. Mockito Usage

Review Mockito usage carefully.

Look for:

* unnecessary mocks
* excessive mocking
* mocks of simple value objects
* mocks of the class under test
* mocks that duplicate implementation details
* deep stubbing
* excessive `verify()` calls

Avoid:

```java
@Mock
private Customer customer;
```

when a real `Customer` object is simple and appropriate.

Prefer real domain objects and mock only external collaborators.

---

# 8. Mock Behavior vs Implementation

Distinguish between useful interaction verification and implementation-detail verification.

Useful:

```java
verify(repository).save(customer);
```

when saving is an important side effect.

Potentially brittle:

```java
verify(repository).findById("123");
verify(mapper).toDto(customer);
verify(logger).info(anyString());
verify(transactionManager).begin();
```

Do not require interaction verification simply because Mockito makes it possible.

Prefer asserting observable behavior.

---

# 9. Mockito Strictness

Look for:

* unused stubbings
* unnecessary `lenient()`
* broad argument matchers
* excessive `any()`
* overly generic stubbing

Be suspicious of:

```java
lenient()
    .when(repository.findById(any()))
    .thenReturn(...);
```

when strict Mockito behavior would expose unnecessary test setup.

Avoid unnecessary:

```java
any()
anyString()
any(Class.class)
```

when exact values communicate the scenario better.

---

# 10. Mocking Final Classes and Static Methods

Identify heavy use of:

* static mocking
* constructor mocking
* static utility mocking
* PowerMock-style approaches

These may indicate design problems.

For example:

```java
mockStatic(CustomerFactory.class);
```

should trigger investigation.

Do not automatically classify it as a defect.

Determine whether the production design unnecessarily makes unit testing difficult.

---

# 11. Test Data

Assess how test data is created.

Prefer simple, readable data:

```java
var customer = new Customer(
    "123",
    "Robert"
);
```

Test builders are useful when object construction is genuinely complex.

Avoid enormous builders for simple objects.

Flag test fixtures that hide important scenario information.

Bad:

```java
var customer = customerFixture.createDefault();
```

when the important property of the scenario is hidden inside the fixture.

Better:

```java
var customer = customerFixture.create()
    .withStatus(CustomerStatus.ACTIVE)
    .withCreditLimit(BigDecimal.valueOf(5000))
    .build();
```

---

# 12. Test Data Builders

If builders exist, assess:

* readability
* sensible defaults
* ability to override relevant fields
* accidental coupling
* excessive abstraction

A test builder should make tests easier to understand, not hide the scenario.

---

# 13. Parameterized Tests

Identify repeated tests that differ only by input/output.

Consider:

```java
@ParameterizedTest
@CsvSource({
    "ACTIVE, true",
    "SUSPENDED, false",
    "CLOSED, false"
})
void shouldDetermineWhetherCustomerIsActive(
    CustomerStatus status,
    boolean expected
) {
    ...
}
```

Recommend parameterization when it improves maintainability.

Do not convert every collection of similar tests into parameterized tests automatically.

---

# 14. Boundary Conditions

Assess whether important boundary conditions are tested.

Depending on the application, inspect:

* null values
* empty collections
* empty strings
* minimum values
* maximum values
* zero
* negative values
* duplicate values
* missing records
* invalid state transitions
* authorization failures
* timeout/error conditions

Only recommend cases that are relevant to the actual production behavior.

---

# 15. Business Rules

Focus particularly on tests around business logic.

For each meaningful business rule ask:

```text
What happens when the rule is satisfied?
What happens when the rule is violated?
What happens at the boundary?
```

Business-rule tests should preferably be independent of:

* Spring context
* database
* HTTP
* messaging infrastructure

when practical.

---

# 16. Test Independence

Check whether tests depend on:

* execution order
* shared mutable state
* static state
* environment variables
* system time
* random values
* external services
* files outside the test environment

Flag tests that can pass or fail depending on execution order.

---

# 17. Time and Randomness

Look for direct use of:

```java
LocalDateTime.now()
Instant.now()
UUID.randomUUID()
Math.random()
```

when deterministic behavior is important.

Prefer injecting abstractions such as:

```java
Clock
```

when time is part of business behavior.

Tests should control time explicitly:

```java
var clock = Clock.fixed(
    Instant.parse("2026-01-01T00:00:00Z"),
    ZoneOffset.UTC
);
```

Do not recommend injecting a clock merely because a timestamp exists in incidental implementation code.

---

# 18. Flaky Tests

Look for indicators of flaky tests:

* `Thread.sleep`
* polling without deterministic synchronization
* current time
* random values
* shared static state
* dependence on test order
* environment-specific assumptions
* asynchronous behavior without proper synchronization

Flag these as high priority when they can undermine CI reliability.

---

# 19. Spring Boot Unit Tests

When testing Spring components, determine whether Spring is actually required.

For example, this:

```java
@SpringBootTest
class CustomerServiceTest {
    ...
}
```

may be unnecessarily expensive if `CustomerService` can be instantiated directly.

Potentially preferable:

```java
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    ...
}
```

However, do not recommend removing Spring when the behavior genuinely depends on Spring infrastructure.

---

# 20. Test Slices

Recognize the distinction between:

* unit tests
* `@WebMvcTest`
* `@DataJpaTest`
* `@JsonTest`
* `@SpringBootTest`

Do not mix these categories in the unit-test quality score.

Assess whether each test type is appropriate for what it verifies.

---

# 21. Code Coverage

If coverage information is available, inspect it.

Use coverage as supporting evidence only.

Do not conclude:

```text
95% coverage = excellent tests
```

Instead ask:

* Are important business rules covered?
* Are failure paths covered?
* Are boundary conditions covered?
* Are tests actually asserting behavior?
* Are critical classes covered?
* Are there large areas of untested business logic?

High coverage with weak assertions should still receive a poor assessment.

---

# 22. Mutation Testing

If mutation testing is present, inspect it.

Tools may include:

* PIT / PIT Mutation Testing

Mutation testing can provide stronger evidence that tests actually detect behavioral changes.

If mutation testing is absent, recommend it only where the application contains sufficiently important business logic to justify the additional build cost.

---

# 23. Test Maintainability

Assess whether tests are likely to survive legitimate production refactoring.

Good tests should normally survive changes to:

* private methods
* internal helper classes
* implementation algorithms
* mapping implementation
* repository implementation

Tests should fail when:

* externally observable behavior changes
* business rules change
* API contracts change
* important side effects disappear

This is one of the most important indicators of test quality.

---

# 24. Common Anti-Patterns

Look for:

### Test Everything

Tests every private method or implementation detail.

### Mock Everything

Every object is mocked, including simple domain objects.

### Verify Everything

Tests contain large numbers of `verify()` calls without asserting outcomes.

### Assert Nothing

The test executes code but provides little meaningful verification.

### Giant Test

One test covers many unrelated behaviors.

### Fixture Obfuscation

Test data is hidden behind complex fixtures.

### Spring Everywhere

Every unit test starts a Spring context.

### Sleep-Based Synchronization

Tests use `Thread.sleep()` to wait for behavior.

### Shared State

Tests depend on mutable static or instance state.

### Happy Path Only

Only successful scenarios are covered.

### Coverage Theater

Tests exist mainly to increase coverage metrics rather than provide confidence.

---

# Finding Severity

Use:

### Critical

Tests provide false confidence around critical business/security behavior or are systematically invalid.

### High

Significant gaps, flaky tests, incorrect isolation, or tests that fail to protect important behavior.

### Medium

Meaningful test-quality or maintainability problems.

### Low

Minor readability or consistency issues.

### Informational

Improvement opportunities or observations.

Do not inflate severity.

---

# Finding Format

For every significant finding use:

```text
### [SEVERITY] Short finding title

**Location**
`src/test/java/.../CustomerServiceTest.java:42`

**Observation**
Describe the concrete problem.

**Why it matters**
Explain what confidence is lost or what maintenance problem is created.

**Recommendation**
Provide a concrete improvement.

**Example**
Only provide code when it materially clarifies the recommendation.
```

Every significant finding should reference actual code or test structure.

Do not produce generic findings without evidence.

---

# Test Quality Assessment

Rate the reviewed unit-test suite:

* Excellent
* Good
* Acceptable
* Needs Improvement
* Poor

Assess:

| Dimension              | Assessment |
| ---------------------- | ---------- |
| Isolation              |            |
| Determinism            |            |
| Readability            |            |
| Assertions             |            |
| Mocking                |            |
| Business-rule coverage |            |
| Edge-case coverage     |            |
| Maintainability        |            |
| Failure-path coverage  |            |
| Test execution speed   |            |

---

# Final Review Output

Produce:

## 1. Overall Assessment

Provide a concise assessment of the unit-test suite.

## 2. Strengths

Identify practices that should be preserved.

## 3. Findings

Order findings:

1. Critical
2. High
3. Medium
4. Low
5. Informational

## 4. Coverage Gaps

Identify important behaviors that appear insufficiently tested.

## 5. Test Architecture

Explain whether the distinction between unit and integration tests is appropriate.

## 6. Recommended Improvements

Provide a prioritized roadmap:

### Immediate

Issues that undermine test reliability or correctness.

### Short Term

Meaningful improvements to coverage and test design.

### Medium Term

Maintainability and architectural improvements.

### Optional

Advanced practices such as mutation testing or architectural test tooling.

---

# Review Rules

## Read-only

Never modify tests or production code.

## Evidence-based

Reference actual tests and source locations.

## Behavior over implementation

Prefer tests that verify externally meaningful behavior.

## Do not over-mock

Mock external collaborators, not everything.

## Do not chase coverage percentages

Coverage is evidence, not the objective.

## Do not force a testing framework

Use the existing project stack unless there is a concrete reason to recommend change.

## Do not require Spring for unit tests

Use Spring only when Spring behavior itself is part of what needs testing.

## Do not confuse unit and integration testing

A `@SpringBootTest` is not automatically a bad test.

It is simply a different category of test.

## Keep recommendations proportional

A small service should not require an enterprise testing framework merely to satisfy theoretical best practices.

---

# Primary Objective

The final question the reviewer should answer is:

> "After reading these tests, how confident would a developer be that the application's important behavior is protected against regression?"

Optimize the review for **confidence, correctness, maintainability, and fast feedback**, rather than test-count or coverage metrics.
