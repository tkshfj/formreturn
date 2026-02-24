<p align="center">
  <img alt="FormReturn logo" src="etc/media/splashscreen.svg" width="320" />
</p>

# FormReturn (maintenance fork)
**Optical Mark Recognition (OMR) made simple — design, scan, and process forms.**

This repository is a downstream maintenance fork of FormReturn, a Java desktop OMR tool originally developed and released by EB Strada Pty Ltd. We focus on updates, fixes, and workflow improvements while preserving upstream behavior and architecture as much as possible.

> This repository does **not** publish official installers or releases.  
> Please use upstream distribution channels for official downloads.

---

## Overview

FormReturn is a **Java 17, Swing-based desktop application** used to:

- design forms in a GUI,
- scan or import completed forms,
- recognize marks and extract structured results,
- export data (e.g., CSV/XML/PDF) and run background processing tasks.

It also includes a **server/daemon component** (Derby + Quartz) with scanner integration (TWAIN/SANE/ICA) and an **installer wizard**.

---

## What we change in this fork

Typical work in this repository includes:

- **Java 17 upgrade** (from Java 8) — updated build target, generics, deprecated API replacements, reflection-based access to internal JDK APIs,
- **security hardening** (XStream deserialization, Zip Slip, SQL injection fixes),
- **code quality** (resource leak fixes, deprecation cleanup, API migrations, SonarQube/IDE warning elimination),
- bug fixes and usability improvements,
- workflow and report/export refinements,
- **dependency management** (vendored legacy JARs, missing transitive dependency fixes, OpenJPA 3.2.2 upgrade),
- build/packaging adjustments needed for our environment.

We aim to keep changes targeted and upstream-friendly whenever possible.

---

## Project layout

Key areas under `src/main/java/com/ebstrada/`:

- `aggregation/` — rules/aggregation engine  
- `formreturn/api/` — public plugin API  
- `formreturn/manager/` — main application (UI, logic, persistence, utilities)  
- `formreturn/scanner/` — scanner client integrations (TWAIN/SANE/ICA)  
- `formreturn/server/` — background server daemon (Derby + Quartz)

### Main entry points

- `com.ebstrada.formreturn.manager.ui.Main` — desktop GUI  
- `com.ebstrada.formreturn.server.ServerGUI` — server GUI / daemon support  
- `com.ebstrada.formreturn.installer.Main` — installer wizard (installer project)

---

## Building

### Prerequisites

- **OpenJDK 17** (or later)
- **Maven**
- **NSIS** (Windows installer packaging only)

#### macOS: select Java 17

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### Build order (two separate Maven projects)

The root project and the installer project are **separate Maven builds** (not a multi-module parent/child). Build the root first, then the installer.

```bash
# 1) Build the main library (from repository root)
mvn clean install

# 2) Build platform installers (from installer/)
cd installer && mvn clean package
```

### Packaging outputs

Artifacts are created under `installer/target/`, typically including:

* macOS `.app`
* Windows NSIS `.exe` (requires NSIS)
* cross-platform “fat” JAR

#### Install NSIS (example: Ubuntu)

```bash
apt install nsis
```

### Vendored dependencies

Legacy dependencies not available on Maven Central are vendored in the `repository/` directory as a local file-based Maven repository. No external custom Maven repository is required — the build is fully self-contained.

Vendored artifacts:

| Artifact | Source |
| --- | --- |
| JAI Core / Codec (`javax.media`) | OSGeo |
| JAI ImageIO Core / JPEG2000 (`net.java.dev.jai-imageio`) | JitPack |
| IJ ImageIO (`net.sf.ij.jaiio`) | SourceForge |
| TWAIN (`uk.co.mmscomputing`) | SourceForge |
| SwingSane (`com.swingsane`) | JitPack |
| Batik All (`org.apache.batik`) | Maven Central (`org.apache.xmlgraphics`) |
| JSPF Core (`net.xeoh.jspf`) | Korpling/HU-Berlin |
| ImageJ (`gov.nih.imagej`) | Maven Central |

> **Note:** Some vendored artifacts use non-canonical groupIds in `pom.xml` (e.g., `org.apache.batik` instead of `org.apache.xmlgraphics` for batik-all, `net.xeoh.jspf` instead of `net.xeoh.plugins` for jspf-core). Do not change these without updating all references.

After cloning, run `mvn dependency:resolve -U` to populate your local Maven cache (`~/.m2/repository`). This is needed for IDE support (e.g., VS Code Java extension). If Maven shows cached 403 errors, delete `*.lastUpdated` files from the relevant `~/.m2/repository` subdirectories.

---

## Running

The application has two components: a **server** (embeds Derby database + Quartz scheduler) and a **desktop GUI** (connects to the server). The server must be started first.

### 1. Start the server

```bash
# Build first
mvn clean install

# Copy dependencies for classpath
mvn dependency:copy-dependencies -DoutputDirectory=target/dependency

# Server with GUI
java -cp "target/classes:target/dependency/*" \
  com.ebstrada.formreturn.server.ServerGUI

# Or headless CLI daemon
java -cp "target/classes:target/dependency/*" \
  com.ebstrada.formreturn.server.ServerGUI cli

# CLI with custom data directory
java -cp "target/classes:target/dependency/*" \
  com.ebstrada.formreturn.server.ServerGUI cli /path/to/data/dir
```

The server automatically starts an embedded Derby database on `127.0.0.1:1527` and creates the `FORMRETURN` schema on first run (via OpenJPA's `buildSchema`). No manual database setup is needed.

### 2. Start the desktop GUI

With the server running:

```bash
java -cp "target/classes:target/dependency/*" \
  com.ebstrada.formreturn.manager.ui.Main
```

### macOS note

If you encounter reflection errors related to Aqua Look & Feel, add:

```bash
java --add-opens java.desktop/com.apple.laf=ALL-UNNAMED \
  -cp "target/classes:target/dependency/*" \
  com.ebstrada.formreturn.manager.ui.Main
```

### 3. Run from installer artifacts

After building the installer (`cd installer && mvn clean package`), the fat JAR in `installer/target/` bundles everything and can be run directly.

---

## Testing

### Unit tests

Tests use **JUnit 4** and live in a **non-standard directory**: `test/main/java/` (not `src/test/java/`). Maven's Surefire plugin does not discover them automatically. Coverage is focused on `com.ebstrada.aggregation` (6 test classes, ~37 test cases).

To run them manually after building:

```bash
# Compile test sources
javac -cp "target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" \
  -d target/test-classes \
  test/main/java/com/ebstrada/aggregation/*.java

# Run all tests
java -cp "target/test-classes:target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" \
  org.junit.runner.JUnitCore \
  com.ebstrada.aggregation.AggregationTest \
  com.ebstrada.aggregation.AndConditionTest \
  com.ebstrada.aggregation.OrConditionTest \
  com.ebstrada.aggregation.RuleTest \
  com.ebstrada.aggregation.RulePartTest \
  com.ebstrada.aggregation.ResultTest
```

### Manual testing checklist

After launching the server and GUI:

1. **Form designer** — create a new form, add fields (text, barcode, checkbox, OMR bubbles), save and reopen
2. **Publication** — publish a form to generate printable pages
3. **Scanning** — import scanned images (or use sample images) via the scan data manager
4. **Processing** — run the processing queue to recognize marks from scanned images
5. **Export** — export captured data as CSV/XML/PDF from the captured data manager
6. **Server tasks** — create and schedule background tasks (export, vacuum, folder monitor) via the server GUI

---

## Persistence and data storage

* JPA provider: **Apache OpenJPA 3.2.2** (with build-time enhancement; enhancer execution disabled for Java 17 class format compatibility)
* Database: **Apache Derby** (embedded or networked), schema `FORMRETURN`
* Preferences / form files: serialized via **XStream**

---

## Dependencies

Key libraries and their current versions:

| Library | Version | Purpose |
| --- | ---: | --- |
| Apache OpenJPA | 3.2.2 | JPA persistence provider |
| Apache Derby | 10.16.1.1 | Embedded/networked database |
| Apache FOP | 2.9 | XSL-FO to PDF rendering |
| Apache PDFBox | 2.0.32 | PDF handling |
| Apache Batik | 1.17 | SVG rendering |
| Avalon Framework | 4.3 / 4.2.0 | Configuration API (barcode4j/FOP) |
| xml-apis-ext | 1.3.04 | W3C SVG DOM interfaces |
| Quartz Scheduler | 2.3.2 | Background job scheduling |
| XStream | 1.4.20 | XML serialization |
| Gson | 2.11.0 | JSON support |
| OpenCSV | 5.9 | CSV import/export |
| JFreeSane | 0.95 | SANE scanner protocol client |
| Barcode4j | 2.1 | Barcode generation |
| reload4j | 1.2.25 | Logging (Log4j 1.x fork) |
| SLF4J | 1.7.36 | Logging facade |
| Commons IO | 2.16.1 | File/IO utilities |
| Commons Lang3 | 3.14.0 | Language utilities |
| Commons Codec | 1.17.1 | Encoding utilities |
| Commons Daemon | 1.4.0 | Unix daemon support |
| Xerces | 2.12.2 | XML parsing |
| JUnit | 4.13.2 | Testing |

**Note:** Derby 10.16.1.1 requires Java 17. Derby 10.17+ requires Java 21 and is not compatible with this project's Java 17 target.

---

## Security and code quality improvements

This fork includes targeted hardening beyond the upstream codebase:

- **XStream deserialization** — restricted allowed types to prevent arbitrary object instantiation; narrowed `java.util.**` wildcard to explicit safe collection types only
- **Zip Slip** — validated archive entry paths during extraction to block directory traversal
- **SQL injection** — replaced all string-concatenated native queries with parameterized JPA queries across FormReader, CSVExporter, CapturedDataManagerFrame, PublicationController, Misc, and FormProcessor; fixed critical injection vector via scanned barcode data in FormReader
- **Resource leaks** — ensured streams, connections, and entity managers are properly closed (try-with-resources); fixed EntityManager leaks in `vacuumDatabase()` and XSL-FO export path; fixed `RandomAccessFile` leak in `isFileComplete()`
- **Concurrency** — added `volatile` to cross-thread `runProcess` flag; bounded infinite transaction retry loops with sleep and max retry count
- **Bug fixes** — fixed CSVExporter null-check on wrong array index (formPassword vs recordId); removed premature `BufferedImage.flush()` that invalidated image data before processing
- **Deprecated API removal** — replaced Guava `Files.createTempDir()` with `java.nio.file.Files`, migrated Batik and Quartz APIs to current versions, replaced `new URL()` with `URI`-based construction, replaced `getModifiers()` with `getModifiersEx()`, replaced `Class.newInstance()` with `getDeclaredConstructor().newInstance()`
- **OpenCSV 5.x** — added handling for new checked exceptions (`CsvValidationException`, `CsvException`)
- **Java 17 migration** — added generic type parameters across ~120 files (JComboBox, DefaultComboBoxModel, Vector, Enumeration, Iterator, Class, etc.), replaced `sun.font` compile-time references with reflection-safe alternatives, upgraded OpenJPA to 3.2.2

---

## Internationalization (i18n)

Resource bundles:

* `src/main/resources/com/ebstrada/formreturn/language/`

English is default; Spanish (`_es`) is available.

---

## Tutorials

Upstream tutorials (for learning how to use the application):

* `http://content.formreturn.com/` (upstream site; may be unavailable)

---

## Code signing and OS warnings (upstream packaging behavior)

Open-source builds are not code-signed:

* **macOS:** Gatekeeper may block first launch; use Finder → right-click → **Open**.
* **Windows:** SmartScreen may warn about an unknown publisher.

---

## Contributing

This repository is maintained as a downstream fork for targeted improvements.

* Open issues/PRs with clear reproduction steps and rationale.
* Keep changes small and aligned with upstream architecture where possible.
* If you plan to contribute upstream, follow upstream contribution guidelines.

---

## License and attribution

FormReturn is an upstream project authored and published by EB Strada Pty Ltd. This repository contains downstream modifications and maintenance work. Please review and preserve upstream LICENSE/NOTICE files when redistributing or deriving work.
