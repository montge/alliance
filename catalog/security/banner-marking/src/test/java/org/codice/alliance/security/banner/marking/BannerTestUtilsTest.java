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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import org.junit.Test;

/**
 * Unit tests for {@link BannerTestUtils}.
 *
 * <p>Tests the utility methods for creating common test banner markings, SAP controls, and AEA
 * markings. These utilities simplify test creation and ensure consistent test data across the
 * banner-marking module.
 *
 * <p><b>Coverage Improvement:</b> New test file providing comprehensive validation of utility
 * factory methods, ensuring they produce correct banner markings and supporting objects.
 *
 * @see BannerTestUtils
 */
public class BannerTestUtilsTest {

  // ==========================================================================
  // US Banner Marking Tests
  // ==========================================================================

  @Test
  public void testCreateUsTopSecretBanner() throws Exception {
    BannerMarkings banner = BannerTestUtils.createUsTopSecretBanner();
    assertThat(banner, is(notNullValue()));
    assertThat(banner.getClassification(), is(ClassificationLevel.TOP_SECRET));
  }

  @Test
  public void testCreateUsSecretBanner() throws Exception {
    BannerMarkings banner = BannerTestUtils.createUsSecretBanner();
    assertThat(banner, is(notNullValue()));
    assertThat(banner.getClassification(), is(ClassificationLevel.SECRET));
  }

  @Test
  public void testCreateUsConfidentialBanner() throws Exception {
    BannerMarkings banner = BannerTestUtils.createUsConfidentialBanner();
    assertThat(banner, is(notNullValue()));
    assertThat(banner.getClassification(), is(ClassificationLevel.CONFIDENTIAL));
  }

  @Test
  public void testCreateUsRestrictedBanner() throws Exception {
    BannerMarkings banner = BannerTestUtils.createUsRestrictedBanner();
    assertThat(banner, is(notNullValue()));
    assertThat(banner.getClassification(), is(ClassificationLevel.RESTRICTED));
  }

  @Test
  public void testCreateUsUnclassifiedBanner() throws Exception {
    BannerMarkings banner = BannerTestUtils.createUsUnclassifiedBanner();
    assertThat(banner, is(notNullValue()));
    assertThat(banner.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
  }

  @Test
  public void testCreateUsSecretNoforn() throws Exception {
    BannerMarkings banner = BannerTestUtils.createUsSecretNoforn();
    assertThat(banner, is(notNullValue()));
    assertThat(banner.getClassification(), is(ClassificationLevel.SECRET));
    assertThat(banner.getDisseminationControls(), hasSize(1));
    assertThat(
        banner.getDisseminationControls().iterator().next(), is(DissemControl.NOFORN));
  }

  @Test
  public void testCreateUsTopSecretOrcon() throws Exception {
    BannerMarkings banner = BannerTestUtils.createUsTopSecretOrcon();
    assertThat(banner, is(notNullValue()));
    assertThat(banner.getClassification(), is(ClassificationLevel.TOP_SECRET));
    assertThat(banner.getDisseminationControls(), hasSize(1));
    assertThat(
        banner.getDisseminationControls().iterator().next(), is(DissemControl.ORCON));
  }

  @Test
  public void testCreateUsUnclassifiedFouo() throws Exception {
    BannerMarkings banner = BannerTestUtils.createUsUnclassifiedFouo();
    assertThat(banner, is(notNullValue()));
    assertThat(banner.getClassification(), is(ClassificationLevel.UNCLASSIFIED));
    assertThat(banner.getDisseminationControls(), hasSize(1));
    assertThat(banner.getDisseminationControls().iterator().next(), is(DissemControl.FOUO));
  }

  // ==========================================================================
  // SAP Control Factory Method Tests
  // ==========================================================================

  @Test
  public void testCreateSapControlSingleProgram() {
    SapControl sapControl = BannerTestUtils.createSapControl("BP");
    assertThat(sapControl, is(notNullValue()));
    assertThat(sapControl.getPrograms(), hasSize(1));
    assertThat(sapControl.getPrograms().get(0), is("BP"));
    assertThat(sapControl.isMultiple(), is(false));
    assertThat(sapControl.isHvsaco(), is(false));
  }

  @Test
  public void testCreateSapControlMultiplePrograms() {
    SapControl sapControl = BannerTestUtils.createSapControlMultiplePrograms("BP", "GB", "TC");
    assertThat(sapControl, is(notNullValue()));
    assertThat(sapControl.getPrograms(), hasSize(3));
    assertThat(sapControl.getPrograms(), containsInAnyOrder("BP", "GB", "TC"));
    assertThat(sapControl.isMultiple(), is(false));
    assertThat(sapControl.isHvsaco(), is(false));
  }

  @Test
  public void testCreateSapControlMultipleProgramsIndicator() {
    SapControl sapControl = BannerTestUtils.createSapControlMultipleProgramsIndicator();
    assertThat(sapControl, is(notNullValue()));
    assertThat(sapControl.isMultiple(), is(true));
    assertThat(sapControl.getPrograms(), hasSize(0));
    assertThat(sapControl.isHvsaco(), is(false));
  }

  @Test
  public void testCreateHvsacoControl() {
    SapControl sapControl = BannerTestUtils.createHvsacoControl();
    assertThat(sapControl, is(notNullValue()));
    assertThat(sapControl.isHvsaco(), is(true));
    assertThat(sapControl.isMultiple(), is(false));
    assertThat(sapControl.getPrograms(), hasSize(0));
  }

  // ==========================================================================
  // AEA Marking Factory Method Tests
  // ==========================================================================

  @Test
  public void testCreateRdMarking() {
    AeaMarking aeaMarking = BannerTestUtils.createRdMarking();
    assertThat(aeaMarking, is(notNullValue()));
    assertThat(aeaMarking.getType(), is(AeaType.RD));
    assertThat(aeaMarking.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aeaMarking.getSigmas(), hasSize(0));
  }

  @Test
  public void testCreateRdCnwdiMarking() {
    AeaMarking aeaMarking = BannerTestUtils.createRdCnwdiMarking();
    assertThat(aeaMarking, is(notNullValue()));
    assertThat(aeaMarking.getType(), is(AeaType.RD));
    assertThat(aeaMarking.isCriticalNuclearWeaponDesignInformation(), is(true));
    assertThat(aeaMarking.getSigmas(), hasSize(0));
  }

  @Test
  public void testCreateRdSigmaMarking() {
    AeaMarking aeaMarking = BannerTestUtils.createRdSigmaMarking(1, 12, 40);
    assertThat(aeaMarking, is(notNullValue()));
    assertThat(aeaMarking.getType(), is(AeaType.RD));
    assertThat(aeaMarking.getSigmas(), hasSize(3));
    assertThat(aeaMarking.getSigmas(), containsInAnyOrder(1, 12, 40));
  }

  @Test
  public void testCreateFrdMarking() {
    AeaMarking aeaMarking = BannerTestUtils.createFrdMarking();
    assertThat(aeaMarking, is(notNullValue()));
    assertThat(aeaMarking.getType(), is(AeaType.FRD));
    assertThat(aeaMarking.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aeaMarking.getSigmas(), hasSize(0));
  }

  @Test
  public void testCreateFrdSigmaMarking() {
    AeaMarking aeaMarking = BannerTestUtils.createFrdSigmaMarking(14, 25);
    assertThat(aeaMarking, is(notNullValue()));
    assertThat(aeaMarking.getType(), is(AeaType.FRD));
    assertThat(aeaMarking.getSigmas(), hasSize(2));
    assertThat(aeaMarking.getSigmas(), containsInAnyOrder(14, 25));
  }

  @Test
  public void testCreateDodUcniMarking() {
    AeaMarking aeaMarking = BannerTestUtils.createDodUcniMarking();
    assertThat(aeaMarking, is(notNullValue()));
    assertThat(aeaMarking.getType(), is(AeaType.DOD_UCNI));
    assertThat(aeaMarking.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aeaMarking.getSigmas(), hasSize(0));
  }

  @Test
  public void testCreateDoeUcniMarking() {
    AeaMarking aeaMarking = BannerTestUtils.createDoeUcniMarking();
    assertThat(aeaMarking, is(notNullValue()));
    assertThat(aeaMarking.getType(), is(AeaType.DOE_UCNI));
    assertThat(aeaMarking.isCriticalNuclearWeaponDesignInformation(), is(false));
    assertThat(aeaMarking.getSigmas(), hasSize(0));
  }

  // ==========================================================================
  // Test Data List Tests
  // ==========================================================================

  @Test
  public void testGetAllValidUsClassifications() {
    List<String> classifications = BannerTestUtils.getAllValidUsClassifications();
    assertThat(classifications, is(notNullValue()));
    assertThat(classifications, hasSize(5));
    assertThat(
        classifications,
        containsInAnyOrder(
            "TOP SECRET", "SECRET", "CONFIDENTIAL", "RESTRICTED", "UNCLASSIFIED"));
  }

  @Test
  public void testGetAllValidSapControls() {
    List<String> sapControls = BannerTestUtils.getAllValidSapControls();
    assertThat(sapControls, is(notNullValue()));
    assertThat(sapControls.size(), is(greaterThan(0)));
    // Verify first entry is valid
    assertThat(sapControls.get(0), is("TOP SECRET//SAR-BP"));
  }

  @Test
  public void testGetAllValidAeaMarkings() {
    List<String> aeaMarkings = BannerTestUtils.getAllValidAeaMarkings();
    assertThat(aeaMarkings, is(notNullValue()));
    assertThat(aeaMarkings.size(), is(greaterThan(0)));
    assertThat(aeaMarkings.size(), is(10));
  }

  @Test
  public void testGetAllValidDissemControls() {
    List<String> dissemControls = BannerTestUtils.getAllValidDissemControls();
    assertThat(dissemControls, is(notNullValue()));
    assertThat(dissemControls.size(), is(greaterThan(0)));
    assertThat(dissemControls.size(), is(6));
  }

  @Test
  public void testGetAllInvalidMarkings() {
    List<String> invalidMarkings = BannerTestUtils.getAllInvalidMarkings();
    assertThat(invalidMarkings, is(notNullValue()));
    assertThat(invalidMarkings.size(), is(greaterThan(0)));
    assertThat(invalidMarkings.size(), is(9));
  }

  // ==========================================================================
  // Integration Tests - Verify Data Lists Produce Valid/Invalid Markings
  // ==========================================================================

  @Test
  public void testAllValidUsClassificationsAreParseable() throws Exception {
    List<String> classifications = BannerTestUtils.getAllValidUsClassifications();
    for (String classification : classifications) {
      BannerMarkings banner = BannerMarkings.parseMarkings(classification);
      assertThat(
          "Failed to parse: " + classification,
          banner,
          is(notNullValue()));
    }
  }

  @Test
  public void testAllValidSapControlsAreParseable() throws Exception {
    List<String> sapControls = BannerTestUtils.getAllValidSapControls();
    for (String sapControl : sapControls) {
      BannerMarkings banner = BannerMarkings.parseMarkings(sapControl);
      assertThat(
          "Failed to parse: " + sapControl,
          banner,
          is(notNullValue()));
      assertThat(
          "SAP control not present: " + sapControl,
          banner.getSapControl(),
          is(notNullValue()));
    }
  }

  @Test
  public void testAllValidAeaMarkingsAreParseable() throws Exception {
    List<String> aeaMarkings = BannerTestUtils.getAllValidAeaMarkings();
    for (String aeaMarking : aeaMarkings) {
      BannerMarkings banner = BannerMarkings.parseMarkings(aeaMarking);
      assertThat(
          "Failed to parse: " + aeaMarking,
          banner,
          is(notNullValue()));
      assertThat(
          "AEA marking not present: " + aeaMarking,
          banner.getAeaMarking(),
          is(notNullValue()));
    }
  }

  @Test
  public void testAllValidDissemControlsAreParseable() throws Exception {
    List<String> dissemControls = BannerTestUtils.getAllValidDissemControls();
    for (String dissemControl : dissemControls) {
      BannerMarkings banner = BannerMarkings.parseMarkings(dissemControl);
      assertThat(
          "Failed to parse: " + dissemControl,
          banner,
          is(notNullValue()));
      assertThat(
          "Dissem controls not present: " + dissemControl,
          banner.getDisseminationControls().isEmpty(),
          is(false));
    }
  }

  @Test
  public void testAllInvalidMarkingsThrowException() {
    List<String> invalidMarkings = BannerTestUtils.getAllInvalidMarkings();
    for (String invalidMarking : invalidMarkings) {
      try {
        BannerMarkings.parseMarkings(invalidMarking);
        throw new AssertionError(
            "Expected MarkingsValidationException for: " + invalidMarking);
      } catch (MarkingsValidationException e) {
        // Expected - this marking should fail validation
        assertThat(
            "Validation exception should have errors for: " + invalidMarking,
            e.getErrors().isEmpty(),
            is(false));
      }
    }
  }

  // ==========================================================================
  // Utility Method Consistency Tests
  // ==========================================================================

  @Test
  public void testSapControlToStringAndReparse() throws Exception {
    SapControl originalSap = BannerTestUtils.createSapControl("BP");
    String sapString = originalSap.toString();
    assertThat(sapString, is("SAR-BP"));

    // Parse into a banner and verify
    BannerMarkings banner = BannerMarkings.parseMarkings("TOP SECRET//" + sapString);
    assertThat(banner.getSapControl(), is(notNullValue()));
    assertThat(banner.getSapControl().getPrograms(), hasSize(1));
    assertThat(banner.getSapControl().getPrograms().get(0), is("BP"));
  }

  @Test
  public void testAeaMarkingToStringAndReparse() throws Exception {
    AeaMarking originalAea = BannerTestUtils.createRdMarking();
    String aeaString = originalAea.toString();
    assertThat(aeaString, is("RESTRICTED DATA"));

    // Parse into a banner and verify
    BannerMarkings banner = BannerMarkings.parseMarkings("SECRET//" + aeaString);
    assertThat(banner.getAeaMarking(), is(notNullValue()));
    assertThat(banner.getAeaMarking().getType(), is(AeaType.RD));
  }
}
