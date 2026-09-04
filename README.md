# PR Sanity Reviewer
A mean-and-lean GitHub Copilot custom agent for performing a fast sanity review of everything committed as part of the current pull request.
The agent is the first line of defense before a Team Lead review. It deliberately does not replace specialist code reviewers, architecture review, security review, or human approval.
What it does
The `PR Sanity Reviewer`:
reviews the complete current feature branch / PR diff
compares the branch against the PR base using `<base>...HEAD`
focuses on high-confidence, high-value problems
performs a broad sanity check rather than a deep specialist review
optionally runs fast project-native build/test verification
checks obvious build, functional, Spring Boot, REST, security, testing, observability, and maintainability problems
produces one compact Team Lead handoff report
does not modify application/source code
The intended workflow is:
```text
Developer
   |
   v
PR Sanity Reviewer
   |
   +--> quick sanity checks
   |
   +--> compact /reviews/<feature-branch>.md
   |
   v
Specialist Review Agents
   |
   v
Team Lead Review
```
Repository layout
Place the agent under:
```text
.github/
└── agents/
    └── pr-sanity-reviewer.agent.md
```
Review reports are generated under:
```text
reviews/
└── <sanitized-feature-branch>.md
```
Example:
```text
reviews/
├── feature-add-payment-api.md
├── feature-JIRA-123-order-api.md
└── bugfix-customer-null-check.md
```
`/reviews/<feature-branch>.md` convention
The report belongs to the repository and is named after the branch that was reviewed.
The agent obtains the branch with:
```bash
git branch --show-current
```
It then sanitizes the name so it can safely be used as a Markdown filename.
Sanitization rules
The agent:
replaces `/` and `\` with `-`
replaces whitespace with `-`
replaces filesystem-unfriendly characters with `-`
collapses repeated `-`
removes unsafe leading/trailing filename characters
prevents path traversal
preserves the recognizable branch name
adds `.md`
Examples:
Git branch	Review report
`feature/add-payment-api`	`reviews/feature-add-payment-api.md`
`bugfix/customer-null-check`	`reviews/bugfix-customer-null-check.md`
`feature/JIRA-123/order-api`	`reviews/feature-JIRA-123-order-api.md`
This makes the report predictable and easy for a Team Lead to find.
Re-running the agent
The agent updates/replaces the same report when run again.
It does not append another report.
```text
feature/add-payment-api
        |
        v
reviews/feature-add-payment-api.md
```
A subsequent review of the same branch updates that same file.
Branch scope
The agent must review the complete PR change set, not merely the latest commit.
Conceptually:
```bash
git diff <base>...HEAD
```
The three-dot diff is important because it represents the changes introduced by the feature branch relative to the merge base.
Useful commands:
```bash
git diff --name-status <base>...HEAD
git diff --stat <base>...HEAD
git diff <base>...HEAD
git diff --check <base>...HEAD
```
Base branch
If the agent is operating in a PR-aware environment, it should use the actual PR base branch.
Otherwise it should determine the repository default/base branch from Git configuration.
It should not guess a base branch if repository configuration is ambiguous.
Source-code write policy
The PR Sanity Reviewer is read-only with respect to the software being reviewed.
It must never modify:
Java/Kotlin source
tests
Gradle/Maven configuration
application configuration
Kubernetes manifests
Helm charts
Terraform
documentation
CI/CD configuration
The only repository artifact it may create or update is:
```text
/reviews/<sanitized-feature-branch>.md
```
The agent should use the normal workspace editing capability provided by GitHub Copilot / IntelliJ.
It should not depend on custom `create_file` or `insert_edit_into_file` tools.
This separation is intentional:
```text
Application source
       ^
       | READ ONLY
       |
PR Sanity Reviewer
       |
       +----> WRITE ONLY
              |
              v
       /reviews/<branch>.md
```
What the sanity review checks
1. Build correctness
   The agent looks for obvious:
   compilation failures
   invalid imports
   incorrect method signatures
   broken dependency references
   malformed configuration
   incompatible API usage
   Maven/Gradle errors
2. Functional correctness
   It looks for high-confidence defects such as:
   null/empty handling errors
   incorrect conditions
   broken control flow
   off-by-one errors
   incorrect mappings
   accidental behavior changes
   unusable exception paths
   resource lifecycle problems
3. Spring Boot sanity
   For changed Spring code, it checks obvious problems involving:
   dependency injection
   bean configuration
   component scanning
   configuration properties
   transactions
   controllers
   services/repositories
   Spring Security
   startup behavior
   This is intentionally a sanity check, not a complete Spring architecture review.
4. REST/API sanity
   It checks:
   HTTP method/status-code mismatches
   request/response mapping problems
   obvious validation gaps
   path/parameter errors
   accidental internal-data exposure
   obvious breaking API changes
5. Security sanity
   Only clear/high-confidence issues should be reported.
   Examples:
   hard-coded secrets
   credentials in source
   obvious authorization bypasses
   missing authorization on clearly protected operations
   credentials/tokens in logs
   obvious injection vulnerabilities
   This is not intended to replace a dedicated security review.
6. Testing sanity
   The agent checks:
   whether changed behavior has meaningful tests
   whether existing tests are broken
   whether tests actually exercise the changed behavior
   obvious gaps around important new behavior
   brittle or misleading tests
   It does not demand tests for every trivial code change.
7. Logging and observability sanity
   The agent checks for:
   secrets/tokens/passwords in logs
   excessive logging
   swallowed exceptions
   insufficient error context
   obvious loss of trace/correlation context where the application depends on it
8. Maintainability sanity
   Only high-value issues should be reported, such as:
   duplicated logic introduced by the PR
   dead code introduced by the PR
   clearly misleading names
   unnecessarily complex implementation
   obvious responsibility violations
   Avoid subjective style debates.
   Severity model
   Severity	Meaning
   BLOCKER	Very likely to prevent build/deployment, cause serious runtime failure, create a critical security issue, or make the feature fundamentally unusable
   IMPORTANT	Significant issue that should normally be addressed before Team Lead approval
   MINOR	Useful non-blocking observation with meaningful value
   The reviewer should strongly prefer a few high-confidence findings over a large number of speculative findings.
   Result model
```text
BLOCKER exists
    -> FAIL

No BLOCKER
but IMPORTANT or MINOR exists
    -> PASS WITH FINDINGS

No findings
    -> PASS
```
A failed or unavailable test command does not automatically make the review `FAIL`. The report explains the verification status.
Compact Team Lead report
The generated report intentionally remains short.
Example:
```markdown
# PR Sanity Review

- **Branch:** `feature/add-payment-api`
- **Base:** `main`
- **Scope:** `8 changed files`
- **Result:** `PASS WITH FINDINGS`

## Blockers

- None

## Important findings

- **IMPORTANT** — `src/main/java/.../PaymentController.java:72` — endpoint returns `200` for a failed creation operation, causing clients to interpret the request as successful.

## Minor findings

- **MINOR** — `src/main/java/.../PaymentService.java:51` — duplicated validation logic introduced by the PR.

## Verification

- **Build/tests:** PASS — `./gradlew test`
- **Diff check:** PASS — `git diff --check`

## Team Lead handoff

The implementation is broadly sane. Team Lead should verify the payment creation error contract before approval.
```
The report is a handoff artifact, not a transcript of everything the agent inspected.
Relationship to specialist review agents
The PR Sanity Reviewer should not become an orchestrator for every possible review concern.
Its job is:
```text
fast + broad + high confidence
```
Specialist agents can separately perform:
```text
Spring Boot architecture
Security
JUnit/TestContainers
REST/API design
Observability
Kubernetes
Terraform
Database
Performance
```
This keeps the sanity agent small and fast.
Agent vs Skill
This sanity check is intentionally implemented as an agent, not merely a skill.
Use an agent when
You need:
a distinct role
a defined review workflow
autonomous inspection
a consistent output format
a persistent review artifact
explicit boundaries on what the reviewer may change
Use a skill when
You want reusable specialist knowledge or instructions that can be invoked by an agent.
Examples:
```text
junit-testing
testcontainers
spring-security
spring-observability
kubernetes
```
A useful architecture is:
```text
Agent
  |
  +-- general PR sanity workflow
  |
  +-- specialist skills when needed
```
Recommended workflow
Step 1 — Developer completes the feature
```text
feature/add-payment-api
```
Step 2 — Run PR Sanity Reviewer
The agent reviews:
```text
<base>...HEAD
```
and writes:
```text
reviews/feature-add-payment-api.md
```
Step 3 — Fix blockers/important findings
The developer addresses meaningful issues.
Step 4 — Re-run the sanity reviewer
The same report is updated:
```text
reviews/feature-add-payment-api.md
```
Step 5 — Run specialist reviewers
Only after the quick sanity check is clean enough to justify deeper analysis.
Step 6 — Team Lead reviews
The Team Lead uses:
```text
reviews/feature-add-payment-api.md
```
as the compact first-pass handoff, together with specialist review outputs and the actual PR.
Design principles
1. Mean
   Be willing to reject obvious defects.
2. Lean
   Do not perform a deep architectural investigation for every PR.
3. High signal
   Prefer confidence over volume.
4. Branch scoped
   Review what the PR changed:
```text
<base>...HEAD
```
not the whole repository.
5. Source read-only
   Never "fix" the code while reviewing it.
   The only write operation is the Team Lead handoff:
```text
/reviews/<sanitized-feature-branch>.md
```
Installation
Copy:
```text
pr-sanity-reviewer.agent.md
```
to:
```text
.github/agents/pr-sanity-reviewer.agent.md
```
The agent will create `reviews/` when necessary.
Expected repository result
After using the agent on:
```text
feature/add-payment-api
```
the repository should contain:
```text
.github/
└── agents/
    └── pr-sanity-reviewer.agent.md

reviews/
└── feature-add-payment-api.md
```
The application source remains untouched by the sanity reviewer.
Team Lead responsibility
The report is an aid to review, not an approval mechanism.
The Team Lead remains responsible for deciding whether the PR is ready to merge.
```text
Automated sanity
       |
       v
Specialist analysis
       |
       v
Human Team Lead judgement
       |
       v
Merge / request changes
```
The agent finds problems; the Team Lead owns the decision.