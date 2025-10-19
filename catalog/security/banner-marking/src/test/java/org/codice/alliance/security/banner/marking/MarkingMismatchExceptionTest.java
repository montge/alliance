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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.Test;

/**
 * Unit tests for {@link MarkingMismatchException}.
 *
 * <p>MarkingMismatchException is a runtime exception thrown when banner markings and portion
 * markings do not match or are incompatible. This is a critical security error that indicates
 * inconsistent classification markings on a document.
 *
 * <p>This exception extends {@link RuntimeException}, making it an unchecked exception that does
 * not require explicit catching or declaration in method signatures.
 *
 * <p>This test class verifies:
 *
 * <ul>
 *   <li>Constructor properly sets message
 *   <li>Message retrieval
 *   <li>Null and empty message handling
 *   <li>Exception throwing and catching behavior
 *   <li>RuntimeException inheritance
 *   <li>Stack trace availability
 * </ul>
 *
 * <p><b>Coverage Target:</b> 95%
 *
 * <p><b>Reference:</b> DoD MANUAL NUMBER 5200.01, Volume 2, Enclosure 4 - Marking Classified
 * Information
 */
public class MarkingMismatchExceptionTest {

  // ==========================================================================
  // Construction Tests
  // ==========================================================================

  /**
   * Test constructor with message.
   *
   * <p>Verifies that the constructor properly sets the error message.
   */
  @Test
  public void testConstructorWithMessage() {
    String message = "Banner marking does not match portion marking";

    MarkingMismatchException exception = new MarkingMismatchException(message);

    assertThat(exception.getMessage(), is(message));
  }

  /**
   * Test constructor with null message.
   *
   * <p>Verifies that the constructor accepts null message values.
   */
  @Test
  public void testConstructorWithNullMessage() {
    MarkingMismatchException exception = new MarkingMismatchException(null);

    assertThat(exception.getMessage(), is(nullValue()));
  }

  /**
   * Test constructor with empty message.
   *
   * <p>Verifies that the constructor accepts empty string messages.
   */
  @Test
  public void testConstructorWithEmptyMessage() {
    MarkingMismatchException exception = new MarkingMismatchException("");

    assertThat(exception.getMessage(), is(""));
  }

  /**
   * Test constructor with whitespace-only message.
   *
   * <p>Verifies that whitespace messages are preserved without trimming.
   */
  @Test
  public void testConstructorWithWhitespaceMessage() {
    String message = "   ";

    MarkingMismatchException exception = new MarkingMismatchException(message);

    assertThat(exception.getMessage(), is(message));
  }

  /**
   * Test constructor with long descriptive message.
   *
   * <p>Verifies that long error messages are properly stored and retrieved.
   */
  @Test
  public void testConstructorWithLongMessage() {
    String message =
        "Banner classification level SECRET does not match portion classification level "
            + "TOP SECRET. All portion markings must be equal to or less than the overall "
            + "document classification level specified in the banner marking.";

    MarkingMismatchException exception = new MarkingMismatchException(message);

    assertThat(exception.getMessage(), is(message));
  }

  /**
   * Test constructor with message containing special characters.
   *
   * <p>Verifies that messages with special characters like slashes (common in security markings)
   * are preserved.
   */
  @Test
  public void testConstructorWithSpecialCharactersInMessage() {
    String message =
        "Mismatch: Banner 'SECRET//NOFORN' incompatible with portion 'TOP SECRET//SI//NOFORN'";

    MarkingMismatchException exception = new MarkingMismatchException(message);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getMessage(), containsString("//"));
    assertThat(exception.getMessage(), containsString("NOFORN"));
  }

  // ==========================================================================
  // Message Tests
  // ==========================================================================

  /**
   * Test getMessage() returns the correct value.
   *
   * <p>Verifies that getMessage() returns the exact message passed to the constructor.
   */
  @Test
  public void testGetMessage() {
    String message = "Classification levels do not match";

    MarkingMismatchException exception = new MarkingMismatchException(message);

    assertThat(exception.getMessage(), is(message));
  }

  /**
   * Test getMessage() consistency across multiple calls.
   *
   * <p>Verifies that getMessage() consistently returns the same value across multiple calls.
   */
  @Test
  public void testGetMessageConsistency() {
    String message = "Portion marking exceeds banner classification";

    MarkingMismatchException exception = new MarkingMismatchException(message);

    String message1 = exception.getMessage();
    String message2 = exception.getMessage();

    assertThat(message1, is(message2));
  }

  // ==========================================================================
  // Exception Inheritance and Type Tests
  // ==========================================================================

  /**
   * Test exception is a RuntimeException.
   *
   * <p>Verifies that MarkingMismatchException is an unchecked RuntimeException.
   */
  @Test
  public void testIsRuntimeException() {
    MarkingMismatchException exception = new MarkingMismatchException("Marking mismatch detected");

    assertThat(exception, is(instanceOf(RuntimeException.class)));
  }

  /**
   * Test exception is an Exception.
   *
   * <p>Verifies that MarkingMismatchException inherits from Exception.
   */
  @Test
  public void testIsException() {
    MarkingMismatchException exception = new MarkingMismatchException("Marking mismatch detected");

    assertThat(exception, is(instanceOf(Exception.class)));
  }

  /**
   * Test exception is a Throwable.
   *
   * <p>Verifies that MarkingMismatchException inherits from Throwable.
   */
  @Test
  public void testIsThrowable() {
    MarkingMismatchException exception = new MarkingMismatchException("Marking mismatch detected");

    assertThat(exception, is(instanceOf(Throwable.class)));
  }

  // ==========================================================================
  // Exception Throwing and Catching Tests
  // ==========================================================================

  /**
   * Test exception can be thrown and caught as MarkingMismatchException.
   *
   * <p>Verifies that the exception can be properly thrown and caught by its specific type.
   */
  @Test
  public void testThrowAndCatchSpecificType() {
    String message = "Banner and portion markings are incompatible";

    try {
      throw new MarkingMismatchException(message);
    } catch (MarkingMismatchException e) {
      assertThat(e.getMessage(), is(message));
    }
  }

  /**
   * Test exception can be caught as RuntimeException.
   *
   * <p>Verifies that the exception can be caught as a RuntimeException.
   */
  @Test
  public void testThrowAndCatchAsRuntimeException() {
    try {
      throw new MarkingMismatchException("Marking mismatch detected");
    } catch (RuntimeException e) {
      assertThat(e, is(instanceOf(MarkingMismatchException.class)));
    }
  }

  /**
   * Test exception can be caught as Exception.
   *
   * <p>Verifies that the exception can be caught as a general Exception.
   */
  @Test
  public void testThrowAndCatchAsException() {
    try {
      throw new MarkingMismatchException("Marking mismatch detected");
    } catch (Exception e) {
      assertThat(e, is(instanceOf(MarkingMismatchException.class)));
      assertThat(e, is(instanceOf(RuntimeException.class)));
    }
  }

  /**
   * Test exception does not require declaration in method signature.
   *
   * <p>Verifies that as a RuntimeException, MarkingMismatchException does not need to be declared
   * in the throws clause. This test method intentionally does not declare "throws
   * MarkingMismatchException".
   */
  @Test
  public void testDoesNotRequireThrowsDeclaration() {
    // This method compiles without "throws MarkingMismatchException"
    throwMarkingMismatchExceptionWithoutDeclaration();
  }

  /**
   * Helper method that throws MarkingMismatchException without declaring it.
   *
   * <p>This demonstrates that MarkingMismatchException is an unchecked exception.
   */
  private void throwMarkingMismatchExceptionWithoutDeclaration() {
    // Method compiles without "throws" declaration because it's a RuntimeException
    if (false) { // Never actually throws in test
      throw new MarkingMismatchException("Test");
    }
  }

  // ==========================================================================
  // Stack Trace and Debugging Tests
  // ==========================================================================

  /**
   * Test exception provides stack trace.
   *
   * <p>Verifies that the exception includes a proper stack trace for debugging.
   */
  @Test
  public void testExceptionHasStackTrace() {
    MarkingMismatchException exception = new MarkingMismatchException("Marking mismatch detected");

    StackTraceElement[] stackTrace = exception.getStackTrace();

    assertThat(stackTrace, is(notNullValue()));
    assertThat(stackTrace.length, is(greaterThan(0)));
  }

  /**
   * Test exception stack trace contains test method.
   *
   * <p>Verifies that the stack trace correctly identifies where the exception was created.
   */
  @Test
  public void testStackTraceContainsOrigin() {
    MarkingMismatchException exception = new MarkingMismatchException("Marking mismatch detected");

    StackTraceElement[] stackTrace = exception.getStackTrace();
    boolean foundTestMethod = false;

    for (StackTraceElement element : stackTrace) {
      if (element.getClassName().contains("MarkingMismatchExceptionTest")) {
        foundTestMethod = true;
        break;
      }
    }

    assertThat(foundTestMethod, is(true));
  }

  /**
   * Test exception toString() is informative.
   *
   * <p>Verifies that toString() provides useful debugging information including the exception type
   * and message.
   */
  @Test
  public void testToStringIsInformative() {
    String message = "Banner marking mismatch";

    MarkingMismatchException exception = new MarkingMismatchException(message);

    String result = exception.toString();

    assertThat(result, containsString("MarkingMismatchException"));
    assertThat(result, containsString(message));
  }

  /**
   * Test exception toString() with null message.
   *
   * <p>Verifies that toString() handles null message gracefully.
   */
  @Test
  public void testToStringWithNullMessage() {
    MarkingMismatchException exception = new MarkingMismatchException(null);

    String result = exception.toString();

    assertThat(result, containsString("MarkingMismatchException"));
  }

  // ==========================================================================
  // Real-World Scenario Tests
  // ==========================================================================

  /**
   * Test exception with realistic classification mismatch scenario.
   *
   * <p>Verifies the exception properly conveys a typical classification level mismatch error.
   */
  @Test
  public void testRealisticClassificationMismatchScenario() {
    String message =
        "Classification mismatch: Banner classification 'SECRET' is lower than "
            + "portion classification 'TOP SECRET'. Portion classification cannot exceed "
            + "banner classification.";

    MarkingMismatchException exception = new MarkingMismatchException(message);

    assertThat(exception.getMessage(), containsString("SECRET"));
    assertThat(exception.getMessage(), containsString("TOP SECRET"));
    assertThat(exception.getMessage(), containsString("Classification mismatch"));
  }

  /**
   * Test exception with realistic dissemination control mismatch scenario.
   *
   * <p>Verifies the exception properly conveys a dissemination control compatibility error.
   */
  @Test
  public void testRealisticDisseminationControlMismatchScenario() {
    String message =
        "Dissemination control mismatch: Banner includes 'NOFORN' (no foreign dissemination) "
            + "but portion marking specifies 'REL TO GBR' (releasable to Great Britain). "
            + "These controls are incompatible.";

    MarkingMismatchException exception = new MarkingMismatchException(message);

    assertThat(exception.getMessage(), containsString("NOFORN"));
    assertThat(exception.getMessage(), containsString("REL TO GBR"));
    assertThat(exception.getMessage(), containsString("incompatible"));
  }

  /**
   * Test exception with realistic SCI control mismatch scenario.
   *
   * <p>Verifies the exception properly conveys an SCI (Sensitive Compartmented Information) control
   * mismatch.
   */
  @Test
  public void testRealisticSciControlMismatchScenario() {
    String message =
        "SCI control mismatch: Portion marking contains 'SI-GAMMA' which is not declared "
            + "in banner marking. All SCI controls in portion markings must appear in the "
            + "banner marking.";

    MarkingMismatchException exception = new MarkingMismatchException(message);

    assertThat(exception.getMessage(), containsString("SI-GAMMA"));
    assertThat(exception.getMessage(), containsString("SCI control"));
    assertThat(exception.getMessage(), containsString("banner marking"));
  }

  // ==========================================================================
  // Edge Case Tests
  // ==========================================================================

  /**
   * Test multiple exceptions with same message are independent.
   *
   * <p>Verifies that creating multiple exceptions with the same message creates independent
   * instances.
   */
  @Test
  public void testMultipleExceptionsAreIndependent() {
    String message = "Marking mismatch";

    MarkingMismatchException exception1 = new MarkingMismatchException(message);
    MarkingMismatchException exception2 = new MarkingMismatchException(message);

    assertThat(exception1, is(not(sameInstance(exception2))));
    assertThat(exception1.getMessage(), is(exception2.getMessage()));
  }

  /**
   * Test exception with message containing newlines.
   *
   * <p>Verifies that multi-line error messages are preserved.
   */
  @Test
  public void testConstructorWithMultilineMessage() {
    String message =
        "Marking mismatch detected:\n"
            + "  Banner: SECRET//NOFORN\n"
            + "  Portion: TOP SECRET//SI//NOFORN\n"
            + "Error: Portion classification exceeds banner classification";

    MarkingMismatchException exception = new MarkingMismatchException(message);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getMessage(), containsString("\n"));
  }

  /**
   * Test exception with Unicode characters in message.
   *
   * <p>Verifies that messages with Unicode characters are properly handled.
   */
  @Test
  public void testConstructorWithUnicodeMessage() {
    String message = "Marking mismatch: Classification \u00BB SECRET \u00AB does not match";

    MarkingMismatchException exception = new MarkingMismatchException(message);

    assertThat(exception.getMessage(), is(message));
    assertThat(exception.getMessage(), containsString("\u00BB"));
    assertThat(exception.getMessage(), containsString("\u00AB"));
  }
}
