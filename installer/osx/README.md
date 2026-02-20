Note: macOS app bundling uses jpackage (built into JDK 14+).

The `installer/pom.xml` invokes jpackage during the package phase to create
`FormReturn.app` with an embedded JDK runtime. No external tools are required
beyond a JDK 21+ installation.
