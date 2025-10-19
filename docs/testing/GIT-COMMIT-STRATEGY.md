# Git Commit Strategy for Phase 2 Coverage Improvement

**Document Version:** 1.0
**Status:** ACTIVE
**Created:** 2025-10-18
**Last Updated:** 2025-10-18

## Purpose

This document defines the Git commit strategy for Phase 2 coverage improvement work (75% → 80% overall, 90-95% for critical modules). The strategy ensures:

1. **DO-278 Compliance:** Traceability from requirements → code → tests → commits
2. **Clear History:** Logical, reviewable commits that tell the story of test development
3. **Atomic Changes:** Each commit is self-contained and buildable
4. **Verifiable Progress:** Coverage improvements can be tracked per commit

## Core Principles

### 1. Separate Infrastructure from Implementation

**Infrastructure commits** set up the scaffolding:
- Test class skeletons
- Test utilities and factories
- Test data files
- Documentation (test plans, coverage maps)

**Implementation commits** add actual test logic:
- Test method implementations
- Assertions and verifications
- Mock setups

**Rationale:** Separates "what we're going to test" from "how we test it". Infrastructure can be reviewed for completeness; implementation for correctness.

### 2. One Logical Change Per Commit

**Good examples:**
- All enum validation tests (Phase 1)
- All data object tests (Phase 2)
- All validation tests for a single category (e.g., classification level validation)

**Bad examples:**
- Half of enum tests + some data object tests
- Tests for multiple unrelated modules
- Test implementation + source code changes (unless fixing a bug found during testing)

### 3. Atomic and Buildable

Every commit must:
- Compile successfully (`mvn compile test-compile`)
- Not break existing tests (`mvn test`)
- Pass static analysis (`mvn fmt:format`)

**Exception:** Infrastructure commits with @Ignore annotations on skeleton tests are acceptable.

### 4. Meaningful Commit Messages

Follow the Conventional Commits format with DO-278 traceability:

```
<type>(<scope>): <short summary>

<detailed description of changes>

<traceability information>

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

## Commit Types and Scopes

### Types

| Type | Usage | Example |
|------|-------|---------|
| `test` | Adding or modifying tests | `test(banner-marking): add enum validation tests` |
| `docs` | Documentation changes | `docs(testing): add coverage heat map` |
| `build` | Build/POM changes for coverage | `build(banner-marking): increase coverage threshold to 95%` |
| `refactor` | Test refactoring | `refactor(test): extract common banner test utilities` |
| `fix` | Fixing broken tests | `fix(test): correct assertion in ClassificationLevelTest` |

### Scopes

Use module or component name:
- `banner-marking` - Security banner module
- `nitf` - NITF imaging module
- `klv` - KLV metadata library
- `coverage` - Overall coverage improvements
- `testing` - Test infrastructure/framework

## Commit Message Templates

### Template 1: Test Infrastructure

```
test(<module>): add test infrastructure for <priority> <category> module

Created comprehensive test infrastructure for <module> to support
Phase 2 coverage improvement (75% → <target>% target).

Infrastructure:
- <N> skeleton test classes (~<M> test method stubs)
- <N> test utility classes (<list names>)
- <N> test data files (~<M> test cases from <source>)
- TEST-PLAN.md with <N>-phase implementation strategy

Module: <path/to/module>
Current coverage: <current>%
Target coverage: <target>%
Priority: <P0-P3> <CRITICAL/HIGH/MEDIUM/LOW> (<reason>)

Related: Phase 2 Week <N> objectives
Requirement: <SYS-XXX-NNN> (<requirement description>)

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

**Example:**
```
test(banner-marking): add test infrastructure for P0 security module

Created comprehensive test infrastructure for banner-marking module
to support Phase 2 coverage improvement (75% → 95% target).

Infrastructure:
- 13 skeleton test classes (~335 test method stubs)
- 2 test utility classes (BannerTestUtils, MetacardTestFactory)
- 4 test data files (~150 test cases from DoD 5200.01)
- TEST-PLAN.md with 6-phase implementation strategy

Module: catalog/security/banner-marking
Current coverage: 0%
Target coverage: 95%
Priority: P0 CRITICAL (security banner validation)

Related: Phase 2 Week 5-6 objectives
Requirement: SYS-SEC-005 (Security classification handling)

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### Template 2: Test Implementation (Phase)

```
test(<module>): implement <phase-name> (<N> tests, +<X>% coverage)

Implemented <phase-name> for <module> as part of Phase 2
coverage improvement strategy.

Tests implemented:
- <TestClass1>: <N> tests (<brief description>)
- <TestClass2>: <N> tests (<brief description>)
- <TestClass3>: <N> tests (<brief description>)

Coverage impact:
- Before: <X>% instruction, <Y>% branch, <Z>% complexity
- After: <X>% instruction, <Y>% branch, <Z>% complexity
- Improvement: +<N>% instruction, +<N>% branch, +<N>% complexity

Test categories:
- Enum validation: <N> tests
- Null/empty handling: <N> tests
- Boundary conditions: <N> tests
- Error cases: <N> tests

Module: <path/to/module>
Phase: <phase-number>/<total-phases> (<phase-name>)

Related: Phase 2 Week <N> objectives
Requirement: <SYS-XXX-NNN>, <SWR-XXX-NNN>
Verifies: <list of source methods tested>

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

**Example:**
```
test(banner-marking): implement Phase 1 enum tests (87 tests, +18% coverage)

Implemented Phase 1 (Enum Validation) for banner-marking module as part
of Phase 2 coverage improvement strategy.

Tests implemented:
- ClassificationLevelTest: 15 tests (value validation, ordering, parsing)
- SciControlTest: 18 tests (all SCI compartments, validation rules)
- SapControlTest: 12 tests (SAP programs, validation)
- AeaTypeTest: 8 tests (AEA categories)
- DissemControlTest: 22 tests (dissemination controls, country codes)
- OtherDissemControlTest: 12 tests (other dissem controls)

Coverage impact:
- Before: 0% instruction, 0% branch, 0% complexity
- After: 18% instruction, 15% branch, 16% complexity
- Improvement: +18% instruction, +15% branch, +16% complexity

Test categories:
- Enum validation: 52 tests
- Null/empty handling: 18 tests
- Boundary conditions: 12 tests
- Error cases: 5 tests

Module: catalog/security/banner-marking
Phase: 1/6 (Enum Validation)

Related: Phase 2 Week 5 objectives
Requirement: SYS-SEC-005, SWR-SEC-001, SWR-SEC-002
Verifies: ClassificationLevel.java, SciControl.java, SapControl.java,
          AeaType.java, DissemControl.java, OtherDissemControl.java

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### Template 3: Test Utilities

```
test(<module>): add test utilities for <purpose>

Created test utilities to support <module> test implementation.

Utilities added:
- <UtilityClass1>: <description>
  - Methods: <list key methods>
  - Purpose: <why needed>

- <UtilityClass2>: <description>
  - Methods: <list key methods>
  - Purpose: <why needed>

Usage:
- Shared across <N> test classes
- Reduces test code duplication by ~<X>%
- Provides consistent test data generation

Module: <path/to/module>

Related: Phase 2 Week <N> objectives

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

**Example:**
```
test(banner-marking): add test utilities for security marking validation

Created test utilities to support banner-marking test implementation.

Utilities added:
- BannerTestUtils: Security banner test data generation
  - Methods: createValidBanner(), createInvalidBanner(),
             bannerWithClassification(), bannerWithControls()
  - Purpose: Consistent test data across validation tests

- MetacardTestFactory: Mock metacard creation
  - Methods: metacardWithSecurity(), metacardWithAttributes(),
             emptyMetacard()
  - Purpose: Simplify metacard setup in tests

Usage:
- Shared across 13 test classes
- Reduces test code duplication by ~40%
- Provides consistent test data generation

Module: catalog/security/banner-marking

Related: Phase 2 Week 5 objectives

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### Template 4: Documentation

```
docs(<area>): <action> <document-name>

<Description of what documentation was added/changed and why>

Contents:
- <Section 1>: <description>
- <Section 2>: <description>
- <Section 3>: <description>

Purpose:
<Why this documentation is needed>

Audience:
<Who should read this>

Related: Phase 2 Week <N> objectives
Requirement: <REQ-ID if applicable>

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

**Example:**
```
docs(testing): add coverage heat map for Phase 2 planning

Added comprehensive coverage heat map identifying modules below
75% baseline and prioritizing coverage improvement work.

Contents:
- Current coverage by module (instruction, branch, complexity)
- Priority classification (P0-P3)
- Target coverage per module
- Risk assessment and justification

Purpose:
Guide Phase 2 test development priorities and track progress
toward 80% overall coverage goal.

Audience:
Development team, QA, project management

Related: Phase 2 Week 5 objectives
Requirement: SYS-COM-002 (Test coverage requirements)

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### Template 5: Coverage Threshold Update

```
build(<module>): increase JaCoCo coverage threshold to <X>%

Updated JaCoCo coverage enforcement for <module> from <old>% to <new>%
based on Phase 2 coverage improvement work.

Changes:
- INSTRUCTION: <old>% → <new>%
- BRANCH: <old>% → <new>%
- COMPLEXITY: <old>% → <new>%

Justification:
<Why this module requires higher coverage>

Actual coverage achieved:
- INSTRUCTION: <actual>%
- BRANCH: <actual>%
- COMPLEXITY: <actual>%

Module: <path/to/module>
Priority: <P0-P3>

Related: Phase 2 Week <N> objectives
Requirement: SYS-COM-002 (Test coverage requirements)

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

**Example:**
```
build(banner-marking): increase JaCoCo coverage threshold to 95%

Updated JaCoCo coverage enforcement for banner-marking from 75% to 95%
based on Phase 2 coverage improvement work.

Changes:
- INSTRUCTION: 75% → 95%
- BRANCH: 75% → 95%
- COMPLEXITY: 75% → 95%

Justification:
Security-critical module (P0) responsible for classification banner
generation and validation. Incorrect banners could mislead users
about classification levels, risking data spillage.

Actual coverage achieved:
- INSTRUCTION: 96.2%
- BRANCH: 94.8%
- COMPLEXITY: 95.5%

Module: catalog/security/banner-marking
Priority: P0 CRITICAL

Related: Phase 2 Week 6 objectives
Requirement: SYS-COM-002 (Test coverage requirements)

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

## Commit Sequence Planning

### Example: banner-marking Module (335 tests, 0% → 95%)

**Total Implementation:** 6 phases, 7 commits over 8-10 days

| Commit | Type | Description | Tests | Coverage | Days |
|--------|------|-------------|-------|----------|------|
| 1 | Infrastructure | Test skeletons, utilities, test plan | 0 active | 0% | 1 |
| 2 | Phase 1 | Enum validation tests | 87 | 18% | 1-2 |
| 3 | Phase 2 | Data object tests | 65 | 35% (+17%) | 1-2 |
| 4 | Phase 3a | Basic validation tests | 55 | 58% (+23%) | 2 |
| 5 | Phase 3b | Complex validation tests | 48 | 78% (+20%) | 2 |
| 6 | Phase 4-5 | Integration + edge cases | 80 | 95% (+17%) | 2-3 |
| 7 | Threshold | Update pom.xml to enforce 95% | 0 | 95% | 0.5 |

**Day-by-Day Breakdown:**

**Day 1 (Mon):**
- Commit 1: Infrastructure setup
- Start Phase 1 implementation

**Day 2 (Tue):**
- Complete Phase 1
- Commit 2: Phase 1 enum tests (87 tests, +18% coverage)

**Day 3-4 (Wed-Thu):**
- Phase 2 implementation
- Commit 3: Phase 2 data object tests (65 tests, +17% coverage)

**Day 5-6 (Fri-Mon):**
- Phase 3a implementation
- Commit 4: Phase 3a basic validation (55 tests, +23% coverage)

**Day 7-8 (Tue-Wed):**
- Phase 3b implementation
- Commit 5: Phase 3b complex validation (48 tests, +20% coverage)

**Day 9-10 (Thu-Fri):**
- Phase 4-5 implementation
- Commit 6: Integration + edge cases (80 tests, +17% coverage)
- Commit 7: Update coverage threshold to 95%

### Alternative: Daily Commits

For faster feedback and smaller changes:

| Commit | Description | Tests | Coverage | Duration |
|--------|-------------|-------|----------|----------|
| 1 | Infrastructure | 0 | 0% | 4 hours |
| 2 | Enum tests: Classification + SCI | 33 | 8% | 4 hours |
| 3 | Enum tests: SAP + AEA | 20 | 12% (+4%) | 4 hours |
| 4 | Enum tests: Dissem controls | 34 | 18% (+6%) | 4 hours |
| 5 | Data objects: Basic structures | 35 | 28% (+10%) | 4 hours |
| ... | ... | ... | ... | ... |

**Trade-offs:**
- **Logical phases:** Fewer commits, clearer story, easier review
- **Daily commits:** Faster feedback, smaller diffs, more granular history

**Recommendation:** Use logical phases for well-planned work; use daily commits for exploratory testing or when learning the codebase.

## Multi-Module Coordination

### Sequential Development (Recommended)

Complete one module before starting another:

```
Week 5:
├── Mon-Wed: banner-marking (P0) → 95%
├── Wed-Fri: catalog-core-classification-api (P0) → 95%
└── Weekend: Begin video-security (P0)

Week 6:
├── Mon-Tue: Complete video-security (P0) → 90%
├── Wed-Thu: imaging-plugin-nitf (P1) → 85%
└── Fri: Overall coverage verification
```

**Advantages:**
- Clear focus, one module at a time
- Easier code review (one module per PR)
- Can measure coverage improvement per module

### Parallel Development (Advanced)

Work on multiple modules simultaneously:

```
Week 5:
├── Developer A: banner-marking (P0)
├── Developer B: catalog-core-classification-api (P0)
└── Developer C: video-security (P0)
```

**Requirements:**
- Multiple developers
- Good branch management
- Careful merge coordination

**Recommended Strategy:** Sequential for solo developer, parallel for teams.

## Branching Strategy

### Option 1: Single Long-Lived Branch

```
feature/phase2-coverage-improvement
├── Commit 1: Infrastructure (banner-marking)
├── Commit 2: Phase 1 enum tests
├── Commit 3: Phase 2 data objects
├── Commit 4: Phase 3a validation
├── ...
└── PR: All Phase 2 coverage work (final review)
```

**Advantages:**
- Single PR review
- All changes together
- Clear "before and after"

**Disadvantages:**
- Large PR, harder to review
- Risk of conflicts
- No incremental feedback

### Option 2: Module-Specific Branches

```
feature/coverage-banner-marking (from master)
├── All banner-marking commits
└── PR #1 → Merged to master

feature/coverage-classification-api (from master)
├── All classification-api commits
└── PR #2 → Merged to master

feature/coverage-video-security (from master)
├── All video-security commits
└── PR #3 → Merged to master
```

**Advantages:**
- Smaller PRs, easier review
- Incremental progress to master
- Faster feedback

**Disadvantages:**
- More branch management
- Need to coordinate dependencies

### Option 3: Phase-Specific Branches

```
feature/phase2-week5 (from master)
├── banner-marking commits
├── classification-api commits
└── PR #1 → Merged to master

feature/phase2-week6 (from master)
├── video-security commits
├── nitf commits
└── PR #2 → Merged to master
```

**Advantages:**
- Weekly milestones
- Balanced PR size
- Regular integration

**Recommendation:** Use Option 2 (module-specific branches) for best balance of reviewability and progress tracking.

## Traceability Integration

### Requirement Tags in Commit Messages

Every test-related commit MUST include:

```
Requirement: <SYS-XXX-NNN>, <SWR-XXX-NNN>, ...
```

**Example:**
```
Requirement: SYS-SEC-005, SWR-SEC-001, SWR-SEC-002
```

### Source Method Traceability

For implementation commits, list methods tested:

```
Verifies:
- ClassificationLevel.java::fromString()
- ClassificationLevel.java::isValid()
- SciControl.java::validate()
```

### Test Method Naming Convention

Use descriptive names that map to requirements:

```java
// Good: Clear what requirement is being tested
@Test
public void testClassificationLevel_SecretLevel_MeetsSwrSec001() {
    // SWR-SEC-001: System SHALL validate SECRET classification
}

// Bad: Generic name, no requirement link
@Test
public void testClassification() {
    // What requirement does this verify?
}
```

### Traceability Matrix Updates

After completing a module, update traceability matrix:

```
docs(requirements): update traceability matrix for banner-marking

Added traceability entries for banner-marking test implementation:
- 335 test methods → 47 source methods
- Coverage: SYS-SEC-005, SWR-SEC-001 through SWR-SEC-015
- All requirements now have test verification

Module: catalog/security/banner-marking
Coverage: 95% (instruction, branch, complexity)

Related: Phase 2 Week 6 completion

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

## Coverage Verification

### Pre-Commit Checks

Before committing test implementation:

```bash
# 1. Run tests to ensure they pass
mvn test -Dtest=ClassificationLevelTest

# 2. Check coverage locally
mvn jacoco:report
# Open target/site/jacoco/index.html

# 3. Verify build success with coverage check
mvn clean verify

# 4. Format code
mvn fmt:format

# 5. Stage and commit
git add .
git commit
```

### Coverage Impact Measurement

**Before implementation:**
```bash
# Record baseline
mvn clean test jacoco:report
grep -A 3 "INSTRUCTION" target/site/jacoco/jacoco.xml > baseline.txt
```

**After implementation:**
```bash
# Measure improvement
mvn clean test jacoco:report
grep -A 3 "INSTRUCTION" target/site/jacoco/jacoco.xml > after.txt
diff baseline.txt after.txt
```

**Include in commit message:**
```
Coverage impact:
- Before: 45% instruction, 42% branch, 44% complexity
- After: 63% instruction, 60% branch, 62% complexity
- Improvement: +18% instruction, +18% branch, +18% complexity
```

## Common Scenarios

### Scenario 1: Found a Bug While Writing Tests

**Correct sequence:**
1. Create failing test that demonstrates the bug
2. Commit: `test(module): add failing test for bug #XYZ`
3. Fix the bug in source code
4. Commit: `fix(module): correct validation logic (fixes #XYZ)`
5. Verify test now passes

**Rationale:** Separates test from fix, provides regression test.

**Single commit alternative (if small):**
```
fix(module): correct validation logic (fixes #XYZ)

Fixed incorrect validation in ClassificationLevel.fromString()
that caused parsing failures for hyphenated classifications.

Bug: SECRET-LEVEL was incorrectly rejected
Fix: Updated regex to allow hyphens in classification names
Test: Added ClassificationLevelTest.testFromString_Hyphenated()

Requirement: SWR-SEC-001
```

### Scenario 2: Refactoring Tests

**If refactoring while implementing:**
```
test(module): implement Phase 2 with test refactoring

Implemented Phase 2 data object tests. Refactored common setup
logic into BannerTestUtils to reduce duplication.

Tests implemented:
- SecurityAttributesTest: 25 tests
- MarkingDataTest: 20 tests
- BannerContextTest: 20 tests

Refactoring:
- Extracted common banner creation to BannerTestUtils
- Reduced test code by ~150 lines
- Improved test readability

Coverage: +17% (35% total)
```

**If refactoring separately:**
```
refactor(test): extract common banner test utilities

Extracted common test setup logic from banner-marking tests
into BannerTestUtils to reduce duplication and improve maintainability.

Changes:
- Created BannerTestUtils with 8 utility methods
- Refactored 7 test classes to use utilities
- Reduced test code by ~150 lines
- No change to test coverage or behavior

Module: catalog/security/banner-marking
```

### Scenario 3: Multi-File Test Implementation

**Option A: All in one commit (if cohesive)**
```
test(banner-marking): implement Phase 3a validation tests (55 tests)

Implemented Phase 3a (basic validation) covering classification
level validation, SCI control validation, and SAP validation.

Tests implemented:
- BannerValidatorTest: 25 tests (classification rules)
- MarkingExtractorTest: 18 tests (metadata extraction)
- ValidationRulesTest: 12 tests (DoD 5200.01 compliance)

Coverage: +23% (58% total)
```

**Option B: Separate by test class (if large)**
```
# Commit 1
test(banner-marking): add BannerValidatorTest (25 tests, +12%)

# Commit 2
test(banner-marking): add MarkingExtractorTest (18 tests, +8%)

# Commit 3
test(banner-marking): add ValidationRulesTest (12 tests, +3%)
```

**Recommendation:** Option A for cohesive phases, Option B if commits become too large (>500 lines).

### Scenario 4: Test Data Files

**If test data is substantial:**
```
test(banner-marking): add DoD 5200.01 test cases

Added comprehensive test data files derived from DoD 5200.01
security marking standards.

Test data:
- valid-banners.csv: 75 valid banner combinations
- invalid-banners.csv: 50 invalid banners with error codes
- classification-levels.json: All DoD classification levels
- sci-compartments.json: Current SCI compartment list

Source: DoD 5200.01-V1-V4 (2020)
Usage: Shared across 8 test classes

Module: catalog/security/banner-marking

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

## DO-278 Compliance Notes

### Commit History as Verification Evidence

Git commit history serves as verification evidence for DO-278:

1. **Requirements Traceability:** Requirement tags in commits link tests to requirements
2. **Verification Records:** Commits document when tests were created and verified
3. **Change Control:** Commit history provides audit trail of test changes
4. **Configuration Management:** Git provides baseline and version control

### Commit Message Requirements for DO-278

Every commit MUST include:
- [ ] What changed (summary)
- [ ] Why it changed (rationale)
- [ ] What requirement it relates to (traceability)
- [ ] What verification was performed (coverage/test execution)

### Example DO-278 Compliant Commit

```
test(banner-marking): implement Phase 1 enum validation tests

WHAT:
Implemented 87 enum validation tests for banner-marking security
module, covering all enumeration types used in classification marking.

WHY:
Phase 2 coverage improvement to achieve 95% coverage on P0 critical
security module. Enumerations are foundation of classification system
and must be thoroughly validated.

REQUIREMENTS:
- SYS-SEC-005: Security classification handling
- SWR-SEC-001: Classification level validation
- SWR-SEC-002: SCI control validation
- SWR-SEC-003: SAP control validation

VERIFICATION:
- Coverage: 0% → 18% (instruction), 0% → 15% (branch)
- All 87 tests passing
- No existing tests broken
- Static analysis passed

TRACEABILITY:
Tests verify:
- ClassificationLevel.java (15 tests)
- SciControl.java (18 tests)
- SapControl.java (12 tests)
- AeaType.java (8 tests)
- DissemControl.java (22 tests)
- OtherDissemControl.java (12 tests)

Module: catalog/security/banner-marking
Phase: 1/6 (Enum Validation)

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

## Git Commands Cheat Sheet

### Creating Feature Branch
```bash
# From master, create module-specific branch
git checkout master
git pull origin master
git checkout -b feature/coverage-banner-marking
```

### Committing Changes
```bash
# Stage test files
git add catalog/security/banner-marking/src/test/

# Commit with template
git commit
# (opens editor, paste template, fill in details)

# Or inline (for small commits)
git commit -m "test(banner-marking): add enum tests (87 tests, +18%)"
```

### Measuring Coverage Impact
```bash
# Before implementation
git checkout -b coverage-measurement
mvn clean test jacoco:report
cp target/site/jacoco/jacoco.xml jacoco-before.xml

# After implementation
mvn clean test jacoco:report
cp target/site/jacoco/jacoco.xml jacoco-after.xml

# Compare
diff jacoco-before.xml jacoco-after.xml
```

### Reviewing Commit History
```bash
# View commits with coverage info
git log --oneline --grep="coverage" --all

# View commits for specific module
git log --oneline -- catalog/security/banner-marking/

# View commit details
git show <commit-hash>
```

### Amending Last Commit (if needed)
```bash
# Add forgotten file
git add forgotten-file.java
git commit --amend --no-edit

# Fix commit message
git commit --amend
```

## Review Checklist

Before committing, verify:

### Code Quality
- [ ] All tests pass: `mvn test`
- [ ] Code formatted: `mvn fmt:format`
- [ ] No checkstyle violations
- [ ] Coverage increased (or maintained)

### Commit Message Quality
- [ ] Type and scope present (`test(module):`)
- [ ] Short summary (< 72 chars)
- [ ] Detailed description of changes
- [ ] Requirement IDs included
- [ ] Coverage impact documented
- [ ] Verification results included
- [ ] Claude Code attribution present

### Traceability
- [ ] Requirement tags complete
- [ ] Source methods listed (for impl commits)
- [ ] Module path specified
- [ ] Phase/priority noted

### Atomicity
- [ ] Commit is self-contained
- [ ] Build succeeds
- [ ] Tests pass
- [ ] Logical grouping of changes

## Best Practices Summary

### DO:
✅ Write descriptive commit messages with full context
✅ Include requirement traceability in every commit
✅ Document coverage impact quantitatively
✅ Keep commits atomic and buildable
✅ Use conventional commit format
✅ List verified source methods
✅ Update traceability matrix after module completion

### DON'T:
❌ Commit broken tests without @Ignore
❌ Mix test implementation with source changes (unless bug fix)
❌ Omit requirement tags
❌ Use vague commit messages ("add tests")
❌ Create commits that don't compile
❌ Commit without running tests
❌ Skip coverage verification

## References

### Internal Documentation
- `docs/testing/COVERAGE-IMPROVEMENT-STRATEGY.md` - Overall Phase 2 strategy
- `docs/requirements/TRACEABILITY-MATRIX.md` - Requirement traceability
- `docs/requirements/SYSTEM-REQUIREMENTS.md` - System requirements
- `CLAUDE.md` - Project guidance

### External Resources
- [Conventional Commits](https://www.conventionalcommits.org/) - Commit message format
- [Git Best Practices](https://git-scm.com/book/en/v2/Distributed-Git-Contributing-to-a-Project)
- DO-278 Section 7: Configuration Management
- DO-278 Section 8: Quality Assurance

---

**Document Owner:** Alliance Development Team
**Review Cycle:** After each Phase 2 module completion
**Next Review:** After banner-marking module completion (Week 5)
