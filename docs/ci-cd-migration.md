# CI/CD Migration Guide: Jenkins to GitHub Actions

## Overview

This document describes the migration from Jenkins-based CI/CD to GitHub Actions, supporting the Alliance project's modernization toward DO-278 compliance and test-driven development.

## Migration Status

**Status**: ✅ Phase 1 Complete - GitHub Actions Foundation Established

### Completed Items
- [x] Created `.github/workflows/` directory structure
- [x] Migrated main build pipeline to `build.yml`
- [x] Created comprehensive security scanning workflow `security-scan.yml`
- [x] Established test coverage tracking in `test-coverage.yml`
- [x] Documented migration process

### Pending Items
- [ ] Configure GitHub Secrets for Maven deployment
- [ ] Set up branch protection rules requiring workflow passes
- [ ] Configure Dependabot for automated dependency updates
- [ ] Establish notification integrations (Slack/email)
- [ ] Validate parallel operation with existing Jenkins pipeline
- [ ] Full platform matrix testing (Windows, macOS)

## Workflow Descriptions

### 1. Build and Test (`build.yml`)

**Purpose**: Primary build and test workflow, replacing Jenkins pipeline

**Triggers**:
- Push to `master` or version branches (`*.x`)
- Pull requests to `master` or version branches
- Nightly scheduled builds (8 PM UTC)
- Manual workflow dispatch

**Jobs**:

1. **validate** (PR only)
   - Quick validation of POM files
   - Code formatting check
   - Fast feedback for contributors

2. **incremental-build** (PR only)
   - Uses gitflow-incremental-builder
   - Builds only changed modules and dependencies
   - Runs tests on affected code
   - Faster feedback cycle for PRs

3. **full-build** (non-PR)
   - Complete build of all modules
   - All unit and integration tests
   - Generates distribution artifacts
   - Multi-platform matrix (expandable)

4. **deploy** (master/version branches only)
   - Deploys artifacts to Maven repository
   - Creates GitHub releases for tags
   - Requires Maven credentials in secrets

5. **notify**
   - Reports build status
   - Future: Slack/Discord integration

**Artifacts Generated**:
- `alliance-distribution` - Built distribution ZIP files
- `test-results-*` - JUnit/Surefire test reports
- `jacoco-reports` - Code coverage reports

### 2. Security Scanning (`security-scan.yml`)

**Purpose**: Comprehensive security analysis aligned with DO-278 security requirements

**Triggers**:
- Push to `master` or version branches
- Pull requests
- Daily scheduled scan (2 AM UTC)
- Manual workflow dispatch

**Jobs**:

1. **dependency-check**
   - OWASP Dependency Check for known CVEs
   - Fails on CVSS >= 7.0
   - Auto-creates GitHub issues for vulnerabilities
   - Caches NVD database for performance

2. **codeql-analysis**
   - GitHub's semantic code analysis
   - Detects security vulnerabilities, bugs, code quality issues
   - Uses `security-extended` and `security-and-quality` query suites
   - Results visible in Security tab

3. **dependency-review** (PR only)
   - Prevents introduction of vulnerable dependencies
   - Reviews license compatibility
   - Comments on PR with findings

4. **secret-scan**
   - TruffleHog scan for accidentally committed secrets
   - Scans commit history
   - Prevents credential leakage

5. **license-check**
   - Apache RAT plugin validation
   - Ensures LGPL v3 compliance
   - Verifies license headers

6. **security-summary**
   - Aggregates all security check results
   - Comments on PR with summary table
   - Blocks merge if security issues detected

**Artifacts Generated**:
- `owasp-dependency-check-report` - HTML vulnerability report
- `license-compliance-report` - License audit results

**Security Issue Workflow**:
When vulnerabilities are detected:
1. Automated GitHub issue created with `security` and `do-278` labels
2. Issue references CLAUDE.md Security Issue Remediation Process
3. Developer must create test harness BEFORE fixing
4. Fix implemented with tests proving remediation
5. Security workflow validates fix in PR

### 3. Test Coverage Analysis (`test-coverage.yml`)

**Purpose**: Track progress from 75% baseline toward 80% overall / 90-95% per module targets

**Triggers**:
- Push to `master` or version branches
- Pull requests
- Manual workflow dispatch

**Jobs**:

1. **coverage-report**
   - Runs all unit tests with JaCoCo instrumentation
   - Generates aggregate coverage report
   - Extracts instruction, branch, and complexity metrics
   - Checks against baseline (75%) and target (80%)
   - Generates per-module coverage analysis
   - Comments on PR with coverage changes

2. **integration-coverage**
   - Separate coverage for integration tests
   - Tracks integration test quality independently

**Artifacts Generated**:
- `jacoco-coverage-reports` - Detailed HTML coverage reports
- `module-coverage-summary` - Markdown table of per-module coverage

**Coverage Enforcement**:
- **Baseline**: 75% (instruction, branch, complexity)
- **Target**: 80% overall, 90-95% per critical module
- Build fails if coverage drops below baseline
- PR comments highlight modules needing improvement

**Module Status Indicators**:
- ✅ **Target Met**: >= 90% coverage
- ⚠️ **Needs Improvement**: 75-89% coverage
- ❌ **Below Baseline**: < 75% coverage

## GitHub Actions vs Jenkins Comparison

| Feature | Jenkins (Current) | GitHub Actions (New) |
|---------|------------------|---------------------|
| Trigger | Polling, webhooks, cron | Native GitHub events |
| PR Builds | Incremental via gitflow-incremental-builder | Same + validation job |
| Security Scanning | Manual OWASP runs | Automated OWASP, CodeQL, secret scanning |
| Coverage Tracking | JaCoCo reports | JaCoCo + trend analysis + PR comments |
| Multi-platform | Linux only | Matrix builds (Linux, Windows, macOS) |
| Artifact Storage | Jenkins server | GitHub Artifacts (90-day retention) |
| Secrets Management | Jenkins credentials | GitHub Secrets (encrypted) |
| Notifications | Slack plugin | GitHub Actions + future integrations |
| Build Cache | Workspace-based | GitHub Actions cache |
| Deployment | Custom Maven deploy | Maven deploy + GitHub Releases |
| Cost | Self-hosted infrastructure | Free for public repos, usage-based for private |

## Configuration Requirements

### GitHub Secrets

The following secrets must be configured in repository settings:

1. **MAVEN_USERNAME** - Maven repository username for artifact deployment
2. **MAVEN_PASSWORD** - Maven repository password/token
3. **DOCKERHUB_USERNAME** - Docker Hub username (if building containers)
4. **DOCKERHUB_TOKEN** - Docker Hub access token

To add secrets:
1. Navigate to repository Settings → Secrets and variables → Actions
2. Click "New repository secret"
3. Add each secret with exact name matching above

### Branch Protection Rules

Recommended branch protection for `master`:

1. Require pull request reviews (1-2 reviewers)
2. Require status checks to pass before merging:
   - `Build and Test / full-build`
   - `Security Scanning / security-summary`
   - `Test Coverage Analysis / coverage-report`
3. Require branches to be up to date before merging
4. Require conversation resolution before merging
5. Require signed commits (optional, recommended for DO-278)

### Dependabot Configuration

Create `.github/dependabot.yml` for automated dependency updates:

```yaml
version: 2
updates:
  - package-ecosystem: "maven"
    directory: "/"
    schedule:
      interval: "weekly"
    open-pull-requests-limit: 10
    reviewers:
      - "alliance-maintainers"
    labels:
      - "dependencies"
      - "security"
```

## DO-278 Compliance Alignment

The GitHub Actions workflows support DO-278 requirements:

### Configuration Management (CM)
- ✅ All builds tagged with git SHA
- ✅ Reproducible builds via Maven caching
- ✅ Artifact versioning and retention
- ✅ Change tracking via PR workflow

### Quality Assurance (QA)
- ✅ Automated code quality checks (checkstyle, formatting)
- ✅ Required PR reviews before merge
- ✅ Test execution mandatory for all changes
- ✅ Coverage enforcement (baseline)

### Verification & Validation (V&V)
- ✅ Test results captured and archived
- ✅ Coverage reports with traceability
- ✅ Security analysis as verification step
- ✅ Multi-level testing (unit, integration, e2e planned)

### Security
- ✅ Automated vulnerability scanning
- ✅ Secret detection
- ✅ License compliance verification
- ✅ Dependency review for supply chain security

## Migration Timeline

### Phase 1: Foundation (COMPLETE)
- ✅ GitHub Actions workflows created
- ✅ Documentation written
- ⏳ Secrets configuration (requires admin access)

### Phase 2: Parallel Operation (1-2 weeks)
- [ ] Configure GitHub secrets
- [ ] Enable workflows on repository
- [ ] Run both Jenkins and GitHub Actions
- [ ] Validate parity in build results
- [ ] Address any discrepancies

### Phase 3: Transition (1 week)
- [ ] Update README.md with GitHub Actions badges
- [ ] Set branch protection rules
- [ ] Enable Dependabot
- [ ] Communicate change to contributors
- [ ] Make GitHub Actions primary CI

### Phase 4: Decommission (1 week)
- [ ] Archive Jenkins build history
- [ ] Disable Jenkins jobs (keep as backup)
- [ ] Remove Jenkinsfile references from docs
- [ ] Update CLAUDE.md to reflect GitHub Actions as primary

## Testing the Workflows Locally

You can test workflows locally using [act](https://github.com/nektos/act):

```bash
# Install act
curl https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash

# Run the build workflow
act push -W .github/workflows/build.yml

# Run specific job
act -j full-build

# Use specific event
act pull_request
```

**Note**: Some actions may not work perfectly with `act` (e.g., setup-java caching), but it's useful for quick validation.

## Rollback Plan

If critical issues arise with GitHub Actions:

1. Re-enable Jenkins pipeline (Jenkinsfile still present)
2. Update branch protection to require Jenkins jobs
3. Investigate and fix GitHub Actions issues
4. Test fixes in feature branch
5. Re-enable GitHub Actions when validated

## Monitoring and Metrics

### Success Metrics
- Build time comparison (Jenkins vs GitHub Actions)
- Security vulnerability detection rate
- Coverage trend (should increase toward 80%)
- PR feedback time (should decrease)
- False positive rate in security scans

### Dashboards
- GitHub Actions tab: Workflow runs and status
- Security tab: CodeQL alerts and Dependabot alerts
- Insights → Dependency graph: Dependency tree and vulnerabilities
- Insights → Community: Standards checklist

## Troubleshooting

### Common Issues

**Issue**: Maven dependency download slow in GitHub Actions
**Solution**: Maven dependencies cached, but first run is slow. Use larger runner if needed.

**Issue**: OWASP NVD database download timeout
**Solution**: Cache is implemented. If timeout persists, use centralized NVD server.

**Issue**: Test failures in GitHub Actions but pass locally
**Solution**: Check for environment-specific issues (timezone, locale, temp directories). GitHub Actions runs in clean Ubuntu container.

**Issue**: Coverage report not generated
**Solution**: Ensure `jacoco-maven-plugin` executions are in `pom.xml`. Check for module-specific opt-outs.

## Support and Questions

For questions about the GitHub Actions migration:
1. Review this document and CLAUDE.md
2. Check workflow run logs in Actions tab
3. Create issue with `ci-cd` label
4. Reference specific workflow run URL

## References

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [GitHub Actions for Maven](https://docs.github.com/en/actions/automating-builds-and-tests/building-and-testing-java-with-maven)
- [CodeQL Documentation](https://codeql.github.com/docs/)
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)
- [DO-278 Standards](https://www.rtca.org/content/publications) (subscription required)
