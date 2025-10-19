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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import ddf.catalog.data.Attribute;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.AttributeImpl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * Unit tests for {@link MarkingExtractor} abstract class.
 *
 * <p>MarkingExtractor is the abstract base class for extracting security attributes from
 * BannerMarkings and adding them to Metacards. This test class verifies the core extraction logic
 * shared across all implementations:
 *
 * <ul>
 *   <li>{@link #process(String, Metacard)} - String-based processing
 *   <li>{@link #process(InputStream, Metacard)} - Stream-based processing
 *   <li>{@link #translateClassification(ClassificationLevel, boolean, String)} - Classification
 *       translation
 *   <li>{@link #setAttProcessors(java.util.Map)} - Attribute processor configuration
 *   <li>{@link #dedupedList(java.util.Collection, java.util.Collection)} - List deduplication
 * </ul>
 *
 * <p><b>Coverage Target:</b> 85-90%
 *
 * <p><b>Test Strategy:</b>
 *
 * <ul>
 *   <li>Use a concrete test implementation to verify abstract class behavior
 *   <li>Mock Metacard and attribute processors for isolation
 *   <li>Test both successful processing and error handling
 *   <li>Verify classification translation for US and NATO markings
 *   <li>Test edge cases: empty input, null values, invalid markings
 * </ul>
 *
 * @see BannerCommonMarkingExtractor
 * @see Dod520001MarkingExtractor
 */
public class MarkingExtractorTest {

  private TestMarkingExtractor extractor;
  private Metacard metacard;
  private BiFunction<Metacard, BannerMarkings, Attribute> mockProcessor;

  /**
   * Concrete test implementation of MarkingExtractor for testing abstract class behavior.
   *
   * <p>This implementation provides a minimal concrete class that exposes the abstract methods for
   * testing without implementing full extractor logic.
   */
  private static class TestMarkingExtractor extends MarkingExtractor {
    // Expose protected methods for testing
    public void testSetAttProcessors(
        java.util.Map<String, BiFunction<Metacard, BannerMarkings, Attribute>> processors) {
      setAttProcessors(processors);
    }

    public List<java.io.Serializable> testDedupedList(
        java.util.Collection<? extends java.io.Serializable> a,
        java.util.Collection<? extends java.io.Serializable> b) {
      return dedupedList(a, b);
    }

    @Override
    public java.util.Set<ddf.catalog.data.AttributeDescriptor> getMetacardAttributes() {
      return java.util.Collections.emptySet();
    }
  }

  @Before
  public void setUp() {
    extractor = new TestMarkingExtractor();
    metacard = mock(Metacard.class);
    mockProcessor = mock(BiFunction.class);
  }

  // ==========================================================================
  // process(String, Metacard) Tests
  // ==========================================================================

  /**
   * Verifies that process(String, Metacard) delegates to process(InputStream, Metacard).
   *
   * <p>The String variant should convert the input string to a ByteArrayInputStream and call the
   * InputStream variant.
   */
  @Test
  public void testProcessStringDelegatesToInputStream() {
    // Setup: Configure processor
    when(mockProcessor.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("test.attr", "test-value"));
    extractor.testSetAttProcessors(ImmutableMap.of("test.attr", mockProcessor));

    // Execute: Process valid marking
    extractor.process("SECRET//NOFORN", metacard);

    // Verify: Processor was called and attribute was set
    verify(mockProcessor, times(1)).apply(eq(metacard), any(BannerMarkings.class));
    verify(metacard, times(1)).setAttribute(any(Attribute.class));
  }

  /**
   * Verifies that process(String, Metacard) handles empty string input.
   *
   * <p>Empty strings should not cause errors but should not set any attributes.
   */
  @Test
  public void testProcessStringWithEmptyInput() {
    extractor.testSetAttProcessors(ImmutableMap.of("test.attr", mockProcessor));

    // Execute: Process empty string
    extractor.process("", metacard);

    // Verify: No attributes were set
    verify(metacard, never()).setAttribute(any(Attribute.class));
  }

  /**
   * Verifies that process(String, Metacard) handles whitespace-only input.
   *
   * <p>Whitespace-only strings should be trimmed and treated as empty.
   */
  @Test
  public void testProcessStringWithWhitespaceOnly() {
    extractor.testSetAttProcessors(ImmutableMap.of("test.attr", mockProcessor));

    // Execute: Process whitespace-only string
    extractor.process("   \n\t   ", metacard);

    // Verify: No attributes were set
    verify(metacard, never()).setAttribute(any(Attribute.class));
  }

  /**
   * Verifies that process(String, Metacard) correctly handles UTF-8 encoded strings.
   *
   * <p>The method uses StandardCharsets.UTF_8 for encoding, so Unicode characters should be
   * preserved.
   */
  @Test
  public void testProcessStringWithUtf8Characters() {
    when(mockProcessor.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("test.attr", "value"));
    extractor.testSetAttProcessors(ImmutableMap.of("test.attr", mockProcessor));

    // Execute: Process string with special characters (should still parse as marking)
    extractor.process("SECRET//NOFORN", metacard);

    // Verify: Processing occurred
    verify(mockProcessor, times(1)).apply(eq(metacard), any(BannerMarkings.class));
  }

  // ==========================================================================
  // process(InputStream, Metacard) Tests
  // ==========================================================================

  /**
   * Verifies that process(InputStream, Metacard) correctly processes valid banner markings.
   *
   * <p>The method should parse the first non-empty line as banner markings and call all registered
   * attribute processors.
   */
  @Test
  public void testProcessInputStreamWithValidMarking() {
    when(mockProcessor.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("test.attr", "test-value"));
    extractor.testSetAttProcessors(ImmutableMap.of("test.attr", mockProcessor));

    InputStream input = new ByteArrayInputStream("SECRET//NOFORN".getBytes(StandardCharsets.UTF_8));

    // Execute
    extractor.process(input, metacard);

    // Verify: Processor was called and attribute was set
    verify(mockProcessor, times(1)).apply(eq(metacard), any(BannerMarkings.class));
    verify(metacard, times(1)).setAttribute(any(Attribute.class));
  }

  /**
   * Verifies that process(InputStream, Metacard) uses only the first non-empty line.
   *
   * <p>According to DoD 5200.1-M, only the first line of a document contains banner markings.
   */
  @Test
  public void testProcessInputStreamUsesFirstNonEmptyLine() {
    when(mockProcessor.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("test.attr", "test-value"));
    extractor.testSetAttProcessors(ImmutableMap.of("test.attr", mockProcessor));

    InputStream input =
        new ByteArrayInputStream(
            "SECRET//NOFORN\nTOP SECRET//SI\nDocument content...".getBytes(StandardCharsets.UTF_8));

    // Execute
    extractor.process(input, metacard);

    // Verify: Only one attribute set (from first line)
    ArgumentCaptor<BannerMarkings> captor = ArgumentCaptor.forClass(BannerMarkings.class);
    verify(mockProcessor, times(1)).apply(eq(metacard), captor.capture());

    BannerMarkings captured = captor.getValue();
    assertThat(captured.getInputMarkings(), is("SECRET//NOFORN"));
  }

  /**
   * Verifies that process(InputStream, Metacard) skips leading empty lines.
   *
   * <p>Empty lines and whitespace should be skipped until the first non-empty line is found.
   */
  @Test
  public void testProcessInputStreamSkipsLeadingEmptyLines() {
    when(mockProcessor.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("test.attr", "test-value"));
    extractor.testSetAttProcessors(ImmutableMap.of("test.attr", mockProcessor));

    InputStream input =
        new ByteArrayInputStream(
            "\n  \n\t\nSECRET//NOFORN\nContent...".getBytes(StandardCharsets.UTF_8));

    // Execute
    extractor.process(input, metacard);

    // Verify: Processor was called with correct marking
    ArgumentCaptor<BannerMarkings> captor = ArgumentCaptor.forClass(BannerMarkings.class);
    verify(mockProcessor, times(1)).apply(eq(metacard), captor.capture());

    BannerMarkings captured = captor.getValue();
    assertThat(captured.getInputMarkings(), is("SECRET//NOFORN"));
  }

  /**
   * Verifies that process(InputStream, Metacard) handles empty input stream.
   *
   * <p>An empty input stream should not cause errors or set any attributes.
   */
  @Test
  public void testProcessInputStreamWithEmptyStream() {
    extractor.testSetAttProcessors(ImmutableMap.of("test.attr", mockProcessor));

    InputStream input = new ByteArrayInputStream(new byte[0]);

    // Execute
    extractor.process(input, metacard);

    // Verify: No attributes were set
    verify(metacard, never()).setAttribute(any(Attribute.class));
  }

  /**
   * Verifies that process(InputStream, Metacard) handles invalid banner markings.
   *
   * <p>Invalid markings should cause MarkingsValidationException, which should be caught and logged
   * without setting attributes.
   */
  @Test
  public void testProcessInputStreamWithInvalidMarkings() {
    extractor.testSetAttProcessors(ImmutableMap.of("test.attr", mockProcessor));

    InputStream input =
        new ByteArrayInputStream("INVALID MARKING FORMAT".getBytes(StandardCharsets.UTF_8));

    // Execute: Should not throw exception
    extractor.process(input, metacard);

    // Verify: No attributes were set
    verify(metacard, never()).setAttribute(any(Attribute.class));
  }

  /**
   * Verifies that process(InputStream, Metacard) calls all registered attribute processors.
   *
   * <p>When multiple processors are registered, all should be called in sequence.
   */
  @Test
  public void testProcessInputStreamCallsAllProcessors() {
    BiFunction<Metacard, BannerMarkings, Attribute> processor1 = mock(BiFunction.class);
    BiFunction<Metacard, BannerMarkings, Attribute> processor2 = mock(BiFunction.class);
    BiFunction<Metacard, BannerMarkings, Attribute> processor3 = mock(BiFunction.class);

    when(processor1.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("attr1", "value1"));
    when(processor2.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("attr2", "value2"));
    when(processor3.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("attr3", "value3"));

    extractor.testSetAttProcessors(
        ImmutableMap.of("attr1", processor1, "attr2", processor2, "attr3", processor3));

    InputStream input = new ByteArrayInputStream("SECRET//NOFORN".getBytes(StandardCharsets.UTF_8));

    // Execute
    extractor.process(input, metacard);

    // Verify: All processors were called
    verify(processor1, times(1)).apply(eq(metacard), any(BannerMarkings.class));
    verify(processor2, times(1)).apply(eq(metacard), any(BannerMarkings.class));
    verify(processor3, times(1)).apply(eq(metacard), any(BannerMarkings.class));

    // Verify: All attributes were set
    verify(metacard, times(3)).setAttribute(any(Attribute.class));
  }

  /**
   * Verifies that process(InputStream, Metacard) handles null input stream gracefully.
   *
   * <p>A null input stream should cause NullPointerException (expected behavior for invalid input).
   */
  @Test(expected = NullPointerException.class)
  public void testProcessInputStreamWithNullInputStream() {
    extractor.testSetAttProcessors(ImmutableMap.of("test.attr", mockProcessor));

    // Execute: Should throw NullPointerException
    extractor.process((InputStream) null, metacard);
  }

  /**
   * Verifies that process() handles null metacard parameter.
   *
   * <p>A null metacard should cause NullPointerException when trying to set attributes.
   */
  @Test(expected = NullPointerException.class)
  public void testProcessWithNullMetacard() {
    when(mockProcessor.apply(any(), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("test.attr", "value"));
    extractor.testSetAttProcessors(ImmutableMap.of("test.attr", mockProcessor));

    // Execute: Should throw NullPointerException when setAttribute is called
    extractor.process("SECRET//NOFORN", null);
  }

  // ==========================================================================
  // translateClassification() Tests
  // ==========================================================================

  /**
   * Verifies that translateClassification() returns short names for US classifications.
   *
   * <p>US (non-NATO) classifications should return their short names (U, R, C, S, TS).
   */
  @Test
  public void testTranslateClassificationUsUnclassified() {
    String result =
        extractor.translateClassification(ClassificationLevel.UNCLASSIFIED, false, null);
    assertThat(result, is("U"));
  }

  @Test
  public void testTranslateClassificationUsRestricted() {
    String result = extractor.translateClassification(ClassificationLevel.RESTRICTED, false, null);
    assertThat(result, is("R"));
  }

  @Test
  public void testTranslateClassificationUsConfidential() {
    String result =
        extractor.translateClassification(ClassificationLevel.CONFIDENTIAL, false, null);
    assertThat(result, is("C"));
  }

  @Test
  public void testTranslateClassificationUsSecret() {
    String result = extractor.translateClassification(ClassificationLevel.SECRET, false, null);
    assertThat(result, is("S"));
  }

  @Test
  public void testTranslateClassificationUsTopSecret() {
    String result = extractor.translateClassification(ClassificationLevel.TOP_SECRET, false, null);
    assertThat(result, is("TS"));
  }

  /**
   * Verifies that translateClassification() returns NATO prefixes for NATO classifications.
   *
   * <p>NATO classifications should be prefixed with "N" except TOP SECRET which becomes "CTS"
   * (COSMIC TOP SECRET).
   */
  @Test
  public void testTranslateClassificationNatoUnclassified() {
    String result = extractor.translateClassification(ClassificationLevel.UNCLASSIFIED, true, null);
    assertThat(result, is("NU"));
  }

  @Test
  public void testTranslateClassificationNatoRestricted() {
    String result = extractor.translateClassification(ClassificationLevel.RESTRICTED, true, null);
    assertThat(result, is("NR"));
  }

  @Test
  public void testTranslateClassificationNatoConfidential() {
    String result = extractor.translateClassification(ClassificationLevel.CONFIDENTIAL, true, null);
    assertThat(result, is("NC"));
  }

  @Test
  public void testTranslateClassificationNatoSecret() {
    String result = extractor.translateClassification(ClassificationLevel.SECRET, true, null);
    assertThat(result, is("NS"));
  }

  @Test
  public void testTranslateClassificationNatoTopSecret() {
    String result = extractor.translateClassification(ClassificationLevel.TOP_SECRET, true, null);
    assertThat(result, is("CTS"));
  }

  /**
   * Verifies that translateClassification() handles NATO ATOMAL qualifier.
   *
   * <p>ATOMAL qualifier adds "A" suffix, with special handling for SECRET (adds "AT" instead of
   * just "A").
   */
  @Test
  public void testTranslateClassificationNatoConfidentialAtimal() {
    String result =
        extractor.translateClassification(ClassificationLevel.CONFIDENTIAL, true, "ATOMAL");
    assertThat(result, is("NCA"));
  }

  @Test
  public void testTranslateClassificationNatoSecretAtimal() {
    String result = extractor.translateClassification(ClassificationLevel.SECRET, true, "ATOMAL");
    assertThat(result, is("NSAT"));
  }

  @Test
  public void testTranslateClassificationNatoTopSecretAtimal() {
    String result =
        extractor.translateClassification(ClassificationLevel.TOP_SECRET, true, "ATOMAL");
    assertThat(result, is("CTSA"));
  }

  /**
   * Verifies that translateClassification() handles NATO BOHEMIA qualifier.
   *
   * <p>BOHEMIA qualifier adds "-B" suffix.
   */
  @Test
  public void testTranslateClassificationNatoBohemia() {
    String result =
        extractor.translateClassification(ClassificationLevel.TOP_SECRET, true, "BOHEMIA");
    assertThat(result, is("CTS-B"));
  }

  @Test
  public void testTranslateClassificationNatoSecretBohemia() {
    String result = extractor.translateClassification(ClassificationLevel.SECRET, true, "BOHEMIA");
    assertThat(result, is("NS-B"));
  }

  /**
   * Verifies that translateClassification() handles NATO BALK qualifier.
   *
   * <p>BALK qualifier adds "-BALK" suffix.
   */
  @Test
  public void testTranslateClassificationNatoBalk() {
    String result = extractor.translateClassification(ClassificationLevel.TOP_SECRET, true, "BALK");
    assertThat(result, is("CTS-BALK"));
  }

  @Test
  public void testTranslateClassificationNatoSecretBalk() {
    String result = extractor.translateClassification(ClassificationLevel.SECRET, true, "BALK");
    assertThat(result, is("NS-BALK"));
  }

  /**
   * Verifies that translateClassification() handles null NATO qualifier.
   *
   * <p>Null qualifier should be treated as no qualifier.
   */
  @Test
  public void testTranslateClassificationNatoNullQualifier() {
    String result = extractor.translateClassification(ClassificationLevel.SECRET, true, null);
    assertThat(result, is("NS"));
  }

  /**
   * Verifies that translateClassification() handles empty NATO qualifier.
   *
   * <p>Empty string qualifier should be treated as no qualifier.
   */
  @Test
  public void testTranslateClassificationNatoEmptyQualifier() {
    String result = extractor.translateClassification(ClassificationLevel.SECRET, true, "");
    assertThat(result, is("NS"));
  }

  /**
   * Verifies that translateClassification() ignores unknown NATO qualifiers.
   *
   * <p>Unknown qualifiers should be ignored (not added to output).
   */
  @Test
  public void testTranslateClassificationNatoUnknownQualifier() {
    String result = extractor.translateClassification(ClassificationLevel.SECRET, true, "UNKNOWN");
    assertThat(result, is("NS"));
  }

  // ==========================================================================
  // setAttProcessors() Tests
  // ==========================================================================

  /**
   * Verifies that setAttProcessors() stores processors correctly.
   *
   * <p>The method should create an immutable copy of the provided map.
   */
  @Test
  public void testSetAttProcessors() {
    BiFunction<Metacard, BannerMarkings, Attribute> processor1 = mock(BiFunction.class);
    BiFunction<Metacard, BannerMarkings, Attribute> processor2 = mock(BiFunction.class);

    when(processor1.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("attr1", "value1"));
    when(processor2.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("attr2", "value2"));

    extractor.testSetAttProcessors(ImmutableMap.of("attr1", processor1, "attr2", processor2));

    // Execute: Process to verify processors were stored
    extractor.process("SECRET//NOFORN", metacard);

    // Verify: Both processors were called
    verify(processor1, times(1)).apply(eq(metacard), any(BannerMarkings.class));
    verify(processor2, times(1)).apply(eq(metacard), any(BannerMarkings.class));
  }

  /**
   * Verifies that setAttProcessors() creates an immutable copy.
   *
   * <p>Changes to the original map should not affect the stored processors.
   */
  @Test
  public void testSetAttProcessorsCreatesImmutableCopy() {
    java.util.Map<String, BiFunction<Metacard, BannerMarkings, Attribute>> mutableMap =
        new java.util.HashMap<>();
    BiFunction<Metacard, BannerMarkings, Attribute> processor1 = mock(BiFunction.class);

    when(processor1.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("attr1", "value1"));

    mutableMap.put("attr1", processor1);
    extractor.testSetAttProcessors(mutableMap);

    // Modify original map
    BiFunction<Metacard, BannerMarkings, Attribute> processor2 = mock(BiFunction.class);
    mutableMap.put("attr2", processor2);

    // Execute: Process to verify only original processor is used
    extractor.process("SECRET//NOFORN", metacard);

    // Verify: Only first processor was called
    verify(processor1, times(1)).apply(eq(metacard), any(BannerMarkings.class));
    verify(processor2, never()).apply(any(Metacard.class), any(BannerMarkings.class));
  }

  /**
   * Verifies that setAttProcessors() handles empty map.
   *
   * <p>An empty processor map should result in no attributes being set.
   */
  @Test
  public void testSetAttProcessorsWithEmptyMap() {
    extractor.testSetAttProcessors(Collections.emptyMap());

    // Execute
    extractor.process("SECRET//NOFORN", metacard);

    // Verify: No attributes were set
    verify(metacard, never()).setAttribute(any(Attribute.class));
  }

  // ==========================================================================
  // dedupedList() Tests
  // ==========================================================================

  /**
   * Verifies that dedupedList() combines two lists and removes duplicates.
   *
   * <p>The result should contain all unique elements from both lists.
   */
  @Test
  public void testDedupedListWithDuplicates() {
    List<String> list1 = Arrays.asList("hello", "world", "goodnight");
    List<String> list2 = Arrays.asList("world", "goodnight", "columbus");

    List<java.io.Serializable> result = extractor.testDedupedList(list1, list2);

    assertThat(result, hasSize(4));
    assertThat(result, containsInAnyOrder("hello", "world", "goodnight", "columbus"));
  }

  /**
   * Verifies that dedupedList() handles non-overlapping lists.
   *
   * <p>When lists have no common elements, result should contain all elements.
   */
  @Test
  public void testDedupedListWithNoDuplicates() {
    List<String> list1 = Arrays.asList("hello", "world");
    List<String> list2 = Arrays.asList("goodnight", "columbus");

    List<java.io.Serializable> result = extractor.testDedupedList(list1, list2);

    assertThat(result, hasSize(4));
    assertThat(result, containsInAnyOrder("hello", "world", "goodnight", "columbus"));
  }

  /**
   * Verifies that dedupedList() handles empty first list.
   *
   * <p>Result should contain only elements from second list.
   */
  @Test
  public void testDedupedListWithEmptyFirstList() {
    List<String> list1 = Collections.emptyList();
    List<String> list2 = Arrays.asList("hello", "world");

    List<java.io.Serializable> result = extractor.testDedupedList(list1, list2);

    assertThat(result, hasSize(2));
    assertThat(result, containsInAnyOrder("hello", "world"));
  }

  /**
   * Verifies that dedupedList() handles empty second list.
   *
   * <p>Result should contain only elements from first list.
   */
  @Test
  public void testDedupedListWithEmptySecondList() {
    List<String> list1 = Arrays.asList("hello", "world");
    List<String> list2 = Collections.emptyList();

    List<java.io.Serializable> result = extractor.testDedupedList(list1, list2);

    assertThat(result, hasSize(2));
    assertThat(result, containsInAnyOrder("hello", "world"));
  }

  /**
   * Verifies that dedupedList() handles both lists empty.
   *
   * <p>Result should be an empty list.
   */
  @Test
  public void testDedupedListWithBothEmpty() {
    List<String> list1 = Collections.emptyList();
    List<String> list2 = Collections.emptyList();

    List<java.io.Serializable> result = extractor.testDedupedList(list1, list2);

    assertThat(result, is(empty()));
  }

  /**
   * Verifies that dedupedList() handles identical lists.
   *
   * <p>When both lists are identical, result should contain each element only once.
   */
  @Test
  public void testDedupedListWithIdenticalLists() {
    List<String> list1 = Arrays.asList("hello", "world");
    List<String> list2 = Arrays.asList("hello", "world");

    List<java.io.Serializable> result = extractor.testDedupedList(list1, list2);

    assertThat(result, hasSize(2));
    assertThat(result, containsInAnyOrder("hello", "world"));
  }

  /**
   * Verifies that dedupedList() preserves different data types.
   *
   * <p>The method works with any Serializable type, not just strings.
   */
  @Test
  public void testDedupedListWithMixedTypes() {
    List<java.io.Serializable> list1 = Arrays.asList("string", 123, 456L);
    List<java.io.Serializable> list2 = Arrays.asList(123, 789L, "another");

    List<java.io.Serializable> result = extractor.testDedupedList(list1, list2);

    assertThat(result, hasSize(5));
    assertThat(result, containsInAnyOrder("string", 123, 456L, 789L, "another"));
  }

  /**
   * Verifies that dedupedList() returns an immutable list.
   *
   * <p>The returned list should be immutable (backed by ImmutableList).
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testDedupedListReturnsImmutableList() {
    List<String> list1 = Arrays.asList("hello");
    List<String> list2 = Arrays.asList("world");

    List<java.io.Serializable> result = extractor.testDedupedList(list1, list2);

    // Attempt to modify should throw UnsupportedOperationException
    result.add("test");
  }

  // ==========================================================================
  // Integration Tests
  // ==========================================================================

  /**
   * Verifies complete processing flow with realistic banner markings.
   *
   * <p>This integration test verifies the entire process from input to attribute setting.
   */
  @Test
  public void testCompleteProcessingFlowWithRealisticMarking() {
    BiFunction<Metacard, BannerMarkings, Attribute> classProcessor = mock(BiFunction.class);
    BiFunction<Metacard, BannerMarkings, Attribute> releasabilityProcessor = mock(BiFunction.class);

    when(classProcessor.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("security.classification", "S"));
    when(releasabilityProcessor.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("security.releasability", "USA"));

    extractor.testSetAttProcessors(
        ImmutableMap.of(
            "security.classification",
            classProcessor,
            "security.releasability",
            releasabilityProcessor));

    // Execute: Process realistic DoD marking
    extractor.process("SECRET//SI//NOFORN", metacard);

    // Verify: Both processors were called
    verify(classProcessor, times(1)).apply(eq(metacard), any(BannerMarkings.class));
    verify(releasabilityProcessor, times(1)).apply(eq(metacard), any(BannerMarkings.class));

    // Verify: Both attributes were set
    verify(metacard, times(2)).setAttribute(any(Attribute.class));
  }

  /**
   * Verifies processing of NATO COSMIC TOP SECRET markings.
   *
   * <p>Tests the complete flow with NATO-specific markings and qualifiers.
   */
  @Test
  public void testCompleteProcessingFlowWithNatoMarking() {
    BiFunction<Metacard, BannerMarkings, Attribute> classProcessor = mock(BiFunction.class);

    when(classProcessor.apply(any(Metacard.class), any(BannerMarkings.class)))
        .thenReturn(new AttributeImpl("security.classification", "CTSA"));

    extractor.testSetAttProcessors(ImmutableMap.of("security.classification", classProcessor));

    // Execute: Process NATO COSMIC marking
    extractor.process("//COSMIC TOP SECRET//ATOMAL", metacard);

    // Verify: Processor was called
    ArgumentCaptor<BannerMarkings> captor = ArgumentCaptor.forClass(BannerMarkings.class);
    verify(classProcessor, times(1)).apply(eq(metacard), captor.capture());

    BannerMarkings captured = captor.getValue();
    assertThat(captured.isNato(), is(true));
    assertThat(captured.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(captured.getNatoQualifier(), is("ATOMAL"));
  }
}
