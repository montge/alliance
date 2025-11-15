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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.codice.ddf.libs.klv.KlvDecodingException;
import org.junit.Test;

/** Unit tests for KlvSecurityValidator to verify security validation logic. */
public class KlvSecurityValidatorTest {

  /**
   * Test that the validator properly rejects KLV with excessive BER length.
   *
   * @throws Exception if test fails
   */
  @Test(expected = KlvDecodingException.class)
  public void testRejectsOversizedBERLength() throws Exception {
    byte[] maliciousKLV = createKLVWithOversizedBERLength();
    KlvSecurityValidator.validateKlvBytes(maliciousKLV);
  }

  /**
   * Test that the validator properly rejects KLV with length > available data.
   *
   * @throws Exception if test fails
   */
  @Test(expected = KlvDecodingException.class)
  public void testRejectsMismatchedLength() throws Exception {
    byte[] maliciousKLV = createKLVWithMismatchedLength();
    KlvSecurityValidator.validateKlvBytes(maliciousKLV);
  }

  /**
   * Test that valid KLV passes validation.
   *
   * @throws Exception if test fails
   */
  @Test
  public void testAcceptsValidKLV() throws Exception {
    byte[] validKLV = createValidKLV();
    // Should not throw exception
    KlvSecurityValidator.validateKlvBytes(validKLV);
  }

  // Helper methods

  private byte[] createKLVWithOversizedBERLength() {
    ByteBuffer buffer = ByteBuffer.allocate(100);

    // UAS key
    byte[] uasKey = {
      0x06, 0x0E, 0x2B, 0x34, 0x02, 0x0B, 0x01, 0x01,
      0x0E, 0x01, 0x03, 0x01, 0x01, 0x00, 0x00, 0x00
    };
    buffer.put(uasKey);

    // Malicious BER: claims max int64
    buffer.put((byte) 0x88);
    for (int i = 0; i < 7; i++) buffer.put((byte) 0xFF);
    buffer.put((byte) 0x7F);

    buffer.put((byte) 0x01);
    buffer.put((byte) 0x02);

    return Arrays.copyOf(buffer.array(), buffer.position());
  }

  private byte[] createKLVWithMismatchedLength() {
    ByteBuffer buffer = ByteBuffer.allocate(100);

    byte[] uasKey = {
      0x06, 0x0E, 0x2B, 0x34, 0x02, 0x0B, 0x01, 0x01,
      0x0E, 0x01, 0x03, 0x01, 0x01, 0x00, 0x00, 0x00
    };
    buffer.put(uasKey);

    // Claims 1000 bytes
    buffer.put((byte) 0x82);
    buffer.putShort((short) 1000);

    // Only provide 20 bytes
    for (int i = 0; i < 20; i++) buffer.put((byte) i);

    return Arrays.copyOf(buffer.array(), buffer.position());
  }

  private byte[] createValidKLV() {
    ByteBuffer buffer = ByteBuffer.allocate(100);

    byte[] uasKey = {
      0x06, 0x0E, 0x2B, 0x34, 0x02, 0x0B, 0x01, 0x01,
      0x0E, 0x01, 0x03, 0x01, 0x01, 0x00, 0x00, 0x00
    };
    buffer.put(uasKey);

    // Valid length: 4 bytes
    buffer.put((byte) 0x04);

    // Valid value: 4 bytes
    buffer.put((byte) 0x01);
    buffer.put((byte) 0x02);
    buffer.putShort((short) 0x1234);

    return Arrays.copyOf(buffer.array(), buffer.position());
  }
}
