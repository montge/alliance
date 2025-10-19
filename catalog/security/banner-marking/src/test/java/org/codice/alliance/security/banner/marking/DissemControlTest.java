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
 * Unit tests for {@link DissemControl} enum.
 *
 * <p>DissemControl represents standard dissemination controls that restrict how classified
 * information can be shared:
 *
 * <ul>
 *   <li>RSEN - Risk Sensitive
 *   <li>IMCON - Controlled Imagery
 *   <li>NOFORN - Not Releasable to Foreign Nationals
 *   <li>PROPIN - Caution-Proprietary Information Involved
 *   <li>RELIDO - Releasable by Information Disclosure Official
 *   <li>FISA - Foreign Intelligence Surveillance Act
 *   <li>ORCON - Originator Controlled
 *   <li>DEA_SENSITIVE - DEA Sensitive
 *   <li>FOUO - For Official Use Only
 *   <li>EYES_ONLY - Eyes Only
 *   <li>WAIVED - Waived
 * </ul>
 *
 * <p><b>Coverage Target:</b> 95%
 *
 * <p><b>Security Importance:</b> Dissemination controls specify handling requirements and
 * distribution restrictions for classified and sensitive information.
 */
public class DissemControlTest {

  // ==========================================================================
  // Enum Values Tests
  // ==========================================================================

  /**
   * Verifies that all eleven dissemination control constants exist.
   *
   * <p>Tests that the values() method returns exactly eleven constants: RSEN, IMCON, NOFORN,
   * PROPIN, RELIDO, FISA, ORCON, DEA_SENSITIVE, FOUO, EYES_ONLY, and WAIVED.
   */
  @Test
  public void testEnumValues() {
    DissemControl[] values = DissemControl.values();

    assertThat("DissemControl should have exactly 11 constants", values.length, is(11));
    assertThat("RSEN should exist", DissemControl.RSEN, notNullValue());
    assertThat("IMCON should exist", DissemControl.IMCON, notNullValue());
    assertThat("NOFORN should exist", DissemControl.NOFORN, notNullValue());
    assertThat("PROPIN should exist", DissemControl.PROPIN, notNullValue());
    assertThat("RELIDO should exist", DissemControl.RELIDO, notNullValue());
    assertThat("FISA should exist", DissemControl.FISA, notNullValue());
    assertThat("ORCON should exist", DissemControl.ORCON, notNullValue());
    assertThat("DEA_SENSITIVE should exist", DissemControl.DEA_SENSITIVE, notNullValue());
    assertThat("FOUO should exist", DissemControl.FOUO, notNullValue());
    assertThat("EYES_ONLY should exist", DissemControl.EYES_ONLY, notNullValue());
    assertThat("WAIVED should exist", DissemControl.WAIVED, notNullValue());
  }

  /**
   * Verifies the order of enum constants.
   *
   * <p>Tests that enum constants are in declaration order.
   */
  @Test
  public void testEnumOrder() {
    DissemControl[] values = DissemControl.values();

    assertThat("First value should be RSEN", values[0], is(DissemControl.RSEN));
    assertThat("Second value should be IMCON", values[1], is(DissemControl.IMCON));
    assertThat("Third value should be NOFORN", values[2], is(DissemControl.NOFORN));
    assertThat("Fourth value should be PROPIN", values[3], is(DissemControl.PROPIN));
    assertThat("Fifth value should be RELIDO", values[4], is(DissemControl.RELIDO));
    assertThat("Sixth value should be FISA", values[5], is(DissemControl.FISA));
    assertThat("Seventh value should be ORCON", values[6], is(DissemControl.ORCON));
    assertThat("Eighth value should be DEA_SENSITIVE", values[7], is(DissemControl.DEA_SENSITIVE));
    assertThat("Ninth value should be FOUO", values[8], is(DissemControl.FOUO));
    assertThat("Tenth value should be EYES_ONLY", values[9], is(DissemControl.EYES_ONLY));
    assertThat("Eleventh value should be WAIVED", values[10], is(DissemControl.WAIVED));
  }

  /** Verifies that valueOf() works correctly for all enum constants. */
  @Test
  public void testValueOf() {
    assertThat(DissemControl.valueOf("RSEN"), is(DissemControl.RSEN));
    assertThat(DissemControl.valueOf("IMCON"), is(DissemControl.IMCON));
    assertThat(DissemControl.valueOf("NOFORN"), is(DissemControl.NOFORN));
    assertThat(DissemControl.valueOf("PROPIN"), is(DissemControl.PROPIN));
    assertThat(DissemControl.valueOf("RELIDO"), is(DissemControl.RELIDO));
    assertThat(DissemControl.valueOf("FISA"), is(DissemControl.FISA));
    assertThat(DissemControl.valueOf("ORCON"), is(DissemControl.ORCON));
    assertThat(DissemControl.valueOf("DEA_SENSITIVE"), is(DissemControl.DEA_SENSITIVE));
    assertThat(DissemControl.valueOf("FOUO"), is(DissemControl.FOUO));
    assertThat(DissemControl.valueOf("EYES_ONLY"), is(DissemControl.EYES_ONLY));
    assertThat(DissemControl.valueOf("WAIVED"), is(DissemControl.WAIVED));
  }

  /** Verifies that valueOf() throws IllegalArgumentException for invalid constant name. */
  @Test(expected = IllegalArgumentException.class)
  public void testValueOfInvalidNameThrowsException() {
    DissemControl.valueOf("INVALID");
  }

  // ==========================================================================
  // getName() Tests
  // ==========================================================================

  /** Verifies that getName() returns the primary banner name for RSEN. */
  @Test
  public void testGetNameRsen() {
    assertThat(DissemControl.RSEN.getName(), is("RSEN"));
  }

  /** Verifies that getName() returns the primary banner name for IMCON. */
  @Test
  public void testGetNameImcon() {
    assertThat(DissemControl.IMCON.getName(), is("IMCON"));
  }

  /** Verifies that getName() returns the primary banner name for NOFORN. */
  @Test
  public void testGetNameNoforn() {
    assertThat(DissemControl.NOFORN.getName(), is("NOFORN"));
  }

  /** Verifies that getName() returns the primary banner name for PROPIN. */
  @Test
  public void testGetNamePropin() {
    assertThat(DissemControl.PROPIN.getName(), is("PROPIN"));
  }

  /** Verifies that getName() returns the primary banner name for RELIDO. */
  @Test
  public void testGetNameRelido() {
    assertThat(DissemControl.RELIDO.getName(), is("RELIDO"));
  }

  /** Verifies that getName() returns the primary banner name for FISA. */
  @Test
  public void testGetNameFisa() {
    assertThat(DissemControl.FISA.getName(), is("FISA"));
  }

  /** Verifies that getName() returns the primary banner name for ORCON. */
  @Test
  public void testGetNameOrcon() {
    assertThat(DissemControl.ORCON.getName(), is("ORCON"));
  }

  /** Verifies that getName() returns the primary banner name for DEA_SENSITIVE. */
  @Test
  public void testGetNameDeaSensitive() {
    assertThat(DissemControl.DEA_SENSITIVE.getName(), is("DEA SENSITIVE"));
  }

  /** Verifies that getName() returns the primary banner name for FOUO. */
  @Test
  public void testGetNameFouo() {
    assertThat(DissemControl.FOUO.getName(), is("FOUO"));
  }

  /** Verifies that getName() returns the primary banner name for EYES_ONLY. */
  @Test
  public void testGetNameEyesOnly() {
    assertThat(DissemControl.EYES_ONLY.getName(), is("EYES ONLY"));
  }

  /** Verifies that getName() returns the primary banner name for WAIVED. */
  @Test
  public void testGetNameWaived() {
    assertThat(DissemControl.WAIVED.getName(), is("WAIVED"));
  }

  // ==========================================================================
  // lookupBannerName() Tests - Primary Banner Names
  // ==========================================================================

  /** Verifies that lookupBannerName() finds RSEN by abbreviation "RSEN". */
  @Test
  public void testLookupBannerNameRsenAbbreviation() {
    assertThat(DissemControl.lookupBannerName("RSEN"), is(DissemControl.RSEN));
  }

  /** Verifies that lookupBannerName() finds RSEN by full name "RISK SENSITIVE". */
  @Test
  public void testLookupBannerNameRsenFullName() {
    assertThat(DissemControl.lookupBannerName("RISK SENSITIVE"), is(DissemControl.RSEN));
  }

  /** Verifies that lookupBannerName() finds IMCON by abbreviation "IMCON". */
  @Test
  public void testLookupBannerNameImconAbbreviation() {
    assertThat(DissemControl.lookupBannerName("IMCON"), is(DissemControl.IMCON));
  }

  /** Verifies that lookupBannerName() finds IMCON by full name "CONTROLLED IMAGERY". */
  @Test
  public void testLookupBannerNameImconFullName() {
    assertThat(DissemControl.lookupBannerName("CONTROLLED IMAGERY"), is(DissemControl.IMCON));
  }

  /** Verifies that lookupBannerName() finds NOFORN by abbreviation "NOFORN". */
  @Test
  public void testLookupBannerNameNofornAbbreviation() {
    assertThat(DissemControl.lookupBannerName("NOFORN"), is(DissemControl.NOFORN));
  }

  /** Verifies that lookupBannerName() finds NOFORN by full name. */
  @Test
  public void testLookupBannerNameNofornFullName() {
    assertThat(
        DissemControl.lookupBannerName("NOT RELEASABLE TO FOREIGN NATIONALS"),
        is(DissemControl.NOFORN));
  }

  /** Verifies that lookupBannerName() finds PROPIN by abbreviation "PROPIN". */
  @Test
  public void testLookupBannerNamePropinAbbreviation() {
    assertThat(DissemControl.lookupBannerName("PROPIN"), is(DissemControl.PROPIN));
  }

  /** Verifies that lookupBannerName() finds PROPIN by full name. */
  @Test
  public void testLookupBannerNamePropinFullName() {
    assertThat(
        DissemControl.lookupBannerName("CAUTION-PROPRIETARY INFORMATION INVOLVED"),
        is(DissemControl.PROPIN));
  }

  /** Verifies that lookupBannerName() finds RELIDO by name "RELIDO". */
  @Test
  public void testLookupBannerNameRelidoAbbreviation() {
    assertThat(DissemControl.lookupBannerName("RELIDO"), is(DissemControl.RELIDO));
  }

  /** Verifies that lookupBannerName() finds RELIDO by full name. */
  @Test
  public void testLookupBannerNameRelidoFullName() {
    assertThat(
        DissemControl.lookupBannerName("RELEASABLE BY INFORMATION DISCLOSURE OFFICIAL"),
        is(DissemControl.RELIDO));
  }

  /** Verifies that lookupBannerName() finds FISA by name "FISA". */
  @Test
  public void testLookupBannerNameFisaAbbreviation() {
    assertThat(DissemControl.lookupBannerName("FISA"), is(DissemControl.FISA));
  }

  /** Verifies that lookupBannerName() finds FISA by full name. */
  @Test
  public void testLookupBannerNameFisaFullName() {
    assertThat(
        DissemControl.lookupBannerName("FOREIGN INTELLIGENCE SURVEILLANCE ACT"),
        is(DissemControl.FISA));
  }

  /** Verifies that lookupBannerName() finds ORCON by abbreviation "ORCON". */
  @Test
  public void testLookupBannerNameOrconAbbreviation() {
    assertThat(DissemControl.lookupBannerName("ORCON"), is(DissemControl.ORCON));
  }

  /** Verifies that lookupBannerName() finds ORCON by full name "ORIGINATOR CONTROLLED". */
  @Test
  public void testLookupBannerNameOrconFullName() {
    assertThat(DissemControl.lookupBannerName("ORIGINATOR CONTROLLED"), is(DissemControl.ORCON));
  }

  /** Verifies that lookupBannerName() finds DEA_SENSITIVE by name "DEA SENSITIVE". */
  @Test
  public void testLookupBannerNameDeaSensitive() {
    assertThat(DissemControl.lookupBannerName("DEA SENSITIVE"), is(DissemControl.DEA_SENSITIVE));
  }

  /** Verifies that lookupBannerName() finds FOUO by name "FOUO". */
  @Test
  public void testLookupBannerNameFouoAbbreviation() {
    assertThat(DissemControl.lookupBannerName("FOUO"), is(DissemControl.FOUO));
  }

  /** Verifies that lookupBannerName() finds FOUO by full name "FOR OFFICIAL USE ONLY". */
  @Test
  public void testLookupBannerNameFouoFullName() {
    assertThat(DissemControl.lookupBannerName("FOR OFFICIAL USE ONLY"), is(DissemControl.FOUO));
  }

  /** Verifies that lookupBannerName() finds EYES_ONLY by name "EYES ONLY". */
  @Test
  public void testLookupBannerNameEyesOnly() {
    assertThat(DissemControl.lookupBannerName("EYES ONLY"), is(DissemControl.EYES_ONLY));
  }

  /** Verifies that lookupBannerName() finds WAIVED by name "WAIVED". */
  @Test
  public void testLookupBannerNameWaived() {
    assertThat(DissemControl.lookupBannerName("WAIVED"), is(DissemControl.WAIVED));
  }

  // ==========================================================================
  // lookupPortionName() Tests - Portion Markings
  // ==========================================================================

  /** Verifies that lookupPortionName() finds RSEN by portion marking "RS". */
  @Test
  public void testLookupPortionNameRsen() {
    assertThat(DissemControl.lookupPortionName("RS"), is(DissemControl.RSEN));
  }

  /** Verifies that lookupPortionName() finds IMCON by portion marking "IMC". */
  @Test
  public void testLookupPortionNameImcon() {
    assertThat(DissemControl.lookupPortionName("IMC"), is(DissemControl.IMCON));
  }

  /** Verifies that lookupPortionName() finds NOFORN by portion marking "NF". */
  @Test
  public void testLookupPortionNameNoforn() {
    assertThat(DissemControl.lookupPortionName("NF"), is(DissemControl.NOFORN));
  }

  /** Verifies that lookupPortionName() finds PROPIN by portion marking "PR". */
  @Test
  public void testLookupPortionNamePropin() {
    assertThat(DissemControl.lookupPortionName("PR"), is(DissemControl.PROPIN));
  }

  /** Verifies that lookupPortionName() finds RELIDO by portion marking "RELIDO". */
  @Test
  public void testLookupPortionNameRelido() {
    assertThat(DissemControl.lookupPortionName("RELIDO"), is(DissemControl.RELIDO));
  }

  /** Verifies that lookupPortionName() finds FISA by portion marking "FISA". */
  @Test
  public void testLookupPortionNameFisa() {
    assertThat(DissemControl.lookupPortionName("FISA"), is(DissemControl.FISA));
  }

  /** Verifies that lookupPortionName() finds ORCON by portion marking "OC". */
  @Test
  public void testLookupPortionNameOrcon() {
    assertThat(DissemControl.lookupPortionName("OC"), is(DissemControl.ORCON));
  }

  /** Verifies that lookupPortionName() finds DEA_SENSITIVE by portion marking "DSEN". */
  @Test
  public void testLookupPortionNameDeaSensitive() {
    assertThat(DissemControl.lookupPortionName("DSEN"), is(DissemControl.DEA_SENSITIVE));
  }

  /** Verifies that lookupPortionName() finds FOUO by portion marking "FOUO". */
  @Test
  public void testLookupPortionNameFouo() {
    assertThat(DissemControl.lookupPortionName("FOUO"), is(DissemControl.FOUO));
  }

  /** Verifies that lookupPortionName() finds EYES_ONLY by portion marking "EYES". */
  @Test
  public void testLookupPortionNameEyesOnly() {
    assertThat(DissemControl.lookupPortionName("EYES"), is(DissemControl.EYES_ONLY));
  }

  /** Verifies that lookupPortionName() finds WAIVED by portion marking "WAIVED". */
  @Test
  public void testLookupPortionNameWaived() {
    assertThat(DissemControl.lookupPortionName("WAIVED"), is(DissemControl.WAIVED));
  }

  // ==========================================================================
  // lookupBannerName() Tests - Negative Cases
  // ==========================================================================

  /** Verifies that lookupBannerName() returns null for invalid input. */
  @Test
  public void testLookupBannerNameInvalidInput() {
    assertThat(DissemControl.lookupBannerName("INVALID"), is(nullValue()));
    assertThat(DissemControl.lookupBannerName("SECRET"), is(nullValue()));
    assertThat(DissemControl.lookupBannerName("TOP SECRET"), is(nullValue()));
  }

  /** Verifies that lookupBannerName() returns null for empty string. */
  @Test
  public void testLookupBannerNameEmptyString() {
    assertThat(DissemControl.lookupBannerName(""), is(nullValue()));
  }

  /** Verifies that lookupBannerName() returns null for null input. */
  @Test
  public void testLookupBannerNameNull() {
    assertThat(DissemControl.lookupBannerName(null), is(nullValue()));
  }

  /**
   * Verifies that lookupBannerName() does not match portion markings.
   *
   * <p>Banner name lookup should not find portion markings like "NF" or "OC".
   */
  @Test
  public void testLookupBannerNameDoesNotMatchPortionMarkings() {
    assertThat(
        "NF is a portion marking, not banner name",
        DissemControl.lookupBannerName("NF"),
        is(nullValue()));
    assertThat(
        "OC is a portion marking, not banner name",
        DissemControl.lookupBannerName("OC"),
        is(nullValue()));
    assertThat(
        "RS is a portion marking, not banner name",
        DissemControl.lookupBannerName("RS"),
        is(nullValue()));
  }

  // ==========================================================================
  // lookupPortionName() Tests - Negative Cases
  // ==========================================================================

  /** Verifies that lookupPortionName() returns null for invalid input. */
  @Test
  public void testLookupPortionNameInvalidInput() {
    assertThat(DissemControl.lookupPortionName("INVALID"), is(nullValue()));
    assertThat(DissemControl.lookupPortionName("SECRET"), is(nullValue()));
    assertThat(DissemControl.lookupPortionName("S"), is(nullValue()));
  }

  /** Verifies that lookupPortionName() returns null for empty string. */
  @Test
  public void testLookupPortionNameEmptyString() {
    assertThat(DissemControl.lookupPortionName(""), is(nullValue()));
  }

  /** Verifies that lookupPortionName() returns null for null input. */
  @Test
  public void testLookupPortionNameNull() {
    assertThat(DissemControl.lookupPortionName(null), is(nullValue()));
  }

  /**
   * Verifies that lookupPortionName() does not match full banner names unless they're also portion
   * markings.
   *
   * <p>Some dissemination controls like NOFORN and ORCON have different abbreviations for portion
   * markings.
   */
  @Test
  public void testLookupPortionNameDoesNotMatchNonPortionBannerNames() {
    assertThat(
        "NOFORN banner name should not match in portion lookup (portion is NF)",
        DissemControl.lookupPortionName("NOFORN"),
        is(nullValue()));
    assertThat(
        "ORCON banner name should not match in portion lookup (portion is OC)",
        DissemControl.lookupPortionName("ORCON"),
        is(nullValue()));
    assertThat(
        "IMCON banner name should not match in portion lookup (portion is IMC)",
        DissemControl.lookupPortionName("IMCON"),
        is(nullValue()));
    assertThat(
        "RSEN banner name should not match in portion lookup (portion is RS)",
        DissemControl.lookupPortionName("RSEN"),
        is(nullValue()));
    assertThat(
        "PROPIN banner name should not match in portion lookup (portion is PR)",
        DissemControl.lookupPortionName("PROPIN"),
        is(nullValue()));
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
    assertThat(DissemControl.valueOf("NOFORN"), is(sameInstance(DissemControl.NOFORN)));
    assertThat(DissemControl.lookupBannerName("ORCON"), is(sameInstance(DissemControl.ORCON)));
    assertThat(DissemControl.lookupPortionName("NF"), is(sameInstance(DissemControl.NOFORN)));
  }

  /** Verifies ordinal values of enum constants. */
  @Test
  public void testOrdinalValues() {
    assertThat(DissemControl.RSEN.ordinal(), is(0));
    assertThat(DissemControl.IMCON.ordinal(), is(1));
    assertThat(DissemControl.NOFORN.ordinal(), is(2));
    assertThat(DissemControl.PROPIN.ordinal(), is(3));
    assertThat(DissemControl.RELIDO.ordinal(), is(4));
    assertThat(DissemControl.FISA.ordinal(), is(5));
    assertThat(DissemControl.ORCON.ordinal(), is(6));
    assertThat(DissemControl.DEA_SENSITIVE.ordinal(), is(7));
    assertThat(DissemControl.FOUO.ordinal(), is(8));
    assertThat(DissemControl.EYES_ONLY.ordinal(), is(9));
    assertThat(DissemControl.WAIVED.ordinal(), is(10));
  }
}
