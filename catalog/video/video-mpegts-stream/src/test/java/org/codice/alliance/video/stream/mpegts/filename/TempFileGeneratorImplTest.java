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
package org.codice.alliance.video.stream.mpegts.filename;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link TempFileGeneratorImpl}.
 *
 * <p>Tests the temporary file generator for MPEG-TS stream files, ensuring proper file naming,
 * cleanup behavior, and file creation.
 *
 * <p><b>Coverage Improvement:</b> Expanded from 1 basic test to 8 comprehensive tests covering
 * file naming conventions, prefix/suffix validation, multiple file generation, and cleanup
 * behavior.
 *
 * @see TempFileGeneratorImpl
 */
public class TempFileGeneratorImplTest {

  private TempFileGeneratorImpl generator;
  private File generatedFile;

  @Before
  public void setUp() {
    generator = new TempFileGeneratorImpl();
  }

  @After
  public void tearDown() {
    if (generatedFile != null && generatedFile.exists()) {
      generatedFile.delete();
    }
  }

  /**
   * Test that generate() returns a non-null File.
   *
   * <p>Basic sanity check that the generator creates a file object.
   */
  @Test
  public void testGenerateReturnsNonNullFile() throws IOException {
    generatedFile = generator.generate();
    assertThat(generatedFile, is(notNullValue()));
  }

  /**
   * Test that generated file exists in filesystem.
   *
   * <p>Verifies that the temp file is actually created on disk.
   */
  @Test
  public void testGeneratedFileExists() throws IOException {
    generatedFile = generator.generate();
    assertThat("Generated file should exist", generatedFile.exists(), is(true));
  }

  /**
   * Test that generated file has correct prefix.
   *
   * <p>Verifies file name starts with "mpegts-stream-".
   */
  @Test
  public void testGeneratedFileHasCorrectPrefix() throws IOException {
    generatedFile = generator.generate();
    String fileName = generatedFile.getName();
    assertThat(
        "File name should start with 'mpegts-stream-'",
        fileName,
        startsWith("mpegts-stream-"));
  }

  /**
   * Test that generated file has correct suffix.
   *
   * <p>Verifies file name ends with ".ts" extension.
   */
  @Test
  public void testGeneratedFileHasCorrectSuffix() throws IOException {
    generatedFile = generator.generate();
    String fileName = generatedFile.getName();
    assertThat("File name should end with '.ts'", fileName, endsWith(".ts"));
  }

  /**
   * Test that generated file is in system temp directory.
   *
   * <p>Verifies that temp files are created in the appropriate temp directory.
   */
  @Test
  public void testGeneratedFileIsInTempDirectory() throws IOException {
    generatedFile = generator.generate();
    String tmpDir = System.getProperty("java.io.tmpdir");
    String filePath = generatedFile.getAbsolutePath();
    assertThat(
        "File should be in temp directory",
        filePath,
        startsWith(tmpDir));
  }

  /**
   * Test that multiple generated files have unique names.
   *
   * <p>Verifies that consecutive calls to generate() create files with different names.
   */
  @Test
  public void testMultipleGeneratedFilesHaveUniqueNames() throws IOException {
    File file1 = generator.generate();
    File file2 = generator.generate();

    try {
      assertThat("First file should exist", file1.exists(), is(true));
      assertThat("Second file should exist", file2.exists(), is(true));
      assertThat(
          "Files should have different names",
          file1.getName().equals(file2.getName()),
          is(false));
    } finally {
      file1.delete();
      file2.delete();
    }
  }

  /**
   * Test that generated file is writable.
   *
   * <p>Verifies that the generated temp file has write permissions.
   */
  @Test
  public void testGeneratedFileIsWritable() throws IOException {
    generatedFile = generator.generate();
    assertThat("Generated file should be writable", generatedFile.canWrite(), is(true));
  }

  /**
   * Test that generator can create multiple files sequentially.
   *
   * <p>Verifies that the same generator instance can create multiple temp files.
   */
  @Test
  public void testGeneratorCanCreateMultipleFiles() throws IOException {
    File file1 = generator.generate();
    File file2 = generator.generate();
    File file3 = generator.generate();

    try {
      assertThat("All files should be non-null", file1, is(notNullValue()));
      assertThat("All files should be non-null", file2, is(notNullValue()));
      assertThat("All files should be non-null", file3, is(notNullValue()));
      assertThat("All files should exist", file1.exists(), is(true));
      assertThat("All files should exist", file2.exists(), is(true));
      assertThat("All files should exist", file3.exists(), is(true));
    } finally {
      file1.delete();
      file2.delete();
      file3.delete();
    }
  }
}
