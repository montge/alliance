# Alliance Software Architecture

**Document Version:** 1.0
**Status:** DRAFT
**Last Updated:** 2025-10-18
**Approved By:** TBD

## Document Purpose

This document describes the software architecture of the Codice Alliance system, including:
- High-level architecture and design principles
- Major components and their interactions
- Technology stack and frameworks
- Design patterns and rationale
- Data flow and processing pipelines

This documentation supports DO-278 compliance by providing design traceability from requirements to implementation.

## Executive Summary

Alliance is a modular, OSGi-based integration framework built on DDF (Distributed Data Framework) that provides specialized support for military and intelligence data formats (NITF/NSIF imagery, STANAG 4609 video, DDMS metadata).

**Key Architectural Characteristics:**
- **Modularity**: OSGi bundle-based plugin architecture
- **Extensibility**: New data formats added without core changes
- **Security**: Classification-aware from ground up
- **Interoperability**: Standards-based (OGC, REST, OpenSearch)
- **Scalability**: Federated deployment model

## Architecture Overview

### Layered Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    User Interface Layer                         │
│  (Intrigue Search UI, Admin Console, External Clients)          │
└────────────────────────┬────────────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────────────┐
│                   Service Layer (REST/SOAP)                      │
│  (Catalog API, OGC Services, OpenSearch, Custom REST APIs)      │
└────────────────────────┬────────────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────────────┐
│              Business Logic Layer (OSGi Bundles)                 │
│ ┌──────────────┬──────────────┬──────────────┬────────────────┐│
│ │   Imaging    │    Video     │   Security   │   Catalog     ││
│ │ (NITF/NSIF)  │ (STANAG4609) │ (Markings)   │    Core       ││
│ └──────────────┴──────────────┴──────────────┴────────────────┘│
│ ┌──────────────┬──────────────┬──────────────┬────────────────┐│
│ │ Transformers │   Plugins    │  Validators  │   Sources     ││
│ └──────────────┴──────────────┴──────────────┴────────────────┘│
└────────────────────────┬────────────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────────────┐
│              Integration Layer (Apache Camel)                    │
│  (Message routing, Protocol adaptation, Enterprise patterns)     │
└────────────────────────┬────────────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────────────┐
│            Data Layer (Storage & Indexing)                       │
│ ┌──────────────┬──────────────┬──────────────┬────────────────┐│
│ │Apache Solr   │Content Store │Metadata DB   │Cache Layer    ││
│ │(Indexing)    │(Files)       │(Metacards)   │(Hazelcast)    ││
│ └──────────────┴──────────────┴──────────────┴────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

### Component View

```
Alliance
├── Catalog (org.codice.alliance.catalog)
│   ├── Core
│   │   ├── catalog-core-api (Interfaces)
│   │   ├── catalog-core-api-impl (Implementation)
│   │   ├── catalog-core-metacardtypes (Data models)
│   │   ├── catalog-core-classification-api (Security interfaces)
│   │   └── catalog-core-classification-impl (Security implementation)
│   ├── Imaging (NITF/NSIF processing)
│   │   ├── imaging-nitf-api (NITF interfaces)
│   │   ├── imaging-nitf-impl (NITF parser)
│   │   ├── imaging-transformer-nitf (NITF → Metacard)
│   │   ├── imaging-plugin-nitf (Post-ingest processing)
│   │   ├── imaging-service-api (Image processing)
│   │   └── imaging-service-impl (Thumbnail, chipping)
│   ├── Video (STANAG 4609 / FMV)
│   │   ├── video-mpegts-stream (MPEG-TS streaming)
│   │   ├── video-mpegts-transformer (KLV extraction)
│   │   └── video-security (Video-specific security)
│   ├── Security (Classification & Access Control)
│   │   └── banner-marking (Classification banners)
│   ├── Plugin (Catalog extensions)
│   │   ├── catalog-plugin-auditcontrolledaccess (Audit logging)
│   │   └── catalog-plugin-defaultsecurityattributevalues (Default markings)
│   └── DDMS (DoD Discovery Metadata)
│       └── catalog-ddms-transformer (DDMS XML processing)
├── Libs (Low-level libraries)
│   ├── stanag4609 (STANAG 4609 protocol)
│   ├── klv (Key-Length-Value metadata)
│   └── mpegts (MPEG Transport Stream)
└── Distribution (Deployable artifacts)
    ├── alliance (Main distribution)
    ├── branding (UI customization)
    ├── docker (Container images)
    └── test/itests (Integration tests)
```

## Design Principles

### 1. OSGi Modularity

**Rationale**: Enables runtime modularity, plugin hot-deployment, and clean dependency management.

**Implementation**:
- Each component is an OSGi bundle
- Blueprint XML for dependency injection
- OSGi services for component communication
- Apache Karaf as OSGi container

**Benefits**:
- Modules independently deployable
- Clean separation of concerns
- Testability (mock OSGi services)
- Runtime reconfiguration

### 2. Transformer Pattern

**Rationale**: Decouples data format parsing from catalog logic.

**Implementation**:
```
Input Format (NITF, MPEG-TS, DDMS)
        ↓
InputTransformer (format-specific)
        ↓
Metacard (canonical format)
        ↓
Catalog Framework
        ↓
Storage/Index
```

**Key Classes**:
- `ddf.catalog.transform.InputTransformer`
- `org.codice.alliance.imaging.transformer.nitf.NitfInputTransformer`
- `org.codice.alliance.video.transformer.mpegts.MpegTsInputTransformer`

**Benefits**:
- Add new formats without changing catalog
- Format parsing isolated and testable
- Canonical internal representation

### 3. Plugin Extension Points

**Rationale**: Allow functionality injection at key processing points.

**Plugin Types**:

| Plugin Type | Interface | Purpose | Example |
|-------------|-----------|---------|---------|
| Pre-Ingest | `PreIngestPlugin` | Validate/enrich before storage | Security validation |
| Post-Ingest | `PostIngestPlugin` | Process after storage | Thumbnail generation |
| Pre-Query | `PreQueryPlugin` | Modify queries before execution | Security filtering |
| Post-Query | `PostQueryPlugin` | Filter/modify results | Classification redaction |
| Access Control | `AccessPlugin` | Authorization decisions | Need-to-know enforcement |

**Example - NITF Post-Ingest Plugin**:
```java
@Component
public class NitfPostIngestPlugin implements PostIngestPlugin {

    @Override
    public CreateResponse process(CreateResponse input) {
        // Generate thumbnails and overviews for NITF images
        // after they've been ingested into catalog
    }
}
```

### 4. Metacard as Canonical Model

**Rationale**: Single internal representation regardless of input format.

**Structure**:
```
Metacard
├── Attributes (flexible key-value pairs)
│   ├── Core attributes (id, title, created, modified)
│   ├── Media attributes (format, dimensions, duration)
│   ├── Location attributes (WKT geometry, coordinates)
│   ├── Temporal attributes (effective, expiration)
│   └── Security attributes (classification, releasability)
├── Type (defines schema)
└── Resource URI (link to actual content)
```

**Extension**: Alliance adds military-specific metacard types
- `NitfMetacardType` - NITF-specific fields
- `VideoMetacardType` - Video/FMV fields
- `IcMetacardType` - Intelligence community fields

### 5. Security by Design

**Rationale**: Classification handling cannot be added later, must be intrinsic.

**Security Layers**:

1. **Transport Security**: TLS for all network communication
2. **Authentication**: SAML 2.0, PKI certificates, LDAP
3. **Authorization**: Attribute-based access control (ABAC)
4. **Data Security**: Classification markings on every metacard
5. **Audit**: All access logged for compliance

**Key Components**:
- `SecurityManager` - Authentication/authorization
- `FilterPlugin` - Redact results based on clearance
- `SecurityAttributesPlugin` - Enforce marking rules
- `AuditPlugin` - Log security events

### 6. Federation Architecture

**Rationale**: Enable distributed search across multiple sites.

**Federation Model**:
```
Client Query
    ↓
Local Catalog Framework
    ↓
├─> Local Source (search local catalog)
├─> CSW Federated Source (remote site 1)
├─> OpenSearch Federated Source (remote site 2)
└─> Custom Federated Source (remote site 3)
    ↓
Result Aggregation
    ↓
Result Filtering (security)
    ↓
Client Response
```

**Benefits**:
- Data stays at source (no central repository)
- Scalable (add sources without changing core)
- Security preserved (source-level markings honored)

## Technology Stack

### Core Frameworks

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| OSGi Container | Apache Karaf | 4.4.8 | Module runtime |
| OSGi Framework | Apache Felix | (via Karaf) | OSGi implementation |
| DI/Service Registry | Blueprint XML | OSGi Blueprint 1.0 | Dependency injection |
| Integration | Apache Camel | 3.22.4 | Enterprise integration patterns |
| Web Services | Apache CXF | (via DDF) | SOAP/REST endpoints |
| Search/Index | Apache Solr | (via DDF) | Full-text and spatial search |
| Caching | Hazelcast | (via DDF) | Distributed cache |
| Build | Maven | 3.9+ | Build automation |
| Testing | JUnit 4 | 4.13.2 | Unit testing |
| Testing | Spock | 2.3 | BDD testing |
| Testing | Mockito | 4.11.0 | Mocking |
| Coverage | JaCoCo | (via Maven) | Code coverage |

### Alliance-Specific Libraries

| Library | Purpose | Location |
|---------|---------|----------|
| codice-imaging-nitf | NITF parsing | External dependency |
| jcodec | Video codec | External dependency |
| geotools | Geospatial operations | External dependency |
| usng4j | USNG coordinate conversion | External dependency |
| KLV parser | KLV metadata extraction | libs/klv |
| STANAG 4609 | Video standard support | libs/stanag4609 |
| MPEG-TS | Transport stream handling | libs/mpegts |

## Key Design Patterns

### 1. Facade Pattern - Catalog Framework

The `CatalogFramework` provides a unified interface to catalog operations:

```java
public interface CatalogFramework {
    CreateResponse create(CreateRequest request);
    UpdateResponse update(UpdateRequest request);
    DeleteResponse delete(DeleteRequest request);
    QueryResponse query(QueryRequest request);
    ResourceResponse getResource(ResourceRequest request);
}
```

**Rationale**: Simplifies client code, hides complexity of plugins, sources, and federation.

### 2. Strategy Pattern - Transformers

Different transformation strategies for different formats:

```java
public interface InputTransformer {
    Metacard transform(InputStream input) throws Exception;
    Metacard transform(InputStream input, String id) throws Exception;
}

// Strategies:
- NitfInputTransformer (for NITF files)
- MpegTsInputTransformer (for video)
- DdmsInputTransformer (for DDMS XML)
```

### 3. Chain of Responsibility - Plugins

Plugins form processing chains:

```
Request → PreIngestPlugin₁ → PreIngestPlugin₂ → ...
  → IngestOperation
  → PostIngestPlugin₁ → PostIngestPlugin₂ → ... → Response
```

Each plugin can:
- Pass request through unchanged
- Modify request/response
- Stop chain (validation failure)

### 4. Observer Pattern - Event Notifications

Catalog events broadcast to subscribers:

```java
public interface EventAdmin {
    void postEvent(Event event);
    void sendEvent(Event event);
}

// Events:
- CREATED (metacard created)
- UPDATED (metacard modified)
- DELETED (metacard removed)
```

**Usage**:
- UI updates on catalog changes
- Trigger workflows on ingest
- Audit logging

### 5. Factory Pattern - Metacard Types

Metacard type factories create format-specific types:

```java
public interface MetacardType {
    String getName();
    Set<AttributeDescriptor> getAttributeDescriptors();
}

// Factories create:
- BasicTypes.BASIC_METACARD
- NitfMetacardType
- VideoMetacardType
```

## Data Flow Diagrams

### NITF Ingest Flow

```
1. Client uploads NITF file
        ↓
2. Content Framework receives file
        ↓
3. InputTransformer auto-detected (by MIME type)
        ↓
4. NitfInputTransformer.transform()
   ├─> Parse NITF header
   ├─> Extract image segments
   ├─> Parse TREs (Tagged Record Extensions)
   ├─> Extract classification markings
   ├─> Generate WKT geometry from coords
   └─> Build NitfMetacard
        ↓
5. PreIngestPlugins execute
   ├─> Validate security markings
   ├─> Set default attributes
   └─> Enrich with external data
        ↓
6. CatalogFramework.create()
   ├─> Store file in ContentStore
   ├─> Index metacard in Solr
   └─> Store metacard in database
        ↓
7. PostIngestPlugins execute
   ├─> NitfPostIngestPlugin generates thumbnail
   ├─> NitfPostIngestPlugin generates overview
   └─> Update metacard with derived resources
        ↓
8. Event posted (CREATED event)
        ↓
9. Response returned to client
```

### Video Stream Processing Flow

```
1. Client requests FMV stream
        ↓
2. Video Streaming Service receives request
        ↓
3. MpegTsTransformer processes stream
   ├─> Parse MPEG-TS packets
   ├─> Extract KLV metadata packets
   ├─> Parse KLV per STANAG 4609
   ├─> Extract temporal metadata
   │   ├─> Timestamp
   │   ├─> Platform position
   │   ├─> Sensor position
   │   ├─> Frame center coords
   │   └─> Slant range
   └─> Build VideoMetacard per chunk
        ↓
4. Metacards ingested (as above)
        ↓
5. Video streamed to client
   ├─> Original MPEG-TS
   └─> Synchronized KLV metadata sidebar
```

### Federated Search Flow

```
1. Client submits query
        ↓
2. PreQueryPlugins execute
   ├─> Inject security filter (user clearance)
   ├─> Expand synonyms
   └─> Validate query syntax
        ↓
3. CatalogFramework distributes query
   ├─> Local Source (this instance)
   ├─> CSW Source 1 (Site A)
   ├─> CSW Source 2 (Site B)
   └─> OpenSearch Source (Site C)
        │ (parallel execution)
        ↓
4. Results aggregated
   ├─> Deduplicate by ID
   ├─> Merge relevance scores
   └─> Sort by specified criteria
        ↓
5. PostQueryPlugins execute
   ├─> Filter by user clearance
   ├─> Redact classified portions
   └─> Apply result limits
        ↓
6. Results returned to client
```

## Component Interactions

### NITF Processing Components

```
┌─────────────────────┐
│  NitfInputTransformer│
│  (Catalog module)   │
└──────────┬──────────┘
           │ uses
           ↓
┌─────────────────────┐
│ NitfParser          │
│ (imaging-nitf-impl) │
└──────────┬──────────┘
           │ uses
           ↓
┌─────────────────────┐
│ codice-imaging-nitf │
│ (External library)  │
└─────────────────────┘
```

Post-ingest:
```
┌─────────────────────┐
│ NitfPostIngestPlugin│
└──────────┬──────────┘
           │ uses
           ↓
┌─────────────────────┐
│ ImagingService      │
│ (Thumbnail gen)     │
└──────────┬──────────┘
           │ uses
           ↓
┌─────────────────────┐
│ JAI / ImageIO       │
│ (Java imaging)      │
└─────────────────────┘
```

### Security Component Interactions

```
┌─────────────────────┐
│ Client Request      │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ SecurityManager     │
│ (Authenticate)      │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ SAML / PKI Handler  │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ User attributes     │
│ (Clearance, etc.)   │
└──────────┬──────────┘
           ↓ (attached to request)
┌─────────────────────┐
│ Catalog Operation   │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ FilterPlugin        │
│ (Authorization)     │
└──────────┬──────────┘
           ↓ (checks)
┌─────────────────────┐
│ Metacard markings   │
│ vs User clearance   │
└─────────────────────┘
```

## Configuration Management

### Blueprint XML Configuration

Services registered via Blueprint XML:

```xml
<!-- Example: NITF Transformer Registration -->
<blueprint xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0">

  <bean id="nitfInputTransformer"
        class="org.codice.alliance.imaging.transformer.nitf.NitfInputTransformer">
    <property name="nitfParser" ref="nitfParser"/>
  </bean>

  <service ref="nitfInputTransformer"
           interface="ddf.catalog.transform.InputTransformer">
    <service-properties>
      <entry key="id" value="nitf"/>
      <entry key="mime-type" value="image/nitf"/>
    </service-properties>
  </service>

</blueprint>
```

**Benefits**:
- Declarative service registration
- Dependency injection
- Runtime reconfiguration

### Karaf Features

Modules grouped into installable features:

```xml
<feature name="imaging-app" version="${project.version}">
  <feature>catalog-app</feature>
  <feature>imaging-nitf-transformer</feature>
  <feature>nitf-render-plugin</feature>
  <bundle>mvn:org.codice.alliance.imaging/imaging-service-impl</bundle>
  <bundle>mvn:org.codice.alliance.imaging/imaging-transformer-chipping</bundle>
</feature>
```

**Benefits**:
- Install/uninstall feature sets
- Dependency resolution
- Version management

## Deployment Architecture

### Standalone Deployment

```
┌────────────────────────────────────┐
│         Alliance Instance           │
│  ┌──────────────────────────────┐  │
│  │     Apache Karaf             │  │
│  │  ┌────────┐  ┌────────┐     │  │
│  │  │Catalog │  │Imaging │     │  │
│  │  │bundles │  │bundles │     │  │
│  │  └────────┘  └────────┘     │  │
│  │  ┌────────┐  ┌────────┐     │  │
│  │  │Video   │  │Security│     │  │
│  │  │bundles │  │bundles │     │  │
│  │  └────────┘  └────────┘     │  │
│  └──────────────────────────────┘  │
│  ┌──────────────────────────────┐  │
│  │      Apache Solr             │  │
│  └──────────────────────────────┘  │
│  ┌──────────────────────────────┐  │
│  │      Content Store           │  │
│  └──────────────────────────────┘  │
└────────────────────────────────────┘
```

### Federated Deployment

```
     ┌─────────────┐
     │   Client    │
     └──────┬──────┘
            │
     ┌──────┴──────────────┐
     │                     │
┌────┴────┐          ┌─────┴────┐
│Alliance │          │ Alliance │
│ Site A  │◄────────►│  Site B  │
└────┬────┘          └─────┬────┘
     │                     │
     │              ┌──────┴──────┐
     │              │             │
┌────┴────┐    ┌────┴────┐  ┌────┴────┐
│Alliance │    │Alliance │  │Alliance │
│ Site C  │    │ Site D  │  │ Site E  │
└─────────┘    └─────────┘  └─────────┘

Each site:
- Independent catalog
- Local data
- Federated query capability
- Security enforcement
```

## Quality Attributes

### Modifiability
- **Design**: OSGi modularity, plugin architecture
- **Benefit**: New formats added without core changes
- **Tradeoff**: Complexity of OSGi container

### Security
- **Design**: Classification intrinsic to metacard model
- **Benefit**: Cannot bypass security
- **Tradeoff**: Performance overhead of access checks

### Performance
- **Design**: Solr indexing, caching layer
- **Benefit**: Fast search even with millions of items
- **Tradeoff**: Index build time, storage requirements

### Interoperability
- **Design**: Standards-based (OGC, REST, OpenSearch)
- **Benefit**: Integration with external systems easy
- **Tradeoff**: Must follow spec even if limiting

### Testability
- **Design**: OSGi service mocking, dependency injection
- **Benefit**: 75% coverage baseline
- **Tradeoff**: Mock setup complexity

## Design Decisions

### Why OSGi?
**Decision**: Use OSGi for modularity
**Alternatives Considered**: Microservices, Monolith with DI
**Rationale**:
- Need hot-deploy for operational systems
- Module isolation critical for stability
- DDF already uses OSGi (inheritance)
**Tradeoffs**:
- (+) Runtime modularity
- (+) Clean dependencies
- (-) Learning curve
- (-) Debugging complexity

### Why Blueprint XML vs Annotations?
**Decision**: Use Blueprint XML for service registration
**Alternatives Considered**: Declarative Services, Spring DM
**Rationale**:
- DDF standard approach
- Runtime configuration changes
- Clear service dependencies
**Tradeoffs**:
- (+) Runtime reconfiguration
- (+) No compilation required
- (-) XML verbosity
- (-) Limited IDE support

### Why Solr for Indexing?
**Decision**: Apache Solr for search/index
**Alternatives Considered**: Elasticsearch, Custom index
**Rationale**:
- Excellent geospatial support
- Proven scalability
- DDF integration exists
**Tradeoffs**:
- (+) Full-text + spatial search
- (+) Faceted search
- (-) Separate process to manage
- (-) Memory footprint

## References

- DDF Architecture: https://codice.atlassian.net/wiki
- OSGi Specification: https://www.osgi.org/developer/specifications/
- Apache Karaf Documentation: https://karaf.apache.org/manual/latest/
- Apache Camel Patterns: https://camel.apache.org/components/latest/eips/enterprise-integration-patterns.html
- STANAG 4545 (NITF): MIL-STD-2500C
- STANAG 4609: STANAG 4609 Edition 3

## Approval

| Role | Name | Signature | Date |
|------|------|-----------|------|
| Chief Architect | TBD | | |
| Security Architect | TBD | | |
| Project Manager | TBD | | |

---

**Next Steps:**
1. Detailed design documents for each module
2. Sequence diagrams for critical workflows
3. API documentation (OpenAPI/Swagger)
4. Threat modeling and security architecture deep-dive
5. Performance benchmarking and optimization guide
