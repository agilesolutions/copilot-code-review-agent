# DUO Java Lead Developer — Rules of Engagement

## Purpose

This document defines my working principles, responsibilities, and rules of engagement as a **Java Lead Developer at DUO (Dienst Uitvoering Onderwijs)**.

The primary objective is to provide strong technical leadership while protecting time for the activities where a Lead Developer adds the most value: **architecture, technology direction, technical risk management, engineering standards, mentoring, and enabling development teams**.

A key principle is to avoid spending disproportionate amounts of senior engineering time on repetitive, mechanical review activities that can be automated or prepared by tooling.

---

## 1. Role and Responsibilities

As Java Lead Developer, my responsibility is not simply to review code or solve difficult implementation problems.

My role is to:

- Provide technical direction to development teams.
- Help establish and maintain sound Java and Spring Boot engineering practices.
- Guide architectural and design decisions.
- Evaluate and introduce new technologies where they provide clear value.
- Identify and manage technical risks.
- Encourage maintainable, testable, observable, secure software.
- Coach developers and share technical knowledge.
- Support teams in making informed engineering decisions.
- Maintain a healthy balance between delivery, quality, technical debt, and long-term maintainability.
- Enable teams to become increasingly self-sufficient rather than becoming a permanent technical bottleneck.

---

## 2. Protecting Lead Developer Capacity

Code review is important, but not every review activity requires Lead Developer attention.

My time should primarily be spent on questions such as:

> **Is this the right solution?**

rather than repeatedly answering:

> **Is this implementation obviously broken?**

Repetitive and predictable checks should increasingly be delegated to:

- Automated tests.
- Static analysis.
- Build tooling.
- Linters and quality gates.
- GitHub Copilot agents and skills.
- Specialist review agents where appropriate.

This creates more capacity for architectural thinking, technology strategy, mentoring, and resolving complex technical risks.

---

## 3. Three-Level Review Model

### Level 1 — PR Sanity Review

A fast, broad, high-signal sanity check identifies obvious problems before a human Lead Developer spends significant time on a PR.

Typical checks include:

- Build and compilation correctness.
- Obvious functional defects.
- Basic test sanity.
- Obvious Spring Boot configuration mistakes.
- REST/API problems.
- Obvious security mistakes.
- Logging and observability gaps.
- Obvious maintainability problems.

The sanity review should be fast, high-confidence, focused on the complete PR change set, and free of unnecessary style nitpicks or speculative business requirements.

### Level 2 — Specialist Technical Review

Specialist reviews provide deeper analysis where required.

Potential specialist areas include:

- Spring Boot / Spring Framework.
- Java and Kotlin.
- JUnit 5 and TestContainers.
- REST/API design.
- Security and OAuth2/OIDC.
- Persistence and database design.
- Messaging and event-driven architecture.
- Observability and OpenTelemetry.
- Kubernetes and cloud-native deployment.
- CI/CD and DevOps.
- Performance and resilience.

Specialist agents provide **evidence and recommendations**, not final technical ownership.

### Level 3 — Lead Developer / Architecture Review

The Lead Developer remains responsible for decisions requiring engineering judgment, including:

- Architecture.
- Design quality.
- Technology selection.
- Architectural trade-offs.
- Integration patterns.
- API evolution.
- Resilience and operational characteristics.
- Security architecture.
- Long-term maintainability.
- Technical debt implications.
- Consistency with the broader DUO architecture and engineering landscape.
- Introduction of new technologies.
- Significant deviations from established engineering practices.

The central question at this level is:

> **Is this the right solution for DUO, the product, the team, and the expected future evolution of the system?**

Automation should prepare information for this decision — it should not make the decision on my behalf.

---

## 4. Review Automation Principles

GitHub Copilot agents and skills are **engineering assistants**, not autonomous technical authorities.

### Agents

Agents are appropriate when a task requires:

- A defined role.
- A repeatable workflow.
- Analysis across multiple files.
- Coordination of specialist activities.
- A structured output.

Examples include:

- PR sanity reviewer.
- Review orchestrator.
- Spring Boot reviewer.
- Security reviewer.
- Architecture reviewer.

### Skills

Skills are appropriate for reusable technical knowledge and procedures.

Examples include:

- JUnit/TestContainers testing guidance.
- Spring Boot development conventions.
- REST API review guidance.
- Observability practices.
- Kubernetes practices.
- Architecture documentation conventions.

The distinction is intentional:

> **Skills provide reusable knowledge; agents perform defined responsibilities using that knowledge.**

---

## 5. PR Review Workflow

The preferred workflow is:

```text
Developer
   |
   v
Pull Request
   |
   v
Automated tests / quality gates
   |
   v
PR Sanity Reviewer
   |
   v
/reviews/<feature-branch>.md
   |
   v
Specialist Reviews
   |
   v
Lead Developer / Architecture Review
   |
   v
Final Team Decision
```

The objective is not to automate the entire review process.

The objective is to ensure that when a human Lead Developer becomes involved, mechanical work has already been performed and important technical questions are clearly visible.

---

## 6. Review Report Convention

Automated PR reviews should produce a concise Markdown report under:

```text
/reviews/<sanitized-feature-branch>.md
```

For example:

```text
feature/add-payment-api
```

becomes:

```text
/reviews/feature-add-payment-api.md
```

The report should normally contain:

- Branch.
- Base branch.
- Review scope.
- Overall result.
- Blockers.
- Important findings.
- Minor findings.
- Verification performed.
- Team Lead handoff.

The report should be replaced or updated when the review is rerun rather than endlessly appended to.

This makes review output predictable, versionable, and easy for a team lead to consume.

---

## 7. Severity and Escalation

### BLOCKER

A finding that should normally prevent approval until addressed.

Examples:

- Clear security vulnerability.
- Code that cannot compile.
- Serious correctness defect.
- Data-loss risk.
- Clearly broken critical behavior.

### IMPORTANT

A meaningful technical problem that should normally be discussed or resolved before merging.

Examples:

- Significant missing test coverage for changed behavior.
- Incorrect transaction handling.
- Serious API design issue.
- Important resilience or observability gap.

### MINOR

A lower-impact improvement or maintainability concern.

Minor findings should not overwhelm the reviewer.

The objective is **signal over volume**.

---

## 8. What I Expect From Development Teams

I want teams to own their software.

Teams should:

- Understand the code they deliver.
- Write and maintain appropriate tests.
- Keep PRs reviewable.
- Explain significant design decisions.
- Address technical debt consciously.
- Use established engineering standards.
- Ask for architectural guidance when decisions have broader impact.
- Treat automated review findings as feedback rather than unquestionable truth.
- Escalate risks early.

A Lead Developer should help teams make better decisions — not become the person who must manually validate every line of code.

---

## 9. Introducing New Technologies

Introducing new technology is one of the responsibilities where Lead Developer attention provides significant value.

Before introducing a technology, consider:

- What problem does it solve?
- Why is the existing platform insufficient?
- What are the operational consequences?
- What skills are required?
- What is the maturity of the technology?
- How does it affect security?
- How does it affect maintainability?
- How does it affect deployment and operations?
- What is the exit strategy?
- Does it fit the existing architecture?
- Can the team support it sustainably?

Where appropriate, the evaluation should result in:

- A proof of concept.
- A technology assessment.
- An Architecture Decision Record (ADR).
- Clear adoption criteria.
- Documentation and team enablement.

The goal is not to introduce technology because it is new.

The goal is to introduce technology when it creates **measurable engineering value**.

---

## 10. Architecture Over Mechanical Review

One of the central rules of engagement is:

> **Senior engineering time should be spent where senior engineering judgment is required.**

Therefore, I should deliberately reduce time spent on:

- Repetitive code inspection.
- Obvious syntax/build issues.
- Mechanical test checks.
- Repeated explanations of standard practices.
- Finding simple defects that tooling can reliably identify.

And increase time spent on:

- System architecture.
- Domain and integration boundaries.
- Technology strategy.
- Technical risk.
- Resilience.
- Security architecture.
- Operational architecture.
- Long-term maintainability.
- Technical debt strategy.
- Team mentoring.
- Knowledge sharing.

---

## 11. Human Accountability

Automation does not transfer responsibility away from the development team or Lead Developer.

Copilot agents may:

- Detect potential problems.
- Explain findings.
- Suggest improvements.
- Organize review information.
- Prepare reports.

They should not independently:

- Approve architectural decisions.
- Decide business requirements.
- Override team ownership.
- Introduce technology without human agreement.
- Hide uncertain findings.
- Modify production code as part of a review unless explicitly authorized by a separate workflow.

The final engineering decision remains human.

---

## 12. Desired Outcome

The intended outcome is a development organization where:

1. Developers receive fast feedback.
2. Mechanical review work is increasingly automated.
3. Specialist technical concerns are surfaced early.
4. PRs arrive at human reviewers in a more digestible state.
5. Lead Developer time is protected for high-value decisions.
6. Development teams become more autonomous.
7. Architectural consistency improves.
8. New technologies are introduced deliberately.
9. Technical risks become visible earlier.
10. Engineering quality improves without unnecessary review bureaucracy.

---

## 13. Guiding Principle

The overall principle can be summarized as:

> **Automate what is repeatable. Delegate what is specialist. Personally own what requires architectural judgment.**

This is how I intend to use GitHub Copilot, agents, skills, automated quality gates, and structured review processes to support my role as Java Lead Developer at DUO.

The technology should make me **more effective as a technical leader**, not simply allow me to review more code.

---

## 14. Initial Copilot Engineering Framework

The supporting repository can evolve toward:

```text
.github/
├── agents/
│   ├── pr-sanity-reviewer.agent.md
│   ├── review-orchestrator.agent.md
│   ├── spring-boot-reviewer.agent.md
│   ├── junit-reviewer.agent.md
│   ├── security-reviewer.agent.md
│   ├── observability-reviewer.agent.md
│   └── architecture-reviewer.agent.md
│
├── skills/
│   ├── junit/
│   ├── spring-boot/
│   ├── security/
│   ├── observability/
│   ├── kubernetes/
│   └── architecture/
│
└── prompts/
    ├── architecture-review.md
    ├── technology-assessment.md
    └── design-review.md

/reviews/
└── <feature-branch>.md
```

This framework should evolve incrementally based on real team needs.

The objective is not to build an elaborate AI platform for its own sake.

The objective is to create a **practical engineering enablement system that gives the Lead Developer more time for architecture, technology leadership, risk management, and people**.
