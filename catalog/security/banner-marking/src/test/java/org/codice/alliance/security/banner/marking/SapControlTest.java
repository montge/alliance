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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import org.junit.Test;

/**
 * Unit tests for {@link SapControl} class.
 *
 * <p>SapControl represents Special Access Program (SAP) controls used for highly sensitive
 * information. SAP markings protect classified programs and can include:
 *
 * <ul>
 *   <li>Single program: SAR-BUTTERED POPCORN or SAR-BP
 *   <li>Multiple programs (separated by /): SAR-BP/GB/TC
 *   <li>Multiple programs indicator: SAR-MULTIPLE PROGRAMS (when more than 3 programs)
 *   <li>HVSACO: Special SAP type (Human Intelligence Value SAP for COunterterrorism Operations)
 * </ul>
 *
 * <p>This test class verifies:
 *
 * <ul>
 *   <li>Constructor with single program parsing
 *   <li>Constructor with multiple programs parsing (slash-separated)
 *   <li>Constructor with MULTIPLE PROGRAMS keyword
 *   <li>Empty constructor (creates HVSACO)
 *   <li>Getter methods (getPrograms(), isMultiple(), isHvsaco())
 *   <li>toString() method for different SAP types
 *   <li>Edge cases and boundary conditions
 * </ul>
 *
 * <p><b>Coverage Target:</b> 95%+
 *
 * <p><b>Security Importance:</b> SAP controls are among the most restrictive security markings.
 * Incorrect parsing could lead to unauthorized disclosure of extremely sensitive information.
 *
 * <p><b>Format Rules:</b>
 *
 * <ul>
 *   <li>Programs are separated by forward slash (/)
 *   <li>Maximum 3 programs should be displayed; use "MULTIPLE PROGRAMS" for more
 *   <li>HVSACO is created using empty constructor
 *   <li>Output format: "SAR-PROGRAM1/PROGRAM2/PROGRAM3"
 * </ul>
 */
public class SapControlTest {

  // ==========================================================================
  // Construction Tests - Single Program
  // ==========================================================================

  /**
   * Test construction with single program.
   *
   * <p>Example: "BUTTERED POPCORN" should create a SapControl with one program, multiple=false,
   * hvsaco=false.
   */
  @Test
  public void testConstructorSingleProgram() {
    SapControl sap = new SapControl("BUTTERED POPCORN");

    assertThat(sap.getPrograms(), is(notNullValue()));
    assertThat(sap.getPrograms(), hasSize(1));
    assertThat(sap.getPrograms().get(0), is("BUTTERED POPCORN"));
    assertThat(sap.isMultiple(), is(false));
    assertThat(sap.isHvsaco(), is(false));
  }

  /**
   * Test construction with single program abbreviation.
   *
   * <p>Example: "BP" (abbreviation for BUTTERED POPCORN).
   */
  @Test
  public void testConstructorSingleProgramAbbreviation() {
    SapControl sap = new SapControl("BP");

    assertThat(sap.getPrograms(), hasSize(1));
    assertThat(sap.getPrograms().get(0), is("BP"));
    assertThat(sap.isMultiple(), is(false));
    assertThat(sap.isHvsaco(), is(false));
  }

  /**
   * Test construction with single program containing hyphens.
   *
   * <p>Some SAP program names may contain hyphens.
   */
  @Test
  public void testConstructorSingleProgramWithHyphens() {
    SapControl sap = new SapControl("PROGRAM-ALPHA");

    assertThat(sap.getPrograms(), hasSize(1));
    assertThat(sap.getPrograms().get(0), is("PROGRAM-ALPHA"));
    assertThat(sap.isMultiple(), is(false));
    assertThat(sap.isHvsaco(), is(false));
  }

  /**
   * Test construction with single program containing numbers.
   *
   * <p>Some SAP program names may contain numbers.
   */
  @Test
  public void testConstructorSingleProgramWithNumbers() {
    SapControl sap = new SapControl("PROGRAM1");

    assertThat(sap.getPrograms(), hasSize(1));
    assertThat(sap.getPrograms().get(0), is("PROGRAM1"));
    assertThat(sap.isMultiple(), is(false));
    assertThat(sap.isHvsaco(), is(false));
  }

  // ==========================================================================
  // Construction Tests - Multiple Programs
  // ==========================================================================

  /**
   * Test construction with two programs separated by slash.
   *
   * <p>Example: "BP/GB" should parse into two separate programs.
   */
  @Test
  public void testConstructorTwoPrograms() {
    SapControl sap = new SapControl("BP/GB");

    assertThat(sap.getPrograms(), hasSize(2));
    assertThat(sap.getPrograms().get(0), is("BP"));
    assertThat(sap.getPrograms().get(1), is("GB"));
    assertThat(sap.isMultiple(), is(false));
    assertThat(sap.isHvsaco(), is(false));
  }

  /**
   * Test construction with three programs separated by slashes.
   *
   * <p>Example: "BP/GB/TC" should parse into three separate programs. Three is the maximum number
   * that should be displayed.
   */
  @Test
  public void testConstructorThreePrograms() {
    SapControl sap = new SapControl("BP/GB/TC");

    assertThat(sap.getPrograms(), hasSize(3));
    assertThat(sap.getPrograms().get(0), is("BP"));
    assertThat(sap.getPrograms().get(1), is("GB"));
    assertThat(sap.getPrograms().get(2), is("TC"));
    assertThat(sap.isMultiple(), is(false));
    assertThat(sap.isHvsaco(), is(false));
  }

  /**
   * Test construction with full program names separated by slashes.
   *
   * <p>Programs can be full names, not just abbreviations.
   */
  @Test
  public void testConstructorMultipleFullNames() {
    SapControl sap = new SapControl("BUTTERED POPCORN/GREEN BEANS");

    assertThat(sap.getPrograms(), hasSize(2));
    assertThat(sap.getPrograms().get(0), is("BUTTERED POPCORN"));
    assertThat(sap.getPrograms().get(1), is("GREEN BEANS"));
    assertThat(sap.isMultiple(), is(false));
    assertThat(sap.isHvsaco(), is(false));
  }

  /**
   * Test construction with mix of abbreviations and full names.
   *
   * <p>Program list can contain both abbreviations and full names.
   */
  @Test
  public void testConstructorMixedProgramFormats() {
    SapControl sap = new SapControl("BP/GREEN BEANS/TC");

    assertThat(sap.getPrograms(), hasSize(3));
    assertThat(sap.getPrograms().get(0), is("BP"));
    assertThat(sap.getPrograms().get(1), is("GREEN BEANS"));
    assertThat(sap.getPrograms().get(2), is("TC"));
    assertThat(sap.isMultiple(), is(false));
    assertThat(sap.isHvsaco(), is(false));
  }

  // ==========================================================================
  // Construction Tests - MULTIPLE PROGRAMS Keyword
  // ==========================================================================

  /**
   * Test construction with MULTIPLE PROGRAMS keyword.
   *
   * <p>When more than 3 programs exist, "MULTIPLE PROGRAMS" is used instead of listing them. This
   * should set multiple=true and programs to empty list.
   */
  @Test
  public void testConstructorMultipleProgramsKeyword() {
    SapControl sap = new SapControl("MULTIPLE PROGRAMS");

    assertThat(sap.getPrograms(), is(notNullValue()));
    assertThat(sap.getPrograms(), is(empty()));
    assertThat(sap.isMultiple(), is(true));
    assertThat(sap.isHvsaco(), is(false));
  }

  /**
   * Test construction with "MULTIPLE PROGRAMS" (exact match required).
   *
   * <p>The keyword must exactly match "MULTIPLE PROGRAMS" to be recognized.
   */
  @Test
  public void testConstructorMultipleProgramsExactMatch() {
    SapControl sap = new SapControl("MULTIPLE PROGRAMS");

    assertThat(sap.isMultiple(), is(true));
    assertThat(sap.getPrograms().isEmpty(), is(true));
  }

  /**
   * Test construction with similar but non-matching keywords.
   *
   * <p>Variations of "MULTIPLE PROGRAMS" should not trigger the special handling.
   */
  @Test
  public void testConstructorMultipleProgramsNoMatch() {
    SapControl sap = new SapControl("MULTIPLE");

    // Should be treated as a single program name
    assertThat(sap.getPrograms(), hasSize(1));
    assertThat(sap.getPrograms().get(0), is("MULTIPLE"));
    assertThat(sap.isMultiple(), is(false));
  }

  // ==========================================================================
  // Construction Tests - Empty Constructor (HVSACO)
  // ==========================================================================

  /**
   * Test empty constructor creates HVSACO.
   *
   * <p>HVSACO (Human Intelligence Value SAP for Counterterrorism Operations) is a special SAP type
   * created using the empty constructor.
   */
  @Test
  public void testConstructorEmpty() {
    SapControl sap = new SapControl();

    assertThat(sap.getPrograms(), is(notNullValue()));
    assertThat(sap.getPrograms(), is(empty()));
    assertThat(sap.isMultiple(), is(false));
    assertThat(sap.isHvsaco(), is(true));
  }

  /**
   * Test HVSACO has no programs.
   *
   * <p>HVSACO is a distinct SAP type that doesn't have program names.
   */
  @Test
  public void testConstructorEmptyNoPrograms() {
    SapControl sap = new SapControl();

    assertThat(sap.getPrograms().isEmpty(), is(true));
  }

  // ==========================================================================
  // Getter Tests
  // ==========================================================================

  /**
   * Test getPrograms() returns immutable list.
   *
   * <p>The programs list should be immutable to prevent external modification.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testGetProgramsImmutable() {
    SapControl sap = new SapControl("BP/GB");
    List<String> programs = sap.getPrograms();
    programs.add("NEW");
  }

  /**
   * Test getPrograms() returns non-null for empty constructor.
   *
   * <p>Even HVSACO should return a non-null (but empty) programs list.
   */
  @Test
  public void testGetProgramsNonNullHvsaco() {
    SapControl sap = new SapControl();
    assertThat(sap.getPrograms(), is(notNullValue()));
  }

  /**
   * Test getPrograms() returns non-null for MULTIPLE PROGRAMS.
   *
   * <p>MULTIPLE PROGRAMS should return a non-null (but empty) programs list.
   */
  @Test
  public void testGetProgramsNonNullMultiple() {
    SapControl sap = new SapControl("MULTIPLE PROGRAMS");
    assertThat(sap.getPrograms(), is(notNullValue()));
  }

  /** Test isMultiple() returns false for single program. */
  @Test
  public void testIsMultipleFalseSingleProgram() {
    SapControl sap = new SapControl("BP");
    assertThat(sap.isMultiple(), is(false));
  }

  /**
   * Test isMultiple() returns false for multiple listed programs.
   *
   * <p>Even when multiple programs are listed, isMultiple() should be false unless the "MULTIPLE
   * PROGRAMS" keyword is used.
   */
  @Test
  public void testIsMultipleFalseMultiplePrograms() {
    SapControl sap = new SapControl("BP/GB/TC");
    assertThat(sap.isMultiple(), is(false));
  }

  /** Test isMultiple() returns true for MULTIPLE PROGRAMS keyword. */
  @Test
  public void testIsMultipleTrueKeyword() {
    SapControl sap = new SapControl("MULTIPLE PROGRAMS");
    assertThat(sap.isMultiple(), is(true));
  }

  /** Test isHvsaco() returns false for normal SAP. */
  @Test
  public void testIsHvsacoFalseNormalSap() {
    SapControl sap = new SapControl("BP");
    assertThat(sap.isHvsaco(), is(false));
  }

  /** Test isHvsaco() returns false for MULTIPLE PROGRAMS. */
  @Test
  public void testIsHvsacoFalseMultiple() {
    SapControl sap = new SapControl("MULTIPLE PROGRAMS");
    assertThat(sap.isHvsaco(), is(false));
  }

  /** Test isHvsaco() returns true for empty constructor. */
  @Test
  public void testIsHvsacoTrueEmptyConstructor() {
    SapControl sap = new SapControl();
    assertThat(sap.isHvsaco(), is(true));
  }

  // ==========================================================================
  // toString() Tests
  // ==========================================================================

  /**
   * Test toString() for HVSACO.
   *
   * <p>HVSACO should return exactly "HVSACO".
   */
  @Test
  public void testToStringHvsaco() {
    SapControl sap = new SapControl();
    assertThat(sap.toString(), is("HVSACO"));
  }

  /**
   * Test toString() for MULTIPLE PROGRAMS.
   *
   * <p>Should return "SAR-MULTIPLE PROGRAMS".
   */
  @Test
  public void testToStringMultiplePrograms() {
    SapControl sap = new SapControl("MULTIPLE PROGRAMS");
    assertThat(sap.toString(), is("SAR-MULTIPLE PROGRAMS"));
  }

  /**
   * Test toString() for single program.
   *
   * <p>Should return "SAR-PROGRAMNAME".
   */
  @Test
  public void testToStringSingleProgram() {
    SapControl sap = new SapControl("BP");
    assertThat(sap.toString(), is("SAR-BP"));
  }

  /**
   * Test toString() for single program with full name.
   *
   * <p>Should return "SAR-FULL NAME".
   */
  @Test
  public void testToStringSingleProgramFullName() {
    SapControl sap = new SapControl("BUTTERED POPCORN");
    assertThat(sap.toString(), is("SAR-BUTTERED POPCORN"));
  }

  /**
   * Test toString() for two programs.
   *
   * <p>Should return "SAR-PROGRAM1/PROGRAM2".
   */
  @Test
  public void testToStringTwoPrograms() {
    SapControl sap = new SapControl("BP/GB");
    assertThat(sap.toString(), is("SAR-BP/GB"));
  }

  /**
   * Test toString() for three programs.
   *
   * <p>Should return "SAR-PROGRAM1/PROGRAM2/PROGRAM3".
   */
  @Test
  public void testToStringThreePrograms() {
    SapControl sap = new SapControl("BP/GB/TC");
    assertThat(sap.toString(), is("SAR-BP/GB/TC"));
  }

  /**
   * Test toString() for programs with full names.
   *
   * <p>Full names should be joined with slashes.
   */
  @Test
  public void testToStringFullNames() {
    SapControl sap = new SapControl("BUTTERED POPCORN/GREEN BEANS");
    assertThat(sap.toString(), is("SAR-BUTTERED POPCORN/GREEN BEANS"));
  }

  // ==========================================================================
  // Edge Cases and Boundary Tests
  // ==========================================================================

  /**
   * Test construction with empty string.
   *
   * <p>Empty string should create a SapControl with one empty program.
   */
  @Test
  public void testConstructorEmptyString() {
    SapControl sap = new SapControl("");

    // Empty string creates a single empty program (not MULTIPLE PROGRAMS)
    assertThat(sap.getPrograms(), hasSize(1));
    assertThat(sap.getPrograms().get(0), is(""));
    assertThat(sap.isMultiple(), is(false));
    assertThat(sap.isHvsaco(), is(false));
  }

  /**
   * Test construction with trailing slash.
   *
   * <p>Edge case: "BP/" splits to ["BP"] because Java's split() discards trailing empty strings.
   */
  @Test
  public void testConstructorTrailingSlash() {
    SapControl sap = new SapControl("BP/");

    // Split on "/" produces ["BP"] (trailing empty string discarded by Java)
    assertThat(sap.getPrograms(), hasSize(1));
    assertThat(sap.getPrograms().get(0), is("BP"));
  }

  /**
   * Test construction with leading slash.
   *
   * <p>Leading slash before programs should result in an empty string element.
   */
  @Test
  public void testConstructorLeadingSlash() {
    SapControl sap = new SapControl("/BP");

    // Split on "/" produces ["", "BP"]
    assertThat(sap.getPrograms(), hasSize(2));
    assertThat(sap.getPrograms().get(0), is(""));
    assertThat(sap.getPrograms().get(1), is("BP"));
  }

  /**
   * Test construction with multiple consecutive slashes.
   *
   * <p>Multiple slashes should create empty string elements.
   */
  @Test
  public void testConstructorMultipleSlashes() {
    SapControl sap = new SapControl("BP//GB");

    // Split on "/" produces ["BP", "", "GB"]
    assertThat(sap.getPrograms(), hasSize(3));
    assertThat(sap.getPrograms().get(0), is("BP"));
    assertThat(sap.getPrograms().get(1), is(""));
    assertThat(sap.getPrograms().get(2), is("GB"));
  }

  /**
   * Test construction with only slashes.
   *
   * <p>Edge case: "///" splits to [] because Java's split() discards all trailing empty strings.
   */
  @Test
  public void testConstructorOnlySlashes() {
    SapControl sap = new SapControl("///");

    // Split on "/" with all empty segments produces [] (all trailing empties discarded by Java)
    assertThat(sap.getPrograms(), hasSize(0));
  }

  /**
   * Test construction with whitespace in program names.
   *
   * <p>Program names can contain spaces (preserved as-is).
   */
  @Test
  public void testConstructorWhitespaceInNames() {
    SapControl sap = new SapControl("PROGRAM ONE/PROGRAM TWO");

    assertThat(sap.getPrograms(), hasSize(2));
    assertThat(sap.getPrograms().get(0), is("PROGRAM ONE"));
    assertThat(sap.getPrograms().get(1), is("PROGRAM TWO"));
  }

  /**
   * Test construction with special characters in program names.
   *
   * <p>Program names can contain hyphens, numbers, and other characters.
   */
  @Test
  public void testConstructorSpecialCharacters() {
    SapControl sap = new SapControl("PROGRAM-1A/PROGRAM-2B");

    assertThat(sap.getPrograms(), hasSize(2));
    assertThat(sap.getPrograms().get(0), is("PROGRAM-1A"));
    assertThat(sap.getPrograms().get(1), is("PROGRAM-2B"));
  }

  /**
   * Test toString() with empty program name.
   *
   * <p>Empty program names should still be included in output.
   */
  @Test
  public void testToStringEmptyProgram() {
    SapControl sap = new SapControl("");
    assertThat(sap.toString(), is("SAR-"));
  }

  /**
   * Test toString() with programs containing empty elements.
   *
   * <p>Empty elements from multiple slashes should appear in output.
   */
  @Test
  public void testToStringEmptyElements() {
    SapControl sap = new SapControl("BP//GB");
    assertThat(sap.toString(), is("SAR-BP//GB"));
  }

  // ==========================================================================
  // Real-World SAP Scenarios
  // ==========================================================================

  /**
   * Test construction with realistic SAP program abbreviation.
   *
   * <p>Common SAP abbreviations are typically 2-3 characters.
   */
  @Test
  public void testConstructorRealisticAbbreviation() {
    SapControl sap = new SapControl("XYZ");

    assertThat(sap.getPrograms(), hasSize(1));
    assertThat(sap.getPrograms().get(0), is("XYZ"));
    assertThat(sap.toString(), is("SAR-XYZ"));
  }

  /**
   * Test construction with realistic full SAP program name.
   *
   * <p>SAP program names can be full descriptive names.
   */
  @Test
  public void testConstructorRealisticFullName() {
    SapControl sap = new SapControl("SPECIAL PROJECT ALPHA");

    assertThat(sap.getPrograms(), hasSize(1));
    assertThat(sap.getPrograms().get(0), is("SPECIAL PROJECT ALPHA"));
    assertThat(sap.toString(), is("SAR-SPECIAL PROJECT ALPHA"));
  }

  /**
   * Test construction with maximum recommended programs (3).
   *
   * <p>Up to 3 programs can be listed before using MULTIPLE PROGRAMS indicator.
   */
  @Test
  public void testConstructorMaximumRecommendedPrograms() {
    SapControl sap = new SapControl("ALPHA/BRAVO/CHARLIE");

    assertThat(sap.getPrograms(), hasSize(3));
    assertThat(sap.toString(), is("SAR-ALPHA/BRAVO/CHARLIE"));
  }
}
