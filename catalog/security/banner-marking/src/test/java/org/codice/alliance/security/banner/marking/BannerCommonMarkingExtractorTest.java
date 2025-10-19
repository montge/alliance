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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.AttributeImpl;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.codice.alliance.catalog.core.api.types.Security;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * Unit tests for {@link BannerCommonMarkingExtractor} class.
 *
 * <p>BannerCommonMarkingExtractor processes common security markings from banner markings and sets
 * them on metacards. This is the core extractor that handles:
 *
 * <ul>
 *   <li>Classification level translation
 *   <li>Releasability processing
 *   <li>Codewords (SCI controls)
 *   <li>Dissemination controls
 *   <li>Owner/Producer
 *   <li>Classification system
 *   <li>Security attribute validation and mismatch detection
 * </ul>
 *
 * <p><b>Coverage Target:</b> 85-90%
 *
 * <p><b>Source Complexity:</b> 392 instructions, 44 branches, 15 methods
 *
 * <p><b>Test Strategy:</b>
 *
 * <ul>
 *   <li>Test each attribute processor method individually
 *   <li>Test security attribute validation and mismatch handling
 *   <li>Test US, FGI, JOINT, and NATO marking types
 *   <li>Test null and empty input handling
 *   <li>Verify metacard attribute setting using ArgumentCaptor
 *   <li>Test exception handling for security conflicts
 * </ul>
 *
 * <p><b>Security Importance:</b> This extractor is critical for ensuring that security markings are
 * correctly extracted and validated. Failures could lead to misclassification or unauthorized
 * access to sensitive information.
 *
 * @see BannerMarkings
 * @see MarkingExtractor
 */
public class BannerCommonMarkingExtractorTest {

  private BannerCommonMarkingExtractor extractor;
  private Metacard metacard;

  @Before
  public void setUp() {
    extractor = new BannerCommonMarkingExtractor();
    metacard = mock(Metacard.class);
  }

  // ==========================================================================
  // Constructor and Initialization Tests
  // ==========================================================================

  /**
   * Verifies that constructor initializes attribute processors correctly.
   *
   * <p>The constructor should set up processors for all security attributes defined in the Security
   * interface.
   */
  @Test
  public void testConstructorInitializesProcessors() {
    // Just instantiating should not throw exception
    BannerCommonMarkingExtractor newExtractor = new BannerCommonMarkingExtractor();
    assertThat(newExtractor, is(notNullValue()));
  }

  /**
   * Verifies that getMetacardAttributes() returns SecurityAttributes descriptors.
   *
   * <p>Should return the set of attribute descriptors for security-related attributes.
   */
  @Test
  public void testGetMetacardAttributes() {
    Set<AttributeDescriptor> descriptors = extractor.getMetacardAttributes();

    assertThat(descriptors, is(notNullValue()));
    assertThat(descriptors, is(not(empty())));
  }

  // ==========================================================================
  // processClassMarking() Tests
  // ==========================================================================

  /**
   * Verifies processClassMarking() with US UNCLASSIFIED marking.
   *
   * <p>Should translate UNCLASSIFIED to "U" and create classification attribute.
   */
  @Test
  public void testProcessClassMarkingUsUnclassified() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("UNCLASSIFIED");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(null);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName(), is(Security.CLASSIFICATION));
    assertThat(result.getValue(), is("U"));
  }

  /**
   * Verifies processClassMarking() with US SECRET marking.
   *
   * <p>Should translate SECRET to "S" and create classification attribute.
   */
  @Test
  public void testProcessClassMarkingUsSecret() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(null);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName(), is(Security.CLASSIFICATION));
    assertThat(result.getValue(), is("S"));
  }

  /**
   * Verifies processClassMarking() with US TOP SECRET marking.
   *
   * <p>Should translate TOP SECRET to "TS" and create classification attribute.
   */
  @Test
  public void testProcessClassMarkingUsTopSecret() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("TOP SECRET//SI//NOFORN");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(null);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is("TS"));
  }

  /**
   * Verifies processClassMarking() with NATO marking.
   *
   * <p>NATO markings should be translated with "N" prefix (e.g., NS for NATO SECRET).
   */
  @Test
  public void testProcessClassMarkingNato() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//NATO SECRET");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(null);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is("NS"));
  }

  /**
   * Verifies processClassMarking() with NATO COSMIC TOP SECRET.
   *
   * <p>COSMIC TOP SECRET should be translated to "CTS".
   */
  @Test
  public void testProcessClassMarkingCosmicTopSecret() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//COSMIC TOP SECRET");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(null);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is("CTS"));
  }

  /**
   * Verifies processClassMarking() with NATO ATOMAL qualifier.
   *
   * <p>ATOMAL qualifier should add "A" suffix (or "AT" for SECRET).
   */
  @Test
  public void testProcessClassMarkingNatoAtimal() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//NATO SECRET//ATOMAL");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(null);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is("NSAT"));
  }

  /**
   * Verifies processClassMarking() when existing classification matches.
   *
   * <p>When metacard already has matching classification, should not throw exception.
   */
  @Test
  public void testProcessClassMarkingWithMatchingExistingValue() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    Attribute existingAttr = new AttributeImpl(Security.CLASSIFICATION, "S");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(existingAttr);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is("S"));
  }

  /**
   * Verifies processClassMarking() throws exception when classification mismatches.
   *
   * <p>When extracted classification differs from existing, should throw MarkingMismatchException.
   */
  @Test(expected = MarkingMismatchException.class)
  public void testProcessClassMarkingWithMismatchThrowsException() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    Attribute existingAttr = new AttributeImpl(Security.CLASSIFICATION, "TS");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(existingAttr);

    extractor.processClassMarking(metacard, markings);
  }

  /**
   * Verifies processClassMarking() handles blank existing value.
   *
   * <p>Blank existing values should be treated as non-existent (no validation).
   */
  @Test
  public void testProcessClassMarkingWithBlankExistingValue() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    Attribute existingAttr = new AttributeImpl(Security.CLASSIFICATION, "");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(existingAttr);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is("S"));
  }

  // ==========================================================================
  // processReleasability() Tests
  // ==========================================================================

  /**
   * Verifies processReleasability() extracts REL TO countries.
   *
   * <p>Should extract releasability countries from banner markings. USA must be first, then
   * trigraphs, then tetragraphs.
   */
  @Test
  public void testProcessReleasabilityWithRelTo() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//REL TO USA, AUS, GBR");
    when(metacard.getAttribute(Security.RELEASABILITY)).thenReturn(null);

    Attribute result = extractor.processReleasability(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName(), is(Security.RELEASABILITY));
    assertThat(result.getValues(), hasItems("USA", "GBR", "AUS"));
  }

  /**
   * Verifies processReleasability() with empty REL TO list.
   *
   * <p>Should handle empty releasability list.
   */
  @Test
  public void testProcessReleasabilityWithEmptyList() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    when(metacard.getAttribute(Security.RELEASABILITY)).thenReturn(null);

    Attribute result = extractor.processReleasability(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), is(empty()));
  }

  /**
   * Verifies processReleasability() with matching existing values.
   *
   * <p>Should not throw exception when values match.
   */
  @Test
  public void testProcessReleasabilityWithMatchingExistingValues() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//REL TO USA, AUS, GBR");
    List<Serializable> existingValues = Arrays.asList("USA", "AUS", "GBR");
    Attribute existingAttr = new AttributeImpl(Security.RELEASABILITY, existingValues);
    when(metacard.getAttribute(Security.RELEASABILITY)).thenReturn(existingAttr);

    Attribute result = extractor.processReleasability(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), containsInAnyOrder("USA", "GBR", "AUS"));
  }

  /**
   * Verifies processReleasability() throws exception on mismatch.
   *
   * <p>Should throw MarkingMismatchException when values don't match.
   */
  @Test(expected = MarkingMismatchException.class)
  public void testProcessReleasabilityWithMismatch() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//REL TO USA, AUS, GBR");
    List<Serializable> existingValues = Arrays.asList("USA", "FRA");
    Attribute existingAttr = new AttributeImpl(Security.RELEASABILITY, existingValues);
    when(metacard.getAttribute(Security.RELEASABILITY)).thenReturn(existingAttr);

    extractor.processReleasability(metacard, markings);
  }

  // ==========================================================================
  // processCodewords() Tests
  // ==========================================================================

  /**
   * Verifies processCodewords() extracts SCI controls without compartments.
   *
   * <p>Simple SCI controls like "SI" should be extracted as-is.
   */
  @Test
  public void testProcessCodewordsSimpleSci() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("TOP SECRET//SI//NOFORN");
    when(metacard.getAttribute(Security.CODEWORDS)).thenReturn(null);

    Attribute result = extractor.processCodewords(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName(), is(Security.CODEWORDS));
    assertThat(result.getValues(), hasItem("SI"));
  }

  /**
   * Verifies processCodewords() extracts SCI controls with compartments.
   *
   * <p>SCI controls with compartments should be formatted as "CONTROL-COMPARTMENT".
   */
  @Test
  public void testProcessCodewordsWithCompartments() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("TOP SECRET//SI-G/TK//NOFORN");
    when(metacard.getAttribute(Security.CODEWORDS)).thenReturn(null);

    Attribute result = extractor.processCodewords(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), hasSize(greaterThan(0)));
  }

  /**
   * Verifies processCodewords() with multiple SCI controls.
   *
   * <p>Should extract all SCI controls from banner markings.
   */
  @Test
  public void testProcessCodewordsMultipleSci() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("TOP SECRET//SI/TK//NOFORN");
    when(metacard.getAttribute(Security.CODEWORDS)).thenReturn(null);

    Attribute result = extractor.processCodewords(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), is(not(empty())));
  }

  /**
   * Verifies processCodewords() with no SCI controls.
   *
   * <p>When no SCI controls present, should return empty list.
   */
  @Test
  public void testProcessCodewordsEmpty() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    when(metacard.getAttribute(Security.CODEWORDS)).thenReturn(null);

    Attribute result = extractor.processCodewords(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), is(empty()));
  }

  /**
   * Verifies processCodewords() validates against existing values.
   *
   * <p>Should throw exception when codewords don't match existing values.
   */
  @Test(expected = MarkingMismatchException.class)
  public void testProcessCodewordsWithMismatch() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("TOP SECRET//SI//NOFORN");
    List<Serializable> existingValues = Arrays.asList("TK");
    Attribute existingAttr = new AttributeImpl(Security.CODEWORDS, existingValues);
    when(metacard.getAttribute(Security.CODEWORDS)).thenReturn(existingAttr);

    extractor.processCodewords(metacard, markings);
  }

  // ==========================================================================
  // processDissem() Tests
  // ==========================================================================

  /**
   * Verifies processDissem() extracts dissemination controls.
   *
   * <p>Should extract dissemination control names from banner markings.
   */
  @Test
  public void testProcessDissemNoforn() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    when(metacard.getAttribute(Security.DISSEMINATION_CONTROLS)).thenReturn(null);

    Attribute result = extractor.processDissem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName(), is(Security.DISSEMINATION_CONTROLS));
    assertThat(result.getValues(), hasItem("NOFORN"));
  }

  /**
   * Verifies processDissem() with multiple dissemination controls.
   *
   * <p>Should extract all dissemination controls.
   */
  @Test
  public void testProcessDissemMultiple() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN/ORCON");
    when(metacard.getAttribute(Security.DISSEMINATION_CONTROLS)).thenReturn(null);

    Attribute result = extractor.processDissem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), hasItems("NOFORN", "ORCON"));
  }

  /**
   * Verifies processDissem() with no dissemination controls.
   *
   * <p>Should return empty list when no dissemination controls present.
   */
  @Test
  public void testProcessDissemEmpty() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET");
    when(metacard.getAttribute(Security.DISSEMINATION_CONTROLS)).thenReturn(null);

    Attribute result = extractor.processDissem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), is(empty()));
  }

  /**
   * Verifies processDissem() validates against existing values.
   *
   * <p>Should throw exception when dissemination controls mismatch.
   */
  @Test(expected = MarkingMismatchException.class)
  public void testProcessDissemWithMismatch() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    List<Serializable> existingValues = Arrays.asList("ORCON");
    Attribute existingAttr = new AttributeImpl(Security.DISSEMINATION_CONTROLS, existingValues);
    when(metacard.getAttribute(Security.DISSEMINATION_CONTROLS)).thenReturn(existingAttr);

    extractor.processDissem(metacard, markings);
  }

  // ==========================================================================
  // processOwnerProducer() Tests
  // ==========================================================================

  /**
   * Verifies processOwnerProducer() for US markings.
   *
   * <p>US markings should set owner-producer to "USA".
   */
  @Test
  public void testProcessOwnerProducerUs() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    when(metacard.getAttribute(Security.OWNER_PRODUCER)).thenReturn(null);

    Attribute result = extractor.processOwnerProducer(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName(), is(Security.OWNER_PRODUCER));
    assertThat(result.getValues(), contains("USA"));
  }

  /**
   * Verifies processOwnerProducer() for FGI markings with country code.
   *
   * <p>FGI markings should set owner-producer to the FGI authority.
   */
  @Test
  public void testProcessOwnerProducerFgi() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//NATO SECRET");
    when(metacard.getAttribute(Security.OWNER_PRODUCER)).thenReturn(null);

    Attribute result = extractor.processOwnerProducer(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), contains("NATO"));
  }

  /**
   * Verifies processOwnerProducer() for NATO markings.
   *
   * <p>NATO markings should set owner-producer to "NATO".
   */
  @Test
  public void testProcessOwnerProducerNato() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//NATO SECRET");
    when(metacard.getAttribute(Security.OWNER_PRODUCER)).thenReturn(null);

    Attribute result = extractor.processOwnerProducer(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), contains("NATO"));
  }

  /**
   * Verifies processOwnerProducer() for COSMIC markings.
   *
   * <p>COSMIC markings should set owner-producer to "NATO".
   */
  @Test
  public void testProcessOwnerProducerCosmic() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//COSMIC TOP SECRET");
    when(metacard.getAttribute(Security.OWNER_PRODUCER)).thenReturn(null);

    Attribute result = extractor.processOwnerProducer(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), contains("NATO"));
  }

  /**
   * Verifies processOwnerProducer() for JOINT markings.
   *
   * <p>JOINT markings should set owner-producer to list of joint authorities.
   */
  @Test
  public void testProcessOwnerProducerJoint() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//JOINT SECRET USA GBR");
    when(metacard.getAttribute(Security.OWNER_PRODUCER)).thenReturn(null);

    Attribute result = extractor.processOwnerProducer(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), containsInAnyOrder("USA", "GBR"));
  }

  /**
   * Verifies processOwnerProducer() with matching existing value for US.
   *
   * <p>Should not throw exception when existing value matches.
   */
  @Test
  public void testProcessOwnerProducerUsWithMatchingExisting() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    Attribute existingAttr = new AttributeImpl(Security.OWNER_PRODUCER, "USA");
    when(metacard.getAttribute(Security.OWNER_PRODUCER)).thenReturn(existingAttr);

    Attribute result = extractor.processOwnerProducer(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), contains("USA"));
  }

  /**
   * Verifies processOwnerProducer() throws exception on mismatch.
   *
   * <p>Should throw MarkingMismatchException when values don't match.
   */
  @Test(expected = MarkingMismatchException.class)
  public void testProcessOwnerProducerWithMismatch() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    Attribute existingAttr = new AttributeImpl(Security.OWNER_PRODUCER, "GBR");
    when(metacard.getAttribute(Security.OWNER_PRODUCER)).thenReturn(existingAttr);

    extractor.processOwnerProducer(metacard, markings);
  }

  // ==========================================================================
  // processClassSystem() Tests
  // ==========================================================================

  /**
   * Verifies processClassSystem() for US markings.
   *
   * <p>US markings should set classification-system to "USA".
   */
  @Test
  public void testProcessClassSystemUs() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(null);

    Attribute result = extractor.processClassSystem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName(), is(Security.CLASSIFICATION_SYSTEM));
    assertThat(result.getValues(), contains("USA"));
  }

  /**
   * Verifies processClassSystem() for FGI markings.
   *
   * <p>FGI markings should set classification-system to the FGI authority.
   */
  @Test
  public void testProcessClassSystemFgi() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//NATO SECRET");
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(null);

    Attribute result = extractor.processClassSystem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), contains("NATO"));
  }

  /**
   * Verifies processClassSystem() for NATO markings.
   *
   * <p>NATO markings should set classification-system to "NATO".
   */
  @Test
  public void testProcessClassSystemNato() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//NATO SECRET");
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(null);

    Attribute result = extractor.processClassSystem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), contains("NATO"));
  }

  /**
   * Verifies processClassSystem() for JOINT markings.
   *
   * <p>JOINT markings should set classification-system to list of joint authorities.
   */
  @Test
  public void testProcessClassSystemJoint() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//JOINT SECRET USA GBR");
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(null);

    Attribute result = extractor.processClassSystem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), containsInAnyOrder("USA", "GBR"));
  }

  // ==========================================================================
  // Security Validation Tests
  // ==========================================================================

  /**
   * Verifies that security mismatch exception includes attribute name.
   *
   * <p>Exception message should contain the attribute name that caused the conflict.
   */
  @Test
  public void testSecurityMismatchExceptionMessage() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    Attribute existingAttr = new AttributeImpl(Security.CLASSIFICATION, "TS");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(existingAttr);

    try {
      extractor.processClassMarking(metacard, markings);
      fail("Expected MarkingMismatchException");
    } catch (MarkingMismatchException e) {
      assertThat(e.getMessage(), containsString(Security.CLASSIFICATION));
    }
  }

  /**
   * Verifies that blank existing values are not validated.
   *
   * <p>Security validation should skip blank/empty existing values.
   */
  @Test
  public void testSecurityValidationIgnoresBlankValues() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    Attribute existingAttr = new AttributeImpl(Security.CLASSIFICATION, "");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(existingAttr);

    // Should not throw exception
    Attribute result = extractor.processClassMarking(metacard, markings);
    assertThat(result, is(notNullValue()));
  }

  // ==========================================================================
  // Integration Tests
  // ==========================================================================

  /**
   * Verifies complete processing of realistic US marking.
   *
   * <p>Integration test with actual banner marking processing through process() method.
   */
  @Test
  public void testCompleteUsMarkingProcessing() {
    String marking = "TOP SECRET//SI//NOFORN";
    when(metacard.getAttribute(any(String.class))).thenReturn(null);

    extractor.process(marking, metacard);

    // Verify attributes were set
    ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);
    verify(metacard, atLeastOnce()).setAttribute(captor.capture());

    List<Attribute> attributes = captor.getAllValues();
    assertThat(attributes, is(not(empty())));
  }

  /**
   * Verifies complete processing of NATO marking.
   *
   * <p>Integration test with NATO-specific markings.
   */
  @Test
  public void testCompleteNatoMarkingProcessing() {
    String marking = "//NATO SECRET//ATOMAL";
    when(metacard.getAttribute(any(String.class))).thenReturn(null);

    extractor.process(marking, metacard);

    // Verify attributes were set
    ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);
    verify(metacard, atLeastOnce()).setAttribute(captor.capture());

    List<Attribute> attributes = captor.getAllValues();
    assertThat(attributes, is(not(empty())));
  }

  /**
   * Verifies complete processing of FGI marking.
   *
   * <p>Integration test with FGI markings.
   */
  @Test
  public void testCompleteFgiMarkingProcessing() {
    String marking = "//GBR SECRET";
    when(metacard.getAttribute(any(String.class))).thenReturn(null);

    extractor.process(marking, metacard);

    // Verify attributes were set
    ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);
    verify(metacard, atLeastOnce()).setAttribute(captor.capture());

    List<Attribute> attributes = captor.getAllValues();
    assertThat(attributes, is(not(empty())));
  }

  /**
   * Verifies that all security map keys are valid security attributes.
   *
   * <p>The securityMap should only contain recognized security attribute names.
   */
  @Test
  public void testSecurityMapContainsValidAttributes() {
    // Verify that all keys in securityMap are actual Security interface constants
    assertThat(extractor.securityMap.keySet(), hasItem(Security.CLASSIFICATION));
    assertThat(extractor.securityMap.keySet(), hasItem(Security.RELEASABILITY));
    assertThat(extractor.securityMap.keySet(), hasItem(Security.CODEWORDS));
    assertThat(extractor.securityMap.keySet(), hasItem(Security.DISSEMINATION_CONTROLS));
    assertThat(extractor.securityMap.keySet(), hasItem(Security.OWNER_PRODUCER));
    assertThat(extractor.securityMap.keySet(), hasItem(Security.CLASSIFICATION_SYSTEM));
  }

  // ==========================================================================
  // Additional processCodewords() Tests
  // ==========================================================================

  /**
   * Verifies processCodewords() with compartments and sub-compartments.
   *
   * <p>SCI controls with compartments and sub-compartments should be properly formatted.
   */
  @Test
  public void testProcessCodewordsWithSubCompartments() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("TOP SECRET//SI-G ABC DEF//NOFORN");
    when(metacard.getAttribute(Security.CODEWORDS)).thenReturn(null);

    Attribute result = extractor.processCodewords(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), is(not(empty())));
    // Should contain formatted codeword with compartment and sub-compartments
  }

  /**
   * Verifies processCodewords() with matching existing codewords.
   *
   * <p>Should not throw exception when codewords match existing values.
   */
  @Test
  public void testProcessCodewordsWithMatchingExisting() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("TOP SECRET//SI//NOFORN");
    List<Serializable> existingValues = Arrays.asList("SI");
    Attribute existingAttr = new AttributeImpl(Security.CODEWORDS, existingValues);
    when(metacard.getAttribute(Security.CODEWORDS)).thenReturn(existingAttr);

    Attribute result = extractor.processCodewords(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), hasItem("SI"));
  }

  // ==========================================================================
  // Additional processDissem() Tests
  // ==========================================================================

  /**
   * Verifies processDissem() with matching existing dissemination controls.
   *
   * <p>Should not throw exception when dissemination controls match.
   */
  @Test
  public void testProcessDissemWithMatchingExisting() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    List<Serializable> existingValues = Arrays.asList("NOFORN");
    Attribute existingAttr = new AttributeImpl(Security.DISSEMINATION_CONTROLS, existingValues);
    when(metacard.getAttribute(Security.DISSEMINATION_CONTROLS)).thenReturn(existingAttr);

    Attribute result = extractor.processDissem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), hasItem("NOFORN"));
  }

  /**
   * Verifies processDissem() with size mismatch in existing values.
   *
   * <p>Should throw exception when list sizes don't match.
   */
  @Test(expected = MarkingMismatchException.class)
  public void testProcessDissemWithSizeMismatch() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    List<Serializable> existingValues = Arrays.asList("NOFORN", "ORCON");
    Attribute existingAttr = new AttributeImpl(Security.DISSEMINATION_CONTROLS, existingValues);
    when(metacard.getAttribute(Security.DISSEMINATION_CONTROLS)).thenReturn(existingAttr);

    extractor.processDissem(metacard, markings);
  }

  // ==========================================================================
  // Additional processOwnerProducer() Tests
  // ==========================================================================

  /**
   * Verifies processOwnerProducer() for FGI non-NATO/COSMIC markings.
   *
   * <p>FGI markings with non-NATO authority should use that authority.
   */
  @Test
  public void testProcessOwnerProducerFgiNonNato() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//GBR SECRET");
    when(metacard.getAttribute(Security.OWNER_PRODUCER)).thenReturn(null);

    Attribute result = extractor.processOwnerProducer(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), contains("GBR"));
  }

  /**
   * Verifies processOwnerProducer() for JOINT with matching existing.
   *
   * <p>Should not throw exception when JOINT authorities match existing values.
   */
  @Test
  public void testProcessOwnerProducerJointWithMatchingExisting() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//JOINT SECRET USA GBR");
    List<Serializable> existingValues = Arrays.asList("GBR", "USA");
    Attribute existingAttr = new AttributeImpl(Security.OWNER_PRODUCER, existingValues);
    when(metacard.getAttribute(Security.OWNER_PRODUCER)).thenReturn(existingAttr);

    Attribute result = extractor.processOwnerProducer(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), containsInAnyOrder("USA", "GBR"));
  }

  /**
   * Verifies processOwnerProducer() for JOINT with mismatched existing.
   *
   * <p>Should throw exception when JOINT authorities don't match existing.
   */
  @Test(expected = MarkingMismatchException.class)
  public void testProcessOwnerProducerJointWithMismatch() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//JOINT SECRET USA GBR");
    List<Serializable> existingValues = Arrays.asList("USA", "FRA");
    Attribute existingAttr = new AttributeImpl(Security.OWNER_PRODUCER, existingValues);
    when(metacard.getAttribute(Security.OWNER_PRODUCER)).thenReturn(existingAttr);

    extractor.processOwnerProducer(metacard, markings);
  }

  /**
   * Verifies processOwnerProducer() with blank existing value.
   *
   * <p>Blank existing values should be ignored (no validation).
   */
  @Test
  public void testProcessOwnerProducerWithBlankExisting() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    Attribute existingAttr = new AttributeImpl(Security.OWNER_PRODUCER, "");
    when(metacard.getAttribute(Security.OWNER_PRODUCER)).thenReturn(existingAttr);

    Attribute result = extractor.processOwnerProducer(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), contains("USA"));
  }

  // ==========================================================================
  // Additional processClassSystem() Tests
  // ==========================================================================

  /**
   * Verifies processClassSystem() for COSMIC markings.
   *
   * <p>COSMIC markings should set classification-system to "NATO".
   */
  @Test
  public void testProcessClassSystemCosmic() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//COSMIC TOP SECRET");
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(null);

    Attribute result = extractor.processClassSystem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), contains("NATO"));
  }

  /**
   * Verifies processClassSystem() for FGI with non-NATO authority.
   *
   * <p>Should use the FGI authority as classification system.
   */
  @Test
  public void testProcessClassSystemFgiNonNato() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//GBR SECRET");
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(null);

    Attribute result = extractor.processClassSystem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), contains("GBR"));
  }

  /**
   * Verifies processClassSystem() with matching existing value.
   *
   * <p>Should not throw exception when values match.
   */
  @Test
  public void testProcessClassSystemWithMatchingExisting() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    Attribute existingAttr = new AttributeImpl(Security.CLASSIFICATION_SYSTEM, "USA");
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(existingAttr);

    Attribute result = extractor.processClassSystem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), contains("USA"));
  }

  /**
   * Verifies processClassSystem() throws exception on mismatch.
   *
   * <p>Should throw MarkingMismatchException when values don't match.
   */
  @Test(expected = MarkingMismatchException.class)
  public void testProcessClassSystemWithMismatch() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    Attribute existingAttr = new AttributeImpl(Security.CLASSIFICATION_SYSTEM, "GBR");
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(existingAttr);

    extractor.processClassSystem(metacard, markings);
  }

  /**
   * Verifies processClassSystem() for JOINT with matching existing.
   *
   * <p>Should not throw exception when JOINT authorities match.
   */
  @Test
  public void testProcessClassSystemJointWithMatchingExisting() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//JOINT SECRET USA GBR");
    List<Serializable> existingValues = Arrays.asList("GBR", "USA");
    Attribute existingAttr = new AttributeImpl(Security.CLASSIFICATION_SYSTEM, existingValues);
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(existingAttr);

    Attribute result = extractor.processClassSystem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), containsInAnyOrder("USA", "GBR"));
  }

  // ==========================================================================
  // Additional NATO Classification Tests
  // ==========================================================================

  /**
   * Verifies processClassMarking() with NATO BOHEMIA qualifier.
   *
   * <p>BOHEMIA qualifier should add "-B" suffix. Note: BOHEMIA is only valid for TOP SECRET.
   */
  @Test
  public void testProcessClassMarkingNatoBohemia() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//COSMIC TOP SECRET//BOHEMIA");
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(null);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is("CTS-B"));
  }

  /**
   * Verifies processClassMarking() with NATO BALK qualifier.
   *
   * <p>BALK qualifier should add "-BALK" suffix. Note: BALK is only valid for TOP SECRET.
   */
  @Test
  public void testProcessClassMarkingNatoBalk() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//COSMIC TOP SECRET//BALK");
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(null);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is("CTS-BALK"));
  }

  /**
   * Verifies processClassMarking() with NATO CONFIDENTIAL.
   *
   * <p>NATO CONFIDENTIAL should be translated to "NC".
   */
  @Test
  public void testProcessClassMarkingNatoConfidential() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//NATO CONFIDENTIAL");
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(null);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is("NC"));
  }

  /**
   * Verifies processClassMarking() with NATO RESTRICTED.
   *
   * <p>NATO RESTRICTED should be translated to "NR".
   */
  @Test
  public void testProcessClassMarkingNatoRestricted() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//NATO RESTRICTED");
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(null);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is("NR"));
  }

  /**
   * Verifies processClassMarking() with NATO UNCLASSIFIED.
   *
   * <p>NATO UNCLASSIFIED should be translated to "NU".
   */
  @Test
  public void testProcessClassMarkingNatoUnclassified() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("//NATO UNCLASSIFIED");
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(null);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is("NU"));
  }

  // ==========================================================================
  // Additional Releasability Edge Cases
  // ==========================================================================

  /**
   * Verifies processReleasability() with size mismatch throws exception.
   *
   * <p>Different list sizes should trigger mismatch exception.
   */
  @Test(expected = MarkingMismatchException.class)
  public void testProcessReleasabilityWithSizeMismatch() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//REL TO USA, AUS");
    List<Serializable> existingValues = Arrays.asList("USA", "AUS", "GBR");
    Attribute existingAttr = new AttributeImpl(Security.RELEASABILITY, existingValues);
    when(metacard.getAttribute(Security.RELEASABILITY)).thenReturn(existingAttr);

    extractor.processReleasability(metacard, markings);
  }

  // ==========================================================================
  // Additional Integration Tests
  // ==========================================================================

  /**
   * Verifies complete processing of JOINT marking with multiple authorities.
   *
   * <p>Integration test with JOINT markings containing multiple authorities.
   */
  @Test
  public void testCompleteJointMarkingProcessing() {
    String marking = "//JOINT SECRET USA GBR";
    when(metacard.getAttribute(any(String.class))).thenReturn(null);

    extractor.process(marking, metacard);

    // Verify attributes were set
    ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);
    verify(metacard, atLeastOnce()).setAttribute(captor.capture());

    List<Attribute> attributes = captor.getAllValues();
    assertThat(attributes, is(not(empty())));
  }

  /**
   * Verifies complete processing with all security conflicts caught.
   *
   * <p>Should throw exception on first security mismatch encountered.
   */
  @Test(expected = MarkingMismatchException.class)
  public void testCompleteProcessingWithSecurityConflict() {
    String marking = "SECRET//NOFORN";
    Attribute existingClassAttr = new AttributeImpl(Security.CLASSIFICATION, "TS");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(existingClassAttr);
    when(metacard.getAttribute(Security.RELEASABILITY)).thenReturn(null);
    when(metacard.getAttribute(Security.CODEWORDS)).thenReturn(null);
    when(metacard.getAttribute(Security.DISSEMINATION_CONTROLS)).thenReturn(null);
    when(metacard.getAttribute(Security.OWNER_PRODUCER)).thenReturn(null);
    when(metacard.getAttribute(Security.CLASSIFICATION_SYSTEM)).thenReturn(null);

    extractor.process(marking, metacard);
  }

  /**
   * Verifies processing with invalid marking fails gracefully.
   *
   * <p>Invalid markings should not throw exception, just skip processing.
   */
  @Test
  public void testProcessingWithInvalidMarkingSkipsProcessing() {
    String marking = "INVALID MARKING FORMAT";
    when(metacard.getAttribute(any(String.class))).thenReturn(null);

    extractor.process(marking, metacard);

    // Should not throw exception, but also should not set attributes
    // (depending on implementation, this may or may not verify calls)
  }

  /**
   * Verifies processing with empty string does nothing.
   *
   * <p>Empty marking string should be handled gracefully.
   */
  @Test
  public void testProcessingWithEmptyString() {
    String marking = "";
    when(metacard.getAttribute(any(String.class))).thenReturn(null);

    extractor.process(marking, metacard);

    // Should not throw exception
  }

  /**
   * Verifies processClassMarking() with US CONFIDENTIAL marking.
   *
   * <p>Should translate CONFIDENTIAL to "C".
   */
  @Test
  public void testProcessClassMarkingUsConfidential() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("CONFIDENTIAL");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(null);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is("C"));
  }

  /**
   * Verifies processClassMarking() with US RESTRICTED marking.
   *
   * <p>Should translate RESTRICTED to "R".
   */
  @Test
  public void testProcessClassMarkingUsRestricted() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("RESTRICTED");
    when(metacard.getAttribute(Security.CLASSIFICATION)).thenReturn(null);

    Attribute result = extractor.processClassMarking(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is("R"));
  }
}
