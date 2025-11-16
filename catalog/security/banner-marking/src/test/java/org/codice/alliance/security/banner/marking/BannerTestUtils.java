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

/**
 * Utility class for creating test banner markings and related test objects.
 *
 * <p>This class provides factory methods for creating common test scenarios:
 *
 * <ul>
 *   <li>Valid banner marking strings for each classification level
 *   <li>Complex banner markings with multiple controls
 *   <li>Invalid banner markings for error testing
 *   <li>SCI, SAP, and AEA marking objects
 *   <li>Test data for validation scenarios
 * </ul>
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>
 * BannerMarkings bannerMarkings = BannerTestUtils.createUsTopSecretBanner();
 * SciControl sciControl = BannerTestUtils.createTkControl("ABC", Arrays.asList("XYZ"));
 * </pre>
 */
public class BannerTestUtils {

  private BannerTestUtils() {
    // Utility class, prevent instantiation
  }

  // ==========================================================================
  // US Banner Marking Factory Methods
  // ==========================================================================

  /**
   * Create a TOP SECRET banner marking.
   *
   * @return BannerMarkings for "TOP SECRET"
   * @throws MarkingsValidationException if parsing fails
   */
  public static BannerMarkings createUsTopSecretBanner() throws MarkingsValidationException {
    return BannerMarkings.parseMarkings("TOP SECRET");
  }

  /**
   * Create a SECRET banner marking.
   *
   * @return BannerMarkings for "SECRET"
   * @throws MarkingsValidationException if parsing fails
   */
  public static BannerMarkings createUsSecretBanner() throws MarkingsValidationException {
    return BannerMarkings.parseMarkings("SECRET");
  }

  /**
   * Create a CONFIDENTIAL banner marking.
   *
   * @return BannerMarkings for "CONFIDENTIAL"
   * @throws MarkingsValidationException if parsing fails
   */
  public static BannerMarkings createUsConfidentialBanner() throws MarkingsValidationException {
    return BannerMarkings.parseMarkings("CONFIDENTIAL");
  }

  /**
   * Create a RESTRICTED banner marking.
   *
   * @return BannerMarkings for "RESTRICTED"
   * @throws MarkingsValidationException if parsing fails
   */
  public static BannerMarkings createUsRestrictedBanner() throws MarkingsValidationException {
    return BannerMarkings.parseMarkings("RESTRICTED");
  }

  /**
   * Create an UNCLASSIFIED banner marking.
   *
   * @return BannerMarkings for "UNCLASSIFIED"
   * @throws MarkingsValidationException if parsing fails
   */
  public static BannerMarkings createUsUnclassifiedBanner() throws MarkingsValidationException {
    return BannerMarkings.parseMarkings("UNCLASSIFIED");
  }

  /**
   * Create a SECRET//NOFORN banner marking.
   *
   * @return BannerMarkings for "SECRET//NOFORN"
   * @throws MarkingsValidationException if parsing fails
   */
  public static BannerMarkings createUsSecretNoforn() throws MarkingsValidationException {
    return BannerMarkings.parseMarkings("SECRET//NOFORN");
  }

  /**
   * Create a TOP SECRET//ORCON banner marking.
   *
   * @return BannerMarkings for "TOP SECRET//ORCON"
   * @throws MarkingsValidationException if parsing fails
   */
  public static BannerMarkings createUsTopSecretOrcon() throws MarkingsValidationException {
    return BannerMarkings.parseMarkings("TOP SECRET//ORCON");
  }

  /**
   * Create an UNCLASSIFIED//FOUO banner marking.
   *
   * @return BannerMarkings for "UNCLASSIFIED//FOUO"
   * @throws MarkingsValidationException if parsing fails
   */
  public static BannerMarkings createUsUnclassifiedFouo() throws MarkingsValidationException {
    return BannerMarkings.parseMarkings("UNCLASSIFIED//FOUO");
  }

  // ==========================================================================
  // FGI Banner Marking Factory Methods
  // ==========================================================================

  // TODO: createFgiNatoSecret() - Create "//NATO SECRET//ATOMAL" BannerMarkings
  // TODO: createFgiCosmicTopSecret() - Create "//COSMIC TOP SECRET//BOHEMIA" BannerMarkings
  // TODO: createFgiCountryMarking(String country, ClassificationLevel level) - Create FGI country
  // marking

  // ==========================================================================
  // Joint Banner Marking Factory Methods
  // ==========================================================================

  // TODO: createJointSecretTwoCountries() - Create "//JOINT SECRET CAN USA" BannerMarkings
  // TODO: createJointTopSecretThreeCountries() - Create "//JOINT TOP SECRET CAN DEU USA"
  // BannerMarkings

  // ==========================================================================
  // SCI Control Factory Methods
  // ==========================================================================

  // TODO: createSciControl(String control) - Create SCI control without compartments (e.g., HCS)
  // TODO: createSciControlWithCompartment(String control, String compartment) - Create SCI with one
  // compartment
  // TODO: createSciControlWithCompartments(String control, List<String> compartments) - Create SCI
  // with multiple compartments
  // TODO: createSciControlWithSubCompartments(String control, String compartment, List<String>
  // subCompartments)

  // TODO: createHcsControl() - Create HCS SciControl
  // TODO: createTkControl(String compartment, List<String> subCompartments) - Create TK SciControl
  // TODO: createSiControl(List<String> compartments) - Create SI SciControl
  // TODO: createComintControl() - Create COMINT SciControl
  // TODO: createKlondikeControl() - Create KLONDIKE SciControl

  // ==========================================================================
  // SAP Control Factory Methods
  // ==========================================================================

  /**
   * Create a SAP control with a single program.
   *
   * @param programName the SAP program name (e.g., "BP")
   * @return SapControl for the specified program
   */
  public static SapControl createSapControl(String programName) {
    return new SapControl(programName);
  }

  /**
   * Create a SAP control with multiple programs.
   *
   * @param programNames the SAP program names (e.g., "BP", "GB", "TC")
   * @return SapControl for multiple programs
   */
  public static SapControl createSapControlMultiplePrograms(String... programNames) {
    return new SapControl(String.join("/", programNames));
  }

  /**
   * Create a SAR-MULTIPLE PROGRAMS SapControl.
   *
   * @return SapControl for multiple programs indicator
   */
  public static SapControl createSapControlMultipleProgramsIndicator() {
    return new SapControl("MULTIPLE PROGRAMS");
  }

  /**
   * Create an HVSACO SapControl.
   *
   * @return SapControl for HVSACO
   */
  public static SapControl createHvsacoControl() {
    return new SapControl();
  }

  // ==========================================================================
  // AEA Marking Factory Methods
  // ==========================================================================

  /**
   * Create a basic RD (Restricted Data) AeaMarking.
   *
   * @return AeaMarking for RD
   */
  public static AeaMarking createRdMarking() {
    return new AeaMarking("RD");
  }

  /**
   * Create an RD-N (RD with CNWDI) AeaMarking.
   *
   * @return AeaMarking for RD-N
   */
  public static AeaMarking createRdCnwdiMarking() {
    return new AeaMarking("RD-N");
  }

  /**
   * Create an RD-SIGMA AeaMarking with specified sigma values.
   *
   * @param sigmas the sigma values (1-99)
   * @return AeaMarking for RD-SIGMA
   */
  public static AeaMarking createRdSigmaMarking(int... sigmas) {
    StringBuilder sb = new StringBuilder("RD-SIGMA");
    for (int i = 0; i < sigmas.length; i++) {
      if (i == 0) {
        sb.append(" ");
      } else {
        sb.append(" ");
      }
      sb.append(sigmas[i]);
    }
    return new AeaMarking(sb.toString());
  }

  /**
   * Create a basic FRD (Formerly Restricted Data) AeaMarking.
   *
   * @return AeaMarking for FRD
   */
  public static AeaMarking createFrdMarking() {
    return new AeaMarking("FRD");
  }

  /**
   * Create an FRD-SIGMA AeaMarking with specified sigma values.
   *
   * @param sigmas the sigma values (1-99)
   * @return AeaMarking for FRD-SIGMA
   */
  public static AeaMarking createFrdSigmaMarking(int... sigmas) {
    StringBuilder sb = new StringBuilder("FRD-SIGMA");
    for (int i = 0; i < sigmas.length; i++) {
      if (i == 0) {
        sb.append(" ");
      } else {
        sb.append(" ");
      }
      sb.append(sigmas[i]);
    }
    return new AeaMarking(sb.toString());
  }

  /**
   * Create a DOD UCNI AeaMarking.
   *
   * @return AeaMarking for DOD_UCNI
   */
  public static AeaMarking createDodUcniMarking() {
    return new AeaMarking("DOD UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION");
  }

  /**
   * Create a DOE UCNI AeaMarking.
   *
   * @return AeaMarking for DOE_UCNI
   */
  public static AeaMarking createDoeUcniMarking() {
    return new AeaMarking("DOE UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION");
  }

  // ==========================================================================
  // Complex Banner Marking Factory Methods
  // ==========================================================================

  // TODO: createComplexBanner() - Create banner with SCI, SAP, AEA, FGI, and dissem controls
  // TODO: createBannerWithSciAndSap() - Create banner with both SCI and SAP controls
  // TODO: createBannerWithAllMarkingTypes() - Create extremely complex banner for stress testing

  // ==========================================================================
  // Invalid Banner Marking Factory Methods (for negative testing)
  // ==========================================================================

  // TODO: createInvalidClassificationBanner() - Create banner with invalid classification
  // TODO: createInvalidSciWithoutDissemBanner() - Create SCI without required dissem control
  // TODO: createInvalidSapTooManyPrograms() - Create SAP with more than 3 programs
  // TODO: createInvalidFrdCnwdiBanner() - Create FRD-N (invalid combination)
  // TODO: createInvalidNofornWithRelToBanner() - Create NOFORN with REL TO (conflict)

  // ==========================================================================
  // Test Data Lists
  // ==========================================================================

  /**
   * Get all valid US classification marking strings.
   *
   * @return list of valid US classification strings
   */
  public static java.util.List<String> getAllValidUsClassifications() {
    return java.util.Arrays.asList(
        "TOP SECRET",
        "SECRET",
        "CONFIDENTIAL",
        "RESTRICTED",
        "UNCLASSIFIED");
  }

  /**
   * Get all valid SAP control marking strings.
   *
   * @return list of valid SAP control strings
   */
  public static java.util.List<String> getAllValidSapControls() {
    return java.util.Arrays.asList(
        "TOP SECRET//SAR-BP",
        "SECRET//SAR-BP/GB",
        "TOP SECRET//SAR-BP/GB/TC",
        "SECRET//SAR-MULTIPLE PROGRAMS",
        "SECRET//HVSACO",
        "TOP SECRET//SAR-BP//WAIVED");
  }

  /**
   * Get all valid AEA marking strings.
   *
   * @return list of valid AEA marking strings
   */
  public static java.util.List<String> getAllValidAeaMarkings() {
    return java.util.Arrays.asList(
        "TOP SECRET//RESTRICTED DATA",
        "SECRET//RD",
        "CONFIDENTIAL//RD",
        "SECRET//RD-N",
        "TOP SECRET//RD-SIGMA 1 12 40",
        "SECRET//FORMERLY RESTRICTED DATA",
        "CONFIDENTIAL//FRD",
        "CONFIDENTIAL//FRD-SIGMA 14",
        "UNCLASSIFIED//DOD UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION",
        "UNCLASSIFIED//DOE UNCLASSIFIED CONTROLLED NUCLEAR INFORMATION");
  }

  /**
   * Get all valid dissemination control marking strings.
   *
   * @return list of valid dissem control strings
   */
  public static java.util.List<String> getAllValidDissemControls() {
    return java.util.Arrays.asList(
        "SECRET//NOFORN",
        "TOP SECRET//ORCON",
        "UNCLASSIFIED//FOUO",
        "SECRET//PROPIN",
        "CONFIDENTIAL//RELIDO",
        "SECRET//IMCON/NOFORN");
  }

  /**
   * Get invalid banner markings for negative testing.
   *
   * @return list of invalid marking strings
   */
  public static java.util.List<String> getAllInvalidMarkings() {
    return java.util.Arrays.asList(
        "RESTRICTED//RD", // RD requires CONFIDENTIAL+
        "RESTRICTED//FRD", // FRD requires CONFIDENTIAL+
        "SECRET//FRD-N", // FRD cannot have CNWDI
        "SECRET//RD-SIGMA 150", // SIGMA out of range
        "SECRET//WAIVED", // WAIVED requires SAP
        "TOP SECRET//SAR-BP/GB/TC/XY/ZZ", // Too many SAP programs
        "RESTRICTED//ORCON", // ORCON requires CONFIDENTIAL+
        "CONFIDENTIAL//IMCON", // IMCON requires SECRET+
        "SECRET//NOFORN/REL TO USA, CAN"); // NOFORN conflicts with REL TO
  }

  // ==========================================================================
  // Assertion Helpers
  // ==========================================================================

  // TODO: assertBannerEquals(BannerMarkings expected, BannerMarkings actual) - Deep equality check
  // TODO: assertSciControlEquals(SciControl expected, SciControl actual) - SCI equality check
  // TODO: assertSapControlEquals(SapControl expected, SapControl actual) - SAP equality check
  // TODO: assertAeaMarkingEquals(AeaMarking expected, AeaMarking actual) - AEA equality check

  // TODO: assertValidationError(MarkingsValidationException ex, String expectedParagraph) - Verify
  // error contains expected paragraph
  // TODO: assertValidationErrors(MarkingsValidationException ex, String... expectedParagraphs) -
  // Verify multiple errors

  // ==========================================================================
  // String Builders
  // ==========================================================================

  // TODO: buildBannerString(ClassificationLevel level, String... controls) - Build banner string
  // from components
  // TODO: buildSciString(String control, Map<String, List<String>> compartments) - Build SCI
  // control string
  // TODO: buildSapString(String... programs) - Build SAP control string
  // TODO: buildAeaString(AeaType type, boolean cnwdi, int... sigmas) - Build AEA marking string
}
