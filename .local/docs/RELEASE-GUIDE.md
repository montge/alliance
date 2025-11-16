# Alliance Release Guide

This guide covers creating versioned releases and nightly builds for the Alliance project.

## Table of Contents

1. [Quick Start](#quick-start)
2. [Release Types](#release-types)
3. [Versioned Releases](#versioned-releases)
4. [Nightly Builds](#nightly-builds)
5. [Manual Release Process](#manual-release-process)
6. [Troubleshooting](#troubleshooting)

---

## Quick Start

### Using GitHub Actions (Recommended)

**Versioned Release:**
1. Go to: https://github.com/montge/alliance/actions/workflows/release.yml
2. Click "Run workflow"
3. Select "versioned" and enter version (e.g., `1.18.0`)
4. Wait for workflow to complete (~30-45 minutes)
5. Release appears at: https://github.com/montge/alliance/releases

**Nightly Build:**
1. Go to: https://github.com/montge/alliance/actions/workflows/release.yml
2. Click "Run workflow"
3. Select "nightly"
4. Wait for workflow to complete (~30-45 minutes)

---

## Release Types

### Versioned Release (Production)
- **Purpose:** Stable, production-ready releases
- **Version Format:** `X.Y.Z` (e.g., `1.18.0`)
- **Git Tags:** `alliance-X.Y.Z`
- **Retention:** Permanent
- **Use Cases:** Production deployments, customer releases

### Nightly Build (Development)
- **Purpose:** Latest development build for testing
- **Version Format:** `X.Y.Z-SNAPSHOT` with timestamp
- **Git Tags:** `nightly-YYYYMMDD-HHMMSS`
- **Retention:** Last 7 builds (auto-cleanup)
- **Use Cases:** Development, testing, continuous integration

---

## Versioned Releases

### Using GitHub Actions

1. **Navigate to Workflow:**
   ```
   https://github.com/montge/alliance/actions/workflows/release.yml
   ```

2. **Trigger Release:**
   - Click "Run workflow" button
   - Select branch: `master`
   - Release type: `versioned`
   - Version: Enter version number (e.g., `1.18.0`)

3. **What Happens:**
   ```
   ┌─────────────────────────────────────────────┐
   │ GitHub Actions Workflow                     │
   ├─────────────────────────────────────────────┤
   │ 1. Checkout code                            │
   │ 2. Set up Java 17 + Maven                   │
   │ 3. Run maven-release-plugin                 │
   │    - Updates POMs to release version        │
   │    - Creates git tag: alliance-X.Y.Z        │
   │    - Builds distribution ZIP                │
   │    - Increments to next SNAPSHOT            │
   │ 4. Create GitHub Release                    │
   │    - Attach alliance-X.Y.Z.zip              │
   │    - Generate release notes                 │
   │ 5. Push tags and commits to GitHub          │
   └─────────────────────────────────────────────┘
   ```

4. **Artifacts Created:**
   - `alliance-X.Y.Z.zip` (~2 GB distribution)
   - Git tag: `alliance-X.Y.Z`
   - GitHub Release with notes

5. **Post-Release:**
   - Download from: https://github.com/montge/alliance/releases
   - Master branch version updated to `X.Y.(Z+1)-SNAPSHOT`

### Versioning Convention

Alliance follows **Semantic Versioning (SemVer):**

- **MAJOR.MINOR.PATCH** (e.g., `1.18.0`)
  - **MAJOR:** Breaking changes, major features
  - **MINOR:** New features, backwards-compatible
  - **PATCH:** Bug fixes, security patches

**Examples:**
- `1.17.5` → `1.18.0` (new features: Phase 2 coverage, Phase 3 security framework)
- `1.18.0` → `1.18.1` (bug fix or security patch)
- `1.18.1` → `2.0.0` (major breaking changes)

**Current Version:**
```bash
# Check current version
grep "<version>" pom.xml | head -1
# Output: <version>1.17.5-SNAPSHOT</version>
```

---

## Nightly Builds

### Using GitHub Actions

1. **Navigate to Workflow:**
   ```
   https://github.com/montge/alliance/actions/workflows/release.yml
   ```

2. **Trigger Nightly Build:**
   - Click "Run workflow" button
   - Select branch: `master`
   - Release type: `nightly`
   - Click "Run workflow"

3. **What Happens:**
   ```
   ┌─────────────────────────────────────────────┐
   │ GitHub Actions Workflow                     │
   ├─────────────────────────────────────────────┤
   │ 1. Checkout latest master                  │
   │ 2. Set up Java 17 + Maven                   │
   │ 3. Build distribution                       │
   │    mvn clean install -DskipTests            │
   │ 4. Create timestamped tag                   │
   │    nightly-YYYYMMDD-HHMMSS                  │
   │ 5. Create GitHub Pre-release                │
   │    - Mark as prerelease (not production)    │
   │    - Attach alliance-X.Y.Z-SNAPSHOT.zip     │
   │ 6. Cleanup old nightly builds               │
   │    - Keep last 7 builds                     │
   │    - Delete older releases                  │
   └─────────────────────────────────────────────┘
   ```

4. **Artifacts Created:**
   - `alliance-X.Y.Z-SNAPSHOT.zip` (~2 GB)
   - Git tag: `nightly-YYYYMMDD-HHMMSS`
   - GitHub Pre-release (marked as prerelease)

5. **Retention Policy:**
   - Automatic cleanup keeps last 7 nightly builds
   - Older builds are automatically deleted

### Automated Nightly Builds (Optional)

To enable automatic daily nightly builds at 2 AM UTC:

**Edit `.github/workflows/release.yml`:**

Uncomment the schedule section at the bottom:
```yaml
on:
  workflow_dispatch:
    # ... existing inputs ...
  schedule:
    - cron: '0 2 * * *'  # 2 AM UTC daily
```

**Commit and push:**
```bash
git add .github/workflows/release.yml
git commit -m "Enable automated nightly builds"
git push origin master
```

---

## Manual Release Process

### Prerequisites

```bash
# Ensure clean working directory
git status
# Should show: "nothing to commit, working tree clean"

# Ensure you're on master branch
git checkout master
git pull origin master
```

### Versioned Release (Manual)

```bash
# 1. Prepare release (updates POMs, creates tag)
mvn release:prepare -DreleaseVersion=1.18.0 -Dtag=alliance-1.18.0

# 2. Build distribution
git checkout alliance-1.18.0
mvn clean install -Prelease -DskipTests=true

# 3. Distribution created at:
ls -lh distribution/alliance/target/alliance-1.18.0.zip

# 4. Create GitHub release
gh release create alliance-1.18.0 \
  --title "Alliance 1.18.0" \
  --notes "Release notes here..." \
  distribution/alliance/target/alliance-1.18.0.zip

# 5. Push to GitHub
git checkout master
git push origin master
git push origin alliance-1.18.0
```

### Nightly Build (Manual)

```bash
# 1. Build latest snapshot
mvn clean install -DskipTests=true

# 2. Create timestamped tag
BUILD_TIMESTAMP=$(date -u +"%Y%m%d-%H%M%S")
git tag -a nightly-${BUILD_TIMESTAMP} -m "Nightly build ${BUILD_TIMESTAMP}"
git push origin nightly-${BUILD_TIMESTAMP}

# 3. Create GitHub pre-release
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

gh release create nightly-${BUILD_TIMESTAMP} \
  --title "Alliance Nightly Build (${BUILD_TIMESTAMP})" \
  --notes "Automated nightly build from master branch" \
  --prerelease \
  distribution/alliance/target/alliance-${CURRENT_VERSION}.zip
```

---

## Release Checklist

### Pre-Release

- [ ] All tests passing: `mvn clean test`
- [ ] Code coverage meets targets: `mvn jacoco:report`
- [ ] Security scans complete: `mvn dependency-check:aggregate`
- [ ] Documentation updated (README.md, CHANGELOG.md)
- [ ] Version number decided (follow SemVer)
- [ ] Clean working directory: `git status`

### During Release

- [ ] Release workflow triggered or manual commands executed
- [ ] Build completes successfully
- [ ] Distribution ZIP created (~2 GB)
- [ ] Git tag created
- [ ] GitHub Release created

### Post-Release

- [ ] Verify release download: https://github.com/montge/alliance/releases
- [ ] Test distribution ZIP:
  ```bash
  unzip alliance-X.Y.Z.zip
  cd alliance-X.Y.Z
  ./bin/alliance  # Should start without errors
  ```
- [ ] Update CHANGELOG.md with release notes
- [ ] Announce release (mailing list, Slack, etc.)

---

## Troubleshooting

### Problem: "Version must be in format X.Y.Z"

**Cause:** Invalid version format entered

**Solution:** Use semantic versioning format: `MAJOR.MINOR.PATCH`
- ✅ Good: `1.18.0`, `2.0.0`, `1.17.6`
- ❌ Bad: `v1.18.0`, `1.18`, `1.18.0-RC1`

### Problem: "Nightly builds require SNAPSHOT version"

**Cause:** Current version in pom.xml is not a SNAPSHOT

**Solution:**
```bash
# Check current version
grep "<version>" pom.xml | head -1

# If not SNAPSHOT, update manually:
mvn versions:set -DnewVersion=1.18.0-SNAPSHOT
git add pom.xml
git commit -m "Update to 1.18.0-SNAPSHOT"
git push origin master
```

### Problem: "No space left on device" During Docker Build

**Cause:** GitHub Actions runner ran out of disk space during Docker image build

**Context:** The Alliance distribution is ~2GB, and the Docker build process temporarily requires even more space. GitHub Actions free runners have limited disk space (~14GB available after OS).

**Solution:** The workflow has been updated to skip Docker builds for nightly releases:
```bash
# Nightly builds now skip Docker module
mvn clean install -DskipTests=true -pl !distribution/docker
```

**Impact:** Nightly releases won't include Docker images, but the ZIP distribution is still created and sufficient for testing.

**For Versioned Releases:** If disk space issues occur, consider:
1. Using self-hosted runners with more disk space
2. Cleaning up intermediate build artifacts before Docker build
3. Using GitHub Actions with larger disk quotas

### Problem: Build Fails with "Tests Failed"

**Cause:** Unit tests are failing

**Solution 1 - Fix Tests (Recommended):**
```bash
# Identify failing tests
mvn test

# Fix the failing tests, then re-run release
```

**Solution 2 - Skip Tests (Emergency Only):**
```bash
# GitHub Actions workflow already skips tests
# For manual release:
mvn release:prepare -DskipTests=true
```

### Problem: "Failed to push tag to remote repository"

**Cause:** Git authentication issue or tag already exists

**Solution:**
```bash
# Check if tag exists
git tag -l | grep alliance-1.18.0

# If exists, delete and recreate
git tag -d alliance-1.18.0
git push origin :refs/tags/alliance-1.18.0

# Then re-run release
```

### Problem: Distribution ZIP Too Large (>2GB)

**Cause:** Normal - Alliance includes many dependencies

**Solution:** This is expected. The ZIP contains:
- Alliance core (~500 MB)
- DDF platform (~1 GB)
- All OSGi bundles and dependencies (~500 MB)

If GitHub Release upload fails due to size:
1. Upload to alternative storage (S3, Artifactory, etc.)
2. Link to download in GitHub Release notes

---

## Release Workflow Diagram

```
┌─────────────────┐
│  Developer      │
│  Triggers       │
│  Release        │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│  GitHub Actions: release.yml            │
├─────────────────────────────────────────┤
│  Versioned Release      Nightly Build   │
│  ├─ Update POMs         ├─ Build latest │
│  ├─ Create tag          ├─ Timestamp    │
│  ├─ Build dist          ├─ Create tag   │
│  ├─ Push to git         ├─ Pre-release  │
│  └─ GitHub Release      └─ Cleanup old  │
└────────┬────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│  Artifacts                              │
│  ├─ alliance-X.Y.Z.zip (~2 GB)          │
│  ├─ Git tag: alliance-X.Y.Z             │
│  └─ GitHub Release with notes           │
└─────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│  Users Download & Deploy                │
└─────────────────────────────────────────┘
```

---

## Additional Resources

- **GitHub Releases:** https://github.com/montge/alliance/releases
- **GitHub Actions:** https://github.com/montge/alliance/actions
- **Maven Release Plugin:** https://maven.apache.org/maven-release/maven-release-plugin/
- **Semantic Versioning:** https://semver.org/

---

## Contact

For questions about the release process:
- File an issue: https://github.com/montge/alliance/issues
- Check existing releases: https://github.com/montge/alliance/releases
