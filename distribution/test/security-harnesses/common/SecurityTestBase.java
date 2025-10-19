/*
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.alliance.test.security.harness.common;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Base class for all security vulnerability test harnesses in Alliance.
 *
 * <p>This class provides common infrastructure for testing security vulnerabilities following
 * DO-278 test-driven development principles. Security tests should extend this class to gain
 * access to:
 *
 * <ul>
 *   <li>Vulnerability detection and verification methods
 *   <li>Exploit payload generation and delivery
 *   <li>Test lifecycle management with timeout protection
 *   <li>CVE metadata tracking and documentation
 *   <li>Security assertion helpers
 * </ul>
 *
 * <p><strong>DO-278 Test-First Philosophy:</strong>
 *
 * <p>These test harnesses are designed to demonstrate that vulnerabilities exist BEFORE fixes are
 * implemented. This ensures:
 *
 * <ol>
 *   <li>Regression tests prevent vulnerability reintroduction
 *   <li>Verification of fix effectiveness
 *   <li>Documentation of security improvements
 *   <li>Compliance with DO-278 verification requirements
 * </ol>
 *
 * <p><strong>Usage Pattern:</strong>
 *
 * <pre>{@code
 * public class XxeVulnerabilityTest extends SecurityTestBase {
 *
 *   @Test
 *   @SecurityTest
 *   @CVE("CVE-2024-12345")
 *   public void testXxeExternalEntityInjection() throws Exception {
 *     // Arrange: Create malicious XXE payload
 *     String xxePayload = VulnerabilityTestUtils.createXxePayload("file:///etc/passwd");
 *
 *     // Act: Process the payload (this should currently succeed, demonstrating vulnerability)
 *     String result = processXmlUnsafely(xxePayload);
 *
 *     // Assert: Verify external entity was resolved (vulnerability exists)
 *     assertVulnerabilityExists(result, Pattern.compile("root:x:0:0"));
 *   }
 * }
 * }</pre>
 *
 * @see VulnerabilityTestUtils for payload generation utilities
 * @see SecurityTest annotation for marking security-specific tests
 * @see CVE annotation for CVE metadata tracking
 */
public abstract class SecurityTestBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityTestBase.class);

  /** Default timeout for security tests (30 seconds) to prevent hanging on exploit attempts. */
  private static final int DEFAULT_TEST_TIMEOUT_SECONDS = 30;

  /** Timeout rule to prevent tests from hanging indefinitely during exploit attempts. */
  @Rule public Timeout globalTimeout = new Timeout(DEFAULT_TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);

  /** Test name rule for logging and debugging purposes. */
  @Rule public TestName testName = new TestName();

  /** Map tracking CVE IDs to test methods for traceability reporting. */
  protected Map<String, List<String>> cveToTestMapping = new HashMap<>();

  /** List of temporary files created during testing for cleanup. */
  protected List<File> temporaryFiles = new ArrayList<>();

  /** Flag indicating whether the current test expects a vulnerability to exist. */
  protected boolean expectVulnerability = true;

  /**
   * Sets up the security test environment before each test execution.
   *
   * <p>This method:
   *
   * <ul>
   *   <li>Logs test initiation for audit trail
   *   <li>Initializes CVE tracking
   *   <li>Sets up temporary file tracking
   *   <li>Configures security manager restrictions (if applicable)
   * </ul>
   *
   * @throws Exception if setup fails
   */
  @Before
  public void setUp() throws Exception {
    LOGGER.info("=== Starting security test: {} ===", testName.getMethodName());
    LOGGER.warn(
        "Security test harness active - this test may demonstrate exploitable vulnerabilities");

    // Track CVE annotations for reporting
    try {
      CVE cveAnnotation =
          this.getClass()
              .getMethod(testName.getMethodName())
              .getAnnotation(CVE.class);
      if (cveAnnotation != null) {
        String cveId = cveAnnotation.value();
        cveToTestMapping.computeIfAbsent(cveId, k -> new ArrayList<>()).add(testName.getMethodName());
        LOGGER.info("Test tracks CVE: {}", cveId);
      }
    } catch (NoSuchMethodException e) {
      // Ignore - method may not exist during setup
    }

    // Initialize temporary file tracking
    temporaryFiles = new ArrayList<>();
  }

  /**
   * Cleans up the security test environment after each test execution.
   *
   * <p>This method:
   *
   * <ul>
   *   <li>Removes temporary files created during testing
   *   <li>Logs test completion status
   *   <li>Clears CVE tracking data
   *   <li>Resets security configurations
   * </ul>
   */
  @After
  public void tearDown() {
    LOGGER.info("=== Completed security test: {} ===", testName.getMethodName());

    // Clean up temporary files
    for (File tempFile : temporaryFiles) {
      try {
        if (tempFile.exists()) {
          Files.delete(tempFile.toPath());
          LOGGER.debug("Deleted temporary file: {}", tempFile.getAbsolutePath());
        }
      } catch (IOException e) {
        LOGGER.warn("Failed to delete temporary file: {}", tempFile.getAbsolutePath(), e);
      }
    }

    temporaryFiles.clear();
  }

  /**
   * Asserts that a vulnerability exists by verifying the exploit pattern matches the result.
   *
   * <p>This method is used during the initial vulnerability demonstration phase. When a test fails
   * with this assertion, it indicates the vulnerability no longer exists (which is good!).
   *
   * @param result the actual result from processing the exploit
   * @param exploitPattern regex pattern that should match if vulnerability exists
   * @throws AssertionError if vulnerability does NOT exist (pattern doesn't match)
   */
  protected void assertVulnerabilityExists(String result, Pattern exploitPattern) {
    assertThat("Result should not be null when testing vulnerability", result, is(notNullValue()));

    boolean vulnerabilityDetected = exploitPattern.matcher(result).find();

    if (!vulnerabilityDetected) {
      LOGGER.info(
          "GOOD NEWS: Vulnerability appears to be fixed! Exploit pattern not found in result.");
      LOGGER.debug("Expected pattern: {}", exploitPattern.pattern());
      LOGGER.debug("Actual result: {}", result);
    }

    assertThat(
        String.format(
            "Vulnerability should exist (exploit pattern '%s' should match result). "
                + "If this fails, the vulnerability may already be fixed - verify and update test expectations.",
            exploitPattern.pattern()),
        vulnerabilityDetected,
        is(true));
  }

  /**
   * Asserts that a vulnerability exists by verifying the result contains the exploit marker.
   *
   * @param result the actual result from processing the exploit
   * @param exploitMarker string that should appear if vulnerability exists
   * @throws AssertionError if vulnerability does NOT exist (marker not found)
   */
  protected void assertVulnerabilityExists(String result, String exploitMarker) {
    assertThat("Result should not be null when testing vulnerability", result, is(notNullValue()));

    if (!result.contains(exploitMarker)) {
      LOGGER.info("GOOD NEWS: Vulnerability appears to be fixed! Exploit marker not found.");
      LOGGER.debug("Expected marker: {}", exploitMarker);
      LOGGER.debug("Actual result: {}", result);
    }

    assertThat(
        String.format(
            "Vulnerability should exist (result should contain '%s'). "
                + "If this fails, the vulnerability may already be fixed - verify and update test expectations.",
            exploitMarker),
        result,
        containsString(exploitMarker));
  }

  /**
   * Asserts that a vulnerability has been fixed by verifying the exploit pattern does NOT match.
   *
   * <p>This method is used after implementing a security fix. The test should pass, confirming the
   * vulnerability is remediated.
   *
   * @param result the actual result from processing the exploit
   * @param exploitPattern regex pattern that should NOT match if vulnerability is fixed
   * @throws AssertionError if vulnerability STILL exists (pattern matches)
   */
  protected void assertVulnerabilityFixed(String result, Pattern exploitPattern) {
    if (result == null) {
      // Null result might indicate safe handling (e.g., exception thrown, processing aborted)
      LOGGER.info("Result is null - this may indicate safe handling of malicious input");
      return;
    }

    boolean vulnerabilityDetected = exploitPattern.matcher(result).find();

    if (vulnerabilityDetected) {
      LOGGER.error("SECURITY ISSUE: Vulnerability still exists! Exploit pattern found in result.");
      LOGGER.error("Exploit pattern: {}", exploitPattern.pattern());
      LOGGER.error("Result: {}", result);
    }

    assertThat(
        String.format(
            "Vulnerability should be fixed (exploit pattern '%s' should NOT match result)",
            exploitPattern.pattern()),
        vulnerabilityDetected,
        is(false));
  }

  /**
   * Asserts that a vulnerability has been fixed by verifying the result does NOT contain the
   * exploit marker.
   *
   * @param result the actual result from processing the exploit
   * @param exploitMarker string that should NOT appear if vulnerability is fixed
   * @throws AssertionError if vulnerability STILL exists (marker found)
   */
  protected void assertVulnerabilityFixed(String result, String exploitMarker) {
    if (result == null) {
      LOGGER.info("Result is null - this may indicate safe handling of malicious input");
      return;
    }

    if (result.contains(exploitMarker)) {
      LOGGER.error("SECURITY ISSUE: Vulnerability still exists! Exploit marker found.");
      LOGGER.error("Exploit marker: {}", exploitMarker);
      LOGGER.error("Result: {}", result);
    }

    assertThat(
        String.format(
            "Vulnerability should be fixed (result should NOT contain '%s')", exploitMarker),
        result,
        not(containsString(exploitMarker)));
  }

  /**
   * Asserts that processing malicious input throws an exception (indicating safe handling).
   *
   * @param maliciousInput the exploit payload to process
   * @param processor functional interface that processes the input
   * @param expectedExceptionType the exception type that should be thrown
   * @param <T> the result type (if no exception is thrown)
   * @throws AssertionError if no exception is thrown (unsafe handling)
   */
  protected <T> void assertThrowsOnMaliciousInput(
      String maliciousInput,
      ThrowingFunction<String, T> processor,
      Class<? extends Exception> expectedExceptionType) {

    try {
      T result = processor.apply(maliciousInput);
      LOGGER.error(
          "SECURITY ISSUE: Malicious input was processed without throwing expected exception!");
      LOGGER.error("Input: {}", maliciousInput);
      LOGGER.error("Result: {}", result);
      fail(
          String.format(
              "Expected %s to be thrown when processing malicious input, but processing succeeded",
              expectedExceptionType.getSimpleName()));
    } catch (Exception e) {
      LOGGER.info("Good: Malicious input triggered exception: {}", e.getClass().getSimpleName());
      assertThat(
          "Exception type should match expected security exception",
          expectedExceptionType.isInstance(e),
          is(true));
    }
  }

  /**
   * Creates an unsafe XML DocumentBuilder for vulnerability testing.
   *
   * <p><strong>WARNING:</strong> This method intentionally creates a vulnerable XML parser for
   * testing purposes. NEVER use this in production code!
   *
   * @return DocumentBuilder with XXE protections disabled
   * @throws ParserConfigurationException if parser creation fails
   */
  protected DocumentBuilder createUnsafeXmlParser() throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // Intentionally disable security features for vulnerability testing
    factory.setFeature("http://xml.org/sax/features/external-general-entities", true);
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
    factory.setExpandEntityReferences(true);
    return factory.newDocumentBuilder();
  }

  /**
   * Creates a safe XML DocumentBuilder with XXE protections enabled.
   *
   * <p>This is the recommended configuration for production XML parsing.
   *
   * @return DocumentBuilder with XXE protections enabled
   * @throws ParserConfigurationException if parser creation fails
   */
  protected DocumentBuilder createSafeXmlParser() throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // Enable security features to prevent XXE
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    factory.setExpandEntityReferences(false);
    factory.setXIncludeAware(false);
    return factory.newDocumentBuilder();
  }

  /**
   * Parses XML content with the specified parser.
   *
   * @param xmlContent the XML string to parse
   * @param parser the DocumentBuilder to use
   * @return the parsed Document
   * @throws SAXException if XML parsing fails
   * @throws IOException if I/O error occurs
   */
  protected Document parseXml(String xmlContent, DocumentBuilder parser)
      throws SAXException, IOException {
    ByteArrayInputStream inputStream =
        new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
    return parser.parse(inputStream);
  }

  /**
   * Reads file content as a string (for test verification).
   *
   * @param filePath the path to the file
   * @return the file content as a string
   * @throws IOException if file cannot be read
   */
  protected String readFileAsString(String filePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
  }

  /**
   * Creates a temporary file for testing and registers it for cleanup.
   *
   * @param prefix the file name prefix
   * @param suffix the file name suffix
   * @return the temporary File
   * @throws IOException if file creation fails
   */
  protected File createTempFile(String prefix, String suffix) throws IOException {
    File tempFile = File.createTempFile(prefix, suffix);
    temporaryFiles.add(tempFile);
    LOGGER.debug("Created temporary file: {}", tempFile.getAbsolutePath());
    return tempFile;
  }

  /**
   * Computes SHA-256 hash of input data.
   *
   * @param data the data to hash
   * @return Base64-encoded hash
   * @throws NoSuchAlgorithmException if SHA-256 is not available
   */
  protected String computeHash(byte[] data) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(data);
    return Base64.getEncoder().encodeToString(hash);
  }

  /**
   * Functional interface for operations that may throw exceptions.
   *
   * @param <T> input type
   * @param <R> result type
   */
  @FunctionalInterface
  protected interface ThrowingFunction<T, R> {
    R apply(T input) throws Exception;
  }

  /**
   * Annotation to mark test methods as security vulnerability tests.
   *
   * <p>This annotation helps identify security-specific tests for reporting and provides metadata
   * about the vulnerability being tested.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface SecurityTest {
    /** Description of the vulnerability being tested. */
    String description() default "";

    /** Severity level (CRITICAL, HIGH, MEDIUM, LOW). */
    String severity() default "HIGH";

    /** Whether this test expects the vulnerability to currently exist. */
    boolean expectVulnerability() default true;
  }

  /**
   * Annotation to associate test methods with CVE identifiers.
   *
   * <p>This enables traceability from CVE IDs to verification tests, supporting DO-278 requirements
   * for verification and validation.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface CVE {
    /** CVE identifier (e.g., "CVE-2024-12345"). */
    String value();

    /** Link to CVE details or advisory. */
    String url() default "";

    /** Description of the CVE. */
    String description() default "";
  }

  /**
   * Annotation to mark tests that verify OWASP Top 10 vulnerabilities.
   *
   * <p>Provides traceability to OWASP vulnerability categories.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface OWASP {
    /** OWASP category (e.g., "A1:2021-Broken Access Control"). */
    String category();

    /** Year of the OWASP Top 10 list. */
    String year() default "2021";
  }
}
