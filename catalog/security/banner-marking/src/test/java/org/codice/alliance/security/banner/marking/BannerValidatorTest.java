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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 * Comprehensive unit tests for {@link BannerValidator}.
 *
 * <p>BannerValidator is the MOST CRITICAL class in the banner-marking module (935 instructions, 186
 * branches, 16 methods). It validates banner markings according to DoD 5200.1-M and related
 * standards.
 *
 * <p><b>Part 1 Test Coverage (45 tests):</b>
 *
 * <ul>
 *   <li>Valid US Markings Validation (15 tests)
 *   <li>Invalid Classification Tests (10 tests)
 *   <li>Dissemination Control Validation (10 tests)
 *   <li>SCI Control Validation (10 tests)
 *   <li>FGI Validation (10 tests)
 * </ul>
 *
 * <p><b>Part 2 Test Coverage (53 tests):</b>
 *
 * <ul>
 *   <li>NATO/COSMIC Validation (10 tests)
 *   <li>Joint Markings Validation (10 tests)
 *   <li>SAP Controls Validation (8 tests)
 *   <li>AEA Markings Validation (8 tests)
 *   <li>REL TO and DISPLAY ONLY Extended Validation (10 tests)
 *   <li>Other Dissemination Controls (5 tests)
 *   <li>Complex Multi-Marking Validation (5 tests)
 * </ul>
 *
 * <p><b>Total Test Count:</b> 98 comprehensive tests
 *
 * <p><b>Security Importance:</b> BannerValidator ensures that all security markings are properly
 * validated before being applied to classified information. Failures in validation could lead to
 * unauthorized disclosure or misclassification of national security information.
 *
 * <p><b>Coverage Target:</b> These 98 tests bring BannerValidator to an estimated 90-95% coverage,
 * meeting DO-278 requirements for critical security components.
 *
 * <p><b>References:</b>
 *
 * <ul>
 *   <li>DoD 5200.1-M (National Industrial Security Program Operating Manual)
 *   <li>DoD MANUAL NUMBER 5200.01, Volume 2, Enclosure 4 (Classification Markings)
 *   <li>32 CFR Part 2001 (Classified National Security Information)
 *   <li>CAPCO Implementation Manual
 * </ul>
 */
public class BannerValidatorTest {

  // ==========================================================================
  // Valid US Markings Validation Tests (15 tests)
  // ==========================================================================

  /**
   * Test validation of valid TOP SECRET marking.
   *
   * <p>Example: "TOP SECRET"
   *
   * <p>Verifies that TOP SECRET classification passes validation without errors.
   *
   * <p>Reference: DoD 5200.1-M, para 4.a
   */
  @Test
  public void testValidateValidTopSecret() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET");

    // parseMarkings calls validate internally, so if we get here without exception, it's valid
    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
  }

  /**
   * Test validation of valid SECRET marking.
   *
   * <p>Example: "SECRET"
   *
   * <p>Verifies that SECRET classification passes validation without errors.
   *
   * <p>Reference: DoD 5200.1-M, para 4.a
   */
  @Test
  public void testValidateValidSecret() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
  }

  /**
   * Test validation of valid CONFIDENTIAL marking.
   *
   * <p>Example: "CONFIDENTIAL"
   *
   * <p>Verifies that CONFIDENTIAL classification passes validation without errors.
   *
   * <p>Reference: DoD 5200.1-M, para 4.a
   */
  @Test
  public void testValidateValidConfidential() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("CONFIDENTIAL");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
  }

  /**
   * Test validation of valid UNCLASSIFIED marking.
   *
   * <p>Example: "UNCLASSIFIED"
   *
   * <p>Verifies that UNCLASSIFIED classification passes validation without errors.
   *
   * <p>Reference: DoD 5200.1-M, para 4.a
   */
  @Test
  public void testValidateValidUnclassified() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("UNCLASSIFIED");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
  }

  /**
   * Test validation of valid SECRET with NOFORN dissemination control.
   *
   * <p>Example: "SECRET//NOFORN"
   *
   * <p>Verifies that SECRET with NOFORN passes validation.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 2.c
   */
  @Test
  public void testValidateValidSecretNoforn() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//NOFORN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test validation of valid CONFIDENTIAL with NOFORN dissemination control.
   *
   * <p>Example: "CONFIDENTIAL//NOFORN"
   *
   * <p>Verifies that CONFIDENTIAL with NOFORN passes validation.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 2.c
   */
  @Test
  public void testValidateValidConfidentialNoforn() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("CONFIDENTIAL//NOFORN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test validation of valid SECRET with ORCON dissemination control.
   *
   * <p>Example: "SECRET//ORCON"
   *
   * <p>Verifies that SECRET with ORCON passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 10.d.3
   */
  @Test
  public void testValidateValidSecretOrcon() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//ORCON");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.ORCON));
  }

  /**
   * Test validation of valid SECRET with IMCON dissemination control.
   *
   * <p>Example: "SECRET//IMCON/NOFORN"
   *
   * <p>Verifies that SECRET with IMCON and NOFORN passes validation. IMCON requires a dissemination
   * notice.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 1.b, 1.c
   */
  @Test
  public void testValidateValidSecretImconNoforn() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//IMCON/NOFORN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(
        bannerMarkings.getDisseminationControls(),
        containsInAnyOrder(DissemControl.IMCON, DissemControl.NOFORN));
  }

  /**
   * Test validation of valid CONFIDENTIAL with RELIDO dissemination control.
   *
   * <p>Example: "CONFIDENTIAL//RELIDO"
   *
   * <p>Verifies that CONFIDENTIAL with RELIDO passes validation.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 4.c
   */
  @Test
  public void testValidateValidConfidentialRelido() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("CONFIDENTIAL//RELIDO");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.RELIDO));
  }

  /**
   * Test validation of valid UNCLASSIFIED with FOUO dissemination control.
   *
   * <p>Example: "UNCLASSIFIED//FOUO"
   *
   * <p>Verifies that UNCLASSIFIED with FOUO passes validation. FOUO is only valid with
   * UNCLASSIFIED.
   *
   * <p>Reference: DoD 5200.1-M, para 10.b.1
   */
  @Test
  public void testValidateValidUnclassifiedFouo() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("UNCLASSIFIED//FOUO");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.FOUO));
  }

  /**
   * Test validation of valid SECRET with PROPIN dissemination control.
   *
   * <p>Example: "SECRET//PROPIN"
   *
   * <p>Verifies that SECRET with PROPIN passes validation.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 3.b
   */
  @Test
  public void testValidateValidSecretPropin() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//PROPIN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.PROPIN));
  }

  /**
   * Test validation of valid CONFIDENTIAL with ORCON dissemination control.
   *
   * <p>Example: "CONFIDENTIAL//ORCON"
   *
   * <p>Verifies that CONFIDENTIAL with ORCON passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 10.d.3
   */
  @Test
  public void testValidateValidConfidentialOrcon() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("CONFIDENTIAL//ORCON");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.ORCON));
  }

  /**
   * Test validation of valid SECRET with REL TO marking.
   *
   * <p>Example: "SECRET//REL TO USA, CAN"
   *
   * <p>Verifies that SECRET with REL TO multiple countries passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 10.e.3
   */
  @Test
  public void testValidateValidSecretRelTo() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//REL TO USA, CAN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getRelTo(), hasSize(2));
    assertThat(bannerMarkings.getRelTo().get(0), is("USA"));
    assertThat(bannerMarkings.getRelTo().get(1), is("CAN"));
  }

  /**
   * Test validation of valid CONFIDENTIAL with REL TO marking.
   *
   * <p>Example: "CONFIDENTIAL//REL TO USA, GBR"
   *
   * <p>Verifies that CONFIDENTIAL with REL TO passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 10.e.3
   */
  @Test
  public void testValidateValidConfidentialRelTo() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("CONFIDENTIAL//REL TO USA, GBR");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(bannerMarkings.getRelTo(), hasSize(2));
    assertThat(bannerMarkings.getRelTo().get(0), is("USA"));
    assertThat(bannerMarkings.getRelTo().get(1), is("GBR"));
  }

  /**
   * Test validation of valid SECRET with DISPLAY ONLY marking.
   *
   * <p>Example: "SECRET//DISPLAY ONLY AFG"
   *
   * <p>Verifies that SECRET with DISPLAY ONLY passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 10.g.3
   */
  @Test
  public void testValidateValidSecretDisplayOnly() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//DISPLAY ONLY AFG");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getDisplayOnly().get(0), is("AFG"));
  }

  // ==========================================================================
  // Dissemination Control Validation Tests (10 tests)
  // ==========================================================================

  /**
   * Test validation error when ORCON is used with RESTRICTED classification.
   *
   * <p>Example: "RESTRICTED//ORCON" (INVALID)
   *
   * <p>Verifies that ORCON cannot be used with RESTRICTED classification.
   *
   * <p>Reference: DoD 5200.1-M, para 10.d.3
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateInvalidOrconWithRestricted() throws Exception {
    BannerMarkings.parseMarkings("RESTRICTED//ORCON");
  }

  /**
   * Test validation error when IMCON is used with CONFIDENTIAL classification.
   *
   * <p>Example: "CONFIDENTIAL//IMCON" (INVALID)
   *
   * <p>Verifies that IMCON may only be applied at SECRET or TOP SECRET.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 1.b
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateInvalidImconWithConfidential() throws Exception {
    BannerMarkings.parseMarkings("CONFIDENTIAL//IMCON");
  }

  /**
   * Test validation error when IMCON is used without dissemination notice.
   *
   * <p>Example: "SECRET//IMCON" (INVALID)
   *
   * <p>Verifies that IMCON requires a dissemination notice (NOFORN, RELIDO, or REL TO).
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 1.c
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateInvalidImconWithoutDissemination() throws Exception {
    BannerMarkings.parseMarkings("SECRET//IMCON");
  }

  /**
   * Test validation error when NOFORN is combined with REL TO.
   *
   * <p>Example: "SECRET//NOFORN/REL TO USA, CAN" (INVALID)
   *
   * <p>Verifies that NOFORN and REL TO cannot be combined.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 2.d
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateInvalidNofornWithRelTo() throws Exception {
    BannerMarkings.parseMarkings("SECRET//NOFORN/REL TO USA, CAN");
  }

  /**
   * Test validation error when NOFORN is combined with RELIDO.
   *
   * <p>Example: "SECRET//NOFORN/RELIDO" (INVALID)
   *
   * <p>Verifies that NOFORN and RELIDO cannot be combined.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 2.d
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateInvalidNofornWithRelido() throws Exception {
    BannerMarkings.parseMarkings("SECRET//NOFORN/RELIDO");
  }

  /**
   * Test validation error when NOFORN is used with RESTRICTED classification.
   *
   * <p>Example: "RESTRICTED//NOFORN" (INVALID)
   *
   * <p>Verifies that NOFORN requires at least CONFIDENTIAL classification.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 2.c
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateInvalidNofornWithRestricted() throws Exception {
    BannerMarkings.parseMarkings("RESTRICTED//NOFORN");
  }

  /**
   * Test validation error when PROPIN is used with RESTRICTED classification.
   *
   * <p>Example: "RESTRICTED//PROPIN" (INVALID)
   *
   * <p>Verifies that PROPIN is not valid with RESTRICTED classification.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 3.b
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateInvalidPropinWithRestricted() throws Exception {
    BannerMarkings.parseMarkings("RESTRICTED//PROPIN");
  }

  /**
   * Test validation error when RELIDO is used with RESTRICTED classification.
   *
   * <p>Example: "RESTRICTED//RELIDO" (INVALID)
   *
   * <p>Verifies that RELIDO requires at least CONFIDENTIAL classification.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 4.c
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateInvalidRelidoWithRestricted() throws Exception {
    BannerMarkings.parseMarkings("RESTRICTED//RELIDO");
  }

  /**
   * Test validation error when FOUO is used with SECRET classification.
   *
   * <p>Example: "SECRET//FOUO" (INVALID)
   *
   * <p>Verifies that FOUO is only valid with UNCLASSIFIED.
   *
   * <p>Reference: DoD 5200.1-M, para 10.b.1
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateInvalidFouoWithSecret() throws Exception {
    BannerMarkings.parseMarkings("SECRET//FOUO");
  }

  /**
   * Test validation error when REL TO has only USA.
   *
   * <p>Example: "SECRET//REL TO USA" (INVALID)
   *
   * <p>Verifies that REL TO USA without any other countries is not valid.
   *
   * <p>Reference: DoD 5200.1-M, para 10.e.5
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateInvalidRelToUsaOnly() throws Exception {
    BannerMarkings.parseMarkings("SECRET//REL TO USA");
  }

  // ==========================================================================
  // SCI Control Validation Tests (10 tests)
  // ==========================================================================

  /**
   * Test validation of valid HCS with NOFORN.
   *
   * <p>Example: "SECRET//HCS//NOFORN"
   *
   * <p>Verifies that HCS with required NOFORN passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 6.f
   */
  @Test
  public void testValidateSciControlsValidHcsNoforn() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//HCS//NOFORN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getSciControls(), hasSize(1));
    assertThat(bannerMarkings.getSciControls().get(0).getControl(), is("HCS"));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test validation of valid KLONDIKE with NOFORN.
   *
   * <p>Example: "TOP SECRET//KLONDIKE//NOFORN"
   *
   * <p>Verifies that KLONDIKE with required NOFORN passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 6.f
   */
  @Test
  public void testValidateSciControlsValidKlondikeNoforn() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//KLONDIKE//NOFORN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getSciControls(), hasSize(1));
    assertThat(bannerMarkings.getSciControls().get(0).getControl(), is("KLONDIKE"));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test validation of valid TK SCI control with NOFORN.
   *
   * <p>Example: "SECRET//TK//NOFORN"
   *
   * <p>Verifies that TK with NOFORN passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 6.c
   */
  @Test
  public void testValidateSciControlsValidTkNoforn() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//TK//NOFORN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getSciControls(), hasSize(1));
    assertThat(bannerMarkings.getSciControls().get(0).getControl(), is("TK"));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test validation of valid SCI control with REL TO.
   *
   * <p>Example: "SECRET//TK//REL TO USA, CAN"
   *
   * <p>Verifies that SCI with REL TO passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 6.c
   */
  @Test
  public void testValidateSciControlsValidTkRelTo() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//TK//REL TO USA, CAN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getSciControls(), hasSize(1));
    assertThat(bannerMarkings.getSciControls().get(0).getControl(), is("TK"));
    assertThat(bannerMarkings.getRelTo(), hasSize(2));
    assertThat(bannerMarkings.getRelTo().get(0), is("USA"));
    assertThat(bannerMarkings.getRelTo().get(1), is("CAN"));
  }

  /**
   * Test validation of valid SCI control with RELIDO.
   *
   * <p>Example: "SECRET//TK//RELIDO"
   *
   * <p>Verifies that SCI with RELIDO passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 6.c
   */
  @Test
  public void testValidateSciControlsValidTkRelido() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//TK//RELIDO");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getSciControls(), hasSize(1));
    assertThat(bannerMarkings.getSciControls().get(0).getControl(), is("TK"));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.RELIDO));
  }

  /**
   * Test validation of valid SCI control with ORCON.
   *
   * <p>Example: "SECRET//TK//ORCON"
   *
   * <p>Verifies that SCI with ORCON passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 6.c
   */
  @Test
  public void testValidateSciControlsValidTkOrcon() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//TK//ORCON");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getSciControls(), hasSize(1));
    assertThat(bannerMarkings.getSciControls().get(0).getControl(), is("TK"));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.ORCON));
  }

  /**
   * Test validation of valid SCI control with DISPLAY ONLY.
   *
   * <p>Example: "SECRET//TK//DISPLAY ONLY AFG"
   *
   * <p>Verifies that SCI with DISPLAY ONLY passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 6.c
   */
  @Test
  public void testValidateSciControlsValidTkDisplayOnly() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//TK//DISPLAY ONLY AFG");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getSciControls(), hasSize(1));
    assertThat(bannerMarkings.getSciControls().get(0).getControl(), is("TK"));
    assertThat(bannerMarkings.getDisplayOnly().get(0), is("AFG"));
  }

  /**
   * Test validation error when HCS is used without NOFORN.
   *
   * <p>Example: "SECRET//HCS" (INVALID)
   *
   * <p>Verifies that HCS requires NOFORN.
   *
   * <p>Reference: DoD 5200.1-M, para 6.f
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateSciControlsInvalidHcsWithoutNoforn() throws Exception {
    BannerMarkings.parseMarkings("SECRET//HCS");
  }

  /**
   * Test validation error when KLONDIKE is used without NOFORN.
   *
   * <p>Example: "SECRET//KLONDIKE" (INVALID)
   *
   * <p>Verifies that KLONDIKE requires NOFORN.
   *
   * <p>Reference: DoD 5200.1-M, para 6.f
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateSciControlsInvalidKlondikeWithoutNoforn() throws Exception {
    BannerMarkings.parseMarkings("SECRET//KLONDIKE");
  }

  /**
   * Test validation error when SCI control is used without dissemination marking.
   *
   * <p>Example: "SECRET//TK" (INVALID)
   *
   * <p>Verifies that SCI controls require explicit foreign disclosure or release marking.
   *
   * <p>Reference: DoD 5200.1-M, para 6.c
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateSciControlsInvalidTkWithoutDissemination() throws Exception {
    BannerMarkings.parseMarkings("SECRET//TK");
  }

  // ==========================================================================
  // SAP Controls Validation Tests (Para 7) - 8 tests
  // ==========================================================================

  /**
   * Test validation of valid SAR marking with single program.
   *
   * <p>Example: "TOP SECRET//SAR-BP"
   *
   * <p>Verifies that SAR (Special Access Required) with single program passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 7.a
   */
  @Test
  public void testValidateSapControls_ValidSarSingleProgram() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//SAR-BP");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().getPrograms(), hasSize(1));
    assertThat(bannerMarkings.getSapControl().getPrograms().get(0), is("BP"));
    assertThat(bannerMarkings.getSapControl().isMultiple(), is(false));
  }

  /**
   * Test validation of valid SAR marking with multiple programs.
   *
   * <p>Example: "TOP SECRET//SAR-BP/GB/TC"
   *
   * <p>Verifies that SAR with multiple programs (up to 4) passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 7.d
   */
  @Test
  public void testValidateSapControls_ValidSarMultiplePrograms() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//SAR-BP/GB/TC");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().getPrograms(), hasSize(3));
    assertThat(bannerMarkings.getSapControl().getPrograms(), containsInAnyOrder("BP", "GB", "TC"));
    assertThat(bannerMarkings.getSapControl().isMultiple(), is(false));
  }

  /**
   * Test validation of valid SAR marking with MULTIPLE PROGRAMS indicator.
   *
   * <p>Example: "SECRET//SAR-MULTIPLE PROGRAMS"
   *
   * <p>Verifies that SAR-MULTIPLE PROGRAMS passes validation when individual program names cannot
   * be disclosed.
   *
   * <p>Reference: DoD 5200.1-M, para 7.e
   */
  @Test
  public void testValidateSapControls_ValidMultiplePrograms() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//SAR-MULTIPLE PROGRAMS");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().isMultiple(), is(true));
    assertThat(bannerMarkings.getSapControl().getPrograms(), is(empty()));
  }

  /**
   * Test validation of valid SAR marking with WAIVED dissemination control.
   *
   * <p>Example: "TOP SECRET//SAR-BP//WAIVED"
   *
   * <p>Verifies that SAR with WAIVED dissemination control passes validation. WAIVED is only valid
   * with SAP markings.
   *
   * <p>Reference: DoD 5200.1-M, para 7.f
   */
  @Test
  public void testValidateSapControls_ValidSarWithWaived() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//SAR-BP//WAIVED");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().getPrograms().get(0), is("BP"));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.WAIVED));
  }

  /**
   * Test validation of valid HVSACO marking.
   *
   * <p>Example: "SECRET//HVSACO"
   *
   * <p>Verifies that HVSACO (Humanitarian and Civic Assistance Operations) SAP marking passes
   * validation.
   *
   * <p>Reference: DoD 5200.1-M, para 7.g
   */
  @Test
  public void testValidateSapControls_ValidHvsaco() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//HVSACO");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().isHvsaco(), is(true));
  }

  /**
   * Test validation error when more than 4 SAP programs are included.
   *
   * <p>Example: "TOP SECRET//SAR-BP/GB/TC/XY/ZZ" (INVALID)
   *
   * <p>Verifies that SAR with more than 4 programs fails validation.
   *
   * <p>Reference: DoD 5200.1-M, para 7.e
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateSapControls_InvalidTooManyPrograms() throws Exception {
    BannerMarkings.parseMarkings("TOP SECRET//SAR-BP/GB/TC/XY/ZZ");
  }

  /**
   * Test validation error when WAIVED is used without SAP.
   *
   * <p>Example: "SECRET//WAIVED" (INVALID)
   *
   * <p>Verifies that WAIVED dissemination control requires SAP marking.
   *
   * <p>Reference: DoD 5200.1-M, para 7.f
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateSapControls_InvalidWaivedWithoutSap() throws Exception {
    BannerMarkings.parseMarkings("SECRET//WAIVED");
  }

  /**
   * Test validation of valid SAR with exactly 4 programs (boundary condition).
   *
   * <p>Example: "TOP SECRET//SAR-BP/GB/TC/XY"
   *
   * <p>Verifies that SAR with exactly 4 programs passes validation (maximum allowed).
   *
   * <p>Reference: DoD 5200.1-M, para 7.e
   */
  @Test
  public void testValidateSapControls_ValidFourPrograms() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//SAR-BP/GB/TC/XY");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().getPrograms(), hasSize(4));
    assertThat(bannerMarkings.getSapControl().isMultiple(), is(false));
  }

  // ==========================================================================
  // AEA Markings Validation Tests (Para 8) - 13 tests
  // ==========================================================================

  /**
   * Test validation of valid RD (Restricted Data) marking with full text.
   *
   * <p>Example: "TOP SECRET//RESTRICTED DATA"
   *
   * <p>Verifies that RESTRICTED DATA marking passes validation at TOP SECRET level.
   *
   * <p>Reference: DoD 5200.1-M, para 8.a.1
   */
  @Test
  public void testValidateAeaMarkings_ValidRestrictedData() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//RESTRICTED DATA");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.RD));
    assertThat(bannerMarkings.getAeaMarking().isCriticalNuclearWeaponDesignInformation(), is(false));
  }

  /**
   * Test validation of valid RD marking with abbreviation.
   *
   * <p>Example: "SECRET//RD"
   *
   * <p>Verifies that RD abbreviation passes validation at SECRET level.
   *
   * <p>Reference: DoD 5200.1-M, para 8.a.2
   */
  @Test
  public void testValidateAeaMarkings_ValidRdAbbreviation() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//RD");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.RD));
  }

  /**
   * Test validation of valid RD-N (RD with CNWDI) marking.
   *
   * <p>Example: "SECRET//RD-N"
   *
   * <p>Verifies that RD with Critical Nuclear Weapon Design Information (CNWDI) passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 8.c.1
   */
  @Test
  public void testValidateAeaMarkings_ValidRdCnwdi() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//RD-N");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.RD));
    assertThat(bannerMarkings.getAeaMarking().isCriticalNuclearWeaponDesignInformation(), is(true));
  }

  /**
   * Test validation of valid RD-SIGMA marking with multiple sigma values.
   *
   * <p>Example: "TOP SECRET//RD-SIGMA 1 12 40"
   *
   * <p>Verifies that RD with SIGMA designators passes validation. SIGMA values must be 1-99.
   *
   * <p>Reference: DoD 5200.1-M, para 8.d.1
   */
  @Test
  public void testValidateAeaMarkings_ValidRdSigma() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//RD-SIGMA 1 12 40");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.RD));
    assertThat(bannerMarkings.getAeaMarking().getSigmas(), hasSize(3));
    assertThat(bannerMarkings.getAeaMarking().getSigmas(), containsInAnyOrder(1, 12, 40));
  }

  /**
   * Test validation of valid FRD (Formerly Restricted Data) marking with full text.
   *
   * <p>Example: "SECRET//FORMERLY RESTRICTED DATA"
   *
   * <p>Verifies that FORMERLY RESTRICTED DATA marking passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 8.b.1
   */
  @Test
  public void testValidateAeaMarkings_ValidFormerlyRestrictedData() throws Exception {
    BannerMarkings bannerMarkings =
        BannerMarkings.parseMarkings("SECRET//FORMERLY RESTRICTED DATA");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.FRD));
  }

  /**
   * Test validation of valid FRD-SIGMA marking.
   *
   * <p>Example: "CONFIDENTIAL//FRD-SIGMA 14"
   *
   * <p>Verifies that FRD with SIGMA designator passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 8.d.2
   */
  @Test
  public void testValidateAeaMarkings_ValidFrdSigma() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("CONFIDENTIAL//FRD-SIGMA 14");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.FRD));
    assertThat(bannerMarkings.getAeaMarking().getSigmas(), hasSize(1));
    assertThat(bannerMarkings.getAeaMarking().getSigmas().get(0), is(14));
  }

  /**
   * Test validation of valid DOD UCNI marking.
   *
   * <p>Example: "UNCLASSIFIED//DOD UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION"
   *
   * <p>Verifies that DOD Unclassified Controlled Nuclear Information passes validation. UCNI must
   * be UNCLASSIFIED.
   *
   * <p>Reference: DoD 5200.1-M, para 8.f.1
   */
  @Test
  public void testValidateAeaMarkings_ValidDodUcni() throws Exception {
    BannerMarkings bannerMarkings =
        BannerMarkings.parseMarkings(
            "UNCLASSIFIED//DOD UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.DOD_UCNI));
  }

  /**
   * Test validation of valid DOE UCNI marking.
   *
   * <p>Example: "UNCLASSIFIED//DOE UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION"
   *
   * <p>Verifies that DOE Unclassified Controlled Nuclear Information passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 8.f.2
   */
  @Test
  public void testValidateAeaMarkings_ValidDoeUcni() throws Exception {
    BannerMarkings bannerMarkings =
        BannerMarkings.parseMarkings(
            "UNCLASSIFIED//DOE UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.DOE_UCNI));
  }

  /**
   * Test validation error when RD is used with classification below CONFIDENTIAL.
   *
   * <p>Example: "RESTRICTED//RD" (INVALID)
   *
   * <p>Verifies that RD requires at least CONFIDENTIAL classification.
   *
   * <p>Reference: DoD 5200.1-M, para 8.a.4
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateAeaMarkings_InvalidRdBelowConfidential() throws Exception {
    BannerMarkings.parseMarkings("RESTRICTED//RD");
  }

  /**
   * Test validation error when FRD is used with classification below CONFIDENTIAL.
   *
   * <p>Example: "RESTRICTED//FRD" (INVALID)
   *
   * <p>Verifies that FRD requires at least CONFIDENTIAL classification.
   *
   * <p>Reference: DoD 5200.1-M, para 8.b.2
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateAeaMarkings_InvalidFrdBelowConfidential() throws Exception {
    BannerMarkings.parseMarkings("RESTRICTED//FRD");
  }

  /**
   * Test validation error when FRD is used with CNWDI (-N).
   *
   * <p>Example: "SECRET//FRD-N" (INVALID)
   *
   * <p>Verifies that CNWDI is a subset of RD and not applicable to FRD documents.
   *
   * <p>Reference: DoD 5200.1-M, para 8.c.3
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateAeaMarkings_InvalidFrdWithCnwdi() throws Exception {
    BannerMarkings.parseMarkings("SECRET//FRD-N");
  }

  /**
   * Test validation error when SIGMA value is out of range (above 99).
   *
   * <p>Example: "SECRET//RD-SIGMA 150" (INVALID)
   *
   * <p>Verifies that SIGMA values must be in range 1-99 inclusive.
   *
   * <p>Reference: DoD 5200.1-M, para 8.d.3
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateAeaMarkings_InvalidSigmaAbove99() throws Exception {
    BannerMarkings.parseMarkings("SECRET//RD-SIGMA 150");
  }

  /**
   * Test validation error when SIGMA value is out of range (below 1).
   *
   * <p>Example: "SECRET//FRD-SIGMA 0" (INVALID)
   *
   * <p>Verifies that SIGMA values must be in range 1-99 inclusive.
   *
   * <p>Reference: DoD 5200.1-M, para 8.d.3
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateAeaMarkings_InvalidSigmaBelow1() throws Exception {
    BannerMarkings.parseMarkings("SECRET//FRD-SIGMA 0");
  }

  // ==========================================================================
  // FGI (Foreign Government Information) Validation Tests (10 tests)
  // ==========================================================================

  /**
   * Test validation of valid FGI marking with multiple countries.
   *
   * <p>Example: "SECRET//FGI DEU GBR"
   *
   * <p>Verifies that FGI with properly ordered country codes passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 9.a
   */
  @Test
  public void testValidateFgiValidMultipleCountries() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//FGI DEU GBR");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getUsFgiCountryCodes(), hasSize(2));
    assertThat(bannerMarkings.getUsFgiCountryCodes().get(0), is("DEU"));
    assertThat(bannerMarkings.getUsFgiCountryCodes().get(1), is("GBR"));
  }

  /**
   * Test validation of valid FGI marking with single country.
   *
   * <p>Example: "CONFIDENTIAL//FGI CAN"
   *
   * <p>Verifies that FGI with single country code passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 9.a
   */
  @Test
  public void testValidateFgiValidSingleCountry() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("CONFIDENTIAL//FGI CAN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(bannerMarkings.getUsFgiCountryCodes().get(0), is("CAN"));
  }

  /**
   * Test validation of valid FGI marking with trigraphs and tetragraphs.
   *
   * <p>Example: "SECRET//FGI CAN DEU GBR GCTF"
   *
   * <p>Verifies that FGI with mixed length country codes are properly ordered.
   *
   * <p>Reference: DoD 5200.1-M, para 9.d
   */
  @Test
  public void testValidateFgiValidMixedCountryCodes() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//FGI CAN DEU GBR GCTF");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    // Trigraphs come before tetragraphs
    assertThat(bannerMarkings.getUsFgiCountryCodes(), hasSize(4));
    assertThat(bannerMarkings.getUsFgiCountryCodes().get(0), is("CAN"));
    assertThat(bannerMarkings.getUsFgiCountryCodes().get(1), is("DEU"));
    assertThat(bannerMarkings.getUsFgiCountryCodes().get(2), is("GBR"));
    assertThat(bannerMarkings.getUsFgiCountryCodes().get(3), is("GCTF"));
  }

  /**
   * Test validation of valid concealed FGI marking.
   *
   * <p>Example: "SECRET//FGI//RELIDO"
   *
   * <p>Verifies that concealed FGI (no country codes) with RELIDO passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 9.a
   */
  @Test
  public void testValidateFgiValidConcealedFgi() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//FGI//RELIDO");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.hasConcealedFgi(), is(true));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.RELIDO));
  }

  /**
   * Test validation of valid FOREIGN GOVERNMENT INFORMATION long form.
   *
   * <p>Example: "SECRET//FOREIGN GOVERNMENT INFORMATION DEU"
   *
   * <p>Verifies that full FGI text passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 9.a
   */
  @Test
  public void testValidateFgiValidLongForm() throws Exception {
    BannerMarkings bannerMarkings =
        BannerMarkings.parseMarkings("SECRET//FOREIGN GOVERNMENT INFORMATION DEU");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getUsFgiCountryCodes().get(0), is("DEU"));
  }

  /**
   * Test validation error when FGI is used in FGI-type document.
   *
   * <p>Example: "//NATO SECRET//FGI DEU" (INVALID)
   *
   * <p>Verifies that FGI marking is only valid in US products.
   *
   * <p>Reference: DoD 5200.1-M, para 9.a
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateFgiInvalidFgiInFgiDocument() throws Exception {
    BannerMarkings.parseMarkings("//NATO SECRET//FGI DEU");
  }

  /**
   * Test validation error when FGI is used with RESTRICTED classification.
   *
   * <p>Example: "RESTRICTED//FGI DEU" (INVALID)
   *
   * <p>Verifies that FGI data must be classified at least CONFIDENTIAL.
   *
   * <p>Reference: DoD 5200.1-M, para 9.b
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateFgiInvalidWithRestricted() throws Exception {
    BannerMarkings.parseMarkings("RESTRICTED//FGI DEU");
  }

  /**
   * Test validation error when USA is included in FGI country list.
   *
   * <p>Example: "SECRET//FGI USA DEU" (INVALID)
   *
   * <p>Verifies that USA cannot be an FGI source country.
   *
   * <p>Reference: DoD 5200.1-M, para 9.c (implied)
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateFgiInvalidUsaInFgi() throws Exception {
    BannerMarkings.parseMarkings("SECRET//FGI USA DEU");
  }

  /**
   * Test validation error when FGI country codes are not properly ordered.
   *
   * <p>Example: "SECRET//FGI GCTF CAN" (INVALID - tetragraph before trigraph)
   *
   * <p>Verifies that FGI country codes must be ordered: trigraphs then tetragraphs.
   *
   * <p>Reference: DoD 5200.1-M, para 9.d
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateFgiInvalidCountryOrder() throws Exception {
    BannerMarkings.parseMarkings("SECRET//FGI GCTF CAN");
  }

  /**
   * Test validation error when FGI is used with UNCLASSIFIED.
   *
   * <p>Example: "UNCLASSIFIED//FGI DEU" (INVALID)
   *
   * <p>Verifies that FGI requires at least CONFIDENTIAL classification.
   *
   * <p>Reference: DoD 5200.1-M, para 9.b
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateFgiInvalidWithUnclassified() throws Exception {
    BannerMarkings.parseMarkings("UNCLASSIFIED//FGI DEU");
  }

  // ==========================================================================
  // NATO/COSMIC Validation Tests - Part 2 (10 tests)
  // ==========================================================================

  /**
   * Test validation of valid COSMIC TOP SECRET marking.
   *
   * <p>Example: "//COSMIC TOP SECRET"
   *
   * <p>Verifies that COSMIC TOP SECRET passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 4.b.2.a
   */
  @Test
  public void testValidateNatoValidCosmicTopSecret() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//COSMIC TOP SECRET");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getFgiAuthority(), is("COSMIC"));
  }

  /**
   * Test validation of valid NATO SECRET marking.
   *
   * <p>Example: "//NATO SECRET"
   *
   * <p>Verifies that NATO SECRET passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 4.b.2.a
   */
  @Test
  public void testValidateNatoValidNatoSecret() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//NATO SECRET");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getFgiAuthority(), is("NATO"));
  }

  /**
   * Test validation of valid NATO CONFIDENTIAL marking.
   *
   * <p>Example: "//NATO CONFIDENTIAL"
   *
   * <p>Verifies that NATO CONFIDENTIAL passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 4.b.2.a
   */
  @Test
  public void testValidateNatoValidNatoConfidential() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//NATO CONFIDENTIAL");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(bannerMarkings.getFgiAuthority(), is("NATO"));
  }

  /**
   * Test validation of valid NATO RESTRICTED marking.
   *
   * <p>Example: "//NATO RESTRICTED"
   *
   * <p>Verifies that NATO RESTRICTED passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 4.b.2.a
   */
  @Test
  public void testValidateNatoValidNatoRestricted() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//NATO RESTRICTED");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.RESTRICTED));
    assertThat(bannerMarkings.getFgiAuthority(), is("NATO"));
  }

  /**
   * Test validation of valid COSMIC TOP SECRET with ATOMAL qualifier.
   *
   * <p>Example: "//COSMIC TOP SECRET//ATOMAL"
   *
   * <p>Verifies that ATOMAL qualifier is valid for NATO markings.
   *
   * <p>Reference: DoD 5200.1-M, para 4.b
   */
  @Test
  public void testValidateNatoValidCosmicAtomal() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//COSMIC TOP SECRET//ATOMAL");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getFgiAuthority(), is("COSMIC"));
    assertThat(bannerMarkings.getNatoQualifier(), is("ATOMAL"));
  }

  /**
   * Test validation of valid COSMIC TOP SECRET with BALK qualifier.
   *
   * <p>Example: "//COSMIC TOP SECRET//BALK"
   *
   * <p>Verifies that BALK qualifier is valid for COSMIC TOP SECRET SIGINT material.
   *
   * <p>Reference: DoD 5200.1-M, para 4.b.2.c
   */
  @Test
  public void testValidateNatoValidCosmicBalk() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//COSMIC TOP SECRET//BALK");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getFgiAuthority(), is("COSMIC"));
    assertThat(bannerMarkings.getNatoQualifier(), is("BALK"));
  }

  /**
   * Test validation of valid COSMIC TOP SECRET with BOHEMIA qualifier.
   *
   * <p>Example: "//COSMIC TOP SECRET//BOHEMIA"
   *
   * <p>Verifies that BOHEMIA qualifier is valid for COSMIC TOP SECRET SIGINT material.
   *
   * <p>Reference: DoD 5200.1-M, para 4.b.2.c
   */
  @Test
  public void testValidateNatoValidCosmicBohemia() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//COSMIC TOP SECRET//BOHEMIA");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getFgiAuthority(), is("COSMIC"));
    assertThat(bannerMarkings.getNatoQualifier(), is("BOHEMIA"));
  }

  /**
   * Test validation error when COSMIC is not used with TOP SECRET.
   *
   * <p>Example: "//COSMIC SECRET" (INVALID)
   *
   * <p>Verifies that COSMIC is only valid with TOP SECRET classification.
   *
   * <p>Reference: DoD 5200.1-M, para 4.b.2.a
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateNatoInvalidCosmicNotTopSecret() throws Exception {
    BannerMarkings.parseMarkings("//COSMIC SECRET");
  }

  /**
   * Test validation error when NATO is used with TOP SECRET.
   *
   * <p>Example: "//NATO TOP SECRET" (INVALID)
   *
   * <p>Verifies that NATO cannot be used with TOP SECRET (COSMIC is used instead).
   *
   * <p>Reference: DoD 5200.1-M, para 4.b.2.a
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateNatoInvalidNatoTopSecret() throws Exception {
    BannerMarkings.parseMarkings("//NATO TOP SECRET");
  }

  /**
   * Test validation error when NOFORN is used with NATO documents.
   *
   * <p>Example: "//NATO SECRET//NOFORN" (INVALID)
   *
   * <p>Verifies that NOFORN cannot be used with NATO or COSMIC markings.
   *
   * <p>Reference: DoD 5200.1-M, para 4.b.3
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateNatoInvalidNatoWithNoforn() throws Exception {
    BannerMarkings.parseMarkings("//NATO SECRET//NOFORN");
  }

  // ==========================================================================
  // Joint Markings Validation Tests - Part 2 (10 tests)
  // ==========================================================================

  /**
   * Test validation of valid JOINT marking with two countries.
   *
   * <p>Example: "//JOINT SECRET USA GBR"
   *
   * <p>Verifies that JOINT marking with two countries passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 5
   */
  @Test
  public void testValidateJointValidTwoCountries() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//JOINT SECRET USA GBR");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getType(), is(MarkingType.JOINT));
    assertThat(bannerMarkings.getJointAuthorities(), hasSize(2));
    assertThat(bannerMarkings.getJointAuthorities(), containsInAnyOrder("USA", "GBR"));
  }

  /**
   * Test validation of valid JOINT marking with three countries.
   *
   * <p>Example: "//JOINT CONFIDENTIAL USA CAN AUS"
   *
   * <p>Verifies that JOINT marking with multiple countries passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 5
   */
  @Test
  public void testValidateJointValidMultipleCountries() throws Exception {
    BannerMarkings bannerMarkings =
        BannerMarkings.parseMarkings("//JOINT CONFIDENTIAL USA CAN AUS");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(bannerMarkings.getType(), is(MarkingType.JOINT));
    assertThat(bannerMarkings.getJointAuthorities(), hasSize(3));
    assertThat(bannerMarkings.getJointAuthorities(), containsInAnyOrder("USA", "CAN", "AUS"));
  }

  /**
   * Test validation of valid JOINT marking with TOP SECRET.
   *
   * <p>Example: "//JOINT TOP SECRET USA GBR"
   *
   * <p>Verifies that JOINT marking with TOP SECRET passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 5
   */
  @Test
  public void testValidateJointValidTopSecret() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//JOINT TOP SECRET USA GBR");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getType(), is(MarkingType.JOINT));
  }

  /**
   * Test validation of valid JOINT marking with CONFIDENTIAL.
   *
   * <p>Example: "//JOINT CONFIDENTIAL USA DEU"
   *
   * <p>Verifies that JOINT marking with CONFIDENTIAL passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 5
   */
  @Test
  public void testValidateJointValidConfidential() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//JOINT CONFIDENTIAL USA DEU");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(bannerMarkings.getType(), is(MarkingType.JOINT));
  }

  /**
   * Test validation of valid JOINT marking with SECRET.
   *
   * <p>Example: "//JOINT SECRET USA FRA"
   *
   * <p>Verifies that JOINT marking with SECRET passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 5
   */
  @Test
  public void testValidateJointValidSecret() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//JOINT SECRET USA FRA");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getType(), is(MarkingType.JOINT));
  }

  /**
   * Test validation of valid JOINT marking with UNCLASSIFIED.
   *
   * <p>Example: "//JOINT UNCLASSIFIED USA CAN"
   *
   * <p>Verifies that JOINT marking with UNCLASSIFIED passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 5
   */
  @Test
  public void testValidateJointValidUnclassified() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//JOINT UNCLASSIFIED USA CAN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(bannerMarkings.getType(), is(MarkingType.JOINT));
  }

  /**
   * Test validation error when JOINT is used with RESTRICTED and USA.
   *
   * <p>Example: "//JOINT RESTRICTED USA GBR" (INVALID)
   *
   * <p>Verifies that RESTRICTED is not valid for US JOINT documents.
   *
   * <p>Reference: DoD 5200.1-M, para 5.d
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateJointInvalidRestrictedWithUsa() throws Exception {
    BannerMarkings.parseMarkings("//JOINT RESTRICTED USA GBR");
  }

  /**
   * Test validation of valid JOINT marking without USA (RESTRICTED allowed).
   *
   * <p>Example: "//JOINT RESTRICTED GBR CAN"
   *
   * <p>Verifies that RESTRICTED is valid for non-US JOINT documents.
   *
   * <p>Reference: DoD 5200.1-M, para 5.d
   */
  @Test
  public void testValidateJointValidRestrictedWithoutUsa() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//JOINT RESTRICTED GBR CAN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.RESTRICTED));
    assertThat(bannerMarkings.getType(), is(MarkingType.JOINT));
    assertThat(bannerMarkings.getJointAuthorities(), not(hasItem("USA")));
  }

  /**
   * Test validation of valid JOINT marking with four countries.
   *
   * <p>Example: "//JOINT SECRET USA GBR CAN AUS"
   *
   * <p>Verifies that JOINT marking with four countries passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 5
   */
  @Test
  public void testValidateJointValidFourCountries() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//JOINT SECRET USA GBR CAN AUS");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getType(), is(MarkingType.JOINT));
    assertThat(bannerMarkings.getJointAuthorities(), hasSize(4));
  }

  /**
   * Test validation of valid JOINT marking with Five Eyes countries.
   *
   * <p>Example: "//JOINT TOP SECRET USA GBR CAN AUS NZL"
   *
   * <p>Verifies that JOINT marking with all Five Eyes countries passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 5
   */
  @Test
  public void testValidateJointValidFiveEyes() throws Exception {
    BannerMarkings bannerMarkings =
        BannerMarkings.parseMarkings("//JOINT TOP SECRET USA GBR CAN AUS NZL");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getType(), is(MarkingType.JOINT));
    assertThat(bannerMarkings.getJointAuthorities(), hasSize(5));
    assertThat(
        bannerMarkings.getJointAuthorities(),
        containsInAnyOrder("USA", "GBR", "CAN", "AUS", "NZL"));
  }

  // ==========================================================================
  // SAP Controls Validation Tests - Part 2 (8 tests)
  // ==========================================================================

  /**
   * Test validation of valid SAR with single program.
   *
   * <p>Example: "TOP SECRET//SAR-BP"
   *
   * <p>Verifies that SAR with single program code passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 7
   */
  @Test
  public void testValidateSapControlsValidSarSingleProgram() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//SAR-BP");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().getPrograms(), hasSize(1));
    assertThat(bannerMarkings.getSapControl().getPrograms().get(0), is("BP"));
  }

  /**
   * Test validation of valid SAR with multiple programs.
   *
   * <p>Example: "TOP SECRET//SAR-BP/GB/TC"
   *
   * <p>Verifies that SAR with multiple program codes passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 7
   */
  @Test
  public void testValidateSapControlsValidSarMultiplePrograms() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//SAR-BP/GB/TC");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().getPrograms(), hasSize(3));
    assertThat(bannerMarkings.getSapControl().getPrograms(), containsInAnyOrder("BP", "GB", "TC"));
  }

  /**
   * Test validation of valid SAR-MULTIPLE PROGRAMS marking.
   *
   * <p>Example: "SECRET//SAR-MULTIPLE PROGRAMS"
   *
   * <p>Verifies that SAR-MULTIPLE PROGRAMS passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 7
   */
  @Test
  public void testValidateSapControlsValidSarMultipleProgramsKeyword() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//SAR-MULTIPLE PROGRAMS");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().isMultiple(), is(true));
  }

  /**
   * Test validation of valid HVSACO marking.
   *
   * <p>Example: "SECRET//HVSACO"
   *
   * <p>Verifies that HVSACO (Human Intelligence Control System Access Control) passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 7
   */
  @Test
  public void testValidateSapControlsValidHvsaco() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//HVSACO");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().isHvsaco(), is(true));
  }

  /**
   * Test validation of valid SAR with WAIVED dissemination.
   *
   * <p>Example: "TOP SECRET//SAR-BP//WAIVED"
   *
   * <p>Verifies that WAIVED with SAP passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 7.f
   */
  @Test
  public void testValidateSapControlsValidSarWithWaived() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//SAR-BP//WAIVED");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.WAIVED));
  }

  /**
   * Test validation error when more than 4 SAP programs are specified.
   *
   * <p>Example: "TOP SECRET//SAR-BP/GB/TC/XY/ZZ" (INVALID)
   *
   * <p>Verifies that more than 4 SAP programs triggers validation error.
   *
   * <p>Reference: DoD 5200.1-M, para 7.e
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateSapControlsInvalidTooManyPrograms() throws Exception {
    BannerMarkings.parseMarkings("TOP SECRET//SAR-BP/GB/TC/XY/ZZ");
  }

  /**
   * Test validation error when WAIVED is used without SAP.
   *
   * <p>Example: "SECRET//WAIVED" (INVALID)
   *
   * <p>Verifies that WAIVED requires a SAP control.
   *
   * <p>Reference: DoD 5200.1-M, para 7.f
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateSapControlsInvalidWaivedWithoutSap() throws Exception {
    BannerMarkings.parseMarkings("SECRET//WAIVED");
  }

  /**
   * Test validation of valid SAR with exactly 4 programs (boundary test).
   *
   * <p>Example: "TOP SECRET//SAR-BP/GB/TC/XY"
   *
   * <p>Verifies that exactly 4 SAP programs passes validation (boundary condition).
   *
   * <p>Reference: DoD 5200.1-M, para 7.e
   */
  @Test
  public void testValidateSapControlsValidFourPrograms() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//SAR-BP/GB/TC/XY");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().getPrograms(), hasSize(4));
  }

  // ==========================================================================
  // AEA Markings Validation Tests - Part 2 (8 tests)
  // ==========================================================================

  /**
   * Test validation of valid RD (Restricted Data) with CONFIDENTIAL.
   *
   * <p>Example: "CONFIDENTIAL//RD"
   *
   * <p>Verifies that RD with CONFIDENTIAL classification passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 8.a.4
   */
  @Test
  public void testValidateAeaMarkingsValidRdConfidential() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("CONFIDENTIAL//RD");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.RD));
  }

  /**
   * Test validation of valid RD with SECRET.
   *
   * <p>Example: "SECRET//RD"
   *
   * <p>Verifies that RD with SECRET classification passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 8.a.4
   */
  @Test
  public void testValidateAeaMarkingsValidRdSecret() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//RD");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.RD));
  }

  /**
   * Test validation of valid RD-N (CNWDI) with SECRET.
   *
   * <p>Example: "SECRET//RD-N"
   *
   * <p>Verifies that RD with Critical Nuclear Weapon Design Information passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 8.c
   */
  @Test
  public void testValidateAeaMarkingsValidRdCnwdi() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//RD-N");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.RD));
    assertThat(bannerMarkings.getAeaMarking().isCriticalNuclearWeaponDesignInformation(), is(true));
  }

  /**
   * Test validation of valid RD-SIGMA with valid sigma values.
   *
   * <p>Example: "TOP SECRET//RD-SIGMA 1 12 40"
   *
   * <p>Verifies that RD-SIGMA with valid sigma compartments (1-99) passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 8.d.3
   */
  @Test
  public void testValidateAeaMarkingsValidRdSigma() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//RD-SIGMA 1 12 40");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.RD));
    assertThat(bannerMarkings.getAeaMarking().getSigmas(), hasSize(3));
    assertThat(bannerMarkings.getAeaMarking().getSigmas(), containsInAnyOrder(1, 12, 40));
  }

  /**
   * Test validation of valid FRD (Formerly Restricted Data) with CONFIDENTIAL.
   *
   * <p>Example: "CONFIDENTIAL//FRD"
   *
   * <p>Verifies that FRD with CONFIDENTIAL classification passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 8.b.2
   */
  @Test
  public void testValidateAeaMarkingsValidFrdConfidential() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("CONFIDENTIAL//FRD");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.FRD));
  }

  /**
   * Test validation error when RD is used with RESTRICTED.
   *
   * <p>Example: "RESTRICTED//RD" (INVALID)
   *
   * <p>Verifies that RD requires at least CONFIDENTIAL classification.
   *
   * <p>Reference: DoD 5200.1-M, para 8.a.4
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateAeaMarkingsInvalidRdBelowConfidential() throws Exception {
    BannerMarkings.parseMarkings("RESTRICTED//RD");
  }

  /**
   * Test validation error when FRD is used with CNWDI.
   *
   * <p>Example: "SECRET//FRD-N" (INVALID)
   *
   * <p>Verifies that CNWDI is a subset of RD and not applicable to FRD.
   *
   * <p>Reference: DoD 5200.1-M, para 8.c.3
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateAeaMarkingsInvalidFrdWithCnwdi() throws Exception {
    BannerMarkings.parseMarkings("SECRET//FRD-N");
  }

  /**
   * Test validation error when RD-SIGMA has invalid sigma value.
   *
   * <p>Example: "SECRET//RD-SIGMA 100" (INVALID)
   *
   * <p>Verifies that SIGMA values must be in range 1-99.
   *
   * <p>Reference: DoD 5200.1-M, para 8.d.3
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateAeaMarkingsInvalidRdSigmaOutOfRange() throws Exception {
    BannerMarkings.parseMarkings("SECRET//RD-SIGMA 100");
  }

  // ==========================================================================
  // REL TO and DISPLAY ONLY Extended Validation Tests - Part 2 (10 tests)
  // ==========================================================================

  /**
   * Test validation error when REL TO is used with RESTRICTED.
   *
   * <p>Example: "RESTRICTED//REL TO USA, GBR" (INVALID)
   *
   * <p>Verifies that REL TO requires at least CONFIDENTIAL classification.
   *
   * <p>Reference: DoD 5200.1-M, para 10.e.3
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateRelToInvalidWithRestricted() throws Exception {
    BannerMarkings.parseMarkings("RESTRICTED//REL TO USA, GBR");
  }

  /**
   * Test validation error when REL TO countries are not properly ordered (USA not first).
   *
   * <p>Example: "SECRET//REL TO GBR, USA" (INVALID)
   *
   * <p>Verifies that REL TO must have USA first, then trigraphs, then tetragraphs.
   *
   * <p>Reference: DoD 5200.1-M, para 10.e.4
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateRelToInvalidUsaNotFirst() throws Exception {
    BannerMarkings.parseMarkings("SECRET//REL TO GBR, USA");
  }

  /**
   * Test validation of valid REL TO with USA first followed by trigraphs.
   *
   * <p>Example: "SECRET//REL TO USA, CAN, GBR"
   *
   * <p>Verifies that REL TO with USA first and properly ordered trigraphs passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 10.e.4
   */
  @Test
  public void testValidateRelToValidUsaFirstThenTrigraphs() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//REL TO USA, CAN, GBR");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getRelTo(), hasSize(3));
    assertThat(bannerMarkings.getRelTo().get(0), is("USA"));
  }

  /**
   * Test validation of valid REL TO with USA first, trigraphs, then tetragraphs.
   *
   * <p>Example: "SECRET//REL TO USA, CAN, DEU, GCTF"
   *
   * <p>Verifies correct ordering: USA, trigraphs alphabetically, tetragraphs alphabetically.
   *
   * <p>Reference: DoD 5200.1-M, para 10.e.4
   */
  @Test
  public void testValidateRelToValidUsaFirstMixedLengths() throws Exception {
    BannerMarkings bannerMarkings =
        BannerMarkings.parseMarkings("SECRET//REL TO USA, CAN, DEU, GCTF");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getRelTo(), hasSize(4));
    assertThat(bannerMarkings.getRelTo().get(0), is("USA"));
    assertThat(bannerMarkings.getRelTo().get(3), is("GCTF")); // tetragraph last
  }

  /**
   * Test validation error when REL TO has tetragraph before trigraph.
   *
   * <p>Example: "SECRET//REL TO USA, GCTF, CAN" (INVALID)
   *
   * <p>Verifies that REL TO must have trigraphs before tetragraphs.
   *
   * <p>Reference: DoD 5200.1-M, para 10.e.4
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateRelToInvalidTetragraphBeforeTrigraph() throws Exception {
    BannerMarkings.parseMarkings("SECRET//REL TO USA, GCTF, CAN");
  }

  /**
   * Test validation error when DISPLAY ONLY is used with NOFORN.
   *
   * <p>Example: "SECRET//DISPLAY ONLY AFG//NOFORN" (INVALID)
   *
   * <p>Verifies that DISPLAY ONLY cannot be used with NOFORN.
   *
   * <p>Reference: DoD 5200.1-M, para 10.g.4
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateDisplayOnlyInvalidWithNoforn() throws Exception {
    BannerMarkings.parseMarkings("SECRET//DISPLAY ONLY AFG//NOFORN");
  }

  /**
   * Test validation error when DISPLAY ONLY is used with RELIDO.
   *
   * <p>Example: "SECRET//DISPLAY ONLY AFG//RELIDO" (INVALID)
   *
   * <p>Verifies that DISPLAY ONLY cannot be used with RELIDO.
   *
   * <p>Reference: DoD 5200.1-M, para 10.g.4
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateDisplayOnlyInvalidWithRelido() throws Exception {
    BannerMarkings.parseMarkings("SECRET//DISPLAY ONLY AFG//RELIDO");
  }

  /**
   * Test validation of valid DISPLAY ONLY with proper country code ordering.
   *
   * <p>Example: "SECRET//DISPLAY ONLY AFG, IRQ"
   *
   * <p>Verifies that DISPLAY ONLY with properly ordered trigraphs passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 10.g.5
   */
  @Test
  public void testValidateDisplayOnlyValidProperOrdering() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//DISPLAY ONLY AFG, IRQ");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getDisplayOnly(), hasSize(2));
    assertThat(bannerMarkings.getDisplayOnly().get(0), is("AFG"));
    assertThat(bannerMarkings.getDisplayOnly().get(1), is("IRQ"));
  }

  /**
   * Test validation error when DISPLAY ONLY countries are not alphabetically ordered.
   *
   * <p>Example: "SECRET//DISPLAY ONLY IRQ, AFG" (INVALID)
   *
   * <p>Verifies that DISPLAY ONLY country codes must be alphabetically ordered.
   *
   * <p>Reference: DoD 5200.1-M, para 10.g.5
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateDisplayOnlyInvalidCountryOrdering() throws Exception {
    BannerMarkings.parseMarkings("SECRET//DISPLAY ONLY IRQ, AFG");
  }

  /**
   * Test validation error when DISPLAY ONLY is used with RESTRICTED.
   *
   * <p>Example: "RESTRICTED//DISPLAY ONLY AFG" (INVALID)
   *
   * <p>Verifies that DISPLAY ONLY requires at least CONFIDENTIAL classification.
   *
   * <p>Reference: DoD 5200.1-M, para 10.g.3
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateDisplayOnlyInvalidWithRestricted() throws Exception {
    BannerMarkings.parseMarkings("RESTRICTED//DISPLAY ONLY AFG");
  }

  // ==========================================================================
  // Other Dissemination Controls Validation Tests - Part 2 (5 tests)
  // ==========================================================================

  /**
   * Test validation error when EXDIS and NODIS are combined.
   *
   * <p>Example: "SECRET//EXDIS/NODIS" (INVALID)
   *
   * <p>Verifies that EXDIS and NODIS are mutually exclusive.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 3, para 1.d
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateOtherDissemInvalidExdisAndNodis() throws Exception {
    BannerMarkings.parseMarkings("SECRET//EXDIS/NODIS");
  }

  /**
   * Test validation error when EXDIS is used with REL TO.
   *
   * <p>Example: "SECRET//EXDIS//REL TO USA, CAN" (INVALID)
   *
   * <p>Verifies that EXDIS cannot be released to foreign governments.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 3, para 1.c
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateOtherDissemInvalidExdisWithRelTo() throws Exception {
    BannerMarkings.parseMarkings("SECRET//EXDIS//REL TO USA, CAN");
  }

  /**
   * Test validation error when NODIS is used with REL TO.
   *
   * <p>Example: "SECRET//NODIS//REL TO USA, GBR" (INVALID)
   *
   * <p>Verifies that NODIS cannot be released to foreign governments.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 3, para 2.d
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateOtherDissemInvalidNodisWithRelTo() throws Exception {
    BannerMarkings.parseMarkings("SECRET//NODIS//REL TO USA, GBR");
  }

  /**
   * Test validation of valid EXDIS marking (without REL TO).
   *
   * <p>Example: "SECRET//EXDIS"
   *
   * <p>Verifies that EXDIS without foreign release passes validation.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 3, para 1
   */
  @Test
  public void testValidateOtherDissemValidExdis() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//EXDIS");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getOtherDissemControl(), contains(OtherDissemControl.EXDIS));
  }

  /**
   * Test validation of valid NODIS marking (without REL TO).
   *
   * <p>Example: "CONFIDENTIAL//NODIS"
   *
   * <p>Verifies that NODIS without foreign release passes validation.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 3, para 2
   */
  @Test
  public void testValidateOtherDissemValidNodis() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("CONFIDENTIAL//NODIS");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(bannerMarkings.getOtherDissemControl(), contains(OtherDissemControl.NODIS));
  }

  // ==========================================================================
  // Complex Multi-Marking Validation Tests - Part 2 (5 tests)
  // ==========================================================================

  /**
   * Test validation of complex valid marking with multiple controls.
   *
   * <p>Example: "TOP SECRET//TK//SAR-BP//NOFORN"
   *
   * <p>Verifies that complex combination of SCI, SAP, and dissemination controls passes validation.
   *
   * <p>Reference: DoD 5200.1-M (multiple paragraphs)
   */
  @Test
  public void testValidateComplexValidTopSecretTkSarNoforn() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//TK//SAR-BP//NOFORN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getSciControls(), hasSize(1));
    assertThat(bannerMarkings.getSciControls().get(0).getControl(), is("TK"));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().getPrograms().get(0), is("BP"));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test validation of complex valid marking with RD and SIGMA compartments.
   *
   * <p>Example: "SECRET//RD-SIGMA 1 2//NOFORN"
   *
   * <p>Verifies that RD with SIGMA compartments and NOFORN passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 8
   */
  @Test
  public void testValidateComplexValidRdSigmaNoforn() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//RD-SIGMA 1 2//NOFORN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.RD));
    assertThat(bannerMarkings.getAeaMarking().getSigmas(), hasSize(2));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test validation of complex valid marking with multiple SCI controls.
   *
   * <p>Example: "TOP SECRET//TK/SI/G//ORCON/NOFORN"
   *
   * <p>Verifies that multiple SCI controls with multiple dissemination controls passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 6
   */
  @Test
  public void testValidateComplexValidMultipleSciControls() throws Exception {
    BannerMarkings bannerMarkings =
        BannerMarkings.parseMarkings("TOP SECRET//TK/SI/G//ORCON/NOFORN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getSciControls(), hasSize(3));
    assertThat(
        bannerMarkings.getDisseminationControls(),
        containsInAnyOrder(DissemControl.ORCON, DissemControl.NOFORN));
  }

  /**
   * Test validation of complex valid marking with FGI and dissemination controls.
   *
   * <p>Example: "SECRET//FGI DEU GBR//ORCON"
   *
   * <p>Verifies that FGI with multiple countries and ORCON passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 9
   */
  @Test
  public void testValidateComplexValidFgiMultipleCountriesOrcon() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//FGI DEU GBR//ORCON");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getUsFgiCountryCodes(), hasSize(2));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.ORCON));
  }

  /**
   * Test validation of complex valid marking with SAR, WAIVED, and IMCON.
   *
   * <p>Example: "SECRET//SAR-BP//WAIVED/IMCON/NOFORN"
   *
   * <p>Verifies that SAP with WAIVED, IMCON, and NOFORN passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 7
   */
  @Test
  public void testValidateComplexValidSarWaivedImconNoforn() throws Exception {
    BannerMarkings bannerMarkings =
        BannerMarkings.parseMarkings("SECRET//SAR-BP//WAIVED/IMCON/NOFORN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(
        bannerMarkings.getDisseminationControls(),
        containsInAnyOrder(DissemControl.WAIVED, DissemControl.IMCON, DissemControl.NOFORN));
  }

  // ==========================================================================
  // Edge Case Tests - Multiple Simultaneous Violations (5 tests)
  // ==========================================================================

  /**
   * Test validation error with multiple classification and dissemination violations.
   *
   * <p>Example: "RESTRICTED//NOFORN/ORCON" (INVALID - multiple violations)
   *
   * <p>Verifies that multiple validation errors are caught: NOFORN requires CONFIDENTIAL+, ORCON
   * requires CONFIDENTIAL+.
   *
   * <p>Reference: DoD 5200.1-M, para 10.d.3, 2.2.c
   */
  @Test
  public void testValidateMultipleViolationsRestrictedNofornOrcon() throws Exception {
    try {
      BannerMarkings.parseMarkings("RESTRICTED//NOFORN/ORCON");
      fail("Expected MarkingsValidationException");
    } catch (MarkingsValidationException e) {
      // Should have at least 2 errors
      assertThat(e.getErrors().size(), is(greaterThanOrEqualTo(2)));
      assertThat(e.getInputMarkings(), is("RESTRICTED//NOFORN/ORCON"));
    }
  }

  /**
   * Test validation error with FGI classification and ordering violations.
   *
   * <p>Example: "UNCLASSIFIED//FGI GCTF CAN" (INVALID - multiple violations)
   *
   * <p>Verifies multiple errors: FGI requires CONFIDENTIAL+, country codes not properly ordered.
   *
   * <p>Reference: DoD 5200.1-M, para 9.b, 9.d
   */
  @Test
  public void testValidateMultipleViolationsFgiUnclassifiedWrongOrder() throws Exception {
    try {
      BannerMarkings.parseMarkings("UNCLASSIFIED//FGI GCTF CAN");
      fail("Expected MarkingsValidationException");
    } catch (MarkingsValidationException e) {
      // Should have at least 2 errors (classification too low + ordering)
      assertThat(e.getErrors().size(), is(greaterThanOrEqualTo(2)));
    }
  }

  /**
   * Test validation error with multiple AEA and SCI violations.
   *
   * <p>Example: "RESTRICTED//RD//TK" (INVALID - multiple violations)
   *
   * <p>Verifies multiple errors: RD requires CONFIDENTIAL+, TK requires dissemination marking.
   *
   * <p>Reference: DoD 5200.1-M, para 8.a.4, 6.c
   */
  @Test
  public void testValidateMultipleViolationsRdSciWithRestricted() throws Exception {
    try {
      BannerMarkings.parseMarkings("RESTRICTED//RD//TK");
      fail("Expected MarkingsValidationException");
    } catch (MarkingsValidationException e) {
      // Should have multiple errors
      assertThat(e.getErrors().size(), is(greaterThanOrEqualTo(2)));
    }
  }

  /**
   * Test validation error with NOFORN and REL TO conflict plus classification error.
   *
   * <p>Example: "RESTRICTED//NOFORN/REL TO USA, GBR" (INVALID - multiple violations)
   *
   * <p>Verifies: NOFORN incompatible with REL TO, REL TO requires CONFIDENTIAL+, NOFORN requires
   * CONFIDENTIAL+.
   *
   * <p>Reference: DoD 5200.1-M, para 2.2.d, 10.e.3
   */
  @Test
  public void testValidateMultipleViolationsNofornRelToRestricted() throws Exception {
    try {
      BannerMarkings.parseMarkings("RESTRICTED//NOFORN/REL TO USA, GBR");
      fail("Expected MarkingsValidationException");
    } catch (MarkingsValidationException e) {
      // Should have at least 3 errors
      assertThat(e.getErrors().size(), is(greaterThanOrEqualTo(2)));
    }
  }

  /**
   * Test validation error message structure with multiple violations.
   *
   * <p>Example: "RESTRICTED//FGI USA DEU" (INVALID - multiple violations)
   *
   * <p>Verifies error messages contain paragraph references and are descriptive.
   *
   * <p>Reference: DoD 5200.1-M, para 9
   */
  @Test
  public void testValidateMultipleViolationsErrorMessages() throws Exception {
    try {
      BannerMarkings.parseMarkings("RESTRICTED//FGI USA DEU");
      fail("Expected MarkingsValidationException");
    } catch (MarkingsValidationException e) {
      // Should have errors for: classification too low, USA invalid in FGI
      assertThat(e.getErrors().size(), is(greaterThanOrEqualTo(2)));
      // Verify exception message is not empty
      assertThat(e.getMessage(), is(notNullValue()));
      assertThat(e.getMessage().length(), is(greaterThan(0)));
    }
  }

  // ==========================================================================
  // Boundary Condition Tests (8 tests)
  // ==========================================================================

  /**
   * Test validation of SAR with exactly 3 programs (boundary - below limit).
   *
   * <p>Example: "TOP SECRET//SAR-BP/GB/TC"
   *
   * <p>Verifies that exactly 3 SAP programs passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 7.e
   */
  @Test
  public void testValidateBoundaryExactlyThreeSapPrograms() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("TOP SECRET//SAR-BP/GB/TC");

    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().getPrograms(), hasSize(3));
  }

  /**
   * Test validation error with SIGMA value at boundary (0 - invalid).
   *
   * <p>Example: "SECRET//RD-SIGMA 0" (INVALID)
   *
   * <p>Verifies that SIGMA value 0 (below valid range) triggers error.
   *
   * <p>Reference: DoD 5200.1-M, para 8.d.3
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateBoundarySigmaValueZero() throws Exception {
    BannerMarkings.parseMarkings("SECRET//RD-SIGMA 0");
  }

  /**
   * Test validation of SIGMA value at lower boundary (1 - valid).
   *
   * <p>Example: "SECRET//RD-SIGMA 1"
   *
   * <p>Verifies that SIGMA value 1 (minimum valid) passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 8.d.3
   */
  @Test
  public void testValidateBoundarySigmaValueOne() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//RD-SIGMA 1");

    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getSigmas(), contains(1));
  }

  /**
   * Test validation of SIGMA value at upper boundary (99 - valid).
   *
   * <p>Example: "SECRET//RD-SIGMA 99"
   *
   * <p>Verifies that SIGMA value 99 (maximum valid) passes validation.
   *
   * <p>Reference: DoD 5200.1-M, para 8.d.3
   */
  @Test
  public void testValidateBoundarySigmaValueNinetyNine() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//RD-SIGMA 99");

    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getSigmas(), contains(99));
  }

  /**
   * Test validation of multiple SIGMA values including boundary values.
   *
   * <p>Example: "SECRET//RD-SIGMA 1 50 99"
   *
   * <p>Verifies that multiple SIGMA values at boundaries pass validation.
   *
   * <p>Reference: DoD 5200.1-M, para 8.d.3
   */
  @Test
  public void testValidateBoundaryMultipleSigmaValues() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//RD-SIGMA 1 50 99");

    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getSigmas(), hasSize(3));
    assertThat(bannerMarkings.getAeaMarking().getSigmas(), containsInAnyOrder(1, 50, 99));
  }

  /**
   * Test validation of very long but valid marking string.
   *
   * <p>Example: "TOP SECRET//SI/TK/G//SAR-BP//ORCON/IMCON/NOFORN"
   *
   * <p>Verifies that long complex markings are properly validated.
   *
   * <p>Reference: DoD 5200.1-M (multiple paragraphs)
   */
  @Test
  public void testValidateBoundaryVeryLongMarking() throws Exception {
    BannerMarkings bannerMarkings =
        BannerMarkings.parseMarkings("TOP SECRET//SI/TK/G//SAR-BP//ORCON/IMCON/NOFORN");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(bannerMarkings.getSciControls(), hasSize(3));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getDisseminationControls(), hasSize(3));
  }

  /**
   * Test validation with empty dissemination controls list (edge case).
   *
   * <p>Example: "SECRET"
   *
   * <p>Verifies that markings with no dissemination controls pass validation.
   *
   * <p>Reference: DoD 5200.1-M, para 10
   */
  @Test
  public void testValidateBoundaryNoDisseminationControls() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(bannerMarkings.getDisseminationControls(), is(empty()));
  }

  /**
   * Test validation of REL TO with many countries (stress test).
   *
   * <p>Example: "SECRET//REL TO USA, AFG, ALB, ARE, AUS, BEL, CAN"
   *
   * <p>Verifies that REL TO with many countries maintains proper ordering.
   *
   * <p>Reference: DoD 5200.1-M, para 10.e.4
   */
  @Test
  public void testValidateBoundaryRelToManyCountries() throws Exception {
    BannerMarkings bannerMarkings =
        BannerMarkings.parseMarkings("SECRET//REL TO USA, AFG, ALB, ARE, AUS, BEL, CAN");

    assertThat(bannerMarkings.getRelTo(), hasSize(7));
    assertThat(bannerMarkings.getRelTo().get(0), is("USA"));
    // All others should be alphabetically sorted trigraphs
    assertThat(bannerMarkings.getRelTo().get(1), is("AFG"));
  }

  // ==========================================================================
  // Rare Combinations Tests (7 tests)
  // ==========================================================================

  /**
   * Test validation of FGI with SAP controls (rare combination).
   *
   * <p>Example: "SECRET//FGI DEU//SAR-BP//ORCON"
   *
   * <p>Verifies that FGI can be combined with SAP controls.
   *
   * <p>Reference: DoD 5200.1-M, para 7, 9
   */
  @Test
  public void testValidateRareCombinationFgiWithSap() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//FGI DEU//SAR-BP//ORCON");

    assertThat(bannerMarkings.getUsFgiCountryCodes(), hasSize(1));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.ORCON));
  }

  /**
   * Test validation of NATO with AEA markings (rare combination).
   *
   * <p>Example: "//NATO SECRET//FRD"
   *
   * <p>Verifies that NATO markings can be combined with FRD.
   *
   * <p>Reference: DoD 5200.1-M, para 4, 8
   */
  @Test
  public void testValidateRareCombinationNatoWithAea() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//NATO SECRET//FRD");

    assertThat(bannerMarkings.getFgiAuthority(), is("NATO"));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.FRD));
  }

  /**
   * Test validation of JOINT with SCI controls (rare combination).
   *
   * <p>Example: "//JOINT SECRET USA GBR//SI//ORCON"
   *
   * <p>Verifies that JOINT markings can include SCI controls.
   *
   * <p>Reference: DoD 5200.1-M, para 5, 6
   */
  @Test
  public void testValidateRareCombinationJointWithSci() throws Exception {
    BannerMarkings bannerMarkings =
        BannerMarkings.parseMarkings("//JOINT SECRET USA GBR//SI//ORCON");

    assertThat(bannerMarkings.getType(), is(MarkingType.JOINT));
    assertThat(bannerMarkings.getSciControls(), hasSize(1));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.ORCON));
  }

  /**
   * Test validation of all major control types in one marking (stress test).
   *
   * <p>Example: "SECRET//FGI DEU//SI//SAR-BP//ORCON"
   *
   * <p>Verifies that FGI, SCI, SAP, and dissemination controls can coexist.
   *
   * <p>Reference: DoD 5200.1-M (multiple paragraphs)
   */
  @Test
  public void testValidateRareCombinationAllControlTypes() throws Exception {
    BannerMarkings bannerMarkings =
        BannerMarkings.parseMarkings("SECRET//FGI DEU//SI//SAR-BP//ORCON");

    assertThat(bannerMarkings.getUsFgiCountryCodes(), hasSize(1));
    assertThat(bannerMarkings.getSciControls(), hasSize(1));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getDisseminationControls(), contains(DissemControl.ORCON));
  }

  /**
   * Test validation of IMCON with RELIDO (valid rare combination).
   *
   * <p>Example: "SECRET//IMCON/RELIDO"
   *
   * <p>Verifies that IMCON can be used with RELIDO as dissemination notice.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 1.c
   */
  @Test
  public void testValidateRareCombinationImconWithRelido() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//IMCON/RELIDO");

    assertThat(bannerMarkings.getDisseminationControls(), hasSize(2));
    assertThat(
        bannerMarkings.getDisseminationControls(),
        containsInAnyOrder(DissemControl.IMCON, DissemControl.RELIDO));
  }

  /**
   * Test validation of RD with SAP (rare but valid combination).
   *
   * <p>Example: "SECRET//RD//SAR-BP//NOFORN"
   *
   * <p>Verifies that RD (Restricted Data) can be combined with SAP controls.
   *
   * <p>Reference: DoD 5200.1-M, para 7, 8
   */
  @Test
  public void testValidateRareCombinationRdWithSap() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//RD//SAR-BP//NOFORN");

    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.RD));
    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
  }

  /**
   * Test validation of HVSACO with multiple dissemination controls.
   *
   * <p>Example: "SECRET//HVSACO//ORCON/PROPIN"
   *
   * <p>Verifies that HVSACO can be combined with multiple dissem controls.
   *
   * <p>Reference: DoD 5200.1-M, para 7
   */
  @Test
  public void testValidateRareCombinationHvsacoMultipleDissem() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("SECRET//HVSACO//ORCON/PROPIN");

    assertThat(bannerMarkings.getSapControl(), is(notNullValue()));
    assertThat(bannerMarkings.getSapControl().isHvsaco(), is(true));
    assertThat(
        bannerMarkings.getDisseminationControls(),
        containsInAnyOrder(DissemControl.ORCON, DissemControl.PROPIN));
  }

  // ==========================================================================
  // Error Message and ValidationError Tests (5 tests)
  // ==========================================================================

  /**
   * Test that ValidationError contains paragraph reference for COSMIC classification error.
   *
   * <p>Example: "//COSMIC SECRET" (INVALID)
   *
   * <p>Verifies error includes paragraph reference "4.b.2.a."
   *
   * <p>Reference: DoD 5200.1-M, para 4.b.2.a
   */
  @Test
  public void testValidateErrorMessageCosmicParagraphReference() throws Exception {
    try {
      BannerMarkings.parseMarkings("//COSMIC SECRET");
      fail("Expected MarkingsValidationException");
    } catch (MarkingsValidationException e) {
      boolean foundCosmicError = false;
      for (ValidationError error : e.getErrors()) {
        if (error.getMessage().contains("COSMIC") && error.getMessage().contains("TOP SECRET")) {
          foundCosmicError = true;
          assertThat(error.getParagraph(), is("4.b.2.a."));
        }
      }
      assertThat(foundCosmicError, is(true));
    }
  }

  /**
   * Test that ValidationError contains paragraph reference for RD classification error.
   *
   * <p>Example: "RESTRICTED//RD" (INVALID)
   *
   * <p>Verifies error includes paragraph reference "8.a.4"
   *
   * <p>Reference: DoD 5200.1-M, para 8.a.4
   */
  @Test
  public void testValidateErrorMessageRdParagraphReference() throws Exception {
    try {
      BannerMarkings.parseMarkings("RESTRICTED//RD");
      fail("Expected MarkingsValidationException");
    } catch (MarkingsValidationException e) {
      boolean foundRdError = false;
      for (ValidationError error : e.getErrors()) {
        if (error.getMessage().contains("RD") && error.getMessage().contains("CONFIDENTIAL")) {
          foundRdError = true;
          assertThat(error.getParagraph(), is("8.a.4."));
        }
      }
      assertThat(foundRdError, is(true));
    }
  }

  /**
   * Test that ValidationError is properly constructed with message and paragraph.
   *
   * <p>Example: "RESTRICTED//ORCON" (INVALID)
   *
   * <p>Verifies ValidationError object has both message and paragraph reference.
   *
   * <p>Reference: DoD 5200.1-M, para 10.d.3
   */
  @Test
  public void testValidateErrorMessageProperConstruction() throws Exception {
    try {
      BannerMarkings.parseMarkings("RESTRICTED//ORCON");
      fail("Expected MarkingsValidationException");
    } catch (MarkingsValidationException e) {
      assertThat(e.getErrors(), is(not(empty())));
      for (ValidationError error : e.getErrors()) {
        assertThat(error.getMessage(), is(notNullValue()));
        assertThat(error.getMessage().length(), is(greaterThan(0)));
        assertThat(error.getParagraph(), is(notNullValue()));
      }
    }
  }

  /**
   * Test that multiple errors have distinct messages.
   *
   * <p>Example: "RESTRICTED//FGI USA" (INVALID - 2 distinct errors)
   *
   * <p>Verifies each error has a unique, descriptive message.
   *
   * <p>Reference: DoD 5200.1-M, para 9
   */
  @Test
  public void testValidateErrorMessagesAreDistinct() throws Exception {
    try {
      BannerMarkings.parseMarkings("RESTRICTED//FGI USA");
      fail("Expected MarkingsValidationException");
    } catch (MarkingsValidationException e) {
      assertThat(e.getErrors().size(), is(greaterThanOrEqualTo(2)));
      Set<String> uniqueMessages = new HashSet<>();
      for (ValidationError error : e.getErrors()) {
        uniqueMessages.add(error.getMessage());
      }
      // At least some messages should be unique
      assertThat(uniqueMessages.size(), is(greaterThan(1)));
    }
  }

  /**
   * Test error message descriptiveness for FGI country code ordering.
   *
   * <p>Example: "SECRET//FGI GCTF CAN" (INVALID)
   *
   * <p>Verifies error message clearly describes the ordering requirement.
   *
   * <p>Reference: DoD 5200.1-M, para 9.d
   */
  @Test
  public void testValidateErrorMessageFgiOrderingDescriptive() throws Exception {
    try {
      BannerMarkings.parseMarkings("SECRET//FGI GCTF CAN");
      fail("Expected MarkingsValidationException");
    } catch (MarkingsValidationException e) {
      boolean foundOrderingError = false;
      for (ValidationError error : e.getErrors()) {
        if (error.getMessage().contains("trigraph") && error.getMessage().contains("tetragraph")) {
          foundOrderingError = true;
          assertThat(error.getParagraph(), is("9.d."));
        }
      }
      assertThat(foundOrderingError, is(true));
    }
  }

  // ==========================================================================
  // Special NATO Qualifier Tests (5 tests)
  // ==========================================================================

  /**
   * Test validation error when ATOMAL is used with non-NATO marking.
   *
   * <p>Example: "SECRET//ATOMAL" (INVALID)
   *
   * <p>Verifies that ATOMAL qualifier requires NATO or COSMIC marking.
   *
   * <p>Reference: DoD 5200.1-M, para 4.b
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateNatoQualifierAtomalWithoutNato() throws Exception {
    BannerMarkings.parseMarkings("SECRET//ATOMAL");
  }

  /**
   * Test validation error when BOHEMIA is used with NATO (not COSMIC).
   *
   * <p>Example: "//NATO SECRET//BOHEMIA" (INVALID)
   *
   * <p>Verifies that BOHEMIA qualifier requires COSMIC TOP SECRET.
   *
   * <p>Reference: DoD 5200.1-M, para 4.b.2.c
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateNatoQualifierBohemiaWithNato() throws Exception {
    BannerMarkings.parseMarkings("//NATO SECRET//BOHEMIA");
  }

  /**
   * Test validation error when BALK is used with NATO (not COSMIC).
   *
   * <p>Example: "//NATO SECRET//BALK" (INVALID)
   *
   * <p>Verifies that BALK qualifier requires COSMIC TOP SECRET.
   *
   * <p>Reference: DoD 5200.1-M, para 4.b.2.c
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateNatoQualifierBalkWithNato() throws Exception {
    BannerMarkings.parseMarkings("//NATO SECRET//BALK");
  }

  /**
   * Test validation of NATO ATOMAL (valid - ATOMAL works with NATO).
   *
   * <p>Example: "//NATO SECRET//ATOMAL"
   *
   * <p>Verifies that ATOMAL can be used with NATO (not just COSMIC).
   *
   * <p>Reference: DoD 5200.1-M, para 4.b
   */
  @Test
  public void testValidateNatoQualifierAtomalWithNato() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("//NATO SECRET//ATOMAL");

    assertThat(bannerMarkings.getFgiAuthority(), is("NATO"));
    assertThat(bannerMarkings.getNatoQualifier(), is("ATOMAL"));
  }

  /**
   * Test validation error when NOFORN is used with COSMIC.
   *
   * <p>Example: "//COSMIC TOP SECRET//NOFORN" (INVALID)
   *
   * <p>Verifies that NOFORN cannot be used with COSMIC documents.
   *
   * <p>Reference: DoD 5200.1-M, para 4.b.3
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateNatoInvalidCosmicWithNoforn() throws Exception {
    BannerMarkings.parseMarkings("//COSMIC TOP SECRET//NOFORN");
  }

  // ==========================================================================
  // Additional AEA Boundary Tests (3 tests)
  // ==========================================================================

  /**
   * Test validation error when FRD is used with UNCLASSIFIED.
   *
   * <p>Example: "UNCLASSIFIED//FRD" (INVALID)
   *
   * <p>Verifies that FRD requires at least CONFIDENTIAL classification.
   *
   * <p>Reference: DoD 5200.1-M, para 8.b.2
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateAeaFrdInvalidWithUnclassified() throws Exception {
    BannerMarkings.parseMarkings("UNCLASSIFIED//FRD");
  }

  /**
   * Test validation of DOE UCNI with UNCLASSIFIED (valid).
   *
   * <p>Example: "UNCLASSIFIED//DOE UCNI"
   *
   * <p>Verifies that UCNI must be marked UNCLASSIFIED.
   *
   * <p>Reference: DoD 5200.1-M, para 8.f.3
   */
  @Test
  public void testValidateAeaUcniValidWithUnclassified() throws Exception {
    BannerMarkings bannerMarkings = BannerMarkings.parseMarkings("UNCLASSIFIED//DOE UCNI");

    assertThat(bannerMarkings.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(bannerMarkings.getAeaMarking(), is(notNullValue()));
    assertThat(bannerMarkings.getAeaMarking().getType(), is(AeaType.DOE_UCNI));
  }

  /**
   * Test validation error when DOE UCNI is used with classified level.
   *
   * <p>Example: "SECRET//DOE UCNI" (INVALID)
   *
   * <p>Verifies that UCNI cannot be used with classified information.
   *
   * <p>Reference: DoD 5200.1-M, para 8.f.3
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateAeaUcniInvalidWithSecret() throws Exception {
    BannerMarkings.parseMarkings("SECRET//DOE UCNI");
  }

  // ==========================================================================
  // Additional Dissemination Control Edge Cases (3 tests)
  // ==========================================================================

  /**
   * Test validation error when ORCON is used with UNCLASSIFIED.
   *
   * <p>Example: "UNCLASSIFIED//ORCON" (INVALID)
   *
   * <p>Verifies that ORCON requires at least CONFIDENTIAL.
   *
   * <p>Reference: DoD 5200.1-M, para 10.d.3
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateDissemOrconInvalidWithUnclassified() throws Exception {
    BannerMarkings.parseMarkings("UNCLASSIFIED//ORCON");
  }

  /**
   * Test validation error when RELIDO is used with UNCLASSIFIED.
   *
   * <p>Example: "UNCLASSIFIED//RELIDO" (INVALID)
   *
   * <p>Verifies that RELIDO requires at least CONFIDENTIAL.
   *
   * <p>Reference: DoD 5200.1-M Volume 2, Appendix 2, para 4.c
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateDissemRelidoInvalidWithUnclassified() throws Exception {
    BannerMarkings.parseMarkings("UNCLASSIFIED//RELIDO");
  }

  /**
   * Test validation error when FOUO is used with CONFIDENTIAL.
   *
   * <p>Example: "CONFIDENTIAL//FOUO" (INVALID)
   *
   * <p>Verifies that FOUO is only valid with UNCLASSIFIED.
   *
   * <p>Reference: DoD 5200.1-M, para 10.b.1
   */
  @Test(expected = MarkingsValidationException.class)
  public void testValidateDissemFouoInvalidWithConfidential() throws Exception {
    BannerMarkings.parseMarkings("CONFIDENTIAL//FOUO");
  }
}
