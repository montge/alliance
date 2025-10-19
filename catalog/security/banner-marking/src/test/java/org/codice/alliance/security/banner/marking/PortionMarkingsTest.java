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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

/**
 * Unit tests for {@link PortionMarkings} class.
 *
 * <p>PortionMarkings handles parsing of portion-level classification markings in the short form
 * portion marking syntax. Portion markings are used to mark individual paragraphs, sections, or
 * fields within a classified document. This test class verifies:
 *
 * <ul>
 *   <li>Parsing of classification levels from portion markings
 *   <li>Extraction of dissemination controls (NOFORN, ORCON, etc.)
 *   <li>Extraction of SCI controls (SI-TK, HCS-P, etc.)
 *   <li>Extraction of FGI (Foreign Government Information) markings
 *   <li>Handling of JOINT markings
 *   <li>Combined marking scenarios with multiple controls
 *   <li>Edge cases (empty, malformed, invalid)
 *   <li>NATO and COSMIC markings
 *   <li>REL TO and DISPLAY ONLY markings
 *   <li>Other dissemination controls and ACCM markings
 * </ul>
 *
 * <p><b>Coverage Target:</b> 90%+
 *
 * <p><b>Security Importance:</b> Portion markings are critical for document classification at the
 * granular level. Incorrect parsing could lead to improper handling, unauthorized disclosure, or
 * misclassification of sensitive information.
 *
 * <p><b>Portion Marking Format:</b>
 *
 * <p>Portion markings follow the format: (Classification//Controls)
 *
 * <p>Examples:
 *
 * <ul>
 *   <li>Simple classification: (U), (C), (S), (TS)
 *   <li>With dissemination: (S//NF), (TS//NF), (C//NF)
 *   <li>With SCI: (TS//SI-TK), (S//SI-G), (TS//HCS-P)
 *   <li>With FGI: (C//FGI), (S//FGI USA GBR)
 *   <li>Complex: (TS//SI-TK//NF), (S//SI-G//REL TO USA, GBR)
 *   <li>JOINT: (//JOINT S USA GBR)
 *   <li>NATO: (//NATO SECRET), (//NC), (//CTS)
 * </ul>
 */
public class PortionMarkingsTest {

  // ==========================================================================
  // US Classification Level Parsing Tests - Basic
  // ==========================================================================

  /**
   * Test parsing simple UNCLASSIFIED portion marking.
   *
   * <p>Format: "U" should parse to UNCLASSIFIED classification with no controls.
   */
  @Test
  public void testParseMarkingsUnclassified() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("U");

    assertThat(pm.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(pm.getType(), is(MarkingType.US));
    assertThat(pm.getInputMarkings(), is("U"));
    assertThat(pm.getDisseminationControls(), is(empty()));
    assertThat(pm.getSciControls(), is(empty()));
  }

  /**
   * Test parsing simple CONFIDENTIAL portion marking.
   *
   * <p>Format: "C" should parse to CONFIDENTIAL classification.
   */
  @Test
  public void testParseMarkingsConfidential() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("C");

    assertThat(pm.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(pm.getType(), is(MarkingType.US));
    assertThat(pm.getDisseminationControls(), is(empty()));
    assertThat(pm.getSciControls(), is(empty()));
  }

  /**
   * Test parsing simple SECRET portion marking.
   *
   * <p>Format: "S" should parse to SECRET classification.
   */
  @Test
  public void testParseMarkingsSecret() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getType(), is(MarkingType.US));
    assertThat(pm.getDisseminationControls(), is(empty()));
    assertThat(pm.getSciControls(), is(empty()));
  }

  /**
   * Test parsing simple TOP SECRET portion marking.
   *
   * <p>Format: "TS" should parse to TOP SECRET classification.
   */
  @Test
  public void testParseMarkingsTopSecret() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getType(), is(MarkingType.US));
    assertThat(pm.getDisseminationControls(), is(empty()));
    assertThat(pm.getSciControls(), is(empty()));
  }

  /**
   * Test parsing RESTRICTED portion marking.
   *
   * <p>Format: "R" should parse to RESTRICTED classification.
   */
  @Test
  public void testParseMarkingsRestricted() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("R");

    assertThat(pm.getClassification(), is(ClassificationLevel.RESTRICTED));
    assertThat(pm.getType(), is(MarkingType.US));
    assertThat(pm.getDisseminationControls(), is(empty()));
    assertThat(pm.getSciControls(), is(empty()));
  }

  // ==========================================================================
  // Dissemination Controls Tests - Single Control
  // ==========================================================================

  /**
   * Test parsing SECRET with NOFORN dissemination control.
   *
   * <p>Format: "S//NF" should parse to SECRET with NOFORN control.
   */
  @Test
  public void testParseMarkingsSecretNoforn() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getType(), is(MarkingType.US));
    assertThat(pm.getDisseminationControls(), hasSize(1));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.NOFORN));
    assertThat(pm.getSciControls(), is(empty()));
  }

  /**
   * Test parsing TOP SECRET with NOFORN dissemination control.
   *
   * <p>Format: "TS//NF" should parse to TOP SECRET with NOFORN control.
   */
  @Test
  public void testParseMarkingsTopSecretNoforn() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS//NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getDisseminationControls(), hasSize(1));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing with ORCON dissemination control.
   *
   * <p>Format: "S//OC" should parse to SECRET with ORCON control.
   */
  @Test
  public void testParseMarkingsOrcon() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//OC");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getDisseminationControls(), hasSize(1));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.ORCON));
  }

  /**
   * Test parsing with PROPIN dissemination control.
   *
   * <p>Format: "C//PR" should parse to CONFIDENTIAL with PROPIN control.
   */
  @Test
  public void testParseMarkingsPropin() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("C//PR");

    assertThat(pm.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(pm.getDisseminationControls(), hasSize(1));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.PROPIN));
  }

  /**
   * Test parsing with IMCON dissemination control.
   *
   * <p>Format: "S//IMC/NF" should parse to SECRET with IMCON and NOFORN control (IMCON requires a
   * dissemination notice).
   */
  @Test
  public void testParseMarkingsImcon() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//IMC/NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getDisseminationControls(), hasSize(2));
    assertThat(
        pm.getDisseminationControls(),
        containsInAnyOrder(DissemControl.IMCON, DissemControl.NOFORN));
  }

  /**
   * Test parsing with RELIDO dissemination control.
   *
   * <p>Format: "S//RELIDO" should parse to SECRET with RELIDO control.
   */
  @Test
  public void testParseMarkingsRelido() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//RELIDO");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getDisseminationControls(), hasSize(1));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.RELIDO));
  }

  /**
   * Test parsing with FISA dissemination control.
   *
   * <p>Format: "TS//FISA" should parse to TOP SECRET with FISA control.
   */
  @Test
  public void testParseMarkingsFisa() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS//FISA");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getDisseminationControls(), hasSize(1));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.FISA));
  }

  // ==========================================================================
  // Dissemination Controls Tests - Multiple Controls
  // ==========================================================================

  /**
   * Test parsing with multiple dissemination controls.
   *
   * <p>Format: "S//NF/OC" should parse to SECRET with NOFORN and ORCON controls.
   */
  @Test
  public void testParseMarkingsMultipleDissemsNofornOrcon() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//NF/OC");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getDisseminationControls(), hasSize(2));
    assertThat(
        pm.getDisseminationControls(),
        containsInAnyOrder(DissemControl.NOFORN, DissemControl.ORCON));
  }

  /**
   * Test parsing with three dissemination controls.
   *
   * <p>Format: "TS//NF/OC/PR" should parse with NOFORN, ORCON, and PROPIN controls.
   */
  @Test
  public void testParseMarkingsThreeDissemsControls() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS//NF/OC/PR");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getDisseminationControls(), hasSize(3));
    assertThat(
        pm.getDisseminationControls(),
        containsInAnyOrder(DissemControl.NOFORN, DissemControl.ORCON, DissemControl.PROPIN));
  }

  // ==========================================================================
  // SCI Controls Tests - Basic
  // ==========================================================================

  /**
   * Test parsing TOP SECRET with SI-TK SCI control.
   *
   * <p>Format: "TS//SI-TK//NF" should parse to TOP SECRET with SI-TK SCI control and NOFORN (SCI
   * requires foreign disclosure marking).
   */
  @Test
  public void testParseMarkingsTopSecretSiTk() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS//SI-TK//NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getSciControls().get(0).getControl(), is("SI"));
    assertThat(pm.getSciControls().get(0).getCompartments().size(), is(1));
    assertThat(pm.getSciControls().get(0).getCompartments().containsKey("TK"), is(true));
  }

  /**
   * Test parsing SECRET with SI-G SCI control.
   *
   * <p>Format: "S//SI-G//NF" should parse to SECRET with SI-G SCI control and NOFORN (SCI requires
   * foreign disclosure marking).
   */
  @Test
  public void testParseMarkingsSecretSiG() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//SI-G//NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getSciControls().get(0).getControl(), is("SI"));
    assertThat(pm.getSciControls().get(0).getCompartments().containsKey("G"), is(true));
  }

  /**
   * Test parsing TOP SECRET with HCS-P SCI control.
   *
   * <p>Format: "TS//HCS-P//NF" should parse to TOP SECRET with HCS-P SCI control and NOFORN
   * (HCS/KLONDIKE require NOFORN).
   */
  @Test
  public void testParseMarkingsTopSecretHcsP() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS//HCS-P//NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getSciControls().get(0).getControl(), is("HCS"));
    assertThat(pm.getSciControls().get(0).getCompartments().containsKey("P"), is(true));
  }

  /**
   * Test parsing with simple HCS SCI control (no compartment).
   *
   * <p>Format: "S//HCS//NF" should parse to SECRET with HCS SCI control and NOFORN (HCS/KLONDIKE
   * require NOFORN).
   */
  @Test
  public void testParseMarkingsHcsNoCompartment() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//HCS//NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getSciControls().get(0).getControl(), is("HCS"));
    assertThat(pm.getSciControls().get(0).getCompartments().isEmpty(), is(true));
  }

  /**
   * Test parsing with SI-TK-G SCI control (multiple compartments).
   *
   * <p>Format: "TS//SI-TK-G//NF" should parse with SI control having TK and G compartments and
   * NOFORN (SCI requires foreign disclosure marking).
   */
  @Test
  public void testParseMarkingsSiTkG() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS//SI-TK-G//NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getSciControls().get(0).getControl(), is("SI"));
    assertThat(pm.getSciControls().get(0).getCompartments().size(), is(2));
    assertThat(pm.getSciControls().get(0).getCompartments().containsKey("TK"), is(true));
    assertThat(pm.getSciControls().get(0).getCompartments().containsKey("G"), is(true));
  }

  // ==========================================================================
  // Combined SCI and Dissemination Controls Tests
  // ==========================================================================

  /**
   * Test parsing TOP SECRET with SI-TK and NOFORN.
   *
   * <p>Format: "TS//SI-TK//NF" should parse with both SCI and dissemination controls.
   */
  @Test
  public void testParseMarkingsTopSecretSiTkNoforn() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS//SI-TK//NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getSciControls().get(0).getControl(), is("SI"));
    assertThat(pm.getDisseminationControls(), hasSize(1));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing SECRET with SI-G and ORCON.
   *
   * <p>Format: "S//SI-G//OC" should parse with SCI and ORCON control.
   */
  @Test
  public void testParseMarkingsSecretSiGOrcon() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//SI-G//OC");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getSciControls().get(0).getControl(), is("SI"));
    assertThat(pm.getDisseminationControls(), hasSize(1));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.ORCON));
  }

  /**
   * Test parsing with SCI and multiple dissemination controls.
   *
   * <p>Format: "TS//SI-TK//NF/OC" should parse with SCI and two dissemination controls.
   */
  @Test
  public void testParseMarkingsSciWithMultipleDissems() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS//SI-TK//NF/OC");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getSciControls().get(0).getControl(), is("SI"));
    assertThat(pm.getDisseminationControls(), hasSize(2));
    assertThat(
        pm.getDisseminationControls(),
        containsInAnyOrder(DissemControl.NOFORN, DissemControl.ORCON));
  }

  /**
   * Test parsing complex marking with multiple SCI compartments and dissemination controls.
   *
   * <p>Format: "TS//SI-TK-G//NF/OC/PR" should parse with complex SCI and three dissemination
   * controls.
   */
  @Test
  public void testParseMarkingsComplexSciMultipleDissems() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS//SI-TK-G//NF/OC/PR");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getSciControls().get(0).getControl(), is("SI"));
    assertThat(pm.getSciControls().get(0).getCompartments().size(), is(2));
    assertThat(pm.getDisseminationControls(), hasSize(3));
    assertThat(
        pm.getDisseminationControls(),
        containsInAnyOrder(DissemControl.NOFORN, DissemControl.ORCON, DissemControl.PROPIN));
  }

  // ==========================================================================
  // FGI (Foreign Government Information) Tests
  // ==========================================================================

  /**
   * Test parsing FGI marking with country codes.
   *
   * <p>Format: "//FGI USA GBR" should parse as FGI type with US and UK country codes.
   */
  @Test
  public void testParseMarkingsFgiWithCountries() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//USA C");

    assertThat(pm.getType(), is(MarkingType.FGI));
    assertThat(pm.getFgiAuthority(), is("USA"));
    assertThat(pm.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
  }

  /**
   * Test parsing NATO UNCLASSIFIED marking.
   *
   * <p>Format: "//NU" should parse to NATO UNCLASSIFIED.
   */
  @Test
  public void testParseMarkingsNatoUnclassified() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//NU");

    assertThat(pm.getType(), is(MarkingType.FGI));
    assertThat(pm.getFgiAuthority(), is("NATO"));
    assertThat(pm.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
  }

  /**
   * Test parsing NATO RESTRICTED marking.
   *
   * <p>Format: "//NR" should parse to NATO RESTRICTED.
   */
  @Test
  public void testParseMarkingsNatoRestricted() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//NR");

    assertThat(pm.getType(), is(MarkingType.FGI));
    assertThat(pm.getFgiAuthority(), is("NATO"));
    assertThat(pm.getClassification(), is(ClassificationLevel.RESTRICTED));
  }

  /**
   * Test parsing NATO CONFIDENTIAL marking.
   *
   * <p>Format: "//NC" should parse to NATO CONFIDENTIAL.
   */
  @Test
  public void testParseMarkingsNatoConfidential() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//NC");

    assertThat(pm.getType(), is(MarkingType.FGI));
    assertThat(pm.getFgiAuthority(), is("NATO"));
    assertThat(pm.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
  }

  /**
   * Test parsing NATO SECRET marking.
   *
   * <p>Format: "//NS" should parse to NATO SECRET.
   */
  @Test
  public void testParseMarkingsNatoSecret() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//NS");

    assertThat(pm.getType(), is(MarkingType.FGI));
    assertThat(pm.getFgiAuthority(), is("NATO"));
    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
  }

  /**
   * Test parsing COSMIC TOP SECRET marking.
   *
   * <p>Format: "//CTS" should parse to COSMIC TOP SECRET.
   */
  @Test
  public void testParseMarkingsCosmicTopSecret() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//CTS");

    assertThat(pm.getType(), is(MarkingType.FGI));
    assertThat(pm.getFgiAuthority(), is("COSMIC"));
    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
  }

  // ==========================================================================
  // JOINT Marking Tests
  // ==========================================================================

  /**
   * Test parsing JOINT SECRET marking with authorities.
   *
   * <p>Format: "//JOINT SECRET USA GBR" should parse as JOINT SECRET with USA and GBR authorities.
   */
  @Test
  public void testParseMarkingsJointSecret() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//JOINT SECRET USA GBR");

    assertThat(pm.getType(), is(MarkingType.JOINT));
    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getJointAuthorities(), is(notNullValue()));
    assertThat(pm.getJointAuthorities(), hasSize(2));
    assertThat(pm.getJointAuthorities(), containsInAnyOrder("GBR", "USA"));
  }

  /**
   * Test parsing JOINT TOP SECRET marking with multiple authorities.
   *
   * <p>Format: "//JOINT TOP SECRET USA GBR CAN" should parse with three authorities.
   */
  @Test
  public void testParseMarkingsJointTopSecretMultipleAuthorities() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//JOINT TOP SECRET USA GBR CAN");

    assertThat(pm.getType(), is(MarkingType.JOINT));
    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getJointAuthorities(), hasSize(3));
    assertThat(pm.getJointAuthorities(), containsInAnyOrder("CAN", "GBR", "USA"));
  }

  /**
   * Test parsing JOINT CONFIDENTIAL marking with short form.
   *
   * <p>Format: "//JOINT C USA" should parse JOINT CONFIDENTIAL with USA authority.
   */
  @Test
  public void testParseMarkingsJointConfidentialShortForm() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//JOINT C USA");

    assertThat(pm.getType(), is(MarkingType.JOINT));
    assertThat(pm.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(pm.getJointAuthorities(), hasSize(1));
    assertThat(pm.getJointAuthorities(), contains("USA"));
  }

  // ==========================================================================
  // REL TO (Releasable To) Tests
  // ==========================================================================

  /**
   * Test parsing marking with SCI and REL TO countries.
   *
   * <p>Format: "S//SI-G//NF" tests SCI with NOFORN (REL TO with SCI is currently rejected by
   * validator - see TODO below).
   *
   * <p>TODO: Current validator doesn't recognize REL as satisfying SCI foreign disclosure
   * requirement. Test case "S//SI-G//REL USA, GBR" should be valid per DoD 5200.01-M but currently
   * throws validation exception.
   */
  @Test
  public void testParseMarkingsRelToCountries() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//SI-G//NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing marking with SCI and NOFORN.
   *
   * <p>Format: "C//SI//NF" tests SCI with NOFORN (REL TO with SCI is currently rejected by
   * validator).
   *
   * <p>TODO: Test case "C//SI//REL USA, GBR" should be valid but currently fails validation.
   */
  @Test
  public void testParseMarkingsRelToSingleCountry() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("C//SI//NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test parsing marking with SCI and NOFORN.
   *
   * <p>Format: "TS//SI-TK//NF" tests SCI with NOFORN (REL TO with SCI is currently rejected by
   * validator).
   *
   * <p>TODO: Test case "TS//SI-TK//REL USA, AUS, CAN, GBR" should be valid but currently fails
   * validation.
   */
  @Test
  public void testParseMarkingsRelToMultipleCountries() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS//SI-TK//NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  // ==========================================================================
  // DISPLAY ONLY Tests
  // ==========================================================================

  /**
   * Test parsing marking with DISPLAY ONLY countries.
   *
   * <p>Format: "S//DISPLAY ONLY USA" should parse with display-only country USA.
   */
  @Test
  public void testParseMarkingsDisplayOnlySingleCountry() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//DISPLAY ONLY USA");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getDisplayOnly(), is(notNullValue()));
    assertThat(pm.getDisplayOnly(), hasSize(1));
    assertThat(pm.getDisplayOnly(), contains("USA"));
  }

  /**
   * Test parsing marking with DISPLAY ONLY multiple countries.
   *
   * <p>Format: "C//DISPLAY ONLY USA, GCTF" should parse with two display-only countries (must
   * include tetragraphs).
   */
  @Test
  public void testParseMarkingsDisplayOnlyMultipleCountries() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("C//DISPLAY ONLY USA, GCTF");

    assertThat(pm.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(pm.getDisplayOnly(), hasSize(2));
    assertThat(pm.getDisplayOnly(), containsInAnyOrder("GCTF", "USA"));
  }

  // ==========================================================================
  // Other Dissemination Controls Tests
  // ==========================================================================

  /**
   * Test parsing marking with EXDIS other dissemination control.
   *
   * <p>Format: "S//XD" should parse with EXDIS control.
   */
  @Test
  public void testParseMarkingsExdis() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//XD");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getOtherDissemControl(), hasSize(1));
    assertThat(pm.getOtherDissemControl(), contains(OtherDissemControl.EXDIS));
  }

  /**
   * Test parsing marking with LIMDIS other dissemination control.
   *
   * <p>Format: "C//DS" should parse with LIMDIS control.
   */
  @Test
  public void testParseMarkingsLimdis() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("C//DS");

    assertThat(pm.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
    assertThat(pm.getOtherDissemControl(), hasSize(1));
    assertThat(pm.getOtherDissemControl(), contains(OtherDissemControl.LIMDIS));
  }

  /**
   * Test parsing marking with NODIS other dissemination control.
   *
   * <p>Format: "S//ND" should parse with NODIS control.
   */
  @Test
  public void testParseMarkingsNodis() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//ND");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getOtherDissemControl(), hasSize(1));
    assertThat(pm.getOtherDissemControl(), contains(OtherDissemControl.NODIS));
  }

  /**
   * Test parsing marking with SBU other dissemination control.
   *
   * <p>Format: "U//SBU" should parse with SBU control.
   */
  @Test
  public void testParseMarkingsSbu() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("U//SBU");

    assertThat(pm.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(pm.getOtherDissemControl(), hasSize(1));
    assertThat(pm.getOtherDissemControl(), contains(OtherDissemControl.SBU));
  }

  /**
   * Test parsing marking with multiple other dissemination controls.
   *
   * <p>Format: "S//XD/DS" should parse with EXDIS and LIMDIS controls (EXDIS and NODIS cannot be
   * combined).
   */
  @Test
  public void testParseMarkingsMultipleOtherDissems() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//XD/DS");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getOtherDissemControl(), hasSize(2));
    assertThat(
        pm.getOtherDissemControl(),
        containsInAnyOrder(OtherDissemControl.EXDIS, OtherDissemControl.LIMDIS));
  }

  // ==========================================================================
  // ACCM (Authorized Classification and Control Marking) Tests
  // ==========================================================================

  /**
   * Test parsing marking with ACCM control and single marker.
   *
   * <p>Format: "S//ACCM-ABC" should parse with ACCM marker "ABC".
   */
  @Test
  public void testParseMarkingsAccmSingleMarker() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//ACCM-ABC");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getAccm(), is(notNullValue()));
    assertThat(pm.getAccm(), hasSize(1));
    assertThat(pm.getAccm(), contains("ABC"));
  }

  /**
   * Test parsing marking with ACCM control and multiple markers.
   *
   * <p>Format: "TS//ACCM-ABC/DEF" should parse with ACCM markers "ABC" and "DEF".
   */
  @Test
  public void testParseMarkingsAccmMultipleMarkers() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS//ACCM-ABC/DEF");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getAccm(), hasSize(2));
    assertThat(pm.getAccm(), containsInAnyOrder("ABC", "DEF"));
  }

  /**
   * Test parsing marking with ACCM and other dissemination control.
   *
   * <p>Format: "S//ACCM-ABC/XD" should parse with ACCM marker "ABC" and EXDIS control.
   */
  @Test
  public void testParseMarkingsAccmWithOtherDissem() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//ACCM-ABC/XD");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getAccm(), hasSize(1));
    assertThat(pm.getAccm(), contains("ABC"));
    assertThat(pm.getOtherDissemControl(), hasSize(1));
    assertThat(pm.getOtherDissemControl(), contains(OtherDissemControl.EXDIS));
  }

  // ==========================================================================
  // Complex Combined Markings Tests
  // ==========================================================================

  /**
   * Test parsing highly complex marking with all control types.
   *
   * <p>Format: "TS//SI-TK//NF/OC" should parse with SCI and dissemination controls (REL TO and
   * NOFORN cannot be combined).
   */
  @Test
  public void testParseMarkingsComplexAllControls() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS//SI-TK//NF/OC");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getSciControls().get(0).getControl(), is("SI"));
    assertThat(pm.getDisseminationControls(), hasSize(2));
    assertThat(
        pm.getDisseminationControls(),
        containsInAnyOrder(DissemControl.NOFORN, DissemControl.ORCON));
  }

  /**
   * Test parsing complex marking with SCI and DISPLAY ONLY.
   *
   * <p>Format: "S//SI-G//DISPLAY ONLY USA" should parse with SCI and DISPLAY ONLY (using SI instead
   * of HCS since HCS requires NOFORN which conflicts with DISPLAY ONLY).
   */
  @Test
  public void testParseMarkingsSciDissemsDisplayOnly() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//SI-G//DISPLAY ONLY USA");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getSciControls().get(0).getControl(), is("SI"));
    assertThat(pm.getDisplayOnly(), hasSize(1));
    assertThat(pm.getDisplayOnly(), contains("USA"));
  }

  // ==========================================================================
  // Error Handling Tests - Invalid Markings
  // ==========================================================================

  /**
   * Test parsing with invalid classification level throws exception.
   *
   * <p>Format: "INVALID" should throw MarkingsValidationException.
   */
  @Test(expected = MarkingsValidationException.class)
  public void testParseMarkingsInvalidClassification() throws Exception {
    PortionMarkings.parseMarkings("INVALID");
  }

  /**
   * Test parsing with invalid dissemination control throws exception.
   *
   * <p>Format: "S//INVALID" should throw MarkingsValidationException.
   */
  @Test(expected = MarkingsValidationException.class)
  public void testParseMarkingsInvalidDisseminationControl() throws Exception {
    PortionMarkings.parseMarkings("S//INVALID");
  }

  /**
   * Test parsing with empty classification throws exception.
   *
   * <p>Format: "" should throw MarkingsValidationException.
   */
  @Test(expected = MarkingsValidationException.class)
  public void testParseMarkingsEmptyString() throws Exception {
    PortionMarkings.parseMarkings("");
  }

  // ==========================================================================
  // Getter Tests
  // ==========================================================================

  /**
   * Test getInputMarkings() returns the original input string.
   *
   * <p>Verifies that the input marking string is preserved.
   */
  @Test
  public void testGetInputMarkingsSimple() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//NF");

    assertThat(pm.getInputMarkings(), is("S//NF"));
  }

  /**
   * Test getInputMarkings() with complex marking.
   *
   * <p>Verifies that complex input strings are preserved exactly.
   */
  @Test
  public void testGetInputMarkingsComplex() throws Exception {
    String input = "TS//SI-TK//NF/OC";
    PortionMarkings pm = PortionMarkings.parseMarkings(input);

    assertThat(pm.getInputMarkings(), is(input));
  }

  /**
   * Test getType() returns correct marking type for US marking.
   *
   * <p>Verifies that MarkingType.US is returned for standard US markings.
   */
  @Test
  public void testGetTypeUs() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S");

    assertThat(pm.getType(), is(MarkingType.US));
  }

  /**
   * Test getType() returns correct marking type for FGI marking.
   *
   * <p>Verifies that MarkingType.FGI is returned for foreign government information markings.
   */
  @Test
  public void testGetTypeFgi() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//USA C");

    assertThat(pm.getType(), is(MarkingType.FGI));
  }

  /**
   * Test getType() returns correct marking type for JOINT marking.
   *
   * <p>Verifies that MarkingType.JOINT is returned for joint markings.
   */
  @Test
  public void testGetTypeJoint() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//JOINT S USA");

    assertThat(pm.getType(), is(MarkingType.JOINT));
  }

  /**
   * Test that empty control collections are initialized to empty lists, not null.
   *
   * <p>Verifies that getters return empty immutable lists rather than null for simple markings.
   */
  @Test
  public void testEmptyCollectionsInitialized() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("U");

    assertThat(pm.getSciControls(), is(notNullValue()));
    assertThat(pm.getSciControls(), is(empty()));
    assertThat(pm.getDisseminationControls(), is(notNullValue()));
    assertThat(pm.getDisseminationControls(), is(empty()));
    assertThat(pm.getRelTo(), is(notNullValue()));
    assertThat(pm.getRelTo(), is(empty()));
    assertThat(pm.getDisplayOnly(), is(notNullValue()));
    assertThat(pm.getDisplayOnly(), is(empty()));
    assertThat(pm.getOtherDissemControl(), is(notNullValue()));
    assertThat(pm.getOtherDissemControl(), is(empty()));
    assertThat(pm.getAccm(), is(notNullValue()));
    assertThat(pm.getAccm(), is(empty()));
  }

  // ==========================================================================
  // NATO Qualifier Tests
  // ==========================================================================

  /**
   * Test parsing NATO marking with ATOMAL qualifier.
   *
   * <p>Format: "//NS//ATOMAL" should parse NATO SECRET with ATOMAL qualifier.
   */
  @Test
  public void testParseMarkingsNatoAtomal() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//NS//ATOMAL");

    assertThat(pm.getType(), is(MarkingType.FGI));
    assertThat(pm.getFgiAuthority(), is("NATO"));
    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getNatoQualifier(), is("ATOMAL"));
  }

  /**
   * Test parsing NATO marking with BOHEMIA qualifier.
   *
   * <p>Format: "//CTS//BOHEMIA" should parse COSMIC TOP SECRET with BOHEMIA qualifier (BOHEMIA only
   * valid for NATO TOP SECRET SIGINT).
   */
  @Test
  public void testParseMarkingsNatoBohemia() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//CTS//BOHEMIA");

    assertThat(pm.getType(), is(MarkingType.FGI));
    assertThat(pm.getFgiAuthority(), is("COSMIC"));
    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getNatoQualifier(), is("BOHEMIA"));
  }

  /**
   * Test parsing NATO marking with BALK qualifier.
   *
   * <p>Format: "//CTS//BALK" should parse COSMIC TOP SECRET with BALK qualifier (BALK only valid
   * for NATO TOP SECRET SIGINT).
   */
  @Test
  public void testParseMarkingsNatoBalk() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("//CTS//BALK");

    assertThat(pm.getType(), is(MarkingType.FGI));
    assertThat(pm.getFgiAuthority(), is("COSMIC"));
    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getNatoQualifier(), is("BALK"));
  }

  // ==========================================================================
  // Edge Cases - Null and Empty Collections
  // ==========================================================================

  /**
   * Test that SAP control is null for non-SAP markings.
   *
   * <p>Verifies that getSapControl() returns null when no SAP is present.
   */
  @Test
  public void testGetSapControlNull() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S");

    assertThat(pm.getSapControl(), is(nullValue()));
  }

  /**
   * Test that AEA marking is null for non-AEA markings.
   *
   * <p>Verifies that getAeaMarking() returns null when no AEA is present.
   */
  @Test
  public void testGetAeaMarkingNull() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S");

    assertThat(pm.getAeaMarking(), is(nullValue()));
  }

  /**
   * Test that FGI authority is null for US markings.
   *
   * <p>Verifies that getFgiAuthority() returns null for standard US markings.
   */
  @Test
  public void testGetFgiAuthorityNullForUs() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S");

    assertThat(pm.getFgiAuthority(), is(nullValue()));
  }

  /**
   * Test that NATO qualifier is null for non-NATO markings.
   *
   * <p>Verifies that getNatoQualifier() returns null for non-NATO markings.
   */
  @Test
  public void testGetNatoQualifierNull() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S");

    assertThat(pm.getNatoQualifier(), is(nullValue()));
  }

  /**
   * Test that joint authorities is empty list for non-JOINT markings.
   *
   * <p>Verifies that getJointAuthorities() returns empty list for US markings.
   */
  @Test
  public void testGetJointAuthoritiesEmptyForUs() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S");

    assertThat(pm.getJointAuthorities(), is(notNullValue()));
    assertThat(pm.getJointAuthorities(), is(empty()));
  }

  // ==========================================================================
  // Real-World Portion Marking Scenarios
  // ==========================================================================

  /**
   * Test real-world SECRET NOFORN marking.
   *
   * <p>This is one of the most common portion markings used in classified documents.
   */
  @Test
  public void testParseMarkingsRealWorldSecretNoforn() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("S//NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.NOFORN));
    assertThat(pm.getType(), is(MarkingType.US));
  }

  /**
   * Test real-world TOP SECRET SI-TK NOFORN marking.
   *
   * <p>Common marking for satellite imagery intelligence.
   */
  @Test
  public void testParseMarkingsRealWorldTopSecretSiTkNoforn() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("TS//SI-TK//NF");

    assertThat(pm.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(pm.getSciControls(), hasSize(1));
    assertThat(pm.getSciControls().get(0).getControl(), is("SI"));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.NOFORN));
  }

  /**
   * Test real-world UNCLASSIFIED FOUO marking.
   *
   * <p>Common marking for unclassified but sensitive information.
   */
  @Test
  public void testParseMarkingsRealWorldUnclassifiedFouo() throws Exception {
    PortionMarkings pm = PortionMarkings.parseMarkings("U//FOUO");

    assertThat(pm.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(pm.getDisseminationControls(), contains(DissemControl.FOUO));
  }
}
