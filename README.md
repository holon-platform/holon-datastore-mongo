# Holon platform MongoDB Datastore

This is the reference __MongoDB__ implementation of the [Holon Platform](https://holon-platform.com) `Datastore` API, using the Java MongoDB driver for data access and manipulation.

See the [Datastore API documentation](https://docs.holon-platform.com/current/reference/holon-core.html#Datastore) for information about the Holon Platform `Datastore` API.

> THIS MODULE IS UNDER DEVELOPMENT.
> The module documentation and usage guide will be available when the first stable release will be published.

## Code structure

See [Holon Platform code structure and conventions](https://github.com/holon-platform/platform/blob/master/CODING.md) to learn about the _"real Java API"_ philosophy with which the project codebase is developed and organized.

## Getting started

### System requirements

The Holon Platform is built using __Java 8__, so you need a JRE/JDK version 8 or above to use the platform artifacts.

### Releases

See [releases](https://github.com/holon-platform/holon-datastore-mongo/releases) for the available releases. Each release tag provides a link to the closed issues.

### Obtain the artifacts

The [Holon Platform](https://holon-platform.com) is open source and licensed under the [Apache 2.0 license](LICENSE.md). All the artifacts (including binaries, sources and javadocs) are available from the [Maven Central](https://mvnrepository.com/repos/central) repository.

The Maven __group id__ for this module is `com.holon-platform.reactor` and a _BOM (Bill of Materials)_ is provided to obtain the module artifacts:

_Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform.mongo</groupId>
        <artifactId>holon-datastore-mongo-bom</artifactId>
        <version>5.2.0-alpha2-SNAPSHOT</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Using the Platform BOM

The [Holon Platform](https://holon-platform.com) provides an overall Maven _BOM (Bill of Materials)_ to easily obtain all the available platform artifacts:

_Platform Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform</groupId>
        <artifactId>bom</artifactId>
        <version>${platform-version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Build from sources

You can build the sources using Maven (version 3.3.x or above is recommended) like this: 

`mvn clean install`

## Getting help

* Check the [platform documentation](https://docs.holon-platform.com/current/reference) or the specific [module documentation](https://docs.holon-platform.com/current/reference/holon-datastore-mongo.html).

* Ask a question on [Stack Overflow](http://stackoverflow.com). We monitor the [`holon-platform`](http://stackoverflow.com/tags/holon-platform) tag.

* Report an [issue](https://github.com/holon-platform/holon-datastore-mongo/issues).

* A [commercial support](https://holon-platform.com/services) is available too.

## Examples

See the [Holon Platform examples](https://github.com/holon-platform/holon-examples) repository for a set of example projects.

## Contribute

See [Contributing to the Holon Platform](https://github.com/holon-platform/platform/blob/master/CONTRIBUTING.md).

[![Gitter chat](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/holon-platform/contribute?utm_source=share-link&utm_medium=link&utm_campaign=share-link) 
Join the __contribute__ Gitter room for any question and to contact us.

## License

All the [Holon Platform](https://holon-platform.com) modules are _Open Source_ software released under the [Apache 2.0 license](LICENSE).

## Artifacts list

Maven _group id_: `com.holon-platform.mongo`

Artifact id | Description
----------- | -----------
`holon-datastore-mongo-core` | Common operations and data structures
`holon-datastore-mongo-core-async` | Common operations and data structures for the asynchronous `Datastore` API implementation
`holon-datastore-mongo-sync` | Synchronous __MongoDB__ `Datastore` API implementation
`holon-datastore-mongo-async` | Asynchronous __MongoDB__ `Datastore` API implementation
`holon-datastore-mongo-reactor` | Reactive __MongoDB__ `Datastore` API implementation using Project Reactor
`holon-datastore-mongo-spring` | __Spring__ integration using the `@EnableMongoDatastore` and `EnableMongoAsyncDatastore` annotation
`holon-datastore-mongo-spring-boot` | __Spring Boot__ integration for __MongoDB__ `Datastore` auto-configuration
`holon-starter-mongo-datastore` | __Spring Boot__ _starter_ for the __MongoDB__ `Datastore` auto-configuration
`holon-starter-mongo-datastore-reactor` | __Spring Boot__ _starter_ for the _reactive_ __MongoDB__ `Datastore` implementation auto-configuration
`holon-datastore-mongo-bom` | Bill Of Materials
`documentation-datastore-mongo` | Documentation
