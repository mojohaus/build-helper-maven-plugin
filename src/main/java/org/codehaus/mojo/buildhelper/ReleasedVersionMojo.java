package org.codehaus.mojo.buildhelper;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.Objects;
import java.util.stream.Stream;

import org.apache.maven.api.ArtifactCoordinate;
import org.apache.maven.api.Session;
import org.apache.maven.api.Version;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.plugin.annotations.Mojo;
import org.apache.maven.api.plugin.annotations.Parameter;
import org.apache.maven.api.services.VersionRangeResolver;
import org.apache.maven.api.services.VersionRangeResolverException;
import org.apache.maven.api.services.VersionRangeResolverResult;
import org.codehaus.mojo.buildhelper.utils.ParsedVersion;

/**
 * Resolve the latest released version of this project. This mojo sets the following properties:
 *
 * <pre>
 *   [propertyPrefix].version
 *   [propertyPrefix].majorVersion
 *   [propertyPrefix].minorVersion
 *   [propertyPrefix].incrementalVersion
 *   [propertyPrefix].buildNumber
 *   [propertyPrefix].qualifier
 * </pre>
 *
 * Where the propertyPrefix is the string set in the mojo parameter.
 *
 * @author Robert Scholte
 * @since 1.6
 */
@Mojo(name = "released-version", defaultPhase = "validate")
public class ReleasedVersionMojo extends AbstractDefinePropertyMojo {

    @Inject
    private Session session;

    /**
     * Prefix string to use for the set of version properties.
     */
    @Parameter(defaultValue = "releasedVersion")
    private String propertyPrefix;

    private void defineVersionProperty(String name, String value) {
        defineProperty(propertyPrefix + '.' + name, Objects.toString(value, ""));
    }

    private void defineVersionProperty(String name, long value) {
        defineVersionProperty(name, Long.toString(value));
    }

    @SuppressWarnings("unchecked")
    public void execute() {

        /*
         * We use a dummy version "0" here to check for all released version.
         * Reason: The current project's version is completely irrelevant for the check to retrieve all available versions.
         * But if the current project's version is a -SNAPSHOT version, only repository from maven settings are
         * requested that are allowed for snapshots - but we want to query for released versions, not for snapshots.
         * Using the dummy version "0" which looks like a released version, the repos with releases are requested.
         * see https://github.com/mojohaus/build-helper-maven-plugin/issues/108
         */
        try {

            VersionRangeResolver resolver = session.getService(VersionRangeResolver.class);

            ArtifactCoordinate artifact = session.createArtifactCoordinate(
                    getProject().getGroupId(),
                    getProject().getArtifactId(),
                    "[0,)",
                    getProject().getPackaging().type().getExtension());

            getLog().debug("Artifact for lookup released version: " + artifact);

            Session s = session.withRemoteRepositories(Stream.concat(
                            session.getRemoteRepositories().stream(),
                            project.getModel().getRepositories().stream().map(session::createRemoteRepository))
                    .toList());
            VersionRangeResolverResult versionRangeResult = resolver.resolve(s, artifact);

            getLog().debug("Resolved versions: " + versionRangeResult.getVersions());

            ParsedVersion releasedVersion = versionRangeResult.getVersions().stream()
                    .filter(v -> !session.isVersionSnapshot(v.toString()))
                    .max(Version::compareTo)
                    .map(v -> new ParsedVersion(v.asString()))
                    .orElse(null);

            getLog().debug("Released version: " + releasedVersion);

            if (releasedVersion != null) {
                // Use ArtifactVersion.toString(), the major, minor and incrementalVersion return all an int.
                String releasedVersionValue = releasedVersion.toString();

                // This would not always reflect the expected version.
                int dashIndex = releasedVersionValue.indexOf('-');
                if (dashIndex >= 0) {
                    releasedVersionValue = releasedVersionValue.substring(0, dashIndex);
                }

                defineVersionProperty("version", releasedVersionValue);
                defineVersionProperty("majorVersion", releasedVersion.getMajor());
                defineVersionProperty("minorVersion", releasedVersion.getMinor());
                defineVersionProperty("incrementalVersion", releasedVersion.getPatch());
                defineVersionProperty("buildNumber", releasedVersion.getBuildNumber());
                defineVersionProperty("qualifier", releasedVersion.getQualifier());
            } else {
                getLog().debug("No released version found.");
            }

        } catch (VersionRangeResolverException e) {
            if (getLog().isWarnEnabled()) {
                getLog().warn("Failed to retrieve artifacts metadata, cannot resolve the released version");
            }
        }
    }
}
