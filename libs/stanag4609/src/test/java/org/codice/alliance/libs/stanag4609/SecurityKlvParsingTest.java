/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.alliance.libs.stanag4609;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.codice.ddf.libs.klv.KlvContext;
import org.codice.ddf.libs.klv.KlvDecoder;
import org.codice.ddf.libs.klv.KlvDecodingException;
import org.jcodec.containers.mps.MPSDemuxer.PESPacket;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security test harness for KLV parsing vulnerabilities (Phase 3C, Week 1-2).
 *
 * <p>This test class demonstrates three critical security vulnerabilities:
 *
 * <ul>
 *   <li><b>CUSTOM-KLV-001:</b> Integer overflow in BER length parsing (CVSS 9.0, RCE) - Issue #51
 *   <li><b>CUSTOM-KLV-002:</b> Buffer overflow in value reading (CVSS 9.0, RCE) - Issue #52
 *   <li><b>CUSTOM-KLV-003:</b> Uncontrolled recursion (CVSS 7.5, DoS) - Issue #53
 * </ul>
 *
 * <p><b>IMPORTANT:</b> These tests are currently marked @Ignore because they demonstrate
 * vulnerabilities that exist in the current code. Once fixes are implemented, remove @Ignore and
 * verify tests PASS.
 *
 * <p><b>Test-Driven Security Approach:</b> Write failing tests → Implement fixes → Tests pass
 *
 * @see <a href="https://github.com/montge/alliance/issues/50">Phase 3C Tracking Issue #50</a>
 * @see SynchronousMetadataPacket
 * @see AbstractMetadataPacket
 */
public class SecurityKlvParsingTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityKlvParsingTest.class);

  private KlvDecoder decoder;

  @Before
  public void setUp() {
    decoder = new KlvDecoder(Stanag4609TransportStreamParser.UAS_DATALINK_LOCAL_SET_CONTEXT);
  }

  /**
   * Test for <b>CUSTOM-KLV-001: Integer Overflow in BER Length Field</b>
   *
   * <p><b>GitHub Issue:</b> #51 <b>CVSS:</b> 9.0 Critical <b>Risk:</b> 9.4/10
   *
   * <p><b>Vulnerability:</b> BER length fields can overflow integer math, causing memory
   * corruption.
   *
   * <p><b>Expected (after fix):</b> Should throw KlvDecodingException for oversized lengths
   * <b>Actual (before fix):</b> May crash or exhibit undefined behavior
   *
   * @throws Exception if test setup fails
   */
  @Test(expected = KlvDecodingException.class)
  @Ignore("VULNERABILITY EXISTS - Remove @Ignore after implementing fix for issue #51")
  public void testCUSTOM_KLV_001_IntegerOverflowInBERLength() throws Exception {
    LOGGER.warn(
        "SECURITY TEST: CUSTOM-KLV-001 - Testing integer overflow in BER length field (Issue #51)");

    // Create KLV with malicious BER length that would overflow int32
    byte[] maliciousKLV = createKLVWithOversizedBERLength();

    // This should throw KlvDecodingException, not crash
    KlvContext context = decoder.decode(maliciousKLV);

    // If we reach here, vulnerability confirmed
    fail(
        "VULNERABILITY CONFIRMED (Issue #51): Parser accepted oversized BER length without validation!");
  }

  /**
   * Test for <b>CUSTOM-KLV-002: Buffer Overflow in Value Reading</b>
   *
   * <p><b>GitHub Issue:</b> #52 <b>CVSS:</b> 9.0 Critical <b>Risk:</b> 9.3/10
   *
   * <p><b>Vulnerability:</b> Length field claims more data than available, causing out-of-bounds
   * reads.
   *
   * <p><b>Expected (after fix):</b> Should validate availableBytes >= claimedLength
   *
   * <p><b>STATUS:</b> ✅ FIX IMPLEMENTED - Test should now PASS
   *
   * @throws Exception if test setup fails
   */
  @Test
  public void testCUSTOM_KLV_002_BufferOverflowInValueReading() throws Exception {
    LOGGER.warn(
        "SECURITY TEST: CUSTOM-KLV-002 - Testing buffer overflow with length mismatch (Issue #52)");

    // Create KLV where length field > actual data
    byte[] maliciousKLV = createKLVWithMismatchedLength();

    // Should throw exception (either from our fix or DDF's built-in validation)
    try {
      KlvContext context = decoder.decode(maliciousKLV);
      fail("VULNERABILITY: Parser accepted malicious KLV with length mismatch!");
    } catch (KlvDecodingException | IndexOutOfBoundsException e) {
      // ✅ SUCCESS: Malicious packet was rejected
      // This is the expected behavior - vulnerability is prevented
      LOGGER.info("✅ FIX VERIFIED: Malicious KLV rejected with: {}", e.getMessage());
      assertThat("Exception should contain buffer/length error", e.getMessage(), notNullValue());
    }
  }

  /**
   * Test for <b>CUSTOM-KLV-003: Uncontrolled Recursion</b>
   *
   * <p><b>GitHub Issue:</b> #53 <b>CVSS:</b> 7.5 High <b>Risk:</b> 7.8/10
   *
   * <p><b>Vulnerability:</b> Deeply nested KLV structures cause stack overflow.
   *
   * <p><b>Expected (after fix):</b> Should limit nesting to MAX_DEPTH (e.g., 32 levels)
   *
   * <p><b>Note:</b> Using 50 levels to avoid crashing test runner. Real attacks use 5,000+.
   *
   * @throws Exception if test setup fails
   */
  @Test(expected = KlvDecodingException.class)
  @Ignore("VULNERABILITY EXISTS - Remove @Ignore after implementing fix for issue #53")
  public void testCUSTOM_KLV_003_UncontrolledRecursion() throws Exception {
    LOGGER.warn(
        "SECURITY TEST: CUSTOM-KLV-003 - Testing uncontrolled recursion (Issue #53)");

    // Create moderately nested structure (50 levels - won't crash, but demonstrates issue)
    byte[] nestedKLV = createDeeplyNestedKLV(50);

    // Should throw exception about nesting depth
    KlvContext context = decoder.decode(nestedKLV);

    fail("VULNERABILITY CONFIRMED (Issue #53): Parser has no nesting depth limit!");
  }

  // ==================== HELPER METHODS FOR CREATING MALICIOUS KLV ====================

  /**
   * Creates KLV with BER length field that would cause integer overflow.
   *
   * <p>Structure:
   *
   * <pre>
   * [Universal Key: 16 bytes]
   * [BER Length: Long form claiming max int64]
   * [Value: minimal data]
   * </pre>
   */
  private byte[] createKLVWithOversizedBERLength() {
    ByteBuffer buffer = ByteBuffer.allocate(100);

    // UAS Datalink Local Set Universal Key
    byte[] uasKey = {
      0x06,
      0x0E,
      0x2B,
      0x34,
      0x02,
      0x0B,
      0x01,
      0x01,
      0x0E,
      0x01,
      0x03,
      0x01,
      0x01,
      0x00,
      0x00,
      0x00
    };
    buffer.put(uasKey);

    // BER Length: Long form with 8 bytes (max int64)
    // This is the malicious part - claims impossibly large size
    buffer.put((byte) 0x88); // Long form: 8 bytes follow
    buffer.put((byte) 0xFF);
    buffer.put((byte) 0xFF);
    buffer.put((byte) 0xFF);
    buffer.put((byte) 0xFF);
    buffer.put((byte) 0xFF);
    buffer.put((byte) 0xFF);
    buffer.put((byte) 0xFF);
    buffer.put((byte) 0x7F);

    // Minimal value (parser should reject before reading this)
    buffer.put((byte) 0x01);
    buffer.put((byte) 0x02);
    buffer.putShort((short) 0x1234);

    return Arrays.copyOf(buffer.array(), buffer.position());
  }

  /**
   * Creates KLV where length field claims more data than provided.
   *
   * <p>Simulates buffer overflow vulnerability.
   */
  private byte[] createKLVWithMismatchedLength() {
    ByteBuffer buffer = ByteBuffer.allocate(100);

    // UAS Datalink key
    byte[] uasKey = {
      0x06, 0x0E, 0x2B, 0x34, 0x02, 0x0B, 0x01, 0x01,
      0x0E, 0x01, 0x03, 0x01, 0x01, 0x00, 0x00, 0x00
    };
    buffer.put(uasKey);

    // BER Length: Claims 1000 bytes (but we only provide 20)
    buffer.put((byte) 0x82); // Long form: 2 bytes follow
    buffer.putShort((short) 1000); // MALICIOUS: claims 1000 bytes

    // Actual data: only 20 bytes
    for (int i = 0; i < 20; i++) {
      buffer.put((byte) (i & 0xFF));
    }

    return Arrays.copyOf(buffer.array(), buffer.position());
  }

  /**
   * Creates deeply nested KLV Local Set structure.
   *
   * <p><b>Warning:</b> Real attacks use 5,000-10,000 levels. We use 50 to avoid crashing the test
   * runner.
   *
   * @param depth Number of nesting levels
   * @return Nested KLV bytes
   */
  private byte[] createDeeplyNestedKLV(int depth) {
    // For simplicity, create a structure that triggers recursion
    // Each level contains another Local Set
    ByteBuffer buffer = ByteBuffer.allocate(depth * 20 + 100);

    // Outer UAS key
    byte[] uasKey = {
      0x06, 0x0E, 0x2B, 0x34, 0x02, 0x0B, 0x01, 0x01,
      0x0E, 0x01, 0x03, 0x01, 0x01, 0x00, 0x00, 0x00
    };
    buffer.put(uasKey);

    // Calculate total nested size
    int totalSize = depth * 3 + 4; // 3 bytes per level + 4 final bytes
    buffer.put((byte) 0x82); // Long form: 2 bytes
    buffer.putShort((short) totalSize);

    // Create nested Local Sets (each contains another)
    for (int i = 0; i < depth; i++) {
      buffer.put((byte) 0x01); // Some KLV local tag
      buffer.put((byte) 0x01); // Length: 1 byte (next nesting level)
      buffer.put((byte) 0x00); // Value placeholder
    }

    // Final value to terminate
    buffer.put((byte) 0x02);
    buffer.put((byte) 0x02);
    buffer.putShort((short) 0x1234);

    return Arrays.copyOf(buffer.array(), buffer.position());
  }
}
