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
package org.codice.alliance.security.banner.marking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.Test;

/**
 * Unit tests for {@link AeaType} enum.
 *
 * <p>AeaType represents the five types of Atomic Energy Act markings:
 *
 * <ul>
 *   <li>RD - Restricted Data
 *   <li>FRD - Formerly Restricted Data
 *   <li>DOD_UCNI - DoD Unclassified Controlled Nuclear Information
 *   <li>DOE_UCNI - DoE Unclassified Controlled Nuclear Information
 *   <li>TFNI - Transclassified Foreign Nuclear Information
 * </ul>
 *
 * <p><b>Coverage Target:</b> 95%
 *
 * <p><b>Security Importance:</b> AEA markings indicate special nuclear information handling
 * requirements under the Atomic Energy Act.
 */
public class AeaTypeTest {

  // ==========================================================================
  // Enum Values Tests
  // ==========================================================================

  /**
   * Verifies that all five AEA type constants exist.
   *
   * <p>Tests that the values() method returns exactly five constants: RD, FRD, DOD_UCNI, DOE_UCNI,
   * and TFNI.
   */
  @Test
  public void testEnumValues() {
    AeaType[] values = AeaType.values();

    assertThat("AeaType should have exactly 5 constants", values.length, is(5));
    assertThat("RD should exist", AeaType.RD, notNullValue());
    assertThat("FRD should exist", AeaType.FRD, notNullValue());
    assertThat("DOD_UCNI should exist", AeaType.DOD_UCNI, notNullValue());
    assertThat("DOE_UCNI should exist", AeaType.DOE_UCNI, notNullValue());
    assertThat("TFNI should exist", AeaType.TFNI, notNullValue());
  }

  /**
   * Verifies the order of enum constants.
   *
   * <p>Tests that enum constants are in declaration order: RD, FRD, DOD_UCNI, DOE_UCNI, TFNI.
   */
  @Test
  public void testEnumOrder() {
    AeaType[] values = AeaType.values();

    assertThat("First value should be RD", values[0], is(AeaType.RD));
    assertThat("Second value should be FRD", values[1], is(AeaType.FRD));
    assertThat("Third value should be DOD_UCNI", values[2], is(AeaType.DOD_UCNI));
    assertThat("Fourth value should be DOE_UCNI", values[3], is(AeaType.DOE_UCNI));
    assertThat("Fifth value should be TFNI", values[4], is(AeaType.TFNI));
  }

  /** Verifies that valueOf() works correctly for all enum constants. */
  @Test
  public void testValueOf() {
    assertThat(AeaType.valueOf("RD"), is(AeaType.RD));
    assertThat(AeaType.valueOf("FRD"), is(AeaType.FRD));
    assertThat(AeaType.valueOf("DOD_UCNI"), is(AeaType.DOD_UCNI));
    assertThat(AeaType.valueOf("DOE_UCNI"), is(AeaType.DOE_UCNI));
    assertThat(AeaType.valueOf("TFNI"), is(AeaType.TFNI));
  }

  /** Verifies that valueOf() throws IllegalArgumentException for invalid constant name. */
  @Test(expected = IllegalArgumentException.class)
  public void testValueOfInvalidNameThrowsException() {
    AeaType.valueOf("INVALID");
  }

  // ==========================================================================
  // getName() Tests
  // ==========================================================================

  /** Verifies that getName() returns the long form name for RD. */
  @Test
  public void testGetNameRd() {
    assertThat(AeaType.RD.getName(), is("RESTRICTED DATA"));
  }

  /** Verifies that getName() returns the long form name for FRD. */
  @Test
  public void testGetNameFrd() {
    assertThat(AeaType.FRD.getName(), is("FORMERLY RESTRICTED DATA"));
  }

  /** Verifies that getName() returns the long form name for DOD_UCNI. */
  @Test
  public void testGetNameDodUcni() {
    assertThat(AeaType.DOD_UCNI.getName(), is("DOD UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION"));
  }

  /** Verifies that getName() returns the long form name for DOE_UCNI. */
  @Test
  public void testGetNameDoeUcni() {
    assertThat(AeaType.DOE_UCNI.getName(), is("DOE UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION"));
  }

  /** Verifies that getName() returns the long form name for TFNI. */
  @Test
  public void testGetNameTfni() {
    assertThat(AeaType.TFNI.getName(), is("TRANSCLASSIFIED FOREIGN NUCLEAR INFORMATION"));
  }

  // ==========================================================================
  // lookupType() Tests - Full Banner Names
  // ==========================================================================

  /** Verifies that lookupType() finds RD by full name "RESTRICTED DATA". */
  @Test
  public void testLookupTypeRdFullName() {
    assertThat(AeaType.lookupType("RESTRICTED DATA"), is(AeaType.RD));
  }

  /** Verifies that lookupType() finds RD by abbreviation "RD". */
  @Test
  public void testLookupTypeRdAbbreviation() {
    assertThat(AeaType.lookupType("RD"), is(AeaType.RD));
  }

  /** Verifies that lookupType() finds FRD by full name "FORMERLY RESTRICTED DATA". */
  @Test
  public void testLookupTypeFrdFullName() {
    assertThat(AeaType.lookupType("FORMERLY RESTRICTED DATA"), is(AeaType.FRD));
  }

  /** Verifies that lookupType() finds FRD by abbreviation "FRD". */
  @Test
  public void testLookupTypeFrdAbbreviation() {
    assertThat(AeaType.lookupType("FRD"), is(AeaType.FRD));
  }

  /** Verifies that lookupType() finds DOD_UCNI by full name. */
  @Test
  public void testLookupTypeDodUcniFullName() {
    assertThat(
        AeaType.lookupType("DOD UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION"),
        is(AeaType.DOD_UCNI));
  }

  /** Verifies that lookupType() finds DOD_UCNI by abbreviation "DOD UCNI". */
  @Test
  public void testLookupTypeDodUcniAbbreviation() {
    assertThat(AeaType.lookupType("DOD UCNI"), is(AeaType.DOD_UCNI));
  }

  /** Verifies that lookupType() finds DOD_UCNI by portion marker "DCNI". */
  @Test
  public void testLookupTypeDodUcniPortionMarker() {
    assertThat(AeaType.lookupType("DCNI"), is(AeaType.DOD_UCNI));
  }

  /** Verifies that lookupType() finds DOE_UCNI by full name. */
  @Test
  public void testLookupTypeDoeUcniFullName() {
    assertThat(
        AeaType.lookupType("DOE UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION"),
        is(AeaType.DOE_UCNI));
  }

  /** Verifies that lookupType() finds DOE_UCNI by abbreviation "DOE UCNI". */
  @Test
  public void testLookupTypeDoeUcniAbbreviation() {
    assertThat(AeaType.lookupType("DOE UCNI"), is(AeaType.DOE_UCNI));
  }

  /** Verifies that lookupType() finds DOE_UCNI by portion marker "UCNI". */
  @Test
  public void testLookupTypeDoeUcniPortionMarker() {
    assertThat(AeaType.lookupType("UCNI"), is(AeaType.DOE_UCNI));
  }

  /** Verifies that lookupType() finds TFNI by full name. */
  @Test
  public void testLookupTypeTfniFullName() {
    assertThat(AeaType.lookupType("TRANSCLASSIFIED FOREIGN NUCLEAR INFORMATION"), is(AeaType.TFNI));
  }

  /** Verifies that lookupType() finds TFNI by abbreviation "TFNI". */
  @Test
  public void testLookupTypeTfniAbbreviation() {
    assertThat(AeaType.lookupType("TFNI"), is(AeaType.TFNI));
  }

  // ==========================================================================
  // lookupType() Tests - Prefix Matching
  // ==========================================================================

  /**
   * Verifies that lookupType() uses prefix matching.
   *
   * <p>The method uses startsWith(), so "FRD-XXXX" should match FRD.
   */
  @Test
  public void testLookupTypePrefixMatchingFrd() {
    assertThat("FRD with suffix should match", AeaType.lookupType("FRD-XXXX"), is(AeaType.FRD));
    assertThat("FRD with data should match", AeaType.lookupType("FRD SIGMA 14"), is(AeaType.FRD));
  }

  /** Verifies that lookupType() prefix matching works for RD. */
  @Test
  public void testLookupTypePrefixMatchingRd() {
    assertThat("RD with suffix should match", AeaType.lookupType("RD-CNWDI"), is(AeaType.RD));
    assertThat(
        "RESTRICTED DATA with suffix should match",
        AeaType.lookupType("RESTRICTED DATA-NSI"),
        is(AeaType.RD));
  }

  // ==========================================================================
  // lookupType() Tests - Negative Cases
  // ==========================================================================

  /** Verifies that lookupType() returns null for invalid input. */
  @Test
  public void testLookupTypeInvalidInput() {
    assertThat(AeaType.lookupType("INVALID"), is(nullValue()));
    assertThat(AeaType.lookupType("SECRET"), is(nullValue()));
    assertThat(AeaType.lookupType("CLASSIFIED"), is(nullValue()));
  }

  /** Verifies that lookupType() returns null for empty string. */
  @Test
  public void testLookupTypeEmptyString() {
    assertThat(AeaType.lookupType(""), is(nullValue()));
  }

  /**
   * Verifies that lookupType() handles null input gracefully.
   *
   * <p>Note: The implementation uses startsWith() which will throw NullPointerException for null
   * input. This test documents current behavior.
   */
  @Test(expected = NullPointerException.class)
  public void testLookupTypeNullThrowsException() {
    AeaType.lookupType(null);
  }

  /**
   * Verifies that lookupType() does not match partial substrings.
   *
   * <p>Since it uses startsWith(), "DATA" should not match "RESTRICTED DATA".
   */
  @Test
  public void testLookupTypeDoesNotMatchSubstring() {
    assertThat("Substring 'DATA' should not match", AeaType.lookupType("DATA"), is(nullValue()));
    assertThat(
        "Substring 'UCNI' alone matches DOE_UCNI portion marker",
        AeaType.lookupType("UCNI"),
        is(AeaType.DOE_UCNI));
  }

  // ==========================================================================
  // Enum Identity Tests
  // ==========================================================================

  /**
   * Verifies that enum constants maintain identity.
   *
   * <p>Enum singletons should maintain reference equality.
   */
  @Test
  public void testEnumIdentity() {
    assertThat(AeaType.valueOf("RD"), is(sameInstance(AeaType.RD)));
    assertThat(AeaType.lookupType("FRD"), is(sameInstance(AeaType.FRD)));
    assertThat(AeaType.lookupType("DCNI"), is(sameInstance(AeaType.DOD_UCNI)));
  }

  /** Verifies ordinal values of enum constants. */
  @Test
  public void testOrdinalValues() {
    assertThat(AeaType.RD.ordinal(), is(0));
    assertThat(AeaType.FRD.ordinal(), is(1));
    assertThat(AeaType.DOD_UCNI.ordinal(), is(2));
    assertThat(AeaType.DOE_UCNI.ordinal(), is(3));
    assertThat(AeaType.TFNI.ordinal(), is(4));
  }
}
