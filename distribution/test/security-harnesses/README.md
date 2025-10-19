# Alliance Security Test Harness Framework

## Table of Contents

1. [Overview](#overview)
2. [Philosophy: Test-First Security](#philosophy-test-first-security)
3. [DO-278 Compliance](#do-278-compliance)
4. [Directory Structure](#directory-structure)
5. [Quick Start](#quick-start)
6. [Writing Security Tests](#writing-security-tests)
7. [Test Harness Architecture](#test-harness-architecture)
8. [Vulnerability Categories](#vulnerability-categories)
9. [Running Security Tests](#running-security-tests)
10. [Interpreting Test Results](#interpreting-test-results)
11. [Fixing Vulnerabilities](#fixing-vulnerabilities)
12. [CVE Tracking and Traceability](#cve-tracking-and-traceability)
13. [Best Practices](#best-practices)
14. [Examples](#examples)
15. [Troubleshooting](#troubleshooting)
16. [References](#references)

---

## Overview

The Alliance Security Test Harness Framework provides comprehensive infrastructure for testing and documenting security vulnerabilities in the Alliance project. This framework follows DO-278 test-driven development principles to ensure:

- **Regression Prevention**: Security tests prevent reintroduction of vulnerabilities
- **Fix Verification**: Tests validate that security fixes are effective
- **Documentation**: Security improvements are documented with evidence
- **Compliance**: Verification and validation requirements are met

### Key Features

- **Test-First Development**: Write tests that demonstrate vulnerabilities BEFORE implementing fixes
- **Comprehensive Coverage**: Test harnesses for XXE, SQL injection, command injection, deserialization, weak crypto, and more
- **DO-278 Alignment**: Full traceability from vulnerabilities to tests to fixes
- **Payload Generation**: Standardized malicious payload generators for reproducible testing
- **Security Annotations**: `@SecurityTest`, `@CVE`, `@OWASP` annotations for metadata tracking
- **Safe Testing**: Controlled test environments with timeouts and resource limits

---

## Philosophy: Test-First Security

### The Problem with Fix-First Approaches

Traditional security remediation follows this pattern:

1. Security scan identifies vulnerability
2. Developer implements fix immediately
3. Vulnerability reappears later (no regression tests)
4. No verification that fix actually works

### The Test-First Security Approach

This framework follows a better pattern:

1. **Demonstrate**: Write test that exploits the vulnerability
2. **Document**: Annotate with CVE, OWASP category, severity
3. **Fix**: Implement security fix with confidence
4. **Verify**: Test passes, proving fix is effective
5. **Protect**: Regression test prevents reintroduction

### Example Workflow

```java
// STEP 1: Write test that demonstrates XXE vulnerability (test will initially pass)
@Test
@SecurityTest(description = "XXE file disclosure", severity = "CRITICAL")
@CVE("CVE-2024-12345")
@Ignore("Demonstrates vulnerability - remove after fix")
public void testXxeFileDisclosure() throws Exception {
    // Arrange: Create malicious XXE payload
    String xxePayload = VulnerabilityTestUtils.createXxePayload("file:///etc/passwd");

    // Act: Process with vulnerable parser
    String result = processXmlWithVulnerableParser(xxePayload);

    // Assert: Verify exploitation succeeded (proves vulnerability exists)
    assertVulnerabilityExists(result, Pattern.compile("root:x:0:0"));
}

// STEP 2: Implement fix (configure safe XML parser)

// STEP 3: Update test to verify fix
@Test
@SecurityTest(description = "Verify XXE fix", expectVulnerability = false)
@CVE("CVE-2024-12345")
public void testXxeFixed() throws Exception {
    String xxePayload = VulnerabilityTestUtils.createXxePayload("file:///etc/passwd");

    // Should throw exception or return safe result
    assertThrowsOnMaliciousInput(xxePayload,
        this::processXmlWithSafeParser,
        SAXException.class);
}
```

---

## DO-278 Compliance

### Requirements Traceability

This framework supports DO-278 requirements traceability:

```
Requirement → Test → Implementation → Verification
    ↓            ↓           ↓              ↓
  SEC-001    testXxe()   SafeParser   Test passes
```

### Documentation Artifacts

The framework generates artifacts required for DO-278:

1. **Test Plans**: Each test harness documents test objectives and procedures
2. **Test Cases**: Individual test methods with clear pass/fail criteria
3. **Test Results**: JUnit reports with pass/fail status
4. **Traceability Matrix**: CVE annotations link vulnerabilities to tests
5. **Verification Evidence**: Test execution logs prove fixes work

### Process Compliance

The test-first approach satisfies DO-278 verification requirements:

- **Objective Evidence**: Tests provide objective evidence of security posture
- **Repeatability**: Standardized payloads ensure reproducible testing
- **Independence**: Tests can be executed independently of production code
- **Coverage**: Test harnesses cover all security vulnerability categories

---

## Directory Structure

```
security-harnesses/
├── README.md                           # This file
├── common/                             # Shared test infrastructure
│   ├── SecurityTestBase.java          # Base class for all security tests
│   ├── VulnerabilityTestUtils.java    # Payload generation utilities
│   └── PayloadGenerator.java          # Advanced payload generators (future)
│
├── xxe/                                # XXE vulnerability tests
│   ├── XxeVulnerabilityTest.java      # XXE test harness
│   └── xxe-payloads/                  # Sample XXE payloads
│       ├── basic-file-read.xml
│       ├── billion-laughs.xml
│       ├── ssrf-aws-metadata.xml
│       └── parameter-entity.xml
│
├── deserialization/                    # Deserialization tests
│   ├── DeserializationVulnerabilityTest.java
│   └── malicious-objects/             # Sample serialized objects
│
├── injection/                          # Injection vulnerability tests
│   ├── SqlInjectionTest.java
│   ├── LdapInjectionTest.java
│   └── CommandInjectionTest.java
│
├── crypto/                             # Cryptography tests
│   ├── WeakCryptoTest.java
│   └── InsecureRngTest.java
│
└── buffer-overflow/                    # Buffer overflow tests
    └── BufferOverflowTest.java
```

---

## Quick Start

### Prerequisites

- Java 8 or later
- Maven 3.1.0+
- JUnit 4.x
- Alliance development environment

### Running Your First Security Test

```bash
# 1. Navigate to security harnesses directory
cd /home/e/Development/alliance/distribution/test/security-harnesses

# 2. Run XXE tests (currently most will be @Ignored)
mvn test -Dtest=XxeVulnerabilityTest

# 3. To run a specific test (removing @Ignore first):
mvn test -Dtest=XxeVulnerabilityTest#testXxeFileDisclosureEtcPasswd

# 4. Run all security tests
mvn test
```

### Understanding Initial Results

**IMPORTANT**: Most tests are initially marked with `@Ignore` because they demonstrate vulnerabilities. This prevents CI/CD failures until fixes are implemented.

To run a test:
1. Remove the `@Ignore` annotation
2. Run the test
3. Test should PASS (demonstrating vulnerability exists)
4. Implement security fix
5. Update test to verify fix
6. Test should PASS (demonstrating vulnerability is fixed)

---

## Writing Security Tests

### Step 1: Extend SecurityTestBase

All security tests should extend `SecurityTestBase`:

```java
package org.codice.alliance.test.security.harness.xxe;

import org.codice.alliance.test.security.harness.common.SecurityTestBase;
import org.codice.alliance.test.security.harness.common.VulnerabilityTestUtils;
import org.junit.Test;

public class MySecurityTest extends SecurityTestBase {

    @Test
    @SecurityTest(description = "My vulnerability test", severity = "HIGH")
    public void testMyVulnerability() throws Exception {
        // Test implementation
    }
}
```

### Step 2: Use Security Annotations

Annotate tests with security metadata:

```java
@Test
@SecurityTest(
    description = "XXE file disclosure via external entity",
    severity = "CRITICAL",
    expectVulnerability = true
)
@CVE(
    value = "CVE-2024-12345",
    url = "https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2024-12345",
    description = "XXE vulnerability in XML parser"
)
@OWASP(
    category = "A05:2021-Security Misconfiguration",
    year = "2021"
)
@Ignore("Test demonstrates vulnerability - remove @Ignore after fix")
public void testXxeVulnerability() {
    // Test code
}
```

### Step 3: Generate Malicious Payload

Use `VulnerabilityTestUtils` for standardized payload generation:

```java
// XXE payload
String xxePayload = VulnerabilityTestUtils.createXxePayload("file:///etc/passwd");

// SQL injection payload
String sqlInjection = VulnerabilityTestUtils.createSqlAuthBypass();

// Command injection payload
String cmdInjection = VulnerabilityTestUtils.createCommandInjectionUnix("cat /etc/passwd");

// Path traversal payload
String pathTraversal = VulnerabilityTestUtils.createPathTraversal("etc/passwd", 5);
```

### Step 4: Execute Vulnerable Code

Demonstrate the vulnerability by executing vulnerable code:

```java
// Example: Unsafe XML parsing
DocumentBuilder unsafeParser = createUnsafeXmlParser();
Document document = parseXml(xxePayload, unsafeParser);
String result = document.getDocumentElement().getTextContent();
```

### Step 5: Assert Vulnerability Exists

Use assertion methods to verify exploitation:

```java
// Assert using regex pattern
assertVulnerabilityExists(result, Pattern.compile("root:x:0:0"));

// Assert using string marker
assertVulnerabilityExists(result, "SENSITIVE_DATA");

// Assert exception is NOT thrown (unsafe handling)
// (The test succeeding means the vulnerability exists)
```

### Step 6: Document Expected Behavior

Add clear documentation:

```java
/**
 * Tests XXE file disclosure vulnerability.
 *
 * <p><strong>Expected Initial Result:</strong> PASS - Vulnerability exists
 * <p><strong>Expected After Fix:</strong> Exception thrown or empty result
 *
 * <p><strong>Remediation:</strong> Configure XML parser to disable external entities:
 * <pre>{@code
 * factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
 * factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
 * }</pre>
 */
```

---

## Test Harness Architecture

### SecurityTestBase

Base class providing:

- **Lifecycle Management**: `@Before` and `@After` hooks for setup/cleanup
- **Timeout Protection**: Default 30-second timeout prevents hanging tests
- **Temporary File Management**: Automatic cleanup of test files
- **CVE Tracking**: Maps CVE IDs to test methods
- **Assertion Helpers**: `assertVulnerabilityExists()`, `assertVulnerabilityFixed()`
- **Parser Helpers**: `createUnsafeXmlParser()`, `createSafeXmlParser()`

### VulnerabilityTestUtils

Utility class providing:

- **XXE Payloads**: `createXxePayload()`, `createBillionLaughsAttack()`
- **SQL Injection**: `createSqlAuthBypass()`, `createSqlUnionSelect()`
- **Command Injection**: `createCommandInjectionUnix()`, `createCommandInjectionWindows()`
- **Path Traversal**: `createPathTraversal()`, `createPathTraversalDoubleEncoded()`
- **Deserialization**: `createSerializedObject()`, `createMalformedSerializedObject()`
- **SSRF**: `createSsrfPayload()`, `createSsrfAwsMetadata()`
- **Cryptography**: `createWeakKey()`, cryptographic weakness detection

### Security Annotations

#### @SecurityTest

Marks a test as security-specific:

```java
@SecurityTest(
    description = "Brief description of vulnerability",
    severity = "CRITICAL|HIGH|MEDIUM|LOW",
    expectVulnerability = true|false
)
```

#### @CVE

Links test to a CVE identifier:

```java
@CVE(
    value = "CVE-2024-12345",
    url = "https://cve.mitre.org/...",
    description = "CVE description"
)
```

#### @OWASP

Maps test to OWASP Top 10 category:

```java
@OWASP(
    category = "A03:2021-Injection",
    year = "2021"
)
```

---

## Vulnerability Categories

### 1. XXE (XML External Entity)

**Test Harness**: `xxe/XxeVulnerabilityTest.java`

**Attack Vectors**:
- File disclosure (`file:///etc/passwd`)
- SSRF (`http://internal-service/`)
- XML bombs (Billion Laughs, Quadratic Blowup)
- Parameter entities
- Error-based exfiltration

**Remediation**:
```java
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
factory.setExpandEntityReferences(false);
```

### 2. SQL Injection

**Test Harness**: `injection/SqlInjectionTest.java`

**Attack Vectors**:
- Authentication bypass (`' OR '1'='1'--`)
- Data extraction (`' UNION SELECT ...`)
- Time-based blind (`' OR SLEEP(5)--`)

**Remediation**:
```java
// Use PreparedStatement instead of string concatenation
String query = "SELECT * FROM users WHERE username=? AND password=?";
PreparedStatement stmt = conn.prepareStatement(query);
stmt.setString(1, username);
stmt.setString(2, password);
```

### 3. Command Injection

**Test Harness**: `injection/CommandInjectionTest.java`

**Attack Vectors**:
- Shell metacharacter injection (`; cat /etc/passwd`)
- Command chaining (`&& malicious-command`)
- Output redirection (`> /tmp/output`)

**Remediation**:
```java
// Use ProcessBuilder with argument array (no shell interpretation)
ProcessBuilder pb = new ProcessBuilder("command", "arg1", "arg2");
// Do NOT use Runtime.exec() with string concatenation
```

### 4. Deserialization

**Test Harness**: `deserialization/DeserializationVulnerabilityTest.java`

**Attack Vectors**:
- Malicious serialized objects
- Gadget chain exploitation
- Remote code execution

**Remediation**:
```java
// Use ObjectInputStream with custom deserialization filter
ObjectInputStream ois = new ObjectInputStream(input) {
    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        // Whitelist allowed classes
        if (!allowedClasses.contains(desc.getName())) {
            throw new InvalidClassException("Unauthorized deserialization attempt");
        }
        return super.resolveClass(desc);
    }
};
```

### 5. Weak Cryptography

**Test Harness**: `crypto/WeakCryptoTest.java`

**Vulnerabilities Tested**:
- Weak algorithms (DES, RC4, MD5)
- Insufficient key sizes (< 256-bit for AES)
- Insecure modes (ECB)
- Weak random number generation

**Remediation**:
```java
// Use strong algorithms
Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
KeyGenerator keyGen = KeyGenerator.getInstance("AES");
keyGen.init(256); // 256-bit key

// Use secure random
SecureRandom random = SecureRandom.getInstanceStrong();
```

---

## Running Security Tests

### Run All Security Tests

```bash
# From Alliance root
mvn test -pl distribution/test/security-harnesses

# From security-harnesses directory
mvn test
```

### Run Specific Test Suite

```bash
# XXE tests only
mvn test -Dtest=XxeVulnerabilityTest

# SQL injection tests only
mvn test -Dtest=SqlInjectionTest

# All injection tests
mvn test -Dtest=*InjectionTest
```

### Run Individual Test

```bash
mvn test -Dtest=XxeVulnerabilityTest#testXxeFileDisclosureEtcPasswd
```

### Run with Increased Timeout

Some security tests (especially DoS tests) may need longer timeouts:

```bash
mvn test -Dsurefire.timeout=300
```

### Run Non-Ignored Tests Only

```bash
# This will run only tests without @Ignore
mvn test
```

### Run Ignored Tests (Vulnerability Demonstrations)

```bash
# Run specific ignored test by removing @Ignore in code first
# Or use custom JUnit runner configuration
```

---

## Interpreting Test Results

### Test Status Meanings

#### ✅ PASS (Initial Phase - Vulnerability Exists)

```
Test: testXxeFileDisclosure
Status: PASSED
Meaning: Vulnerability successfully demonstrated
Action: Implement security fix
```

When a vulnerability demonstration test PASSES, it means:
- The exploit payload was processed
- The vulnerability exists
- You should implement a fix

#### ❌ FAIL (Initial Phase - Unexpected)

```
Test: testXxeFileDisclosure
Status: FAILED
Meaning: Vulnerability does not exist (good!)
Action: Verify and update test expectations
```

If a vulnerability demonstration test FAILS unexpectedly:
- The vulnerability may already be fixed
- The test may be incorrect
- Verify the actual behavior and update test

#### ✅ PASS (After Fix - Vulnerability Fixed)

```
Test: testXxeFixed
Status: PASSED
Meaning: Security fix is effective
Action: None - maintain regression test
```

After implementing a fix, the verification test should PASS, confirming:
- The fix prevents exploitation
- The vulnerability is remediated
- Regression protection is in place

### Understanding Logs

Security tests generate detailed logs:

```
INFO  - === Starting security test: testXxeFileDisclosure ===
WARN  - Security test harness active - this test may demonstrate exploitable vulnerabilities
INFO  - Test tracks CVE: CVE-2024-12345
WARN  - Generating XXE payload targeting: file:///etc/passwd
DEBUG - Parsed content: root:x:0:0:root:/root:/bin/bash...
ERROR - SECURITY ISSUE: Vulnerability still exists!
INFO  - === Completed security test: testXxeFileDisclosure ===
```

**Key Log Levels**:
- `INFO`: Test lifecycle events
- `WARN`: Security-relevant actions (payload generation, vulnerability detection)
- `ERROR`: Security issues detected
- `DEBUG`: Detailed test data (payloads, results)

---

## Fixing Vulnerabilities

### The Fix Workflow

```
1. Run vulnerability demonstration test (should PASS, showing vuln exists)
2. Implement security fix in production code
3. Update test to verify fix (remove @Ignore, change assertions)
4. Run verification test (should PASS, showing vuln is fixed)
5. Commit both fix and test together
```

### Example: Fixing XXE

**Step 1: Demonstration test exists**
```java
@Test
@SecurityTest(description = "XXE file disclosure", severity = "CRITICAL")
@Ignore("Demonstrates vulnerability")
public void testXxeFileDisclosure() {
    String result = parseXmlUnsafely(xxePayload);
    assertVulnerabilityExists(result, PASSWD_PATTERN); // Currently passes
}
```

**Step 2: Implement fix in production code**
```java
// In production XML parser factory
public class XmlParserFactory {
    public static DocumentBuilder createSafeParser() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder();
    }
}
```

**Step 3: Update test to verify fix**
```java
@Test
@SecurityTest(description = "Verify XXE is fixed", expectVulnerability = false)
public void testXxeFixed() {
    // Should throw exception or return safe result
    assertThrowsOnMaliciousInput(
        xxePayload,
        payload -> parseXmlSafely(payload),
        SAXException.class
    );
}
```

**Step 4: Commit together**
```bash
git add production/XmlParserFactory.java
git add distribution/test/security-harnesses/xxe/XxeVulnerabilityTest.java
git commit -m "fix: prevent XXE attacks in XML parsing

- Configure XML parsers to disable external entity resolution
- Add regression tests to prevent reintroduction

Fixes: CVE-2024-12345
Test: testXxeFixed()"
```

---

## CVE Tracking and Traceability

### CVE Annotation Usage

Link tests to specific CVEs for traceability:

```java
@Test
@CVE(
    value = "CVE-2024-12345",
    url = "https://nvd.nist.gov/vuln/detail/CVE-2024-12345",
    description = "XXE vulnerability in Alliance XML transformer"
)
public void testCve202412345() {
    // Test implementation
}
```

### Generating Traceability Report

The framework tracks CVE-to-test mappings. Access via:

```java
// In test tearDown or report generation
Map<String, List<String>> cveMapping = cveToTestMapping;
// CVE-2024-12345 -> [testXxeFileDisclosure, testXxeFixed]
```

### OWASP Top 10 Mapping

Map vulnerabilities to OWASP categories:

```java
@OWASP(category = "A03:2021-Injection")       // SQL/Command/LDAP injection
@OWASP(category = "A05:2021-Security Misconfiguration")  // XXE
@OWASP(category = "A02:2021-Cryptographic Failures")     // Weak crypto
@OWASP(category = "A08:2021-Software and Data Integrity Failures") // Deserialization
```

---

## Best Practices

### 1. Always Test-First

✅ **DO**: Write vulnerability demonstration test before fixing
```java
@Test
@Ignore("Demonstrates vulnerability - remove after fix")
public void testXxeVulnerability() {
    // Demonstrates vulnerability exists
    assertVulnerabilityExists(result, pattern);
}
```

❌ **DON'T**: Fix vulnerability without test
```java
// No test exists - vulnerability may return!
```

### 2. Use Descriptive Test Names

✅ **DO**: Clear, specific test names
```java
public void testXxeFileDisclosureEtcPasswd()
public void testSqlInjectionAuthBypass()
public void testCommandInjectionUnixCatPasswd()
```

❌ **DON'T**: Vague test names
```java
public void testSecurity()
public void testXxe()
```

### 3. Document Expected Behavior

✅ **DO**: Comprehensive JavaDoc
```java
/**
 * Tests XXE file disclosure via external entity reference.
 *
 * <p><strong>Expected Initial:</strong> PASS (vulnerability exists)
 * <p><strong>Expected After Fix:</strong> Exception or safe result
 * <p><strong>Remediation:</strong> Disable external entities in XML parser
 */
```

### 4. Use Standardized Payloads

✅ **DO**: Use VulnerabilityTestUtils
```java
String payload = VulnerabilityTestUtils.createXxePayload("file:///etc/passwd");
```

❌ **DON'T**: Hardcode payloads
```java
String payload = "<?xml version=\"1.0\"?><!DOCTYPE foo [<!ENTITY...";
```

### 5. Annotate with Metadata

✅ **DO**: Full annotations
```java
@Test
@SecurityTest(description = "...", severity = "CRITICAL")
@CVE("CVE-2024-12345")
@OWASP(category = "A05:2021-Security Misconfiguration")
```

### 6. Clean Up Resources

✅ **DO**: Use provided helpers
```java
File tempFile = createTempFile("test", ".xml");
// Automatically cleaned up in tearDown()
```

### 7. Set Appropriate Timeouts

✅ **DO**: Use @Ignore for DoS tests initially
```java
@Test
@Ignore("May cause memory exhaustion")
public void testBillionLaughsAttack() { ... }
```

---

## Examples

### Example 1: Complete XXE Test

```java
package org.codice.alliance.test.security.harness.xxe;

import static org.hamcrest.MatcherAssert.assertThat;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import org.codice.alliance.test.security.harness.common.SecurityTestBase;
import org.codice.alliance.test.security.harness.common.VulnerabilityTestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

public class MyXxeTest extends SecurityTestBase {

    private static final Pattern PASSWD_PATTERN = Pattern.compile("root:x:0:0");

    @Test
    @SecurityTest(description = "XXE file disclosure", severity = "CRITICAL")
    @CVE("CVE-2024-12345")
    @OWASP(category = "A05:2021-Security Misconfiguration")
    @Ignore("Demonstrates vulnerability - remove after fix")
    public void testXxeFileDisclosure() throws Exception {
        // Arrange
        String xxePayload = VulnerabilityTestUtils.createXxePayload("file:///etc/passwd");

        // Act
        DocumentBuilder parser = createUnsafeXmlParser();
        Document doc = parseXml(xxePayload, parser);
        String result = doc.getDocumentElement().getTextContent();

        // Assert
        assertVulnerabilityExists(result, PASSWD_PATTERN);
    }

    @Test
    @SecurityTest(description = "Verify XXE fix", expectVulnerability = false)
    @CVE("CVE-2024-12345")
    public void testXxeFixed() throws Exception {
        String xxePayload = VulnerabilityTestUtils.createXxePayload("file:///etc/passwd");

        assertThrowsOnMaliciousInput(
            xxePayload,
            payload -> {
                DocumentBuilder parser = createSafeXmlParser();
                return parseXml(payload, parser).getDocumentElement().getTextContent();
            },
            SAXException.class
        );
    }
}
```

### Example 2: SQL Injection Test

```java
package org.codice.alliance.test.security.harness.injection;

import org.codice.alliance.test.security.harness.common.SecurityTestBase;
import org.codice.alliance.test.security.harness.common.VulnerabilityTestUtils;
import org.junit.Test;

public class MySqlTest extends SecurityTestBase {

    @Test
    @SecurityTest(description = "SQL injection auth bypass", severity = "CRITICAL")
    @OWASP(category = "A03:2021-Injection")
    public void testSqlAuthBypass() {
        // Arrange
        String maliciousUsername = VulnerabilityTestUtils.createSqlAuthBypass();

        // Act
        String query = String.format(
            "SELECT * FROM users WHERE username='%s' AND password='%s'",
            maliciousUsername, "anything"
        );

        // Assert
        assertVulnerabilityExists(query, "OR '1'='1'");
    }
}
```

---

## Troubleshooting

### Problem: Tests are all ignored

**Cause**: Tests are marked with `@Ignore` to prevent CI failures before fixes

**Solution**: Remove `@Ignore` annotation to run specific tests

### Problem: OutOfMemoryError in XML bomb test

**Cause**: Billion Laughs attack successfully consumed all memory

**Solution**:
- This proves the vulnerability exists
- Increase JVM heap: `mvn test -DargLine="-Xmx2g"`
- Implement fix to prevent entity expansion

### Problem: FileNotFoundException in XXE test

**Cause**: Target file doesn't exist on test system

**Solution**:
- Use temporary files: `File temp = createTempFile("test", ".xml")`
- Or skip assertion on systems without target file

### Problem: Test passes but should fail

**Cause**: Vulnerability may already be fixed

**Solution**:
- Verify production code
- Update test expectations (`expectVulnerability = false`)
- Change assertions to `assertVulnerabilityFixed()`

### Problem: Timeout exceeded

**Cause**: Test is taking longer than 30 seconds

**Solution**:
- Increase timeout: `@Rule public Timeout timeout = new Timeout(60, TimeUnit.SECONDS);`
- Or optimize test payload size

---

## References

### DO-278 Resources

- **DO-278A**: Guidelines for Communication, Navigation, Surveillance and Air Traffic Management (CNS/ATM) Systems Software Integrity Assurance
- **DO-178C**: Software Considerations in Airborne Systems and Equipment Certification

### Security Resources

- **OWASP Top 10 2021**: https://owasp.org/Top10/
- **CWE Top 25**: https://cwe.mitre.org/top25/
- **NIST NVD**: https://nvd.nist.gov/

### XXE Resources

- **OWASP XXE Prevention Cheat Sheet**: https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html
- **XXE Payloads**: https://github.com/swisskyrepo/PayloadsAllTheThings/tree/master/XXE%20Injection

### SQL Injection Resources

- **OWASP SQL Injection**: https://owasp.org/www-community/attacks/SQL_Injection
- **SQL Injection Cheat Sheet**: https://www.netsparker.com/blog/web-security/sql-injection-cheat-sheet/

### Deserialization Resources

- **Java Deserialization Cheat Sheet**: https://cheatsheetseries.owasp.org/cheatsheets/Deserialization_Cheat_Sheet.html

---

## Contributing

### Adding New Vulnerability Tests

1. Create test class extending `SecurityTestBase`
2. Add vulnerability demonstration tests with `@Ignore`
3. Add utility methods to `VulnerabilityTestUtils` if needed
4. Document in this README
5. Submit PR with tests (fixes implemented separately)

### Test Naming Convention

- Test classes: `[VulnerabilityType]VulnerabilityTest.java` or `[VulnerabilityType]Test.java`
- Test methods: `test[VulnerabilityType][SpecificAttack]`
- Example: `testXxeFileDisclosureEtcPasswd`

### Documentation Standards

- All test methods must have JavaDoc
- Include "Expected Initial" and "Expected After Fix" sections
- Document remediation approach
- Add CVE/OWASP annotations

---

## License

This test framework is part of the Codice Alliance project and is licensed under the GNU Lesser General Public License v3.

```
Copyright (c) Codice Foundation

This is free software: you can redistribute it and/or modify it under the terms
of the GNU Lesser General Public License as published by the Free Software
Foundation, either version 3 of the License, or any later version.
```

---

## Support and Questions

For questions about the security test harness framework:

1. Review this README thoroughly
2. Check existing tests for examples
3. Consult Alliance development documentation
4. Contact the security team

---

**Version**: 1.0
**Last Updated**: 2025-10-19
**Maintainer**: Alliance Security Team
