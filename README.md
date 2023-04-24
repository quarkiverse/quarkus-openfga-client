# Quarkus OpenFGA Client

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.openfga/quarkus-openfga-client?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.openfga/quarkus-openfga-client)
[![Build](https://github.com/quarkiverse/quarkus-openfga-client/actions/workflows/build.yml/badge.svg)](https://github.com/quarkiverse/quarkus-openfga-client/actions/workflows/build.yml)

## Overview

The **Quarkus OpenFGA Client** extension provides a reactive client for accessing [OpenFGA](https://openfga.dev)
instances. Additionally, it is the client that powers the
[Quarkus Zanzibar - OpenFGA Connector](https://github.com/quarkiverse/quarkus-zanzibar#OpenFGA-Connector) to provide 
Find Grained Authorization for Quarkus applications.

## Documentation

The documentation for this extension can be found
[here](https://quarkiverse.github.io/quarkiverse-docs/quarkus-openfga-client/dev/index.html).

## Installation

### Dependency

Add the `quarkus-openfga-client` extension to your Quarkus project in your `pom.xml` for Maven or `build.gradle(.kts)` for Gradle.

#### Maven

```xml
<dependency>
    <groupId>io.quarkiverse.openfga</groupId>
    <artifactId>quarkus-openfga-client</artifactId>
    <version>${openfga.version}</version>
</dependency>
```

#### Gradle

```kotlin
implementation("io.quarkiverse.openfga:quarkus-openfga-client:${openfga.version}")
```
