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
import org.codice.ddf.libs.klv.KlvDecoder;
import org.jcodec.containers.mps.MPSDemuxer.PESPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SynchronousMetadataPacket extends AbstractMetadataPacket {

  private static final Logger LOGGER = LoggerFactory.getLogger(SynchronousMetadataPacket.class);

  private static final int METADATA_ACCESS_UNIT_HEADER_LENGTH = 5;

  /**
   * Maximum allowed payload length in a Metadata Access Unit (16 MB).
   *
   * <p>This limit prevents integer overflow attacks (CUSTOM-KLV-002, Issue #52) and ensures
   * reasonable memory usage. STANAG 4609 KLV payloads should not exceed this size in legitimate
   * use cases.
   *
   * @see <a href="https://github.com/montge/alliance/issues/52">Issue #52</a>
   */
  private static final int MAX_PAYLOAD_LENGTH = 16 * 1024 * 1024; // 16 MB

  SynchronousMetadataPacket(
      final byte[] pesPacketBytes, final PESPacket pesHeader, final KlvDecoder decoder) {
    super(pesPacketBytes, pesHeader, decoder);
  }

  @Override
  protected byte[] getKLVBytes() {

    final byte[] metadataAccessUnit = getPESPacketPayload();

    if (metadataAccessUnit == null) {
      return null;
    }

    if (metadataAccessUnit.length > METADATA_ACCESS_UNIT_HEADER_LENGTH) {
      return getKLVPayloadFromMetadataAccessUnit(metadataAccessUnit);
    }

    return null;
  }

  private byte[] getKLVPayloadFromMetadataAccessUnit(final byte[] metadataAccessUnit) {
    // Read payload length from MAU header bytes 3-4 (big-endian unsigned 16-bit)
    final int payloadLength =
        ((metadataAccessUnit[3] & 0xFF) << 8) | (metadataAccessUnit[4] & 0xFF);

    // SECURITY FIX (CUSTOM-KLV-002, Issue #52): Validate payload length before use
    // This prevents buffer overflow attacks where length field claims more data than available
    if (payloadLength > MAX_PAYLOAD_LENGTH) {
      LOGGER.warn(
          "Rejecting MAU with excessive payload length: {} bytes (max: {})",
          payloadLength,
          MAX_PAYLOAD_LENGTH);
      return null;
    }

    // SECURITY FIX: Verify sufficient data is available
    // Prevents out-of-bounds read when length field exceeds actual buffer size
    final int availablePayloadBytes = metadataAccessUnit.length - METADATA_ACCESS_UNIT_HEADER_LENGTH;
    if (payloadLength > availablePayloadBytes) {
      LOGGER.warn(
          "Rejecting MAU with mismatched length: claims {} bytes but only {} available",
          payloadLength,
          availablePayloadBytes);
      return null;
    }

    // SECURITY FIX: Use safe integer arithmetic to prevent overflow
    // Check that headerLength + payloadLength doesn't overflow
    if (payloadLength > Integer.MAX_VALUE - METADATA_ACCESS_UNIT_HEADER_LENGTH) {
      LOGGER.warn("Rejecting MAU: payloadLength would cause integer overflow");
      return null;
    }

    final int payloadEnd = METADATA_ACCESS_UNIT_HEADER_LENGTH + payloadLength;

    // Validate payloadEnd is within buffer bounds (defense in depth)
    if (payloadEnd > metadataAccessUnit.length) {
      LOGGER.warn(
          "Rejecting MAU: calculated payloadEnd ({}) exceeds buffer size ({})",
          payloadEnd,
          metadataAccessUnit.length);
      return null;
    }

    int headerOffset = 0;
    if (metadataAccessUnit.length == payloadEnd) {
      headerOffset = METADATA_ACCESS_UNIT_HEADER_LENGTH;
    }

    return Arrays.copyOfRange(metadataAccessUnit, headerOffset, payloadEnd);
  }
}
