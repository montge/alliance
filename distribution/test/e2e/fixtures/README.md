# E2E Test Fixtures

## Overview

This directory contains test data files (fixtures) used by E2E tests. Fixtures represent realistic data samples that Alliance would process in production.

## ⚠️ IMPORTANT: No Real Classified Data

**NEVER commit real classified data to this repository!**

- All fixtures must be unclassified test/sample data
- Classification markings in test files should be dummy/example markings only
- Verify fixtures are approved for public release before committing

## Directory Structure

### `nitf/` - NITF Image Fixtures

NITF 2.1 (STANAG 4545) compliant image files for testing image ingest and processing.

**Required Fixtures:**
- `sample_nitf_basic.ntf` - Simple 512x512 8-bit grayscale NITF
- `sample_nitf_classified.ntf` - NITF with classification markings (UNCLASSIFIED test data)
- `sample_nitf_large.ntf` - Larger image for performance testing (~10MB)
- `sample_nitf_multiband.ntf` - Multi-band (RGB) imagery

**Fixture Specifications:**
- Format: NITF 2.1 / STANAG 4545
- Size: < 10MB per file (keep repository small)
- Geolocation: Should include valid coordinate data
- Classification: Use test markings like "//UNCLASSIFIED//FOR TESTING ONLY"

**Creating NITF Fixtures:**
```bash
# Use NITF creation tools or download public samples from:
# - NGA Public Sample Data
# - USGS Public Datasets
# - NATO Publicly Released Imagery
```

### `video/` - Video Stream Fixtures

MPEG-TS streams with STANAG 4609 KLV metadata for testing Full Motion Video (FMV) capabilities.

**Required Fixtures:**
- `sample_klv_basic.ts` - Simple MPEG-TS with basic KLV tags
- `sample_klv_full.ts` - Comprehensive KLV tag set (all common tags)
- `sample_klv_geolocation.ts` - Focus on geolocation metadata

**Fixture Specifications:**
- Format: MPEG-TS with embedded KLV (STANAG 4609)
- Duration: 10-30 seconds (keep files small)
- KLV Tags: Must include timestamp, geolocation, sensor data
- Video: Can be low-resolution to reduce file size

**Creating Video Fixtures:**
```bash
# Use tools like:
# - FFmpeg with KLV muxing
# - STANAG 4609 compliant encoders
# - Public FMV datasets (check MITRE, NGA public releases)
```

### `ddms/` - DDMS Metadata Fixtures

DoD Discovery Metadata Specification (DDMS) XML files for testing metadata transformation.

**Required Fixtures:**
- `sample_ddms_2.0.xml` - DDMS 2.0 metadata
- `sample_ddms_3.0.xml` - DDMS 3.0 metadata
- `sample_ddms_classified.xml` - DDMS with classification (UNCLASSIFIED test)

**Fixture Specifications:**
- Format: Valid DDMS XML (validates against schema)
- Version: DDMS 2.0 and 3.0
- Content: Representative metadata for military intelligence data

**Creating DDMS Fixtures:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<ddms:Resource xmlns:ddms="http://metadata.dod.mil/mdr/ns/DDMS/2.0/">
  <ddms:identifier ddms:qualifier="URI" ddms:value="urn:test:sample:001"/>
  <ddms:title>Sample DDMS Metadata</ddms:title>
  <ddms:creator>
    <ddms:Organization>
      <ddms:name>Test Organization</ddms:name>
    </ddms:Organization>
  </ddms:creator>
  <ddms:subjectCoverage>
    <ddms:Subject>
      <ddms:keyword ddms:value="test"/>
    </ddms:Subject>
  </ddms:subjectCoverage>
  <ddms:security ddms:classification="U" ddms:ownerProducer="USA"/>
</ddms:Resource>
```

## Adding New Fixtures

### Process

1. **Create or obtain fixture**
   - Ensure it's unclassified/public
   - Verify format compliance

2. **Add to appropriate directory**
   ```bash
   cp my-fixture.ntf distribution/test/e2e/fixtures/nitf/
   ```

3. **Document the fixture**
   - Add entry to this README
   - Include purpose, format, size

4. **Reference in tests**
   ```java
   Path fixture = Paths.get("fixtures/nitf/my-fixture.ntf");
   byte[] data = Files.readAllBytes(fixture);
   ```

5. **Commit with clear message**
   ```bash
   git add distribution/test/e2e/fixtures/nitf/my-fixture.ntf
   git commit -m "test: add NITF fixture for multiband imagery testing"
   ```

### Fixture Guidelines

**DO:**
- ✅ Use small file sizes (< 10MB)
- ✅ Use realistic, representative data
- ✅ Include various data characteristics (size, complexity, metadata)
- ✅ Document what each fixture tests
- ✅ Verify fixtures are publicly releasable

**DON'T:**
- ❌ Commit classified or sensitive data
- ❌ Use excessively large files (bloats repository)
- ❌ Use data without proper licensing/permissions
- ❌ Leave fixtures undocumented

## Fixture Inventory

| Fixture | Format | Size | Purpose | Status |
|---------|--------|------|---------|--------|
| *(To be added)* | NITF | - | Basic NITF ingest test | ⏳ Pending |
| *(To be added)* | MPEG-TS+KLV | - | KLV metadata extraction | ⏳ Pending |
| *(To be added)* | DDMS XML | - | DDMS transformation | ⏳ Pending |

## External Resources

### Public Test Data Sources

1. **NITF/Imagery:**
   - [NGA Public Sample Data](https://www.nga.mil/)
   - [USGS EarthExplorer](https://earthexplorer.usgs.gov/)
   - [Sentinel Hub (Copernicus)](https://www.sentinel-hub.com/)

2. **Video/FMV:**
   - [MITRE Public FMV Datasets](https://www.mitre.org/)
   - NATO Public Sample Data (check NSGREG)

3. **DDMS:**
   - [DDMS Schema and Examples](https://metadata.ces.mil/)

### Tools

- **NITF Tools:**
  - NITF Explorer (NGA)
  - GDAL (with NITF driver)

- **Video Tools:**
  - FFmpeg (MPEG-TS muxing)
  - VideoLAN (analysis)

- **Metadata Tools:**
  - XMLSpy (XML validation)
  - Oxygen XML Editor

## Security Review

All fixtures in this directory have been reviewed and approved for:
- ✅ Public release (no classification concerns)
- ✅ No PII (Personally Identifiable Information)
- ✅ No proprietary/trade secret data
- ✅ Appropriate licensing for open-source use

**Reviewer:** (To be assigned)
**Review Date:** (To be assigned)

---

**Last Updated:** 2025-10-18
**Next Review:** TBD (when fixtures are added)
