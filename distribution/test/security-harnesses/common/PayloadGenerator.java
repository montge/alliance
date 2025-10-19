/*
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.alliance.test.security.harness.common;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Advanced payload generator for security vulnerability testing.
 *
 * <p>This class provides template-based payload generation with variable substitution, enabling
 * creation of customized exploit payloads from pre-defined templates.
 *
 * <p><strong>Future Enhancement:</strong> This class is a placeholder for advanced payload
 * generation features including:
 *
 * <ul>
 *   <li>Template-based payload generation with variable substitution
 *   <li>Payload mutation and fuzzing
 *   <li>Encoding/obfuscation of payloads
 *   <li>Polyglot payload generation (multi-format exploits)
 *   <li>Automated payload variation for bypass testing
 * </ul>
 *
 * <p>Current implementation provides basic template loading. Additional features will be added as
 * security testing requirements evolve.
 *
 * @see VulnerabilityTestUtils for current payload generation methods
 * @see SecurityTestBase for test infrastructure
 */
public final class PayloadGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger(PayloadGenerator.class);

  /** Private constructor to prevent instantiation of utility class. */
  private PayloadGenerator() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  /**
   * Loads a payload template from a file.
   *
   * <p>Payload templates are stored in vulnerability-specific directories (e.g.,
   * xxe/xxe-payloads/) and can be customized with variable substitution.
   *
   * @param templateFile the template file to load
   * @return the template content as a string
   * @throws IOException if template file cannot be read
   */
  public static String loadTemplate(File templateFile) throws IOException {
    LOGGER.debug("Loading payload template: {}", templateFile.getAbsolutePath());

    if (!templateFile.exists()) {
      throw new IOException("Template file not found: " + templateFile.getAbsolutePath());
    }

    if (!templateFile.canRead()) {
      throw new IOException("Template file not readable: " + templateFile.getAbsolutePath());
    }

    return new String(Files.readAllBytes(templateFile.toPath()), StandardCharsets.UTF_8);
  }

  /**
   * Generates a payload from a template with variable substitution.
   *
   * <p>Variables in templates are specified as ${variableName} and are replaced with values from
   * the provided map.
   *
   * <p><strong>Example Template:</strong>
   *
   * <pre>{@code
   * <?xml version="1.0"?>
   * <!DOCTYPE foo [
   *   <!ENTITY xxe SYSTEM "${targetUri}">
   * ]>
   * <root><data>&xxe;</data></root>
   * }</pre>
   *
   * <p><strong>Usage:</strong>
   *
   * <pre>{@code
   * Map<String, String> vars = new HashMap<>();
   * vars.put("targetUri", "file:///etc/passwd");
   * String payload = PayloadGenerator.generateFromTemplate(templateFile, vars);
   * }</pre>
   *
   * @param templateFile the template file
   * @param variables map of variable names to values
   * @return the generated payload with substituted variables
   * @throws IOException if template cannot be loaded
   */
  public static String generateFromTemplate(File templateFile, Map<String, String> variables)
      throws IOException {
    String template = loadTemplate(templateFile);
    return substituteVariables(template, variables);
  }

  /**
   * Substitutes variables in a template string.
   *
   * <p>Variables are specified as ${variableName} in the template.
   *
   * @param template the template string
   * @param variables map of variable names to values
   * @return the template with variables substituted
   */
  public static String substituteVariables(String template, Map<String, String> variables) {
    String result = template;

    for (Map.Entry<String, String> entry : variables.entrySet()) {
      String placeholder = "${" + entry.getKey() + "}";
      String value = entry.getValue();

      result = result.replace(placeholder, value);
      LOGGER.trace("Substituted ${} -> {}", entry.getKey(), value);
    }

    // Check for unsubstituted variables
    if (result.contains("${")) {
      LOGGER.warn("Template contains unsubstituted variables: {}", result);
    }

    return result;
  }

  /**
   * URL-encodes a payload.
   *
   * @param payload the payload to encode
   * @return URL-encoded payload
   */
  public static String urlEncode(String payload) {
    try {
      return java.net.URLEncoder.encode(payload, StandardCharsets.UTF_8.name());
    } catch (java.io.UnsupportedEncodingException e) {
      LOGGER.error("Failed to URL encode payload", e);
      return payload;
    }
  }

  /**
   * Base64-encodes a payload.
   *
   * @param payload the payload to encode
   * @return Base64-encoded payload
   */
  public static String base64Encode(String payload) {
    return java.util.Base64.getEncoder()
        .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Hex-encodes a payload.
   *
   * @param payload the payload to encode
   * @return hex-encoded payload
   */
  public static String hexEncode(String payload) {
    StringBuilder hex = new StringBuilder();
    for (byte b : payload.getBytes(StandardCharsets.UTF_8)) {
      hex.append(String.format("%02x", b));
    }
    return hex.toString();
  }

  /**
   * Creates a fuzzed variant of a payload by replacing characters.
   *
   * <p>This is useful for bypass testing when input validation may be present.
   *
   * @param payload the original payload
   * @param substitutions map of characters to replace
   * @return fuzzed payload
   */
  public static String fuzzPayload(String payload, Map<Character, String> substitutions) {
    String result = payload;

    for (Map.Entry<Character, String> entry : substitutions.entrySet()) {
      result = result.replace(entry.getKey().toString(), entry.getValue());
    }

    LOGGER.debug("Fuzzed payload: {} -> {}", payload, result);
    return result;
  }

  /**
   * Creates a case-variation fuzzer for bypassing case-sensitive filters.
   *
   * @param payload the original payload
   * @return map of case variations
   */
  public static Map<String, String> createCaseVariations(String payload) {
    Map<String, String> variations = new HashMap<>();

    variations.put("original", payload);
    variations.put("lowercase", payload.toLowerCase());
    variations.put("uppercase", payload.toUpperCase());
    variations.put("alternating", alternateCase(payload, true));
    variations.put("reverse-alternating", alternateCase(payload, false));

    return variations;
  }

  /**
   * Creates alternating case version of a string.
   *
   * @param input the input string
   * @param startUpper whether to start with uppercase
   * @return alternating case string
   */
  private static String alternateCase(String input, boolean startUpper) {
    StringBuilder result = new StringBuilder();
    boolean upper = startUpper;

    for (char c : input.toCharArray()) {
      if (Character.isLetter(c)) {
        result.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c));
        upper = !upper;
      } else {
        result.append(c);
      }
    }

    return result.toString();
  }

  /**
   * Creates a polyglot payload that is valid in multiple contexts.
   *
   * <p>Polyglot payloads can exploit vulnerabilities across different parsers/interpreters
   * simultaneously.
   *
   * <p><strong>Example:</strong> A payload that is valid XML, JSON, and HTML simultaneously.
   *
   * @param contexts array of target contexts (e.g., "xml", "json", "html")
   * @return polyglot payload (placeholder for future implementation)
   */
  public static String createPolyglot(String... contexts) {
    LOGGER.warn("Polyglot generation not yet implemented - using basic payload");
    // Future: Generate sophisticated polyglot payloads
    return "<!-- xml -->{\"json\":\"payload\"}<html></html>";
  }

  /**
   * Applies multiple encoding layers to a payload.
   *
   * @param payload the original payload
   * @param encodings array of encoding types in order (e.g., "url", "base64")
   * @return multiply-encoded payload
   */
  public static String multiEncode(String payload, String... encodings) {
    String result = payload;

    for (String encoding : encodings) {
      switch (encoding.toLowerCase()) {
        case "url":
          result = urlEncode(result);
          break;
        case "base64":
          result = base64Encode(result);
          break;
        case "hex":
          result = hexEncode(result);
          break;
        default:
          LOGGER.warn("Unknown encoding type: {}", encoding);
      }
      LOGGER.debug("Applied {} encoding: {}", encoding, result);
    }

    return result;
  }
}
