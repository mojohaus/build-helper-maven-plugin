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

import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;

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
@Mojo( name = "released-version", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true )
public class ReleasedVersionMojo
    extends AbstractDefinePropertyMojo
{

    /**
     * The artifact metadata source to use.
     */

    @Component
    private RepositorySystem repoSystem;

    @Component
    private ArtifactHandlerManager artifactHandlerManager;

    @Parameter( defaultValue = "${repositorySystemSession}", readonly = true )
    private RepositorySystemSession repoSession;

    /**
     * Prefix string to use for the set of version properties.
     */
    @Parameter( defaultValue = "releasedVersion" )
    private String propertyPrefix;

    private void defineVersionProperty( String name, String value )
    {
        defineProperty( propertyPrefix + '.' + name, Objects.toString( value, "" ) );
    }

    private void defineVersionProperty( String name, int value )
    {
        defineVersionProperty( name, Integer.toString( value ) );
    }

    @SuppressWarnings( "unchecked" )
    public void execute()
    {

        /*
         * We use a dummy version "0" here to check for all released version.
         * Reason: The current project's version is completely irrelevant for the check to retrieve all available versions.
         * But if the current project's version is a -SNAPSHOT version, only repository from maven settings are
         * requested that are allowed for snapshots - but we want to query for released versions, not for snapshots.
         * Using the dummy version "0" which looks like a released version, the repos with releases are requested.
         * see https://github.com/mojohaus/build-helper-maven-plugin/issues/108
         */
        try
        {

            DefaultArtifact artifact =
                    new DefaultArtifact(getProject().getGroupId(), getProject().getArtifactId(),
                            artifactHandlerManager.getArtifactHandler(getProject().getPackaging()).getExtension(), "[0,)");

            getLog().debug("Artifact for lookup released version: " + artifact);
            VersionRangeRequest request = new VersionRangeRequest(artifact, getProject().getRemoteProjectRepositories(), null);

            VersionRangeResult versionRangeResult = repoSystem.resolveVersionRange(repoSession, request);

            getLog().debug("Resolved versions: " + versionRangeResult.getVersions());

            DefaultArtifactVersion releasedVersion = versionRangeResult.getVersions().stream()
                    .filter(v -> !ArtifactUtils.isSnapshot(v.toString()))
                    .map(v -> new DefaultArtifactVersion(v.toString()))
                    .max(DefaultArtifactVersion::compareTo)
                    .orElse(null);

            getLog().debug("Released version: " + releasedVersion);

            if ( releasedVersion != null )
            {
                // Use ArtifactVersion.toString(), the major, minor and incrementalVersion return all an int.
                String releasedVersionValue = releasedVersion.toString();

                // This would not always reflect the expected version.
                int dashIndex = releasedVersionValue.indexOf( '-' );
                if ( dashIndex >= 0 )
                {
                    releasedVersionValue = releasedVersionValue.substring( 0, dashIndex );
                }

                defineVersionProperty( "version", releasedVersionValue );
                defineVersionProperty( "majorVersion", releasedVersion.getMajorVersion() );
                defineVersionProperty( "minorVersion", releasedVersion.getMinorVersion() );
                defineVersionProperty( "incrementalVersion", releasedVersion.getIncrementalVersion() );
                defineVersionProperty( "buildNumber", releasedVersion.getBuildNumber() );
                defineVersionProperty( "qualifier", releasedVersion.getQualifier() );
            }
            else {
                getLog().debug("No released version found.");
            }

        }
        catch (VersionRangeResolutionException e )
        {
            if ( getLog().isWarnEnabled() )
            {
                getLog().warn( "Failed to retrieve artifacts metadata, cannot resolve the released version" );
            }
        }
    }
}
