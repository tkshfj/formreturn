<p align="center">
  <img alt="logo" src="https://raw.githubusercontent.com/rquast/formreturn/main/etc/media/splashscreen.svg">
</p>

# FormReturn
Optical Mark Recognition Made Simple

## Download
Download the latest release from: [https://github.com/rquast/formreturn/releases](https://github.com/rquast/formreturn/releases)

### Releases
Releases prior to version 1.7.5 were released under a closed-source license and can be downloaded from [https://releases.formreturn.com/](https://releases.formreturn.com/). If you purchased a license and require a license key to unlock older versions, download the license generator from [https://github.com/rquast/formreturn-license](https://github.com/rquast/formreturn-license). From 1.7.5 onward, open source contributions can be made by creating a pull request. Contribution guidelines will be drafted shortly.

### Code Signing Issues
Open source releases are not codesigned. 

If you're using a Mac, you will need to download, extract and right-click the application file and select "open" to run. This will bypass gatekeeper. 

If you're using Windows, you will get a message saying that the software is from an unknown publisher.

### Tutorials
Tutorials for using FormReturn can be found at [http://content.formreturn.com/](http://content.formreturn.com/)

---

## Building & Developing

FormReturn currently requires OpenJDK 8 and Maven to compile and run.

### Requirements

NSIS is required for buiding and packaging the windows binaries and installer. Install NSIS (ubuntu example below):
```
apt install nsis
```

### Setting Java 1.8 on Mac

To switch to Java 1.8, execute the following command in a terminal:
```
export JAVA_HOME=`/usr/libexec/java_home -v 1.8`
```

### Maven repository

Some FormReturn dependencies are no longer hosted by the maven central repository, and are configured to be obtained from [http://maven.formreturn.com/](http://maven.formreturn.com/)

### Building & Packaging
To build, from the root of the project, run:
```
mvn clean install
```
This will build the formreturn library that is then installed in your local maven repository.

To create a package distribution, from the "installer" directory, run:
```
mvn clean package
```
In the "installer/target" directory, this will create a Mac "app", a windows exe installer and a Linux jar installer.

---

## Dependencies

Key libraries and their current versions:

| Library | Version | Purpose |
|---|---|---|
| Apache OpenJPA | 2.4.3 | JPA persistence provider |
| Apache Derby | 10.14.2.0 | Embedded/networked database |
| Apache FOP | 2.9 | XSL-FO to PDF rendering |
| Apache PDFBox | 2.0.32 | PDF handling |
| Apache Batik | 1.17 | SVG rendering |
| Quartz Scheduler | 2.3.2 | Background job scheduling |
| XStream | 1.4.20 | XML serialization |
| Gson | 2.11.0 | JSON support |
| OpenCSV | 5.9 | CSV import/export |
| reload4j | 1.2.25 | Logging (Log4j 1.x fork) |
| SLF4J | 1.7.36 | Logging facade |
| Commons IO | 2.16.1 | File/IO utilities |
| Commons Lang3 | 3.14.0 | Language utilities |
| Commons Codec | 1.17.1 | Encoding utilities |
| Commons Daemon | 1.4.0 | Unix daemon support |
| Xerces | 2.12.2 | XML parsing |
| JUnit | 4.13.2 | Testing |

Note: Derby 10.14.2.0 is the last version compatible with Java 8. All other dependencies have been updated to the latest versions supporting Java 8.

