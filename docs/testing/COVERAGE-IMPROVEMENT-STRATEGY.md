# Test Coverage Improvement Strategy

**Phase:** Phase 2, Week 5-6
**Created:** 2025-10-18
**Current Baseline:** 75% (Instruction, Branch, Complexity)
**Target:** 80% overall, 90-95% for critical modules

---

## Executive Summary

This document defines the strategy for improving Alliance test coverage from the current 75% baseline to 80% overall coverage, with security-critical modules achieving 90-95% coverage. This is a Phase 2, Week 5-6 objective supporting DO-278 compliance and test-driven development methodology.

**Key Findings:**
- Current enforcement: 75% minimum on INSTRUCTION, BRANCH, and COMPLEXITY metrics
- Enforcement mechanism: `org.codice.jacoco.LenientLimit` per OSGi bundle
- Can be overridden per module (must override ALL THREE metrics)
- Phase 2 target: 80% overall (5% improvement)
- Critical module target: 90-95% coverage

---

## Current Coverage Enforcement

### JaCoCo Configuration (pom.xml)

**Location:** Root `pom.xml` in `<pluginManagement>` section

```xml
<limit implementation="org.codice.jacoco.LenientLimit">
    <counter>INSTRUCTION</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.75</minimum>
</limit>
<limit implementation="org.codice.jacoco.LenientLimit">
    <counter>BRANCH</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.75</minimum>
</limit>
<limit implementation="org.codice.jacoco.LenientLimit">
    <counter>COMPLEXITY</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.75</minimum>
</limit>
```

### Coverage Metrics Explained

1. **INSTRUCTION Coverage (0.75 = 75%)**
   - Measures: Percentage of Java bytecode instructions executed during tests
   - Most granular metric
   - Indicates how much code actually runs

2. **BRANCH Coverage (0.75 = 75%)**
   - Measures: Percentage of conditional branches (if/else, switch, loops) tested
   - Tests both true and false paths
   - Critical for logic coverage

3. **COMPLEXITY Coverage (0.75 = 75%)**
   - Measures: Cyclomatic complexity coverage
   - Higher complexity = more paths through code
   - Ensures complex methods are thoroughly tested

**Note:** `org.codice.jacoco.LenientLimit` is a custom DDF implementation that provides more flexible coverage enforcement than standard JaCoCo.

---

## Coverage Improvement Process

### Step 1: Identify Coverage Gaps (Week 5, Day 1-3)

#### 1.1 Generate Coverage Reports

```bash
# Run tests with JaCoCo coverage
mvn clean test jacoco:report -DskipITs=true -T 1.5C

# Generate aggregate report (if needed)
mvn jacoco:report-aggregate
```

**Report Locations:**
- Per-module: `{module}/target/site/jacoco/index.html`
- Aggregate: `target/site/jacoco-aggregate/index.html`

#### 1.2 Analyze Coverage by Module

Create a coverage heat map:

```bash
# Extract coverage data from all modules
find . -name "jacoco.xml" -exec grep -H "INSTRUCTION" {} \; | \
  awk -F'[:,"]' '{print $1, $5, $7}' | \
  sort -t' ' -k3 -n > coverage-by-module.txt
```

**Create:** `docs/testing/COVERAGE-HEAT-MAP.md`

| Module | Instruction % | Branch % | Complexity % | Status | Priority |
|--------|---------------|----------|--------------|--------|----------|
| catalog-core-api | 72% | 68% | 70% | ⚠️ Below baseline | P1 |
| imaging-nitf-impl | 65% | 60% | 62% | 🔴 Critical gap | P0 |
| video-mpegts-transformer | 78% | 75% | 76% | ✅ Above baseline | P2 |

#### 1.3 Identify "Coverage Deserts"

**Coverage Desert:** Module or package significantly below 75% baseline

**Criteria:**
- Instruction coverage < 70%
- Branch coverage < 65%
- Any security-critical module < 75%

**Action:** Flag for immediate test development

---

### Step 2: Prioritize Modules (Week 5, Day 4)

#### Priority Matrix

| Priority | Criteria | Target Coverage | Timeframe |
|----------|----------|-----------------|-----------|
| **P0 - Critical** | Security modules < 75% | 90-95% | Week 5 |
| **P1 - High** | Core modules < 75% | 80-85% | Week 5-6 |
| **P2 - Medium** | Non-critical 70-75% | 75-80% | Week 6 |
| **P3 - Low** | Non-critical > 75% | Maintain | Ongoing |

#### Security-Critical Modules (P0)

**Must achieve 90-95% coverage:**

1. `catalog/core/catalog-core-classification-api/`
   - **Why:** Classification handling (UNCLASS → SECRET)
   - **Risk:** Improper marking could leak classified data
   - **Target:** 95%

2. `catalog/security/banner-marking/`
   - **Why:** Security banner generation and display
   - **Risk:** Incorrect banners mislead users about classification
   - **Target:** 95%

3. `video/video-security/`
   - **Why:** Video stream security metadata
   - **Risk:** Classification leakage in video metadata
   - **Target:** 90%

4. `libs/klv/`
   - **Why:** KLV metadata parsing (security-sensitive)
   - **Risk:** Buffer overflows, metadata injection
   - **Target:** 90%

#### Core Data Processing Modules (P1)

**Must achieve 80-85% coverage:**

5. `catalog/imaging/imaging-plugin-nitf/`
   - **Why:** NITF image ingest and processing
   - **Risk:** Image parsing vulnerabilities (XXE, buffer overflows)
   - **Target:** 85%

6. `catalog/imaging/imaging-transformer-nitf/`
   - **Why:** NITF → Metacard transformation
   - **Risk:** Metadata loss, incorrect geolocation
   - **Target:** 85%

7. `catalog/video/video-mpegts-transformer/`
   - **Why:** MPEG-TS video processing
   - **Risk:** Video parsing vulnerabilities
   - **Target:** 85%

8. `catalog/ddms/`
   - **Why:** DDMS XML transformation
   - **Risk:** XXE attacks, XML injection
   - **Target:** 80%

---

### Step 3: Write Tests (Week 5-6, Day 5-14)

#### 3.1 Coverage Improvement Patterns

**Pattern 1: Untested Branches**

```java
// Before: Only happy path tested
public String processData(String input) {
    if (input == null) {
        return "default";  // ❌ Untested branch
    }
    return input.toUpperCase();  // ✅ Tested
}

// Add test for null branch
@Test
public void testProcessData_NullInput_ReturnsDefault() {
    String result = processData(null);
    assertThat(result).isEqualTo("default");
}
```

**Pattern 2: Error Handling Paths**

```java
// Before: Exception paths not tested
public void saveFile(Path path, byte[] data) throws IOException {
    try {
        Files.write(path, data);  // ✅ Tested (happy path)
    } catch (IOException e) {
        log.error("Failed to save", e);  // ❌ Untested
        throw e;
    }
}

// Add test for IOException
@Test
public void testSaveFile_IOException_LogsAndRethrows() {
    Path readOnlyPath = mockReadOnlyPath();
    assertThatThrownBy(() -> saveFile(readOnlyPath, data))
        .isInstanceOf(IOException.class);
    // Verify logging occurred
}
```

**Pattern 3: Boundary Conditions**

```java
// Before: Only typical values tested
public boolean isValid(int value) {
    return value >= 0 && value <= 100;
}

// Add boundary tests
@Test
public void testIsValid_BoundaryConditions() {
    assertThat(isValid(-1)).isFalse();    // Below minimum
    assertThat(isValid(0)).isTrue();      // Minimum
    assertThat(isValid(50)).isTrue();     // Middle
    assertThat(isValid(100)).isTrue();    // Maximum
    assertThat(isValid(101)).isFalse();   // Above maximum
}
```

#### 3.2 Complex Method Strategy

**Cyclomatic Complexity > 10:** Break down and test systematically

**Example:** Method with complexity 15

```java
public Result process(Input input) {
    // Decision 1: Input validation
    if (input == null) { /* path 1 */ }

    // Decision 2: Type check
    if (input.getType() == Type.A) { /* path 2 */ }
    else if (input.getType() == Type.B) { /* path 3 */ }
    else { /* path 4 */ }

    // Decision 3: Security check
    if (!hasPermission()) { /* path 5 */ }

    // ... more conditions
}
```

**Test Strategy:**
1. Create decision table mapping inputs → expected paths
2. Write one test per unique path
3. Use parameterized tests for similar paths
4. Mock dependencies to control flow

```java
@ParameterizedTest
@CsvSource({
    "null, null, INVALID_INPUT",
    "TYPE_A, true, SUCCESS_A",
    "TYPE_B, true, SUCCESS_B",
    "TYPE_A, false, PERMISSION_DENIED"
})
public void testProcess_VariousInputs(String type, boolean hasPermission, String expectedResult) {
    // Arrange
    Input input = createInput(type);
    mockPermissions(hasPermission);

    // Act
    Result result = process(input);

    // Assert
    assertThat(result.getStatus()).isEqualTo(expectedResult);
}
```

---

### Step 4: Override Coverage Limits (Per Module)

For modules requiring > 75% coverage, override in module's `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <executions>
                <execution>
                    <id>default-check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>BUNDLE</element>
                                <!-- MUST override ALL THREE limits -->
                                <limits>
                                    <limit implementation="org.codice.jacoco.LenientLimit">
                                        <counter>INSTRUCTION</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.90</minimum>  <!-- 90% for critical module -->
                                    </limit>
                                    <limit implementation="org.codice.jacoco.LenientLimit">
                                        <counter>BRANCH</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.90</minimum>
                                    </limit>
                                    <limit implementation="org.codice.jacoco.LenientLimit">
                                        <counter>COMPLEXITY</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.90</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

**⚠️ CRITICAL:** When overriding, MUST override ALL THREE limits. If you only override one, the others default to 0.0 (0%), not 0.75 (75%).

---

## Coverage Improvement Tools

### 1. JaCoCo HTML Reports

**Navigate to:** `target/site/jacoco/index.html` per module

**Red (< 50%):** Critical gap - needs immediate attention
**Yellow (50-75%):** Partial coverage - needs improvement
**Green (> 75%):** Good coverage - maintain

**Drill down:**
- Package → Class → Method level
- See exact lines not covered (red highlighting)
- Identify untested branches (yellow diamonds)

### 2. Coverage Diff Tool

```bash
# Compare coverage before/after test additions
diff <(grep "INSTRUCTION" old-jacoco.xml) \
     <(grep "INSTRUCTION" new-jacoco.xml)
```

### 3. IDE Integration

**IntelliJ IDEA:**
- Run → Run with Coverage
- Shows green/red bars in editor gutter
- Identifies untested branches visually

**Eclipse:**
- EclEmma plugin (built-in)
- Coverage mode highlights untested code

---

## Verification and Validation

### Build Verification

```bash
# Verify coverage meets new thresholds
mvn clean verify -P coverage-check

# Should see:
[INFO] All coverage checks passed
```

### Pre-Commit Check

```bash
# Run before committing new tests
mvn test jacoco:check

# Fails if coverage drops below thresholds
```

### CI/CD Integration

**GitHub Actions** (`.github/workflows/test-coverage.yml`):
- Runs coverage check on every PR
- Fails PR if coverage decreases
- Comments on PR with coverage report

---

## Best Practices

### DO:
- ✅ Write tests BEFORE implementing features (TDD)
- ✅ Test happy path AND error paths
- ✅ Test boundary conditions (min, max, min-1, max+1)
- ✅ Use descriptive test names: `testMethodName_Condition_ExpectedResult`
- ✅ One assertion concept per test
- ✅ Mock external dependencies
- ✅ Test at the right level (unit vs integration)

### DON'T:
- ❌ Write tests just to increase coverage (test meaningful scenarios)
- ❌ Test getters/setters (unless they contain logic)
- ❌ Skip error path testing
- ❌ Use real databases/filesystems in unit tests
- ❌ Test private methods directly (test through public API)
- ❌ Override coverage limits downward (always increase or maintain)

---

## Phase 2 Milestones

### Week 5 Deliverables
- [x] Coverage heat map (`COVERAGE-HEAT-MAP.md`)
- [ ] Identify modules below 75%
- [ ] Write tests for P0 modules (security-critical)
- [ ] Achieve 90% on 2-3 critical modules
- [ ] Overall coverage: 76-77% (incremental progress)

### Week 6 Deliverables
- [ ] Complete P1 module improvements (core data processing)
- [ ] Address P2 modules as time permits
- [ ] Overall coverage: 78-80%
- [ ] Document coverage improvement success stories
- [ ] Update module pom.xml files with new limits

---

## Success Metrics

| Metric | Baseline (Start) | Week 5 Target | Week 6 Target | Phase 2 Complete |
|--------|------------------|---------------|---------------|------------------|
| Overall Instruction | 75% | 76% | 78% | 80% |
| Overall Branch | 75% | 76% | 78% | 80% |
| Overall Complexity | 75% | 76% | 78% | 80% |
| Critical Modules (avg) | varies | 85% | 90% | 90-95% |
| Modules < 75% | TBD | TBD - 20% | TBD - 50% | 0 |

**TBD:** To be determined after initial coverage audit completes

---

## Risk Mitigation

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| Legacy code difficult to test | High | Medium | Focus on new code quality, refactor incrementally |
| Time constraint for 80% | Medium | High | Prioritize critical modules first, defer non-critical |
| Flaky tests reduce confidence | High | Low | Invest in test stability, isolate tests properly |
| Coverage gaming (meaningless tests) | Medium | Medium | Code review enforcement, meaningful test requirement |

---

## References

### Internal Documentation
- `docs/PHASE2-PLAN.md` - Overall Phase 2 plan
- `docs/security/VULNERABILITY-BASELINE.md` - Security testing requirements
- `CLAUDE.md` - Project guidance (coverage requirements)

### External Resources
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [AssertJ Assertions](https://assertj.github.io/doc/) - Fluent assertions library
- [Mockito](https://site.mockito.org/) - Mocking framework

### DO-278 Compliance
- DO-278 Section 6.3.4: Test Coverage Analysis
- DO-278 Section 6.4.4.2: Structural Coverage Analysis
- DO-278 Annex A: Objectives for Software Verification

---

**Last Updated:** 2025-10-18
**Next Review:** After initial coverage audit completes
**Document Owner:** Alliance Development Team
