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

import org.codice.ddf.libs.klv.KlvDecodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security validator for KLV (Key-Length-Value) data to prevent parsing vulnerabilities.
 *
 * <p>This class provides defense-in-depth security validation for KLV data before it reaches the
 * DDF KlvDecoder. It specifically addresses:
 *
 * <ul>
 *   <li><b>CUSTOM-KLV-001 (Issue #51):</b> Integer overflow in BER length field parsing
 *   <li>Excessive length values that could cause memory exhaustion
 *   <li>Malformed BER encoding that could crash the parser
 * </ul>
 *
 * <p><b>BER (Basic Encoding Rules) Length Encoding:</b>
 *
 * <ul>
 *   <li>Short form: 1 byte, bit 7 = 0, value in bits 6-0 (0-127)
 *   <li>Long form: First byte bit 7 = 1, bits 6-0 = number of length bytes following
 * </ul>
 *
 * @see <a href="https://github.com/montge/alliance/issues/51">Issue #51: CUSTOM-KLV-001</a>
 */
class KlvSecurityValidator {

  private static final Logger LOGGER = LoggerFactory.getLogger(KlvSecurityValidator.class);

  /**
   * Maximum allowed BER-encoded length value (16 MB).
   *
   * <p>This limit prevents integer overflow attacks and excessive memory allocation. Legitimate
   * STANAG 4609 KLV packets should not exceed this size.
   *
   * <p><b>Security Note:</b> Setting this too high could allow DoS via memory exhaustion. Setting
   * too low could reject legitimate large video metadata (e.g., embedded thumbnails).
   */
  private static final long MAX_BER_LENGTH = 16L * 1024 * 1024; // 16 MB

  /**
   * Maximum number of bytes allowed for BER length encoding (8 bytes = int64).
   *
   * <p>BER allows arbitrarily long length fields, but we limit to 8 bytes to prevent parsing
   * attacks and stay within long integer bounds.
   */
  private static final int MAX_BER_LENGTH_BYTES = 8;

  /**
   * Universal Key length for STANAG 4609 KLV (16 bytes).
   *
   * <p>UAS Datalink Local Set uses 16-byte Universal Label keys per SMPTE 336M.
   */
  private static final int UNIVERSAL_KEY_LENGTH = 16;

  /**
   * Validates KLV bytes for security vulnerabilities before decoding.
   *
   * <p>This method performs comprehensive security checks on raw KLV data to prevent:
   *
   * <ul>
   *   <li>Integer overflow in BER length field (CUSTOM-KLV-001, Issue #51)
   *   <li>Excessive length values causing memory exhaustion
   *   <li>Buffer overflows from length > available data
   *   <li>Malformed BER encoding
   * </ul>
   *
   * <p><b>Defense Strategy:</b> Fail-fast validation before expensive decoding operations.
   *
   * @param klvBytes Raw KLV bytes to validate (must not be null)
   * @throws KlvDecodingException if validation fails (malicious or malformed KLV detected)
   * @see #parseBERLength(byte[], int)
   */
  static void validateKlvBytes(final byte[] klvBytes) throws KlvDecodingException {
    if (klvBytes == null) {
      throw new KlvDecodingException("KLV bytes cannot be null");
    }

    // Minimum KLV: 16-byte key + 1-byte length + 0+ bytes value
    if (klvBytes.length < UNIVERSAL_KEY_LENGTH + 1) {
      throw new KlvDecodingException(
          String.format(
              "KLV data too short: %d bytes (minimum %d bytes required)",
              klvBytes.length, UNIVERSAL_KEY_LENGTH + 1));
    }

    // Parse and validate BER length field (starts after 16-byte universal key)
    final int berOffset = UNIVERSAL_KEY_LENGTH;
    final BERLength berLength = parseBERLength(klvBytes, berOffset);

    // SECURITY CHECK 1: Validate length doesn't exceed maximum
    if (berLength.value > MAX_BER_LENGTH) {
      LOGGER.warn(
          "SECURITY: Rejecting KLV with excessive BER length: {} bytes (max: {})",
          berLength.value,
          MAX_BER_LENGTH);
      throw new KlvDecodingException(
          String.format(
              "BER length exceeds maximum allowed: %d bytes (max: %d)",
              berLength.value, MAX_BER_LENGTH));
    }

    // SECURITY CHECK 2: Validate sufficient data is available
    // Calculate total required bytes: key + BER encoding + value
    final int totalBERBytes = 1 + berLength.numLengthBytes; // BER encoding size
    final long totalRequired = (long) UNIVERSAL_KEY_LENGTH + totalBERBytes + berLength.value;

    // Check for integer overflow in totalRequired calculation
    if (totalRequired < 0 || totalRequired > Integer.MAX_VALUE) {
      LOGGER.warn("SECURITY: Integer overflow detected in KLV total length calculation");
      throw new KlvDecodingException(
          "KLV total length calculation overflow (integer overflow attack detected)");
    }

    if (klvBytes.length < totalRequired) {
      LOGGER.warn(
          "SECURITY: KLV length mismatch: claims {} bytes but only {} available",
          totalRequired,
          klvBytes.length);
      throw new KlvDecodingException(
          String.format(
              "Insufficient KLV data: requires %d bytes but only %d available",
              totalRequired, klvBytes.length));
    }

    LOGGER.debug(
        "KLV security validation passed: {} bytes (BER length: {})", klvBytes.length, berLength.value);
  }

  /**
   * Parses a BER-encoded length field with security validation.
   *
   * <p><b>BER Encoding:</b>
   *
   * <pre>
   * Short form (0-127):
   *   Byte 0: 0xxxxxxx (bit 7 = 0, value in bits 6-0)
   *
   * Long form (128+):
   *   Byte 0: 1nnnnnnn (bit 7 = 1, bits 6-0 = number of length bytes following)
   *   Bytes 1-n: Length value in big-endian
   * </pre>
   *
   * <p><b>Security Checks:</b>
   *
   * <ul>
   *   <li>Limits length encoding to MAX_BER_LENGTH_BYTES (8 bytes)
   *   <li>Validates sufficient bytes available for length encoding
   *   <li>Prevents integer overflow when parsing long values
   *   <li>Rejects indefinite length encoding (0x80)
   * </ul>
   *
   * @param data Byte array containing BER-encoded length
   * @param offset Offset to start of BER length field
   * @return Parsed BER length information
   * @throws KlvDecodingException if BER encoding is invalid or malicious
   */
  private static BERLength parseBERLength(final byte[] data, final int offset)
      throws KlvDecodingException {

    if (offset >= data.length) {
      throw new KlvDecodingException("BER length offset exceeds data bounds");
    }

    final int firstByte = data[offset] & 0xFF;

    // Short form: bit 7 = 0, value in bits 6-0
    if ((firstByte & 0x80) == 0) {
      return new BERLength(firstByte, 0);
    }

    // Long form: bit 7 = 1, bits 6-0 = number of length bytes
    final int numLengthBytes = firstByte & 0x7F;

    // SECURITY CHECK: Reject indefinite length (0x80)
    if (numLengthBytes == 0) {
      throw new KlvDecodingException("Indefinite BER length encoding not supported (0x80)");
    }

    // SECURITY CHECK: Limit length encoding size to prevent attacks
    if (numLengthBytes > MAX_BER_LENGTH_BYTES) {
      LOGGER.warn(
          "SECURITY: Rejecting BER length with excessive encoding: {} bytes (max: {})",
          numLengthBytes,
          MAX_BER_LENGTH_BYTES);
      throw new KlvDecodingException(
          String.format(
              "BER length encoding too large: %d bytes (max: %d)",
              numLengthBytes, MAX_BER_LENGTH_BYTES));
    }

    // SECURITY CHECK: Validate sufficient bytes for length encoding
    if (offset + 1 + numLengthBytes > data.length) {
      throw new KlvDecodingException(
          String.format(
              "Insufficient data for BER length: needs %d bytes but only %d available",
              numLengthBytes, data.length - offset - 1));
    }

    // Parse length value from big-endian bytes
    // Use long to prevent overflow during parsing
    long lengthValue = 0;
    for (int i = 0; i < numLengthBytes; i++) {
      final int byteVal = data[offset + 1 + i] & 0xFF;

      // SECURITY CHECK: Detect overflow before it happens
      // If lengthValue * 256 would overflow, reject
      if (lengthValue > (Long.MAX_VALUE >> 8)) {
        LOGGER.warn("SECURITY: Integer overflow detected during BER length parsing");
        throw new KlvDecodingException(
            "BER length value overflow (value exceeds maximum parseable size)");
      }

      lengthValue = (lengthValue << 8) | byteVal;
    }

    return new BERLength(lengthValue, numLengthBytes);
  }

  /**
   * Container for parsed BER length information.
   *
   * <p>Immutable data class holding both the decoded length value and metadata about the encoding.
   */
  private static class BERLength {
    /** Decoded length value (number of bytes in KLV value field). */
    final long value;

    /**
     * Number of bytes used to encode the length (excluding first byte).
     *
     * <p>0 = short form (single byte), 1-8 = long form with N length bytes
     */
    final int numLengthBytes;

    BERLength(long value, int numLengthBytes) {
      this.value = value;
      this.numLengthBytes = numLengthBytes;
    }
  }
}
