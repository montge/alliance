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
package org.codice.alliance.imaging.chip.service.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link ChipOutOfBoundsException}.
 *
 * <p>Tests the exception class to ensure proper message handling and exception chaining.
 *
 * <p><b>Coverage Improvement:</b> This module had 0% test coverage. These tests provide basic
 * validation of the exception class, improving overall project test coverage.
 */
public class ChipOutOfBoundsExceptionTest {

  @Test
  public void testExceptionWithMessage() {
    String expectedMessage = "Chip coordinates are outside image bounds";
    ChipOutOfBoundsException exception = new ChipOutOfBoundsException(expectedMessage);

    assertThat("Exception should have message", exception.getMessage(), is(expectedMessage));
    assertThat("Exception should not have cause", exception.getCause(), is(nullValue()));
  }

  @Test
  public void testExceptionWithMessageAndCause() {
    String expectedMessage = "Chip operation failed";
    IllegalArgumentException cause = new IllegalArgumentException("Invalid coordinates");

    ChipOutOfBoundsException exception = new ChipOutOfBoundsException(expectedMessage, cause);

    assertThat("Exception should have message", exception.getMessage(), is(expectedMessage));
    assertThat("Exception should have cause", exception.getCause(), is(notNullValue()));
    assertThat("Exception cause should match", exception.getCause(), is(cause));
  }

  @Test
  public void testExceptionIsThrowable() {
    ChipOutOfBoundsException exception = new ChipOutOfBoundsException("Test");

    assertThat("Exception should be instance of Exception", exception instanceof Exception, is(true));
    assertThat("Exception should be instance of Throwable", exception instanceof Throwable, is(true));
  }

  @Test
  public void testExceptionWithNullMessage() {
    ChipOutOfBoundsException exception = new ChipOutOfBoundsException(null);

    assertThat("Exception should allow null message", exception.getMessage(), is(nullValue()));
  }

  @Test
  public void testExceptionCanBeThrown() {
    try {
      throw new ChipOutOfBoundsException("Test exception");
    } catch (ChipOutOfBoundsException e) {
      assertThat("Exception should be catchable", e.getMessage(), is("Test exception"));
    }
  }
}
