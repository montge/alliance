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
package org.codice.alliance.test.security.harness.injection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.regex.Pattern;
import org.codice.alliance.test.security.harness.common.SecurityTestBase;
import org.codice.alliance.test.security.harness.common.VulnerabilityTestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security test harness for SQL injection vulnerabilities.
 *
 * <p>This test harness demonstrates SQL injection vulnerabilities in database query construction.
 * SQL injection allows attackers to:
 *
 * <ul>
 *   <li>Bypass authentication mechanisms
 *   <li>Extract sensitive data from databases
 *   <li>Modify or delete database records
 *   <li>Execute administrative operations on the database
 * </ul>
 *
 * <p><strong>OWASP Classification:</strong> A03:2021 – Injection
 *
 * <p><strong>Remediation:</strong> Use parameterized queries (PreparedStatement) instead of string
 * concatenation.
 *
 * @see SecurityTestBase
 * @see VulnerabilityTestUtils
 */
public class SqlInjectionTest extends SecurityTestBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(SqlInjectionTest.class);

  /**
   * Simulates unsafe SQL query construction (for testing purposes only).
   *
   * @param username user-supplied username
   * @param password user-supplied password
   * @return SQL query string (UNSAFE - for demonstration only)
   */
  private String buildUnsafeSqlQuery(String username, String password) {
    // WARNING: This is intentionally vulnerable code for testing
    return String.format(
        "SELECT * FROM users WHERE username='%s' AND password='%s'", username, password);
  }

  /**
   * Tests basic SQL injection authentication bypass.
   *
   * <p>Using the payload "admin' OR '1'='1' --", an attacker can bypass authentication.
   */
  @Test
  @SecurityTest(description = "SQL injection authentication bypass", severity = "CRITICAL")
  @OWASP(category = "A03:2021-Injection")
  @Ignore("Test demonstrates vulnerability - remove @Ignore to verify fix")
  public void testSqlInjectionAuthBypass() {
    LOGGER.info("Testing SQL injection authentication bypass");

    // Arrange: Create SQL injection payload
    String maliciousUsername = VulnerabilityTestUtils.createSqlAuthBypass();
    String password = "anything";

    // Act: Build query with malicious input
    String query = buildUnsafeSqlQuery(maliciousUsername, password);

    LOGGER.debug("Generated query: {}", query);

    // Assert: Verify injection modified query logic
    assertVulnerabilityExists(query, "OR '1'='1'");
    assertThat("Query should always evaluate to true", query.contains("'1'='1'"), is(true));
  }

  /**
   * Tests SQL injection with UNION SELECT for data extraction.
   */
  @Test
  @SecurityTest(description = "SQL injection UNION SELECT attack", severity = "CRITICAL")
  @OWASP(category = "A03:2021-Injection")
  @Ignore("Test demonstrates vulnerability - remove @Ignore to verify fix")
  public void testSqlInjectionUnionSelect() {
    LOGGER.info("Testing SQL injection UNION SELECT");

    // Arrange: Create UNION SELECT payload
    String maliciousInput = VulnerabilityTestUtils.createSqlUnionSelect(3);

    // Act: Build query
    String query = buildUnsafeSqlQuery(maliciousInput, "password");

    LOGGER.debug("Generated query: {}", query);

    // Assert: Verify UNION SELECT was injected
    assertVulnerabilityExists(query, Pattern.compile("UNION\\s+SELECT"));
  }

  /**
   * Tests time-based blind SQL injection.
   */
  @Test
  @SecurityTest(description = "Time-based blind SQL injection", severity = "HIGH")
  @OWASP(category = "A03:2021-Injection")
  @Ignore("Test demonstrates vulnerability - remove @Ignore to verify fix")
  public void testSqlInjectionTimeBasedBlind() {
    LOGGER.info("Testing time-based blind SQL injection");

    // Arrange: Create time-based payload
    String maliciousInput = VulnerabilityTestUtils.createSqlTimeBasedBlind(5);

    // Act: Build query
    String query = buildUnsafeSqlQuery(maliciousInput, "password");

    LOGGER.debug("Generated query: {}", query);

    // Assert: Verify SLEEP function was injected
    assertVulnerabilityExists(query, Pattern.compile("SLEEP\\(\\d+\\)"));
  }
}
