# OWASP Dependency-Check Scan Guide

**Purpose:** Instructions for running OWASP dependency-check to extract detailed CVE data

**Alliance Version:** 1.17.5-SNAPSHOT
**OWASP Plugin Version:** Configured in root pom.xml

---

## Quick Start

### Run OWASP Scan with All Formats

```bash
cd /home/e/Development/alliance

# Run aggregate scan across all modules
mvn org.owasp:dependency-check-maven:aggregate \
  -DfailBuildOnCVSS=11 \
  -Dformat=HTML,JSON,CSV,XML \
  -DskipTests=true

# Reports will be generated in:
# target/dependency-check-report.html
# target/dependency-check-report.json
# target/dependency-check-report.csv
# target/dependency-check-report.xml
```

### Quick JSON-Only Scan

```bash
mvn org.owasp:dependency-check-maven:aggregate \
  -DfailBuildOnCVSS=11 \
  -Dformat=JSON \
  -DskipTests=true
```

---

## Scan Options

### Basic Scan (All Modules)

```bash
mvn dependency-check:aggregate
```

**Options:**
- Scans all modules in reactor
- Uses default configuration from pom.xml
- Generates HTML report only
- Never fails build (CVSS threshold = 11)

### Specific Module Scan

```bash
cd catalog/imaging/imaging-plugin-nitf
mvn dependency-check:check
```

**Use When:**
- Testing a specific module
- Faster iteration during development
- Module-specific vulnerability analysis

### Full Scan with All Outputs

```bash
mvn org.owasp:dependency-check-maven:aggregate \
  -DfailBuildOnCVSS=11 \
  -Dformat=HTML,JSON,CSV,XML,JUNIT,ALL \
  -DskipTests=true \
  -Dproject.build.directory=target/owasp \
  -DautoUpdate=true
```

**Options Explained:**
- `aggregate` - Scan all modules together
- `failBuildOnCVSS=11` - Never fail (11 > max CVSS of 10)
- `format=HTML,JSON,CSV,XML` - All output formats
- `skipTests=true` - Don't run unit tests
- `project.build.directory` - Custom output directory
- `autoUpdate=true` - Update CVE database before scan

---

## Output Formats

### HTML Report (Best for Human Review)

```bash
mvn dependency-check:aggregate -Dformat=HTML
```

**Location:** `target/dependency-check-report.html`

**Contents:**
- Summary of vulnerabilities by severity
- Detailed CVE listings with descriptions
- Dependency tree visualization
- Clickable links to CVE databases

**Best For:**
- Initial analysis
- Management reports
- Security audit documentation

### JSON Report (Best for Automation)

```bash
mvn dependency-check:aggregate -Dformat=JSON
```

**Location:** `target/dependency-check-report.json`

**Contents:**
```json
{
  "dependencies": [
    {
      "fileName": "netty-all-4.1.68.Final.jar",
      "vulnerabilities": [
        {
          "name": "CVE-2023-34462",
          "cvssv3": {
            "baseScore": 9.8,
            "baseSeverity": "CRITICAL"
          },
          "description": "...",
          "references": [...]
        }
      ]
    }
  ]
}
```

**Best For:**
- Parsing with scripts
- Importing to spreadsheets
- Automated processing
- CI/CD integration

### CSV Report (Best for Spreadsheet Analysis)

```bash
mvn dependency-check:aggregate -Dformat=CSV
```

**Location:** `target/dependency-check-report.csv`

**Contents:**
```csv
Dependency,CVE,CVSS,Severity,Description
netty-all-4.1.68.Final.jar,CVE-2023-34462,9.8,CRITICAL,"..."
```

**Best For:**
- Excel/Google Sheets analysis
- Filtering and sorting
- Pivot tables
- Quick CVE counts

### XML Report (Best for Tool Integration)

```bash
mvn dependency-check:aggregate -Dformat=XML
```

**Location:** `target/dependency-check-report.xml`

**Best For:**
- XSLT transformations
- Integration with security tools
- Custom report generation

---

## Extracting CVE Data for Inventory

### Step 1: Run Scan with JSON Output

```bash
mvn org.owasp:dependency-check-maven:aggregate \
  -DfailBuildOnCVSS=11 \
  -Dformat=JSON \
  -DskipTests=true
```

### Step 2: Parse JSON to CSV

```bash
# Using jq (JSON processor)
jq -r '.dependencies[] |
  .vulnerabilities[]? |
  [.name, .cvssv3.baseScore // .cvssv2.score, .cvssv3.baseSeverity // "UNKNOWN", .description] |
  @csv' target/dependency-check-report.json > /tmp/cve-list.csv
```

**Output:** `/tmp/cve-list.csv`
```csv
"CVE-2023-34462","9.8","CRITICAL","Netty vulnerability..."
"CVE-2021-44228","10.0","CRITICAL","Log4Shell JNDI injection..."
```

### Step 3: Enhanced Parsing (with Dependency Info)

```bash
# Extract CVE with dependency name and affected files
jq -r '.dependencies[] |
  .fileName as $dep |
  .vulnerabilities[]? |
  [$dep, .name, .cvssv3.baseScore // .cvssv2.score, .cvssv3.baseSeverity // "UNKNOWN", .description] |
  @csv' target/dependency-check-report.json > /tmp/cve-detailed.csv
```

**Output:** `/tmp/cve-detailed.csv`
```csv
"netty-all-4.1.68.Final.jar","CVE-2023-34462","9.8","CRITICAL","..."
```

### Step 4: Count Vulnerabilities by Severity

```bash
# Count by severity
jq '.dependencies[].vulnerabilities[]? | .cvssv3.baseSeverity // .cvssv2.severity // "UNKNOWN"' \
  target/dependency-check-report.json |
  sort |
  uniq -c
```

**Output:**
```
     27 "CRITICAL"
     98 "HIGH"
     66 "MEDIUM"
     29 "LOW"
```

---

## Advanced Configuration

### Update CVE Database

```bash
# Update NVD data before scan (recommended weekly)
mvn dependency-check:update-only
```

**When to Use:**
- First scan after project clone
- Weekly maintenance
- After long period without scanning

### Scan with Custom Suppression File

```bash
mvn dependency-check:aggregate \
  -DsuppressionFile=dependency-check-maven-config.xml
```

**Current Suppression:**
```xml
<!-- Alliance artifacts (false positives) -->
<suppress>
  <gav regex="true">org.codice.alliance.*:.*</gav>
  <cvssBelow>10</cvssBelow>
</suppress>
```

**Use Case:** Suppress known false positives

### Fast Scan (Skip Update)

```bash
mvn dependency-check:aggregate \
  -DautoUpdate=false \
  -DskipTests=true
```

**When to Use:**
- Rapid iteration during development
- CVE database recently updated
- Offline environments (with cached DB)

### Fail Build on Specific CVSS

```bash
# Fail on HIGH or above (CVSS >= 7.0)
mvn dependency-check:aggregate \
  -DfailBuildOnCVSS=7.0
```

**Severity Thresholds:**
- `10.0` - Fail only on CVSS 10.0 (max)
- `9.0` - Fail on CRITICAL (9.0-10.0)
- `7.0` - Fail on HIGH or above (7.0-10.0)
- `4.0` - Fail on MEDIUM or above (4.0-10.0)
- `0.1` - Fail on any vulnerability

**Current Setting:** `11.0` (never fails, Phase 2 documentation mode)
**Target Setting:** `7.0` (Phase 3 enforcement mode)

---

## GitHub Dependabot Alternative

### Export Dependabot Alerts (Requires Permissions)

```bash
# Using GitHub CLI
gh api /repos/codice/alliance/dependabot/alerts \
  --paginate \
  --jq '.[] | {cve: .security_advisory.cve_id, severity: .security_advisory.severity, package: .dependency.package.name}' \
  > /tmp/dependabot-alerts.json
```

**Advantages:**
- GitHub's curated vulnerability database
- Automatic pull requests for fixes
- Integration with GitHub Security tab

**Disadvantages:**
- Requires repository admin permissions
- API rate limits
- May miss some Maven-specific vulnerabilities

### Compare OWASP vs Dependabot

```bash
# OWASP CVE list
jq -r '.dependencies[].vulnerabilities[]? | .name' target/dependency-check-report.json | sort -u > /tmp/owasp-cves.txt

# Dependabot CVE list
jq -r '.[].security_advisory.cve_id' /tmp/dependabot-alerts.json | sort -u > /tmp/dependabot-cves.txt

# Find differences
comm -3 /tmp/owasp-cves.txt /tmp/dependabot-cves.txt
```

---

## Troubleshooting

### Error: "Unable to Update NVD Data"

**Cause:** Network issues, NVD API rate limiting

**Solution:**
```bash
# Skip update, use cached database
mvn dependency-check:aggregate -DautoUpdate=false

# Or manually update with retry
mvn dependency-check:update-only
```

### Error: "OutOfMemoryError"

**Cause:** Large dependency tree, insufficient heap

**Solution:**
```bash
# Increase Maven memory
export MAVEN_OPTS="-Xmx4g -Xms1g"
mvn dependency-check:aggregate
```

### Slow Scan Performance

**Optimization:**
```bash
# Skip analyzers for faster scan
mvn dependency-check:aggregate \
  -DskipTests=true \
  -DassemblyAnalyzerEnabled=false \
  -DnuspecAnalyzerEnabled=false \
  -DnugetconfAnalyzerEnabled=false
```

### Report Not Generated

**Check:**
```bash
# Verify output directory exists
ls -la target/dependency-check-report.*

# Check Maven logs for errors
mvn dependency-check:aggregate -X  # Debug mode
```

---

## CI/CD Integration

### GitHub Actions Workflow

```yaml
name: OWASP Dependency Check

on:
  schedule:
    - cron: '0 2 * * 0'  # Weekly on Sunday 2 AM
  workflow_dispatch:     # Manual trigger

jobs:
  owasp-scan:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 8
        uses: actions/setup-java@v4
        with:
          java-version: '8'
          distribution: 'temurin'

      - name: Cache OWASP Database
        uses: actions/cache@v4
        with:
          path: ~/.m2/repository/org/owasp/dependency-check-data
          key: owasp-db-${{ github.run_id }}
          restore-keys: owasp-db-

      - name: Run OWASP Scan
        run: |
          mvn org.owasp:dependency-check-maven:aggregate \
            -DfailBuildOnCVSS=11 \
            -Dformat=HTML,JSON \
            -DskipTests=true

      - name: Upload Reports
        uses: actions/upload-artifact@v4
        with:
          name: owasp-reports
          path: target/dependency-check-report.*

      - name: Parse and Comment on PR
        if: github.event_name == 'pull_request'
        run: |
          # Parse JSON, create comment with new CVEs
          ./scripts/parse-owasp-report.sh
```

---

## Next Steps

1. **Run Initial Scan**
   ```bash
   mvn dependency-check:aggregate -Dformat=JSON,HTML -DskipTests=true
   ```

2. **Review HTML Report**
   - Open `target/dependency-check-report.html`
   - Review all CRITICAL and HIGH vulnerabilities
   - Take screenshots for documentation

3. **Extract CVE Data**
   ```bash
   jq -r '.dependencies[] | .fileName as $dep | .vulnerabilities[]? | [$dep, .name, .cvssv3.baseScore, .cvssv3.baseSeverity, .description] | @csv' \
     target/dependency-check-report.json > docs/security/owasp-cves.csv
   ```

4. **Populate Inventory**
   - Import `owasp-cves.csv` into `VULNERABILITY-INVENTORY.csv`
   - Map CVEs to Alliance modules
   - Prioritize for test harness development

5. **Schedule Regular Scans**
   - Add to GitHub Actions workflow
   - Run weekly or on every PR
   - Track vulnerability trends

---

## References

- [OWASP Dependency-Check](https://jeremylong.github.io/DependencyCheck/) - Official documentation
- [Maven Plugin](https://jeremylong.github.io/DependencyCheck/dependency-check-maven/) - Maven-specific docs
- [NVD Data Feeds](https://nvd.nist.gov/vuln/data-feeds) - NIST vulnerability database
- [Suppression File](https://jeremylong.github.io/DependencyCheck/general/suppression.html) - False positive suppression

---

**Last Updated:** 2025-10-18
**Maintained By:** Alliance Security Team
