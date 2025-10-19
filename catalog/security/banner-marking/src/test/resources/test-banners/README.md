# Test Banner Data Files

This directory contains test data files with valid and invalid banner markings for comprehensive testing of the banner-marking module.

## File Descriptions

### valid-us-banners.txt
Contains valid US classification banner markings organized by category:
- Basic US classifications (TOP SECRET, SECRET, CONFIDENTIAL, UNCLASSIFIED)
- US with dissemination controls (NOFORN, ORCON, PROPIN, etc.)
- US with SCI controls (HCS, TK, SI, COMINT, etc.)
- US with SAP controls (SAR, HVSACO)
- US with AEA markings (RD, FRD, UCNI)
- US with FGI country codes
- US with REL TO and DISPLAY ONLY
- US with other dissemination controls (EXDIS, NODIS, LIMDIS, etc.)
- US with ACCM
- Complex multi-control banners

### valid-fgi-banners.txt
Contains valid FGI (Foreign Government Information) banner markings:
- NATO markings (NATO SECRET//ATOMAL, COSMIC TOP SECRET//BOHEMIA, etc.)
- Country-specific FGI markings (//DEU SECRET, //CAN RESTRICTED, etc.)

### valid-joint-banners.txt
Contains valid JOINT banner markings:
- Joint with two countries
- Joint with three or more countries
- Joint with additional controls

### invalid-banners.txt
Contains invalid banner markings for negative testing, organized by violation type:
- Invalid FGI markings (with paragraph references)
- Invalid JOINT markings
- Invalid SCI controls
- Invalid SAP controls
- Invalid AEA markings
- Invalid FGI country codes
- Invalid dissemination controls
- Invalid REL TO and DISPLAY ONLY
- Invalid other dissemination controls
- Edge cases and malformed markings

Each invalid banner includes:
1. The invalid banner string
2. Expected paragraph reference from DoD 5200.1-M
3. Description of the violation

## File Format

### Valid Banner Files
```
<banner_string>|<description>
```

Example:
```
SECRET//NOFORN|SECRET with NOFORN
```

### Invalid Banner Files
```
<banner_string>|<expected_paragraph_reference>|<description>
```

Example:
```
RESTRICTED//RD|8.a.4.|RD requires CONFIDENTIAL or higher
```

## Usage in Tests

These files can be loaded in tests using standard Java file I/O:

```java
List<String> validBanners = Files.readAllLines(
    Paths.get("src/test/resources/test-banners/valid-us-banners.txt")
);

for (String line : validBanners) {
    if (line.startsWith("#") || line.trim().isEmpty()) {
        continue; // Skip comments and empty lines
    }
    String[] parts = line.split("\\|");
    String bannerString = parts[0];
    String description = parts[1];
    // Use in test...
}
```

## Maintenance

When adding new test cases:
1. Add to the appropriate file (valid-us-banners.txt, invalid-banners.txt, etc.)
2. Follow the existing format
3. Add descriptive comments for complex scenarios
4. Include paragraph references for invalid banners
5. Update this README if adding new categories

## References

All test data is based on:
- DoD 5200.1-M: DoD Information Security Program
- CAPCO Implementation Manual
- ISOO Marking Guidelines
