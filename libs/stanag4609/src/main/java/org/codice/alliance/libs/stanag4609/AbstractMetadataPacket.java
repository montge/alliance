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

import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;
import org.codice.ddf.libs.klv.KlvContext;
import org.codice.ddf.libs.klv.KlvDecoder;
import org.codice.ddf.libs.klv.KlvDecodingException;
import org.codice.ddf.libs.klv.data.numerical.KlvUnsignedShort;
import org.codice.ddf.libs.klv.data.set.KlvLocalSet;
import org.jcodec.containers.mps.MPSDemuxer.PESPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractMetadataPacket {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMetadataPacket.class);

  /**
   * Location of the "PES header length" field which contains the number of extra bytes contained in
   * the header.
   */
  private static final int PES_HEADER_LENGTH_INDEX = 8;

  private static final int BASE_PES_PACKET_HEADER_LENGTH = 9;

  /**
   * Maximum allowed PES payload length (32 MB).
   *
   * <p>This limit prevents integer overflow attacks (CUSTOM-KLV-002, Issue #52) in PES packet
   * processing. Legitimate STANAG 4609 PES packets should not exceed this size.
   *
   * @see <a href="https://github.com/montge/alliance/issues/52">Issue #52</a>
   */
  private static final int MAX_PES_PAYLOAD_LENGTH = 32 * 1024 * 1024; // 32 MB

  /**
   * Maximum allowed additional header bytes (255 is spec maximum, but we're conservative).
   *
   * <p>Prevents integer overflow in headerLength calculation.
   */
  private static final int MAX_ADDITIONAL_HEADER_BYTES = 255;

  private final byte[] pesPacketBytes;

  private final PESPacket pesHeader;

  protected final KlvDecoder decoder;

  AbstractMetadataPacket(
      final byte[] pesPacketBytes, final PESPacket pesHeader, final KlvDecoder decoder) {
    this.pesPacketBytes = pesPacketBytes;
    this.pesHeader = pesHeader;
    this.decoder = decoder;
  }

  private boolean validateChecksum(final KlvContext klvContext, final byte[] klvBytes)
      throws KlvDecodingException {
    if (!klvContext.hasDataElement(Stanag4609TransportStreamParser.UAS_DATALINK_LOCAL_SET)) {
      throw new KlvDecodingException("KLV did not contain the UAS Datalink Local Set");
    }

    final KlvContext localSetContext =
        ((KlvLocalSet)
                klvContext.getDataElementByName(
                    Stanag4609TransportStreamParser.UAS_DATALINK_LOCAL_SET))
            .getValue();

    if (localSetContext.hasDataElement(Stanag4609TransportStreamParser.CHECKSUM)) {
      final int packetChecksum =
          ((KlvUnsignedShort)
                  localSetContext.getDataElementByName(Stanag4609TransportStreamParser.CHECKSUM))
              .getValue();

      short calculatedChecksum = 0;
      // Checksum is calculated by a 16-bit sum from the beginning of the KLV set to the 1-byte
      // checksum length (the checksum value is 2 bytes, which is why we subtract 2).
      for (int i = 0; i < klvBytes.length - 2; ++i) {
        calculatedChecksum += (klvBytes[i] & 0xFF) << (8 * ((i + 1) % 2));
      }

      return (calculatedChecksum & 0xFFFF) == packetChecksum;
    }

    throw new KlvDecodingException(
        "Decoded KLV packet didn't contain checksum (which is required).");
  }

  protected final byte[] getPESPacketPayload() {

    if (this.pesPacketBytes.length < BASE_PES_PACKET_HEADER_LENGTH) {
      return null;
    }

    // Read additional header bytes from PES header
    int additionalHeaderBytes = Byte.toUnsignedInt(pesPacketBytes[PES_HEADER_LENGTH_INDEX]);

    // SECURITY FIX (CUSTOM-KLV-002, Issue #52): Validate additionalHeaderBytes
    // This value comes from untrusted PES packet and could be malicious
    if (additionalHeaderBytes > MAX_ADDITIONAL_HEADER_BYTES) {
      LOGGER.warn(
          "Rejecting PES packet with excessive additional header bytes: {} (max: {})",
          additionalHeaderBytes,
          MAX_ADDITIONAL_HEADER_BYTES);
      return null;
    }

    // SECURITY FIX: Validate pesHeader.length before arithmetic
    if (pesHeader.length < 3 + additionalHeaderBytes) {
      LOGGER.warn(
          "Rejecting PES packet: header length ({}) too small for additional header bytes ({})",
          pesHeader.length,
          additionalHeaderBytes);
      return null;
    }

    int payloadLength = pesHeader.length - 3 - additionalHeaderBytes;

    // SECURITY FIX: Validate payload length is reasonable
    if (payloadLength > MAX_PES_PAYLOAD_LENGTH) {
      LOGGER.warn(
          "Rejecting PES packet with excessive payload length: {} bytes (max: {})",
          payloadLength,
          MAX_PES_PAYLOAD_LENGTH);
      return null;
    }

    // Negative payload length indicates malformed packet
    if (payloadLength < 0) {
      LOGGER.warn("Rejecting PES packet with negative payload length: {}", payloadLength);
      return null;
    }

    // SECURITY FIX: Safe integer arithmetic for headerLength
    // Check overflow before addition
    if (additionalHeaderBytes > Integer.MAX_VALUE - BASE_PES_PACKET_HEADER_LENGTH) {
      LOGGER.warn("Rejecting PES packet: headerLength calculation would overflow");
      return null;
    }

    int headerLength = BASE_PES_PACKET_HEADER_LENGTH + additionalHeaderBytes;

    // SECURITY FIX: Validate headerLength doesn't exceed buffer
    if (headerLength > pesPacketBytes.length) {
      LOGGER.warn(
          "Rejecting PES packet: headerLength ({}) exceeds buffer size ({})",
          headerLength,
          pesPacketBytes.length);
      return null;
    }

    // SECURITY FIX: Safe integer arithmetic for payloadEnd
    // Check overflow before addition
    if (payloadLength > Integer.MAX_VALUE - headerLength) {
      LOGGER.warn("Rejecting PES packet: payloadEnd calculation would overflow");
      return null;
    }

    final int payloadEnd = Math.min(pesPacketBytes.length, headerLength + payloadLength);

    // SECURITY FIX: Validate slice bounds (defense in depth)
    if (headerLength < 0 || payloadEnd < 0 || payloadEnd < headerLength) {
      LOGGER.warn(
          "Rejecting PES packet: invalid slice bounds (headerLength={}, payloadEnd={})",
          headerLength,
          payloadEnd);
      return null;
    }

    return Arrays.copyOfRange(pesPacketBytes, headerLength, payloadEnd);
  }

  /** @return klv payload bytes, otherwise null */
  protected abstract byte[] getKLVBytes();

  final DecodedKLVMetadataPacket decodeKLV() throws KlvDecodingException {
    final byte[] klvBytes = getKLVBytes();

    if (klvBytes != null && klvBytes.length > 0) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("KLV bytes: {}", DatatypeConverter.printHexBinary(klvBytes));
      }

      // SECURITY FIX (CUSTOM-KLV-001, Issue #51): Validate KLV before decoding
      // This prevents integer overflow attacks via malicious BER length fields
      // Defense-in-depth: validate before passing to DDF decoder
      KlvSecurityValidator.validateKlvBytes(klvBytes);

      final KlvContext decodedKLV = decoder.decode(klvBytes);

      if (validateChecksum(decodedKLV, klvBytes)) {
        return new DecodedKLVMetadataPacket(pesHeader.pts, decodedKLV);
      } else {
        throw new KlvDecodingException("KLV packet checksum does not match.");
      }
    }

    return null;
  }
}
