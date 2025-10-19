## Description

<!-- Provide a clear and concise description of the changes in this PR -->

## Type of Change

<!-- Check all that apply -->

- [ ] Bug fix (non-breaking change that fixes an issue)
- [ ] New feature (non-breaking change that adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to change)
- [ ] Security fix (addresses a security vulnerability)
- [ ] Dependency update
- [ ] Documentation update
- [ ] Performance improvement
- [ ] Code refactoring
- [ ] Test coverage improvement

## DO-278 Compliance Checklist

<!-- All items must be checked for PR approval -->

### Requirements Traceability
- [ ] Requirement ID documented (if applicable): ___________
- [ ] Change traced to requirement or bug report
- [ ] Impact analysis completed for affected components

### Test-Driven Development (TDD)
- [ ] **Tests written BEFORE implementation** (critical for new features/bug fixes)
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated (if applicable)
- [ ] End-to-end tests added/updated (if applicable)
- [ ] All tests pass locally

### Code Quality
- [ ] Code follows google-java-format style (`mvn fmt:format`)
- [ ] Checkstyle passes (`mvn checkstyle:check`)
- [ ] No new compiler warnings
- [ ] Error Prone static analysis passes
- [ ] LGPL v3 license headers present on new files

### Coverage Requirements
- [ ] **Coverage does not decrease below baseline (75%)**
- [ ] New code has >= 90% coverage (instruction, branch, complexity)
- [ ] Coverage report reviewed (check workflow artifacts)

### Security (if applicable)
- [ ] **For security fixes**: Test harness created BEFORE fix
- [ ] No hardcoded credentials or secrets
- [ ] OWASP dependency check passes
- [ ] CodeQL analysis passes
- [ ] Secret scanning passes

### Verification & Validation
- [ ] Code reviewed by at least one other developer
- [ ] Manual testing performed (describe below)
- [ ] Regression testing completed (existing features still work)
- [ ] Edge cases and error conditions tested

### Documentation
- [ ] Code comments added for complex logic
- [ ] JavaDoc updated for public APIs
- [ ] README.md updated (if applicable)
- [ ] CHANGELOG.md updated (if applicable)
- [ ] Architecture diagrams updated (if applicable)

## Security Considerations

<!-- If this PR addresses security issues, complete this section -->

### Security Fix Process (if applicable)
- [ ] CVE Number (if applicable): ___________
- [ ] Vulnerability test harness created first (link to commit): ___________
- [ ] Test demonstrates vulnerability in controlled manner
- [ ] Fix implemented with tests proving remediation
- [ ] No information disclosure in commit messages

### Security Impact Assessment
- [ ] No new security vulnerabilities introduced
- [ ] Authentication/authorization not affected
- [ ] Input validation implemented for all user inputs
- [ ] Output encoding implemented where needed
- [ ] Sensitive data handling reviewed

## Testing Performed

<!-- Describe the testing you performed to verify your changes -->

### Unit Tests
<!-- List key unit tests added/modified -->
-

### Integration Tests
<!-- List integration tests added/modified -->
-

### Manual Testing
<!-- Describe manual testing performed -->
-

### Test Coverage Summary
<!-- Will be auto-populated by test-coverage.yml workflow -->
<!-- Or paste coverage metrics if running locally -->

**Before this PR:**
- Instruction Coverage: ___%
- Branch Coverage: ___%
- Complexity Coverage: ___%

**After this PR:**
- Instruction Coverage: ___%
- Branch Coverage: ___%
- Complexity Coverage: ___%

## Breaking Changes

<!-- If this PR contains breaking changes, describe them and migration path -->

- [ ] No breaking changes
- [ ] Breaking changes documented below:

<!-- Describe breaking changes and how users should migrate -->

## Dependencies

<!-- List any dependencies required by this change -->

- [ ] No new dependencies
- [ ] New dependencies listed below:

<!-- List new dependencies with justification -->

## Deployment Notes

<!-- Any special deployment considerations? -->

- [ ] No special deployment steps required
- [ ] Special deployment steps documented below:

<!-- Describe deployment steps if needed -->

## Rollback Plan

<!-- How can this change be rolled back if issues arise? -->

- [ ] Standard rollback (revert commit)
- [ ] Special rollback procedure documented below:

<!-- Describe rollback procedure if needed -->

## Related Issues

<!-- Link related issues -->

Closes #
Related to #

## Screenshots (if applicable)

<!-- Add screenshots to help explain your changes -->

## Additional Context

<!-- Add any other context about the PR here -->

## Pre-Merge Checklist

<!-- Final checks before merge -->

- [ ] All CI/CD workflows pass (build, security, coverage)
- [ ] Code review approved by required reviewers
- [ ] All conversations resolved
- [ ] Branch is up to date with target branch
- [ ] Commit messages follow conventional format
- [ ] No merge conflicts

---

## Reviewer Guidance

### For Reviewers

Please verify:

1. **DO-278 Compliance**: All checklist items above are completed
2. **Test Quality**: Tests are meaningful and cover edge cases
3. **Security**: No security regressions or vulnerabilities introduced
4. **Code Quality**: Code is maintainable and follows project standards
5. **Documentation**: Changes are adequately documented
6. **Coverage**: No coverage decrease, new code well-tested

### Review Focus Areas

- [ ] Logic correctness
- [ ] Error handling
- [ ] Security considerations
- [ ] Performance impact
- [ ] Test coverage and quality
- [ ] Documentation completeness
- [ ] API compatibility
- [ ] Code maintainability

### Approval Criteria

- At least one approval from project maintainer
- All CI/CD checks pass
- All checklist items completed
- No unresolved conversations
- Coverage meets minimum requirements (75% baseline, 90% for new code)
