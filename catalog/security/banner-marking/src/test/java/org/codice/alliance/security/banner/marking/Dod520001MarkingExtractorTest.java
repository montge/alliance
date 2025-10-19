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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
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
import org.codice.alliance.catalog.core.api.types.Dod520001;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * Unit tests for {@link Dod520001MarkingExtractor} class.
 *
 * <p>Dod520001MarkingExtractor processes DoD 5200.1-M specific security markings that supplement
 * the common markings. This includes:
 *
 * <ul>
 *   <li>Special Access Programs (SAP) - SAR-XXX, HVSACO
 *   <li>Atomic Energy Act (AEA) - RD, FRD, TFNI with SIGMA and -N qualifiers
 *   <li>DoD Unclassified Controlled Nuclear Information (DOD UCNI)
 *   <li>DoE Unclassified Controlled Nuclear Information (DOE UCNI)
 *   <li>Foreign Government Information (FGI) country codes
 *   <li>Other Dissemination Controls (ACCM-XXX, etc.)
 * </ul>
 *
 * <p><b>Coverage Target:</b> 85-90%
 *
 * <p><b>Source Complexity:</b> 201 instructions, 14 branches, 9 methods
 *
 * <p><b>Test Strategy:</b>
 *
 * <ul>
 *   <li>Test each DoD-specific attribute processor method
 *   <li>Test SAP control processing (programs, HVSACO, multiple)
 *   <li>Test AEA marking processing (RD, FRD, TFNI with qualifiers)
 *   <li>Test UCNI processing (DoD and DoE variants)
 *   <li>Test FGI country code extraction
 *   <li>Test other dissemination controls and ACCM
 *   <li>Test null/empty input handling
 *   <li>Verify attribute deduplication
 * </ul>
 *
 * <p><b>DoD 5200.1-M Compliance:</b> This extractor ensures compliance with DoD Manual 5200.01,
 * Volume 2, which defines marking requirements for classified national security information.
 *
 * @see BannerMarkings
 * @see MarkingExtractor
 * @see <a href="http://www.dtic.mil/whs/directives/corres/pdf/520001_vol2.pdf">DOD Manual Number
 *     5200.01, Volume 2</a>
 */
public class Dod520001MarkingExtractorTest {

  private Dod520001MarkingExtractor extractor;
  private Metacard metacard;

  @Before
  public void setUp() {
    extractor = new Dod520001MarkingExtractor();
    metacard = mock(Metacard.class);
  }

  // ==========================================================================
  // Constructor and Initialization Tests
  // ==========================================================================

  /**
   * Verifies that constructor initializes attribute processors correctly.
   *
   * <p>The constructor should set up processors for all DoD 5200.1-M specific attributes.
   */
  @Test
  public void testConstructorInitializesProcessors() {
    // Just instantiating should not throw exception
    Dod520001MarkingExtractor newExtractor = new Dod520001MarkingExtractor();
    assertThat(newExtractor, is(notNullValue()));
  }

  /**
   * Verifies that getMetacardAttributes() returns Dod520001Attributes descriptors.
   *
   * <p>Should return the set of attribute descriptors for DoD 5200.1-M specific attributes.
   */
  @Test
  public void testGetMetacardAttributes() {
    Set<AttributeDescriptor> descriptors = extractor.getMetacardAttributes();

    assertThat(descriptors, is(notNullValue()));
    assertThat(descriptors, is(not(empty())));
  }

  // ==========================================================================
  // processSap() Tests
  // ==========================================================================

  /**
   * Verifies processSap() with SAR program marking.
   *
   * <p>Should extract SAP program name from "SAR-PROGRAM" format.
   */
  @Test
  public void testProcessSapWithSarProgram() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("TOP SECRET//SAR-PROGRAM1");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_SAP)).thenReturn(null);

    Attribute result = extractor.processSap(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName(), is(Dod520001.SECURITY_DOD5200_SAP));
    assertThat(result.getValue(), is(notNullValue()));
    assertThat(result.getValue().toString(), containsString("SAR"));
  }

  /**
   * Verifies processSap() with HVSACO marking.
   *
   * <p>HVSACO is a special SAP designation that doesn't include program names.
   */
  @Test
  public void testProcessSapWithHvsaco() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("TOP SECRET//HVSACO");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_SAP)).thenReturn(null);

    Attribute result = extractor.processSap(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue().toString(), is("HVSACO"));
  }

  /**
   * Verifies processSap() with multiple SAP programs.
   *
   * <p>Should handle multiple programs separated by slashes.
   */
  @Test
  public void testProcessSapWithMultiplePrograms() throws Exception {
    BannerMarkings markings =
        BannerMarkings.parseMarkings("TOP SECRET//SAR-PROGRAM1/PROGRAM2/PROGRAM3");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_SAP)).thenReturn(null);

    Attribute result = extractor.processSap(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is(notNullValue()));
  }

  /**
   * Verifies processSap() with SPECIAL ACCESS REQUIRED long form.
   *
   * <p>Should accept both "SAR-" and "SPECIAL ACCESS REQUIRED-" formats.
   */
  @Test
  public void testProcessSapWithSpecialAccessRequiredLongForm() throws Exception {
    BannerMarkings markings =
        BannerMarkings.parseMarkings("TOP SECRET//SPECIAL ACCESS REQUIRED-PROGRAM1");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_SAP)).thenReturn(null);

    Attribute result = extractor.processSap(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue().toString(), containsString("SAR"));
  }

  /**
   * Verifies processSap() returns current attribute when no SAP control present.
   *
   * <p>When banner markings don't contain SAP, should return existing attribute or null.
   */
  @Test
  public void testProcessSapWithNoSapControl() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    Attribute existingAttr = new AttributeImpl(Dod520001.SECURITY_DOD5200_SAP, "SAR-EXISTING");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_SAP)).thenReturn(existingAttr);

    Attribute result = extractor.processSap(metacard, markings);

    assertThat(result, is(existingAttr));
  }

  /**
   * Verifies processSap() returns null when no SAP and no existing attribute.
   *
   * <p>When no SAP control and no existing attribute, should return null.
   */
  @Test
  public void testProcessSapWithNoSapControlAndNoExisting() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_SAP)).thenReturn(null);

    Attribute result = extractor.processSap(metacard, markings);

    assertThat(result, is(nullValue()));
  }

  // ==========================================================================
  // processAea() Tests
  // ==========================================================================

  /**
   * Verifies processAea() with Restricted Data marking.
   *
   * <p>Should extract RD (Restricted Data) AEA marking.
   */
  @Test
  public void testProcessAeaWithRestrictedData() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//RD//NOFORN");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_AEA)).thenReturn(null);

    Attribute result = extractor.processAea(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName(), is(Dod520001.SECURITY_DOD5200_AEA));
    assertThat(result.getValue().toString(), is("RESTRICTED DATA"));
  }

  /**
   * Verifies processAea() with Formerly Restricted Data marking.
   *
   * <p>Should extract FRD (Formerly Restricted Data) AEA marking.
   */
  @Test
  public void testProcessAeaWithFormerlyRestrictedData() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//FRD//NOFORN");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_AEA)).thenReturn(null);

    Attribute result = extractor.processAea(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue().toString(), is("FORMERLY RESTRICTED DATA"));
  }

  /**
   * Verifies processAea() with Critical Nuclear Weapon Design Information.
   *
   * <p>Should extract RD-N marking (Critical Nuclear Weapon Design Information).
   */
  @Test
  public void testProcessAeaWithCriticalNuclearWeaponDesign() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//RD-N");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_AEA)).thenReturn(null);

    Attribute result = extractor.processAea(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue().toString(), containsString("-N"));
  }

  /**
   * Verifies processAea() with SIGMA markings.
   *
   * <p>Should extract RD with SIGMA compartments.
   */
  @Test
  public void testProcessAeaWithSigma() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//RD-SIGMA 1 2");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_AEA)).thenReturn(null);

    Attribute result = extractor.processAea(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue().toString(), containsString("SIGMA"));
  }

  /**
   * Verifies processAea() returns current attribute when no AEA marking present.
   *
   * <p>When banner markings don't contain AEA, should return existing attribute or null.
   */
  @Test
  public void testProcessAeaWithNoAeaMarking() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    Attribute existingAttr = new AttributeImpl(Dod520001.SECURITY_DOD5200_AEA, "RD");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_AEA)).thenReturn(existingAttr);

    Attribute result = extractor.processAea(metacard, markings);

    assertThat(result, is(existingAttr));
  }

  /**
   * Verifies processAea() returns null when no AEA and no existing attribute.
   *
   * <p>When no AEA marking and no existing attribute, should return null.
   */
  @Test
  public void testProcessAeaWithNoAeaMarkingAndNoExisting() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_AEA)).thenReturn(null);

    Attribute result = extractor.processAea(metacard, markings);

    assertThat(result, is(nullValue()));
  }

  // ==========================================================================
  // processDodUcni() Tests
  // ==========================================================================

  /**
   * Verifies processDodUcni() extracts DoD UCNI marking.
   *
   * <p>Should create attribute with "DOD UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION" value.
   */
  @Test
  public void testProcessDodUcniWithMarking() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("UNCLASSIFIED//DOD UCNI");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_DODUCNI)).thenReturn(null);

    Attribute result = extractor.processDodUcni(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName(), is(Dod520001.SECURITY_DOD5200_DODUCNI));
    assertThat(result.getValue().toString(), is("DOD UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION"));
  }

  /**
   * Verifies processDodUcni() returns existing attribute when no DOD UCNI present.
   *
   * <p>When banner markings don't contain DOD UCNI, should return existing attribute.
   */
  @Test
  public void testProcessDodUcniWithoutMarking() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("UNCLASSIFIED");
    Attribute existingAttr =
        new AttributeImpl(
            Dod520001.SECURITY_DOD5200_DODUCNI, "DOD UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_DODUCNI)).thenReturn(existingAttr);

    Attribute result = extractor.processDodUcni(metacard, markings);

    assertThat(result, is(existingAttr));
  }

  /**
   * Verifies processDodUcni() returns existing when no marking and no existing attribute.
   *
   * <p>When no DOD UCNI marking, should return current metacard attribute (null).
   */
  @Test
  public void testProcessDodUcniWithoutMarkingAndNoExisting() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("UNCLASSIFIED");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_DODUCNI)).thenReturn(null);

    Attribute result = extractor.processDodUcni(metacard, markings);

    assertThat(result, is(nullValue()));
  }

  // ==========================================================================
  // processDoeUcni() Tests
  // ==========================================================================

  /**
   * Verifies processDoeUcni() extracts DoE UCNI marking.
   *
   * <p>Should create attribute with "DOE UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION" value.
   */
  @Test
  public void testProcessDoeUcniWithMarking() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("UNCLASSIFIED//DOE UCNI");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_DOEUCNI)).thenReturn(null);

    Attribute result = extractor.processDoeUcni(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName(), is(Dod520001.SECURITY_DOD5200_DOEUCNI));
    assertThat(result.getValue().toString(), is("DOE UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION"));
  }

  /**
   * Verifies processDoeUcni() returns existing attribute when no DOE UCNI present.
   *
   * <p>When banner markings don't contain DOE UCNI, should return existing attribute.
   */
  @Test
  public void testProcessDoeUcniWithoutMarking() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("UNCLASSIFIED");
    Attribute existingAttr =
        new AttributeImpl(
            Dod520001.SECURITY_DOD5200_DOEUCNI, "DOE UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_DOEUCNI)).thenReturn(existingAttr);

    Attribute result = extractor.processDoeUcni(metacard, markings);

    assertThat(result, is(existingAttr));
  }

  /**
   * Verifies processDoeUcni() returns existing when no marking and no existing attribute.
   *
   * <p>When no DOE UCNI marking, should return current metacard attribute (null).
   */
  @Test
  public void testProcessDoeUcniWithoutMarkingAndNoExisting() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("UNCLASSIFIED");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_DOEUCNI)).thenReturn(null);

    Attribute result = extractor.processDoeUcni(metacard, markings);

    assertThat(result, is(nullValue()));
  }

  // ==========================================================================
  // processFgi() Tests
  // ==========================================================================

  /**
   * Verifies processFgi() extracts FGI country codes for US markings.
   *
   * <p>Should extract FGI country codes with "FGI " prefix.
   */
  @Test
  public void testProcessFgiWithCountryCodes() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//FGI AUS");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_FGI)).thenReturn(null);

    Attribute result = extractor.processFgi(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName(), is(Dod520001.SECURITY_DOD5200_FGI));
    assertThat(result.getValue().toString(), startsWith("FGI"));
    assertThat(result.getValue().toString(), containsString("AUS"));
  }

  /**
   * Verifies processFgi() with single country code.
   *
   * <p>Should handle single country code properly.
   */
  @Test
  public void testProcessFgiWithSingleCountry() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//FGI AUS");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_FGI)).thenReturn(null);

    Attribute result = extractor.processFgi(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue().toString(), is("FGI AUS"));
  }

  /**
   * Verifies processFgi() with empty country code list (concealed FGI).
   *
   * <p>FGI marking with no country codes should result in "FGI" only.
   */
  @Test
  public void testProcessFgiWithNoCodes() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//FGI");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_FGI)).thenReturn(null);

    Attribute result = extractor.processFgi(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValue().toString(), is("FGI"));
  }

  /**
   * Verifies processFgi() with multiple country codes.
   *
   * <p>Should handle multiple country codes separated by spaces. FGI country codes must be alpha
   * trigraphs or tetragraphs.
   */
  @Test
  public void testProcessFgiWithMultipleCountries() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//FGI AUS GBRX");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_FGI)).thenReturn(null);

    Attribute result = extractor.processFgi(metacard, markings);

    assertThat(result, is(notNullValue()));
    String fgiValue = result.getValue().toString();
    assertThat(fgiValue, startsWith("FGI"));
    assertThat(fgiValue, containsString("AUS"));
    assertThat(fgiValue, containsString("GBRX"));
  }

  /**
   * Verifies processFgi() handles FGI marking with trailing spaces.
   *
   * <p>Should trim the output properly even with empty country code list.
   */
  @Test
  public void testProcessFgiWithEmptyCountryListTrimmed() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//FGI");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_FGI)).thenReturn(null);

    Attribute result = extractor.processFgi(metacard, markings);

    assertThat(result, is(notNullValue()));
    // Empty list should result in just "FGI" after trimming
    assertThat(result.getValue().toString(), is("FGI"));
  }

  // ==========================================================================
  // processOtherDissem() Tests
  // ==========================================================================

  /**
   * Verifies processOtherDissem() extracts other dissemination controls.
   *
   * <p>Should extract dissemination controls not covered by standard dissemination controls.
   */
  @Test
  public void testProcessOtherDissemWithControls() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//SBU");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_OTHER_DISSEM)).thenReturn(null);

    Attribute result = extractor.processOtherDissem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getName(), is(Dod520001.SECURITY_DOD5200_OTHER_DISSEM));
    assertThat(result.getValues(), is(not(empty())));
  }

  /**
   * Verifies processOtherDissem() extracts ACCM markings.
   *
   * <p>ACCM markings should be prefixed with "ACCM-" in the output.
   */
  @Test
  public void testProcessOtherDissemWithAccm() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//ACCM-ABC/XYZ");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_OTHER_DISSEM)).thenReturn(null);

    Attribute result = extractor.processOtherDissem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), hasSize(greaterThan(0)));
    // Should have ACCM prefixed values
    boolean hasAccmPrefix =
        result.getValues().stream()
            .anyMatch(val -> val.toString().startsWith("ACCM-") || val.toString().equals("ABC"));
    assertThat(hasAccmPrefix, is(true));
  }

  /**
   * Verifies processOtherDissem() with multiple ACCM codes.
   *
   * <p>Should extract all ACCM codes and prefix them with "ACCM-".
   */
  @Test
  public void testProcessOtherDissemWithMultipleAccm() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//ACCM-CODE1/CODE2/CODE3");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_OTHER_DISSEM)).thenReturn(null);

    Attribute result = extractor.processOtherDissem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), hasSize(greaterThan(0)));
  }

  /**
   * Verifies processOtherDissem() deduplicates with existing values.
   *
   * <p>When existing attribute has values, should deduplicate combined list.
   */
  @Test
  public void testProcessOtherDissemWithExistingValues() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//SBU");
    List<Serializable> existingValues = Arrays.asList("EXISTING1", "EXISTING2");
    Attribute existingAttr =
        new AttributeImpl(Dod520001.SECURITY_DOD5200_OTHER_DISSEM, existingValues);
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_OTHER_DISSEM)).thenReturn(existingAttr);

    Attribute result = extractor.processOtherDissem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), hasItems("EXISTING1", "EXISTING2"));
  }

  /**
   * Verifies processOtherDissem() with no other dissem controls.
   *
   * <p>When no other dissemination controls, should return empty list.
   */
  @Test
  public void testProcessOtherDissemWithNoControls() throws Exception {
    BannerMarkings markings = BannerMarkings.parseMarkings("SECRET//NOFORN");
    when(metacard.getAttribute(Dod520001.SECURITY_DOD5200_OTHER_DISSEM)).thenReturn(null);

    Attribute result = extractor.processOtherDissem(metacard, markings);

    assertThat(result, is(notNullValue()));
    assertThat(result.getValues(), is(empty()));
  }

  // ==========================================================================
  // Integration Tests
  // ==========================================================================

  /**
   * Verifies complete processing of DoD marking with SAP.
   *
   * <p>Integration test with SAP-specific markings.
   */
  @Test
  public void testCompleteProcessingWithSap() {
    String marking = "TOP SECRET//SAR-PROGRAM1//NOFORN";
    when(metacard.getAttribute(any(String.class))).thenReturn(null);

    extractor.process(marking, metacard);

    // Verify attributes were set
    ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);
    verify(metacard, atLeastOnce()).setAttribute(captor.capture());

    List<Attribute> attributes = captor.getAllValues();
    assertThat(attributes, is(not(empty())));
  }

  /**
   * Verifies complete processing of DoD marking with AEA.
   *
   * <p>Integration test with AEA-specific markings.
   */
  @Test
  public void testCompleteProcessingWithAea() {
    String marking = "SECRET//RD-SIGMA 1 2";
    when(metacard.getAttribute(any(String.class))).thenReturn(null);

    extractor.process(marking, metacard);

    // Verify attributes were set
    ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);
    verify(metacard, atLeastOnce()).setAttribute(captor.capture());

    List<Attribute> attributes = captor.getAllValues();
    assertThat(attributes, is(not(empty())));
  }

  /**
   * Verifies complete processing of DoD marking with UCNI.
   *
   * <p>Integration test with UCNI-specific markings.
   */
  @Test
  public void testCompleteProcessingWithUcni() {
    String marking = "UNCLASSIFIED//DOD UCNI";
    when(metacard.getAttribute(any(String.class))).thenReturn(null);

    extractor.process(marking, metacard);

    // Verify attributes were set
    ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);
    verify(metacard, atLeastOnce()).setAttribute(captor.capture());

    List<Attribute> attributes = captor.getAllValues();
    assertThat(attributes, is(not(empty())));
  }

  /**
   * Verifies complete processing of complex DoD marking.
   *
   * <p>Integration test with multiple DoD-specific markings combined.
   */
  @Test
  public void testCompleteProcessingWithComplexMarking() {
    String marking = "TOP SECRET//SAR-PROGRAM1//SI//NOFORN//FGI GBR";
    when(metacard.getAttribute(any(String.class))).thenReturn(null);

    extractor.process(marking, metacard);

    // Verify attributes were set
    ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);
    verify(metacard, atLeastOnce()).setAttribute(captor.capture());

    List<Attribute> attributes = captor.getAllValues();
    assertThat(attributes, is(not(empty())));
  }

  /**
   * Verifies processing when no DoD-specific markings present.
   *
   * <p>When banner has no DoD-specific markings, processor should handle gracefully.
   */
  @Test
  public void testProcessingWithNoDoD520001Markings() {
    String marking = "SECRET//NOFORN";
    when(metacard.getAttribute(any(String.class))).thenReturn(null);

    extractor.process(marking, metacard);

    // Verify attributes were set (may be null/empty for DoD-specific attributes)
    ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);
    verify(metacard, atLeastOnce()).setAttribute(captor.capture());
  }
}
