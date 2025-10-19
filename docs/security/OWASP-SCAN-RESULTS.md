# OWASP Dependency-Check Scan Results

**Scan Date:** 2025-10-19 10:21:31  
**Project:** Alliance 1.17.5-SNAPSHOT  
**OWASP Dependency-Check Version:** 12.1.0  
**Scan Type:** Aggregate (all modules)

## Executive Summary

Total vulnerabilities identified: **372**

### Severity Breakdown

| Severity | Count | Percentage |
|----------|-------|------------|
| CRITICAL | 21 | 5.6% |
| HIGH     | 105 | 28.2% |
| MEDIUM   | 204 | 54.8% |
| LOW      | 3 | 0.8% |
| UNASSIGNED | 39 | 10.5% |

### Risk Assessment

- **CRITICAL + HIGH vulnerabilities:** 126 (33.9%)
- **Requires immediate attention:** CRITICAL severity items
- **Requires DO-278 test harness development:** All identified vulnerabilities

## Report Locations

- **HTML Report:** `/home/e/Development/alliance/target/dependency-check-report.html`
- **JSON Report:** `/home/e/Development/alliance/target/dependency-check-report.json`
- **Report Size:** HTML: 13MB, JSON: 11MB

## Top 30 CRITICAL and HIGH Severity CVEs

The following vulnerabilities pose the highest risk and should be prioritized for remediation:

### 1. CVE-2021-21345

**Severity:** CRITICAL (CVSS v3: 9.9)  
**Affected Dependency:** `mxparser-1.2.2.jar`

**Description:**  
XStream is a Java library to serialize objects to XML and back again. In XStream before version 1.4.16, there is a vulnerability which may allow a remote attacker who has sufficient rights to execute commands of the host only by manipulating the processed input stream. No user is affected, who followed the recommendation to setup XStream's security framework with a whitelist limited to the mini...

**References:**  
- https://lists.apache.org/thread.html/r9ac71b047767205aa22e3a08cb33f3e0586de6b2fac48b425c6e16b0%40%3Cdev.jmeter.apache.org%3E
- https://www.oracle.com//security-alerts/cpujul2021.html
- http://x-stream.github.io/changes.html#1.4.16

---

### 2. CVE-2025-48913

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `cxf-core-3.6.7.jar`

**Description:**  
If untrusted users are allowed to configure JMS for Apache CXF, previously they could use RMI or LDAP URLs, potentially leading to code execution capabilities.  This interface is now restricted to reject those protocols, removing this possibility.

Users are recommended to upgrade to versions 3.6.8, 4.0.9 or 4.1.3, which fix this issue.

**References:**  
- https://lists.apache.org/thread/f1nv488ztc0js4g5ml2v88mzkzslyh83

---

### 3. CVE-2025-48913

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `cxf-services-ws-discovery-service-3.6.7.jar`

**Description:**  
If untrusted users are allowed to configure JMS for Apache CXF, previously they could use RMI or LDAP URLs, potentially leading to code execution capabilities.  This interface is now restricted to reject those protocols, removing this possibility.

Users are recommended to upgrade to versions 3.6.8, 4.0.9 or 4.1.3, which fix this issue.

**References:**  
- https://lists.apache.org/thread/f1nv488ztc0js4g5ml2v88mzkzslyh83

---

### 4. CVE-2022-1471

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `jruby-stdlib-9.3.8.0.jar: snakeyaml-1.31.jar`

**Description:**  
SnakeYaml's Constructor() class does not restrict types which can be instantiated during deserialization. Deserializing yaml content provided by an attacker can lead to remote code execution. We recommend using SnakeYaml's SafeConsturctor when parsing untrusted content to restrict deserialization. We recommend upgrading to version 2.0 and beyond.

**References:**  
- http://www.openwall.com/lists/oss-security/2023/11/19/1
- https://github.com/google/security-research/security/advisories/GHSA-mjmj-j48q-9wg2
- https://infosecwriteups.com/%EF%B8%8F-inside-the-160-comment-fight-to-fix-snakeyamls-rce-default-1a20c5ca4d4c

---

### 5. CVE-2024-52046

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `mina-core-2.2.3.jar`

**Description:**  
The ObjectSerializationDecoder in Apache MINA uses Java’s native deserialization protocol to process
incoming serialized data but lacks the necessary security checks and defenses. This vulnerability allows
attackers to exploit the deserialization process by sending specially crafted malicious serialized data,
potentially leading to remote code execution (RCE) attacks.



					


				


			


		...

**References:**  
- http://www.openwall.com/lists/oss-security/2024/12/25/1
- https://lists.apache.org/thread/4wxktgjpggdbto15d515wdctohb0qmv8
- https://security.netapp.com/advisory/ntap-20250103-0001/

---

### 6. CVE-2013-7285

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `mxparser-1.2.2.jar`

**Description:**  
Xstream API versions up to 1.4.6 and version 1.4.10, if the security framework has not been initialized, may allow a remote attacker to run arbitrary shell commands by manipulating the processed input stream when unmarshaling XML or any supported format. e.g. JSON.

**References:**  
- http://seclists.org/oss-sec/2014/q1/69
- https://lists.apache.org/thread.html/6d3d34adcf3dfc48e36342aa1f18ce3c20bb8e4c458a97508d5bfed1%40%3Cissues.activemq.apache.org%3E
- http://blog.diniscruz.com/2013/12/xstream-remote-code-execution-exploit.html

---

### 7. CVE-2021-21344

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `mxparser-1.2.2.jar`

**Description:**  
XStream is a Java library to serialize objects to XML and back again. In XStream before version 1.4.16, there is a vulnerability which may allow a remote attacker to load and execute arbitrary code from a remote host only by manipulating the processed input stream. No user is affected, who followed the recommendation to setup XStream's security framework with a whitelist limited to the minimal ...

**References:**  
- https://lists.apache.org/thread.html/r9ac71b047767205aa22e3a08cb33f3e0586de6b2fac48b425c6e16b0%40%3Cdev.jmeter.apache.org%3E
- https://www.oracle.com//security-alerts/cpujul2021.html
- http://x-stream.github.io/changes.html#1.4.16

---

### 8. CVE-2021-21346

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `mxparser-1.2.2.jar`

**Description:**  
XStream is a Java library to serialize objects to XML and back again. In XStream before version 1.4.16, there is a vulnerability which may allow a remote attacker to load and execute arbitrary code from a remote host only by manipulating the processed input stream. No user is affected, who followed the recommendation to setup XStream's security framework with a whitelist limited to the minimal ...

**References:**  
- https://lists.apache.org/thread.html/r9ac71b047767205aa22e3a08cb33f3e0586de6b2fac48b425c6e16b0%40%3Cdev.jmeter.apache.org%3E
- https://www.oracle.com//security-alerts/cpujul2021.html
- http://x-stream.github.io/changes.html#1.4.16

---

### 9. CVE-2021-21347

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `mxparser-1.2.2.jar`

**Description:**  
XStream is a Java library to serialize objects to XML and back again. In XStream before version 1.4.16, there is a vulnerability which may allow a remote attacker to load and execute arbitrary code from a remote host only by manipulating the processed input stream. No user is affected, who followed the recommendation to setup XStream's security framework with a whitelist limited to the minimal ...

**References:**  
- https://lists.apache.org/thread.html/r9ac71b047767205aa22e3a08cb33f3e0586de6b2fac48b425c6e16b0%40%3Cdev.jmeter.apache.org%3E
- https://www.oracle.com//security-alerts/cpujul2021.html
- http://x-stream.github.io/changes.html#1.4.16

---

### 10. CVE-2021-21350

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `mxparser-1.2.2.jar`

**Description:**  
XStream is a Java library to serialize objects to XML and back again. In XStream before version 1.4.16, there is a vulnerability which may allow a remote attacker to execute arbitrary code only by manipulating the processed input stream. No user is affected, who followed the recommendation to setup XStream's security framework with a whitelist limited to the minimal required types. If you rely ...

**References:**  
- https://lists.apache.org/thread.html/r9ac71b047767205aa22e3a08cb33f3e0586de6b2fac48b425c6e16b0%40%3Cdev.jmeter.apache.org%3E
- https://www.oracle.com//security-alerts/cpujul2021.html
- http://x-stream.github.io/changes.html#1.4.16

---

### 11. CVE-2023-39017

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `quartz-2.3.2.jar`

**Description:**  
quartz-jobs 2.3.2 and below was discovered to contain a code injection vulnerability in the component org.quartz.jobs.ee.jms.SendQueueMessageJob.execute. This vulnerability is exploited via passing an unchecked argument. NOTE: this is disputed by multiple parties because it is not plausible that untrusted user input would reach the code location where injection must occur.

**References:**  
- https://github.com/quartz-scheduler/quartz/issues/943
- https://github.com/quartz-scheduler/quartz/issues/943

---

### 12. CVE-2019-19919

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `simple-2.29.27.jar: handlebars.js`

**Description:**  
Versions of handlebars prior to 4.3.0 are vulnerable to Prototype Pollution leading to Remote Code Execution. Templates may alter an Object's __proto__ and __defineGetter__ properties, which may allow an attacker to execute arbitrary code through crafted payloads.

**References:**  
- https://www.tenable.com/security/tns-2021-14
- https://github.com/wycats/handlebars.js/blob/master/release-notes.md#v430---september-24th-2019
- https://www.npmjs.com/advisories/1164

---

### 13. CVE-2021-23369

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `simple-2.29.27.jar: handlebars.js`

**Description:**  
The package handlebars before 4.7.7 are vulnerable to Remote Code Execution (RCE) when selecting certain compiling options to compile templates coming from an untrusted source.

**References:**  
- https://security.netapp.com/advisory/ntap-20210604-0008/
- https://security.netapp.com/advisory/ntap-20210604-0008/
- https://github.com/handlebars-lang/handlebars.js/commit/b6d3de7123eebba603e321f04afdbae608e8fea8

---

### 14. CVE-2021-23383

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `simple-2.29.27.jar: handlebars.js`

**Description:**  
The package handlebars before 4.7.7 are vulnerable to Prototype Pollution when selecting certain compiling options to compile templates coming from an untrusted source.

**References:**  
- https://snyk.io/vuln/SNYK-JAVA-ORGWEBJARSNPM-1279030
- https://snyk.io/vuln/SNYK-JS-HANDLEBARS-1279029
- https://security.netapp.com/advisory/ntap-20210618-0007/

---

### 15. CVE-2022-39135

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `solr-distro-2.29.27-assembly.zip: avatica-core-1.25.0.jar`

**Description:**  
Apache Calcite 1.22.0 introduced the SQL operators EXISTS_NODE, EXTRACT_XML, XML_TRANSFORM and EXTRACT_VALUE do not restrict XML External Entity references in their configuration, making them vulnerable to a potential XML External Entity (XXE) attack. Therefore any client exposing these operators, typically by using Oracle dialect (the first three) or MySQL dialect (the last one), is affected b...

**References:**  
- http://www.openwall.com/lists/oss-security/2022/11/21/3
- https://lists.apache.org/thread/ohdnhlgm6jvt3srw8l7spkm2d5vwm082
- http://www.openwall.com/lists/oss-security/2022/11/21/3

---

### 16. CVE-2022-26612

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `solr-distro-2.29.27-assembly.zip: hadoop-client-runtime-3.4.0.jar (shaded: org.apache.hadoop.thirdparty:hadoop-shaded-protobuf_3_21:1.2.0)`

**Description:**  
In Apache Hadoop, The unTar function uses unTarUsingJava function on Windows and the built-in tar utility on Unix and other OSes. As a result, a TAR entry may create a symlink under the expected extraction directory which points to an external directory. A subsequent TAR entry may extract an arbitrary file into the external directory using the symlink name. This however would be caught by the s...

**References:**  
- https://security.netapp.com/advisory/ntap-20220519-0004/
- https://lists.apache.org/thread/hslo7wzw2449gv1jyjk8g6ttd7935fyz
- https://lists.apache.org/thread/hslo7wzw2449gv1jyjk8g6ttd7935fyz

---

### 17. CVE-2022-26612

**Severity:** CRITICAL (CVSS v3: 9.8)  
**Affected Dependency:** `solr-distro-2.29.27-assembly.zip: hadoop-client-runtime-3.4.0.jar`

**Description:**  
In Apache Hadoop, The unTar function uses unTarUsingJava function on Windows and the built-in tar utility on Unix and other OSes. As a result, a TAR entry may create a symlink under the expected extraction directory which points to an external directory. A subsequent TAR entry may extract an arbitrary file into the external directory using the symlink name. This however would be caught by the s...

**References:**  
- https://security.netapp.com/advisory/ntap-20220519-0004/
- https://lists.apache.org/thread/hslo7wzw2449gv1jyjk8g6ttd7935fyz
- https://lists.apache.org/thread/hslo7wzw2449gv1jyjk8g6ttd7935fyz

---

### 18. CVE-2021-21342

**Severity:** CRITICAL (CVSS v3: 9.1)  
**Affected Dependency:** `mxparser-1.2.2.jar`

**Description:**  
XStream is a Java library to serialize objects to XML and back again. In XStream before version 1.4.16, there is a vulnerability where the processed stream at unmarshalling time contains type information to recreate the formerly written objects. XStream creates therefore new instances based on these type information. An attacker can manipulate the processed input stream and replace or inject ob...

**References:**  
- https://lists.apache.org/thread.html/r9ac71b047767205aa22e3a08cb33f3e0586de6b2fac48b425c6e16b0%40%3Cdev.jmeter.apache.org%3E
- https://www.oracle.com//security-alerts/cpujul2021.html
- http://x-stream.github.io/changes.html#1.4.16

---

### 19. CVE-2021-21351

**Severity:** CRITICAL (CVSS v3: 9.1)  
**Affected Dependency:** `mxparser-1.2.2.jar`

**Description:**  
XStream is a Java library to serialize objects to XML and back again. In XStream before version 1.4.16, there is a vulnerability may allow a remote attacker to load and execute arbitrary code from a remote host only by manipulating the processed input stream. No user is affected, who followed the recommendation to setup XStream's security framework with a whitelist limited to the minimal requir...

**References:**  
- https://lists.apache.org/thread.html/r9ac71b047767205aa22e3a08cb33f3e0586de6b2fac48b425c6e16b0%40%3Cdev.jmeter.apache.org%3E
- https://www.oracle.com//security-alerts/cpujul2021.html
- http://x-stream.github.io/changes.html#1.4.16

---

### 20. CVE-2019-20444

**Severity:** CRITICAL (CVSS v3: 9.1)  
**Affected Dependency:** `solr-distro-2.29.27-assembly.zip: grpc-netty-1.65.1.jar`

**Description:**  
HttpObjectDecoder.java in Netty before 4.1.44 allows an HTTP header that lacks a colon, which might be interpreted as a separate header with an incorrect syntax, or might be interpreted as an "invalid fold."

**References:**  
- https://access.redhat.com/errata/RHSA-2020:0606
- https://lists.apache.org/thread.html/rff210a24f3a924829790e69eaefa84820902b7b31f17c3bf2def9114%40%3Ccommits.druid.apache.org%3E
- https://lists.apache.org/thread.html/rc7eb5634b71d284483e58665b22bf274a69bd184d9bd7ede52015d91%40%3Ccommon-issues.hadoop.apache.org%3E

---

### 21. CVE-2019-20445

**Severity:** CRITICAL (CVSS v3: 9.1)  
**Affected Dependency:** `solr-distro-2.29.27-assembly.zip: grpc-netty-1.65.1.jar`

**Description:**  
HttpObjectDecoder.java in Netty before 4.1.44 allows a Content-Length header to be accompanied by a second Content-Length header, or by a Transfer-Encoding header.

**References:**  
- https://access.redhat.com/errata/RHSA-2020:0606
- https://lists.apache.org/thread.html/rff210a24f3a924829790e69eaefa84820902b7b31f17c3bf2def9114%40%3Ccommits.druid.apache.org%3E
- https://lists.apache.org/thread.html/r36fcf538b28f2029e8b4f6b9a772f3b107913a78f09b095c5b153a62%40%3Cissues.zookeeper.apache.org%3E

---

### 22. CVE-2025-48734

**Severity:** HIGH (CVSS v3: 8.8)  
**Affected Dependency:** `commons-beanutils-1.9.4.jar`

**Description:**  
Improper Access Control vulnerability in Apache Commons.



A special BeanIntrospector class was added in version 1.9.2. This can be used to stop attackers from using the declared class property of Java enum objects to get access to the classloader. However this protection was not enabled by default. PropertyUtilsBean (and consequently BeanUtilsBean) now disallows declared class level property ...

**References:**  
- https://lists.apache.org/thread/s0hb3jkfj5f3ryx6c57zqtfohb0of1g9
- http://www.openwall.com/lists/oss-security/2025/05/28/6

---

### 23. CVE-2023-6787

**Severity:** HIGH (CVSS v3: 8.8)  
**Affected Dependency:** `kernel-2.29.27.zip: keycloak-osgi-adapter-18.0.2.jar`

**Description:**  
A flaw was found in Keycloak that occurs from an error in the re-authentication mechanism within org.keycloak.authentication. This flaw allows hijacking an active Keycloak session by triggering a new authentication process with the query parameter "prompt=login," prompting the user to re-enter their credentials. If the user cancels this re-authentication by selecting "Restart login," an account...

**References:**  
- https://bugzilla.redhat.com/show_bug.cgi?id=2254375
- https://bugzilla.redhat.com/show_bug.cgi?id=2254375
- https://access.redhat.com/security/cve/CVE-2023-6787

---

### 24. CVE-2023-6787

**Severity:** HIGH (CVSS v3: 8.8)  
**Affected Dependency:** `kernel-2.29.27.zip: keycloak-pax-web-undertow-18.0.2.jar`

**Description:**  
A flaw was found in Keycloak that occurs from an error in the re-authentication mechanism within org.keycloak.authentication. This flaw allows hijacking an active Keycloak session by triggering a new authentication process with the query parameter "prompt=login," prompting the user to re-enter their credentials. If the user cancels this re-authentication by selecting "Restart login," an account...

**References:**  
- https://bugzilla.redhat.com/show_bug.cgi?id=2254375
- https://bugzilla.redhat.com/show_bug.cgi?id=2254375
- https://access.redhat.com/security/cve/CVE-2023-6787

---

### 25. CVE-2020-26217

**Severity:** HIGH (CVSS v3: 8.8)  
**Affected Dependency:** `mxparser-1.2.2.jar`

**Description:**  
XStream before version 1.4.14 is vulnerable to Remote Code Execution.The vulnerability may allow a remote attacker to run arbitrary shell commands only by manipulating the processed input stream. Only users who rely on blocklists are affected. Anyone using XStream's Security Framework allowlist is not affected. The linked advisory provides code workarounds for users who cannot upgrade. The issu...

**References:**  
- https://www.oracle.com/security-alerts/cpuapr2022.html
- https://lists.debian.org/debian-lts-announce/2020/12/msg00001.html
- https://security.netapp.com/advisory/ntap-20210409-0004/

---

### 26. CVE-2021-29505

**Severity:** HIGH (CVSS v3: 8.8)  
**Affected Dependency:** `mxparser-1.2.2.jar`

**Description:**  
XStream is software for serializing Java objects to XML and back again. A vulnerability in XStream versions prior to 1.4.17 may allow a remote attacker has sufficient rights to execute commands of the host only by manipulating the processed input stream. No user who followed the recommendation to setup XStream's security framework with a whitelist limited to the minimal required types is affect...

**References:**  
- https://lists.fedoraproject.org/archives/list/package-announce%40lists.fedoraproject.org/message/QGXIU3YDPG6OGTDHMBLAFN7BPBERXREB/
- https://www.debian.org/security/2021/dsa-5004
- https://lists.fedoraproject.org/archives/list/package-announce%40lists.fedoraproject.org/message/22KVR6B5IZP3BGQ3HPWIO2FWWCKT3DHP/

---

### 27. CVE-2021-39139

**Severity:** HIGH (CVSS v3: 8.8)  
**Affected Dependency:** `mxparser-1.2.2.jar`

**Description:**  
XStream is a simple library to serialize objects to XML and back again. In affected versions this vulnerability may allow a remote attacker to load and execute arbitrary code from a remote host only by manipulating the processed input stream. A user is only affected if using the version out of the box with JDK 1.7u21 or below. However, this scenario can be adjusted easily to an external Xalan t...

**References:**  
- https://lists.fedoraproject.org/archives/list/package-announce%40lists.fedoraproject.org/message/QGXIU3YDPG6OGTDHMBLAFN7BPBERXREB/
- https://www.debian.org/security/2021/dsa-5004
- https://github.com/x-stream/xstream/security/advisories/GHSA-64xx-cq4q-mf44

---

### 28. CVE-2025-27818

**Severity:** HIGH (CVSS v3: 8.8)  
**Affected Dependency:** `solr-distro-2.29.27-assembly.zip: kafka-server-3.9.0.jar`

**Description:**  
A possible security vulnerability has been identified in Apache Kafka.
This requires access to a alterConfig to the cluster resource, or Kafka Connect worker, and the ability to create/modify connectors on it with an arbitrary Kafka client SASL JAAS config
and a SASL-based security protocol, which has been possible on Kafka clusters since Apache Kafka 2.0.0 (Kafka Connect 2.3.0).
When configuri...

**References:**  
- https://kafka.apache.org/cve-list
- http://www.openwall.com/lists/oss-security/2025/06/09/2

---

### 29. CVE-2022-1271

**Severity:** HIGH (CVSS v3: 8.8)  
**Affected Dependency:** `solr-distro-2.29.27-assembly.zip: xz-1.9.jar`

**Description:**  
An arbitrary file write vulnerability was found in GNU gzip's zgrep utility. When zgrep is applied on the attacker's chosen file name (for example, a crafted file name), this can overwrite an attacker's content to an arbitrary attacker-selected file. This flaw occurs due to insufficient validation when processing filenames with two or more newlines where selected content and the target file nam...

**References:**  
- https://security.gentoo.org/glsa/202209-01
- https://tukaani.org/xz/xzgrep-ZDI-CAN-16587.patch
- https://bugzilla.redhat.com/show_bug.cgi?id=2073310

---

### 30. CVE-2021-21349

**Severity:** HIGH (CVSS v3: 8.6)  
**Affected Dependency:** `mxparser-1.2.2.jar`

**Description:**  
XStream is a Java library to serialize objects to XML and back again. In XStream before version 1.4.16, there is a vulnerability which may allow a remote attacker to request data from internal resources that are not publicly available only by manipulating the processed input stream. No user is affected, who followed the recommendation to setup XStream's security framework with a whitelist limit...

**References:**  
- https://lists.apache.org/thread.html/r9ac71b047767205aa22e3a08cb33f3e0586de6b2fac48b425c6e16b0%40%3Cdev.jmeter.apache.org%3E
- https://www.oracle.com//security-alerts/cpujul2021.html
- http://x-stream.github.io/changes.html#1.4.16

---

## Next Steps

### Immediate Actions (Next 2 Weeks)

1. **Review CRITICAL vulnerabilities** (21 total)
   - Focus on CVEs with CVSS scores >= 9.0
   - Prioritize dependencies with remote code execution (RCE) potential
   - Examples: CVE-2021-21345 (XStream), CVE-2022-1471 (SnakeYAML), CVE-2025-48913 (Apache CXF)

2. **Develop Test Harnesses** (Per DO-278 Requirements)
   - Create vulnerability reproduction tests BEFORE remediation
   - Document attack vectors and exploitation scenarios
   - Establish regression test suite

3. **Dependency Updates**
   - Identify available patches for affected libraries
   - Test updates in isolated environment
   - Verify compatibility with Alliance architecture

### Short-term Actions (2-4 Weeks)

1. **Address HIGH severity vulnerabilities** (105 total)
   - Focus on authentication/authorization issues
   - Review XML/deserialization vulnerabilities
   - Update Apache Hadoop, Netty, and Jetty dependencies

2. **Suppression Review**
   - Audit existing suppressions in `dependency-check-maven-config.xml`
   - Validate suppression justifications
   - Remove outdated suppressions

### Medium-term Actions (1-3 Months)

1. **MEDIUM severity remediation** (204 total)
2. **Dependency management strategy**
   - Upgrade to dependency-check-maven 12.1.0+ in project POM
   - Implement automated dependency scanning in CI/CD
   - Establish regular dependency update cadence

3. **DO-278 Documentation**
   - Document all vulnerabilities in requirements traceability matrix
   - Link CVEs to test cases
   - Create verification and validation reports

## Technical Notes

### Scan Configuration

- **CVSS Failure Threshold:** 11 (permissive - all vulnerabilities documented)
- **Output Formats:** HTML, JSON
- **NVD Database:** Updated using NVD API 2.0
- **Scan Duration:** ~27 minutes
- **Dependencies Analyzed:** All Alliance modules + DDF distribution artifacts

### Known Limitations

1. **Version Issue:** Project uses dependency-check-maven 6.1.1 (configured in ddf-parent:1.0.12)
   - NVD JSON Feed 1.1 API deprecated (returns 403 Forbidden)
   - Scan required manual upgrade to version 12.1.0
   - **Recommendation:** Update `ddf-parent` POM to use 12.1.0+

2. **Suppressed Vulnerabilities:** Some CVEs may be suppressed via:
   - `https://raw.githubusercontent.com/codice/ddf/master/dependency-check-maven-config.xml`
   - `dependency-check-maven-config.xml` (local)

3. **False Positives:** Some CVEs may not apply to Alliance's usage:
   - Review CPE matching accuracy
   - Validate vulnerability applicability to runtime configuration

### Remediation Workflow

Per DO-278 and CLAUDE.md security requirements:

```bash
# 1. Create test harness demonstrating vulnerability
mvn test -Dtest=SecurityVulnerabilityTest#testCVE_XXXX_XXXX

# 2. Implement fix (dependency upgrade, code change, etc.)
# Edit pom.xml or source code

# 3. Verify fix with test harness
mvn test -Dtest=SecurityVulnerabilityTest#testCVE_XXXX_XXXX

# 4. Run full test suite
mvn test

# 5. Re-run OWASP scan to confirm remediation
mvn org.owasp:dependency-check-maven:12.1.0:aggregate

# 6. Update documentation
# - CHANGELOG.md
# - Security advisory
# - Traceability matrix
```

## Related Documentation

- [OWASP Scan Guide](/home/e/Development/alliance/docs/security/OWASP-SCAN-GUIDE.md)
- [Security README](/home/e/Development/alliance/docs/security/README.md)
- [CI/CD Migration Guide](/home/e/Development/alliance/docs/ci-cd-migration.md)
- [Project CLAUDE.md](/home/e/Development/alliance/CLAUDE.md)

## Scan Command Reference

```bash
# Full aggregate scan (recommended)
cd /home/e/Development/alliance
mvn org.owasp:dependency-check-maven:12.1.0:aggregate \
    -DfailBuildOnCVSS=11 \
    -DskipTests=true \
    -Dformats=HTML,JSON

# Scan with owasp-dist profile (includes distribution zips)
mvn org.owasp:dependency-check-maven:12.1.0:aggregate \
    -Powasp-dist \
    -DfailBuildOnCVSS=7 \
    -DskipTests=true

# Scan specific module
cd catalog/imaging/imaging-nitf-impl
mvn org.owasp:dependency-check-maven:12.1.0:check
```

---

**Generated:** 2025-10-19 10:21:31 by OWASP dependency-check scan automation
