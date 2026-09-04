## Mandatory Unit Test Coverage Verification

When the changeset contains new or modified production code:
1. Identify every newly added or substantially modified production class under `src/main/java`.
2. For each production class, determine whether it contains executable logic or behavior that should be unit tested.
3. Search the complete test source tree under:
    - `src/test/java`
4. Find tests corresponding to each production class.
5. Do not assume a test exists merely because a test package exists.
6. Verify that the test actually exercises the new or modified behavior.
7. Report a finding when a new production class containing meaningful
   business/application logic has no corresponding unit test.
8. Report a finding when an existing test class exists but does not test
   the newly introduced behavior.
9. Report a finding when important new branches, error handling, validation,
   or business rules have no meaningful test coverage.
10. Do not require unit tests for classes that are purely declarative,
    configuration-only, generated code, DTOs/records without behavior,
    or trivial framework wiring unless project standards explicitly require it.
11. Prefer behavioral coverage over a simple test-class existence check.