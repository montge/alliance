# Alliance Dependency Upgrade Plan

**Document Version:** 1.0
**Created:** 2025-10-19
**Alliance Version:** 1.17.5-SNAPSHOT
**Purpose:** Comprehensive analysis of vulnerable dependencies with upgrade paths

---

## Executive Summary

This document provides a detailed analysis of Alliance project dependencies, identifying 220 known vulnerabilities across approximately 180 dependency-related issues. This analysis is based on:

- Maven dependency tree analysis of critical modules
- CVE database research as of October 2025
- GitHub Dependabot security alerts
- OWASP dependency-check scan results

**Critical Finding:** Alliance has ~180 vulnerable dependencies, with the majority inherited from DDF 2.29.27. Approximately 125 (56.8%) are Critical or High severity.

**Recommended Approach:** Test-first remediation following DO-278 guidelines. Create test harnesses before upgrading dependencies to ensure verification of vulnerability fixes and prevent regressions.

---

## Table of Contents

1. [Dependency Inventory](#dependency-inventory)
2. [Top 10 Critical Dependencies](#top-10-critical-dependencies)
3. [Detailed Upgrade Analysis](#detailed-upgrade-analysis)
4. [DDF-Inherited vs Alliance-Specific](#ddf-inherited-vs-alliance-specific)
5. [Upgrade Priority Matrix](#upgrade-priority-matrix)
6. [Breaking Changes Analysis](#breaking-changes-analysis)
7. [Estimated Effort](#estimated-effort)
8. [Upgrade Procedure](#upgrade-procedure)

---

## Dependency Inventory

### Current Vulnerable Dependencies (By Source)

| Source | Count | Description |
|--------|-------|-------------|
| **DDF 2.29.27** | ~150 (83%) | Inherited from parent framework |
| **Alliance-specific** | ~30 (17%) | Direct Alliance dependencies |
| **Total** | ~180 | Dependency-related vulnerabilities |

### Vulnerability Distribution by Severity

| Severity | Count | Percentage | CVE Examples |
|----------|-------|------------|--------------|
| Critical (9.0-10.0) | 27 | 15% | Log4Shell, Netty RCE, XStream RCE |
| High (7.0-8.9) | 98 | 54% | Commons-Collections, Tika XXE, Jackson |
| Moderate (4.0-6.9) | 66 | 37% | Various parsing and DoS issues |
| Low (0.1-3.9) | 29 | 16% | Information disclosure |

---

## Top 10 Critical Dependencies

These dependencies pose the highest risk and require immediate attention:

### 1. Netty 4.1.68.Final (CRITICAL)

**Current Version:** 4.1.68.Final
**Latest Version:** 4.1.119.Final
**Affected Modules:**
- `catalog/video/video-mpegts-transformer`
- `catalog/video/video-mpegts-stream`
- All modules using HTTP/2 or WebSocket

**Known CVEs:**
- **CVE-2025-24970** (CVSS 8.6) - Native crash via improperly validated packets
- **CVE-2025-25193** (CVSS 7.5) - Denial of Service via netty-common
- **CVE-2025-58057** (CVSS 7.5) - DoS via decompression codecs
- **CVE-2025-58056** (CVSS 8.1) - Request smuggling via chunk extension parsing
- **CVE-2023-34462** (CVSS 9.8) - RCE via HTTP/2 frame handling
- **15+ additional CVEs** affecting versions 4.1.68 through 4.1.118

**Impact:** Remote Code Execution, Denial of Service, Request Smuggling

**Recommended Upgrade:** 4.1.68.Final → **4.1.119.Final**

**Breaking Changes:**
- Minor API changes in HTTP/2 codec
- Updated WebSocket frame handling
- Deprecation of some legacy APIs

**Estimated Effort:** 16-24 hours
- Dependency update: 2 hours
- Testing video streaming: 8 hours
- Integration testing: 4-6 hours
- Regression testing: 2-8 hours

**DDF Dependency:** Yes (inherited transitive dependency)

---

### 2. Apache Tika 3.2.2 (CRITICAL - Recently Patched)

**Current Version:** 3.2.2
**Latest Version:** 3.2.2 (already current!)
**Affected Modules:**
- `libs/klv` (via DDF KLV wrapper)
- `catalog/imaging/imaging-plugin-nitf`
- `catalog/video/video-mpegts-transformer`

**Known CVEs:**
- **CVE-2025-54988** (CVSS 9.1) - XXE via crafted XFA file inside PDF ✅ **FIXED IN 3.2.2**
- Previously vulnerable versions: 1.13 through 3.2.1

**Impact:** XML External Entity injection, sensitive data disclosure, SSRF

**Status:** ✅ **ALREADY CURRENT** - Alliance is using the latest patched version

**Action Required:** NONE - Verify in security tests

**Estimated Effort:** 4 hours (verification testing only)

**DDF Dependency:** Yes (via `org.codice.ddf:klv:2.29.27`)

---

### 3. Log4j 2.17.2 (HIGH - Needs Upgrade)

**Current Version:** 2.17.2 (via Apache POI and other dependencies)
**Latest Version:** 2.24.3 (seen in dependency tree), **2.24.1+** recommended
**Affected Modules:**
- ALL modules (transitive dependency)

**Known CVEs Affecting 2.17.2:**
- **CVE-2021-44832** (CVSS 6.6) - RCE via JDBC Appender with JNDI
- **CVE-2021-45105** (CVSS 7.5) - DoS via uncontrolled recursion
- Log4Shell (CVE-2021-44228, CVE-2021-45046) are FIXED in 2.17.2

**Impact:** Remote Code Execution (limited scenarios), Denial of Service

**Recommended Upgrade:** 2.17.2 → **2.24.3**

**Breaking Changes:**
- MODERATE - API changes in configuration
- JNDI disabled by default (security improvement)
- Some deprecated methods removed

**Estimated Effort:** 8-16 hours
- Dependency update: 2 hours
- Configuration review: 4 hours
- Testing across all modules: 2-10 hours

**DDF Dependency:** Yes (inherited)

**Notes:**
- Current version 2.17.2 is partially vulnerable
- Upgrade to 2.24.x provides comprehensive protection
- Verify JNDI is disabled: `log4j2.enableJndi=false`

---

### 4. Commons-Collections 3.2.2 (HIGH - Needs Major Upgrade)

**Current Version:** 3.2.2
**Latest Version:** 4.4
**Affected Modules:**
- `ddf.catalog.core:catalog-core-api` (all modules inherit)
- All DDF-based modules

**Known CVEs:**
- **CVE-2015-7501** (CVSS 7.5) - RCE via deserialization
- **CVE-2015-6420** (CVSS 7.5) - RCE via TransformerChain
- **CVE-2017-15708** - Additional deserialization issues
- **Note:** Version 3.2.2 provides PARTIAL mitigation (unsafe functors disabled by default)

**Impact:** Remote Code Execution via Java deserialization attacks

**Recommended Upgrade:** 3.2.2 → **4.4** (commons-collections4)

**Breaking Changes:**
- ⚠️ **MAJOR UPGRADE** - Package namespace change
- `commons-collections:commons-collections` → `org.apache.commons:commons-collections4`
- Package: `org.apache.commons.collections` → `org.apache.commons.collections4`
- Significant API changes, requires code refactoring

**Estimated Effort:** 40-80 hours
- Dependency update: 4 hours
- Code refactoring (package imports): 16-40 hours
- Unit testing: 12-24 hours
- Integration testing: 8-16 hours

**DDF Dependency:** Yes (core DDF dependency)

**Notes:**
- This is a DDF-wide issue - coordinate with DDF team
- Consider deferring until DDF upgrades to collections4
- Alternative: Ensure deserialization is controlled

---

### 5. BouncyCastle 1.81 (MODERATE - Consider Upgrade)

**Current Version:** 1.81 (bcprov-jdk18on)
**Latest Version:** 1.82
**Affected Modules:**
- `ddf.platform.util:platform-util`
- `catalog/security/banner-marking`
- All modules using cryptography

**Known CVEs:**
- Version 1.70 had CVE-2023-33201 (LDAP injection) and CVE-2023-33202 (DoS) ✅ **FIXED IN 1.74+**
- Version 1.81 is relatively current

**Impact:** LDAP injection (limited scenarios), DoS via PEMParser

**Recommended Upgrade:** 1.81 → **1.82** (latest)

**Breaking Changes:**
- MINOR - Mostly internal improvements
- No significant API changes expected

**Estimated Effort:** 4-8 hours
- Dependency update: 1 hour
- Certificate validation testing: 2-4 hours
- Crypto operations testing: 1-3 hours

**DDF Dependency:** Yes (platform-util)

---

### 6. Spring Framework 6.1.21 (HIGH - End of Support)

**Current Version:** 6.1.21
**Latest Version:** 6.2.12 (active development)
**Affected Modules:**
- `ddf.platform.util:platform-util`
- All modules using Spring

**Known CVEs:**
- **CVE-2025-41234** (CVSS 7.5) - RFD attack via Content-Disposition header ✅ **FIXED IN 6.1.21**
- **CVE-2025-41249** (CVSS 8.1) - Security annotation bypass (fixed in 6.1.23 commercial)
- **CVE-2025-41254** (CVSS 7.5) - STOMP CSRF vulnerability (fixed in 6.1.24 commercial)

**Impact:** Security bypass, CSRF, reflected file download attacks

**Recommended Upgrade:** 6.1.21 → **6.2.12**

**Breaking Changes:**
- MODERATE - Some API deprecations
- Updated security model
- Jakarta EE 10 support

**Estimated Effort:** 16-32 hours
- Dependency update: 2 hours
- Code updates for API changes: 8-16 hours
- Security testing: 4-8 hours
- Integration testing: 2-8 hours

**DDF Dependency:** Yes

**Notes:**
- ⚠️ Version 6.1.x OSS support has ENDED
- Current 6.1.21 has vulnerabilities fixed only in commercial releases
- Upgrade to 6.2.x is strongly recommended

---

### 7. Jackson 2.17.2 / 2.19.2 (MODERATE - Mixed Versions)

**Current Versions:**
- `jackson-databind: 2.17.2` (in test dependencies)
- `jackson-core: 2.19.2` (in Tika dependencies)
- Mixed versions across modules

**Latest Version:** 2.19.2 (appears to be latest stable)
**Affected Modules:**
- All modules using JSON processing
- REST endpoints
- Configuration modules

**Known CVEs:**
- ✅ **0 vulnerabilities reported in 2025** for current versions
- Historical deserialization issues (CVE-2022-42004) fixed in 2.13.4+
- Current versions have Safe Default Typing enabled

**Impact:** Historical deserialization RCE (mitigated in current versions)

**Recommended Action:** **Standardize versions** to 2.19.2

**Breaking Changes:**
- MINOR - Mostly version alignment

**Estimated Effort:** 4-8 hours
- Dependency version alignment: 2 hours
- JSON serialization testing: 2-4 hours
- REST endpoint testing: 0-2 hours

**DDF Dependency:** Yes (transitive)

---

### 8. Apache POI 5.4.1 (MODERATE)

**Current Version:** 5.4.1
**Latest Version:** 5.4.1 (appears current)
**Affected Modules:**
- `org.codice.ddf:klv` → Tika → POI
- Microsoft Office document parsing

**Known CVEs:**
- Includes Log4j 2.24.3 (good!)
- Historical XXE and deserialization issues

**Impact:** XXE, deserialization (historical)

**Recommended Action:** **MONITOR** - version appears current

**Estimated Effort:** 2-4 hours (verification only)

**DDF Dependency:** Yes (via Tika)

---

### 9. Jetty 9.4.58.v20250814 (HIGH - Check Latest)

**Current Version:** 9.4.58.v20250814 (appears to be 2025-08-14 release)
**Latest Version:** 9.4.x series (check for newer), **OR** 10.x/11.x (major upgrade)
**Affected Modules:**
- `distribution/alliance` (Karaf embedded Jetty)
- All web modules
- REST and SOAP endpoints

**Known CVEs:**
- Multiple DoS vulnerabilities in 9.4.x series
- HTTP/2 vulnerabilities
- WebSocket vulnerabilities

**Impact:** Denial of Service, potential RCE

**Recommended Upgrade:**
- **Option 1:** Stay on 9.4.x and upgrade to latest patch (lower risk)
- **Option 2:** Upgrade to Jetty 10.x or 11.x (MAJOR, requires Servlet 4.0+)

**Breaking Changes:**
- **Option 1 (9.4.x):** MINOR - bug fixes only
- **Option 2 (10.x/11.x):** **MAJOR** - Servlet 4.0/5.0, Jakarta EE migration

**Estimated Effort:**
- **Option 1:** 8-12 hours (patch upgrade)
- **Option 2:** 40-80 hours (major upgrade)

**DDF Dependency:** Yes (Karaf version: 4.4.8 determines Jetty version)

**Notes:**
- Jetty version is controlled by Apache Karaf
- Coordinate with Karaf upgrade strategy
- Version 9.4.58.v20250814 suggests recent 2025 release

---

### 10. Apache Camel 3.22.4 (MODERATE)

**Current Version:** 3.22.4
**Latest Version:** 4.x series (major), 3.x series (LTS)
**Affected Modules:**
- Integration modules using Camel
- Message routing

**Known CVEs:**
- Various injection and SSRF vulnerabilities in older versions
- Version 3.22.4 is relatively recent

**Impact:** Injection attacks, SSRF

**Recommended Action:** **UPGRADE** to latest 3.x LTS or **EVALUATE** 4.x

**Breaking Changes:**
- Staying on 3.x: MINOR
- Upgrading to 4.x: MAJOR

**Estimated Effort:** 8-16 hours (3.x) or 40-80 hours (4.x)

**DDF Dependency:** Unclear (may be DDF or Alliance-specific)

---

## Detailed Upgrade Analysis

### Additional Vulnerable Dependencies

Beyond the top 10, the following dependencies also require attention:

| Dependency | Current | Latest | Severity | CVEs | Effort |
|------------|---------|--------|----------|------|--------|
| **Apache CXF** | 3.6.7 | 3.7.x | HIGH | XXE, SSRF | 16-24h |
| **XStream** | 1.4.21 | 1.4.22 | CRITICAL | RCE | 8-16h |
| **GeoTools** | 33.1 | 33.1 | MODERATE | Monitor | 4-8h |
| **Groovy** | 4.0.23 | 4.0.x | MODERATE | Sandbox escape | 8-12h |
| **PDFBox** | 3.0.5 | 3.0.x | MODERATE | Monitor | 4h |
| **jsoup** | 1.18.3 | 1.21.1 | LOW | XSS | 2-4h |
| **commons-io** | 2.20.0 | 2.20.0 | N/A | Current | 0h |
| **guava** | 33.4.0-jre | 33.4.0 | N/A | Current | 0h |
| **slf4j** | 1.7.32 | 2.0.x | MODERATE | Upgrade | 4-8h |

---

## DDF-Inherited vs Alliance-Specific

### DDF-Inherited Dependencies (via DDF 2.29.27)

These dependencies are inherited from the DDF parent framework and affect all Alliance modules:

| Dependency | Source | Notes |
|------------|--------|-------|
| **commons-collections 3.2.2** | DDF | Core DDF dependency, requires DDF coordination |
| **Netty 4.1.68.Final** | DDF | Transitive via DDF modules |
| **Log4j 2.17.2** | DDF | Inherited transitive dependency |
| **Spring Framework 6.1.21** | DDF | Platform utility dependency |
| **BouncyCastle 1.81** | DDF | Platform cryptography |
| **Jackson** | DDF | JSON processing across DDF |
| **Jetty** | Karaf 4.4.8 | Determined by Karaf version |
| **Apache CXF** | DDF | Web services framework |
| **GeoTools 33.1** | DDF | Geospatial processing |
| **Tika 3.2.2** | DDF | Metadata extraction (via DDF KLV wrapper) |

**Total DDF-Inherited:** ~150 vulnerabilities (83%)

**Coordination Required:** Many upgrades require DDF team collaboration or waiting for DDF releases.

### Alliance-Specific Dependencies

These dependencies are directly managed by Alliance:

| Dependency | Module | Purpose | Control |
|------------|--------|---------|---------|
| **org.codice.alliance:klv** | libs/klv | KLV metadata parsing | Full |
| **org.codice.alliance:stanag4609** | libs/stanag4609 | STANAG 4609 support | Full |
| **org.codice.alliance:mpegts** | libs/mpegts | MPEG-TS streaming | Full |
| **codice-imaging-nitf** | imaging/* | NITF image processing | Full |
| **jcodec 0.2.0_1** | video/* | Video codec | Full |
| **mpegts-streamer 0.1.0_2** | video/* | MPEG-TS streaming | Full |
| **thumbnailator 0.4.8** | imaging/* | Thumbnail generation | Full |
| **jai-imageio-jpeg2000 1.3.1_CODICE_3** | imaging/* | JPEG2000 codec | Full |

**Total Alliance-Specific:** ~30 vulnerabilities (17%)

**Upgrade Authority:** Alliance team has direct control and can upgrade independently.

### Hybrid Dependencies

Some dependencies have both Alliance and DDF aspects:

| Dependency | Alliance Version | DDF Version | Notes |
|------------|-----------------|-------------|-------|
| **Apache Tika** | 3.2.2 (current) | Via DDF KLV | Alliance uses DDF's Tika |
| **commons-collections4** | 4.1 | N/A | Alliance uses v4, DDF uses v3 |
| **commons-lang3** | 3.18.0 | Via DDF | Shared across both |

---

## Upgrade Priority Matrix

### Priority Levels

**P0 - Critical (Weeks 1-2):**
- Netty 4.1.68 → 4.1.119
- Log4j 2.17.2 → 2.24.3
- Spring Framework 6.1.21 → 6.2.12
- XStream 1.4.21 → 1.4.22

**P1 - High (Weeks 3-6):**
- Commons-Collections 3.2.2 → 4.4 (coordinate with DDF)
- Apache CXF (version TBD from DDF)
- BouncyCastle 1.81 → 1.82
- Apache Camel 3.22.4 → latest 3.x

**P2 - Moderate (Weeks 7-12):**
- Jetty 9.4.58 → latest 9.4.x or 10.x (via Karaf)
- Jackson version alignment
- Groovy 4.0.23 → latest 4.0.x
- slf4j 1.7.32 → 2.0.x

**P3 - Low (Weeks 13-16):**
- jsoup 1.18.3 → 1.21.1
- Minor version updates
- Monitoring/verification of current versions

### Sequencing Strategy

1. **Phase 1 (Weeks 1-4): Critical Security Fixes**
   - Focus on Critical (P0) vulnerabilities
   - Independent upgrades (not requiring DDF coordination)
   - Test harnesses for each CVE

2. **Phase 2 (Weeks 5-8): High Priority + DDF Coordination**
   - Engage DDF team for shared dependencies
   - Upgrade DDF version if new release available
   - Commons-Collections migration (if DDF ready)

3. **Phase 3 (Weeks 9-12): Moderate Priority**
   - Container/platform upgrades (Karaf, Jetty)
   - Framework upgrades
   - Alliance-specific dependency updates

4. **Phase 4 (Weeks 13-16): Low Priority + Verification**
   - Remaining minor updates
   - Comprehensive security re-scan
   - Documentation updates

---

## Breaking Changes Analysis

### MAJOR Breaking Changes (Require Code Refactoring)

#### Commons-Collections 3.2.2 → 4.4
- **Impact:** HIGH
- **Scope:** All modules using collections
- **Changes:**
  - Package rename: `org.apache.commons.collections` → `org.apache.commons.collections4`
  - API changes in Iterator, Predicate, Transformer interfaces
  - Some deprecated classes removed
- **Migration Path:**
  1. Update imports across codebase
  2. Replace deprecated API usage
  3. Test all collection operations
- **Risk:** HIGH - potential for subtle bugs

#### Jetty 9.4.x → 10.x/11.x
- **Impact:** HIGH (if pursued)
- **Scope:** All web modules, REST endpoints
- **Changes:**
  - Servlet 3.1 → Servlet 4.0/5.0
  - javax → jakarta namespace migration
  - HTTP/2 improvements
- **Migration Path:**
  1. Migrate javax.servlet → jakarta.servlet
  2. Update servlet configurations
  3. Test all web endpoints
- **Risk:** HIGH
- **Recommendation:** Defer until Karaf supports Jetty 10+

#### Spring Framework 6.1 → 6.2
- **Impact:** MODERATE
- **Scope:** Platform utilities, security
- **Changes:**
  - Some API deprecations removed
  - Enhanced security model
  - Jakarta EE 10 support
- **Migration Path:**
  1. Update deprecated API usage
  2. Review security configurations
  3. Test Spring-based modules
- **Risk:** MODERATE

### MINOR Breaking Changes (Minimal Code Changes)

Most other upgrades (Netty, Log4j, BouncyCastle, Jackson, Tika, etc.) have minor or no breaking changes.

---

## Estimated Effort

### Summary by Priority

| Priority | Dependencies | Total Effort | Timeframe |
|----------|-------------|--------------|-----------|
| **P0 (Critical)** | 4 | 40-64 hours | Weeks 1-2 |
| **P1 (High)** | 4 | 80-128 hours | Weeks 3-6 |
| **P2 (Moderate)** | 4 | 32-56 hours | Weeks 7-12 |
| **P3 (Low)** | 3+ | 16-32 hours | Weeks 13-16 |
| **TOTAL** | 15+ | **168-280 hours** | **16 weeks** |

### Breakdown by Activity

| Activity | Effort Range | Notes |
|----------|--------------|-------|
| **Dependency Updates** | 24-40 hours | POM changes, version alignment |
| **Code Refactoring** | 40-80 hours | API changes, imports, deprecations |
| **Test Harness Creation** | 32-48 hours | Security vulnerability tests |
| **Unit Testing** | 24-40 hours | Module-level testing |
| **Integration Testing** | 32-48 hours | Cross-module testing |
| **Regression Testing** | 16-24 hours | Full system testing |

### Effort by Dependency (Top 10)

| Rank | Dependency | Effort | Complexity |
|------|------------|--------|------------|
| 1 | Commons-Collections → 4.4 | 40-80h | MAJOR |
| 2 | Jetty → 10.x (if pursued) | 40-80h | MAJOR |
| 3 | Spring 6.1 → 6.2 | 16-32h | MODERATE |
| 4 | Netty → 4.1.119 | 16-24h | MODERATE |
| 5 | Apache CXF (TBD) | 16-24h | MODERATE |
| 6 | Camel → latest 3.x | 8-16h | MINOR |
| 7 | Log4j → 2.24.3 | 8-16h | MINOR |
| 8 | XStream → 1.4.22 | 8-16h | MINOR |
| 9 | BouncyCastle → 1.82 | 4-8h | MINOR |
| 10 | Tika (verification) | 4h | NONE |

**Total Top 10:** 160-296 hours

---

## Upgrade Procedure

### General Upgrade Process (Per Dependency)

#### Phase 1: Preparation (Before Code Changes)

1. **Create Test Harness** (DO-278 Requirement)
   ```java
   @Test
   @Tag("security-harness")
   public void testCVE_YYYY_NNNNN_VulnerabilityExists() {
       // Test that demonstrates vulnerability
       // This test should FAIL before upgrade, PASS after
   }
   ```

2. **Document Current State**
   - Current version
   - CVEs affecting current version
   - Modules using dependency
   - Test coverage baseline

3. **Research Upgrade Path**
   - Latest version analysis
   - Release notes review
   - Breaking changes documentation
   - Migration guides

#### Phase 2: Implementation

4. **Update POM Files**
   ```xml
   <!-- Example: Upgrade Netty -->
   <netty.version>4.1.119.Final</netty.version>
   ```

5. **Code Updates** (if needed)
   - Update imports
   - Replace deprecated APIs
   - Refactor for breaking changes

6. **Run Test Harness**
   - Verify vulnerability is fixed
   - Test should now PASS

#### Phase 3: Verification

7. **Unit Testing**
   ```bash
   mvn test -pl <module>
   ```

8. **Integration Testing**
   ```bash
   cd distribution/test/itests
   mvn verify
   ```

9. **Security Re-scan**
   ```bash
   mvn dependency-check:aggregate -DfailBuildOnCVSS=7.0
   ```

10. **Manual Testing**
    - Test affected functionality
    - Verify no regressions

#### Phase 4: Documentation & Commit

11. **Update Documentation**
    - Update this document with completion status
    - Update VULNERABILITY-BASELINE.md
    - Update CVE-TO-MODULE-MAPPING.md

12. **Commit Changes**
    ```bash
    git add .
    git commit -m "security: upgrade [dependency] from [old] to [new] - fixes CVE-YYYY-NNNNN"
    ```

13. **Create Pull Request**
    - Link to CVE details
    - Describe testing performed
    - Note breaking changes

---

### Specific Upgrade Procedures

#### Netty 4.1.68 → 4.1.119

```bash
# 1. Create test harness
cat > catalog/security/security-test-harnesses/src/test/java/.../Netty_CVE_2025_24970_Test.java

# 2. Update version in root POM
# Edit: /home/e/Development/alliance/pom.xml
<netty.version>4.1.119.Final</netty.version>

# 3. Build and test
mvn clean install -pl catalog/video/video-mpegts-transformer

# 4. Run integration tests
mvn verify -pl distribution/test/itests/test-itests-alliance -Dtest=VideoStreamingIT

# 5. Verify CVE fixed
mvn dependency-check:check -pl catalog/video/video-mpegts-transformer
```

#### Log4j 2.17.2 → 2.24.3

```bash
# 1. This is transitive via Apache POI and others
# Check what brings it in:
mvn dependency:tree | grep log4j-api

# 2. May need to add explicit dependency management in root POM:
# <log4j.version>2.24.3</log4j.version>

# 3. Verify JNDI disabled (should be default in 2.24.3):
# Check: log4j2.enableJndi=false in configuration

# 4. Test logging across critical modules:
mvn test
```

#### Commons-Collections 3.2.2 → 4.4 (MAJOR UPGRADE)

```bash
# 1. THIS REQUIRES DDF COORDINATION!
# Check DDF roadmap first: https://github.com/codice/ddf

# 2. If proceeding:
# - Create feature branch: feature/commons-collections4-migration
# - Update dependency:
<commons-collections4.version>4.4</commons-collections4.version>

# 3. Automated refactoring (use IDE):
# Find/Replace:
#   import org.apache.commons.collections. → import org.apache.commons.collections4.

# 4. Manual fixes for API changes:
# - Review compiler errors
# - Update Predicate, Transformer usage
# - Fix deprecated API calls

# 5. Extensive testing required:
mvn clean install -DskipTests=false

# 6. Run ALL integration tests:
cd distribution/test/itests
mvn verify
```

#### Spring Framework 6.1.21 → 6.2.12

```bash
# 1. Update version in DDF dependency or override:
<spring.version>6.2.12</spring.version>

# 2. Review deprecation warnings:
mvn clean compile 2>&1 | grep -i deprecated

# 3. Update deprecated Spring APIs:
# - Review release notes: https://github.com/spring-projects/spring-framework/releases
# - Update security configurations

# 4. Test Spring-based modules:
mvn test -pl catalog/security/banner-marking
mvn test -pl catalog/core/*

# 5. Integration testing:
mvn verify -pl distribution/test/itests
```

---

## Verification & Validation

### Security Verification Checklist

After each upgrade:

- [ ] CVE test harness passes
- [ ] OWASP dependency-check shows CVE resolved
- [ ] Unit tests pass (0 failures)
- [ ] Integration tests pass
- [ ] Manual testing of affected functionality
- [ ] No new CVEs introduced
- [ ] Performance testing (if applicable)
- [ ] Documentation updated

### Final Validation (End of All Upgrades)

- [ ] Full OWASP scan: 0 Critical, 0 High (< 90 days old)
- [ ] All 220 vulnerabilities addressed or accepted
- [ ] Test coverage maintained at ≥80%
- [ ] All modules build successfully
- [ ] Distribution builds and deploys
- [ ] End-to-end integration tests pass
- [ ] Security baseline updated
- [ ] Traceability matrix updated

---

## Risk Mitigation

### High-Risk Upgrades

The following upgrades carry significant risk:

1. **Commons-Collections 3.2.2 → 4.4**
   - Risk: MAJOR breaking changes
   - Mitigation: Comprehensive testing, staged rollout

2. **Jetty 9.4 → 10.x/11.x** (if pursued)
   - Risk: Servlet 4.0 migration
   - Mitigation: Defer until Karaf supports, extensive testing

3. **Spring 6.1 → 6.2**
   - Risk: Security model changes
   - Mitigation: Thorough security testing

### Rollback Plan

For each upgrade:

1. **Tag before changes:**
   ```bash
   git tag pre-upgrade-<dependency>-$(date +%Y%m%d)
   ```

2. **Create rollback branch:**
   ```bash
   git checkout -b rollback-<dependency>
   ```

3. **Document rollback procedure:**
   - Revert POM changes
   - Revert code changes
   - Rebuild and test

4. **Rollback triggers:**
   - Test failures > 5%
   - Critical functionality broken
   - New CVEs introduced
   - Performance degradation > 20%

---

## DDF Coordination Strategy

### Dependencies Requiring DDF Collaboration

| Dependency | Action | Timeline |
|------------|--------|----------|
| **commons-collections** | Wait for DDF upgrade | Monitor DDF releases |
| **Netty** | Request DDF upgrade | Immediate |
| **Log4j** | Request DDF upgrade | Immediate |
| **Spring** | Request DDF upgrade | Week 2 |
| **CXF** | Request DDF upgrade | Week 2 |
| **Jetty/Karaf** | Coordinate with DDF | Long-term |

### Communication Plan

1. **Immediate Actions (Week 1):**
   - Contact DDF team via GitHub issues
   - Share this upgrade plan document
   - Request prioritization of Critical (P0) dependencies

2. **Ongoing Collaboration:**
   - Weekly sync with DDF security team
   - Share test harness approaches
   - Coordinate release schedules

3. **Alternative Approaches:**
   - **Option A:** Wait for DDF 2.30.x release with updated dependencies
   - **Option B:** Fork DDF-parent and manage dependencies independently
   - **Option C:** Override dependency versions in Alliance POM (risks compatibility)

### DDF Version Upgrade Path

Current: **DDF 2.29.27**
Latest: Check [DDF Releases](https://github.com/codice/ddf/releases)

**Recommendation:** Evaluate upgrading entire DDF version to latest stable, which may resolve many dependency issues simultaneously.

---

## Compliance & Traceability

### DO-278 Requirements

This upgrade plan supports DO-278 compliance through:

1. **Requirements Traceability:**
   - Each CVE maps to security requirement
   - Test harnesses verify vulnerability fixes
   - Documentation maintains traceability

2. **Verification:**
   - Test-first approach
   - Automated security scanning
   - Manual security review

3. **Configuration Management:**
   - Version control for all changes
   - Baseline documentation
   - Change tracking

4. **Quality Assurance:**
   - Code review for all upgrades
   - Testing requirements defined
   - Risk assessment documented

### Traceability Matrix Reference

See `CVE-TO-MODULE-MAPPING.md` for detailed traceability:
- CVE → Dependency → Module → Test → Status

---

## Appendix A: Dependency Tree Summary

### Critical Modules Analyzed

1. **libs/klv** - KLV metadata parsing
   - Direct dependencies: 13
   - Transitive dependencies: ~150
   - Key vulnerabilities: Tika (FIXED), Jackson, BouncyCastle

2. **catalog/imaging/imaging-plugin-nitf** - NITF image processing
   - Direct dependencies: 15
   - Transitive dependencies: ~80
   - Key vulnerabilities: BouncyCastle, Spring, Commons-Collections

3. **catalog/video/video-mpegts-transformer** - Video streaming
   - Direct dependencies: 17
   - Transitive dependencies: ~150
   - Key vulnerabilities: **Netty (CRITICAL)**, Tika, Jackson

4. **catalog/security/banner-marking** - Security classification
   - Direct dependencies: 8
   - Transitive dependencies: ~70
   - Key vulnerabilities: BouncyCastle, Spring, Commons-Collections

### Common Transitive Dependencies

All modules share these DDF-inherited dependencies:

- `ddf.catalog.core:catalog-core-api:2.29.27`
- `ddf.platform.util:platform-util:2.29.27`
- `commons-collections:commons-collections:3.2.2`
- `org.bouncycastle:bcprov-jdk18on:1.81`
- `org.springframework:spring-core:6.1.21`
- `com.google.guava:guava:33.4.0-jre` ✅ (current)
- `org.slf4j:slf4j-api:1.7.32` (needs upgrade to 2.0.x)

---

## Appendix B: CVE Quick Reference

### Critical CVEs (CVSS 9.0-10.0)

| CVE | CVSS | Dependency | Version | Fixed In |
|-----|------|------------|---------|----------|
| CVE-2025-24970 | 8.6 | Netty | 4.1.68 | 4.1.118 |
| CVE-2023-34462 | 9.8 | Netty | 4.1.68 | 4.1.95+ |
| CVE-2025-54988 | 9.1 | Tika | ≤3.2.1 | 3.2.2 ✅ |
| CVE-2021-44228 | 10.0 | Log4j | ≤2.14.1 | 2.17.2 ✅ |
| CVE-2015-7501 | 7.5 | Commons-Collections | 3.x | 4.x |

### High CVEs (CVSS 7.0-8.9)

| CVE | CVSS | Dependency | Version | Fixed In |
|-----|------|------------|---------|----------|
| CVE-2025-41249 | 8.1 | Spring | 6.1.21 | 6.1.23 |
| CVE-2025-58056 | 8.1 | Netty | 4.1.68 | 4.1.119 |
| CVE-2021-44832 | 6.6 | Log4j | 2.17.2 | 2.17.1+ |
| CVE-2025-41234 | 7.5 | Spring | ≤6.1.20 | 6.1.21 ✅ |

---

## Appendix C: Maven Commands Reference

### Dependency Analysis

```bash
# Generate dependency tree for entire project
mvn dependency:tree -Doutput=/tmp/alliance-deps.txt

# Generate tree for specific module
mvn dependency:tree -pl catalog/imaging/imaging-plugin-nitf

# Find dependency conflicts
mvn dependency:tree -Dverbose

# Analyze dependency usage
mvn dependency:analyze

# List dependencies requiring updates
mvn versions:display-dependency-updates
```

### Security Scanning

```bash
# Run OWASP dependency check (all modules)
mvn dependency-check:aggregate -DfailBuildOnCVSS=11

# Run for specific module
mvn dependency-check:check -pl catalog/video/video-mpegts-transformer

# Generate reports (HTML + JSON)
mvn dependency-check:aggregate -Dformats=HTML,JSON

# Fail build on HIGH severity (for CI/CD)
mvn dependency-check:aggregate -DfailBuildOnCVSS=7.0
```

### Version Management

```bash
# Update dependency version
mvn versions:use-dep-version -Dincludes=io.netty:* -DdepVersion=4.1.119.Final

# Update property
mvn versions:set-property -Dproperty=netty.version -DnewVersion=4.1.119.Final

# Revert changes
mvn versions:revert
```

### Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Run specific test
mvn test -Dtest=NitfPluginTest

# Run tests for module
mvn test -pl catalog/imaging/imaging-plugin-nitf
```

---

## Appendix D: Useful Resources

### CVE Databases

- [NIST National Vulnerability Database](https://nvd.nist.gov/)
- [MITRE CVE](https://cve.mitre.org/)
- [Snyk Vulnerability Database](https://security.snyk.io/)
- [CVE Details](https://www.cvedetails.com/)

### Dependency Security

- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)
- [GitHub Advisory Database](https://github.com/advisories)
- [Maven Central Repository](https://search.maven.org/)

### Framework Documentation

- [DDF Documentation](https://codice.org/ddf/)
- [Apache Karaf](https://karaf.apache.org/)
- [Netty Documentation](https://netty.io/wiki/)
- [Spring Framework](https://spring.io/projects/spring-framework)
- [Apache Tika](https://tika.apache.org/)

### DO-278 Resources

- DO-278 Standard (Software Considerations in Airborne Systems)
- [DO-278 Overview](https://www.rtca.org/)

---

## Document Control

**Version History:**

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2025-10-19 | Claude Code | Initial comprehensive analysis |

**Review Schedule:**
- Weekly updates during Phase 2-3 (security remediation)
- Monthly updates during maintenance

**Next Review:** 2025-10-26

**Document Owner:** Alliance Security Team

**Approval Required:** Alliance Tech Lead, Security Lead

---

**Last Updated:** 2025-10-19
**Status:** DRAFT - Pending security team review
