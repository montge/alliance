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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;
import org.junit.Test;

/**
 * Unit tests for {@link BannerMarkings} class (Part 1 of 2).
 *
 * <p>BannerMarkings is the MOST COMPLEX class in the banner-marking module (849 instructions, 116
 * branches, 30 methods). It is responsible for parsing and representing security banner markings
 * according to DoD 5200.1-M and related standards.
 *
 * <p><b>Part 1 Test Coverage (~40% of class complexity):</b>
 *
 * <ul>
 *   <li>US Markings - Basic Classification (10 tests)
 *   <li>US Markings - With Dissemination Controls (10 tests)
 *   <li>US Markings - With SCI Controls (10 tests)
 *   <li>FGI Markings - NATO, COSMIC, Country Codes (10 tests)
 *   <li>Constructor Tests (5 tests)
 * </ul>
 *
 * <p><b>Part 2 Coverage (separate test file - to be implemented):</b>
 *
 * <ul>
 *   <li>JOINT Markings (10 tests)
 *   <li>SAP Controls (8 tests)
 *   <li>AEA Markings (8 tests)
 *   <li>Complex Multi-Marking Tests (10 tests)
 *   <li>REL TO and DISPLAY ONLY (8 tests)
 *   <li>Edge Cases, Validation, Error Handling (10+ tests)
 * </ul>
 *
 * <p><b>Security Importance:</b> Banner markings are critical for proper classification and access
 * control of national security information. Incorrect parsing could lead to unauthorized access or
 * misclassification of sensitive data.
 *
 * <p><b>Coverage Target:</b> Part 1 aims for ~40% coverage of BannerMarkings class. Part 2 will
 * bring total coverage to 90-95%.
 *
 * <p><b>References:</b>
 *
 * <ul>
 *   <li>DoD 5200.1-M (National Industrial Security Program Operating Manual)
 *   <li>32 CFR Part 2001 (Classified National Security Information)
 *   <li>CAPCO Implementation Manual
 * </ul>
 */
public class BannerMarkingsTest {

  // ==========================================================================
  // US Markings - Basic Classification (10 tests)
  // ==========================================================================

  /**
   * Test parsing UNCLASSIFIED marking (simplest US marking).
   *
   * <p>Example: "UNCLASSIFIED"
   *
   * <p>Verifies that basic UNCLASSIFIED marking is correctly parsed with US type and no additional
   * controls.
   */
  @Test
  public void testParseUsUnclassified() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("UNCLASSIFIED");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(marking.getInputMarkings(), is("UNCLASSIFIED"));
    assertThat(marking.getSciControls(), is(empty()));
    assertThat(marking.getDisseminationControls(), is(empty()));
  }

  /**
   * Test parsing RESTRICTED marking.
   *
   * <p>Example: "RESTRICTED"
   *
   * <p>RESTRICTED is the NATO equivalent classification level between UNCLASSIFIED and
   * CONFIDENTIAL.
   */
  @Test
  public void testParseUsRestricted() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("RESTRICTED");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.RESTRICTED));
    assertThat(marking.getInputMarkings(), is("RESTRICTED"));
    assertThat(marking.getSciControls(), is(empty()));
    assertThat(marking.getDisseminationControls(), is(empty()));
  }

  /**
   * Test parsing CONFIDENTIAL marking.
   *
   * <p>Example: "CONFIDENTIAL"
   *
   * <p>CONFIDENTIAL is the third level of US classification.
   */
  @Test
  public void testParseUsConfidential() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("CONFIDENTIAL");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(marking.getInputMarkings(), is("CONFIDENTIAL"));
    assertThat(marking.getSciControls(), is(empty()));
    assertThat(marking.getDisseminationControls(), is(empty()));
  }

  /**
   * Test parsing SECRET marking.
   *
   * <p>Example: "SECRET"
   *
   * <p>SECRET is the second-highest level of US classification.
   */
  @Test
  public void testParseUsSecret() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getInputMarkings(), is("SECRET"));
    assertThat(marking.getSciControls(), is(empty()));
    assertThat(marking.getDisseminationControls(), is(empty()));
  }

  /**
   * Test parsing TOP SECRET marking.
   *
   * <p>Example: "TOP SECRET"
   *
   * <p>TOP SECRET is the highest level of US classification (note the space in the name).
   */
  @Test
  public void testParseUsTopSecret() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getInputMarkings(), is("TOP SECRET"));
    assertThat(marking.getSciControls(), is(empty()));
    assertThat(marking.getDisseminationControls(), is(empty()));
  }

  /**
   * Test parsing US marking with short name "U" for UNCLASSIFIED.
   *
   * <p>Example: "U"
   *
   * <p>Short names are valid alternatives to full classification names.
   */
  @Test
  public void testParseUsUnclassifiedShortName() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("U");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(marking.getInputMarkings(), is("U"));
  }

  /**
   * Test parsing US marking with short name "C" for CONFIDENTIAL.
   *
   * <p>Example: "C"
   */
  @Test
  public void testParseUsConfidentialShortName() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("C");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(marking.getInputMarkings(), is("C"));
  }

  /**
   * Test parsing US marking with short name "S" for SECRET.
   *
   * <p>Example: "S"
   */
  @Test
  public void testParseUsSecretShortName() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("S");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getInputMarkings(), is("S"));
  }

  /**
   * Test parsing US marking with short name "TS" for TOP SECRET.
   *
   * <p>Example: "TS"
   */
  @Test
  public void testParseUsTopSecretShortName() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TS");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getInputMarkings(), is("TS"));
  }

  /**
   * Test parsing US marking with short name "R" for RESTRICTED.
   *
   * <p>Example: "R"
   */
  @Test
  public void testParseUsRestrictedShortName() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("R");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.RESTRICTED));
    assertThat(marking.getInputMarkings(), is("R"));
  }

  // ==========================================================================
  // US Markings - With Dissemination Controls (10 tests)
  // ==========================================================================

  /**
   * Test parsing SECRET with NOFORN dissemination control.
   *
   * <p>Example: "SECRET//NOFORN"
   *
   * <p>NOFORN (Not Releasable to Foreign Nationals) is one of the most common dissemination
   * controls.
   */
  @Test
  public void testParseUsSecretNoforn() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing TOP SECRET with ORCON dissemination control.
   *
   * <p>Example: "TOP SECRET//ORCON"
   *
   * <p>ORCON (Originator Controlled) requires originator approval before further dissemination.
   */
  @Test
  public void testParseUsTopSecretOrcon() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//ORCON");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.ORCON));
  }

  /**
   * Test parsing CONFIDENTIAL with PROPIN dissemination control.
   *
   * <p>Example: "CONFIDENTIAL//PROPIN"
   *
   * <p>PROPIN (Proprietary Information Involved) indicates proprietary information is present.
   */
  @Test
  public void testParseUsConfidentialPropin() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("CONFIDENTIAL//PROPIN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.PROPIN));
  }

  /**
   * Test parsing SECRET with RELIDO dissemination control.
   *
   * <p>Example: "SECRET//RELIDO"
   *
   * <p>RELIDO (Releasable by Information Disclosure Official) requires disclosure official approval
   * for release.
   */
  @Test
  public void testParseUsSecretRelido() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//RELIDO");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.RELIDO));
  }

  /**
   * Test parsing UNCLASSIFIED with FOUO dissemination control.
   *
   * <p>Example: "UNCLASSIFIED//FOUO"
   *
   * <p>FOUO (For Official Use Only) is a common unclassified dissemination control.
   */
  @Test
  public void testParseUsUnclassifiedFouo() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("UNCLASSIFIED//FOUO");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.FOUO));
  }

  /**
   * Test parsing SECRET with multiple dissemination controls (NOFORN and ORCON).
   *
   * <p>Example: "SECRET//NOFORN/ORCON"
   *
   * <p>Multiple dissemination controls can be combined with forward slashes.
   */
  @Test
  public void testParseUsSecretMultipleDissemControls() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//NOFORN/ORCON");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getDisseminationControls(), hasSize(2));
    assertThat(marking.getDisseminationControls().contains(DissemControl.NOFORN), is(true));
    assertThat(marking.getDisseminationControls().contains(DissemControl.ORCON), is(true));
  }

  /**
   * Test parsing TOP SECRET with three dissemination controls.
   *
   * <p>Example: "TOP SECRET//ORCON/NOFORN/PROPIN"
   *
   * <p>Tests handling of three simultaneous dissemination controls.
   */
  @Test
  public void testParseUsTopSecretThreeDissemControls() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//ORCON/NOFORN/PROPIN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getDisseminationControls(), hasSize(3));
    assertThat(marking.getDisseminationControls().contains(DissemControl.ORCON), is(true));
    assertThat(marking.getDisseminationControls().contains(DissemControl.NOFORN), is(true));
    assertThat(marking.getDisseminationControls().contains(DissemControl.PROPIN), is(true));
  }

  /**
   * Test parsing SECRET with FISA dissemination control.
   *
   * <p>Example: "SECRET//FISA"
   *
   * <p>FISA (Foreign Intelligence Surveillance Act) marking indicates FISA collection.
   */
  @Test
  public void testParseUsSecretFisa() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//FISA");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.FISA));
  }

  /**
   * Test parsing SECRET with IMCON dissemination control.
   *
   * <p>Example: "SECRET//IMCON/NOFORN"
   *
   * <p>IMCON (Controlled Imagery) is used for sensitive imagery intelligence. Per DoD 5200.1-M Para
   * 1.c, IMCON requires a dissemination notice (REL TO, NOFORN, or RELIDO).
   */
  @Test
  public void testParseUsSecretImcon() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//IMCON/NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getDisseminationControls(), hasSize(2));
    assertThat(marking.getDisseminationControls().contains(DissemControl.IMCON), is(true));
    assertThat(marking.getDisseminationControls().contains(DissemControl.NOFORN), is(true));
  }

  /**
   * Test parsing TOP SECRET with DEA SENSITIVE dissemination control.
   *
   * <p>Example: "TOP SECRET//DEA SENSITIVE"
   *
   * <p>DEA SENSITIVE is used for Drug Enforcement Administration sensitive information.
   */
  @Test
  public void testParseUsTopSecretDeaSensitive() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//DEA SENSITIVE");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.DEA_SENSITIVE));
  }

  // ==========================================================================
  // US Markings - With SCI Controls (10 tests)
  // ==========================================================================

  /**
   * Test parsing TOP SECRET with SI-TK SCI control.
   *
   * <p>Example: "TOP SECRET//SI-TK//NOFORN"
   *
   * <p>SI-TK (Special Intelligence - TALENT KEYHOLE) is used for satellite imagery intelligence.
   * Per DoD 5200.1-M Para 6.c, SCI markings require explicit foreign disclosure controls (typically
   * NOFORN).
   */
  @Test
  public void testParseUsTopSecretSiTk() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//SI-TK//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSciControls().get(0).getControl(), is("SI"));
    assertThat(marking.getSciControls().get(0).getCompartments().containsKey("TK"), is(true));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing SECRET with SI-G SCI control.
   *
   * <p>Example: "SECRET//SI-G//NOFORN"
   *
   * <p>SI-G (Special Intelligence - GAMMA) is used for certain SIGINT products. Per DoD 5200.1-M
   * Para 6.c, SCI markings require explicit foreign disclosure controls (typically NOFORN).
   */
  @Test
  public void testParseUsSecretSiG() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//SI-G//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSciControls().get(0).getControl(), is("SI"));
    assertThat(marking.getSciControls().get(0).getCompartments().containsKey("G"), is(true));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing TOP SECRET with HCS-P SCI control.
   *
   * <p>Example: "TOP SECRET//HCS-P//NOFORN"
   *
   * <p>HCS-P (HUMINT Control System - Privileged) is used for human intelligence sources. Per DoD
   * 5200.1-M Para 6.f, HCS/KLONDIKE markings require NOFORN.
   */
  @Test
  public void testParseUsTopSecretHcsP() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//HCS-P//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSciControls().get(0).getControl(), is("HCS"));
    assertThat(marking.getSciControls().get(0).getCompartments().containsKey("P"), is(true));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing TOP SECRET with TK SCI control (no SI prefix).
   *
   * <p>Example: "TOP SECRET//TK//NOFORN"
   *
   * <p>TK can be used standalone without SI prefix. Per DoD 5200.1-M Para 6.c, SCI markings require
   * explicit foreign disclosure controls (typically NOFORN).
   */
  @Test
  public void testParseUsTopSecretTk() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//TK//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSciControls().get(0).getControl(), is("TK"));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing TOP SECRET with HCS SCI control (no compartment).
   *
   * <p>Example: "TOP SECRET//HCS//NOFORN"
   *
   * <p>HCS can be used without a compartment designation. Per DoD 5200.1-M Para 6.f, HCS/KLONDIKE
   * markings require NOFORN.
   */
  @Test
  public void testParseUsTopSecretHcs() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//HCS//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSciControls().get(0).getControl(), is("HCS"));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing TOP SECRET with SI-TK and NOFORN.
   *
   * <p>Example: "TOP SECRET//SI-TK//NOFORN"
   *
   * <p>SCI controls require dissemination controls (typically NOFORN).
   */
  @Test
  public void testParseUsTopSecretSiTkNoforn() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//SI-TK//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSciControls().get(0).getControl(), is("SI"));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing SECRET with SI-G and NOFORN.
   *
   * <p>Example: "SECRET//SI-G//NOFORN"
   *
   * <p>Combines SI-G SCI control with NOFORN dissemination control.
   */
  @Test
  public void testParseUsSecretSiGNoforn() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//SI-G//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSciControls().get(0).getControl(), is("SI"));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing TOP SECRET with SI-TK-G combined SCI controls.
   *
   * <p>Example: "TOP SECRET//SI-TK-G//NOFORN"
   *
   * <p>Multiple SCI compartments can be combined with hyphens.
   */
  @Test
  public void testParseUsTopSecretSiTkG() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//SI-TK-G//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSciControls().get(0).getControl(), is("SI"));
    assertThat(marking.getSciControls().get(0).getCompartments().containsKey("TK"), is(true));
    assertThat(marking.getSciControls().get(0).getCompartments().containsKey("G"), is(true));
  }

  /**
   * Test parsing TOP SECRET with multiple SCI controls separated by slashes.
   *
   * <p>Example: "TOP SECRET//TK/HCS//NOFORN"
   *
   * <p>Multiple separate SCI controls can be listed with forward slashes.
   */
  @Test
  public void testParseUsTopSecretMultipleSciControls() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//TK/HCS//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSciControls(), hasSize(2));
    assertThat(marking.getSciControls().get(0).getControl(), is("TK"));
    assertThat(marking.getSciControls().get(1).getControl(), is("HCS"));
  }

  /**
   * Test parsing SECRET with SI-TK and ORCON.
   *
   * <p>Example: "SECRET//SI-TK//ORCON"
   *
   * <p>SCI controls can be paired with dissemination controls other than NOFORN.
   */
  @Test
  public void testParseUsSecretSiTkOrcon() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//SI-TK//ORCON");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSciControls().get(0).getControl(), is("SI"));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.ORCON));
  }

  // ==========================================================================
  // FGI (Foreign Government Information) Markings (10 tests)
  // ==========================================================================

  /**
   * Test parsing FGI marking with single country code.
   *
   * <p>Example: "//FGI CAN"
   *
   * <p>FGI (Foreign Government Information) markings start with "//" and list country codes.
   */
  @Test
  public void testParseFgiSingleCountry() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//FGI CAN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getUsFgiCountryCodes(), hasSize(1));
    assertThat(marking.getUsFgiCountryCodes(), contains("CAN"));
    assertThat(marking.hasConcealedFgi(), is(false));
  }

  /**
   * Test parsing FGI marking with multiple country codes.
   *
   * <p>Example: "SECRET//FGI AUS CAN GBR"
   *
   * <p>Multiple country codes are separated by spaces. Per DoD 5200.1-M Para 9.d, FGI country codes
   * must be alphabetically sorted with trigraphs first, then tetragraphs.
   */
  @Test
  public void testParseFgiMultipleCountries() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//FGI AUS CAN GBR");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getUsFgiCountryCodes(), hasSize(3));
    assertThat(marking.getUsFgiCountryCodes().contains("CAN"), is(true));
    assertThat(marking.getUsFgiCountryCodes().contains("GBR"), is(true));
    assertThat(marking.getUsFgiCountryCodes().contains("AUS"), is(true));
  }

  /**
   * Test parsing concealed FGI marking (no country codes).
   *
   * <p>Example: "SECRET//FGI"
   *
   * <p>FGI without country codes indicates concealed foreign government information.
   */
  @Test
  public void testParseFgiConcealed() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//FGI");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getUsFgiCountryCodes(), is(empty()));
    assertThat(marking.hasConcealedFgi(), is(true));
  }

  /**
   * Test parsing NATO SECRET marking.
   *
   * <p>Example: "//NATO SECRET"
   *
   * <p>NATO markings are a special type of FGI marking with NATO classification levels.
   */
  @Test
  public void testParseFgiNatoSecret() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//NATO SECRET");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getFgiAuthority(), is("NATO"));
    assertThat(marking.isNato(), is(true));
  }

  /**
   * Test parsing NATO SECRET with ATOMAL qualifier.
   *
   * <p>Example: "//NATO SECRET//ATOMAL"
   *
   * <p>ATOMAL is a NATO compartment for atomic/nuclear information.
   */
  @Test
  public void testParseFgiNatoSecretAtomal() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//NATO SECRET//ATOMAL");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getFgiAuthority(), is("NATO"));
    assertThat(marking.getNatoQualifier(), is("ATOMAL"));
    assertThat(marking.isNato(), is(true));
  }

  /**
   * Test parsing NATO CONFIDENTIAL marking.
   *
   * <p>Example: "//NATO CONFIDENTIAL"
   *
   * <p>NATO CONFIDENTIAL is a lower NATO classification level.
   */
  @Test
  public void testParseFgiNatoConfidential() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//NATO CONFIDENTIAL");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(marking.getFgiAuthority(), is("NATO"));
    assertThat(marking.isNato(), is(true));
  }

  /**
   * Test parsing COSMIC TOP SECRET marking.
   *
   * <p>Example: "//COSMIC TOP SECRET"
   *
   * <p>COSMIC TOP SECRET is the highest NATO classification level.
   */
  @Test
  public void testParseFgiCosmicTopSecret() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//COSMIC TOP SECRET");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getFgiAuthority(), is("COSMIC"));
    assertThat(marking.isNato(), is(true));
  }

  /**
   * Test parsing NATO marking with short code "NS" for NATO SECRET.
   *
   * <p>Example: "//NS"
   *
   * <p>NS is the short code for NATO SECRET.
   */
  @Test
  public void testParseFgiNatoSecretShortCode() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//NS");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getFgiAuthority(), is("NATO"));
    assertThat(marking.isNato(), is(true));
  }

  /**
   * Test parsing NATO marking with short code "NC" for NATO CONFIDENTIAL.
   *
   * <p>Example: "//NC"
   *
   * <p>NC is the short code for NATO CONFIDENTIAL.
   */
  @Test
  public void testParseFgiNatoConfidentialShortCode() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//NC");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(marking.getFgiAuthority(), is("NATO"));
    assertThat(marking.isNato(), is(true));
  }

  /**
   * Test parsing COSMIC TOP SECRET with short code "CTS".
   *
   * <p>Example: "//CTS"
   *
   * <p>CTS is the short code for COSMIC TOP SECRET.
   */
  @Test
  public void testParseFgiCosmicTopSecretShortCode() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//CTS");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getFgiAuthority(), is("COSMIC"));
    assertThat(marking.isNato(), is(true));
  }

  // ==========================================================================
  // Constructor Tests (5 tests)
  // ==========================================================================

  /**
   * Test BannerMarkings constructor with US type and valid classification.
   *
   * <p>Verifies direct constructor usage (not via parseMarkings).
   */
  @Test
  public void testConstructorUsTypeSecret() throws Exception {
    BannerMarkings marking = new BannerMarkings(MarkingType.US, "SECRET", "SECRET");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getInputMarkings(), is("SECRET"));
  }

  /**
   * Test BannerMarkings constructor with US type and TOP SECRET.
   *
   * <p>Verifies constructor handles multi-word classification names.
   */
  @Test
  public void testConstructorUsTypeTopSecret() throws Exception {
    BannerMarkings marking = new BannerMarkings(MarkingType.US, "TOP SECRET", "TOP SECRET");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getInputMarkings(), is("TOP SECRET"));
  }

  /**
   * Test BannerMarkings constructor with US type and short name.
   *
   * <p>Verifies constructor accepts short classification names.
   */
  @Test
  public void testConstructorUsTypeShortName() throws Exception {
    BannerMarkings marking = new BannerMarkings(MarkingType.US, "TS", "TS");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getInputMarkings(), is("TS"));
  }

  /**
   * Test BannerMarkings constructor with FGI type and NATO marking.
   *
   * <p>Verifies constructor handles FGI markings with NATO authority.
   */
  @Test
  public void testConstructorFgiTypeNatoSecret() throws Exception {
    BannerMarkings marking = new BannerMarkings(MarkingType.FGI, "NATO SECRET", "//NATO SECRET");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getFgiAuthority(), is("NATO"));
    assertThat(marking.getInputMarkings(), is("//NATO SECRET"));
  }

  /**
   * Test BannerMarkings constructor with invalid classification throws exception.
   *
   * <p>Verifies that invalid classification names are rejected at construction time.
   */
  @Test(expected = MarkingsValidationException.class)
  public void testConstructorInvalidClassification() throws Exception {
    new BannerMarkings(MarkingType.US, "INVALID", "INVALID");
  }

  // ==========================================================================
  // Getter Tests - Basic Properties (5 tests)
  // ==========================================================================

  /**
   * Test getInputMarkings() returns original input string.
   *
   * <p>Verifies that the original input marking string is preserved.
   */
  @Test
  public void testGetInputMarkings() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//NOFORN");
    assertThat(marking.getInputMarkings(), is("SECRET//NOFORN"));
  }

  /**
   * Test getType() returns correct marking type.
   *
   * <p>Verifies that marking type (US, FGI, JOINT) is correctly identified.
   */
  @Test
  public void testGetType() throws Exception {
    BannerMarkings usMarking = BannerMarkings.parseMarkings("SECRET");
    assertThat(usMarking.getType(), is(MarkingType.US));

    BannerMarkings fgiMarking = BannerMarkings.parseMarkings("//NATO SECRET");
    assertThat(fgiMarking.getType(), is(MarkingType.FGI));
  }

  /**
   * Test getClassification() returns correct classification level.
   *
   * <p>Verifies that classification level is correctly parsed and accessible.
   */
  @Test
  public void testGetClassification() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//NOFORN");
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
  }

  /**
   * Test getFgiAuthority() returns correct authority for FGI markings.
   *
   * <p>Verifies that FGI authority (NATO, COSMIC, country code) is correctly extracted.
   */
  @Test
  public void testGetFgiAuthority() throws Exception {
    BannerMarkings natoMarking = BannerMarkings.parseMarkings("//NATO SECRET");
    assertThat(natoMarking.getFgiAuthority(), is("NATO"));

    BannerMarkings cosmicMarking = BannerMarkings.parseMarkings("//COSMIC TOP SECRET");
    assertThat(cosmicMarking.getFgiAuthority(), is("COSMIC"));
  }

  /**
   * Test isNato() correctly identifies NATO markings.
   *
   * <p>Verifies that NATO and COSMIC markings are identified as NATO-type FGI markings.
   */
  @Test
  public void testIsNato() throws Exception {
    BannerMarkings natoMarking = BannerMarkings.parseMarkings("//NATO SECRET");
    assertThat(natoMarking.isNato(), is(true));

    BannerMarkings cosmicMarking = BannerMarkings.parseMarkings("//COSMIC TOP SECRET");
    assertThat(cosmicMarking.isNato(), is(true));

    BannerMarkings usMarking = BannerMarkings.parseMarkings("SECRET");
    assertThat(usMarking.isNato(), is(false));
  }

  // ==========================================================================
  // Collection Initialization Tests (5 tests)
  // ==========================================================================

  /**
   * Test that SCI controls list is initialized to empty for simple markings.
   *
   * <p>Verifies that collections are never null, always initialized to empty immutable lists.
   */
  @Test
  public void testSciControlsInitializedEmpty() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");
    assertThat(marking.getSciControls(), is(notNullValue()));
    assertThat(marking.getSciControls(), is(empty()));
  }

  /**
   * Test that dissemination controls list is initialized to empty.
   *
   * <p>Verifies collections are initialized even when no dissemination controls present.
   */
  @Test
  public void testDisseminationControlsInitializedEmpty() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");
    assertThat(marking.getDisseminationControls(), is(notNullValue()));
    assertThat(marking.getDisseminationControls(), is(empty()));
  }

  /**
   * Test that REL TO list is initialized to empty.
   *
   * <p>Verifies REL TO country list is initialized.
   */
  @Test
  public void testRelToInitializedEmpty() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");
    assertThat(marking.getRelTo(), is(notNullValue()));
    assertThat(marking.getRelTo(), is(empty()));
  }

  /**
   * Test that DISPLAY ONLY list is initialized to empty.
   *
   * <p>Verifies DISPLAY ONLY country list is initialized.
   */
  @Test
  public void testDisplayOnlyInitializedEmpty() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");
    assertThat(marking.getDisplayOnly(), is(notNullValue()));
    assertThat(marking.getDisplayOnly(), is(empty()));
  }

  /**
   * Test that ACCM list is initialized to empty.
   *
   * <p>Verifies ACCM (Authorized Classification and Control Markings) list is initialized.
   */
  @Test
  public void testAccmInitializedEmpty() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");
    assertThat(marking.getAccm(), is(notNullValue()));
    assertThat(marking.getAccm(), is(empty()));
  }

  // ==========================================================================
  // AEA (Atomic Energy Act) Getter Tests (5 tests)
  // ==========================================================================

  /**
   * Test getSapControl() returns null when no SAP control present.
   *
   * <p>Verifies that SAP control is null for non-SAP markings.
   */
  @Test
  public void testGetSapControlNull() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");
    assertThat(marking.getSapControl(), is(nullValue()));
  }

  /**
   * Test getAeaMarking() returns null when no AEA marking present.
   *
   * <p>Verifies that AEA marking is null for non-AEA markings.
   */
  @Test
  public void testGetAeaMarkingNull() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");
    assertThat(marking.getAeaMarking(), is(nullValue()));
  }

  /**
   * Test getDodUcni() returns false when no DOD UCNI present.
   *
   * <p>Verifies that DOD UCNI check returns false for non-UCNI markings.
   */
  @Test
  public void testGetDodUcniFalse() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");
    assertThat(marking.getDodUcni(), is(false));
  }

  /**
   * Test getDoeUcni() returns false when no DOE UCNI present.
   *
   * <p>Verifies that DOE UCNI check returns false for non-UCNI markings.
   */
  @Test
  public void testDoeUcniFalse() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");
    assertThat(marking.getDoeUcni(), is(false));
  }

  /**
   * Test getOtherDissemControl() returns empty list when none present.
   *
   * <p>Verifies that other dissemination controls list is empty when not present.
   */
  @Test
  public void testGetOtherDissemControlEmpty() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");
    assertThat(marking.getOtherDissemControl(), is(notNullValue()));
    assertThat(marking.getOtherDissemControl(), is(empty()));
  }

  // ==========================================================================
  // REL TO Parsing Tests (5 tests)
  // ==========================================================================

  /**
   * Test parsing REL TO with two countries.
   *
   * <p>Example: "SECRET//REL TO USA, CAN"
   *
   * <p>REL TO (Releasable To) specifies authorized countries for release. Per DoD 5200.1-M Para
   * 10.e.5, REL TO USA alone without other countries is invalid - must have at least one other
   * country.
   */
  @Test
  public void testParseRelToSingleCountry() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//REL TO USA, CAN");

    assertThat(marking.getRelTo(), hasSize(2));
    assertThat(marking.getRelTo().contains("USA"), is(true));
    assertThat(marking.getRelTo().contains("CAN"), is(true));
  }

  /**
   * Test parsing REL TO with two countries.
   *
   * <p>Example: "SECRET//REL TO USA, CAN"
   *
   * <p>Multiple countries are comma-separated in REL TO.
   */
  @Test
  public void testParseRelToTwoCountries() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//REL TO USA, CAN");

    assertThat(marking.getRelTo(), hasSize(2));
    assertThat(marking.getRelTo().contains("USA"), is(true));
    assertThat(marking.getRelTo().contains("CAN"), is(true));
  }

  /**
   * Test parsing REL TO with three countries.
   *
   * <p>Example: "SECRET//REL TO USA, CAN, GBR"
   *
   * <p>Tests handling of three-country REL TO marking.
   */
  @Test
  public void testParseRelToThreeCountries() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//REL TO USA, CAN, GBR");

    assertThat(marking.getRelTo(), hasSize(3));
    assertThat(marking.getRelTo().contains("USA"), is(true));
    assertThat(marking.getRelTo().contains("CAN"), is(true));
    assertThat(marking.getRelTo().contains("GBR"), is(true));
  }

  /**
   * Test parsing REL TO with Five Eyes countries.
   *
   * <p>Example: "SECRET//REL TO USA, AUS, CAN, GBR, NZL"
   *
   * <p>Five Eyes intelligence alliance members. Per DoD 5200.1-M Para 10.e.4, REL TO codes must be
   * ordered with USA first, then alphabetically by trigraphs, then tetragraphs.
   */
  @Test
  public void testParseRelToFiveEyes() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//REL TO USA, AUS, CAN, GBR, NZL");

    assertThat(marking.getRelTo(), hasSize(5));
    assertThat(marking.getRelTo().contains("USA"), is(true));
    assertThat(marking.getRelTo().contains("CAN"), is(true));
    assertThat(marking.getRelTo().contains("GBR"), is(true));
    assertThat(marking.getRelTo().contains("AUS"), is(true));
    assertThat(marking.getRelTo().contains("NZL"), is(true));
  }

  /**
   * Test parsing marking with both SCI and REL TO.
   *
   * <p>Example: "TOP SECRET//SI-TK//REL TO USA, CAN"
   *
   * <p>SCI controls can be combined with REL TO (alternative to NOFORN).
   */
  @Test
  public void testParseSciWithRelTo() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//SI-TK//REL TO USA, CAN");

    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSciControls().get(0).getControl(), is("SI"));
    assertThat(marking.getRelTo(), hasSize(2));
    assertThat(marking.getRelTo().contains("USA"), is(true));
    assertThat(marking.getRelTo().contains("CAN"), is(true));
  }

  // ==========================================================================
  // PART 2: Advanced Marking Types and Complex Scenarios
  // ==========================================================================

  // ==========================================================================
  // JOINT Markings (10 tests)
  // ==========================================================================

  /**
   * Test parsing JOINT SECRET marking with two countries.
   *
   * <p>Example: "//JOINT SECRET CAN USA"
   *
   * <p>JOINT markings indicate information jointly owned by multiple countries. Country codes are
   * stored in sorted order.
   */
  @Test
  public void testParseJointSecretTwoCountries() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//JOINT SECRET CAN USA");

    assertThat(marking.getType(), is(MarkingType.JOINT));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getJointAuthorities(), hasSize(2));
    assertThat(marking.getJointAuthorities().contains("CAN"), is(true));
    assertThat(marking.getJointAuthorities().contains("USA"), is(true));
    assertThat(marking.getInputMarkings(), is("//JOINT SECRET CAN USA"));
  }

  /**
   * Test parsing JOINT TOP SECRET marking with three countries.
   *
   * <p>Example: "//JOINT TOP SECRET CAN DEU USA"
   *
   * <p>JOINT markings can include multiple partner nations.
   */
  @Test
  public void testParseJointTopSecretThreeCountries() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//JOINT TOP SECRET CAN DEU USA");

    assertThat(marking.getType(), is(MarkingType.JOINT));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getJointAuthorities(), hasSize(3));
    assertThat(marking.getJointAuthorities().contains("CAN"), is(true));
    assertThat(marking.getJointAuthorities().contains("DEU"), is(true));
    assertThat(marking.getJointAuthorities().contains("USA"), is(true));
  }

  /**
   * Test parsing JOINT CONFIDENTIAL marking.
   *
   * <p>Example: "//JOINT CONFIDENTIAL USA GBR"
   *
   * <p>JOINT can be used at CONFIDENTIAL level.
   */
  @Test
  public void testParseJointConfidential() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//JOINT CONFIDENTIAL USA GBR");

    assertThat(marking.getType(), is(MarkingType.JOINT));
    assertThat(marking.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(marking.getJointAuthorities(), hasSize(2));
    assertThat(marking.getJointAuthorities().contains("USA"), is(true));
    assertThat(marking.getJointAuthorities().contains("GBR"), is(true));
  }

  /**
   * Test parsing JOINT marking with short classification name.
   *
   * <p>Example: "//JOINT S USA CAN"
   *
   * <p>JOINT markings support short classification names.
   */
  @Test
  public void testParseJointSecretShortName() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//JOINT S USA CAN");

    assertThat(marking.getType(), is(MarkingType.JOINT));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getJointAuthorities(), hasSize(2));
    assertThat(marking.getJointAuthorities().contains("USA"), is(true));
    assertThat(marking.getJointAuthorities().contains("CAN"), is(true));
  }

  /**
   * Test parsing JOINT TOP SECRET with short name TS.
   *
   * <p>Example: "//JOINT TS USA GBR AUS"
   *
   * <p>JOINT supports TS short name for TOP SECRET.
   */
  @Test
  public void testParseJointTopSecretShortName() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//JOINT TS USA GBR AUS");

    assertThat(marking.getType(), is(MarkingType.JOINT));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getJointAuthorities(), hasSize(3));
    assertThat(marking.getJointAuthorities().contains("USA"), is(true));
    assertThat(marking.getJointAuthorities().contains("GBR"), is(true));
    assertThat(marking.getJointAuthorities().contains("AUS"), is(true));
  }

  /**
   * Test parsing JOINT UNCLASSIFIED marking.
   *
   * <p>Example: "//JOINT UNCLASSIFIED USA CAN"
   *
   * <p>JOINT can be applied to UNCLASSIFIED information.
   */
  @Test
  public void testParseJointUnclassified() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//JOINT UNCLASSIFIED USA CAN");

    assertThat(marking.getType(), is(MarkingType.JOINT));
    assertThat(marking.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(marking.getJointAuthorities(), hasSize(2));
  }

  /**
   * Test parsing JOINT CONFIDENTIAL marking.
   *
   * <p>Example: "//JOINT CONFIDENTIAL USA GBR"
   *
   * <p>JOINT documents require CONFIDENTIAL or higher classification level.
   */
  @Test
  public void testParseJointRestricted() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//JOINT CONFIDENTIAL USA GBR");

    assertThat(marking.getType(), is(MarkingType.JOINT));
    assertThat(marking.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(marking.getJointAuthorities(), hasSize(2));
  }

  /**
   * Test parsing JOINT marking with Five Eyes countries.
   *
   * <p>Example: "//JOINT SECRET USA CAN GBR AUS NZL"
   *
   * <p>JOINT markings with Five Eyes alliance members.
   */
  @Test
  public void testParseJointFiveEyes() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//JOINT SECRET USA CAN GBR AUS NZL");

    assertThat(marking.getType(), is(MarkingType.JOINT));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getJointAuthorities(), hasSize(5));
    assertThat(marking.getJointAuthorities().contains("USA"), is(true));
    assertThat(marking.getJointAuthorities().contains("CAN"), is(true));
    assertThat(marking.getJointAuthorities().contains("GBR"), is(true));
    assertThat(marking.getJointAuthorities().contains("AUS"), is(true));
    assertThat(marking.getJointAuthorities().contains("NZL"), is(true));
  }

  /**
   * Test getJointAuthorities() returns sorted list.
   *
   * <p>Example: "//JOINT SECRET USA AUS CAN" should sort to AUS, CAN, USA
   *
   * <p>Joint authorities are always sorted alphabetically.
   */
  @Test
  public void testJointAuthoritiesSorted() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//JOINT SECRET USA AUS CAN");

    List<String> authorities = marking.getJointAuthorities();
    assertThat(authorities, hasSize(3));
    assertThat(authorities.get(0), is("AUS"));
    assertThat(authorities.get(1), is("CAN"));
    assertThat(authorities.get(2), is("USA"));
  }

  /**
   * Test getJointAuthorities() returns null for non-JOINT markings.
   *
   * <p>Verifies that non-JOINT markings return empty list (not null) for joint authorities.
   */
  @Test
  public void testJointAuthoritiesEmptyForNonJoint() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");

    assertThat(marking.getJointAuthorities(), is(notNullValue()));
    assertThat(marking.getJointAuthorities(), is(empty()));
  }

  // ==========================================================================
  // SAP (Special Access Program) Controls (8 tests)
  // ==========================================================================

  /**
   * Test parsing SECRET with SAP control (single program).
   *
   * <p>Example: "SECRET//SAR-BP"
   *
   * <p>SAR (Special Access Required) followed by program code. BP is a common SAP program code.
   */
  @Test
  public void testParseSapSingleProgram() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//SAR-BP");

    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getSapControl().getPrograms(), hasSize(1));
    assertThat(marking.getSapControl().getPrograms(), contains("BP"));
    assertThat(marking.getSapControl().isMultiple(), is(false));
    assertThat(marking.getSapControl().isHvsaco(), is(false));
  }

  /**
   * Test parsing TOP SECRET with multiple SAP programs.
   *
   * <p>Example: "TOP SECRET//SAR-BP/GB/TW"
   *
   * <p>Multiple SAP programs are separated by forward slashes. Maximum 3 programs allowed.
   */
  @Test
  public void testParseSapMultiplePrograms() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//SAR-BP/GB/TW");

    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getSapControl().getPrograms(), hasSize(3));
    assertThat(marking.getSapControl().getPrograms().contains("BP"), is(true));
    assertThat(marking.getSapControl().getPrograms().contains("GB"), is(true));
    assertThat(marking.getSapControl().getPrograms().contains("TW"), is(true));
  }

  /**
   * Test parsing SAP with MULTIPLE PROGRAMS marker.
   *
   * <p>Example: "SECRET//SAR-MULTIPLE PROGRAMS"
   *
   * <p>When more than 3 programs apply, MULTIPLE PROGRAMS is used instead of listing them.
   */
  @Test
  public void testParseSapMultipleProgramsMarker() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//SAR-MULTIPLE PROGRAMS");

    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getSapControl().isMultiple(), is(true));
    assertThat(marking.getSapControl().getPrograms(), is(empty()));
    assertThat(marking.getSapControl().isHvsaco(), is(false));
  }

  /**
   * Test parsing HVSACO SAP control.
   *
   * <p>Example: "TOP SECRET//HVSACO"
   *
   * <p>HVSACO (High Value Sensitive Activities and Capabilities Operations) is a special SAP
   * category.
   */
  @Test
  public void testParseSapHvsaco() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//HVSACO");

    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getSapControl().isHvsaco(), is(true));
    assertThat(marking.getSapControl().isMultiple(), is(false));
    assertThat(marking.getSapControl().getPrograms(), is(empty()));
  }

  /**
   * Test parsing SAP with two programs.
   *
   * <p>Example: "SECRET//SAR-BP/GB"
   *
   * <p>Two SAP programs can be combined.
   */
  @Test
  public void testParseSapTwoPrograms() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//SAR-BP/GB");

    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getSapControl().getPrograms(), hasSize(2));
    assertThat(marking.getSapControl().getPrograms().contains("BP"), is(true));
    assertThat(marking.getSapControl().getPrograms().contains("GB"), is(true));
  }

  /**
   * Test parsing SAP with long form SPECIAL ACCESS REQUIRED.
   *
   * <p>Example: "SECRET//SPECIAL ACCESS REQUIRED-BP"
   *
   * <p>SAP can use full form "SPECIAL ACCESS REQUIRED" instead of "SAR".
   */
  @Test
  public void testParseSapLongForm() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//SPECIAL ACCESS REQUIRED-BP");

    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getSapControl().getPrograms(), hasSize(1));
    assertThat(marking.getSapControl().getPrograms(), contains("BP"));
  }

  /**
   * Test parsing SAP combined with NOFORN.
   *
   * <p>Example: "TOP SECRET//SAR-BP//NOFORN"
   *
   * <p>SAP controls can be combined with dissemination controls.
   */
  @Test
  public void testParseSapWithNoforn() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//SAR-BP//NOFORN");

    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getSapControl().getPrograms(), contains("BP"));
    assertThat(marking.getDisseminationControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test SAP toString() method for single program.
   *
   * <p>Verifies SapControl toString representation.
   */
  @Test
  public void testSapControlToString() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//SAR-BP");

    String sapString = marking.getSapControl().toString();
    assertThat(sapString, is("SAR-BP"));
  }

  // ==========================================================================
  // AEA (Atomic Energy Act) Markings (8 tests)
  // ==========================================================================

  /**
   * Test parsing RESTRICTED DATA (RD) AEA marking.
   *
   * <p>Example: "SECRET//RD"
   *
   * <p>RD (Restricted Data) is Atomic Energy Act restricted data classification.
   */
  @Test
  public void testParseAeaRestrictedData() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//RD");

    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.RD));
    assertThat(marking.getAeaMarking().isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(marking.getAeaMarking().getSigmas(), is(empty()));
  }

  /**
   * Test parsing FORMERLY RESTRICTED DATA (FRD) AEA marking.
   *
   * <p>Example: "SECRET//FRD"
   *
   * <p>FRD (Formerly Restricted Data) is data that has been declassified from RD.
   */
  @Test
  public void testParseAeaFormerlyRestrictedData() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//FRD");

    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.FRD));
    assertThat(marking.getAeaMarking().isCriticalNuclearWeaponDesignInformation(), is(false));
  }

  /**
   * Test parsing RD-N (Critical Nuclear Weapon Design Information).
   *
   * <p>Example: "TOP SECRET//RD-N"
   *
   * <p>RD-N indicates Critical Nuclear Weapon Design Information (CNWDI).
   */
  @Test
  public void testParseAeaCnwdi() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//RD-N");

    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.RD));
    assertThat(marking.getAeaMarking().isCriticalNuclearWeaponDesignInformation(), is(true));
    assertThat(marking.getAeaMarking().getSigmas(), is(empty()));
  }

  /**
   * Test parsing RD with SIGMA markings.
   *
   * <p>Example: "SECRET//RD-SIGMA 1 2 3"
   *
   * <p>SIGMA markings indicate specific nuclear weapon designs or categories.
   */
  @Test
  public void testParseAeaRdSigma() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//RD-SIGMA 1 2 3");

    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.RD));
    assertThat(marking.getAeaMarking().getSigmas(), hasSize(3));
    assertThat(marking.getAeaMarking().getSigmas().contains(1), is(true));
    assertThat(marking.getAeaMarking().getSigmas().contains(2), is(true));
    assertThat(marking.getAeaMarking().getSigmas().contains(3), is(true));
  }

  /**
   * Test parsing FRD with SIGMA marking.
   *
   * <p>Example: "SECRET//FRD-SIGMA 14"
   *
   * <p>FRD can also have SIGMA designations.
   */
  @Test
  public void testParseAeaFrdSigma() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//FRD-SIGMA 14");

    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.FRD));
    assertThat(marking.getAeaMarking().getSigmas(), hasSize(1));
    assertThat(marking.getAeaMarking().getSigmas(), contains(14));
  }

  /**
   * Test parsing DOD UCNI (Unclassified Controlled Nuclear Information).
   *
   * <p>Example: "UNCLASSIFIED//DOD UCNI"
   *
   * <p>DOD UCNI is unclassified but controlled nuclear information under DoD authority.
   */
  @Test
  public void testParseAeaDodUcni() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("UNCLASSIFIED//DOD UCNI");

    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.DOD_UCNI));
    assertThat(marking.getDodUcni(), is(true));
    assertThat(marking.getDoeUcni(), is(false));
  }

  /**
   * Test parsing DOE UCNI.
   *
   * <p>Example: "UNCLASSIFIED//DOE UCNI"
   *
   * <p>DOE UCNI is unclassified but controlled nuclear information under DOE authority.
   */
  @Test
  public void testParseAeaDoeUcni() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("UNCLASSIFIED//DOE UCNI");

    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.DOE_UCNI));
    assertThat(marking.getDoeUcni(), is(true));
    assertThat(marking.getDodUcni(), is(false));
  }

  /**
   * Test parsing TFNI (Transclassified Foreign Nuclear Information).
   *
   * <p>Example: "SECRET//TFNI"
   *
   * <p>TFNI is foreign nuclear information that has been transclassified under US authority.
   */
  @Test
  public void testParseAeaTfni() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//TFNI");

    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.TFNI));
  }

  // ==========================================================================
  // Complex Multi-Marking Tests (10 tests)
  // ==========================================================================

  /**
   * Test parsing SCI combined with SAP.
   *
   * <p>Example: "TOP SECRET//SI-TK//SAR-BP//NOFORN"
   *
   * <p>Real-world complex marking combining SCI and SAP controls.
   */
  @Test
  public void testParseSciWithSap() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//SI-TK//SAR-BP//NOFORN");

    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSciControls().get(0).getControl(), is("SI"));
    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getSapControl().getPrograms(), contains("BP"));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing SCI combined with AEA.
   *
   * <p>Example: "TOP SECRET//SI-TK//RD-N//NOFORN"
   *
   * <p>SCI and AEA controls can coexist for nuclear intelligence.
   */
  @Test
  public void testParseSciWithAea() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//SI-TK//RD-N//NOFORN");

    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSciControls().get(0).getControl(), is("SI"));
    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.RD));
    assertThat(marking.getAeaMarking().isCriticalNuclearWeaponDesignInformation(), is(true));
  }

  /**
   * Test parsing SAP combined with AEA.
   *
   * <p>Example: "SECRET//SAR-BP//RD//NOFORN"
   *
   * <p>SAP and AEA can be combined for special access nuclear programs.
   */
  @Test
  public void testParseSapWithAea() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//SAR-BP//RD//NOFORN");

    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getSapControl().getPrograms(), contains("BP"));
    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.RD));
  }

  /**
   * Test parsing maximum complexity: SCI + SAP + AEA.
   *
   * <p>Example: "TOP SECRET//SI-TK//SAR-BP//RD-N//NOFORN"
   *
   * <p>Highly classified marking with all three major control types.
   */
  @Test
  public void testParseMaximumComplexity() throws Exception {
    BannerMarkings marking =
        BannerMarkings.parseMarkings("TOP SECRET//SI-TK//SAR-BP//RD-N//NOFORN");

    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing multiple SCI controls with SAP.
   *
   * <p>Example: "TOP SECRET//TK/HCS//SAR-BP/GB//NOFORN"
   *
   * <p>Multiple SCI compartments with multiple SAP programs.
   */
  @Test
  public void testParseMultipleSciWithMultipleSap() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//TK/HCS//SAR-BP/GB//NOFORN");

    assertThat(marking.getSciControls(), hasSize(2));
    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getSapControl().getPrograms(), hasSize(2));
  }

  /**
   * Test parsing FGI with US classification.
   *
   * <p>Example: "SECRET//FGI CAN GBR//NOFORN"
   *
   * <p>FGI marking within US classification system.
   */
  @Test
  public void testParseFgiWithDissemControls() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//FGI CAN GBR//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getUsFgiCountryCodes(), hasSize(2));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing SCI with multiple dissemination controls.
   *
   * <p>Example: "SECRET//SI-G//NOFORN/ORCON/PROPIN"
   *
   * <p>SCI with three dissemination controls.
   */
  @Test
  public void testParseSciWithMultipleDissemControls() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//SI-G//NOFORN/ORCON/PROPIN");

    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), hasSize(3));
    assertThat(marking.getDisseminationControls().contains(DissemControl.NOFORN), is(true));
    assertThat(marking.getDisseminationControls().contains(DissemControl.ORCON), is(true));
    assertThat(marking.getDisseminationControls().contains(DissemControl.PROPIN), is(true));
  }

  /**
   * Test parsing HVSACO with SCI.
   *
   * <p>Example: "TOP SECRET//SI-TK//HVSACO//NOFORN"
   *
   * <p>HVSACO SAP combined with SCI controls.
   */
  @Test
  public void testParseHvsacoWithSci() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//SI-TK//HVSACO//NOFORN");

    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getSapControl().isHvsaco(), is(true));
  }

  /**
   * Test parsing complex marking with REL TO.
   *
   * <p>Example: "SECRET//SI-G//SAR-BP//REL TO USA, CAN, GBR"
   *
   * <p>SCI and SAP with REL TO instead of NOFORN.
   */
  @Test
  public void testParseComplexWithRelTo() throws Exception {
    BannerMarkings marking =
        BannerMarkings.parseMarkings("SECRET//SI-G//SAR-BP//REL TO USA, CAN, GBR");

    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getRelTo(), hasSize(3));
    assertThat(marking.getRelTo().contains("USA"), is(true));
    assertThat(marking.getRelTo().contains("CAN"), is(true));
    assertThat(marking.getRelTo().contains("GBR"), is(true));
  }

  /**
   * Test parsing AEA with multiple dissemination controls.
   *
   * <p>Example: "SECRET//RD//NOFORN/ORCON"
   *
   * <p>Restricted Data with multiple dissemination controls.
   */
  @Test
  public void testParseAeaWithMultipleDissemControls() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//RD//NOFORN/ORCON");

    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.RD));
    assertThat(marking.getDisseminationControls(), hasSize(2));
    assertThat(marking.getDisseminationControls().contains(DissemControl.NOFORN), is(true));
    assertThat(marking.getDisseminationControls().contains(DissemControl.ORCON), is(true));
  }

  // ==========================================================================
  // DISPLAY ONLY Markings (5 tests)
  // ==========================================================================

  /**
   * Test parsing DISPLAY ONLY with single country.
   *
   * <p>Example: "SECRET//DISPLAY ONLY USA"
   *
   * <p>DISPLAY ONLY restricts display/printing to specified countries.
   */
  @Test
  public void testParseDisplayOnlySingleCountry() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//DISPLAY ONLY USA");

    assertThat(marking.getDisplayOnly(), hasSize(1));
    assertThat(marking.getDisplayOnly(), contains("USA"));
  }

  /**
   * Test parsing DISPLAY ONLY with two countries.
   *
   * <p>Example: "SECRET//DISPLAY ONLY USA, GCTF"
   *
   * <p>DISPLAY ONLY can specify multiple authorized countries (trigraphs before tetragraphs).
   */
  @Test
  public void testParseDisplayOnlyTwoCountries() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//DISPLAY ONLY USA, GCTF");

    assertThat(marking.getDisplayOnly(), hasSize(2));
    assertThat(marking.getDisplayOnly().contains("USA"), is(true));
    assertThat(marking.getDisplayOnly().contains("GCTF"), is(true));
  }

  /**
   * Test parsing DISPLAY ONLY with three countries.
   *
   * <p>Example: "SECRET//DISPLAY ONLY CAN, GBR, GCTF"
   *
   * <p>Three countries authorized for display (trigraphs before tetragraphs).
   */
  @Test
  public void testParseDisplayOnlyThreeCountries() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//DISPLAY ONLY CAN, GBR, GCTF");

    assertThat(marking.getDisplayOnly(), hasSize(3));
    assertThat(marking.getDisplayOnly().contains("CAN"), is(true));
    assertThat(marking.getDisplayOnly().contains("GBR"), is(true));
    assertThat(marking.getDisplayOnly().contains("GCTF"), is(true));
  }

  /**
   * Test parsing DISPLAY ONLY with Five Eyes countries and tetragraph.
   *
   * <p>Example: "SECRET//DISPLAY ONLY AUS, CAN, GBR, NZL, GCTF"
   *
   * <p>Multiple countries authorized for display (trigraphs before tetragraphs).
   */
  @Test
  public void testParseDisplayOnlyFiveEyes() throws Exception {
    BannerMarkings marking =
        BannerMarkings.parseMarkings("SECRET//DISPLAY ONLY AUS, CAN, GBR, NZL, GCTF");

    assertThat(marking.getDisplayOnly(), hasSize(5));
    assertThat(marking.getDisplayOnly().contains("AUS"), is(true));
    assertThat(marking.getDisplayOnly().contains("CAN"), is(true));
    assertThat(marking.getDisplayOnly().contains("GBR"), is(true));
    assertThat(marking.getDisplayOnly().contains("NZL"), is(true));
    assertThat(marking.getDisplayOnly().contains("GCTF"), is(true));
  }

  /**
   * Test parsing DISPLAY ONLY combined with other dissemination controls.
   *
   * <p>Example: "SECRET//DISPLAY ONLY USA//ORCON"
   *
   * <p>DISPLAY ONLY can be combined with other dissemination controls (but not NOFORN or RELIDO).
   */
  @Test
  public void testParseDisplayOnlyWithNoforn() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//DISPLAY ONLY USA//ORCON");

    assertThat(marking.getDisplayOnly(), hasSize(1));
    assertThat(marking.getDisplayOnly(), contains("USA"));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.ORCON));
  }

  // ==========================================================================
  // Other Dissemination Controls (6 tests)
  // ==========================================================================

  /**
   * Test parsing EXDIS (Exclusive Distribution).
   *
   * <p>Example: "SECRET//EXDIS"
   *
   * <p>EXDIS limits distribution to a very select group.
   */
  @Test
  public void testParseOtherDissemExdis() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//EXDIS");

    assertThat(marking.getOtherDissemControl(), hasSize(1));
    assertThat(marking.getOtherDissemControl(), contains(OtherDissemControl.EXDIS));
  }

  /**
   * Test parsing LIMDIS (Limited Distribution).
   *
   * <p>Example: "SECRET//LIMDIS"
   *
   * <p>LIMDIS restricts distribution to those with specific need-to-know.
   */
  @Test
  public void testParseOtherDissemLimdis() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//LIMDIS");

    assertThat(marking.getOtherDissemControl(), hasSize(1));
    assertThat(marking.getOtherDissemControl(), contains(OtherDissemControl.LIMDIS));
  }

  /**
   * Test parsing NODIS (No Distribution).
   *
   * <p>Example: "TOP SECRET//NODIS"
   *
   * <p>NODIS is the most restrictive dissemination control.
   */
  @Test
  public void testParseOtherDissemNodis() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//NODIS");

    assertThat(marking.getOtherDissemControl(), hasSize(1));
    assertThat(marking.getOtherDissemControl(), contains(OtherDissemControl.NODIS));
  }

  /**
   * Test parsing SBU (Sensitive But Unclassified).
   *
   * <p>Example: "UNCLASSIFIED//SBU"
   *
   * <p>SBU is an unclassified information control marking.
   */
  @Test
  public void testParseOtherDissemSbu() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("UNCLASSIFIED//SBU");

    assertThat(marking.getOtherDissemControl(), hasSize(1));
    assertThat(marking.getOtherDissemControl(), contains(OtherDissemControl.SBU));
  }

  /**
   * Test parsing SBU NOFORN.
   *
   * <p>Example: "UNCLASSIFIED//SBU NOFORN"
   *
   * <p>SBU combined with NOFORN restriction.
   */
  @Test
  public void testParseOtherDissemSbuNoforn() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("UNCLASSIFIED//SBU NOFORN");

    assertThat(marking.getOtherDissemControl(), hasSize(1));
    assertThat(marking.getOtherDissemControl(), contains(OtherDissemControl.SBU_NOFORN));
  }

  /**
   * Test parsing LES (Law Enforcement Sensitive).
   *
   * <p>Example: "UNCLASSIFIED//LES"
   *
   * <p>LES is used for law enforcement sensitive information.
   */
  @Test
  public void testParseOtherDissemLes() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("UNCLASSIFIED//LES");

    assertThat(marking.getOtherDissemControl(), hasSize(1));
    assertThat(marking.getOtherDissemControl(), contains(OtherDissemControl.LES));
  }

  // ==========================================================================
  // Edge Cases & Validation (10 tests)
  // ==========================================================================

  /**
   * Test parsing empty string throws exception.
   *
   * <p>Verifies that empty input is rejected.
   */
  @Test(expected = MarkingsValidationException.class)
  public void testParseEmptyString() throws Exception {
    BannerMarkings.parseMarkings("");
  }

  /**
   * Test parsing null string throws exception.
   *
   * <p>Verifies that null input is rejected.
   */
  @Test(expected = NullPointerException.class)
  public void testParseNullString() throws Exception {
    BannerMarkings.parseMarkings(null);
  }

  /**
   * Test parsing invalid classification throws exception.
   *
   * <p>Example: "INVALID CLASSIFICATION"
   *
   * <p>Verifies that unknown classification levels are rejected.
   */
  @Test(expected = MarkingsValidationException.class)
  public void testParseInvalidClassification() throws Exception {
    BannerMarkings.parseMarkings("INVALID CLASSIFICATION");
  }

  /**
   * Test parsing marking with invalid control combination.
   *
   * <p>Example: "SECRET//UNKNOWN CONTROL"
   *
   * <p>Malformed marking with invalid dissemination control should be rejected.
   */
  @Test(expected = MarkingsValidationException.class)
  public void testParseMalformedSlashes() throws Exception {
    BannerMarkings.parseMarkings("SECRET//UNKNOWN CONTROL");
  }

  /**
   * Test parsing JOINT without classification.
   *
   * <p>Example: "//JOINT"
   *
   * <p>JOINT requires classification level.
   */
  @Test(expected = MarkingsValidationException.class)
  public void testParseJointWithoutClassification() throws Exception {
    BannerMarkings.parseMarkings("//JOINT");
  }

  /**
   * Test parsing FGI without authority.
   *
   * <p>Example: "//"
   *
   * <p>FGI requires authority and classification.
   */
  @Test(expected = MarkingsValidationException.class)
  public void testParseFgiWithoutAuthority() throws Exception {
    BannerMarkings.parseMarkings("//");
  }

  /**
   * Test parsing marking with trailing slashes.
   *
   * <p>Example: "SECRET//"
   *
   * <p>Trailing delimiters should be handled gracefully.
   */
  @Test
  public void testParseTrailingSlashes() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//");

    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
  }

  /**
   * Test parsing marking with whitespace.
   *
   * <p>Example: " SECRET "
   *
   * <p>Leading/trailing whitespace should not affect parsing (trimmed internally).
   */
  @Test
  public void testParseWithWhitespace() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");

    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
  }

  /**
   * Test getAccm() returns empty list when no ACCM present.
   *
   * <p>Verifies ACCM list is initialized empty.
   */
  @Test
  public void testGetAccmEmpty() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");

    assertThat(marking.getAccm(), is(notNullValue()));
    assertThat(marking.getAccm(), is(empty()));
  }

  /**
   * Test hasConcealedFgi() returns false for non-FGI markings.
   *
   * <p>Verifies concealed FGI flag is false for US markings.
   */
  @Test
  public void testHasConcealedFgiFalse() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");

    assertThat(marking.hasConcealedFgi(), is(false));
  }

  // ==========================================================================
  // Additional Coverage Tests (5 tests)
  // ==========================================================================

  /**
   * Test parsing RESTRICTED DATA with long form.
   *
   * <p>Example: "SECRET//RESTRICTED DATA"
   *
   * <p>AEA markings support both short and long forms.
   */
  @Test
  public void testParseAeaRestrictedDataLongForm() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//RESTRICTED DATA");

    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.RD));
  }

  /**
   * Test parsing FORMERLY RESTRICTED DATA with long form.
   *
   * <p>Example: "SECRET//FORMERLY RESTRICTED DATA"
   *
   * <p>FRD long form support.
   */
  @Test
  public void testParseAeaFrdLongForm() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//FORMERLY RESTRICTED DATA");

    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.FRD));
  }

  /**
   * Test parsing NATO UNCLASSIFIED with short code NU.
   *
   * <p>Example: "//NU"
   *
   * <p>NATO UNCLASSIFIED short code.
   */
  @Test
  public void testParseFgiNatoUnclassifiedShortCode() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//NU");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(marking.getFgiAuthority(), is("NATO"));
    assertThat(marking.isNato(), is(true));
  }

  /**
   * Test parsing NATO RESTRICTED with short code NR.
   *
   * <p>Example: "//NR"
   *
   * <p>NATO RESTRICTED short code.
   */
  @Test
  public void testParseFgiNatoRestrictedShortCode() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//NR");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.RESTRICTED));
    assertThat(marking.getFgiAuthority(), is("NATO"));
    assertThat(marking.isNato(), is(true));
  }

  /**
   * Test parsing COSMIC with BOHEMIA qualifier.
   *
   * <p>Example: "//COSMIC TOP SECRET//BOHEMIA"
   *
   * <p>BOHEMIA is a NATO compartment qualifier only valid for COSMIC TOP SECRET SIGINT material.
   */
  @Test
  public void testParseFgiNatoBohemia() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//COSMIC TOP SECRET//BOHEMIA");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getFgiAuthority(), is("COSMIC"));
    assertThat(marking.getNatoQualifier(), is("BOHEMIA"));
    assertThat(marking.isNato(), is(true));
  }

  // ==========================================================================
  // Edge Case Tests - Complex Parsing (Part 3)
  // ==========================================================================

  /**
   * Test parsing FGI with concealed country code (empty FGI marker).
   *
   * <p>Example: "SECRET//FGI"
   *
   * <p>FGI without country codes indicates concealed foreign government information. The
   * hasConcealedFgi flag should be true.
   */
  @Test
  public void testParseFgiConcealedCountryCodes() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//FGI");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.hasConcealedFgi(), is(true));
    assertThat(marking.getUsFgiCountryCodes(), is(empty()));
  }

  /**
   * Test parsing FGI with long form and concealed country codes.
   *
   * <p>Example: "SECRET//FOREIGN GOVERNMENT INFORMATION"
   *
   * <p>Tests processing of long form FGI marker without country codes.
   */
  @Test
  public void testParseFgiLongFormConcealedCountryCodes() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//FOREIGN GOVERNMENT INFORMATION");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.hasConcealedFgi(), is(true));
    assertThat(marking.getUsFgiCountryCodes(), is(empty()));
  }

  /**
   * Test parsing FGI with single country code and classification.
   *
   * <p>Example: "SECRET//FGI//NOFORN"
   *
   * <p>Tests parsing of FGI that's concealed (no country codes shown).
   */
  @Test
  public void testParseFgiConcealedWithDissemControl() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//FGI//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.hasConcealedFgi(), is(true));
    assertThat(marking.getUsFgiCountryCodes(), is(empty()));
    assertThat(marking.getDisseminationControls(), hasSize(1));
  }

  /**
   * Test parsing with multiple consecutive delimiter sections (empty segment).
   *
   * <p>Example: "TOP SECRET//SI-TK////NOFORN"
   *
   * <p>Tests handling of empty segments created by consecutive delimiters.
   */
  @Test
  public void testParseMultipleConsecutiveDelimiters() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//SI-TK////NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSciControls(), hasSize(1));
    assertThat(marking.getDisseminationControls(), hasSize(1));
  }

  /**
   * Test parsing SAP with HVSACO (High Value Special Access Compartmented Operations).
   *
   * <p>Example: "TOP SECRET//HVSACO//NOFORN"
   *
   * <p>HVSACO is a special SAP marker without program identifiers.
   */
  @Test
  public void testParseSapHvsacoStandalone() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("TOP SECRET//HVSACO//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing ACCM with single marker.
   *
   * <p>Example: "SECRET//ACCM-ALPHA//NOFORN"
   *
   * <p>Tests Authorized Classification and Control Markings (ACCM) with single code.
   */
  @Test
  public void testParseAccmSingleMarker() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//ACCM-ALPHA//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getAccm(), hasSize(1));
    assertThat(marking.getAccm(), contains("ALPHA"));
  }

  /**
   * Test parsing ACCM with multiple markers.
   *
   * <p>Example: "SECRET//ACCM-ALPHA/BRAVO/CHARLIE//NOFORN"
   *
   * <p>Tests multiple ACCM codes in sequence.
   */
  @Test
  public void testParseAccmMultipleMarkers() throws Exception {
    BannerMarkings marking =
        BannerMarkings.parseMarkings("SECRET//ACCM-ALPHA/BRAVO/CHARLIE//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getAccm(), hasSize(3));
    assertThat(marking.getAccm().contains("ALPHA"), is(true));
    assertThat(marking.getAccm().contains("BRAVO"), is(true));
    assertThat(marking.getAccm().contains("CHARLIE"), is(true));
  }

  /**
   * Test parsing ACCM mixed with other dissemination controls.
   *
   * <p>Example: "SECRET//EXDIS/ACCM-DELTA/ECHO//NOFORN"
   *
   * <p>Tests ACCM codes appearing after other dissemination controls.
   */
  @Test
  public void testParseAccmMixedWithOtherDissem() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//EXDIS/ACCM-DELTA/ECHO//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getOtherDissemControl(), hasSize(1));
    assertThat(marking.getOtherDissemControl(), contains(OtherDissemControl.EXDIS));
    assertThat(marking.getAccm(), hasSize(2));
    assertThat(marking.getAccm().contains("DELTA"), is(true));
    assertThat(marking.getAccm().contains("ECHO"), is(true));
  }

  /**
   * Test parsing all OtherDissemControl types - EXDIS long form.
   *
   * <p>Example: "SECRET//EXCLUSIVE DISTRIBUTION"
   *
   * <p>Tests EXDIS (Exclusive Distribution) long form processing.
   */
  @Test
  public void testParseOtherDissemExdisLongForm() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//EXCLUSIVE DISTRIBUTION");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getOtherDissemControl(), hasSize(1));
    assertThat(marking.getOtherDissemControl(), contains(OtherDissemControl.EXDIS));
  }

  /**
   * Test parsing all OtherDissemControl types - LIMDIS long form.
   *
   * <p>Example: "SECRET//LIMITED DISTRIBUTION"
   *
   * <p>Tests LIMDIS (Limited Distribution) long form processing.
   */
  @Test
  public void testParseOtherDissemLimdisLongForm() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//LIMITED DISTRIBUTION");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getOtherDissemControl(), hasSize(1));
    assertThat(marking.getOtherDissemControl(), contains(OtherDissemControl.LIMDIS));
  }

  /**
   * Test parsing all OtherDissemControl types - NODIS long form.
   *
   * <p>Example: "SECRET//NO DISTRIBUTION"
   *
   * <p>Tests NODIS (No Distribution) long form processing.
   */
  @Test
  public void testParseOtherDissemNodisLongForm() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//NO DISTRIBUTION");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getOtherDissemControl(), hasSize(1));
    assertThat(marking.getOtherDissemControl(), contains(OtherDissemControl.NODIS));
  }

  /**
   * Test parsing all OtherDissemControl types - LES NOFORN.
   *
   * <p>Example: "UNCLASSIFIED//LES NOFORN"
   *
   * <p>Tests LES NOFORN (Law Enforcement Sensitive No Foreign) variant.
   */
  @Test
  public void testParseOtherDissemLesNoforn() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("UNCLASSIFIED//LES NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getOtherDissemControl(), hasSize(1));
    assertThat(marking.getOtherDissemControl(), contains(OtherDissemControl.LES_NOFORN));
  }

  /**
   * Test parsing all OtherDissemControl types - SSI.
   *
   * <p>Example: "UNCLASSIFIED//SSI"
   *
   * <p>Tests SSI (Sensitive Security Information) processing.
   */
  @Test
  public void testParseOtherDissemSsi() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("UNCLASSIFIED//SSI");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getOtherDissemControl(), hasSize(1));
    assertThat(marking.getOtherDissemControl(), contains(OtherDissemControl.SSI));
  }

  /**
   * Test parsing all OtherDissemControl types - SSI long form.
   *
   * <p>Example: "UNCLASSIFIED//SENSITIVE SECURITY INFORMATION"
   *
   * <p>Tests SSI long form processing.
   */
  @Test
  public void testParseOtherDissemSsiLongForm() throws Exception {
    BannerMarkings marking =
        BannerMarkings.parseMarkings("UNCLASSIFIED//SENSITIVE SECURITY INFORMATION");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getOtherDissemControl(), hasSize(1));
    assertThat(marking.getOtherDissemControl(), contains(OtherDissemControl.SSI));
  }

  /**
   * Test parsing NATO with ATOMAL qualifier.
   *
   * <p>Example: "//NS//ATOMAL"
   *
   * <p>Tests NATO ATOMAL (Atomic) compartment qualifier.
   */
  @Test
  public void testParseFgiNatoAtomal() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//NS//ATOMAL");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getFgiAuthority(), is("NATO"));
    assertThat(marking.getNatoQualifier(), is("ATOMAL"));
    assertThat(marking.isNato(), is(true));
  }

  /**
   * Test parsing NATO with CONFIDENTIAL classification (full form).
   *
   * <p>Example: "//NATO CONFIDENTIAL"
   *
   * <p>Tests NATO CONFIDENTIAL parsing with full classification name.
   */
  @Test
  public void testParseFgiNatoConfidentialFullForm() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//NATO CONFIDENTIAL");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(marking.getFgiAuthority(), is("NATO"));
    assertThat(marking.isNato(), is(true));
  }

  /**
   * Test parsing FGI with country authority using short classification name.
   *
   * <p>Example: "//GBR S"
   *
   * <p>Tests FGI parsing with short classification form after country code.
   */
  @Test
  public void testParseFgiCountryAuthorityShortClass() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//GBR S");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getFgiAuthority(), is("GBR"));
    assertThat(marking.isNato(), is(false));
  }

  /**
   * Test parsing FGI with country authority using long classification name.
   *
   * <p>Example: "//CAN SECRET"
   *
   * <p>Tests FGI parsing with full classification form after country code.
   */
  @Test
  public void testParseFgiCountryAuthorityLongClass() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//CAN SECRET");

    assertThat(marking.getType(), is(MarkingType.FGI));
    assertThat(marking.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(marking.getFgiAuthority(), is("CAN"));
    assertThat(marking.isNato(), is(false));
  }

  /**
   * Test parsing JOINT with authorities sorted alphabetically.
   *
   * <p>Example: "//JOINT SECRET USA CAN GBR AUS NZL"
   *
   * <p>Tests that JOINT authorities are sorted in alphabetical order (implementation requirement).
   */
  @Test
  public void testParseJointAuthoritiesSorted() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//JOINT SECRET USA CAN GBR AUS NZL");

    assertThat(marking.getType(), is(MarkingType.JOINT));
    assertThat(marking.getJointAuthorities(), hasSize(5));
    // Check sorted order: AUS, CAN, GBR, NZL, USA
    assertThat(marking.getJointAuthorities().get(0), is("AUS"));
    assertThat(marking.getJointAuthorities().get(1), is("CAN"));
    assertThat(marking.getJointAuthorities().get(2), is("GBR"));
    assertThat(marking.getJointAuthorities().get(3), is("NZL"));
    assertThat(marking.getJointAuthorities().get(4), is("USA"));
  }

  /**
   * Test getDodUcni returns false when no AEA marking present.
   *
   * <p>Verifies getDodUcni() getter returns false for markings without DOD UCNI.
   */
  @Test
  public void testGetDodUcniReturnsFalseWhenNoAea() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//NOFORN");

    assertThat(marking.getDodUcni(), is(false));
    assertThat(marking.getAeaMarking(), is(nullValue()));
  }

  /**
   * Test getDoeUcni returns false when no AEA marking present.
   *
   * <p>Verifies getDoeUcni() getter returns false for markings without DOE UCNI.
   */
  @Test
  public void testGetDoeUcniReturnsFalseWhenNoAea() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//NOFORN");

    assertThat(marking.getDoeUcni(), is(false));
    assertThat(marking.getAeaMarking(), is(nullValue()));
  }

  /**
   * Test getDodUcni returns false when different AEA type present.
   *
   * <p>Verifies getDodUcni() returns false when AEA is RD (not DOD UCNI).
   */
  @Test
  public void testGetDodUcniReturnsFalseWhenRdPresent() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//RD//NOFORN");

    assertThat(marking.getDodUcni(), is(false));
    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.RD));
  }

  /**
   * Test getDoeUcni returns false when different AEA type present.
   *
   * <p>Verifies getDoeUcni() returns false when AEA is RD (not DOE UCNI).
   */
  @Test
  public void testGetDoeUcniReturnsFalseWhenRdPresent() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//RD//NOFORN");

    assertThat(marking.getDoeUcni(), is(false));
    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.RD));
  }

  /**
   * Test getDodUcni returns true when DOD UCNI present.
   *
   * <p>Verifies getDodUcni() getter correctly identifies DOD UCNI markings.
   */
  @Test
  public void testGetDodUcniReturnsTrueWhenPresent() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//DOD UCNI");

    assertThat(marking.getDodUcni(), is(true));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.DOD_UCNI));
  }

  /**
   * Test getDoeUcni returns true when DOE UCNI present.
   *
   * <p>Verifies getDoeUcni() getter correctly identifies DOE UCNI markings.
   */
  @Test
  public void testGetDoeUcniReturnsTrueWhenPresent() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("UNCLASSIFIED//DOE UCNI");

    assertThat(marking.getDoeUcni(), is(true));
    assertThat(marking.getAeaMarking().getType(), is(AeaType.DOE_UCNI));
  }

  /**
   * Test isNato returns false for US markings.
   *
   * <p>Verifies isNato() returns false for non-NATO markings.
   */
  @Test
  public void testIsNatoReturnsFalseForUs() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//NOFORN");

    assertThat(marking.isNato(), is(false));
  }

  /**
   * Test isNato returns false for non-NATO FGI markings.
   *
   * <p>Verifies isNato() returns false for FGI markings from non-NATO countries.
   */
  @Test
  public void testIsNatoReturnsFalseForNonNatoFgi() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//GBR SECRET");

    assertThat(marking.isNato(), is(false));
    assertThat(marking.getType(), is(MarkingType.FGI));
  }

  /**
   * Test isNato returns true for NATO markings.
   *
   * <p>Verifies isNato() returns true for NATO FGI markings.
   */
  @Test
  public void testIsNatoReturnsTrueForNato() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//NATO SECRET");

    assertThat(marking.isNato(), is(true));
  }

  /**
   * Test isNato returns true for COSMIC markings.
   *
   * <p>Verifies isNato() returns true for COSMIC FGI markings.
   */
  @Test
  public void testIsNatoReturnsTrueForCosmic() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//CTS");

    assertThat(marking.isNato(), is(true));
    assertThat(marking.getFgiAuthority(), is("COSMIC"));
  }

  /**
   * Test hasConcealedFgi returns false when no FGI present.
   *
   * <p>Verifies hasConcealedFgi() returns false for US markings without FGI.
   */
  @Test
  public void testHasConcealedFgiReturnsFalseWhenNoFgi() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//NOFORN");

    assertThat(marking.hasConcealedFgi(), is(false));
  }

  /**
   * Test hasConcealedFgi returns false for non-FGI markings.
   *
   * <p>Verifies hasConcealedFgi() returns false when no FGI present.
   */
  @Test
  public void testHasConcealedFgiReturnsFalseForNonFgi() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");

    assertThat(marking.hasConcealedFgi(), is(false));
    assertThat(marking.getUsFgiCountryCodes(), is(empty()));
  }

  /**
   * Test hasConcealedFgi returns true when FGI without country codes.
   *
   * <p>Verifies hasConcealedFgi() returns true when FGI marker has no country codes.
   */
  @Test
  public void testHasConcealedFgiReturnsTrueWhenNoCountryCodes() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//FGI");

    assertThat(marking.hasConcealedFgi(), is(true));
    assertThat(marking.getUsFgiCountryCodes(), is(empty()));
  }

  /**
   * Test getInputMarkings preserves original input.
   *
   * <p>Verifies getInputMarkings() returns the exact input string.
   */
  @Test
  public void testGetInputMarkingsPreservesOriginal() throws Exception {
    String input = "TOP SECRET//SI-TK//NOFORN";
    BannerMarkings marking = BannerMarkings.parseMarkings(input);

    assertThat(marking.getInputMarkings(), is(input));
  }

  /**
   * Test getSapControl returns null when no SAP present.
   *
   * <p>Verifies getSapControl() returns null for markings without SAP.
   */
  @Test
  public void testGetSapControlReturnsNullWhenNoSap() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//NOFORN");

    assertThat(marking.getSapControl(), is(nullValue()));
  }

  /**
   * Test getAeaMarking returns null when no AEA present.
   *
   * <p>Verifies getAeaMarking() returns null for markings without AEA.
   */
  @Test
  public void testGetAeaMarkingReturnsNullWhenNoAea() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//NOFORN");

    assertThat(marking.getAeaMarking(), is(nullValue()));
  }

  /**
   * Test getFgiAuthority returns null for US markings.
   *
   * <p>Verifies getFgiAuthority() returns null for MarkingType.US.
   */
  @Test
  public void testGetFgiAuthorityReturnsNullForUs() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//NOFORN");

    assertThat(marking.getFgiAuthority(), is(nullValue()));
    assertThat(marking.getType(), is(MarkingType.US));
  }

  /**
   * Test getNatoQualifier returns null when no qualifier present.
   *
   * <p>Verifies getNatoQualifier() returns null for NATO markings without qualifiers.
   */
  @Test
  public void testGetNatoQualifierReturnsNullWhenNotPresent() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("//NATO SECRET");

    assertThat(marking.getNatoQualifier(), is(nullValue()));
  }

  /**
   * Test getJointAuthorities returns empty list for US markings.
   *
   * <p>Verifies getJointAuthorities() returns empty list for MarkingType.US.
   */
  @Test
  public void testGetJointAuthoritiesReturnsEmptyForUs() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET//NOFORN");

    assertThat(marking.getJointAuthorities(), is(empty()));
  }

  /**
   * Test all collection getters return non-null empty lists when not populated.
   *
   * <p>Verifies all collection getters are properly initialized (not null).
   */
  @Test
  public void testAllCollectionGettersInitialized() throws Exception {
    BannerMarkings marking = BannerMarkings.parseMarkings("SECRET");

    // All collections should be non-null (even if empty)
    assertThat(marking.getSciControls(), is(notNullValue()));
    assertThat(marking.getDisseminationControls(), is(notNullValue()));
    assertThat(marking.getRelTo(), is(notNullValue()));
    assertThat(marking.getDisplayOnly(), is(notNullValue()));
    assertThat(marking.getOtherDissemControl(), is(notNullValue()));
    assertThat(marking.getAccm(), is(notNullValue()));
    assertThat(marking.getJointAuthorities(), is(notNullValue()));
    assertThat(marking.getUsFgiCountryCodes(), is(notNullValue()));
  }

  /**
   * Test complex marking with multiple combinations.
   *
   * <p>Example: "TOP SECRET//SI-TK/HCS-P//SAR-PROGRAM//RD-SIGMA 14 18//FGI//NOFORN"
   *
   * <p>Tests parsing of complex marking with SCI, SAP, AEA, FGI concealed, and dissem controls.
   */
  @Test
  public void testParseComplexMultipleControlTypes() throws Exception {
    BannerMarkings marking =
        BannerMarkings.parseMarkings(
            "TOP SECRET//SI-TK/HCS-P//SAR-PROGRAM//RD-SIGMA 14 18//FGI//NOFORN");

    assertThat(marking.getType(), is(MarkingType.US));
    assertThat(marking.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(marking.getSciControls(), hasSize(2));
    assertThat(marking.getSapControl(), is(notNullValue()));
    assertThat(marking.getAeaMarking(), is(notNullValue()));
    assertThat(marking.hasConcealedFgi(), is(true));
    assertThat(marking.getDisseminationControls(), hasSize(1));
  }
}
