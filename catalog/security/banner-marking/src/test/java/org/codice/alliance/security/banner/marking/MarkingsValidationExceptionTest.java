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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 * Unit tests for {@link MarkingsValidationException}.
 *
 * <p>MarkingsValidationException is thrown when banner markings fail validation. It contains:
 *
 * <ul>
 *   <li>A detail message describing the validation failure
 *   <li>The input markings that failed validation
 *   <li>A set of {@link ValidationError} objects detailing all validation failures
 * </ul>
 *
 * <p>This test class verifies:
 *
 * <ul>
 *   <li>Both constructor overloads (with and without error set)
 *   <li>Message formatting with single and multiple errors
 *   <li>Input markings preservation
 *   <li>Error set immutability
 *   <li>Null and empty parameter handling
 *   <li>Exception behavior and inheritance
 * </ul>
 *
 * <p><b>Coverage Target:</b> 95%
 *
 * <p><b>Reference:</b> DoD MANUAL NUMBER 5200.01, Volume 2, Enclosure 4 - Marking Classified
 * Information
 */
public class MarkingsValidationExceptionTest {

  // ==========================================================================
  // Construction Tests - Two Argument Constructor (Message + Input Markings)
  // ==========================================================================

  /**
   * Test constructor with message and input markings only.
   *
   * <p>Verifies that the two-argument constructor properly sets the message and input markings, and
   * initializes the errors set as empty.
   */
  @Test
  public void testConstructorWithMessageAndInputMarkings() {
    String message = "Invalid classification level";
    String inputMarkings = "SECRET//NOFORN";

    MarkingsValidationException exception = new MarkingsValidationException(message, inputMarkings);

    assertThat(exception.getMessage(), containsString(message));
    assertThat(exception.getMessage(), containsString(inputMarkings));
    assertThat(exception.getInputMarkings(), is(inputMarkings));
    assertThat(exception.getErrors(), is(empty()));
  }

  /**
   * Test constructor with null message.
   *
   * <p>Verifies that the constructor accepts null message values.
   */
  @Test
  public void testConstructorWithNullMessage() {
    String inputMarkings = "SECRET//NOFORN";

    MarkingsValidationException exception = new MarkingsValidationException(null, inputMarkings);

    assertThat(exception.getMessage(), containsString(inputMarkings));
    assertThat(exception.getInputMarkings(), is(inputMarkings));
    assertThat(exception.getErrors(), is(empty()));
  }

  /**
   * Test constructor with null input markings.
   *
   * <p>Verifies that the constructor accepts null input markings.
   */
  @Test
  public void testConstructorWithNullInputMarkings() {
    String message = "Invalid classification level";

    MarkingsValidationException exception = new MarkingsValidationException(message, null);

    assertThat(exception.getMessage(), containsString(message));
    assertThat(exception.getInputMarkings(), is(nullValue()));
    assertThat(exception.getErrors(), is(empty()));
  }

  /**
   * Test constructor with empty message.
   *
   * <p>Verifies that the constructor accepts empty string messages.
   */
  @Test
  public void testConstructorWithEmptyMessage() {
    String inputMarkings = "SECRET//NOFORN";

    MarkingsValidationException exception = new MarkingsValidationException("", inputMarkings);

    assertThat(exception.getMessage(), containsString(inputMarkings));
    assertThat(exception.getInputMarkings(), is(inputMarkings));
  }

  /**
   * Test constructor with empty input markings.
   *
   * <p>Verifies that the constructor accepts empty string input markings.
   */
  @Test
  public void testConstructorWithEmptyInputMarkings() {
    String message = "Invalid classification level";

    MarkingsValidationException exception = new MarkingsValidationException(message, "");

    assertThat(exception.getMessage(), containsString(message));
    assertThat(exception.getInputMarkings(), is(""));
  }

  /**
   * Test constructor with both null parameters.
   *
   * <p>Verifies that the constructor handles both null values gracefully.
   */
  @Test
  public void testConstructorWithBothNulls() {
    MarkingsValidationException exception = new MarkingsValidationException(null, null);

    assertThat(exception.getInputMarkings(), is(nullValue()));
    assertThat(exception.getErrors(), is(empty()));
  }

  // ==========================================================================
  // Construction Tests - Three Argument Constructor (Message + Input Markings + Errors)
  // ==========================================================================

  /**
   * Test constructor with message, input markings, and single error.
   *
   * <p>Verifies that the three-argument constructor properly sets all fields with a single
   * validation error.
   */
  @Test
  public void testConstructorWithSingleError() {
    String message = "Validation failed";
    String inputMarkings = "SECRET//NOFORN";
    ValidationError error = new ValidationError("Invalid dissemination control", "G", "G-3");
    Set<ValidationError> errors = ImmutableSet.of(error);

    MarkingsValidationException exception =
        new MarkingsValidationException(message, inputMarkings, errors);

    assertThat(exception.getInputMarkings(), is(inputMarkings));
    assertThat(exception.getErrors(), hasSize(1));
    assertThat(exception.getErrors(), contains(error));
  }

  /**
   * Test constructor with message, input markings, and multiple errors.
   *
   * <p>Verifies that the three-argument constructor properly stores multiple validation errors.
   */
  @Test
  public void testConstructorWithMultipleErrors() {
    String message = "Multiple validation failures";
    String inputMarkings = "TOP SECRET//SI-GAMMA//NOFORN";
    ValidationError error1 = new ValidationError("Invalid classification", "A", "A-1");
    ValidationError error2 = new ValidationError("Invalid SCI control", "G", "G-4");
    ValidationError error3 = new ValidationError("Missing required marking", "B", "B-2");
    Set<ValidationError> errors = ImmutableSet.of(error1, error2, error3);

    MarkingsValidationException exception =
        new MarkingsValidationException(message, inputMarkings, errors);

    assertThat(exception.getInputMarkings(), is(inputMarkings));
    assertThat(exception.getErrors(), hasSize(3));
    assertThat(exception.getErrors(), containsInAnyOrder(error1, error2, error3));
  }

  /**
   * Test constructor with empty error set.
   *
   * <p>Verifies that the constructor handles empty error sets.
   */
  @Test
  public void testConstructorWithEmptyErrorSet() {
    String message = "Validation failed";
    String inputMarkings = "SECRET//NOFORN";
    Set<ValidationError> errors = ImmutableSet.of();

    MarkingsValidationException exception =
        new MarkingsValidationException(message, inputMarkings, errors);

    assertThat(exception.getInputMarkings(), is(inputMarkings));
    assertThat(exception.getErrors(), is(empty()));
  }

  /**
   * Test constructor with mutable error set.
   *
   * <p>Verifies that the constructor creates an immutable copy of the error set.
   */
  @Test
  public void testConstructorCopiesErrorSet() {
    String message = "Validation failed";
    String inputMarkings = "SECRET//NOFORN";
    ValidationError error = new ValidationError("Invalid marking");
    Set<ValidationError> mutableErrors = new HashSet<>();
    mutableErrors.add(error);

    MarkingsValidationException exception =
        new MarkingsValidationException(message, inputMarkings, mutableErrors);

    // Modify the original set
    ValidationError newError = new ValidationError("Another error");
    mutableErrors.add(newError);

    // Exception should still have only the original error
    assertThat(exception.getErrors(), hasSize(1));
    assertThat(exception.getErrors(), contains(error));
    assertThat(exception.getErrors(), not(contains(newError)));
  }

  // ==========================================================================
  // Input Markings Tests
  // ==========================================================================

  /**
   * Test getInputMarkings() returns the correct value.
   *
   * <p>Verifies that getInputMarkings() returns the exact input markings passed to the constructor.
   */
  @Test
  public void testGetInputMarkings() {
    String inputMarkings = "TOP SECRET//SI//NOFORN";

    MarkingsValidationException exception = new MarkingsValidationException("Test", inputMarkings);

    assertThat(exception.getInputMarkings(), is(inputMarkings));
  }

  /**
   * Test getInputMarkings() with complex marking string.
   *
   * <p>Verifies that complex multi-part markings are preserved exactly.
   */
  @Test
  public void testGetInputMarkingsWithComplexMarking() {
    String inputMarkings = "TOP SECRET//SI-G//TK//RSEN//REL TO USA, GBR, CAN, AUS, NZL";

    MarkingsValidationException exception = new MarkingsValidationException("Test", inputMarkings);

    assertThat(exception.getInputMarkings(), is(inputMarkings));
  }

  // ==========================================================================
  // Error Set Tests
  // ==========================================================================

  /**
   * Test getErrors() returns empty set for two-argument constructor.
   *
   * <p>Verifies that the two-argument constructor initializes errors as an empty immutable set.
   */
  @Test
  public void testGetErrorsEmptyForTwoArgConstructor() {
    MarkingsValidationException exception =
        new MarkingsValidationException("Test", "SECRET//NOFORN");

    assertThat(exception.getErrors(), is(notNullValue()));
    assertThat(exception.getErrors(), is(empty()));
  }

  /**
   * Test getErrors() returns immutable set.
   *
   * <p>Verifies that the returned error set cannot be modified.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testGetErrorsReturnsImmutableSet() {
    ValidationError error = new ValidationError("Test error");
    Set<ValidationError> errors = ImmutableSet.of(error);

    MarkingsValidationException exception =
        new MarkingsValidationException("Test", "SECRET//NOFORN", errors);

    Set<ValidationError> returnedErrors = exception.getErrors();
    // Should throw UnsupportedOperationException
    returnedErrors.add(new ValidationError("Another error"));
  }

  /**
   * Test getErrors() consistency across multiple calls.
   *
   * <p>Verifies that getErrors() returns the same set instance on multiple calls.
   */
  @Test
  public void testGetErrorsConsistency() {
    ValidationError error = new ValidationError("Test error");
    Set<ValidationError> errors = ImmutableSet.of(error);

    MarkingsValidationException exception =
        new MarkingsValidationException("Test", "SECRET//NOFORN", errors);

    Set<ValidationError> errors1 = exception.getErrors();
    Set<ValidationError> errors2 = exception.getErrors();

    assertThat(errors1, is(errors2));
  }

  // ==========================================================================
  // Message Formatting Tests
  // ==========================================================================

  /**
   * Test getMessage() format with no errors.
   *
   * <p>Verifies that getMessage() returns a formatted message containing both the error message and
   * input markings when no validation errors are present.
   */
  @Test
  public void testGetMessageWithNoErrors() {
    String message = "Invalid banner format";
    String inputMarkings = "SECRET//NOFORN";

    MarkingsValidationException exception = new MarkingsValidationException(message, inputMarkings);

    String result = exception.getMessage();

    assertThat(result, containsString(message));
    assertThat(result, containsString(inputMarkings));
  }

  /**
   * Test getMessage() format with single error.
   *
   * <p>Verifies that getMessage() includes the validation error details in the formatted message.
   */
  @Test
  public void testGetMessageWithSingleError() {
    String message = "Validation failed";
    String inputMarkings = "SECRET//NOFORN";
    ValidationError error = new ValidationError("Invalid dissemination control", "G", "G-3");
    Set<ValidationError> errors = ImmutableSet.of(error);

    MarkingsValidationException exception =
        new MarkingsValidationException(message, inputMarkings, errors);

    String result = exception.getMessage();

    assertThat(result, containsString(message));
    assertThat(result, containsString(inputMarkings));
    assertThat(result, containsString("Invalid dissemination control"));
    assertThat(result, containsString("DoD MANUAL NUMBER 5200.01"));
  }

  /**
   * Test getMessage() format with multiple errors.
   *
   * <p>Verifies that getMessage() includes all validation errors in the formatted message,
   * separated by newlines.
   */
  @Test
  public void testGetMessageWithMultipleErrors() {
    String message = "Multiple validation failures";
    String inputMarkings = "TOP SECRET//SI-GAMMA//NOFORN";
    ValidationError error1 = new ValidationError("Invalid classification", "A", "A-1");
    ValidationError error2 = new ValidationError("Invalid SCI control", "G", "G-4");
    Set<ValidationError> errors = ImmutableSet.of(error1, error2);

    MarkingsValidationException exception =
        new MarkingsValidationException(message, inputMarkings, errors);

    String result = exception.getMessage();

    assertThat(result, containsString(message));
    assertThat(result, containsString(inputMarkings));
    assertThat(result, containsString("Invalid classification"));
    assertThat(result, containsString("Invalid SCI control"));
    // Should contain newlines for multiple errors
    assertThat(result, containsString("\n"));
  }

  /**
   * Test getMessage() includes all error details.
   *
   * <p>Verifies that getMessage() includes complete ValidationError.toString() output for each
   * error.
   */
  @Test
  public void testGetMessageIncludesErrorDetails() {
    String message = "Validation failed";
    String inputMarkings = "SECRET//NOFORN";
    ValidationError error = new ValidationError("Missing required field", "B", "B-3");
    Set<ValidationError> errors = ImmutableSet.of(error);

    MarkingsValidationException exception =
        new MarkingsValidationException(message, inputMarkings, errors);

    String result = exception.getMessage();

    // Should include the complete error toString() output
    assertThat(result, containsString("Missing required field"));
    assertThat(result, containsString("DoD MANUAL NUMBER 5200.01, Volume 2, Enc 4"));
    assertThat(result, containsString("Appendix B"));
    assertThat(result, containsString("Para B-3"));
  }

  // ==========================================================================
  // Exception Behavior Tests
  // ==========================================================================

  /**
   * Test exception can be thrown and caught as MarkingsValidationException.
   *
   * <p>Verifies that the exception can be properly thrown and caught by its specific type.
   */
  @Test
  public void testThrowAndCatchSpecificType() {
    try {
      throw new MarkingsValidationException("Test error", "SECRET//NOFORN");
    } catch (MarkingsValidationException e) {
      assertThat(e.getMessage(), containsString("Test error"));
      assertThat(e.getInputMarkings(), is("SECRET//NOFORN"));
    }
  }

  /**
   * Test exception can be caught as Exception.
   *
   * <p>Verifies that the exception inherits from Exception and can be caught as such.
   */
  @Test
  public void testThrowAndCatchAsException() {
    try {
      throw new MarkingsValidationException("Test error", "SECRET//NOFORN");
    } catch (Exception e) {
      assertThat(e, instanceOf(MarkingsValidationException.class));
      assertThat(e.getMessage(), containsString("Test error"));
    }
  }

  /**
   * Test exception provides stack trace.
   *
   * <p>Verifies that the exception includes a proper stack trace for debugging.
   */
  @Test
  public void testExceptionHasStackTrace() {
    MarkingsValidationException exception =
        new MarkingsValidationException("Test error", "SECRET//NOFORN");

    StackTraceElement[] stackTrace = exception.getStackTrace();

    assertThat(stackTrace, is(notNullValue()));
    assertThat(stackTrace.length, is(greaterThan(0)));
  }

  /**
   * Test exception is not a RuntimeException.
   *
   * <p>Verifies that MarkingsValidationException is a checked exception, not a runtime exception.
   */
  @Test
  public void testIsCheckedException() {
    MarkingsValidationException exception =
        new MarkingsValidationException("Test error", "SECRET//NOFORN");

    assertThat(exception, is(instanceOf(Exception.class)));
    assertThat(exception, is(not(instanceOf(RuntimeException.class))));
  }

  /**
   * Test exception toString() is informative.
   *
   * <p>Verifies that toString() provides useful debugging information.
   */
  @Test
  public void testToStringIsInformative() {
    ValidationError error = new ValidationError("Test error", "G", "G-1");
    Set<ValidationError> errors = ImmutableSet.of(error);

    MarkingsValidationException exception =
        new MarkingsValidationException("Validation failed", "SECRET//NOFORN", errors);

    String result = exception.toString();

    assertThat(result, containsString("MarkingsValidationException"));
  }
}
