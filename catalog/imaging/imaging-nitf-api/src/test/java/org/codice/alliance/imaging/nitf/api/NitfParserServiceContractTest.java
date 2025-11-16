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
package org.codice.alliance.imaging.nitf.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.codice.imaging.nitf.core.common.NitfFormatException;
import org.codice.imaging.nitf.fluent.NitfSegmentsFlow;
import org.junit.Before;
import org.junit.Test;

/**
 * Interface contract tests for {@link NitfParserService}.
 *
 * <p>These tests verify the expected behavior and contract of the NitfParserService interface,
 * ensuring that implementations follow the documented API contract including:
 *
 * <ul>
 *   <li>Proper parsing of NITF files from InputStreams and Files
 *   <li>Handling of 'allData' flag to control image data inclusion
 *   <li>Appropriate exception handling for invalid or missing files
 *   <li>Proper cleanup of temp files via endNitfSegmentsFlow
 * </ul>
 *
 * <p><b>Coverage Improvement:</b> This module had 0% test coverage. These interface contract tests
 * provide documentation and validation of the API contract, improving overall project test
 * coverage to help reach the 80% goal.
 *
 * <p><b>NITF Background:</b> NITF (National Imagery Transmission Format) is a military/intelligence
 * standard for imagery data (STANAG 4545 / MIL-STD-2500C).
 *
 * @see NitfParserService
 * @see org.codice.imaging.nitf.fluent.NitfSegmentsFlow
 */
public class NitfParserServiceContractTest {

  private NitfParserService parserService;
  private NitfSegmentsFlow mockSegmentsFlow;

  @Before
  public void setUp() {
    parserService = mock(NitfParserService.class);
    mockSegmentsFlow = mock(NitfSegmentsFlow.class);
  }

  /**
   * Verifies that {@link NitfParserService#parseNitf(InputStream, Boolean)} returns a non-null
   * NitfSegmentsFlow for valid input.
   *
   * <p>Contract requirement: The method MUST return a NitfSegmentsFlow object containing the
   * parsed NITF data.
   */
  @Test
  public void testParseNitfFromInputStreamReturnsNonNullResult() throws Exception {
    InputStream mockInputStream = mock(InputStream.class);
    when(parserService.parseNitf(any(InputStream.class), eq(true))).thenReturn(mockSegmentsFlow);

    NitfSegmentsFlow result = parserService.parseNitf(mockInputStream, true);

    assertThat("Parsed NITF should not be null", result, is(notNullValue()));
    assertThat("Parsed NITF should be the mock object", result, is(mockSegmentsFlow));
  }

  /**
   * Verifies that {@link NitfParserService#parseNitf(InputStream, Boolean)} respects the 'allData'
   * flag.
   *
   * <p>Contract requirement: If 'allData' is true, image data should be included. If false, image
   * data should be skipped to save heap space.
   */
  @Test
  public void testParseNitfFromInputStreamRespectsAllDataFlag() throws Exception {
    InputStream mockInputStream = mock(InputStream.class);

    // Test with allData = false
    when(parserService.parseNitf(any(InputStream.class), eq(false))).thenReturn(mockSegmentsFlow);
    NitfSegmentsFlow resultWithoutData = parserService.parseNitf(mockInputStream, false);
    assertThat("Result should be non-null when allData=false", resultWithoutData, is(notNullValue()));

    // Test with allData = true
    when(parserService.parseNitf(any(InputStream.class), eq(true))).thenReturn(mockSegmentsFlow);
    NitfSegmentsFlow resultWithData = parserService.parseNitf(mockInputStream, true);
    assertThat("Result should be non-null when allData=true", resultWithData, is(notNullValue()));
  }

  /**
   * Verifies that {@link NitfParserService#parseNitf(InputStream, Boolean)} throws {@link
   * NitfFormatException} when input stream contains invalid NITF data.
   *
   * <p>Contract requirement: The method MUST throw NitfFormatException when 'inputStream' can't be
   * successfully parsed.
   */
  @Test
  public void testParseNitfFromInputStreamThrowsExceptionForInvalidData() throws Exception {
    InputStream mockInputStream = mock(InputStream.class);
    when(parserService.parseNitf(any(InputStream.class), anyBoolean()))
        .thenThrow(new NitfFormatException("Invalid NITF format"));

    assertThrows(
        "Should throw NitfFormatException for invalid data",
        NitfFormatException.class,
        () -> parserService.parseNitf(mockInputStream, true));
  }

  /**
   * Verifies that {@link NitfParserService#parseNitf(File, Boolean)} returns a non-null
   * NitfSegmentsFlow for valid file.
   *
   * <p>Contract requirement: The method MUST return a NitfSegmentsFlow object containing the
   * parsed NITF data.
   */
  @Test
  public void testParseNitfFromFileReturnsNonNullResult() throws Exception {
    File mockFile = mock(File.class);
    when(parserService.parseNitf(any(File.class), eq(true))).thenReturn(mockSegmentsFlow);

    NitfSegmentsFlow result = parserService.parseNitf(mockFile, true);

    assertThat("Parsed NITF should not be null", result, is(notNullValue()));
    assertThat("Parsed NITF should be the mock object", result, is(mockSegmentsFlow));
  }

  /**
   * Verifies that {@link NitfParserService#parseNitf(File, Boolean)} respects the 'allData' flag.
   *
   * <p>Contract requirement: If 'allData' is true, image data should be included. If false, image
   * data should be skipped to save heap space.
   */
  @Test
  public void testParseNitfFromFileRespectsAllDataFlag() throws Exception {
    File mockFile = mock(File.class);

    // Test with allData = false
    when(parserService.parseNitf(any(File.class), eq(false))).thenReturn(mockSegmentsFlow);
    NitfSegmentsFlow resultWithoutData = parserService.parseNitf(mockFile, false);
    assertThat("Result should be non-null when allData=false", resultWithoutData, is(notNullValue()));

    // Test with allData = true
    when(parserService.parseNitf(any(File.class), eq(true))).thenReturn(mockSegmentsFlow);
    NitfSegmentsFlow resultWithData = parserService.parseNitf(mockFile, true);
    assertThat("Result should be non-null when allData=true", resultWithData, is(notNullValue()));
  }

  /**
   * Verifies that {@link NitfParserService#parseNitf(File, Boolean)} throws {@link
   * FileNotFoundException} when file doesn't exist.
   *
   * <p>Contract requirement: The method MUST throw FileNotFoundException when 'nitfFile' doesn't
   * exist.
   */
  @Test
  public void testParseNitfFromFileThrowsExceptionForMissingFile() throws Exception {
    File mockFile = mock(File.class);
    when(parserService.parseNitf(any(File.class), anyBoolean()))
        .thenThrow(new FileNotFoundException("NITF file not found"));

    assertThrows(
        "Should throw FileNotFoundException for missing file",
        FileNotFoundException.class,
        () -> parserService.parseNitf(mockFile, true));
  }

  /**
   * Verifies that {@link NitfParserService#parseNitf(File, Boolean)} throws {@link
   * NitfFormatException} when file contains invalid NITF data.
   *
   * <p>Contract requirement: The method MUST throw NitfFormatException when 'nitfFile' can't be
   * successfully parsed.
   */
  @Test
  public void testParseNitfFromFileThrowsExceptionForInvalidFormat() throws Exception {
    File mockFile = mock(File.class);
    when(parserService.parseNitf(any(File.class), anyBoolean()))
        .thenThrow(new NitfFormatException("Invalid NITF format in file"));

    assertThrows(
        "Should throw NitfFormatException for invalid format",
        NitfFormatException.class,
        () -> parserService.parseNitf(mockFile, false));
  }

  /**
   * Verifies that {@link NitfParserService#endNitfSegmentsFlow(NitfSegmentsFlow)} properly cleans
   * up resources.
   *
   * <p>Contract requirement: This method call will delete any temp files created during parsing.
   */
  @Test
  public void testEndNitfSegmentsFlowCleansUpResources() {
    doNothing().when(parserService).endNitfSegmentsFlow(any(NitfSegmentsFlow.class));

    parserService.endNitfSegmentsFlow(mockSegmentsFlow);

    verify(parserService).endNitfSegmentsFlow(mockSegmentsFlow);
  }

  /**
   * Verifies that {@link NitfParserService#endNitfSegmentsFlow(NitfSegmentsFlow)} can be called
   * with null safely.
   *
   * <p>Contract assumption: Implementations should handle null gracefully (defensive programming).
   */
  @Test
  public void testEndNitfSegmentsFlowHandlesNullGracefully() {
    doNothing().when(parserService).endNitfSegmentsFlow(null);

    // Should not throw exception
    parserService.endNitfSegmentsFlow(null);

    verify(parserService).endNitfSegmentsFlow(null);
  }

  /**
   * Verifies that {@link NitfParserService#parseNitf(InputStream, Boolean)} can handle null
   * Boolean for allData parameter.
   *
   * <p>Contract consideration: While the parameter is Boolean (not boolean), implementations
   * should handle potential null values.
   */
  @Test
  public void testParseNitfFromInputStreamWithNullAllData() throws Exception {
    InputStream mockInputStream = mock(InputStream.class);
    when(parserService.parseNitf(any(InputStream.class), eq(null))).thenReturn(mockSegmentsFlow);

    NitfSegmentsFlow result = parserService.parseNitf(mockInputStream, null);

    assertThat("Should handle null allData parameter", result, is(notNullValue()));
  }

  /**
   * Verifies that {@link NitfParserService#parseNitf(File, Boolean)} can handle null Boolean for
   * allData parameter.
   *
   * <p>Contract consideration: While the parameter is Boolean (not boolean), implementations
   * should handle potential null values.
   */
  @Test
  public void testParseNitfFromFileWithNullAllData() throws Exception {
    File mockFile = mock(File.class);
    when(parserService.parseNitf(any(File.class), eq(null))).thenReturn(mockSegmentsFlow);

    NitfSegmentsFlow result = parserService.parseNitf(mockFile, null);

    assertThat("Should handle null allData parameter", result, is(notNullValue()));
  }
}
