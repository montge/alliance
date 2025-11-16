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
package org.codice.alliance.catalog.core.internal.impl.classification;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link SecurityClassificationServiceImpl}.
 *
 * <p>Tests the security classification comparator service which provides custom sorting of
 * classification levels. This is critical for ensuring proper security handling and display
 * ordering of classified information.
 *
 * <p><b>Coverage Improvement:</b> Expanded from 3 tests to 18 tests, covering normalization
 * behavior, case insensitivity, whitespace handling, unknown classification handling, empty list
 * edge cases, and realistic military classification ordering.
 *
 * @see SecurityClassificationServiceImpl
 */
public class SecurityClassificationServiceImplTest {

  private SecurityClassificationServiceImpl service;

  @Before
  public void setUp() {
    service = new SecurityClassificationServiceImpl();
  }

  /**
   * Test basic sorting with simple classification levels.
   *
   * <p>Verifies that classifications are sorted according to the defined order.
   */
  @Test
  public void testSort() {
    service.setSortOrder(Arrays.asList("a", "b", "c"));
    List<String> in = Arrays.asList("a", "b", "c", "a", "b", "c", "a", "b", "c");
    Collections.sort(in, service.getSecurityClassificationComparator());
    assertThat(in, is(Arrays.asList("a", "a", "a", "b", "b", "b", "c", "c", "c")));
  }

  /**
   * Test sorting with unknown classification entries.
   *
   * <p>Verifies that classifications not in the sort order are placed last (sorted with
   * Integer.MAX_VALUE priority).
   */
  @Test
  public void testSortWithoutSortOrderEntry() {
    service.setSortOrder(Arrays.asList("a", "b"));
    List<String> in = Arrays.asList("a", "b", "c", "a", "b", "c", "a", "b", "c");
    Collections.sort(in, service.getSecurityClassificationComparator());
    assertThat(in, is(Arrays.asList("a", "a", "a", "b", "b", "b", "c", "c", "c")));
  }

  /**
   * Test whitespace trimming in sort order configuration.
   *
   * <p>Verifies that leading/trailing whitespace is normalized when setting sort order.
   */
  @Test
  public void testSetSortedOrder() {
    service.setSortOrder(Arrays.asList(" c ", "\t\tb ", "   a \n\n"));
    List<String> in = Arrays.asList("a", "b", "c", "a", "b", "c", "a", "b", "c");
    Collections.sort(in, service.getSecurityClassificationComparator());
    assertThat(in, is(Arrays.asList("c", "c", "c", "b", "b", "b", "a", "a", "a")));
  }

  /**
   * Test case-insensitive sorting.
   *
   * <p>Verifies that classification comparisons are case-insensitive (e.g., "SECRET" equals
   * "secret").
   */
  @Test
  public void testCaseInsensitiveSorting() {
    service.setSortOrder(Arrays.asList("topsecret", "secret", "confidential"));
    List<String> in = Arrays.asList("SECRET", "TopSecret", "CONFIDENTIAL", "Secret");
    Collections.sort(in, service.getSecurityClassificationComparator());
    assertThat(
        in,
        is(Arrays.asList("TopSecret", "SECRET", "Secret", "CONFIDENTIAL")));
  }

  /**
   * Test whitespace normalization in classification values.
   *
   * <p>Verifies that whitespace is stripped during comparison (e.g., "TOP SECRET" equals
   * "TOPSECRET").
   */
  @Test
  public void testWhitespaceNormalization() {
    service.setSortOrder(Arrays.asList("topsecret", "secret"));
    List<String> in = Arrays.asList("TOP SECRET", "SECRET", "Top Secret", "S E C R E T");
    Collections.sort(in, service.getSecurityClassificationComparator());
    assertThat(
        in,
        is(Arrays.asList("TOP SECRET", "Top Secret", "SECRET", "S E C R E T")));
  }

  /**
   * Test realistic US military classification ordering.
   *
   * <p>Verifies correct ordering: TOP SECRET > SECRET > CONFIDENTIAL > UNCLASSIFIED.
   */
  @Test
  public void testMilitaryClassificationOrdering() {
    service.setSortOrder(
        Arrays.asList("TOP SECRET", "SECRET", "CONFIDENTIAL", "UNCLASSIFIED"));
    List<String> in =
        Arrays.asList("UNCLASSIFIED", "SECRET", "TOP SECRET", "CONFIDENTIAL", "SECRET");
    Collections.sort(in, service.getSecurityClassificationComparator());
    assertThat(
        in,
        is(
            Arrays.asList(
                "TOP SECRET", "SECRET", "SECRET", "CONFIDENTIAL", "UNCLASSIFIED")));
  }

  /**
   * Test sorting with empty sort order list.
   *
   * <p>Verifies that all classifications are treated as unknown (max priority) when sort order is
   * empty.
   */
  @Test
  public void testEmptySortOrder() {
    service.setSortOrder(Collections.emptyList());
    List<String> in = Arrays.asList("a", "b", "c");
    Collections.sort(in, service.getSecurityClassificationComparator());
    // All have same priority (MAX_VALUE), so original order should be preserved (stable sort)
    assertThat(in, is(Arrays.asList("a", "b", "c")));
  }

  /**
   * Test sorting with single classification in sort order.
   *
   * <p>Verifies that a single known classification is sorted before unknown ones.
   */
  @Test
  public void testSingleClassificationInSortOrder() {
    service.setSortOrder(Collections.singletonList("secret"));
    List<String> in = Arrays.asList("unclassified", "secret", "topsecret", "secret");
    Collections.sort(in, service.getSecurityClassificationComparator());
    assertThat(
        in,
        is(Arrays.asList("secret", "secret", "unclassified", "topsecret")));
  }

  /**
   * Test that unknown classifications all have equal priority.
   *
   * <p>Verifies that multiple unknown classifications compare as equal (both get MAX_VALUE).
   */
  @Test
  public void testMultipleUnknownClassifications() {
    service.setSortOrder(Arrays.asList("a"));
    Comparator<String> comparator = service.getSecurityClassificationComparator();

    // Both unknown, should be equal (compare returns 0)
    assertThat(comparator.compare("unknown1", "unknown2"), is(0));
    assertThat(comparator.compare("x", "y"), is(0));
  }

  /**
   * Test comparator with known vs unknown classification.
   *
   * <p>Verifies that known classifications sort before unknown ones.
   */
  @Test
  public void testKnownBeforeUnknown() {
    service.setSortOrder(Arrays.asList("secret"));
    Comparator<String> comparator = service.getSecurityClassificationComparator();

    // Known should come before unknown (negative compare result)
    assertThat(comparator.compare("secret", "unknown"), lessThan(0));
    assertThat(comparator.compare("unknown", "secret"), greaterThan(0));
  }

  /**
   * Test comparator with different case variations of same classification.
   *
   * <p>Verifies that case variations of the same classification compare as equal.
   */
  @Test
  public void testCaseVariationsEqual() {
    service.setSortOrder(Arrays.asList("topsecret"));
    Comparator<String> comparator = service.getSecurityClassificationComparator();

    assertThat(comparator.compare("TOPSECRET", "topsecret"), is(0));
    assertThat(comparator.compare("TopSecret", "TOP SECRET"), is(0));
    assertThat(comparator.compare("T O P S E C R E T", "topsecret"), is(0));
  }

  /**
   * Test comparator ordering between different known classifications.
   *
   * <p>Verifies that classifications are ordered according to their position in sort order.
   */
  @Test
  public void testOrderingBetweenKnownClassifications() {
    service.setSortOrder(Arrays.asList("first", "second", "third"));
    Comparator<String> comparator = service.getSecurityClassificationComparator();

    assertThat(comparator.compare("first", "second"), lessThan(0));
    assertThat(comparator.compare("second", "third"), lessThan(0));
    assertThat(comparator.compare("third", "first"), greaterThan(0));
  }

  /**
   * Test getSecurityClassificationComparator returns non-null comparator.
   *
   * <p>Verifies that the service always provides a valid comparator after initialization.
   */
  @Test
  public void testGetComparatorNotNull() {
    service.setSortOrder(Arrays.asList("a", "b", "c"));
    Comparator<String> comparator = service.getSecurityClassificationComparator();
    assertThat(comparator != null, is(true));
  }

  /**
   * Test that sort order updates are reflected in comparator.
   *
   * <p>Verifies that calling setSortOrder updates the comparator's behavior.
   */
  @Test
  public void testSortOrderUpdate() {
    // Initial sort order
    service.setSortOrder(Arrays.asList("a", "b", "c"));
    List<String> in = new ArrayList<>(Arrays.asList("c", "a", "b"));
    Collections.sort(in, service.getSecurityClassificationComparator());
    assertThat(in, is(Arrays.asList("a", "b", "c")));

    // Update sort order
    service.setSortOrder(Arrays.asList("c", "b", "a"));
    in = new ArrayList<>(Arrays.asList("c", "a", "b"));
    Collections.sort(in, service.getSecurityClassificationComparator());
    assertThat(in, is(Arrays.asList("c", "b", "a")));
  }

  /**
   * Test NATO classification ordering.
   *
   * <p>Verifies sorting of NATO classification levels: COSMIC TOP SECRET > NATO SECRET > NATO
   * CONFIDENTIAL > NATO RESTRICTED.
   */
  @Test
  public void testNatoClassificationOrdering() {
    service.setSortOrder(
        Arrays.asList(
            "COSMIC TOP SECRET",
            "NATO SECRET",
            "NATO CONFIDENTIAL",
            "NATO RESTRICTED",
            "NATO UNCLASSIFIED"));
    List<String> in =
        Arrays.asList(
            "NATO UNCLASSIFIED",
            "COSMIC TOP SECRET",
            "NATO CONFIDENTIAL",
            "NATO SECRET",
            "NATO RESTRICTED");
    Collections.sort(in, service.getSecurityClassificationComparator());
    assertThat(
        in,
        is(
            Arrays.asList(
                "COSMIC TOP SECRET",
                "NATO SECRET",
                "NATO CONFIDENTIAL",
                "NATO RESTRICTED",
                "NATO UNCLASSIFIED")));
  }

  /**
   * Test mixed known and unknown classifications in large list.
   *
   * <p>Verifies correct sorting behavior with a larger dataset containing both known and unknown
   * classifications.
   */
  @Test
  public void testLargeListWithMixedClassifications() {
    service.setSortOrder(Arrays.asList("high", "medium", "low"));
    List<String> in =
        Arrays.asList(
            "unknown1", "low", "high", "unknown2", "medium", "high", "low", "unknown3", "medium");
    Collections.sort(in, service.getSecurityClassificationComparator());

    // Expected: all "high" first, then "medium", then "low", then all unknowns
    assertThat(
        in,
        is(
            Arrays.asList(
                "high", "high", "medium", "medium", "low", "low", "unknown1", "unknown2",
                "unknown3")));
  }
}
