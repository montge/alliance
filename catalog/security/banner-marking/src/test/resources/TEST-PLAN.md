# Banner-Marking Module Test Plan

## Executive Summary

This document outlines the comprehensive testing strategy for the `banner-marking` module, which implements security classification banner parsing and validation according to DoD 5200.1-M.

**Module:** `/home/e/Development/alliance/catalog/security/banner-marking`
**Current Coverage:** 0% (no JUnit tests exist, only Groovy/Spock tests)
**Target Coverage:** 95%
**Priority:** P0 (High Security Criticality)
**Total Source Files:** 17
**Estimated Total Methods:** ~120

## Objectives

1. Achieve 95% code coverage through comprehensive JUnit 4 tests
2. Validate all DoD 5200.1-M security marking rules
3. Ensure security classification accuracy (zero tolerance for misclassification)
4. Provide regression protection for security-critical code
5. Enable confident refactoring and maintenance

## Test Strategy

### Approach

- **Test-First Mindset:** Follow TDD principles (skeleton tests created, implementations next)
- **Comprehensive Coverage:** Test normal cases, edge cases, error conditions
- **Data-Driven Testing:** Use parameterized tests for rule variations
- **Validation Focus:** Emphasize security validation rules from DoD 5200.1-M
- **Regression Protection:** Prevent security vulnerabilities through test coverage

### Test Types

1. **Unit Tests** (Primary focus)
   - Isolated testing of each class
   - Mock external dependencies
   - Fast execution (<5 seconds total)

2. **Integration Tests** (Existing Groovy/Spock tests)
   - End-to-end parsing and validation
   - Real object interactions
   - Complement JUnit unit tests

3. **Parameterized Tests**
   - Use JUnit parameterized tests for rule variations
   - Load test data from `/test-banners/` resource files
   - Cover all DoD 5200.1-M paragraphs

## Source Files and Test Coverage

### Phase 1: Core Data Model (Priority 1)

| Source File | Test File | Methods | Complexity | Status | Notes |
|------------|-----------|---------|------------|--------|-------|
| `ClassificationLevel.java` | `ClassificationLevelTest.java` | ~8 | Low | Skeleton Created | Enum with 5 levels |
| `MarkingType.java` | `MarkingTypeTest.java` | ~5 | Low | Skeleton Created | Enum with 3 types (US/FGI/JOINT) |
| `AeaType.java` | `AeaTypeTest.java` | ~5 | Low | Skeleton Created | Enum with 4 AEA types |
| `DissemControl.java` | `DissemControlTest.java` | ~10 | Low | Skeleton Created | Enum with ~9 dissem controls |
| `OtherDissemControl.java` | `OtherDissemControlTest.java` | ~6 | Low | Skeleton Created | Enum with 4 other controls |

**Phase 1 Coverage Target:** 95%
**Estimated Effort:** 2-3 hours
**Dependencies:** None

### Phase 2: Complex Data Structures (Priority 1)

| Source File | Test File | Methods | Complexity | Status | Notes |
|------------|-----------|---------|------------|--------|-------|
| `SciControl.java` | `SciControlTest.java` | ~15 | High | Skeleton Created | SCI with compartments/sub-compartments |
| `SapControl.java` | `SapControlTest.java` | ~12 | Medium | Skeleton Created | SAP with programs, HVSACO |
| `AeaMarking.java` | `AeaMarkingTest.java` | ~15 | Medium | Skeleton Created | AEA with CNWDI, SIGMA |

**Phase 2 Coverage Target:** 95%
**Estimated Effort:** 4-6 hours
**Dependencies:** Phase 1 enums

### Phase 3: Main Entities (Priority 1)

| Source File | Test File | Methods | Complexity | Status | Notes |
|------------|-----------|---------|------------|--------|-------|
| `BannerMarkings.java` | `BannerMarkingsSpec.groovy` | ~40 | Very High | Groovy Tests Exist | Main banner parsing class |
| `PortionMarkings.java` | `PortionMarkingsSpec.groovy` | ~20 | High | Groovy Tests Exist | Portion marking parsing |

**Phase 3 Note:** Groovy/Spock tests already provide excellent coverage. JUnit tests would be redundant.
**Recommendation:** Keep existing Groovy tests, add JUnit tests only for gaps.

### Phase 4: Validation (Priority 1 - CRITICAL)

| Source File | Test File | Methods | Complexity | Status | Notes |
|------------|-----------|---------|------------|--------|-------|
| `BannerValidator.java` | `BannerValidatorTest.java` | ~50 | Very High | Skeleton Created | Validates all DoD 5200.1-M rules |

**Phase 4 Coverage Target:** 95%
**Estimated Effort:** 8-12 hours (most complex component)
**Dependencies:** All Phase 1-3 classes
**Security Criticality:** HIGHEST - validates classification correctness

### Phase 5: Extractors (Priority 2)

| Source File | Test File | Methods | Complexity | Status | Notes |
|------------|-----------|---------|------------|--------|-------|
| `BannerCommonMarkingExtractor.java` | `BannerCommonMarkingExtractorSpec.groovy` | ~15 | Medium | Groovy Tests Exist | Extracts common attributes |
| `Dod520001MarkingExtractor.java` | `Dod520001MarkingExtractorSpec.groovy` | ~12 | Medium | Groovy Tests Exist | Extracts DoD-specific attributes |
| `MarkingExtractor.java` | `MarkingExtractorTest.java` | ~3 | Low | Skeleton Created | Interface definition |

**Phase 5 Note:** Groovy tests exist and are comprehensive. JUnit tests for interface only.

### Phase 6: Exceptions (Priority 3)

| Source File | Test File | Methods | Complexity | Status | Notes |
|------------|-----------|---------|------------|--------|-------|
| `ValidationError.java` | `ValidationErrorTest.java` | ~6 | Low | Skeleton Created | Error with paragraph ref |
| `MarkingsValidationException.java` | `MarkingsValidationExceptionTest.java` | ~5 | Low | Skeleton Created | Validation exception |
| `MarkingMismatchException.java` | `MarkingMismatchExceptionTest.java` | ~3 | Low | Skeleton Created | Mismatch exception |

**Phase 6 Coverage Target:** 90% (some Groovy tests exist)
**Estimated Effort:** 1-2 hours
**Dependencies:** None

## Test Implementation Priority

### Phase 1: Foundation (Week 1)
1. Implement enum tests (ClassificationLevel, MarkingType, AeaType, DissemControl, OtherDissemControl)
2. Create BannerTestUtils helper methods
3. Create MetacardTestFactory helper methods
4. Verify test infrastructure works

**Deliverable:** 5 enum test classes fully implemented, ~95% coverage on enums

### Phase 2: Data Structures (Week 1-2)
1. Implement SciControlTest
2. Implement SapControlTest
3. Implement AeaMarkingTest
4. Verify complex object creation and equality

**Deliverable:** 3 complex object test classes, ~95% coverage

### Phase 3: Validation (Week 2-3) - CRITICAL PATH
1. Implement BannerValidatorTest incrementally by DoD 5200.1-M section:
   - Section 4.a: US markings validation
   - Section 4.b: FGI markings validation
   - Section 5: JOINT markings validation
   - Section 6: SCI controls validation
   - Section 7: SAP controls validation
   - Section 8: AEA markings validation
   - Section 9: FGI country codes validation
   - Section 10: Dissemination controls validation
   - Complex validation scenarios

2. Use parameterized tests with data from `/test-banners/invalid-banners.txt`
3. Verify all paragraph references are correct

**Deliverable:** BannerValidatorTest with ~120 test methods, ~95% coverage

### Phase 4: Exceptions and Utilities (Week 3)
1. Implement exception tests (ValidationError, MarkingsValidationException, MarkingMismatchException)
2. Implement MarkingExtractorTest (interface contract)
3. Complete BannerTestUtils implementations
4. Complete MetacardTestFactory implementations

**Deliverable:** All remaining test classes, 95% overall module coverage

### Phase 5: Integration and Verification (Week 4)
1. Run full test suite
2. Generate JaCoCo coverage report
3. Identify and fill coverage gaps
4. Verify all DoD 5200.1-M rules are tested
5. Performance testing (ensure tests run fast)

**Deliverable:** 95% coverage achieved, all tests passing, fast execution

## DoD 5200.1-M Paragraph Coverage Matrix

This matrix maps test methods to specific paragraphs in DoD 5200.1-M:

### Classification Markings (Section 4)
| Paragraph | Rule | Test Methods | Test File |
|-----------|------|--------------|-----------|
| 4.a | US classification levels | testValidateUsMarkings_* | BannerValidatorTest |
| 4.b.2.a | NATO/COSMIC require control marking | testValidateFgiMarkings_InvalidNatoWithoutControl | BannerValidatorTest |
| 4.b.2.c | NATO/COSMIC control marking restrictions | testValidateFgiMarkings_InvalidNatoIncorrectControl | BannerValidatorTest |
| 4.b.3 | NATO/COSMIC cannot have NOFORN | testValidateFgiMarkings_InvalidCosmicWithNoforn | BannerValidatorTest |

### Joint Markings (Section 5)
| Paragraph | Rule | Test Methods | Test File |
|-----------|------|--------------|-----------|
| 5.d | JOINT cannot be RESTRICTED | testValidateJointMarkings_InvalidRestricted | BannerValidatorTest |

### SCI Controls (Section 6)
| Paragraph | Rule | Test Methods | Test File |
|-----------|------|--------------|-----------|
| 6.c | SCI requires dissemination control | testValidateSciControls_InvalidMissingDissemination | BannerValidatorTest |
| 6.f | HCS-specific requirements | testValidateSciControls_InvalidHcsWithoutNoforn | BannerValidatorTest |

### SAP Controls (Section 7)
| Paragraph | Rule | Test Methods | Test File |
|-----------|------|--------------|-----------|
| 7.e | Maximum 3 SAP programs | testValidateSapControls_InvalidTooManyPrograms | BannerValidatorTest |
| 7.f | WAIVED requires SAP | testValidateSapControls_InvalidWaivedWithoutSap | BannerValidatorTest |

### AEA Markings (Section 8)
| Paragraph | Rule | Test Methods | Test File |
|-----------|------|--------------|-----------|
| 8.a.4 | RD requires CONFIDENTIAL+ | testValidateAeaMarkings_InvalidRdBelowConfidential | BannerValidatorTest |
| 8.b.2 | FRD requires CONFIDENTIAL+ | testValidateAeaMarkings_InvalidFrdBelowConfidential | BannerValidatorTest |
| 8.c.3 | FRD cannot have CNWDI | testValidateAeaMarkings_InvalidFrdWithCnwdi | BannerValidatorTest |
| 8.d.3 | FRD SIGMA range 1-99 | testValidateAeaMarkings_InvalidFrdSigmaRange | BannerValidatorTest |

### FGI Country Codes (Section 9)
| Paragraph | Rule | Test Methods | Test File |
|-----------|------|--------------|-----------|
| 9.a | FGI document cannot have FGI marking | testValidateFgiCountryCodes_InvalidFgiInFgiDocument | BannerValidatorTest |
| 9.b | FGI not allowed with RESTRICTED | testValidateFgiCountryCodes_InvalidFgiWithRestricted | BannerValidatorTest |
| 9.d | FGI countries must be alphabetical | testValidateFgiCountryCodes_InvalidCountryOrder | BannerValidatorTest |

### Dissemination Controls (Section 10)
| Paragraph | Rule | Test Methods | Test File |
|-----------|------|--------------|-----------|
| 1.b, 1.c | IMCON classification requirements | testValidateDisseminationControls_InvalidImconClassification | BannerValidatorTest |
| 1.c | IMCON cannot combine with FISA | testValidateDisseminationControls_InvalidImconWithFisa | BannerValidatorTest |
| 2.c | NOFORN not with RESTRICTED | testValidateDisseminationControls_InvalidNofornWithRestricted | BannerValidatorTest |
| 2.d | NOFORN conflicts with REL TO/RELIDO | testValidateDisseminationControls_InvalidNofornWithRelto | BannerValidatorTest |
| 3.b | PROPIN not with RESTRICTED | testValidateDisseminationControls_InvalidPropinWithRestricted | BannerValidatorTest |
| 4.c | RELIDO not with RESTRICTED | testValidateDisseminationControls_InvalidRelidoWithRestricted | BannerValidatorTest |
| 10.d.3 | ORCON not with RESTRICTED | testValidateDisseminationControls_InvalidOrconWithRestricted | BannerValidatorTest |
| 10.e.3 | REL TO not with RESTRICTED | testValidateRelTo_InvalidWithRestricted | BannerValidatorTest |
| 10.e.5 | REL TO cannot be USA only | testValidateRelTo_InvalidUsaOnly | BannerValidatorTest |
| 10.e.7 | REL TO conflicts with NOFORN | testValidateRelTo_InvalidWithNoforn | BannerValidatorTest |
| 10.g.3 | DISPLAY ONLY not with RESTRICTED | testValidateDisplayOnly_InvalidWithRestricted | BannerValidatorTest |
| 10.g.4 | DISPLAY ONLY conflicts with NOFORN/RELIDO | testValidateDisplayOnly_InvalidWithNoforn | BannerValidatorTest |

## Test Data Organization

### Test Resource Files
- `/test-banners/valid-us-banners.txt` - 50+ valid US banner examples
- `/test-banners/valid-fgi-banners.txt` - 10+ valid FGI banner examples
- `/test-banners/valid-joint-banners.txt` - 10+ valid JOINT banner examples
- `/test-banners/invalid-banners.txt` - 50+ invalid banners with paragraph refs
- `/test-banners/README.md` - Documentation for test data files

### Test Utilities
- `BannerTestUtils.java` - Factory methods for creating test banners
- `MetacardTestFactory.java` - Factory methods for creating test metacards

## Coverage Goals

### By Component
| Component | Target Coverage | Rationale |
|-----------|----------------|-----------|
| Enums (ClassificationLevel, etc.) | 95% | Simple enums, high coverage achievable |
| Data Objects (SciControl, etc.) | 95% | Complex but testable |
| BannerValidator | 95% | CRITICAL - must be thoroughly tested |
| BannerMarkings | 90% | Groovy tests exist |
| PortionMarkings | 90% | Groovy tests exist |
| Extractors | 90% | Groovy tests exist |
| Exceptions | 95% | Simple classes |

### Overall Module Target
**95% code coverage** (instruction, branch, and complexity)

## Security Test Scenarios

### Critical Security Tests
1. **Misclassification Prevention**
   - Test that invalid combinations are rejected
   - Test that paragraph references are correct
   - Test that no valid markings are rejected

2. **Injection/Tampering Protection**
   - Test with malformed input
   - Test with special characters
   - Test with extremely long inputs
   - Test with null/empty inputs

3. **Validation Completeness**
   - Test every DoD 5200.1-M rule has a test
   - Test every error paragraph is referenced in tests
   - Test complex multi-control validation

4. **Regression Protection**
   - Test all known valid banners remain valid
   - Test all known invalid banners remain invalid
   - Test that fixes don't break other rules

## Test Execution

### Local Development
```bash
# Run all tests
mvn test -pl catalog/security/banner-marking

# Run specific test class
mvn test -Dtest=BannerValidatorTest -pl catalog/security/banner-marking

# Run with coverage report
mvn clean test jacoco:report -pl catalog/security/banner-marking

# View coverage report
open catalog/security/banner-marking/target/site/jacoco/index.html
```

### CI/CD Integration
Tests will run automatically in GitHub Actions workflows:
- On every PR
- On every push to feature branches
- In nightly builds

## Success Criteria

### Coverage Metrics
- [ ] Overall module coverage ≥ 95%
- [ ] BannerValidator coverage ≥ 95%
- [ ] All enums coverage ≥ 95%
- [ ] All data classes coverage ≥ 95%
- [ ] Zero critical security validation paths untested

### Test Quality
- [ ] All DoD 5200.1-M paragraphs have corresponding tests
- [ ] All validation errors have tests
- [ ] All test data files are used
- [ ] Test execution time < 10 seconds
- [ ] Zero flaky tests
- [ ] All tests have descriptive names and documentation

### Documentation
- [ ] TEST-PLAN.md complete and accurate
- [ ] All test classes have comprehensive JavaDoc
- [ ] Test data files are documented
- [ ] Coverage gaps are documented and justified

## Risks and Mitigations

### Risk 1: Complexity of BannerValidator
**Risk:** BannerValidator is very complex with ~50 methods and ~120 validation rules.
**Mitigation:**
- Break into phases by DoD 5200.1-M section
- Use parameterized tests to reduce duplication
- Load test data from resource files
- Implement incrementally with frequent verification

### Risk 2: Test Data Accuracy
**Risk:** Test data may not accurately reflect DoD 5200.1-M requirements.
**Mitigation:**
- Cross-reference with existing Groovy tests
- Manual review of test data files
- Include paragraph references in invalid banners
- Validate against real-world examples

### Risk 3: Groovy Test Redundancy
**Risk:** JUnit tests may duplicate existing Groovy tests.
**Mitigation:**
- Focus JUnit tests on untested code paths
- Keep Groovy tests for integration scenarios
- Use JUnit for unit tests, Groovy for integration
- Analyze coverage reports to identify gaps

### Risk 4: Time Constraints
**Risk:** Comprehensive testing may take longer than estimated.
**Mitigation:**
- Prioritize by phase (enums first, validator second)
- Set intermediate milestones
- Accept 90% coverage as acceptable for some components
- Use automation (parameterized tests, data files)

## Maintenance Plan

### Ongoing Maintenance
1. **When adding new classification rules:**
   - Add to appropriate test data file
   - Add test method to BannerValidatorTest
   - Update paragraph coverage matrix
   - Verify coverage remains ≥ 95%

2. **When fixing bugs:**
   - Add failing test demonstrating bug
   - Fix bug
   - Verify test passes
   - Ensure no regressions

3. **When refactoring:**
   - Ensure all tests still pass
   - Update tests if interfaces change
   - Maintain coverage ≥ 95%

### Quarterly Review
- Review test data for accuracy
- Update DoD 5200.1-M paragraph references if spec changes
- Identify and fill any coverage gaps
- Performance tune slow tests

## References

- DoD 5200.1-M: DoD Information Security Program
- CAPCO Implementation Manual
- ISOO Marking Guidelines
- Alliance CLAUDE.md (project development standards)
- Existing Groovy/Spock tests for reference patterns

## Appendix A: Test Method Count by Class

| Test Class | Estimated Test Methods | Status |
|-----------|----------------------|--------|
| ClassificationLevelTest | 20 | Skeleton Created |
| MarkingTypeTest | 12 | Skeleton Created |
| AeaTypeTest | 12 | Skeleton Created |
| DissemControlTest | 15 | Skeleton Created |
| OtherDissemControlTest | 10 | Skeleton Created |
| SciControlTest | 35 | Skeleton Created |
| SapControlTest | 35 | Skeleton Created |
| AeaMarkingTest | 40 | Skeleton Created |
| BannerValidatorTest | 120 | Skeleton Created |
| ValidationErrorTest | 12 | Skeleton Created |
| MarkingsValidationExceptionTest | 10 | Skeleton Created |
| MarkingMismatchExceptionTest | 8 | Skeleton Created |
| MarkingExtractorTest | 6 | Skeleton Created |
| **TOTAL** | **~335 test methods** | **13 skeleton test classes created** |

## Appendix B: Implementation Timeline

### Week 1
- **Days 1-2:** Implement all enum tests (5 classes)
- **Days 3-4:** Implement BannerTestUtils and MetacardTestFactory
- **Day 5:** Implement SciControlTest

### Week 2
- **Days 1-2:** Implement SapControlTest and AeaMarkingTest
- **Days 3-5:** Begin BannerValidatorTest (Sections 4.a - 5)

### Week 3
- **Days 1-3:** Continue BannerValidatorTest (Sections 6 - 8)
- **Days 4-5:** Continue BannerValidatorTest (Sections 9 - 10)

### Week 4
- **Days 1-2:** Complete BannerValidatorTest (complex scenarios)
- **Day 3:** Implement exception tests
- **Day 4:** Run coverage analysis, fill gaps
- **Day 5:** Final verification, documentation updates

**Total Estimated Effort:** 20 working days (4 weeks)
