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
package org.codice.alliance.test.security.harness.crypto;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.codice.alliance.test.security.harness.common.SecurityTestBase;
import org.codice.alliance.test.security.harness.common.VulnerabilityTestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security test harness for weak cryptography vulnerabilities.
 *
 * <p>This test harness demonstrates vulnerabilities related to weak cryptographic algorithms,
 * insufficient key sizes, and insecure random number generation.
 *
 * <p><strong>OWASP Classification:</strong> A02:2021 – Cryptographic Failures
 */
public class WeakCryptoTest extends SecurityTestBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(WeakCryptoTest.class);

  /**
   * Tests detection of DES algorithm usage (considered weak).
   */
  @Test
  @SecurityTest(description = "Weak DES encryption algorithm", severity = "HIGH")
  @OWASP(category = "A02:2021-Cryptographic Failures")
  @Ignore("Test demonstrates vulnerability - remove @Ignore to verify fix")
  public void testWeakDesAlgorithm() throws Exception {
    LOGGER.info("Testing weak DES algorithm usage");

    // Arrange & Act: Use DES (weak algorithm)
    KeyGenerator keyGen = KeyGenerator.getInstance("DES");
    SecretKey key = keyGen.generateKey();
    Cipher cipher = Cipher.getInstance("DES");

    // Assert: DES usage should be detected
    assertThat("DES algorithm should be detected", cipher.getAlgorithm().contains("DES"), is(true));
    LOGGER.warn("Weak DES algorithm in use - should use AES instead");
  }

  /**
   * Tests detection of insufficient key size.
   */
  @Test
  @SecurityTest(description = "Insufficient AES key size", severity = "MEDIUM")
  @OWASP(category = "A02:2021-Cryptographic Failures")
  @Ignore("Test demonstrates vulnerability - remove @Ignore to verify fix")
  public void testInsufficientKeySize() throws Exception {
    LOGGER.info("Testing insufficient AES key size");

    // Arrange: Create 128-bit key (should be 256-bit for high security)
    byte[] weakKey = VulnerabilityTestUtils.createWeakKey(128);
    SecretKey key = new SecretKeySpec(weakKey, "AES");

    // Assert: Key size should be insufficient
    assertThat("Key size should be 128 bits (weak)", key.getEncoded().length * 8, is(128));
    LOGGER.warn("128-bit AES key detected - recommend 256-bit for high security");
  }

  /**
   * Tests detection of ECB mode usage (insecure).
   */
  @Test
  @SecurityTest(description = "Insecure ECB cipher mode", severity = "HIGH")
  @OWASP(category = "A02:2021-Cryptographic Failures")
  @Ignore("Test demonstrates vulnerability - remove @Ignore to verify fix")
  public void testInsecureEcbMode() throws Exception {
    LOGGER.info("Testing insecure ECB mode");

    // Arrange & Act: Use ECB mode (insecure - no IV)
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

    // Assert: ECB mode should be detected
    assertThat("ECB mode should be detected", cipher.getAlgorithm().contains("ECB"), is(true));
    LOGGER.warn("ECB mode detected - should use CBC or GCM mode instead");
  }
}
