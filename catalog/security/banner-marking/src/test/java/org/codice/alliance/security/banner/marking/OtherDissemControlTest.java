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
 * Unit tests for {@link OtherDissemControl} enum.
 *
 * <p>OtherDissemControl represents additional dissemination controls applied to classified and
 * unclassified information:
 *
 * <ul>
 *   <li>ACCM - ACCM
 *   <li>EXDIS - Exclusive Distribution
 *   <li>LIMDIS - Limited Distribution
 *   <li>NODIS - No Distribution
 *   <li>SBU - Sensitive But Unclassified
 *   <li>SBU_NOFORN - Sensitive But Unclassified NOFORN
 *   <li>LES - Law Enforcement Sensitive
 *   <li>LES_NOFORN - Law Enforcement Sensitive NOFORN
 *   <li>SSI - Sensitive Security Information
 * </ul>
 *
 * <p><b>Coverage Target:</b> 95%
 *
 * <p><b>Security Importance:</b> Other dissemination controls restrict distribution of information
 * beyond standard classification markings. Proper parsing and validation are critical.
 */
public class OtherDissemControlTest {

  // ==========================================================================
  // Enum Values Tests
  // ==========================================================================

  /**
   * Verifies that all nine other dissemination control constants exist.
   *
   * <p>Tests that the values() method returns exactly nine constants: ACCM, EXDIS, LIMDIS, NODIS,
   * SBU, SBU_NOFORN, LES, LES_NOFORN, and SSI.
   */
  @Test
  public void testEnumValues() {
    OtherDissemControl[] values = OtherDissemControl.values();

    assertThat("OtherDissemControl should have exactly 9 constants", values.length, is(9));
    assertThat("ACCM should exist", OtherDissemControl.ACCM, notNullValue());
    assertThat("EXDIS should exist", OtherDissemControl.EXDIS, notNullValue());
    assertThat("LIMDIS should exist", OtherDissemControl.LIMDIS, notNullValue());
    assertThat("NODIS should exist", OtherDissemControl.NODIS, notNullValue());
    assertThat("SBU should exist", OtherDissemControl.SBU, notNullValue());
    assertThat("SBU_NOFORN should exist", OtherDissemControl.SBU_NOFORN, notNullValue());
    assertThat("LES should exist", OtherDissemControl.LES, notNullValue());
    assertThat("LES_NOFORN should exist", OtherDissemControl.LES_NOFORN, notNullValue());
    assertThat("SSI should exist", OtherDissemControl.SSI, notNullValue());
  }

  /**
   * Verifies the order of enum constants.
   *
   * <p>Tests that enum constants are in declaration order.
   */
  @Test
  public void testEnumOrder() {
    OtherDissemControl[] values = OtherDissemControl.values();

    assertThat("First value should be ACCM", values[0], is(OtherDissemControl.ACCM));
    assertThat("Second value should be EXDIS", values[1], is(OtherDissemControl.EXDIS));
    assertThat("Third value should be LIMDIS", values[2], is(OtherDissemControl.LIMDIS));
    assertThat("Fourth value should be NODIS", values[3], is(OtherDissemControl.NODIS));
    assertThat("Fifth value should be SBU", values[4], is(OtherDissemControl.SBU));
    assertThat("Sixth value should be SBU_NOFORN", values[5], is(OtherDissemControl.SBU_NOFORN));
    assertThat("Seventh value should be LES", values[6], is(OtherDissemControl.LES));
    assertThat("Eighth value should be LES_NOFORN", values[7], is(OtherDissemControl.LES_NOFORN));
    assertThat("Ninth value should be SSI", values[8], is(OtherDissemControl.SSI));
  }

  /** Verifies that valueOf() works correctly for all enum constants. */
  @Test
  public void testValueOf() {
    assertThat(OtherDissemControl.valueOf("ACCM"), is(OtherDissemControl.ACCM));
    assertThat(OtherDissemControl.valueOf("EXDIS"), is(OtherDissemControl.EXDIS));
    assertThat(OtherDissemControl.valueOf("LIMDIS"), is(OtherDissemControl.LIMDIS));
    assertThat(OtherDissemControl.valueOf("NODIS"), is(OtherDissemControl.NODIS));
    assertThat(OtherDissemControl.valueOf("SBU"), is(OtherDissemControl.SBU));
    assertThat(OtherDissemControl.valueOf("SBU_NOFORN"), is(OtherDissemControl.SBU_NOFORN));
    assertThat(OtherDissemControl.valueOf("LES"), is(OtherDissemControl.LES));
    assertThat(OtherDissemControl.valueOf("LES_NOFORN"), is(OtherDissemControl.LES_NOFORN));
    assertThat(OtherDissemControl.valueOf("SSI"), is(OtherDissemControl.SSI));
  }

  /** Verifies that valueOf() throws IllegalArgumentException for invalid constant name. */
  @Test(expected = IllegalArgumentException.class)
  public void testValueOfInvalidNameThrowsException() {
    OtherDissemControl.valueOf("INVALID");
  }

  // ==========================================================================
  // getName() Tests
  // ==========================================================================

  /** Verifies that getName() returns the banner name for ACCM. */
  @Test
  public void testGetNameAccm() {
    assertThat(OtherDissemControl.ACCM.getName(), is("ACCM"));
  }

  /** Verifies that getName() returns the banner name for EXDIS. */
  @Test
  public void testGetNameExdis() {
    assertThat(OtherDissemControl.EXDIS.getName(), is("EXDIS"));
  }

  /** Verifies that getName() returns the banner name for LIMDIS. */
  @Test
  public void testGetNameLimdis() {
    assertThat(OtherDissemControl.LIMDIS.getName(), is("LIMDIS"));
  }

  /** Verifies that getName() returns the banner name for NODIS. */
  @Test
  public void testGetNameNodis() {
    assertThat(OtherDissemControl.NODIS.getName(), is("NODIS"));
  }

  /** Verifies that getName() returns the banner name for SBU. */
  @Test
  public void testGetNameSbu() {
    assertThat(OtherDissemControl.SBU.getName(), is("SBU"));
  }

  /** Verifies that getName() returns the banner name for SBU_NOFORN. */
  @Test
  public void testGetNameSbuNoforn() {
    assertThat(OtherDissemControl.SBU_NOFORN.getName(), is("SBU NOFORN"));
  }

  /** Verifies that getName() returns the banner name for LES. */
  @Test
  public void testGetNameLes() {
    assertThat(OtherDissemControl.LES.getName(), is("LES"));
  }

  /** Verifies that getName() returns the banner name for LES_NOFORN. */
  @Test
  public void testGetNameLesNoforn() {
    assertThat(OtherDissemControl.LES_NOFORN.getName(), is("LES NOFORN"));
  }

  /** Verifies that getName() returns the banner name for SSI. */
  @Test
  public void testGetNameSsi() {
    assertThat(OtherDissemControl.SSI.getName(), is("SSI"));
  }

  // ==========================================================================
  // lookupBannerName() Tests - Valid Cases
  // ==========================================================================

  /** Verifies that lookupBannerName() finds ACCM by abbreviation. */
  @Test
  public void testLookupBannerNameAccm() {
    assertThat(OtherDissemControl.lookupBannerName("ACCM"), is(OtherDissemControl.ACCM));
  }

  /** Verifies that lookupBannerName() finds EXDIS by abbreviation "EXDIS". */
  @Test
  public void testLookupBannerNameExdisAbbreviation() {
    assertThat(OtherDissemControl.lookupBannerName("EXDIS"), is(OtherDissemControl.EXDIS));
  }

  /** Verifies that lookupBannerName() finds EXDIS by full name "EXCLUSIVE DISTRIBUTION". */
  @Test
  public void testLookupBannerNameExdisFullName() {
    assertThat(
        OtherDissemControl.lookupBannerName("EXCLUSIVE DISTRIBUTION"),
        is(OtherDissemControl.EXDIS));
  }

  /** Verifies that lookupBannerName() finds LIMDIS by abbreviation "LIMDIS". */
  @Test
  public void testLookupBannerNameLimdisAbbreviation() {
    assertThat(OtherDissemControl.lookupBannerName("LIMDIS"), is(OtherDissemControl.LIMDIS));
  }

  /** Verifies that lookupBannerName() finds LIMDIS by full name "LIMITED DISTRIBUTION". */
  @Test
  public void testLookupBannerNameLimdisFullName() {
    assertThat(
        OtherDissemControl.lookupBannerName("LIMITED DISTRIBUTION"), is(OtherDissemControl.LIMDIS));
  }

  /** Verifies that lookupBannerName() finds NODIS by abbreviation "NODIS". */
  @Test
  public void testLookupBannerNameNodisAbbreviation() {
    assertThat(OtherDissemControl.lookupBannerName("NODIS"), is(OtherDissemControl.NODIS));
  }

  /** Verifies that lookupBannerName() finds NODIS by full name "NO DISTRIBUTION". */
  @Test
  public void testLookupBannerNameNodisFullName() {
    assertThat(
        OtherDissemControl.lookupBannerName("NO DISTRIBUTION"), is(OtherDissemControl.NODIS));
  }

  /** Verifies that lookupBannerName() finds SBU by abbreviation "SBU". */
  @Test
  public void testLookupBannerNameSbuAbbreviation() {
    assertThat(OtherDissemControl.lookupBannerName("SBU"), is(OtherDissemControl.SBU));
  }

  /** Verifies that lookupBannerName() finds SBU by full name "SENSITIVE BUT UNCLASSIFIED". */
  @Test
  public void testLookupBannerNameSbuFullName() {
    assertThat(
        OtherDissemControl.lookupBannerName("SENSITIVE BUT UNCLASSIFIED"),
        is(OtherDissemControl.SBU));
  }

  /** Verifies that lookupBannerName() finds SBU_NOFORN by "SBU NOFORN". */
  @Test
  public void testLookupBannerNameSbuNofornAbbreviation() {
    assertThat(
        OtherDissemControl.lookupBannerName("SBU NOFORN"), is(OtherDissemControl.SBU_NOFORN));
  }

  /** Verifies that lookupBannerName() finds SBU_NOFORN by full name. */
  @Test
  public void testLookupBannerNameSbuNofornFullName() {
    assertThat(
        OtherDissemControl.lookupBannerName("SENSITIVE BUT UNCLASSIFIED NOFORN"),
        is(OtherDissemControl.SBU_NOFORN));
  }

  /** Verifies that lookupBannerName() finds LES by abbreviation "LES". */
  @Test
  public void testLookupBannerNameLesAbbreviation() {
    assertThat(OtherDissemControl.lookupBannerName("LES"), is(OtherDissemControl.LES));
  }

  /** Verifies that lookupBannerName() finds LES by full name (note trailing space in source). */
  @Test
  public void testLookupBannerNameLesFullName() {
    assertThat(
        OtherDissemControl.lookupBannerName("LAW ENFORCEMENT SENSITIVE "),
        is(OtherDissemControl.LES));
  }

  /** Verifies that lookupBannerName() finds LES_NOFORN by "LES NOFORN". */
  @Test
  public void testLookupBannerNameLesNofornAbbreviation() {
    assertThat(
        OtherDissemControl.lookupBannerName("LES NOFORN"), is(OtherDissemControl.LES_NOFORN));
  }

  /**
   * Verifies that lookupBannerName() finds LES_NOFORN by full name (note trailing space in source).
   */
  @Test
  public void testLookupBannerNameLesNofornFullName() {
    assertThat(
        OtherDissemControl.lookupBannerName("LAW ENFORCEMENT SENSITIVE NOFORN "),
        is(OtherDissemControl.LES_NOFORN));
  }

  /** Verifies that lookupBannerName() finds SSI by abbreviation "SSI". */
  @Test
  public void testLookupBannerNameSsiAbbreviation() {
    assertThat(OtherDissemControl.lookupBannerName("SSI"), is(OtherDissemControl.SSI));
  }

  /** Verifies that lookupBannerName() finds SSI by full name "SENSITIVE SECURITY INFORMATION". */
  @Test
  public void testLookupBannerNameSsiFullName() {
    assertThat(
        OtherDissemControl.lookupBannerName("SENSITIVE SECURITY INFORMATION"),
        is(OtherDissemControl.SSI));
  }

  // ==========================================================================
  // lookupBannerName() Tests - Negative Cases
  // ==========================================================================

  /** Verifies that lookupBannerName() returns null for invalid input. */
  @Test
  public void testLookupBannerNameInvalidInput() {
    assertThat(OtherDissemControl.lookupBannerName("INVALID"), is(nullValue()));
    assertThat(OtherDissemControl.lookupBannerName("SECRET"), is(nullValue()));
    assertThat(OtherDissemControl.lookupBannerName("FOUO"), is(nullValue()));
  }

  /** Verifies that lookupBannerName() returns null for empty string. */
  @Test
  public void testLookupBannerNameEmptyString() {
    assertThat(OtherDissemControl.lookupBannerName(""), is(nullValue()));
  }

  /**
   * Verifies that lookupBannerName() handles null input gracefully.
   *
   * <p>The implementation uses stream filtering with contains(), which handles null without
   * throwing NPE and returns null.
   */
  @Test
  public void testLookupBannerNameNullReturnsNull() {
    assertThat(OtherDissemControl.lookupBannerName(null), is(nullValue()));
  }

  // ==========================================================================
  // lookupPortionName() Tests - Valid Cases
  // ==========================================================================

  /** Verifies that lookupPortionName() finds ACCM by portion marker "ACCM". */
  @Test
  public void testLookupPortionNameAccm() {
    assertThat(OtherDissemControl.lookupPortionName("ACCM"), is(OtherDissemControl.ACCM));
  }

  /** Verifies that lookupPortionName() finds EXDIS by portion marker "XD". */
  @Test
  public void testLookupPortionNameExdis() {
    assertThat(OtherDissemControl.lookupPortionName("XD"), is(OtherDissemControl.EXDIS));
  }

  /** Verifies that lookupPortionName() finds LIMDIS by portion marker "DS". */
  @Test
  public void testLookupPortionNameLimdis() {
    assertThat(OtherDissemControl.lookupPortionName("DS"), is(OtherDissemControl.LIMDIS));
  }

  /** Verifies that lookupPortionName() finds NODIS by portion marker "ND". */
  @Test
  public void testLookupPortionNameNodis() {
    assertThat(OtherDissemControl.lookupPortionName("ND"), is(OtherDissemControl.NODIS));
  }

  /** Verifies that lookupPortionName() finds SBU by portion marker "SBU". */
  @Test
  public void testLookupPortionNameSbu() {
    assertThat(OtherDissemControl.lookupPortionName("SBU"), is(OtherDissemControl.SBU));
  }

  /** Verifies that lookupPortionName() finds SBU_NOFORN by portion marker "SBU-NF". */
  @Test
  public void testLookupPortionNameSbuNoforn() {
    assertThat(OtherDissemControl.lookupPortionName("SBU-NF"), is(OtherDissemControl.SBU_NOFORN));
  }

  /** Verifies that lookupPortionName() finds LES by portion marker "LES". */
  @Test
  public void testLookupPortionNameLes() {
    assertThat(OtherDissemControl.lookupPortionName("LES"), is(OtherDissemControl.LES));
  }

  /** Verifies that lookupPortionName() finds LES_NOFORN by portion marker "LES-NF". */
  @Test
  public void testLookupPortionNameLesNoforn() {
    assertThat(OtherDissemControl.lookupPortionName("LES-NF"), is(OtherDissemControl.LES_NOFORN));
  }

  /** Verifies that lookupPortionName() finds SSI by portion marker "SSI". */
  @Test
  public void testLookupPortionNameSsi() {
    assertThat(OtherDissemControl.lookupPortionName("SSI"), is(OtherDissemControl.SSI));
  }

  // ==========================================================================
  // lookupPortionName() Tests - Negative Cases
  // ==========================================================================

  /** Verifies that lookupPortionName() returns null for invalid input. */
  @Test
  public void testLookupPortionNameInvalidInput() {
    assertThat(OtherDissemControl.lookupPortionName("INVALID"), is(nullValue()));
    assertThat(OtherDissemControl.lookupPortionName("S"), is(nullValue()));
    assertThat(OtherDissemControl.lookupPortionName("FD"), is(nullValue()));
  }

  /** Verifies that lookupPortionName() returns null for empty string. */
  @Test
  public void testLookupPortionNameEmptyString() {
    assertThat(OtherDissemControl.lookupPortionName(""), is(nullValue()));
  }

  /**
   * Verifies that lookupPortionName() handles null input gracefully.
   *
   * <p>The implementation uses stream filtering with contains(), which handles null without
   * throwing NPE and returns null.
   */
  @Test
  public void testLookupPortionNameNullReturnsNull() {
    assertThat(OtherDissemControl.lookupPortionName(null), is(nullValue()));
  }

  // ==========================================================================
  // prefixBannerMatch() Tests
  // ==========================================================================

  /** Verifies that prefixBannerMatch() returns true for exact matches. */
  @Test
  public void testPrefixBannerMatchExact() {
    assertThat("EXDIS should match", OtherDissemControl.prefixBannerMatch("EXDIS"), is(true));
    assertThat("NODIS should match", OtherDissemControl.prefixBannerMatch("NODIS"), is(true));
    assertThat("LIMDIS should match", OtherDissemControl.prefixBannerMatch("LIMDIS"), is(true));
    assertThat(
        "EXCLUSIVE DISTRIBUTION should match",
        OtherDissemControl.prefixBannerMatch("EXCLUSIVE DISTRIBUTION"),
        is(true));
  }

  /**
   * Verifies that prefixBannerMatch() returns true for prefix matches.
   *
   * <p>The method uses startsWith(), so values beginning with banner names should match.
   */
  @Test
  public void testPrefixBannerMatchPrefix() {
    assertThat(
        "EXDIS with suffix should match",
        OtherDissemControl.prefixBannerMatch("EXDIS-ALPHA"),
        is(true));
    assertThat(
        "SBU with data should match",
        OtherDissemControl.prefixBannerMatch("SBU INFORMATION"),
        is(true));
    assertThat(
        "LES with suffix should match", OtherDissemControl.prefixBannerMatch("LES DATA"), is(true));
  }

  /** Verifies that prefixBannerMatch() returns false for non-matches. */
  @Test
  public void testPrefixBannerMatchNoMatch() {
    assertThat(
        "INVALID should not match", OtherDissemControl.prefixBannerMatch("INVALID"), is(false));
    assertThat(
        "SECRET should not match", OtherDissemControl.prefixBannerMatch("SECRET"), is(false));
    assertThat("Empty should not match", OtherDissemControl.prefixBannerMatch(""), is(false));
  }

  /**
   * Verifies that prefixBannerMatch() does not match substrings.
   *
   * <p>Since it uses startsWith(), "DISTRIBUTION" should not match "LIMITED DISTRIBUTION".
   */
  @Test
  public void testPrefixBannerMatchDoesNotMatchSubstring() {
    assertThat(
        "Substring 'DISTRIBUTION' should not match",
        OtherDissemControl.prefixBannerMatch("DISTRIBUTION"),
        is(false));
    assertThat(
        "Substring 'DIS' should not match", OtherDissemControl.prefixBannerMatch("DIS"), is(false));
  }

  /**
   * Verifies that prefixBannerMatch() handles null input.
   *
   * <p>Note: The implementation uses startsWith() which will throw NullPointerException for null
   * input. This test documents current behavior.
   */
  @Test(expected = NullPointerException.class)
  public void testPrefixBannerMatchNullThrowsException() {
    OtherDissemControl.prefixBannerMatch(null);
  }

  // ==========================================================================
  // prefixPortionMatch() Tests
  // ==========================================================================

  /** Verifies that prefixPortionMatch() returns true for exact matches. */
  @Test
  public void testPrefixPortionMatchExact() {
    assertThat("XD should match", OtherDissemControl.prefixPortionMatch("XD"), is(true));
    assertThat("ND should match", OtherDissemControl.prefixPortionMatch("ND"), is(true));
    assertThat("DS should match", OtherDissemControl.prefixPortionMatch("DS"), is(true));
    assertThat("SBU should match", OtherDissemControl.prefixPortionMatch("SBU"), is(true));
    assertThat("SBU-NF should match", OtherDissemControl.prefixPortionMatch("SBU-NF"), is(true));
    assertThat("LES should match", OtherDissemControl.prefixPortionMatch("LES"), is(true));
    assertThat("LES-NF should match", OtherDissemControl.prefixPortionMatch("LES-NF"), is(true));
    assertThat("SSI should match", OtherDissemControl.prefixPortionMatch("SSI"), is(true));
  }

  /**
   * Verifies that prefixPortionMatch() returns true for prefix matches.
   *
   * <p>The method uses startsWith(), so values beginning with portion names should match.
   */
  @Test
  public void testPrefixPortionMatchPrefix() {
    assertThat(
        "XD with suffix should match", OtherDissemControl.prefixPortionMatch("XD-DATA"), is(true));
    assertThat(
        "SBU with suffix should match",
        OtherDissemControl.prefixPortionMatch("SBU-INFO"),
        is(true));
    assertThat(
        "LES with suffix should match",
        OtherDissemControl.prefixPortionMatch("LES-CASE"),
        is(true));
  }

  /** Verifies that prefixPortionMatch() returns false for non-matches. */
  @Test
  public void testPrefixPortionMatchNoMatch() {
    assertThat(
        "INVALID should not match", OtherDissemControl.prefixPortionMatch("INVALID"), is(false));
    assertThat("S should not match", OtherDissemControl.prefixPortionMatch("S"), is(false));
    assertThat("Empty should not match", OtherDissemControl.prefixPortionMatch(""), is(false));
  }

  /**
   * Verifies that prefixPortionMatch() handles null input.
   *
   * <p>Note: The implementation uses startsWith() which will throw NullPointerException for null
   * input. This test documents current behavior.
   */
  @Test(expected = NullPointerException.class)
  public void testPrefixPortionMatchNullThrowsException() {
    OtherDissemControl.prefixPortionMatch(null);
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
    assertThat(OtherDissemControl.valueOf("EXDIS"), is(sameInstance(OtherDissemControl.EXDIS)));
    assertThat(
        OtherDissemControl.lookupBannerName("NODIS"), is(sameInstance(OtherDissemControl.NODIS)));
    assertThat(
        OtherDissemControl.lookupPortionName("XD"), is(sameInstance(OtherDissemControl.EXDIS)));
    assertThat(
        OtherDissemControl.lookupPortionName("SBU-NF"),
        is(sameInstance(OtherDissemControl.SBU_NOFORN)));
  }

  /** Verifies ordinal values of enum constants. */
  @Test
  public void testOrdinalValues() {
    assertThat(OtherDissemControl.ACCM.ordinal(), is(0));
    assertThat(OtherDissemControl.EXDIS.ordinal(), is(1));
    assertThat(OtherDissemControl.LIMDIS.ordinal(), is(2));
    assertThat(OtherDissemControl.NODIS.ordinal(), is(3));
    assertThat(OtherDissemControl.SBU.ordinal(), is(4));
    assertThat(OtherDissemControl.SBU_NOFORN.ordinal(), is(5));
    assertThat(OtherDissemControl.LES.ordinal(), is(6));
    assertThat(OtherDissemControl.LES_NOFORN.ordinal(), is(7));
    assertThat(OtherDissemControl.SSI.ordinal(), is(8));
  }
}
