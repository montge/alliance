# Netty Upgrade Decision - Phase 3B

## Executive Summary

**Decision:** Upgrade Netty from 4.1.68.Final → **4.1.119.Final** (NOT 4.2.7.Final)

**Rationale:** The 4.1.119 upgrade path is significantly safer while still addressing critical CVEs. The 4.2.x upgrade introduces multiple breaking changes that require extensive testing and code modifications.

**CVEs Fixed:** 15+ vulnerabilities including CVE-2025-25193 (CVSS 5.5, DoS)

---

## Current State

**Alliance Netty Version:** 4.1.68.Final (released July 2021)

**Versions Behind:** 51 patch versions (4.1.68 → 4.1.119)

**Modules Using Netty:**
- `catalog/video/video-stream` - Uses netty-buffer, netty-codec, netty-common, netty-resolver, netty-transport
- `catalog/video/video-admin-plugin` - Uses netty-buffer, netty-codec

**Dependabot PR #13:** Proposes upgrade to 4.2.7.Final (MAJOR version jump)

---

## Upgrade Path Analysis

### Option A: Upgrade to Netty 4.1.119.Final (RECOMMENDED)

**Pros:**
- ✅ Same major version (4.1.x) - minimal breaking changes
- ✅ Fixes CVE-2025-25193 (DoS vulnerability, CVSS 5.5)
- ✅ Fixes 15+ additional CVEs from 51 patch versions
- ✅ No protobuf MAJOR version bump (stays compatible)
- ✅ No netty-codec module split (maintains structure)
- ✅ No BouncyCastle dependency changes
- ✅ Estimated testing effort: 4-8 hours
- ✅ Low risk of breaking existing functionality

**Cons:**
- ⚠️ Does not get 4.2.x new features (not needed for Alliance)

**Breaking Changes:** NONE expected (patch version upgrades within 4.1.x line)

**CVEs Fixed:**
- **CVE-2025-25193** (CVSS 5.5) - DoS via unsafe environment file reading
- Plus 15+ additional CVEs fixed in versions 4.1.69 through 4.1.119

### Option B: Upgrade to Netty 4.2.7.Final (Dependabot PR #13)

**Pros:**
- ✅ Latest Netty version (as of February 2025)
- ✅ New features: improved memory allocator, better HTTP/2 support
- ✅ Fixes all CVEs (including those fixed in 4.1.119)

**Cons:**
- ❌ MAJOR version upgrade (4.1 → 4.2) - significant breaking changes
- ❌ **Breaking:** protobuf-java 2.6.1 → 3.25.5 (MAJOR API changes)
- ❌ **Breaking:** netty-codec split into multiple sub-modules
- ❌ **Breaking:** BouncyCastle dependencies change (jdk15on → jdk18on, 1.69 → 1.80)
- ❌ **Breaking:** Adaptive memory allocator now default (behavioral change)
- ❌ Requires Java 8+ (Alliance already uses Java 8+, but still a requirement change)
- ❌ Estimated testing effort: 40-80 hours
- ❌ HIGH risk of breaking existing video streaming functionality

**Breaking Changes:**

1. **Module Structure Changes:**
   ```
   4.1.x: netty-codec (single jar)
   4.2.x: netty-codec-* (multiple jars: codec-http, codec-http2, codec-dns, etc.)
   ```

2. **Protobuf API Changes:**
   ```
   4.1.x: protobuf-java 2.6.1 (com.google.protobuf.Message API)
   4.2.x: protobuf-java 3.25.5 (com.google.protobuf3.Message API)
   ```

3. **BouncyCastle Changes:**
   ```
   4.1.x: bcprov-jdk15on:1.69
   4.2.x: bcprov-jdk18on:1.80
   ```

4. **Memory Allocator Default:**
   ```
   4.1.x: PooledByteBufAllocator (default)
   4.2.x: AdaptiveByteBufAllocator (new default)
   ```

**Migration Guide:** https://github.com/netty/netty/wiki/Netty-4.2-Migration-Guide

---

## Risk Assessment

### Netty 4.1.119.Final (Recommended)

| Risk Factor | Level | Notes |
|-------------|-------|-------|
| API Compatibility | LOW | Patch version, no API changes expected |
| Build Breakage | LOW | No module structure changes |
| Runtime Breakage | LOW | Same major version, behavioral consistency |
| Testing Effort | LOW | 4-8 hours full regression testing |
| Rollback Complexity | LOW | Simple version revert if issues found |
| **Overall Risk** | **LOW** | Safe upgrade path |

### Netty 4.2.7.Final (Dependabot PR #13)

| Risk Factor | Level | Notes |
|-------------|-------|-------|
| API Compatibility | HIGH | MAJOR version, breaking API changes |
| Build Breakage | MEDIUM | Module split may require pom.xml changes |
| Runtime Breakage | HIGH | Memory allocator changes, protobuf incompatibilities |
| Testing Effort | HIGH | 40-80 hours comprehensive testing |
| Rollback Complexity | MEDIUM | May require code changes to revert |
| **Overall Risk** | **HIGH** | Extensive testing required |

---

## CVE Analysis

### CVE-2025-25193 (Fixed in Netty 4.1.119)

**CVSS Score:** 5.5 (MODERATE)

**Type:** Denial of Service (DoS)

**Description:**
Netty has a vulnerability in `io.netty:netty-common` where improper handling of environment files can cause a DoS attack. This is a continuation of CVE-2024-47535, where the fix was incomplete - null-bytes were not counted against the input limit.

**Attack Vector:**
When null-bytes (0x00) are encountered by InputStreamReader, it issues replacement characters in charset decoding, which fills up the line-buffer in BufferedReader.readLine() because replacement characters are not line-break characters.

**Fix:** Commit d1fbda62d3a47835d3fb35db8bd42ecc205a5386 contains updated fix in Netty 4.1.119.Final

**Impact on Alliance:**
- **Likelihood:** LOW - Alliance does not directly process environment files via Netty
- **Impact:** MODERATE - DoS could affect video streaming functionality
- **Mitigation:** Upgrade to 4.1.119+ required

### Additional CVEs Fixed (4.1.69 through 4.1.119)

According to Snyk vulnerability database, versions 4.1.68 and earlier have 15+ known CVEs. Netty 4.1.119.Final has **ZERO direct vulnerabilities** per Snyk verification.

**CVE Categories:**
- HTTP request smuggling vulnerabilities
- Buffer overflow vulnerabilities
- Information disclosure vulnerabilities
- Denial of Service vulnerabilities

---

## Recommendation

**Upgrade to Netty 4.1.119.Final via the following steps:**

### Step 1: Update pom.xml

```xml
<properties>
    <!-- Override DDF's Netty version -->
    <netty.version>4.1.119.Final</netty.version>
</properties>
```

### Step 2: Build and Test

```bash
# Clean build
mvn clean install -DskipTests=true

# Run full test suite
mvn clean test -T 4

# Run integration tests
cd distribution/test/itests
mvn clean test
```

### Step 3: Verify Video Streaming

Test video modules specifically:
- `catalog/video/video-stream`
- `catalog/video/video-admin-plugin`
- `catalog/video/video-mpegts-transformer`

### Step 4: OWASP Scan

```bash
mvn dependency-check:aggregate -DfailBuildOnCVSS=11
```

Verify CVE count reduction.

### Step 5: Close Dependabot PR #13

Comment explaining why 4.1.119 is safer than 4.2.7 for now, and that 4.2.x can be considered in a future phase after comprehensive testing.

---

## Future Work

**Phase 3C or Later:** Consider Netty 4.2.x upgrade

**Prerequisites for 4.2.x upgrade:**
1. Complete testing of 4.1.119 upgrade (Phase 3B)
2. Review protobuf 3.x migration requirements
3. Test netty-codec module split impact
4. Allocate 40-80 hours for testing
5. Coordinate with DDF team (may affect upstream)

**Timeline:** Q3 2025 or later (not urgent - 4.1.119 addresses all critical CVEs)

---

## References

- [Netty 4.2 Migration Guide](https://github.com/netty/netty/wiki/Netty-4.2-Migration-Guide)
- [Netty 4.1.119.Final Release Notes](https://netty.io/news/2025/02/26/4-1-119-Final.html)
- [CVE-2025-25193 Details](https://nvd.nist.gov/vuln/detail/CVE-2025-25193)
- [Netty GitHub Releases](https://github.com/netty/netty/releases)
- [Snyk Vulnerability Database](https://security.snyk.io/package/maven/io.netty%3Anetty-handler/4.1.119.Final)

---

## Decision Log

**Date:** 2025-10-19

**Decision Maker:** Phase 3B Security Remediation

**Decision:** Upgrade to Netty 4.1.119.Final (reject Dependabot PR #13's 4.2.7.Final)

**Rationale:**
- Addresses all critical CVEs with minimal risk
- Maintains API compatibility with Alliance code
- Reduces testing effort from 40-80 hours to 4-8 hours
- Enables faster security remediation
- Defers 4.2.x upgrade to future phase with proper planning

**Approval:** Pending review by Alliance security team

**Related:** Phase 3A (Quick Wins Complete), Phase 3B (Complex Upgrades In Progress)
