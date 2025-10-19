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

import org.codice.alliance.test.security.harness.common.SecurityTestBase;
import org.codice.alliance.test.security.harness.common.VulnerabilityTestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security test harness for command injection vulnerabilities.
 *
 * <p>This test harness demonstrates command injection vulnerabilities when user input is passed to
 * system commands.
 *
 * <p><strong>OWASP Classification:</strong> A03:2021 – Injection
 */
public class CommandInjectionTest extends SecurityTestBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommandInjectionTest.class);

  /**
   * Tests Unix command injection.
   */
  @Test
  @SecurityTest(description = "Unix command injection", severity = "CRITICAL")
  @OWASP(category = "A03:2021-Injection")
  @Ignore("Test demonstrates vulnerability - remove @Ignore to verify fix")
  public void testCommandInjectionUnix() {
    LOGGER.info("Testing Unix command injection");

    String maliciousInput = VulnerabilityTestUtils.createCommandInjectionUnix("cat /etc/passwd");
    String command = "ping -c 1 " + maliciousInput;

    LOGGER.debug("Generated command: {}", command);
    assertVulnerabilityExists(command, "; cat /etc/passwd");
  }

  /**
   * Tests Windows command injection.
   */
  @Test
  @SecurityTest(description = "Windows command injection", severity = "CRITICAL")
  @OWASP(category = "A03:2021-Injection")
  @Ignore("Test demonstrates vulnerability - remove @Ignore to verify fix")
  public void testCommandInjectionWindows() {
    LOGGER.info("Testing Windows command injection");

    String maliciousInput = VulnerabilityTestUtils.createCommandInjectionWindows("dir");
    String command = "ping -n 1 " + maliciousInput;

    LOGGER.debug("Generated command: {}", command);
    assertVulnerabilityExists(command, "& dir &");
  }
}
