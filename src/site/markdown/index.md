title: Introduction
author:
- Dennis Lundberg
- Karl Heinz Marbaise
date: 2016-08-12

<!--
The MIT License

Copyright (c) 2004, The Codehaus

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->

# Build Helper Maven Plugin

- This plugin contains various small independent goals to assist with the Maven build lifecycle.

## Notes

Starting with version 3.0.0 this plugin will only support Maven 3+. If you really
need Maven 2+ support you have to use version 1.12 or earlier.

## Goals Overview

- [build-helper:add-source](./add-source-mojo.html) Add more source directories to the POM.
- [build-helper:add-test-source](./add-test-source-mojo.html) Add test source directories to the POM.
- [build-helper:add-resource](./add-resource-mojo.html) Add more resource directories to the POM.
- [build-helper:add-test-resource](./add-test-resource-mojo.html) Add test resource directories to the POM.
- [build-helper:attach-artifact](./attach-artifact-mojo.html) Attach additional artifacts to be installed and deployed.
- [build-helper:regex-property](./regex-property-mojo.html) Sets a property by applying a regex replacement rule to a supplied value.
- [build-helper:regex-properties](./regex-properties-mojo.html) Sets a property by applying a regex replacement rule to a supplied value.
- [build-helper:released-version](./released-version-mojo.html) Resolve the latest released version of this project.
- [build-helper:parse-version](./parse-version-mojo.html) Parse the version into different properties.
- [build-helper:reserve-network-port](./reserve-network-port-mojo.html) Reserve a list of random and unused network ports.
- [build-helper:local-ip](./local-ip-mojo.html) Retrieve current host IP address.
- [build-helper:hostname](./hostname-mojo.html) Retrieve current hostname.
- [build-helper:cpu-count](./cpu-count-mojo.html) Retrieve number of available CPU.
- [build-helper:timestamp-property](./timestamp-property-mojo.html) Sets a property based on the current date and time.
- [build-helper:uptodate-property](./uptodate-property-mojo.html) Sets a property according to whether a file set's outputs are up to date with respect to its inputs.
- [build-helper:uptodate-properties](./uptodate-properties-mojo.html) Sets multiple properties according to whether multiple file sets' outputs are up to date with respect to their inputs.
- [build-helper:rootlocation](./rootlocation-mojo.html) Sets a property which defines the root folder of a multi module build.

## Usage

Instructions on how to use the Build Helper Maven Plugin can be found on the [usage page](./usage.html).

## Examples

- [Reserve network port](https://github.com/mojohaus/build-helper-maven-plugin/blob/master/src/it/reserve-ports/pom.xml)
- [Passing reserve port to Surefire](https://github.com/mojohaus/build-helper-maven-plugin/blob/master/src/it/surefire/pom.xml)

