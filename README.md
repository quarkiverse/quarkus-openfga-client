# Quarkus OpenFGA Client

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.openfga/quarkus-openfga-client?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.openfga/quarkus-openfga-client)
[![Build](https://github.com/quarkiverse/quarkus-openfga-client/actions/workflows/build.yml/badge.svg)](https://github.com/quarkiverse/quarkus-openfga-client/actions/workflows/build.yml)

## Overview

The **Quarkus OpenFGA Client** extension provides a reactive client for accessing [OpenFGA](https://openfga.dev)
instances. Additionally, it is the client that powers the
[Quarkus Zanzibar - OpenFGA Connector](https://github.com/quarkiverse/quarkus-zanzibar#OpenFGA-Connector) to provide 
Find Grained Authorization for Quarkus applications.

## Usage

Adding the `quarkus-openfga-client` extension to your project defines an `OpenFGAClient` bean that is configured to
access the OpenFGA instance configured in `application.properties`.


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

### Configuration

The extension requires two configuration properties to be defined at startup to define what instance and store are
targeted by the client. The `url` property selects the scheme, host and, optionally, the port of the OpenFGA instance.
While `store` determines which authorization store is targeted; it can be referenced by store id or name.

```properties
quarkus.openfga.url=http://localhost:80
quarkus.openfga.store=my-app-authz
```

#### DevServices

The extension supports Quarkus's DevServices and will start and configure a local OpenFGA in `dev` and `test` if no
`url` configuration property is provided. Additionally it will automatically create and configure an authorization
store in the server.

In addition to starting and creating an authorization store, an authorization model can be initialized in the store
by configuring the `quarkus.openfga.devservices.authorization-model` or
`quarkus.openfga.devservices.authorization-model.lcation` property.

### Clients

The extension provides three injectable clients for accessing the configured instance and store.

* `OpenFGAClient` - Main client for accessing the OpenFGA instance.
* `StoreClient` - Access authorization store configured via `quarkus.openfga.store-id`
* `AuthorizationModelClient` - Access authorization model configured via `quarkus.openfga.authoriztion-id` or the default model if none is configured.

### Examples

#### Write a Relationship Tuple

```java
@Inject
StoreClient storeClient;

void write() {
    var authModelClient = storeClient.authorizationModels().defaultModel();
    authModelClient.write(TupleKey.of("thing:1", "reader", "me"));
}
```

#### Check Access for a Relationship Tuple

```java
@Inject
AuthorizationModelClient defaultAuthModelClient;

void write() {
    if (defaultAuthModelClient.check(TupleKey.of("thing:1", "reader", "me"))) {
        print("Allowed!")
    }
}
```

## Documentation

The documentation for this extension should be maintained as part of this repository and it is stored in the `docs/` directory.

The layout should follow the [Antora's Standard File and Directory Set](https://docs.antora.org/antora/2.3/standard-directories/).

Once the docs are ready to be published, please open a PR including this repository in the [Quarkiverse Docs Antora playbook](https://github.com/quarkiverse/quarkiverse-docs/blob/main/antora-playbook.yml#L7). See an example [here](https://github.com/quarkiverse/quarkiverse-docs/pull/1).
