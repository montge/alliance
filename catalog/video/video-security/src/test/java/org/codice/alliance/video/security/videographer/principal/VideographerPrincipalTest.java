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
package org.codice.alliance.video.security.videographer.principal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Principal;
import org.junit.Test;

/**
 * Unit tests for {@link VideographerPrincipal}.
 *
 * <p>Tests the VideographerPrincipal class which represents a video streaming client identified by
 * their IP address. This principal is used in the Videographer security realm for authenticating
 * FMV (Full Motion Video) streaming clients.
 *
 * <p><b>Coverage Improvement:</b> This class had 0% test coverage. These tests provide validation
 * of principal name formatting, address parsing, and serialization behavior, improving overall
 * project test coverage toward the 80%+ goal.
 *
 * @see VideographerPrincipal
 */
public class VideographerPrincipalTest {

  private static final String TEST_IP_ADDRESS = "192.168.1.100";
  private static final String LOCALHOST_IP = "127.0.0.1";
  private static final String IPV6_ADDRESS = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";

  /**
   * Verifies that VideographerPrincipal constructs the correct name format.
   *
   * <p>Expected format: "Videographer@{ipAddress}"
   */
  @Test
  public void testConstructorCreatesCorrectName() {
    VideographerPrincipal principal = new VideographerPrincipal(TEST_IP_ADDRESS);

    assertThat("Principal should be created", principal, is(notNullValue()));
    assertThat(
        "Name should follow format Videographer@IP",
        principal.getName(),
        is(equalTo("Videographer@" + TEST_IP_ADDRESS)));
  }

  /**
   * Verifies that the getAddress method returns the IP address used in construction.
   */
  @Test
  public void testGetAddressReturnsConstructorArgument() {
    VideographerPrincipal principal = new VideographerPrincipal(TEST_IP_ADDRESS);

    assertThat("Address should match constructor argument", principal.getAddress(), is(equalTo(TEST_IP_ADDRESS)));
  }

  /**
   * Verifies that VideographerPrincipal implements the Principal interface.
   */
  @Test
  public void testImplementsPrincipalInterface() {
    VideographerPrincipal principal = new VideographerPrincipal(LOCALHOST_IP);

    assertThat("Should be instance of Principal", principal instanceof Principal, is(true));
  }

  /**
   * Verifies that VideographerPrincipal implements Serializable interface.
   */
  @Test
  public void testImplementsSerializableInterface() {
    VideographerPrincipal principal = new VideographerPrincipal(LOCALHOST_IP);

    assertThat("Should be instance of Serializable", principal instanceof Serializable, is(true));
  }

  /**
   * Verifies that toString returns the same value as getName.
   *
   * <p>This is important for principal display in security contexts.
   */
  @Test
  public void testToStringMatchesGetName() {
    VideographerPrincipal principal = new VideographerPrincipal(TEST_IP_ADDRESS);

    assertThat("toString should match getName", principal.toString(), is(equalTo(principal.getName())));
    assertThat(
        "toString should return formatted name",
        principal.toString(),
        is(equalTo("Videographer@" + TEST_IP_ADDRESS)));
  }

  /**
   * Verifies that parseAddressFromName correctly extracts IP address from principal name.
   */
  @Test
  public void testParseAddressFromNameExtractsIpAddress() {
    String fullName = "Videographer@" + TEST_IP_ADDRESS;

    String parsedAddress = VideographerPrincipal.parseAddressFromName(fullName);

    assertThat("Should parse IP address from name", parsedAddress, is(equalTo(TEST_IP_ADDRESS)));
  }

  /**
   * Verifies that parseAddressFromName handles localhost address.
   */
  @Test
  public void testParseAddressFromNameWithLocalhost() {
    String fullName = "Videographer@127.0.0.1";

    String parsedAddress = VideographerPrincipal.parseAddressFromName(fullName);

    assertThat("Should parse localhost address", parsedAddress, is(equalTo("127.0.0.1")));
  }

  /**
   * Verifies that parseAddressFromName handles IPv6 addresses.
   */
  @Test
  public void testParseAddressFromNameWithIpv6() {
    String fullName = "Videographer@" + IPV6_ADDRESS;

    String parsedAddress = VideographerPrincipal.parseAddressFromName(fullName);

    assertThat("Should parse IPv6 address", parsedAddress, is(equalTo(IPV6_ADDRESS)));
  }

  /**
   * Verifies that parseAddressFromName returns null for empty string.
   */
  @Test
  public void testParseAddressFromNameWithEmptyString() {
    String parsedAddress = VideographerPrincipal.parseAddressFromName("");

    assertThat("Should return null for empty string", parsedAddress, is(nullValue()));
  }

  /**
   * Verifies that parseAddressFromName returns null for null input.
   */
  @Test
  public void testParseAddressFromNameWithNull() {
    String parsedAddress = VideographerPrincipal.parseAddressFromName(null);

    assertThat("Should return null for null input", parsedAddress, is(nullValue()));
  }

  /**
   * Verifies that parseAddressFromName returns null for malformed name (no delimiter).
   */
  @Test
  public void testParseAddressFromNameWithNoDelimiter() {
    String malformedName = "Videographer192.168.1.100";

    String parsedAddress = VideographerPrincipal.parseAddressFromName(malformedName);

    assertThat("Should return null for name without delimiter", parsedAddress, is(nullValue()));
  }

  /**
   * Verifies that parseAddressFromName returns null for name with too many delimiters.
   */
  @Test
  public void testParseAddressFromNameWithMultipleDelimiters() {
    String malformedName = "Videographer@192.168@1.100";

    String parsedAddress = VideographerPrincipal.parseAddressFromName(malformedName);

    // Should return null because parts.length != 2
    assertThat("Should return null for name with multiple delimiters", parsedAddress, is(nullValue()));
  }

  /**
   * Verifies that parseAddressFromName returns null for name with only one part.
   */
  @Test
  public void testParseAddressFromNameWithOnlyPrefix() {
    String malformedName = "Videographer@";

    String parsedAddress = VideographerPrincipal.parseAddressFromName(malformedName);

    // parts[1] would be empty string, which should still be returned
    assertThat("Should return empty string for name with only prefix", parsedAddress, is(equalTo("")));
  }

  /**
   * Verifies that VideographerPrincipal can be serialized and deserialized.
   *
   * <p>This is important for session persistence and distributed security contexts.
   */
  @Test
  public void testSerialization() throws Exception {
    VideographerPrincipal original = new VideographerPrincipal(TEST_IP_ADDRESS);

    // Serialize
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(original);
    oos.close();

    // Deserialize
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bais);
    VideographerPrincipal deserialized = (VideographerPrincipal) ois.readObject();
    ois.close();

    // Verify
    assertThat("Deserialized principal should not be null", deserialized, is(notNullValue()));
    assertThat(
        "Deserialized name should match original",
        deserialized.getName(),
        is(equalTo(original.getName())));
    assertThat(
        "Deserialized address should match original",
        deserialized.getAddress(),
        is(equalTo(original.getAddress())));
  }

  /**
   * Verifies that VideographerPrincipal works with special IP addresses.
   */
  @Test
  public void testWithSpecialIpAddresses() {
    // Test with 0.0.0.0 (any address)
    VideographerPrincipal anyAddress = new VideographerPrincipal("0.0.0.0");
    assertThat("Should handle 0.0.0.0", anyAddress.getName(), is(equalTo("Videographer@0.0.0.0")));

    // Test with 255.255.255.255 (broadcast)
    VideographerPrincipal broadcast = new VideographerPrincipal("255.255.255.255");
    assertThat(
        "Should handle broadcast address",
        broadcast.getName(),
        is(equalTo("Videographer@255.255.255.255")));

    // Test with localhost
    VideographerPrincipal localhost = new VideographerPrincipal("localhost");
    assertThat("Should handle localhost", localhost.getName(), is(equalTo("Videographer@localhost")));
  }

  /**
   * Verifies behavior with empty string as address.
   */
  @Test
  public void testWithEmptyAddress() {
    VideographerPrincipal principal = new VideographerPrincipal("");

    assertThat("Should create principal with empty address", principal.getName(), is(equalTo("Videographer@")));
    assertThat("Address should be empty string", principal.getAddress(), is(equalTo("")));
  }

  /**
   * Verifies that the principal name can be round-tripped through parsing.
   */
  @Test
  public void testNameRoundTrip() {
    VideographerPrincipal original = new VideographerPrincipal(TEST_IP_ADDRESS);
    String name = original.getName();
    String parsedAddress = VideographerPrincipal.parseAddressFromName(name);
    VideographerPrincipal reconstructed = new VideographerPrincipal(parsedAddress);

    assertThat(
        "Reconstructed principal should have same name",
        reconstructed.getName(),
        is(equalTo(original.getName())));
    assertThat(
        "Reconstructed principal should have same address",
        reconstructed.getAddress(),
        is(equalTo(original.getAddress())));
  }
}
