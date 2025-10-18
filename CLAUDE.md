# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Codice Alliance is an open source, modular integration framework built on DDF (Distributed Data Framework) with DoD/IC and NATO support capabilities. It provides specialized support for:
- NITF/NSIF (STANAG 4545) imagery ingest and parsing
- STANAG 4609 KLV metadata extraction from FMV clips
- Military/intelligence data cataloging and retrieval

Alliance is built on Apache Karaf for OSGi support and uses Apache Camel and Apache CXF for integration.

## Modernization Strategy (IN PROGRESS)

**CRITICAL CONTEXT:** This project is undergoing a significant modernization effort to align with DO-278 standards (Software Considerations in Airborne Systems and Equipment Certification) and modern DevOps practices.

### Current State vs Target State

**Current State:**
- Jenkins-based CI/CD pipeline (see `Jenkinsfile`)
- JaCoCo coverage at 75% (instruction, branch, complexity)
- Manual security scanning via OWASP dependency-check
- Karaf-based distribution (unzip and run)
- No formal requirements traceability
- Limited platform packaging support

**Target State:**
- GitHub Actions-based CI/CD (GitHub-native approach)
- Test-driven development (TDD) methodology
- 80% overall coverage, 90-95% per module
- DO-278 compliant processes:
  - Requirements documentation and traceability
  - Verification and validation procedures
  - Configuration management
  - Quality assurance processes
- Multi-level test harnesses: unit, integration, end-to-end
- Package manager support (apt, yum, homebrew, chocolatey)
- Security-first approach: test harnesses before fixes
- Multi-platform support: Linux, Windows, macOS, POSIX systems
- Platform independence via Java (already achieved)

### Reference Implementation
See `github.com/montge/stratux` for example of DO-278 approach with test-driven development.

### Key Principles for Development

1. **Test-First Development:**
   - Write tests before fixing bugs or adding features
   - Build comprehensive test harnesses for security issues before remediation
   - All code changes must include corresponding tests

2. **DO-278 Alignment:**
   - Document requirements before implementation
   - Maintain traceability matrices (requirements → design → code → tests)
   - Follow verification and validation procedures
   - All changes require review and approval

3. **Security-First:**
   - Known security issues exist (OWASP scans identify vulnerabilities)
   - DO NOT fix security issues without test coverage first
   - Build test harnesses that demonstrate the vulnerability
   - Then implement fixes with tests proving remediation

4. **Coverage Targets:**
   - Minimum 80% overall code coverage
   - Individual modules should target 90-95%
   - Coverage must include: unit tests, integration tests, end-to-end tests
   - Current baseline is 75% - incremental improvement required

## Build Commands

### Requirements
- **Java 11 or later** (LTS versions recommended)
  - **Minimum**: Java 11 (LTS)
  - **Recommended**: Java 17 (LTS) - per SYS-PLT-002
  - **Supported**: Java 21 (latest LTS)
  - **Tested**: Java 11, 17, 21 in CI/CD
- Maven 3.1.0 or later (3.9.5+ recommended)
- Set `JAVA_HOME` and `MAVEN_OPTS` environment variables

### Standard Build
```bash
mvn install
```

### Parallel Build
Use Maven's parallel build feature to speed up compilation:
```bash
# Using explicit thread count
mvn install -T 8

# Using relative thread count (1.5 threads per core)
mvn install -T 1.5C
```

### Code Formatting
The project uses google-java-format. If the build fails due to formatting violations:
```bash
mvn fmt:format
```

### Skip Static Analysis
To skip checkstyle, formatting, and other static analysis checks:
```bash
mvn install -DskipStatic=true
```

### Running Tests
```bash
# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=ClassName

# Run a single test method
mvn test -Dtest=ClassName#methodName

# Run tests in a specific module
cd catalog/imaging/imaging-plugin-nitf
mvn test
```

### Integration Tests
Integration tests are located in `distribution/test/itests/`. These use Pax Exam for OSGi container testing.

## Architecture

### Module Structure

The project is organized into four main top-level modules:

**1. catalog/**
- Core catalog functionality and APIs
- Imaging support (NITF processing)
- Video support (MPEG-TS and STANAG 4609)
- Security and classification
- DDMS (DoD Discovery Metadata Specification) transformers
- Catalog plugins

**2. libs/**
- Low-level libraries for military data formats
- `stanag4609/` - STANAG 4609 support
- `klv/` - Key-Length-Value metadata parsing
- `mpegts/` - MPEG Transport Stream handling

**3. distribution/**
- Final distributable packages
- Karaf feature definitions
- Alliance branding
- Documentation
- Integration tests (`distribution/test/itests/`)
- Docker support

**4. gitsetup/**
- Git configuration utilities

### Key Architectural Patterns

**OSGi Bundles:**
All modules are packaged as OSGi bundles using maven-bundle-plugin. Blueprint XML files in `src/main/resources/OSGI-INF/blueprint/` define service registrations and dependency injection.

**Karaf Features:**
Application modules contain `features.xml` files defining feature installations. Example locations:
- `catalog/imaging/imaging-app/src/main/resources/features.xml`
- `catalog/video/video-app/src/main/resources/features.xml`

**Metacard Extensions:**
Alliance extends DDF's Metacard system with military-specific metadata types. Core metacard types are in `catalog/core/catalog-core-metacardtypes/`.

**Transformers:**
Input and output transformers convert between data formats:
- `imaging-transformer-nitf` - NITF to Metacard transformation
- `video-mpegts-transformer` - MPEG-TS to Metacard transformation
- `catalog-ddms-transformer` - DDMS XML handling

**Post-Ingest Plugins:**
Plugins implementing `PostIngestPlugin` interface process metacards after ingest:
- `imaging-plugin-nitf/NitfPostIngestPlugin` - Generates thumbnails and overviews for NITF images
- Plugins are registered via OSGi blueprint

**Classification/Security:**
Security markings and classification are handled through:
- `catalog/core/catalog-core-classification-api` - Classification interfaces
- `catalog/security/banner-marking` - Security banner handling
- `video-security` - Video-specific security processing

### Testing

**Unit Tests:**
- Use JUnit 4, Mockito, and Hamcrest
- Spock/Groovy tests supported (test files ending in `*Spec.class`)
- Test dependencies are globally configured in root `pom.xml`

**Integration Tests:**
- Located in `distribution/test/itests/`
- Use Pax Exam for OSGi container testing
- Test common utilities in `test-itests-common/`

### Code Quality

**Required Coverage:**
JaCoCo enforces 75% coverage for INSTRUCTION, BRANCH, and COMPLEXITY metrics (configurable per module).

**Static Analysis:**
- Checkstyle with custom rules from ddf-support
- Google Java Format (google-java-format)
- Error Prone compiler plugin for bug detection
- OWASP dependency checking

**Profiles:**
- `staticAnalysis` - Enabled by default, runs all code quality checks
- `release` - Attaches sources and javadocs
- `owasp-dist` - Scans distribution artifacts for vulnerabilities

## Distribution

After building, the Alliance distribution is available at:
```
distribution/alliance/target/alliance-<version>.zip
```

To run the distribution:
```bash
# Unzip the distribution
unzip distribution/alliance/target/alliance-*.zip

# Run Alliance (Linux/Mac)
./alliance-<version>/bin/alliance

# Run Alliance (Windows)
alliance-<version>\bin\alliance.bat
```

## Development Notes

- Alliance inherits DDF's platform, catalog, security, and UI capabilities
- When adding new data format support, follow the pattern in `catalog/imaging/` or `catalog/video/`
- New OSGi services should be registered via Blueprint XML in `OSGI-INF/blueprint/`
- Karaf features should declare all required bundle dependencies with proper version ranges
- All Java code must follow google-java-format style
- LGPL v3 license headers are required on all source files (enforced by checkstyle)

## CI/CD Migration Roadmap

### Phase 1: GitHub Actions Foundation
**Status:** ✅ COMPLETE

**Completed Tasks:**
1. ✅ Created `.github/workflows/` directory structure
2. ✅ Migrated Jenkins pipeline stages to GitHub Actions:
   - `build.yml` - Main build and test workflow with incremental and full builds
   - `security-scan.yml` - Comprehensive security analysis (OWASP, CodeQL, secrets, licenses)
   - `test-coverage.yml` - Coverage tracking and reporting
3. ✅ Replaced Jenkins-specific functionality:
   - Build triggers → GitHub Actions events (push, PR, schedule)
   - Maven caching → GitHub Actions cache
   - Notifications → GitHub issue creation and PR comments
4. ✅ Set up framework for multi-platform matrix builds
5. ✅ Documented migration in `docs/ci-cd-migration.md`

**Remaining Configuration** (requires repository admin):
- Configure GitHub Secrets (MAVEN_USERNAME, MAVEN_PASSWORD)
- Set up branch protection rules
- Enable Dependabot
- Parallel operation with Jenkins (Jenkinsfile retained for other users)

See `docs/ci-cd-migration.md` for complete details.

### Phase 2: Enhanced Testing Infrastructure
**Status:** NOT STARTED

Tasks:
1. Establish test pyramid structure:
   ```
   distribution/test/
   ├── unit/          # Fast, isolated unit tests (existing in src/test/)
   ├── integration/   # Component integration tests (existing in itests/)
   └── e2e/           # End-to-end system tests (NEW)
   ```

2. Create test harness framework:
   - Security vulnerability test suite
   - Performance regression tests
   - Multi-platform compatibility tests
   - NITF/STANAG format validation tests

3. Increase coverage incrementally:
   - Audit current 75% coverage baseline
   - Identify critical paths lacking coverage
   - Add tests to reach 80% overall
   - Focus on high-risk modules for 90-95% coverage

4. Implement test reporting:
   - JaCoCo reports in GitHub Actions artifacts
   - Coverage trend tracking
   - Failed test notifications

### Phase 3: DO-278 Process Implementation
**Status:** NOT STARTED

**IMPORTANT:** DO-278 requires formal processes beyond just code quality. This is a documentation-heavy phase.

Tasks:
1. Requirements Management:
   - Create `docs/requirements/` structure
   - Document system requirements (high-level capabilities)
   - Document software requirements (module-level specifications)
   - Establish requirements traceability matrix format

2. Configuration Management:
   - Define baseline and change control procedures
   - Version control standards (already using git)
   - Build reproducibility requirements
   - Artifact versioning strategy

3. Quality Assurance:
   - Code review procedures (GitHub PRs already in use)
   - Verification procedures (test execution)
   - Validation procedures (system-level acceptance)
   - Problem reporting and tracking

4. Verification & Validation:
   - Test plans for each requirement
   - Test cases with traceability to requirements
   - Test coverage analysis
   - V&V reports

5. Process Documentation:
   - Software Development Plan (SDP)
   - Software Verification Plan (SVP)
   - Software Configuration Management Plan (SCMP)
   - Software Quality Assurance Plan (SQAP)

6. Java Language Features and Security Audit:
   - **Prerequisite**: Complete Phase 2 (80%+ coverage achieved)
   - Audit codebase for effective use of modern Java features (Java 11-21)
   - Identify opportunities to leverage:
     - Java 11+: Local-Variable Type Inference (var), HTTP Client API, improved Optional
     - Java 17: Pattern Matching, Sealed Classes, Records, Text Blocks
     - Java 21: Virtual Threads, Pattern Matching enhancements, Sequenced Collections
   - Security-critical feature adoption:
     - Strong encapsulation (modules/JPMS where beneficial)
     - Improved cryptographic APIs
     - Security Manager deprecation migration
     - Randomness improvements (SecureRandom enhancements)
   - Performance optimizations:
     - Garbage Collector improvements (G1GC, ZGC considerations)
     - JVM performance monitoring and tuning
     - Compiler optimizations and inlining opportunities
   - Create migration plan for adopting beneficial features
   - Document rationale for Java version minimum (currently 11)

### Phase 4: Package Distribution
**Status:** NOT STARTED

Tasks:
1. Platform-specific packaging:
   - Debian/Ubuntu: Create `.deb` packages
   - RHEL/CentOS: Create `.rpm` packages
   - Windows: Create MSI installers or Chocolatey packages
   - macOS: Create `.pkg` or Homebrew formula

2. Repository setup:
   - APT repository for Debian-based systems
   - YUM repository for RedHat-based systems
   - Chocolatey repository for Windows
   - Homebrew tap for macOS

3. Distribution automation:
   - GitHub Actions workflow for package builds
   - Automated publishing to package repositories
   - Version management across platforms

4. Platform testing:
   - Test installation on Ubuntu LTS versions
   - Test installation on RHEL/CentOS
   - Test installation on Windows 10/11
   - Test installation on macOS (if applicable)

## Security Issue Remediation Process

**CRITICAL:** When addressing security vulnerabilities identified by OWASP scans or other tools:

1. **DO NOT immediately fix the vulnerability**
2. **DO** create a test harness that demonstrates the vulnerability:
   ```
   // Example test structure
   @Test
   public void testXXEVulnerability() {
       // Arrange: Set up malicious XML input
       // Act: Process the input with current code
       // Assert: Verify the vulnerability exists (test should currently fail in a controlled way)
   }
   ```

3. **DO** document the vulnerability:
   - CVE number (if applicable)
   - Affected modules
   - Attack vector
   - Mitigation approach

4. **THEN** implement the fix and verify the test now passes

5. **VERIFY** fix doesn't break existing functionality

This approach ensures:
- Regression tests prevent reintroduction
- Verification of fix effectiveness
- Documentation of security improvements
- Compliance with DO-278 verification requirements

## Current Known Gaps

Based on initial analysis, the following areas need attention:

1. **Missing GitHub Actions workflows** - Current CI is Jenkins-only
2. **No formal requirements documentation** - Required for DO-278
3. **No end-to-end test suite** - Only unit and integration tests exist
4. **No package manager distributions** - Only ZIP distribution exists
5. **Security vulnerabilities present** - OWASP scans identify issues (exact count TBD)
6. **No DO-278 process documentation** - Plans and procedures needed
7. **Coverage gaps** - Need 5-20% coverage increase depending on module

## Next Steps for Contributors

When working on this project:

1. **For new features:**
   - Write requirements documentation first
   - Write tests (TDD approach)
   - Implement feature
   - Update traceability matrix

2. **For bug fixes:**
   - Create failing test demonstrating bug
   - Fix bug
   - Verify test passes

3. **For security issues:**
   - Follow Security Issue Remediation Process above
   - Never commit credentials or secrets

4. **For CI/CD improvements:**
   - Maintain Jenkins pipeline until GitHub Actions is proven
   - Run both in parallel during transition
   - Document differences and migration notes
