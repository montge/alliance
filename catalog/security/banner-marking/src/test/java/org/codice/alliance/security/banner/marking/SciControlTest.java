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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * Unit tests for {@link SciControl} class.
 *
 * <p>SciControl represents Sensitive Compartmented Information (SCI) controls with compartments and
 * sub-compartments. This test class verifies:
 *
 * <ul>
 *   <li>Object construction from marking strings
 *   <li>Parsing of SCI controls with various formats
 *   <li>Compartment and sub-compartment extraction
 *   <li>Null safety and edge cases
 *   <li>Complex SCI marking scenarios
 * </ul>
 *
 * <p><b>Coverage Target:</b> 95%
 *
 * <p><b>Security Importance:</b> SCI controls are critical for compartmented intelligence
 * information. Incorrect parsing could lead to unauthorized access or misclassification.
 *
 * <p><b>Format Examples:</b>
 *
 * <ul>
 *   <li>Simple control: "HCS"
 *   <li>Control with compartment: "HCS-P"
 *   <li>Control with sub-compartments: "SI-TK ALFA BRAVO"
 *   <li>Multiple compartments: "SI-TK-G"
 *   <li>Complex: "SI-TK ALFA BRAVO-G CHARLIE"
 * </ul>
 */
public class SciControlTest {

  // ==========================================================================
  // Construction Tests - Simple Controls (No Compartments)
  // ==========================================================================

  /**
   * Test construction with simple control (no compartments or sub-compartments).
   *
   * <p>Example: "HCS" should create a SciControl with control="HCS" and empty compartments map.
   */
  @Test
  public void testConstructorSimpleControl() {
    SciControl sci = new SciControl("HCS");

    assertThat(sci.getControl(), is("HCS"));
    assertThat(sci.getCompartments(), is(notNullValue()));
    assertThat(sci.getCompartments().isEmpty(), is(true));
  }

  /**
   * Test construction with SI control.
   *
   * <p>SI (Special Intelligence) is a common SCI control.
   */
  @Test
  public void testConstructorSiControl() {
    SciControl sci = new SciControl("SI");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().isEmpty(), is(true));
  }

  /**
   * Test construction with TK control.
   *
   * <p>TK (TALENT KEYHOLE) is a common SCI control for satellite imagery.
   */
  @Test
  public void testConstructorTkControl() {
    SciControl sci = new SciControl("TK");

    assertThat(sci.getControl(), is("TK"));
    assertThat(sci.getCompartments().isEmpty(), is(true));
  }

  /**
   * Test construction with G control.
   *
   * <p>G (GAMMA) is a common SCI control.
   */
  @Test
  public void testConstructorGControl() {
    SciControl sci = new SciControl("G");

    assertThat(sci.getControl(), is("G"));
    assertThat(sci.getCompartments().isEmpty(), is(true));
  }

  // ==========================================================================
  // Construction Tests - Single Compartment (No Sub-Compartments)
  // ==========================================================================

  /**
   * Test construction with control and single compartment (no sub-compartments).
   *
   * <p>Example: "HCS-P" should create control="HCS" with compartment "P" having no
   * sub-compartments.
   */
  @Test
  public void testConstructorSingleCompartmentNoSubs() {
    SciControl sci = new SciControl("HCS-P");

    assertThat(sci.getControl(), is("HCS"));
    assertThat(sci.getCompartments(), is(notNullValue()));
    assertThat(sci.getCompartments().size(), is(1));
    assertThat(sci.getCompartments(), hasEntry(equalTo("P"), hasSize(0)));
  }

  /**
   * Test construction with SI-TK compartment.
   *
   * <p>SI-TK is a common combination for Special Intelligence TALENT KEYHOLE.
   */
  @Test
  public void testConstructorSiTkCompartment() {
    SciControl sci = new SciControl("SI-TK");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(1));
    assertThat(sci.getCompartments(), hasEntry(equalTo("TK"), hasSize(0)));
  }

  /**
   * Test construction with SI-G compartment.
   *
   * <p>SI-G (Special Intelligence GAMMA) is another common combination.
   */
  @Test
  public void testConstructorSiGCompartment() {
    SciControl sci = new SciControl("SI-G");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(1));
    assertThat(sci.getCompartments(), hasEntry(equalTo("G"), hasSize(0)));
  }

  // ==========================================================================
  // Construction Tests - Compartment with Sub-Compartments
  // ==========================================================================

  /**
   * Test construction with compartment and single sub-compartment.
   *
   * <p>Example: "SI-TK ALFA" should create control="SI", compartment="TK" with sub-compartment
   * ["ALFA"].
   */
  @Test
  public void testConstructorCompartmentWithSingleSub() {
    SciControl sci = new SciControl("SI-TK ALFA");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(1));
    assertThat(sci.getCompartments(), hasEntry(equalTo("TK"), hasSize(1)));

    List<String> tkSubs = sci.getCompartments().get("TK");
    assertThat(tkSubs.get(0), is("ALFA"));
  }

  /**
   * Test construction with compartment and multiple sub-compartments.
   *
   * <p>Example: "SI-TK ALFA BRAVO" should create control="SI", compartment="TK" with
   * sub-compartments ["ALFA", "BRAVO"].
   */
  @Test
  public void testConstructorCompartmentWithMultipleSubs() {
    SciControl sci = new SciControl("SI-TK ALFA BRAVO");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(1));
    assertThat(sci.getCompartments(), hasEntry(equalTo("TK"), hasSize(2)));

    List<String> tkSubs = sci.getCompartments().get("TK");
    assertThat(tkSubs.get(0), is("ALFA"));
    assertThat(tkSubs.get(1), is("BRAVO"));
  }

  /**
   * Test construction with compartment and three sub-compartments.
   *
   * <p>Example: "SI-TK ALFA BRAVO CHARLIE" should parse all three sub-compartments.
   */
  @Test
  public void testConstructorCompartmentWithThreeSubs() {
    SciControl sci = new SciControl("SI-TK ALFA BRAVO CHARLIE");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(1));

    List<String> tkSubs = sci.getCompartments().get("TK");
    assertThat(tkSubs, hasSize(3));
    assertThat(tkSubs.get(0), is("ALFA"));
    assertThat(tkSubs.get(1), is("BRAVO"));
    assertThat(tkSubs.get(2), is("CHARLIE"));
  }

  /**
   * Test construction with G compartment and sub-compartment.
   *
   * <p>Example: "SI-G GOLF" should parse G compartment with GOLF sub-compartment.
   */
  @Test
  public void testConstructorGCompartmentWithSub() {
    SciControl sci = new SciControl("SI-G GOLF");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(1));

    List<String> gSubs = sci.getCompartments().get("G");
    assertThat(gSubs, hasSize(1));
    assertThat(gSubs.get(0), is("GOLF"));
  }

  // ==========================================================================
  // Construction Tests - Multiple Compartments
  // ==========================================================================

  /**
   * Test construction with two compartments, neither with sub-compartments.
   *
   * <p>Example: "SI-TK-G" should create control="SI" with compartments "TK" and "G", both empty.
   */
  @Test
  public void testConstructorTwoCompartmentsNoSubs() {
    SciControl sci = new SciControl("SI-TK-G");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(2));
    assertThat(sci.getCompartments(), hasEntry(equalTo("TK"), hasSize(0)));
    assertThat(sci.getCompartments(), hasEntry(equalTo("G"), hasSize(0)));
  }

  /**
   * Test construction with two compartments, first has sub-compartments.
   *
   * <p>Example: "SI-TK ALFA BRAVO-G" should parse TK with subs ["ALFA", "BRAVO"] and G with no
   * subs.
   */
  @Test
  public void testConstructorTwoCompartmentsFirstHasSubs() {
    SciControl sci = new SciControl("SI-TK ALFA BRAVO-G");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(2));

    // Verify TK compartment with subs
    assertThat(sci.getCompartments(), hasEntry(equalTo("TK"), hasSize(2)));
    List<String> tkSubs = sci.getCompartments().get("TK");
    assertThat(tkSubs.get(0), is("ALFA"));
    assertThat(tkSubs.get(1), is("BRAVO"));

    // Verify G compartment without subs
    assertThat(sci.getCompartments(), hasEntry(equalTo("G"), hasSize(0)));
  }

  /**
   * Test construction with two compartments, second has sub-compartments.
   *
   * <p>Example: "SI-TK-G GOLF" should parse TK with no subs and G with subs ["GOLF"].
   */
  @Test
  public void testConstructorTwoCompartmentsSecondHasSubs() {
    SciControl sci = new SciControl("SI-TK-G GOLF");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(2));

    // Verify TK compartment without subs
    assertThat(sci.getCompartments(), hasEntry(equalTo("TK"), hasSize(0)));

    // Verify G compartment with subs
    assertThat(sci.getCompartments(), hasEntry(equalTo("G"), hasSize(1)));
    List<String> gSubs = sci.getCompartments().get("G");
    assertThat(gSubs.get(0), is("GOLF"));
  }

  /**
   * Test construction with two compartments, both have sub-compartments.
   *
   * <p>Example: "SI-TK ALFA-G GOLF" should parse both compartments with their respective
   * sub-compartments.
   */
  @Test
  public void testConstructorTwoCompartmentsBothHaveSubs() {
    SciControl sci = new SciControl("SI-TK ALFA-G GOLF");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(2));

    // Verify TK compartment with subs
    assertThat(sci.getCompartments(), hasEntry(equalTo("TK"), hasSize(1)));
    List<String> tkSubs = sci.getCompartments().get("TK");
    assertThat(tkSubs.get(0), is("ALFA"));

    // Verify G compartment with subs
    assertThat(sci.getCompartments(), hasEntry(equalTo("G"), hasSize(1)));
    List<String> gSubs = sci.getCompartments().get("G");
    assertThat(gSubs.get(0), is("GOLF"));
  }

  /**
   * Test construction with three compartments in complex arrangement.
   *
   * <p>Example: "SI-TK ALFA-G-HCS" should parse all three compartments.
   */
  @Test
  public void testConstructorThreeCompartments() {
    SciControl sci = new SciControl("SI-TK ALFA-G-HCS");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(3));

    // Verify TK compartment with subs
    assertThat(sci.getCompartments(), hasEntry(equalTo("TK"), hasSize(1)));
    List<String> tkSubs = sci.getCompartments().get("TK");
    assertThat(tkSubs.get(0), is("ALFA"));

    // Verify G and HCS compartments without subs
    assertThat(sci.getCompartments(), hasEntry(equalTo("G"), hasSize(0)));
    assertThat(sci.getCompartments(), hasEntry(equalTo("HCS"), hasSize(0)));
  }

  // ==========================================================================
  // Compartment Ordering Tests (ImmutableSortedMap)
  // ==========================================================================

  /**
   * Test that compartments are stored in sorted order.
   *
   * <p>The implementation uses ImmutableSortedMap, so compartments should be alphabetically sorted.
   */
  @Test
  public void testCompartmentsAreSorted() {
    SciControl sci = new SciControl("SI-TK-G-HCS");

    // Get compartment keys
    List<String> compartmentKeys = List.copyOf(sci.getCompartments().keySet());

    // Verify alphabetical order: G, HCS, TK
    assertThat(compartmentKeys, hasSize(3));
    assertThat(compartmentKeys.get(0), is("G"));
    assertThat(compartmentKeys.get(1), is("HCS"));
    assertThat(compartmentKeys.get(2), is("TK"));
  }

  /**
   * Test sorting with reverse-order input.
   *
   * <p>Input "SI-Z-Y-X" should be stored as X, Y, Z.
   */
  @Test
  public void testCompartmentsSortedReverseInput() {
    SciControl sci = new SciControl("SI-Z-Y-X");

    List<String> compartmentKeys = List.copyOf(sci.getCompartments().keySet());

    assertThat(compartmentKeys, hasSize(3));
    assertThat(compartmentKeys.get(0), is("X"));
    assertThat(compartmentKeys.get(1), is("Y"));
    assertThat(compartmentKeys.get(2), is("Z"));
  }

  // ==========================================================================
  // Getter Tests
  // ==========================================================================

  /**
   * Test getControl() returns correct control string.
   *
   * <p>Verifies that control is properly extracted from marking.
   */
  @Test
  public void testGetControlSimple() {
    SciControl sci = new SciControl("HCS");
    assertThat(sci.getControl(), is("HCS"));
  }

  /**
   * Test getControl() with complex marking.
   *
   * <p>Control should be the part before the first hyphen.
   */
  @Test
  public void testGetControlComplex() {
    SciControl sci = new SciControl("SI-TK ALFA BRAVO-G GOLF");
    assertThat(sci.getControl(), is("SI"));
  }

  /**
   * Test getCompartments() returns non-null map.
   *
   * <p>Even for simple controls with no compartments, map should not be null.
   */
  @Test
  public void testGetCompartmentsNotNull() {
    SciControl sci = new SciControl("HCS");
    assertThat(sci.getCompartments(), is(notNullValue()));
  }

  /**
   * Test getCompartments() returns immutable map.
   *
   * <p>Attempting to modify the returned map should throw UnsupportedOperationException.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testGetCompartmentsImmutable() {
    SciControl sci = new SciControl("SI-TK");
    Map<String, List<String>> compartments = sci.getCompartments();
    compartments.put("NEW", List.of("TEST"));
  }

  /**
   * Test that sub-compartment lists are immutable.
   *
   * <p>Attempting to modify a sub-compartment list should throw UnsupportedOperationException.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testGetCompartmentsSubListImmutable() {
    SciControl sci = new SciControl("SI-TK ALFA");
    List<String> tkSubs = sci.getCompartments().get("TK");
    tkSubs.add("NEW");
  }

  // ==========================================================================
  // Edge Cases and Special Scenarios
  // ==========================================================================

  /**
   * Test construction with single character control.
   *
   * <p>Controls can be single letters like "G".
   */
  @Test
  public void testConstructorSingleCharControl() {
    SciControl sci = new SciControl("G");
    assertThat(sci.getControl(), is("G"));
    assertThat(sci.getCompartments().isEmpty(), is(true));
  }

  /**
   * Test construction with long control name.
   *
   * <p>Some controls like "KLONDIKE" are multi-character.
   */
  @Test
  public void testConstructorLongControl() {
    SciControl sci = new SciControl("KLONDIKE");
    assertThat(sci.getControl(), is("KLONDIKE"));
    assertThat(sci.getCompartments().isEmpty(), is(true));
  }

  /**
   * Test construction with numbers in compartments.
   *
   * <p>Some compartments may contain numbers.
   */
  @Test
  public void testConstructorNumbersInCompartments() {
    SciControl sci = new SciControl("SI-TK ALFA1 BRAVO2");

    assertThat(sci.getControl(), is("SI"));
    List<String> tkSubs = sci.getCompartments().get("TK");
    assertThat(tkSubs, hasSize(2));
    assertThat(tkSubs.get(0), is("ALFA1"));
    assertThat(tkSubs.get(1), is("BRAVO2"));
  }

  /**
   * Test construction with hyphenated sub-compartments.
   *
   * <p>Hyphens separate compartments, not sub-compartments within a compartment.
   */
  @Test
  public void testConstructorHyphenatedSubCompartments() {
    // This tests that "ALFA-BRAVO" is treated as two compartments, not one hyphenated name
    SciControl sci = new SciControl("SI-ALFA-BRAVO");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(2));
    assertThat(sci.getCompartments(), hasEntry(equalTo("ALFA"), hasSize(0)));
    assertThat(sci.getCompartments(), hasEntry(equalTo("BRAVO"), hasSize(0)));
  }

  // ==========================================================================
  // Null Safety and Boundary Tests
  // ==========================================================================

  /**
   * Test construction with empty string.
   *
   * <p>Edge case: what happens with empty string input. Should create control="" and empty
   * compartments.
   */
  @Test
  public void testConstructorEmptyString() {
    SciControl sci = new SciControl("");
    assertThat(sci.getControl(), is(""));
    assertThat(sci.getCompartments().isEmpty(), is(true));
  }

  /**
   * Test construction with only hyphens after control.
   *
   * <p>Edge case: "SI---" splits to ["SI"] because Java's split() discards trailing empty strings.
   * Since split.length == 1, the constructor returns an empty compartments map.
   */
  @Test
  public void testConstructorOnlyHyphens() {
    SciControl sci = new SciControl("SI---");

    assertThat(sci.getControl(), is("SI"));
    // Java split() discards trailing empty strings, leaving split.length == 1
    assertThat(sci.getCompartments().size(), is(0));
  }

  /**
   * Test construction with trailing hyphen.
   *
   * <p>Edge case: "SI-TK-" splits to ["SI", "TK"] because Java's split() discards the trailing
   * empty string. Only the TK compartment is added.
   */
  @Test
  public void testConstructorTrailingHyphen() {
    SciControl sci = new SciControl("SI-TK-");

    assertThat(sci.getControl(), is("SI"));
    // Java split() discards trailing empty string, so only TK compartment exists
    assertThat(sci.getCompartments().size(), is(1));
    assertTrue(sci.getCompartments().containsKey("TK"));
  }

  /**
   * Test construction with leading hyphen.
   *
   * <p>Edge case: "-TK" should create empty control and TK compartment.
   */
  @Test
  public void testConstructorLeadingHyphen() {
    SciControl sci = new SciControl("-TK");

    assertThat(sci.getControl(), is(""));
    assertThat(sci.getCompartments().size(), is(1));
    assertTrue(sci.getCompartments().containsKey("TK"));
  }

  // ==========================================================================
  // Real-World SCI Control Scenarios
  // ==========================================================================

  /**
   * Test HCS-P marking (common real-world example).
   *
   * <p>HCS-P (HUMINT Control System - Privileged) is a common SCI marking.
   */
  @Test
  public void testConstructorHcsP() {
    SciControl sci = new SciControl("HCS-P");

    assertThat(sci.getControl(), is("HCS"));
    assertThat(sci.getCompartments().size(), is(1));
    assertThat(sci.getCompartments(), hasEntry(equalTo("P"), hasSize(0)));
  }

  /**
   * Test SI-G marking (Special Intelligence GAMMA).
   *
   * <p>SI-G is used for certain SIGINT products.
   */
  @Test
  public void testConstructorSiGRealWorld() {
    SciControl sci = new SciControl("SI-G");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(1));
    assertThat(sci.getCompartments(), hasEntry(equalTo("G"), hasSize(0)));
  }

  /**
   * Test SI-TK marking (Special Intelligence TALENT KEYHOLE).
   *
   * <p>SI-TK is used for satellite imagery intelligence.
   */
  @Test
  public void testConstructorSiTkRealWorld() {
    SciControl sci = new SciControl("SI-TK");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(1));
    assertThat(sci.getCompartments(), hasEntry(equalTo("TK"), hasSize(0)));
  }

  /**
   * Test complex SI-TK-G marking.
   *
   * <p>SI-TK-G combines TALENT KEYHOLE and GAMMA compartments.
   */
  @Test
  public void testConstructorSiTkGRealWorld() {
    SciControl sci = new SciControl("SI-TK-G");

    assertThat(sci.getControl(), is("SI"));
    assertThat(sci.getCompartments().size(), is(2));
    assertThat(sci.getCompartments(), hasEntry(equalTo("TK"), hasSize(0)));
    assertThat(sci.getCompartments(), hasEntry(equalTo("G"), hasSize(0)));
  }
}
