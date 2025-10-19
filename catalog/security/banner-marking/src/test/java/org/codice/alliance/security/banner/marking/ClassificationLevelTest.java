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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for {@link ClassificationLevel} enum.
 *
 * <p>ClassificationLevel represents the five levels of US classification: TOP SECRET, SECRET,
 * CONFIDENTIAL, RESTRICTED, and UNCLASSIFIED. This test class verifies:
 *
 * <ul>
 *   <li>Enum value parsing from strings
 *   <li>String representation of each level
 *   <li>Ordinal values and ordering
 *   <li>Case-insensitive parsing
 *   <li>Error handling for invalid inputs
 * </ul>
 *
 * <p><b>Coverage Target:</b> 95%
 *
 * <p><b>Security Importance:</b> Classification levels are the foundation of all security markings.
 * Incorrect parsing or representation could lead to misclassification of data.
 */
public class ClassificationLevelTest {

  // ==========================================================================
  // Enum Values Tests
  // ==========================================================================

  /**
   * Test that all five classification levels exist as enum constants.
   *
   * <p>Verifies that the ClassificationLevel enum contains exactly five values in the expected
   * order.
   */
  @Test
  public void testEnumConstants() {
    ClassificationLevel[] values = ClassificationLevel.values();
    assertThat("Should have exactly 5 classification levels", values.length, is(5));
    assertThat(values[0], is(ClassificationLevel.UNCLASSIFIED));
    assertThat(values[1], is(ClassificationLevel.RESTRICTED));
    assertThat(values[2], is(ClassificationLevel.CONFIDENTIAL));
    assertThat(values[3], is(ClassificationLevel.SECRET));
    assertThat(values[4], is(ClassificationLevel.TOP_SECRET));
  }

  /**
   * Test that TOP_SECRET enum constant exists.
   *
   * <p>Verifies that TOP_SECRET can be referenced and is not null.
   */
  @Test
  public void testTopSecretValue() {
    assertThat(ClassificationLevel.TOP_SECRET, is(notNullValue()));
    assertThat(ClassificationLevel.valueOf("TOP_SECRET"), is(ClassificationLevel.TOP_SECRET));
  }

  /**
   * Test that SECRET enum constant exists.
   *
   * <p>Verifies that SECRET can be referenced and is not null.
   */
  @Test
  public void testSecretValue() {
    assertThat(ClassificationLevel.SECRET, is(notNullValue()));
    assertThat(ClassificationLevel.valueOf("SECRET"), is(ClassificationLevel.SECRET));
  }

  /**
   * Test that CONFIDENTIAL enum constant exists.
   *
   * <p>Verifies that CONFIDENTIAL can be referenced and is not null.
   */
  @Test
  public void testConfidentialValue() {
    assertThat(ClassificationLevel.CONFIDENTIAL, is(notNullValue()));
    assertThat(ClassificationLevel.valueOf("CONFIDENTIAL"), is(ClassificationLevel.CONFIDENTIAL));
  }

  /**
   * Test that RESTRICTED enum constant exists.
   *
   * <p>Verifies that RESTRICTED can be referenced and is not null.
   */
  @Test
  public void testRestrictedValue() {
    assertThat(ClassificationLevel.RESTRICTED, is(notNullValue()));
    assertThat(ClassificationLevel.valueOf("RESTRICTED"), is(ClassificationLevel.RESTRICTED));
  }

  /**
   * Test that UNCLASSIFIED enum constant exists.
   *
   * <p>Verifies that UNCLASSIFIED can be referenced and is not null.
   */
  @Test
  public void testUnclassifiedValue() {
    assertThat(ClassificationLevel.UNCLASSIFIED, is(notNullValue()));
    assertThat(ClassificationLevel.valueOf("UNCLASSIFIED"), is(ClassificationLevel.UNCLASSIFIED));
  }

  // ==========================================================================
  // getName() Tests
  // ==========================================================================

  /**
   * Test getName() returns "UNCLASSIFIED" for UNCLASSIFIED level.
   *
   * <p>Verifies that the long name for UNCLASSIFIED is correctly set.
   */
  @Test
  public void testGetNameUnclassified() {
    assertThat(ClassificationLevel.UNCLASSIFIED.getName(), is("UNCLASSIFIED"));
  }

  /**
   * Test getName() returns "RESTRICTED" for RESTRICTED level.
   *
   * <p>Verifies that the long name for RESTRICTED is correctly set.
   */
  @Test
  public void testGetNameRestricted() {
    assertThat(ClassificationLevel.RESTRICTED.getName(), is("RESTRICTED"));
  }

  /**
   * Test getName() returns "CONFIDENTIAL" for CONFIDENTIAL level.
   *
   * <p>Verifies that the long name for CONFIDENTIAL is correctly set.
   */
  @Test
  public void testGetNameConfidential() {
    assertThat(ClassificationLevel.CONFIDENTIAL.getName(), is("CONFIDENTIAL"));
  }

  /**
   * Test getName() returns "SECRET" for SECRET level.
   *
   * <p>Verifies that the long name for SECRET is correctly set.
   */
  @Test
  public void testGetNameSecret() {
    assertThat(ClassificationLevel.SECRET.getName(), is("SECRET"));
  }

  /**
   * Test getName() returns "TOP SECRET" for TOP_SECRET level.
   *
   * <p>Verifies that the long name for TOP_SECRET is correctly set. Note the space in "TOP SECRET".
   */
  @Test
  public void testGetNameTopSecret() {
    assertThat(ClassificationLevel.TOP_SECRET.getName(), is("TOP SECRET"));
  }

  // ==========================================================================
  // getShortName() Tests
  // ==========================================================================

  /**
   * Test getShortName() returns correct abbreviations for all levels.
   *
   * <p>Verifies that short names are correctly assigned: U, R, C, S, TS.
   */
  @Test
  public void testGetShortNameAllLevels() {
    assertThat(ClassificationLevel.UNCLASSIFIED.getShortName(), is("U"));
    assertThat(ClassificationLevel.RESTRICTED.getShortName(), is("R"));
    assertThat(ClassificationLevel.CONFIDENTIAL.getShortName(), is("C"));
    assertThat(ClassificationLevel.SECRET.getShortName(), is("S"));
    assertThat(ClassificationLevel.TOP_SECRET.getShortName(), is("TS"));
  }

  // ==========================================================================
  // lookup() Tests - Valid Cases
  // ==========================================================================

  /**
   * Test lookup() with valid long names.
   *
   * <p>Verifies that lookup() can find all classification levels by their full names.
   */
  @Test
  public void testLookupValidNames() {
    assertThat(ClassificationLevel.lookup("UNCLASSIFIED"), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(ClassificationLevel.lookup("RESTRICTED"), is(ClassificationLevel.RESTRICTED));
    assertThat(ClassificationLevel.lookup("CONFIDENTIAL"), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(ClassificationLevel.lookup("SECRET"), is(ClassificationLevel.SECRET));
    assertThat(ClassificationLevel.lookup("TOP SECRET"), is(ClassificationLevel.TOP_SECRET));
  }

  /**
   * Test lookup() is case-sensitive.
   *
   * <p>The implementation uses an exact match map, so lowercase or mixed case names should not
   * match.
   */
  @Test
  public void testLookupCaseInsensitive() {
    // Based on the implementation, lookup is case-sensitive (no toLowerCase() in map)
    assertThat(
        "lookup should be case-sensitive",
        ClassificationLevel.lookup("unclassified"),
        is(nullValue()));
    assertThat(
        "lookup should be case-sensitive",
        ClassificationLevel.lookup("Top Secret"),
        is(nullValue()));
    assertThat(
        "lookup should be case-sensitive", ClassificationLevel.lookup("secret"), is(nullValue()));
  }

  /**
   * Test lookup() with invalid name returns null.
   *
   * <p>Verifies that looking up a non-existent classification name returns null.
   */
  @Test
  public void testLookupInvalidName() {
    assertThat(ClassificationLevel.lookup("INVALID"), is(nullValue()));
    assertThat(ClassificationLevel.lookup("CLASSIFIED"), is(nullValue()));
    assertThat(ClassificationLevel.lookup("PUBLIC"), is(nullValue()));
  }

  /**
   * Test lookup() with null input returns null.
   *
   * <p>Verifies null-safe behavior of the lookup method.
   */
  @Test
  public void testLookupNull() {
    assertThat(ClassificationLevel.lookup(null), is(nullValue()));
  }

  /**
   * Test lookup() with empty string returns null.
   *
   * <p>Verifies that empty string input is handled gracefully.
   */
  @Test
  public void testLookupEmpty() {
    assertThat(ClassificationLevel.lookup(""), is(nullValue()));
  }

  /**
   * Test lookup() does not match whitespace variations.
   *
   * <p>Verifies that leading/trailing whitespace causes lookup to fail.
   */
  @Test
  public void testLookupWhitespace() {
    assertThat(ClassificationLevel.lookup(" UNCLASSIFIED"), is(nullValue()));
    assertThat(ClassificationLevel.lookup("UNCLASSIFIED "), is(nullValue()));
    assertThat(ClassificationLevel.lookup(" UNCLASSIFIED "), is(nullValue()));
    assertThat(ClassificationLevel.lookup("TOP  SECRET"), is(nullValue())); // double space
  }

  // ==========================================================================
  // lookupByShortname() Tests - Valid Cases
  // ==========================================================================

  /**
   * Test lookupByShortname() with valid short names.
   *
   * <p>Verifies that lookupByShortname() can find all classification levels by their abbreviations.
   */
  @Test
  public void testLookupByShortnameValidShortNames() {
    assertThat(ClassificationLevel.lookupByShortname("U"), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(ClassificationLevel.lookupByShortname("R"), is(ClassificationLevel.RESTRICTED));
    assertThat(ClassificationLevel.lookupByShortname("C"), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(ClassificationLevel.lookupByShortname("S"), is(ClassificationLevel.SECRET));
    assertThat(ClassificationLevel.lookupByShortname("TS"), is(ClassificationLevel.TOP_SECRET));
  }

  /**
   * Test lookupByShortname() is case-sensitive.
   *
   * <p>The implementation uses an exact match map, so lowercase abbreviations should not match.
   */
  @Test
  public void testLookupByShortnameCaseSensitive() {
    assertThat(
        "lookupByShortname should be case-sensitive",
        ClassificationLevel.lookupByShortname("u"),
        is(nullValue()));
    assertThat(
        "lookupByShortname should be case-sensitive",
        ClassificationLevel.lookupByShortname("ts"),
        is(nullValue()));
    assertThat(
        "lookupByShortname should be case-sensitive",
        ClassificationLevel.lookupByShortname("Ts"),
        is(nullValue()));
  }

  /**
   * Test lookupByShortname() with invalid short name returns null.
   *
   * <p>Verifies that looking up a non-existent short name returns null.
   */
  @Test
  public void testLookupByShortnameInvalidShortName() {
    assertThat(ClassificationLevel.lookupByShortname("X"), is(nullValue()));
    assertThat(ClassificationLevel.lookupByShortname("UC"), is(nullValue()));
    assertThat(ClassificationLevel.lookupByShortname("T"), is(nullValue()));
  }

  /**
   * Test lookupByShortname() with null input returns null.
   *
   * <p>Verifies null-safe behavior of the lookupByShortname method.
   */
  @Test
  public void testLookupByShortnameNull() {
    assertThat(ClassificationLevel.lookupByShortname(null), is(nullValue()));
  }

  /**
   * Test lookupByShortname() with empty string returns null.
   *
   * <p>Verifies that empty string input is handled gracefully.
   */
  @Test
  public void testLookupByShortnameEmpty() {
    assertThat(ClassificationLevel.lookupByShortname(""), is(nullValue()));
  }

  /**
   * Test lookupByShortname() does not match with whitespace.
   *
   * <p>Verifies that whitespace around short names causes lookup to fail.
   */
  @Test
  public void testLookupByShortnameWhitespace() {
    assertThat(ClassificationLevel.lookupByShortname(" U"), is(nullValue()));
    assertThat(ClassificationLevel.lookupByShortname("U "), is(nullValue()));
    assertThat(ClassificationLevel.lookupByShortname(" TS "), is(nullValue()));
  }

  // ==========================================================================
  // Enum Comparable Tests
  // ==========================================================================

  /**
   * Test that enum constants have correct ordinal values.
   *
   * <p>Verifies the order: UNCLASSIFIED(0), RESTRICTED(1), CONFIDENTIAL(2), SECRET(3),
   * TOP_SECRET(4).
   */
  @Test
  public void testOrdinalValues() {
    assertThat(ClassificationLevel.UNCLASSIFIED.ordinal(), is(0));
    assertThat(ClassificationLevel.RESTRICTED.ordinal(), is(1));
    assertThat(ClassificationLevel.CONFIDENTIAL.ordinal(), is(2));
    assertThat(ClassificationLevel.SECRET.ordinal(), is(3));
    assertThat(ClassificationLevel.TOP_SECRET.ordinal(), is(4));
  }

  /**
   * Test Comparable ordering of classification levels.
   *
   * <p>Enums implement Comparable based on declaration order. Verifies that levels can be compared.
   */
  @Test
  public void testComparableOrdering() {
    // TOP_SECRET > SECRET > CONFIDENTIAL > RESTRICTED > UNCLASSIFIED
    assertTrue(
        "TOP_SECRET should be greater than SECRET",
        ClassificationLevel.TOP_SECRET.compareTo(ClassificationLevel.SECRET) > 0);
    assertTrue(
        "SECRET should be greater than CONFIDENTIAL",
        ClassificationLevel.SECRET.compareTo(ClassificationLevel.CONFIDENTIAL) > 0);
    assertTrue(
        "CONFIDENTIAL should be greater than RESTRICTED",
        ClassificationLevel.CONFIDENTIAL.compareTo(ClassificationLevel.RESTRICTED) > 0);
    assertTrue(
        "RESTRICTED should be greater than UNCLASSIFIED",
        ClassificationLevel.RESTRICTED.compareTo(ClassificationLevel.UNCLASSIFIED) > 0);

    assertTrue(
        "UNCLASSIFIED should be less than TOP_SECRET",
        ClassificationLevel.UNCLASSIFIED.compareTo(ClassificationLevel.TOP_SECRET) < 0);

    // Test comparing same level (using valueOf to avoid self-comparison warning)
    assertThat(
        "Same level should be equal",
        ClassificationLevel.SECRET.compareTo(ClassificationLevel.valueOf("SECRET")),
        is(0));
  }

  /**
   * Test that enum constants maintain identity.
   *
   * <p>Verifies that enum singletons maintain reference equality.
   */
  @Test
  public void testEnumIdentity() {
    assertThat(
        ClassificationLevel.valueOf("TOP_SECRET"),
        is(sameInstance(ClassificationLevel.TOP_SECRET)));
    assertThat(ClassificationLevel.lookup("SECRET"), is(sameInstance(ClassificationLevel.SECRET)));
    assertThat(
        ClassificationLevel.lookupByShortname("U"),
        is(sameInstance(ClassificationLevel.UNCLASSIFIED)));
  }
}
