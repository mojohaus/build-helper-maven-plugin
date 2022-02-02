# MojoHaus Build Helper Maven Plugin
 
[![The MIT License](https://img.shields.io/github/license/mojohaus/build-helper-maven-plugin.svg?label=License)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/org.codehaus.mojo/build-helper-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/artifact/org.codehaus.mojo/build-helper-maven-plugin)
[![Build Status](https://github.com/mojohaus/build-helper-maven-plugin/workflows/GitHub%20CI/badge.svg?branch=master)](https://github.com/mojohaus/build-helper-maven-plugin/actions/workflows/maven.yml?query=branch%3Amaster)

This is the [build-helper-maven-plugin](http://www.mojohaus.org/build-helper-maven-plugin/) contains
serveral goals to support you in different kinds of task, like parsing version information,
add supplemental source/test folders to a Maven project or attach supplemental artifacts.

More details can be found on the [goals overview page](http://www.mojohaus.org/build-helper-maven-plugin/).

## Releasing

* Make sure `gpg-agent` is running.
* Execute `mvn -B release:prepare release:perform`

For publishing the site do the following:

```
cd target/checkout
mvn verify site site:stage scm-publish:publish-scm
```
