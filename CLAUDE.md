# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FormReturn is a Java 17 Swing-based Optical Mark Recognition (OMR) desktop application for designing, scanning, and processing forms. It includes a form designer GUI, a background server daemon with Quartz-scheduled tasks, scanner integration (TWAIN/SANE/ICA), and an installer wizard. Originally built for Java 8, the codebase was upgraded to Java 17 with generics, deprecated API replacements, and reflection-based access to internal JDK APIs.

## Build Commands

Requires **OpenJDK 17** (or later) and **Maven**. On Mac: `export JAVA_HOME=$(/usr/libexec/java_home -v 17)`

```bash
# Build the main library (from project root)
mvn clean install

# Create platform installers (from installer/ directory, after root install)
cd installer && mvn clean package
```

The installer produces a Mac `.app`, Windows NSIS `.exe`, and cross-platform fat JAR in `installer/target/`. The macOS `.app` is built with `jpackage` (bundled with JDK 14+), which embeds the JDK runtime automatically. The Windows NSIS installer step only runs on Windows (Maven profile `windows-installer`) and requires NSIS (`apt install nsis`).

## Testing

Tests use JUnit 4 and are in a non-standard location: `test/main/java/` (not `src/test/java/`). They cover the `com.ebstrada.aggregation` package only. Standard `mvn test` may not discover them automatically.

## Architecture

### Two-step build

The root `pom.xml` and `installer/pom.xml` are **separate Maven projects** (not a parent-child module). Build root first with `mvn clean install`, then build the installer.

### Installer build pipeline (`installer/pom.xml`)

The installer `package` phase runs these steps in order:

1. **`maven-dependency-plugin:copy-dependencies`** (prepare-package) — copies all runtime JARs (version-stripped) to `target/jpackage-input/`
2. **`launch4j`** — creates Windows `.exe` wrappers (`formreturn.exe`, `formreturn_server.exe`)
3. **`maven-assembly-plugin`** — creates fat JAR (`formreturn_setup_1.7.5.jar`) with all deps under `lib/`
4. **`antrun:create-app-bundle`** — invokes `jpackage --type app-image` to create `target/app/FormReturn.app` with embedded JDK runtime, secondary "FormReturn Server" launcher (`--add-launcher`), `.frf`/`.frs` file associations, and `--add-opens` flags for XStream reflection. Copies `jsonscan` into `Contents/Resources/`
5. **`antrun:update-app-bundle-into-uberjar`** — extracts fat JAR to `target/appbundle/`, creates `formreturn_server.jar` with classpath manifest, updates fat JAR
6. **`antrun:create-a-windows-signed-nsis-installer`** — Windows-only (Maven profile `windows-installer`), builds NSIS installer

Config files for jpackage are in `installer/jpackage/` (server launcher properties, file association properties).

### Main packages under `src/main/java/com/ebstrada/`

- **`aggregation/`** — Self-contained rule/aggregation engine with conditions, functions, and selections
- **`formreturn/api/`** — Public plugin API (database, export, messaging, task interfaces)
- **`formreturn/manager/`** — Core application (largest subsystem):
  - `gef/` — Graphical editing framework for form design canvas
  - `logic/` — Business logic: aggregation, data import, export (CSV/XML/image/PDF), recognition (OMR reader/structure)
  - `persistence/` — JPA entities (`persistence/jpa/`) and XStream serialization models
  - `ui/` — Swing UI: form editor, captured data manager (`cdm/`), processing queue (`pqm/`), scan data manager (`sdm/`), reprocessor, preferences, wizard
  - `util/` — Utilities, database helpers, image processing, preferences persistence
- **`formreturn/scanner/`** — Scanner client: TWAIN (Windows), SANE (Linux/SwingSane), ICA (macOS)
- **`formreturn/server/`** — Background server daemon:
  - `derby/` — Embedded Derby network server management
  - `quartz/` — Quartz 2.x task scheduler and job implementations
  - `preferences/` — Server task preferences persistence (XStream-based)

### Key entry points

| Class | Role |
|---|---|
| `com.ebstrada.formreturn.manager.ui.Main` | Primary GUI application (form designer/manager) |
| `com.ebstrada.formreturn.server.ServerGUI` | Server GUI; also supports CLI mode via `ServerDaemon.startCommandLineDaemon(args)` |
| `com.ebstrada.formreturn.installer.Main` | Installer wizard (in `installer/` module) |

### Persistence

- **JPA provider**: Apache OpenJPA 3.2.2 with build-time bytecode enhancement (`openjpa-maven-plugin` at `process-classes` phase, enhancer execution disabled for Java 17 class format compatibility)
- **Database**: Apache Derby (embedded or networked), schema `FORMRETURN`
- **Entity classes**: 21 entities in `com.ebstrada.formreturn.manager.persistence.jpa` — configured in `src/main/resources/META-INF/persistence.xml`
- **Preferences/form files**: Serialized via XStream (requires `--add-opens java.desktop/java.awt=ALL-UNNAMED` at runtime for `Dimension`/`Color`/`Rectangle` reflection)

### Key dependencies

- **Derby 10.16.1.1** — Requires Java 17; 10.17+ requires Java 21 and is not compatible with this project's Java 17 target
- **Quartz 2.3.2** — Job scheduling (uses `@DisallowConcurrentExecution`, `JobBuilder`/`TriggerBuilder` pattern)
- **Apache FOP 2.9** — PDF generation via XSL-FO; factory created with `FopFactory.newInstance(URI, InputStream)`
- **OpenCSV 5.9** — CSV import/export; uses `CSVReaderBuilder`/`CSVParserBuilder` pattern (package: `com.opencsv`)
- **reload4j 1.2.25** — Logging (maintained fork of Log4j 1.x, same `org.apache.log4j` package)
- **Vendored local repo**: Legacy deps (JAI, TWAIN, SwingSane, Batik, JSPF) not available on Maven Central are vendored in `repository/` as a file-based Maven repository. The remote `http://maven.formreturn.com/maven2` is commented out in `pom.xml` since it is behind Cloudflare and unreachable. Note: some vendored artifacts use non-canonical groupIds (e.g., `org.apache.batik:batik-all` instead of the canonical `org.apache.xmlgraphics`, `net.xeoh.jspf:jspf-core` instead of `net.xeoh.plugins`) — do not change these without updating all references

### Git

- Only one branch: `main` (no `master`)
- Only one remote: `origin` (`git@github.com:tkshfj/formreturn.git`)

### i18n

Resource bundles in `src/main/resources/com/ebstrada/formreturn/language/` with English (default) and Spanish (`_es`) translations. Accessed via `Localizer.localize("Bundle", "Key")`.
