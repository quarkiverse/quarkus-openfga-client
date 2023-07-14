# Quarkus OpenFGA Client
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

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

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/kdubb"><img src="https://avatars.githubusercontent.com/u/787655?v=4?s=100" width="100px;" alt="Kevin Wooten"/><br /><sub><b>Kevin Wooten</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openfga-client/commits?author=kdubb" title="Code">💻</a> <a href="#maintenance-kdubb" title="Maintenance">🚧</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!