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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import org.junit.Test;

/**
 * Unit tests for {@link ValidationError}.
 *
 * <p>ValidationError represents a validation error that occurs when processing DoD 5200.1-M
 * security markings. Each error contains:
 *
 * <ul>
 *   <li>A descriptive error message
 *   <li>An optional appendix reference (e.g., "Appendix G")
 *   <li>An optional paragraph reference (e.g., "G-2")
 * </ul>
 *
 * <p>This test class verifies:
 *
 * <ul>
 *   <li>All three constructor overloads
 *   <li>Getter methods return correct values
 *   <li>toString() formatting with and without appendix/paragraph
 *   <li>Null and empty string handling
 *   <li>Edge cases and boundary conditions
 * </ul>
 *
 * <p><b>Coverage Target:</b> 95%
 *
 * <p><b>Reference:</b> DoD MANUAL NUMBER 5200.01, Volume 2, Enclosure 4 - Marking Classified
 * Information
 */
public class ValidationErrorTest {

  // ==========================================================================
  // Construction Tests - Single Argument Constructor
  // ==========================================================================

  /**
   * Test constructor with message only.
   *
   * <p>Verifies that the single-argument constructor properly sets the message and uses default
   * values for appendix (empty string) and paragraph ("-").
   */
  @Test
  public void testConstructorWithMessage() {
    ValidationError error = new ValidationError("Invalid classification level");

    assertThat(error.getMessage(), is("Invalid classification level"));
    assertThat(error.getAppendix(), is(""));
    assertThat(error.getParagraph(), is("-"));
  }

  /**
   * Test constructor with null message.
   *
   * <p>Verifies that the constructor accepts null message values, allowing the caller to handle
   * null checking if needed.
   */
  @Test
  public void testConstructorWithNullMessage() {
    ValidationError error = new ValidationError(null);

    assertThat(error.getMessage(), is(nullValue()));
    assertThat(error.getAppendix(), is(""));
    assertThat(error.getParagraph(), is("-"));
  }

  /**
   * Test constructor with empty message.
   *
   * <p>Verifies that the constructor accepts empty string messages.
   */
  @Test
  public void testConstructorWithEmptyMessage() {
    ValidationError error = new ValidationError("");

    assertThat(error.getMessage(), is(""));
    assertThat(error.getAppendix(), is(""));
    assertThat(error.getParagraph(), is("-"));
  }

  /**
   * Test constructor with whitespace-only message.
   *
   * <p>Verifies that whitespace messages are preserved without trimming.
   */
  @Test
  public void testConstructorWithWhitespaceMessage() {
    ValidationError error = new ValidationError("   ");

    assertThat(error.getMessage(), is("   "));
    assertThat(error.getAppendix(), is(""));
    assertThat(error.getParagraph(), is("-"));
  }

  /**
   * Test constructor with long message.
   *
   * <p>Verifies that long error messages are properly stored and retrieved.
   */
  @Test
  public void testConstructorWithLongMessage() {
    String longMessage =
        "This is a very long error message that describes a complex validation error "
            + "with multiple details about what went wrong during the parsing of the security banner";
    ValidationError error = new ValidationError(longMessage);

    assertThat(error.getMessage(), is(longMessage));
  }

  // ==========================================================================
  // Construction Tests - Two Argument Constructor
  // ==========================================================================

  /**
   * Test constructor with message and paragraph.
   *
   * <p>Verifies that the two-argument constructor properly sets message and paragraph while using
   * default value for appendix (empty string).
   */
  @Test
  public void testConstructorWithMessageAndParagraph() {
    ValidationError error = new ValidationError("Invalid dissemination control", "G-3");

    assertThat(error.getMessage(), is("Invalid dissemination control"));
    assertThat(error.getAppendix(), is(""));
    assertThat(error.getParagraph(), is("G-3"));
  }

  /**
   * Test constructor with message and null paragraph.
   *
   * <p>Verifies that null paragraph values are accepted.
   */
  @Test
  public void testConstructorWithMessageAndNullParagraph() {
    ValidationError error = new ValidationError("Error message", null);

    assertThat(error.getMessage(), is("Error message"));
    assertThat(error.getAppendix(), is(""));
    assertThat(error.getParagraph(), is(nullValue()));
  }

  /**
   * Test constructor with message and empty paragraph.
   *
   * <p>Verifies that empty string paragraph values are accepted.
   */
  @Test
  public void testConstructorWithMessageAndEmptyParagraph() {
    ValidationError error = new ValidationError("Error message", "");

    assertThat(error.getMessage(), is("Error message"));
    assertThat(error.getAppendix(), is(""));
    assertThat(error.getParagraph(), is(""));
  }

  /**
   * Test constructor with message and dash paragraph.
   *
   * <p>Verifies that the special dash value for paragraph is handled correctly.
   */
  @Test
  public void testConstructorWithMessageAndDashParagraph() {
    ValidationError error = new ValidationError("Error message", "-");

    assertThat(error.getMessage(), is("Error message"));
    assertThat(error.getAppendix(), is(""));
    assertThat(error.getParagraph(), is("-"));
  }

  // ==========================================================================
  // Construction Tests - Three Argument Constructor
  // ==========================================================================

  /**
   * Test constructor with all three parameters.
   *
   * <p>Verifies that the three-argument constructor properly sets all fields.
   */
  @Test
  public void testConstructorWithAllParameters() {
    ValidationError error = new ValidationError("Invalid SCI control", "Appendix G", "G-4");

    assertThat(error.getMessage(), is("Invalid SCI control"));
    assertThat(error.getAppendix(), is("Appendix G"));
    assertThat(error.getParagraph(), is("G-4"));
  }

  /**
   * Test constructor with appendix but no "Appendix" prefix.
   *
   * <p>Verifies that the constructor stores the appendix value as-is without adding "Appendix"
   * prefix.
   */
  @Test
  public void testConstructorWithAppendixWithoutPrefix() {
    ValidationError error = new ValidationError("Error message", "G", "G-1");

    assertThat(error.getMessage(), is("Error message"));
    assertThat(error.getAppendix(), is("G"));
    assertThat(error.getParagraph(), is("G-1"));
  }

  /**
   * Test constructor with null appendix.
   *
   * <p>Verifies that null appendix values are accepted.
   */
  @Test
  public void testConstructorWithNullAppendix() {
    ValidationError error = new ValidationError("Error message", null, "G-1");

    assertThat(error.getMessage(), is("Error message"));
    assertThat(error.getAppendix(), is(nullValue()));
    assertThat(error.getParagraph(), is("G-1"));
  }

  /**
   * Test constructor with empty appendix.
   *
   * <p>Verifies that empty string appendix values are accepted.
   */
  @Test
  public void testConstructorWithEmptyAppendix() {
    ValidationError error = new ValidationError("Error message", "", "G-1");

    assertThat(error.getMessage(), is("Error message"));
    assertThat(error.getAppendix(), is(""));
    assertThat(error.getParagraph(), is("G-1"));
  }

  /**
   * Test constructor with all null parameters.
   *
   * <p>Verifies that the constructor handles all null values gracefully.
   */
  @Test
  public void testConstructorWithAllNulls() {
    ValidationError error = new ValidationError(null, null, null);

    assertThat(error.getMessage(), is(nullValue()));
    assertThat(error.getAppendix(), is(nullValue()));
    assertThat(error.getParagraph(), is(nullValue()));
  }

  // ==========================================================================
  // Getter Tests
  // ==========================================================================

  /**
   * Test getMessage() returns the correct value.
   *
   * <p>Verifies that getMessage() returns the exact message passed to the constructor.
   */
  @Test
  public void testGetMessage() {
    ValidationError error = new ValidationError("Test message", "G", "G-1");

    assertThat(error.getMessage(), is("Test message"));
  }

  /**
   * Test getAppendix() returns the correct value.
   *
   * <p>Verifies that getAppendix() returns the exact appendix passed to the constructor.
   */
  @Test
  public void testGetAppendix() {
    ValidationError error = new ValidationError("Test message", "Appendix G", "G-1");

    assertThat(error.getAppendix(), is("Appendix G"));
  }

  /**
   * Test getParagraph() returns the correct value.
   *
   * <p>Verifies that getParagraph() returns the exact paragraph passed to the constructor.
   */
  @Test
  public void testGetParagraph() {
    ValidationError error = new ValidationError("Test message", "G", "G-1");

    assertThat(error.getParagraph(), is("G-1"));
  }

  /**
   * Test that getter values are immutable.
   *
   * <p>Verifies that all getter methods consistently return the same values across multiple calls.
   */
  @Test
  public void testGettersAreConsistent() {
    ValidationError error = new ValidationError("Message", "App", "Para");

    // Call getters multiple times
    String msg1 = error.getMessage();
    String msg2 = error.getMessage();
    String app1 = error.getAppendix();
    String app2 = error.getAppendix();
    String para1 = error.getParagraph();
    String para2 = error.getParagraph();

    // Verify consistency
    assertThat(msg1, is(msg2));
    assertThat(app1, is(app2));
    assertThat(para1, is(para2));
  }

  // ==========================================================================
  // String Representation Tests
  // ==========================================================================

  /**
   * Test toString() with all parameters.
   *
   * <p>Verifies that toString() includes message, manual reference, appendix, and paragraph when
   * all are provided.
   */
  @Test
  public void testToStringWithAllParameters() {
    ValidationError error = new ValidationError("Invalid marking", "G", "G-3");

    String result = error.toString();

    assertThat(result, containsString("Invalid marking"));
    assertThat(result, containsString("DoD MANUAL NUMBER 5200.01, Volume 2, Enc 4"));
    assertThat(result, containsString("Appendix G"));
    assertThat(result, containsString("Para G-3"));
    assertThat(result, startsWith("{"));
    assertThat(result, endsWith("}"));
  }

  /**
   * Test toString() with message only (no appendix or paragraph).
   *
   * <p>Verifies that toString() omits appendix and paragraph references when using single-argument
   * constructor.
   */
  @Test
  public void testToStringWithMessageOnly() {
    ValidationError error = new ValidationError("Invalid marking");

    String result = error.toString();

    assertThat(result, containsString("Invalid marking"));
    assertThat(result, containsString("DoD MANUAL NUMBER 5200.01, Volume 2, Enc 4"));
    assertThat(result, not(containsString("Appendix")));
    assertThat(result, not(containsString("Para")));
  }

  /**
   * Test toString() with message and paragraph only (empty appendix).
   *
   * <p>Verifies that toString() omits appendix when it's empty but includes paragraph.
   */
  @Test
  public void testToStringWithParagraphButNoAppendix() {
    ValidationError error = new ValidationError("Invalid control", "G-5");

    String result = error.toString();

    assertThat(result, containsString("Invalid control"));
    assertThat(result, containsString("DoD MANUAL NUMBER 5200.01, Volume 2, Enc 4"));
    assertThat(result, not(containsString("Appendix")));
    assertThat(result, containsString("Para G-5"));
  }

  /**
   * Test toString() format structure.
   *
   * <p>Verifies that toString() follows the expected format pattern: {message: manual reference,
   * optional appendix, optional paragraph}
   */
  @Test
  public void testToStringFormatStructure() {
    ValidationError error = new ValidationError("Test error", "H", "H-2");

    String result = error.toString();

    // Should start with {message: DoD MANUAL...
    assertThat(result, startsWith("{Test error: DoD MANUAL NUMBER 5200.01, Volume 2, Enc 4"));
    assertThat(result, endsWith("}"));
  }

  /**
   * Test toString() with empty appendix does not show appendix section.
   *
   * <p>Verifies that empty string appendix values cause the appendix section to be omitted.
   */
  @Test
  public void testToStringWithEmptyAppendix() {
    ValidationError error = new ValidationError("Error", "", "G-1");

    String result = error.toString();

    assertThat(result, not(containsString("Appendix")));
    assertThat(result, containsString("Para G-1"));
  }

  /**
   * Test toString() with dash paragraph does not show paragraph section.
   *
   * <p>Verifies that dash ("-") paragraph values cause the paragraph section to be omitted.
   */
  @Test
  public void testToStringWithDashParagraph() {
    ValidationError error = new ValidationError("Error", "G", "-");

    String result = error.toString();

    assertThat(result, containsString("Appendix G"));
    assertThat(result, not(containsString("Para")));
  }

  /**
   * Test toString() with null message.
   *
   * <p>Verifies that toString() handles null message gracefully.
   */
  @Test
  public void testToStringWithNullMessage() {
    ValidationError error = new ValidationError(null, "G", "G-1");

    String result = error.toString();

    assertThat(result, containsString("DoD MANUAL NUMBER 5200.01, Volume 2, Enc 4"));
    assertThat(result, containsString("Appendix G"));
    assertThat(result, containsString("Para G-1"));
  }

  /**
   * Test toString() output is useful for logging.
   *
   * <p>Verifies that toString() produces a human-readable format suitable for log files and error
   * messages.
   */
  @Test
  public void testToStringIsLoggingFriendly() {
    ValidationError error = new ValidationError("Missing classification", "G", "G-1");

    String result = error.toString();

    // Should be single line, no newlines
    assertThat(result, not(containsString("\n")));
    // Should contain all key information
    assertThat(result, containsString("Missing classification"));
    assertThat(result, containsString("5200.01"));
  }
}
