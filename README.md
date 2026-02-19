<p align="center">
  <img alt="FormReturn logo" src="etc/media/splashscreen.svg" width="320" />
</p>

# FormReturn (maintenance fork)
**Optical Mark Recognition (OMR) made simple — design, scan, and process forms.**

This repository is a **downstream maintenance fork** of FormReturn, a Java desktop OMR application originally created and released by others. We focus on **updates, fixes, and workflow improvements** while preserving upstream behavior and architecture as much as possible.

> This repository does **not** publish official installers or releases.  
> Please use upstream distribution channels for official downloads.

---

## Overview

FormReturn is a **Java 8, Swing-based desktop application** used to:

- design forms in a GUI,
- scan or import completed forms,
- recognize marks and extract structured results,
- export data (e.g., CSV/XML/PDF) and run background processing tasks.

It also includes a **server/daemon component** (Derby + Quartz) with scanner integration (TWAIN/SANE/ICA) and an **installer wizard**.

---

## What we change in this fork

Typical work in this repository includes:

- maintenance updates while keeping **Java 8 compatibility**,
- bug fixes and usability improvements,
- workflow and report/export refinements,
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

- **OpenJDK 8**
- **Maven**
- **NSIS** (Windows installer packaging only)

#### macOS: select Java 8

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
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

Legacy dependencies not available on Maven Central (JAI, TWAIN, SwingSane, Batik, JSPF) are vendored in the `repository/` directory as a local file-based Maven repository. No external custom Maven repository is required — the build is fully self-contained.

After cloning, run `mvn dependency:resolve -U` to populate your local Maven cache (`~/.m2/repository`). This is needed for IDE support (e.g., VS Code Java extension).

---

## Testing

Tests use **JUnit 4**, but they live in a **non-standard directory**: `test/main/java/` (not `src/test/java/`). As a result, `mvn test` may not discover them automatically. Coverage is primarily focused on `com.ebstrada.aggregation`.

---

## Persistence and data storage

* JPA provider: **Apache OpenJPA 2.4.3** (with build-time enhancement)
* Database: **Apache Derby** (embedded or networked), schema `FORMRETURN`
* Preferences / form files: serialized via **XStream**

---

## Dependencies (Java 8 compatible)

Key libraries and their current versions:

| Library          |   Version | Purpose                     |
| ---------------- | --------: | --------------------------- |
| Apache OpenJPA   |     2.4.3 | JPA persistence provider    |
| Apache Derby     | 10.14.2.0 | Embedded/networked database |
| Apache FOP       |       2.9 | XSL-FO to PDF rendering     |
| Apache PDFBox    |    2.0.32 | PDF handling                |
| Apache Batik     |      1.17 | SVG rendering               |
| Quartz Scheduler |     2.3.2 | Background job scheduling   |
| XStream          |    1.4.20 | XML serialization           |
| Gson             |    2.11.0 | JSON support                |
| OpenCSV          |       5.9 | CSV import/export           |
| reload4j         |    1.2.25 | Logging (Log4j 1.x fork)    |
| SLF4J            |    1.7.36 | Logging facade              |
| Commons IO       |    2.16.1 | File/IO utilities           |
| Commons Lang3    |    3.14.0 | Language utilities          |
| Commons Codec    |    1.17.1 | Encoding utilities          |
| Commons Daemon   |     1.4.0 | Unix daemon support         |
| Xerces           |    2.12.2 | XML parsing                 |
| JUnit            |    4.13.2 | Testing                     |

**Note:** Derby 10.14.2.0 is the last Derby release compatible with Java 8. Other dependencies are kept at the latest versions that still support Java 8.

---

## Internationalization (i18n)

Resource bundles:

* `src/main/resources/com/ebstrada/formreturn/language/`

English is default; Spanish (`_es`) is available.

---

## Tutorials

Upstream tutorials (for learning how to use the application):

* `http://content.formreturn.com/`

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

FormReturn is an upstream project authored and published by others. This repository contains downstream modifications and maintenance work. Please review and preserve upstream LICENSE/NOTICE files when redistributing or deriving work.
