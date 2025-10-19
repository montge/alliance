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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.Test;

/**
 * Unit tests for {@link MarkingType} enum.
 *
 * <p>MarkingType represents the three types of banner markings:
 *
 * <ul>
 *   <li>US - United States originated classified information
 *   <li>FGI - Foreign Government Information
 *   <li>JOINT - Joint classified information from multiple countries
 * </ul>
 *
 * <p><b>Coverage Target:</b> 95%
 *
 * <p><b>Examples:</b>
 *
 * <ul>
 *   <li>US: "TOP SECRET" or "SECRET//NOFORN"
 *   <li>FGI: "//NATO SECRET//ATOMAL" or "//DEU RESTRICTED"
 *   <li>JOINT: "//JOINT SECRET CAN USA" or "//JOINT TOP SECRET CAN DEU USA"
 * </ul>
 */
public class MarkingTypeTest {

  // ==========================================================================
  // Enum Constants Tests
  // ==========================================================================

  /**
   * Verifies that all three marking type constants exist.
   *
   * <p>Tests that the values() method returns exactly three constants: US, FGI, and JOINT.
   */
  @Test
  public void testEnumConstantsAllPresent() {
    MarkingType[] values = MarkingType.values();

    assertThat("MarkingType should have exactly 3 constants", values.length, is(3));
    assertThat("MarkingType should contain US", values, hasItemInArray(MarkingType.US));
    assertThat("MarkingType should contain FGI", values, hasItemInArray(MarkingType.FGI));
    assertThat("MarkingType should contain JOINT", values, hasItemInArray(MarkingType.JOINT));
  }

  /** Verifies that the US enum constant exists and is accessible. */
  @Test
  public void testUsValueExists() {
    assertThat("US constant should not be null", MarkingType.US, notNullValue());
  }

  /** Verifies that the FGI enum constant exists and is accessible. */
  @Test
  public void testFgiValueExists() {
    assertThat("FGI constant should not be null", MarkingType.FGI, notNullValue());
  }

  /** Verifies that the JOINT enum constant exists and is accessible. */
  @Test
  public void testJointValueExists() {
    assertThat("JOINT constant should not be null", MarkingType.JOINT, notNullValue());
  }

  // ==========================================================================
  // valueOf() Tests
  // ==========================================================================

  /** Verifies that valueOf() correctly returns the US constant. */
  @Test
  public void testValueOfUs() {
    MarkingType result = MarkingType.valueOf("US");

    assertThat("valueOf('US') should return US constant", result, is(MarkingType.US));
    assertThat("valueOf('US') should be same instance", result, sameInstance(MarkingType.US));
  }

  /** Verifies that valueOf() correctly returns the FGI constant. */
  @Test
  public void testValueOfFgi() {
    MarkingType result = MarkingType.valueOf("FGI");

    assertThat("valueOf('FGI') should return FGI constant", result, is(MarkingType.FGI));
    assertThat("valueOf('FGI') should be same instance", result, sameInstance(MarkingType.FGI));
  }

  /** Verifies that valueOf() correctly returns the JOINT constant. */
  @Test
  public void testValueOfJoint() {
    MarkingType result = MarkingType.valueOf("JOINT");

    assertThat("valueOf('JOINT') should return JOINT constant", result, is(MarkingType.JOINT));
    assertThat("valueOf('JOINT') should be same instance", result, sameInstance(MarkingType.JOINT));
  }

  /** Verifies that valueOf() throws IllegalArgumentException for invalid constant name. */
  @Test(expected = IllegalArgumentException.class)
  public void testValueOfInvalidNameThrowsException() {
    MarkingType.valueOf("INVALID");
  }

  /** Verifies that valueOf() throws NullPointerException for null input. */
  @Test(expected = NullPointerException.class)
  public void testValueOfNullThrowsException() {
    MarkingType.valueOf(null);
  }

  /**
   * Verifies that valueOf() is case-sensitive.
   *
   * <p>Lowercase "us" should throw IllegalArgumentException, only uppercase "US" is valid.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testValueOfCaseSensitive() {
    MarkingType.valueOf("us");
  }

  // ==========================================================================
  // values() Tests
  // ==========================================================================

  /** Verifies that values() returns all three constants. */
  @Test
  public void testValuesReturnsAllThree() {
    MarkingType[] values = MarkingType.values();

    assertThat("values() should return 3 elements", values.length, is(3));
  }

  /**
   * Verifies the order of enum constants.
   *
   * <p>Tests that the enum constants are declared in the order: US, FGI, JOINT.
   */
  @Test
  public void testEnumOrder() {
    MarkingType[] values = MarkingType.values();

    assertThat("First constant should be US", values[0], is(MarkingType.US));
    assertThat("Second constant should be FGI", values[1], is(MarkingType.FGI));
    assertThat("Third constant should be JOINT", values[2], is(MarkingType.JOINT));
  }

  // ==========================================================================
  // toString() Tests
  // ==========================================================================

  /** Verifies that toString() returns correct string representation for US. */
  @Test
  public void testToStringUs() {
    assertThat("US.toString() should return 'US'", MarkingType.US.toString(), is("US"));
  }

  /** Verifies that toString() returns correct string representation for FGI. */
  @Test
  public void testToStringFgi() {
    assertThat("FGI.toString() should return 'FGI'", MarkingType.FGI.toString(), is("FGI"));
  }

  /** Verifies that toString() returns correct string representation for JOINT. */
  @Test
  public void testToStringJoint() {
    assertThat("JOINT.toString() should return 'JOINT'", MarkingType.JOINT.toString(), is("JOINT"));
  }

  /** Verifies that toString() returns correct values for all enum constants. */
  @Test
  public void testToStringAllValues() {
    for (MarkingType type : MarkingType.values()) {
      assertThat("toString() should return non-null value", type.toString(), notNullValue());
      assertThat(
          "toString() should return non-empty string", type.toString().length(), greaterThan(0));
    }
  }
}
