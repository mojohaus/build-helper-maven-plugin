# Agent guide for build-helper-maven-plugin

This file is for automated agents and human contributors working on this repository. It records
what the code and the POMs do not make obvious.

## Layout

- One module. Each goal is a `*Mojo` class in `org.codehaus.mojo.buildhelper`; the shared logic of
  related goals is in the `Abstract*Mojo` classes beside them (resource, define-property, regex
  and up-to-date goals). Version parsing is in `versioning`.
- The goals are documented in the Javadoc of the mojos, which the plugin site renders, and in
  `src/site`.

## Build gates

- Main code must use the Java 8 API: mojo-parent sets `maven.compiler.release` to 8.
- Spotless runs on JDK 17 and later only. It applies the formatting in `process-sources` on a local
  build and checks it when the `CI` environment variable is set. It formats Java with
  palantir-java-format and also reformats Markdown, so run `mvn spotless:apply` after editing the
  README or this file.
- The plugin compiles against Maven 3.9.16 (`mavenVersion`) and declares a literal
  `<prerequisites><maven>3.9.0</maven>`; keep the two apart, so raising `mavenVersion` does not
  raise the prerequisite. Building the plugin itself needs Maven 3.9 (`minimalMavenBuildVersion`).
- `.github/workflows/maven.yml` calls the shared `apache/maven-gh-actions-shared`
  `maven-verify.yml@v5` workflow. Branches in this repository build under the push event.

## Unit tests

- JUnit 5 only, in `src/test/java`. `FixtureUtils` creates files with given modification times from
  a `fixture.properties`, for the up-to-date goals.
- Prefer a unit test. Add an integration test only for what needs a real Maven build: reactors,
  `rootlocation` in submodules, or a goal bound into the lifecycle.

## Integration tests

- They live in `src/it`, one directory per test, and run through the `run-its` profile. The invoker
  installs the plugin into `target/local-repo` and builds with `src/it/settings.xml`.
- Run a subset:

  ```shell
  mvn -P run-its verify -Dinvoker.test='parse-version,reserve-ports'
  ```
- They run in parallel, one per CPU core (`invoker.parallelThreads` 1C), so a test must not depend
  on another test's output.
- The forked builds get `JAVA_TOOL_OPTIONS=-XX:TieredStopAtLevel=1 -XX:+UseSerialGC`, so every JVM
  prints a `Picked up JAVA_TOOL_OPTIONS` line on stderr.
- Write `verify.groovy` as `assert` statements. Invoker fails a script whose last expression is
  anything other than `true` or `null`, so a trailing `list.each { }` fails the test; use a `for`
  loop, or end the script with `return true`.

## Dependencies

- Deliberate pins in `.github/dependabot.yml`: Maven artifacts stay below 3.10.0, the Maven 3.9.x
  line being the plugin baseline; Maven Resolver below 2.0 and SLF4J below 2.0, because the 2.x
  releases target Maven 4 and need an `SLF4JServiceProvider` that the Maven 3 runtime does not
  provide.

## Pull requests

- Pull requests are squash- or rebase-merged; merge commits are disabled. Release Drafter builds
  the release notes from the titles and labels, so the title should describe the change for a
  user.

