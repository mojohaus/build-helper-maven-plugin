# MojoHaus Build Helper Maven Plugin

[![The MIT License](https://img.shields.io/github/license/mojohaus/build-helper-maven-plugin.svg?label=License)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/org.codehaus.mojo/build-helper-maven-plugin.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/org.codehaus.mojo/build-helper-maven-plugin)
[![Build Status](https://github.com/mojohaus/build-helper-maven-plugin/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/mojohaus/build-helper-maven-plugin/actions/workflows/maven.yml?query=branch%3Amaster)

The Build Helper Maven Plugin collects small, independent goals for tasks the POM cannot express
on its own: adding source and resource directories, attaching extra artifacts, parsing the
project version into properties, and setting properties from regular expressions, timestamps,
free network ports or the host environment.

It requires Maven 3.9.0 or later and Java 8 or later.

```shell
mvn build-helper:parse-version help:evaluate -Dexpression=parsedVersion.nextMinorVersion -q -DforceStdout
```

The [plugin site](https://www.mojohaus.org/build-helper-maven-plugin/) lists every goal and has
[usage examples](https://www.mojohaus.org/build-helper-maven-plugin/usage.html).

Contributor and agent notes are in [AGENTS.md](AGENTS.md).

## Releasing

* Make sure `gpg-agent` is running.
* Execute `mvn -B release:prepare release:perform`

For publishing the site do the following:

```
cd target/checkout
mvn verify site site:stage scm-publish:publish-scm
```

