# ESPI-Hot-Birds

## Java runtime

This project targets Java 25, the current LTS release. Use a Java 25 JDK to
build and run the application. The `.java-version` file selects Java 25 in
supported version managers; otherwise, set `JAVA_HOME` to a JDK 25 installation.

Build and test with the Maven Wrapper:

```sh
bash ./mvnw clean test
```