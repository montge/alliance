# Alliance End-to-End (E2E) Test Suite

## Overview

The E2E test suite provides comprehensive system-level testing of Alliance functionality in a deployed environment. These tests verify complete workflows from data ingest through search and retrieval, testing the system as users would interact with it.

## Purpose

E2E tests complement unit and integration tests by:
- **Validating complete workflows** - Full user scenarios from start to finish
- **Testing deployed configuration** - Real OSGi bundles, real services
- **Verifying system integration** - All components working together
- **Performance validation** - Real-world performance characteristics
- **Regression detection** - Catch issues that only appear in full deployment

## Test Pyramid Position

```
        /\
       /  \    E2E Tests (Slowest, High Value)
      /----\   - Full system deployment
     /      \  - Complete workflows
    /--------\ - Real data fixtures
   /          \
  /  Integration\  Integration Tests
 /--------------\ - Component interactions
/     Unit Tests \ Unit Tests (Fastest, Most Granular)
------------------
```

## Directory Structure

```
e2e/
├── README.md              # This file
├── pom.xml                # E2E test Maven configuration
├── common/                # Shared test utilities
│   ├── AllianceClient.java      # REST API client wrapper
│   ├── DockerManager.java       # Docker Compose lifecycle
│   ├── TestDataLoader.java      # Fixture management
│   └── Assertions.java          # Custom E2E assertions
├── scenarios/             # Test scenarios (actual tests)
│   ├── nitf-ingest/
│   │   ├── NitfIngestE2ETest.java
│   │   └── README.md
│   ├── video-klv/
│   │   ├── VideoKlvE2ETest.java
│   │   └── README.md
│   ├── federated-search/
│   │   ├── FederatedSearchE2ETest.java
│   │   └── README.md
│   └── security-marking/
│       ├── SecurityMarkingE2ETest.java
│       └── README.md
├── fixtures/              # Test data
│   ├── nitf/
│   │   ├── sample_nitf_2.1.ntf
│   │   └── README.md
│   ├── video/
│   │   ├── sample_klv_stream.ts
│   │   └── README.md
│   └── ddms/
│       ├── sample_ddms.xml
│       └── README.md
└── docker/                # Docker Compose configurations
    ├── docker-compose.yml       # Main Alliance deployment
    ├── docker-compose.test.yml  # Test-specific overrides
    └── README.md
```

## Prerequisites

### Required Software
- **Docker** 20.10+
- **Docker Compose** 2.0+
- **Java** 11+ (to run tests)
- **Maven** 3.9+

### Environment Setup

```bash
# Verify Docker is running
docker --version
docker-compose --version

# Build Alliance distribution first
cd /path/to/alliance
mvn clean install -DskipTests=true

# Distribution will be at:
# distribution/alliance/target/alliance-<version>.zip
```

## Running E2E Tests

### Full Suite

```bash
# From alliance root directory
mvn verify -P e2e-tests

# Or from this directory
cd distribution/test/e2e
mvn verify
```

### Single Scenario

```bash
# Run specific test class
mvn verify -P e2e-tests -Dit.test=NitfIngestE2ETest

# Run specific test method
mvn verify -P e2e-tests -Dit.test=NitfIngestE2ETest#testNitfIngestAndSearch
```

### With Docker Cleanup

```bash
# Stop and remove containers after tests
mvn verify -P e2e-tests -De2e.cleanup=true
```

### Skip E2E Tests (Default)

```bash
# E2E tests are skipped by default in regular builds
mvn install  # E2E tests NOT run
```

## Test Lifecycle

Each E2E test follows this lifecycle:

1. **Setup Phase** (BeforeAll)
   - Start Docker Compose with Alliance
   - Wait for Alliance to be ready (health check)
   - Load test fixtures if needed

2. **Test Execution**
   - Execute test scenario
   - Interact with Alliance via REST API
   - Verify expected outcomes

3. **Teardown Phase** (AfterAll)
   - Stop Docker containers
   - Clean up test data
   - Archive logs if test failed

## Writing E2E Tests

### Template

```java
package org.codice.alliance.test.e2e.scenarios;

import org.codice.alliance.test.e2e.common.AllianceClient;
import org.codice.alliance.test.e2e.common.DockerManager;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MyScenarioE2ETest {

    private DockerManager docker;
    private AllianceClient alliance;

    @BeforeAll
    void setUp() throws Exception {
        // Start Alliance in Docker
        docker = new DockerManager("docker/docker-compose.yml");
        docker.start();
        docker.waitForReady(Duration.ofMinutes(5));

        // Create API client
        alliance = new AllianceClient("http://localhost:8993");
        alliance.waitForAvailability(Duration.ofMinutes(2));
    }

    @Test
    void testMyScenario() {
        // Arrange
        byte[] testData = loadFixture("fixtures/my-test-file.dat");

        // Act
        String metacardId = alliance.ingest(testData, "application/octet-stream");

        // Assert
        assertThat(metacardId).isNotNull();

        var results = alliance.search("*");
        assertThat(results).hasSize(1);
    }

    @AfterAll
    void tearDown() {
        if (docker != null) {
            docker.stop();
        }
    }
}
```

### Best Practices

1. **Use Page Object Pattern**
   ```java
   AllianceCatalogPage catalog = alliance.catalog();
   catalog.ingest(nitfData);
   SearchResults results = catalog.search("title:sample");
   ```

2. **Wait for Async Operations**
   ```java
   // Bad: flaky
   alliance.ingest(data);
   var results = alliance.search("*"); // May not find it yet!

   // Good: wait for ingest to complete
   alliance.ingest(data);
   await().atMost(30, SECONDS)
         .until(() -> alliance.search("*").size() == 1);
   ```

3. **Use Meaningful Assertions**
   ```java
   // Bad
   assertThat(results.size()).isEqualTo(1);

   // Good
   assertThat(results)
       .as("Should find exactly one NITF image after ingest")
       .hasSize(1)
       .first()
       .hasFieldOrPropertyWithValue("title", "Expected Title");
   ```

4. **Clean Up Test Data**
   ```java
   @AfterEach
   void cleanupMetacards() {
       alliance.deleteAll(); // Remove test data between tests
   }
   ```

5. **Use Descriptive Test Names**
   ```java
   @Test
   void shouldExtractKlvMetadataFromStanag4609VideoStream() {
       // Test body
   }
   ```

## Test Scenarios

### 1. NITF Ingest (`scenarios/nitf-ingest/`)

**Purpose:** Verify NITF image ingest, metadata extraction, and searchability

**Workflow:**
1. Ingest NITF 2.1 image file
2. Verify metacard created with correct metadata
3. Search for ingested image
4. Retrieve image data
5. Generate thumbnail and overview

**Key Assertions:**
- NITF metadata fields extracted correctly
- Image dimensions, bit depth, geolocation present
- Classification markings handled properly
- Thumbnail generation successful

### 2. Video KLV (`scenarios/video-klv/`)

**Purpose:** Verify STANAG 4609 video ingest and KLV metadata extraction

**Workflow:**
1. Ingest MPEG-TS stream with KLV metadata
2. Verify KLV metadata extracted
3. Verify temporal metadata (time-series geolocation)
4. Search for video clips
5. Stream video playback

**Key Assertions:**
- KLV tags extracted correctly
- Temporal metadata tracks correctly
- Video segments searchable
- Streaming works properly

### 3. Federated Search (`scenarios/federated-search/`)

**Purpose:** Verify multi-node federated search functionality

**Workflow:**
1. Start 3 Alliance nodes
2. Configure federation between nodes
3. Ingest data to different nodes
4. Execute federated search from one node
5. Verify results from all nodes

**Key Assertions:**
- Federation configured correctly
- Search queries distributed to all nodes
- Results aggregated and deduplicated
- Response times acceptable

### 4. Security Marking (`scenarios/security-marking/`)

**Purpose:** Verify classification marking handling and access control

**Workflow:**
1. Ingest classified data with markings
2. Verify markings stored correctly
3. Verify access control enforced
4. Test marking propagation through transforms
5. Verify banner display

**Key Assertions:**
- Classification levels enforced
- Portion markings displayed
- Access control blocks unauthorized users
- Marking propagation correct

## Test Fixtures

### NITF Fixtures (`fixtures/nitf/`)

**Sample NITF files for testing:**
- `sample_nitf_2.1.ntf` - Basic NITF 2.1 image (512x512, 8-bit grayscale)
- `sample_nitf_classified.ntf` - NITF with classification markings
- `sample_nitf_large.ntf` - Large NITF for performance testing
- `sample_nitf_multiband.ntf` - Multi-band imagery

**Fixture Requirements:**
- Must be actual NITF 2.1 files (STANAG 4545 compliant)
- Should include various classification levels
- Must not contain real classified data (test data only!)

### Video Fixtures (`fixtures/video/`)

**Sample video files for testing:**
- `sample_klv_stream.ts` - MPEG-TS with STANAG 4609 KLV metadata
- `sample_klv_minimal.ts` - Minimal KLV tags
- `sample_klv_full.ts` - Comprehensive KLV tag set

**Fixture Requirements:**
- MPEG-TS format with embedded KLV
- KLV metadata must follow STANAG 4609
- Should include geolocation, timestamps, sensor data

### DDMS Fixtures (`fixtures/ddms/`)

**Sample DDMS XML files:**
- `sample_ddms_2.0.xml` - DDMS 2.0 metadata
- `sample_ddms_classified.xml` - DDMS with classification

## Docker Deployment

### Docker Compose Configuration

The `docker/docker-compose.yml` defines:
- Alliance instance with all features
- Solr for catalog backend
- Any required mock services

```yaml
version: '3.8'
services:
  alliance:
    image: alliance:latest
    build:
      context: ../../../distribution/alliance/target
      dockerfile: Dockerfile
    ports:
      - "8993:8993"  # HTTPS
      - "8181:8181"  # HTTP
    environment:
      - JAVA_MAX_MEM=4096m
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8181/admin"]
      interval: 30s
      timeout: 10s
      retries: 5
```

### Environment Variables

- `ALLIANCE_URL` - Base URL for Alliance (default: http://localhost:8993)
- `ALLIANCE_ADMIN_USER` - Admin username (default: admin)
- `ALLIANCE_ADMIN_PASSWORD` - Admin password (default: admin)
- `E2E_CLEANUP` - Cleanup containers after tests (default: false)
- `E2E_DEBUG` - Enable debug logging (default: false)

## Debugging E2E Tests

### View Container Logs

```bash
# View Alliance logs
docker logs alliance_e2e

# Follow logs in real-time
docker logs -f alliance_e2e

# View all service logs
docker-compose logs -f
```

### Connect to Running Container

```bash
# Connect to Alliance container
docker exec -it alliance_e2e /bin/bash

# Check Alliance status
curl http://localhost:8181/admin
```

### Debug Test Execution

```bash
# Run with debug logging
mvn verify -P e2e-tests -De2e.debug=true -X

# Pause on test failure
mvn verify -P e2e-tests -Dmaven.test.failure.ignore=true
```

### Keep Containers Running After Test

```bash
# Don't cleanup on failure (for investigation)
mvn verify -P e2e-tests -De2e.cleanup=false

# Then investigate:
docker ps
docker logs alliance_e2e
docker exec -it alliance_e2e /bin/bash
```

## Performance Considerations

### Test Execution Time

| Scenario | Expected Duration | Timeout |
|----------|------------------|---------|
| NITF Ingest | ~30 seconds | 2 minutes |
| Video KLV | ~45 seconds | 3 minutes |
| Federated Search | ~60 seconds | 5 minutes |
| Security Marking | ~30 seconds | 2 minutes |

**Total Suite:** ~3-5 minutes (sequential execution)

### Optimization Tips

1. **Parallel Execution** (Future)
   ```bash
   mvn verify -P e2e-tests -Dparallel=classes -DthreadCount=2
   ```

2. **Reuse Containers** (Future)
   - Start Alliance once, run all tests
   - Cleanup between tests, not between classes

3. **Selective Execution**
   ```bash
   # Run only fast tests
   mvn verify -P e2e-tests -Dgroups=fast
   ```

## CI/CD Integration

### GitHub Actions

E2E tests will run:
- On PR to `master` (optional, triggered manually)
- On merge to `master` (always)
- Nightly (comprehensive suite)

```yaml
# .github/workflows/e2e-tests.yml
name: E2E Tests

on:
  pull_request:
    branches: [master]
  schedule:
    - cron: '0 2 * * *'  # 2 AM UTC daily

jobs:
  e2e-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - name: Run E2E Tests
        run: mvn verify -P e2e-tests
      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: e2e-test-results
          path: distribution/test/e2e/target/failsafe-reports/
```

## Troubleshooting

### Common Issues

**Problem:** Docker containers fail to start
**Solution:**
```bash
# Check Docker is running
docker ps

# Check for port conflicts
lsof -i :8993
lsof -i :8181

# Clean up old containers
docker-compose down -v
```

**Problem:** Alliance takes too long to start
**Solution:**
- Increase health check timeout
- Check Docker resource limits (CPU, memory)
- Review Alliance logs for startup issues

**Problem:** Tests are flaky
**Solution:**
- Add explicit waits for async operations
- Increase timeouts
- Check for test data pollution between tests

**Problem:** Fixtures not found
**Solution:**
- Verify fixtures directory structure
- Check file paths in tests
- Ensure fixtures are included in test resources

## Contributing

### Adding New Scenarios

1. Create scenario directory: `scenarios/my-scenario/`
2. Write test class: `MyScenarioE2ETest.java`
3. Add fixtures: `fixtures/my-scenario/`
4. Document in `scenarios/my-scenario/README.md`
5. Update this README with scenario description

### Fixture Guidelines

- **No Real Classified Data** - Use test/sample data only
- **Small Files** - Keep fixtures under 10MB
- **Realistic** - Should represent actual use cases
- **Documented** - Include README explaining fixture purpose

## References

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Testcontainers](https://www.testcontainers.org/)
- [Docker Compose](https://docs.docker.com/compose/)
- [NITF Specification (STANAG 4545)](https://nsgreg.nga.mil/NSGDOC/files/doc/Document/STANAG4545.pdf)
- [STANAG 4609](https://nsgreg.nga.mil/NSGDOC/files/doc/Document/STANAG_4609.pdf)

## Status

**Current State:** Framework structure created, tests to be implemented

**Next Steps:**
1. Implement `common/` utilities (AllianceClient, DockerManager)
2. Create Docker Compose configuration
3. Implement first scenario (NITF Ingest)
4. Add test fixtures
5. Integrate with CI/CD

---

**Last Updated:** 2025-10-18
**Maintained By:** Alliance Development Team
