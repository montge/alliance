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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import org.junit.Test;

/**
 * Unit tests for {@link AeaMarking} class.
 *
 * <p>AeaMarking represents Atomic Energy Act markings that protect nuclear weapons and materials
 * information. This test class verifies:
 *
 * <ul>
 *   <li>Parsing of AEA marking strings (RD, FRD, DOD_UCNI, DOE_UCNI, TFNI)
 *   <li>Critical Nuclear Weapon Design Information (-N) flag handling
 *   <li>SIGMA compartment parsing and storage
 *   <li>SG (abbreviated SIGMA) format support
 *   <li>String representation (toString)
 *   <li>Edge cases and null handling
 * </ul>
 *
 * <p><b>Coverage Target:</b> 95%+
 *
 * <p><b>Security Importance:</b> AEA markings protect nuclear weapons design and materials
 * information. Incorrect parsing could lead to security violations or misclassification of
 * sensitive nuclear information.
 *
 * <p><b>AEA Marking Types:</b>
 *
 * <ul>
 *   <li>RD = Restricted Data (nuclear weapons design)
 *   <li>FRD = Formerly Restricted Data (declassified RD)
 *   <li>DOD_UCNI = DoD Unclassified Controlled Nuclear Information
 *   <li>DOE_UCNI = DoE Unclassified Controlled Nuclear Information
 *   <li>TFNI = Transclassified Foreign Nuclear Information
 * </ul>
 *
 * <p><b>Format Examples:</b>
 *
 * <ul>
 *   <li>Basic: "RD", "FRD", "DOD UCNI"
 *   <li>CNWDI: "RD-N" (Critical Nuclear Weapon Design Information)
 *   <li>SIGMA: "RD-SIGMA 1 2 3", "FRD-SIGMA 14"
 *   <li>SG: "RD-SG1 2" (abbreviated SIGMA format)
 * </ul>
 */
public class AeaMarkingTest {

  // ==========================================================================
  // Construction Tests - Basic AEA Types (No CNWDI or SIGMA)
  // ==========================================================================

  /**
   * Test construction with RD (Restricted Data) marking.
   *
   * <p>RD is the most common AEA marking for nuclear weapons design information.
   */
  @Test
  public void testConstructorRestrictedData() {
    AeaMarking aea = new AeaMarking("RD");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), is(notNullValue()));
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test construction with RESTRICTED DATA (full name for RD).
   *
   * <p>The full name "RESTRICTED DATA" should be recognized and parsed as AeaType.RD.
   */
  @Test
  public void testConstructorRestrictedDataFullName() {
    AeaMarking aea = new AeaMarking("RESTRICTED DATA");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test construction with FRD (Formerly Restricted Data) marking.
   *
   * <p>FRD is used for information declassified from RD status.
   */
  @Test
  public void testConstructorFormerlyRestrictedData() {
    AeaMarking aea = new AeaMarking("FRD");

    assertThat(aea.getType(), is(AeaType.FRD));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test construction with FORMERLY RESTRICTED DATA (full name for FRD).
   *
   * <p>The full name should be recognized and parsed as AeaType.FRD.
   */
  @Test
  public void testConstructorFormerlyRestrictedDataFullName() {
    AeaMarking aea = new AeaMarking("FORMERLY RESTRICTED DATA");

    assertThat(aea.getType(), is(AeaType.FRD));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test construction with DOD UCNI marking.
   *
   * <p>DOD UCNI (DoD Unclassified Controlled Nuclear Information) is for unclassified nuclear
   * information requiring protection.
   */
  @Test
  public void testConstructorDodUcni() {
    AeaMarking aea = new AeaMarking("DCNI");

    assertThat(aea.getType(), is(AeaType.DOD_UCNI));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test construction with DOD UCNI full name.
   *
   * <p>The full "DOD UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION" string should be recognized.
   */
  @Test
  public void testConstructorDodUcniFullName() {
    AeaMarking aea = new AeaMarking("DOD UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION");

    assertThat(aea.getType(), is(AeaType.DOD_UCNI));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test construction with DOE UCNI marking.
   *
   * <p>DOE UCNI (DoE Unclassified Controlled Nuclear Information) is the Department of Energy
   * equivalent.
   */
  @Test
  public void testConstructorDoeUcni() {
    AeaMarking aea = new AeaMarking("UCNI");

    assertThat(aea.getType(), is(AeaType.DOE_UCNI));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test construction with DOE UCNI full name.
   *
   * <p>The full "DOE UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION" string should be recognized.
   */
  @Test
  public void testConstructorDoeUcniFullName() {
    AeaMarking aea = new AeaMarking("DOE UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION");

    assertThat(aea.getType(), is(AeaType.DOE_UCNI));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test construction with TFNI marking.
   *
   * <p>TFNI (Transclassified Foreign Nuclear Information) is for nuclear information from foreign
   * sources.
   */
  @Test
  public void testConstructorTfni() {
    AeaMarking aea = new AeaMarking("TFNI");

    assertThat(aea.getType(), is(AeaType.TFNI));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test construction with TFNI full name.
   *
   * <p>The full "TRANSCLASSIFIED FOREIGN NUCLEAR INFORMATION" string should be recognized.
   */
  @Test
  public void testConstructorTfniFullName() {
    AeaMarking aea = new AeaMarking("TRANSCLASSIFIED FOREIGN NUCLEAR INFORMATION");

    assertThat(aea.getType(), is(AeaType.TFNI));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), is(empty()));
  }

  // ==========================================================================
  // Construction Tests - CNWDI (Critical Nuclear Weapon Design Information)
  // ==========================================================================

  /**
   * Test construction with RD-N (Critical Nuclear Weapon Design Information).
   *
   * <p>The -N suffix indicates critical nuclear weapon design information, the most sensitive AEA
   * category.
   */
  @Test
  public void testConstructorRdWithCnwdi() {
    AeaMarking aea = new AeaMarking("RD-N");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(true));
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test construction with RESTRICTED DATA-N (full name with CNWDI).
   *
   * <p>The full name format should also support the -N suffix.
   */
  @Test
  public void testConstructorRestrictedDataWithCnwdi() {
    AeaMarking aea = new AeaMarking("RESTRICTED DATA-N");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(true));
    assertThat(aea.getSigmas(), is(empty()));
  }

  // ==========================================================================
  // Construction Tests - SIGMA Compartments (Standard Format)
  // ==========================================================================

  /**
   * Test construction with RD-SIGMA and single sigma value.
   *
   * <p>SIGMA compartments provide additional subcategories within RD or FRD markings.
   */
  @Test
  public void testConstructorRdWithSingleSigma() {
    AeaMarking aea = new AeaMarking("RD-SIGMA 1");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), hasSize(1));
    assertThat(aea.getSigmas(), contains(1));
  }

  /**
   * Test construction with RD-SIGMA and two sigma values.
   *
   * <p>Multiple SIGMA values are space-separated.
   */
  @Test
  public void testConstructorRdWithTwoSigmas() {
    AeaMarking aea = new AeaMarking("RD-SIGMA 1 2");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), hasSize(2));
    assertThat(aea.getSigmas(), contains(1, 2));
  }

  /**
   * Test construction with RD-SIGMA and three sigma values.
   *
   * <p>Common real-world example with multiple compartments.
   */
  @Test
  public void testConstructorRdWithThreeSigmas() {
    AeaMarking aea = new AeaMarking("RD-SIGMA 1 2 3");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), hasSize(3));
    assertThat(aea.getSigmas(), contains(1, 2, 3));
  }

  /**
   * Test construction with FRD-SIGMA.
   *
   * <p>FRD can also have SIGMA compartments, typically limited to 1-99.
   */
  @Test
  public void testConstructorFrdWithSigma() {
    AeaMarking aea = new AeaMarking("FRD-SIGMA 14");

    assertThat(aea.getType(), is(AeaType.FRD));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), hasSize(1));
    assertThat(aea.getSigmas(), contains(14));
  }

  /**
   * Test construction with FORMERLY RESTRICTED DATA-SIGMA (full name with SIGMA).
   *
   * <p>The full name format should support SIGMA compartments.
   */
  @Test
  public void testConstructorFrdFullNameWithSigma() {
    AeaMarking aea = new AeaMarking("FORMERLY RESTRICTED DATA-SIGMA 14");

    assertThat(aea.getType(), is(AeaType.FRD));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), hasSize(1));
    assertThat(aea.getSigmas(), contains(14));
  }

  /**
   * Test construction with large SIGMA numbers.
   *
   * <p>RD supports SIGMA values up to 999. Test with three-digit values.
   */
  @Test
  public void testConstructorRdWithLargeSigmas() {
    AeaMarking aea = new AeaMarking("RD-SIGMA 100 500 999");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.getSigmas(), hasSize(3));
    assertThat(aea.getSigmas(), contains(100, 500, 999));
  }

  /**
   * Test construction with multiple FRD SIGMAs.
   *
   * <p>FRD can have multiple SIGMA values, each typically in the 1-99 range.
   */
  @Test
  public void testConstructorFrdWithMultipleSigmas() {
    AeaMarking aea = new AeaMarking("FRD-SIGMA 10 20 30");

    assertThat(aea.getType(), is(AeaType.FRD));
    assertThat(aea.getSigmas(), hasSize(3));
    assertThat(aea.getSigmas(), contains(10, 20, 30));
  }

  // ==========================================================================
  // Construction Tests - SG Format (Abbreviated SIGMA)
  // ==========================================================================

  /**
   * Test construction with RD-SG format (abbreviated SIGMA).
   *
   * <p>SG is an abbreviated form of SIGMA that appears in some marking systems.
   */
  @Test
  public void testConstructorRdWithSgSingleValue() {
    AeaMarking aea = new AeaMarking("RD-SG1");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aea.getSigmas(), hasSize(1));
    assertThat(aea.getSigmas(), contains(1));
  }

  /**
   * Test construction with RD-SG format with multiple values.
   *
   * <p>SG format should support space-separated values like SIGMA format.
   */
  @Test
  public void testConstructorRdWithSgMultipleValues() {
    AeaMarking aea = new AeaMarking("RD-SG1 2 3");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.getSigmas(), hasSize(3));
    assertThat(aea.getSigmas(), contains(1, 2, 3));
  }

  /**
   * Test construction with FRD-SG format.
   *
   * <p>FRD should also support the SG abbreviated format.
   */
  @Test
  public void testConstructorFrdWithSg() {
    AeaMarking aea = new AeaMarking("FRD-SG14");

    assertThat(aea.getType(), is(AeaType.FRD));
    assertThat(aea.getSigmas(), hasSize(1));
    assertThat(aea.getSigmas(), contains(14));
  }

  /**
   * Test construction with SG format with leading space.
   *
   * <p>Test that parsing handles whitespace correctly after "SG".
   */
  @Test
  public void testConstructorSgWithLeadingSpace() {
    AeaMarking aea = new AeaMarking("RD-SG 5");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.getSigmas(), hasSize(1));
    assertThat(aea.getSigmas(), contains(5));
  }

  // ==========================================================================
  // Getter Tests
  // ==========================================================================

  /**
   * Test getType() returns correct AeaType for RD.
   *
   * <p>Verifies that the type field is properly set during construction.
   */
  @Test
  public void testGetTypeRd() {
    AeaMarking aea = new AeaMarking("RD");
    assertThat(aea.getType(), is(AeaType.RD));
  }

  /** Test getType() returns correct AeaType for FRD. */
  @Test
  public void testGetTypeFrd() {
    AeaMarking aea = new AeaMarking("FRD");
    assertThat(aea.getType(), is(AeaType.FRD));
  }

  /** Test getType() returns correct AeaType for DOD_UCNI. */
  @Test
  public void testGetTypeDodUcni() {
    AeaMarking aea = new AeaMarking("DCNI");
    assertThat(aea.getType(), is(AeaType.DOD_UCNI));
  }

  /** Test getType() returns correct AeaType for DOE_UCNI. */
  @Test
  public void testGetTypeDoeUcni() {
    AeaMarking aea = new AeaMarking("UCNI");
    assertThat(aea.getType(), is(AeaType.DOE_UCNI));
  }

  /** Test getType() returns correct AeaType for TFNI. */
  @Test
  public void testGetTypeTfni() {
    AeaMarking aea = new AeaMarking("TFNI");
    assertThat(aea.getType(), is(AeaType.TFNI));
  }

  /**
   * Test isCriticalNuclearWeaponDesignInformation() returns false for basic RD.
   *
   * <p>Without the -N suffix, CNWDI should be false.
   */
  @Test
  public void testIsCnwdiFalseForBasicRd() {
    AeaMarking aea = new AeaMarking("RD");
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
  }

  /**
   * Test isCriticalNuclearWeaponDesignInformation() returns true for RD-N.
   *
   * <p>The -N suffix should set the CNWDI flag to true.
   */
  @Test
  public void testIsCnwdiTrueForRdN() {
    AeaMarking aea = new AeaMarking("RD-N");
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(true));
  }

  /**
   * Test getSigmas() returns empty list for basic RD.
   *
   * <p>Without SIGMA compartments, the list should be empty (not null).
   */
  @Test
  public void testGetSigmasEmptyForBasicRd() {
    AeaMarking aea = new AeaMarking("RD");
    assertThat(aea.getSigmas(), is(notNullValue()));
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test getSigmas() returns correct values for RD-SIGMA.
   *
   * <p>SIGMA values should be returned as an immutable list.
   */
  @Test
  public void testGetSigmasWithValues() {
    AeaMarking aea = new AeaMarking("RD-SIGMA 1 2 3");
    List<Integer> sigmas = aea.getSigmas();

    assertThat(sigmas, hasSize(3));
    assertThat(sigmas, contains(1, 2, 3));
  }

  // ==========================================================================
  // Immutability Tests
  // ==========================================================================

  /**
   * Test that getSigmas() returns an immutable list.
   *
   * <p>Attempting to modify the returned list should throw UnsupportedOperationException.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testGetSigmasImmutable() {
    AeaMarking aea = new AeaMarking("RD-SIGMA 1 2 3");
    List<Integer> sigmas = aea.getSigmas();
    sigmas.add(4);
  }

  /**
   * Test that getSigmas() for empty list is immutable.
   *
   * <p>Even an empty SIGMA list should be immutable.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testGetSigmasEmptyImmutable() {
    AeaMarking aea = new AeaMarking("RD");
    List<Integer> sigmas = aea.getSigmas();
    sigmas.add(1);
  }

  // ==========================================================================
  // toString() Tests
  // ==========================================================================

  /**
   * Test toString() for basic RD marking.
   *
   * <p>toString() should return the canonical name from AeaType.
   */
  @Test
  public void testToStringBasicRd() {
    AeaMarking aea = new AeaMarking("RD");
    assertThat(aea.toString(), is("RESTRICTED DATA"));
  }

  /** Test toString() for basic FRD marking. */
  @Test
  public void testToStringBasicFrd() {
    AeaMarking aea = new AeaMarking("FRD");
    assertThat(aea.toString(), is("FORMERLY RESTRICTED DATA"));
  }

  /**
   * Test toString() for RD-N (with CNWDI).
   *
   * <p>The -N suffix should be appended to the marking.
   */
  @Test
  public void testToStringRdWithCnwdi() {
    AeaMarking aea = new AeaMarking("RD-N");
    assertThat(aea.toString(), is("RESTRICTED DATA-N"));
  }

  /**
   * Test toString() for RD-SIGMA with single value.
   *
   * <p>SIGMA values should be formatted as "-SIGMA &lt;numbers&gt;".
   */
  @Test
  public void testToStringRdWithSingleSigma() {
    AeaMarking aea = new AeaMarking("RD-SIGMA 1");
    assertThat(aea.toString(), is("RESTRICTED DATA-SIGMA 1"));
  }

  /**
   * Test toString() for RD-SIGMA with multiple values.
   *
   * <p>Multiple SIGMA values should be space-separated.
   */
  @Test
  public void testToStringRdWithMultipleSigmas() {
    AeaMarking aea = new AeaMarking("RD-SIGMA 1 2 3");
    assertThat(aea.toString(), is("RESTRICTED DATA-SIGMA 1 2 3"));
  }

  /**
   * Test toString() for FRD-SIGMA.
   *
   * <p>FRD should also support SIGMA in toString output.
   */
  @Test
  public void testToStringFrdWithSigma() {
    AeaMarking aea = new AeaMarking("FRD-SIGMA 14");
    assertThat(aea.toString(), is("FORMERLY RESTRICTED DATA-SIGMA 14"));
  }

  /**
   * Test toString() for DOD UCNI.
   *
   * <p>UCNI types should return their canonical names.
   */
  @Test
  public void testToStringDodUcni() {
    AeaMarking aea = new AeaMarking("DCNI");
    assertThat(aea.toString(), is("DOD UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION"));
  }

  /** Test toString() for DOE UCNI. */
  @Test
  public void testToStringDoeUcni() {
    AeaMarking aea = new AeaMarking("UCNI");
    assertThat(aea.toString(), is("DOE UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION"));
  }

  /** Test toString() for TFNI. */
  @Test
  public void testToStringTfni() {
    AeaMarking aea = new AeaMarking("TFNI");
    assertThat(aea.toString(), is("TRANSCLASSIFIED FOREIGN NUCLEAR INFORMATION"));
  }

  // ==========================================================================
  // Edge Cases and Error Handling
  // ==========================================================================

  /**
   * Test construction with invalid SIGMA value (non-numeric).
   *
   * <p>Non-numeric SIGMA values should be filtered out (parseSigma returns null).
   */
  @Test
  public void testConstructorWithInvalidSigmaValue() {
    AeaMarking aea = new AeaMarking("RD-SIGMA 1 ABC 3");

    assertThat(aea.getType(), is(AeaType.RD));
    // "ABC" should be filtered out, leaving only 1 and 3
    assertThat(aea.getSigmas(), hasSize(2));
    assertThat(aea.getSigmas(), contains(1, 3));
  }

  /**
   * Test construction with mixed valid and invalid SIGMA values.
   *
   * <p>Valid numeric values should be preserved, invalid ones filtered.
   */
  @Test
  public void testConstructorWithMixedSigmaValues() {
    AeaMarking aea = new AeaMarking("RD-SIGMA 10 XYZ 20 INVALID 30");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.getSigmas(), hasSize(3));
    assertThat(aea.getSigmas(), contains(10, 20, 30));
  }

  /**
   * Test construction with only invalid SIGMA values.
   *
   * <p>If all SIGMA values are invalid, the list should be empty.
   */
  @Test
  public void testConstructorWithOnlyInvalidSigmaValues() {
    AeaMarking aea = new AeaMarking("RD-SIGMA ABC XYZ");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test construction with SIGMA but no values.
   *
   * <p>Edge case: "RD-SIGMA" with no values should result in empty SIGMA list.
   */
  @Test
  public void testConstructorSigmaNoValues() {
    AeaMarking aea = new AeaMarking("RD-SIGMA");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.isCriticalNuclearWeaponDesignInformation(), is(false));
    // SIGMA keyword present but no values - should be empty after trim
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test construction with extra whitespace in SIGMA values.
   *
   * <p>Extra spaces should be handled correctly by the split pattern.
   */
  @Test
  public void testConstructorSigmaWithExtraWhitespace() {
    AeaMarking aea = new AeaMarking("RD-SIGMA  1   2   3  ");

    assertThat(aea.getType(), is(AeaType.RD));
    assertThat(aea.getSigmas(), hasSize(3));
    assertThat(aea.getSigmas(), contains(1, 2, 3));
  }

  /**
   * Test construction with SG and no immediately following digit.
   *
   * <p>Edge case: "RD-SG " (with space after SG) should parse correctly.
   */
  @Test
  public void testConstructorSgWithSpace() {
    AeaMarking aea = new AeaMarking("RD-SG ");

    assertThat(aea.getType(), is(AeaType.RD));
    // "SG" followed by empty string after trim should result in empty list
    assertThat(aea.getSigmas(), is(empty()));
  }

  /**
   * Test toString() output can be re-parsed.
   *
   * <p>Round-trip test: toString() output should be parseable back to equivalent object.
   */
  @Test
  public void testToStringRoundTrip() {
    AeaMarking original = new AeaMarking("RD-SIGMA 1 2 3");
    String markingString = original.toString();

    AeaMarking reparsed = new AeaMarking(markingString);

    assertThat(reparsed.getType(), is(original.getType()));
    assertThat(
        reparsed.isCriticalNuclearWeaponDesignInformation(),
        is(original.isCriticalNuclearWeaponDesignInformation()));
    assertThat(reparsed.getSigmas(), is(original.getSigmas()));
  }

  /** Test toString() round-trip for CNWDI marking. */
  @Test
  public void testToStringRoundTripCnwdi() {
    AeaMarking original = new AeaMarking("RD-N");
    String markingString = original.toString();

    AeaMarking reparsed = new AeaMarking(markingString);

    assertThat(reparsed.getType(), is(AeaType.RD));
    assertThat(reparsed.isCriticalNuclearWeaponDesignInformation(), is(true));
  }
}
