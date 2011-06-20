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

import java.util.List;

import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Resolve the latest released version of this project.
 * This mojo sets the following properties:
 * 
 * <pre>
 *   [propertyPrefix].version
 * </pre>
 * 
 * Where the propertyPrefix is the string set in the mojo parameter.
 * 
 * @author Robert Scholte
 * @goal released-version
 * @phase validate
 * @since 1.6
 * @threadSafe
 */
public class ReleasedVersionMojo
    extends AbstractMojo
{

    /**
     * The Maven Project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The artifact metadata source to use.
     * 
     * @component
     * @required
     * @readonly
     */
    private ArtifactMetadataSource artifactMetadataSource;

    /**
     * @component
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    private ArtifactRepository localRepository;

    /**
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    private List<ArtifactRepository> remoteArtifactRepositories;

    /**
     * Prefix string to use for the set of version properties.
     * 
     * @parameter default-value="releasedVersion"
     */
    private String propertyPrefix;

    @SuppressWarnings( "unchecked" )
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        org.apache.maven.artifact.Artifact artifact =
            artifactFactory.createArtifact( project.getGroupId(), project.getArtifactId(), "", "", "" );
        try
        {
            ArtifactVersion releasedVersion = null;
            List<ArtifactVersion> versions =
                artifactMetadataSource.retrieveAvailableVersions( artifact, localRepository, remoteArtifactRepositories );
            for ( ArtifactVersion version : versions )
            {
                if ( !ArtifactUtils.isSnapshot( version.toString() )
                    && ( releasedVersion == null || version.compareTo( releasedVersion ) == 1 ) )
                {
                    releasedVersion = version;
                }
            }
            if ( releasedVersion != null )
            {
                String releasedVersionValue;

                // Use ArtifactVersion.toString(), the major, minor and incrementalVersion return all an int.
                // This would not always reflect the expected version.
                int dashIndex = releasedVersion.toString().indexOf( '-' );
                if ( dashIndex >= 0 )
                {
                    releasedVersionValue = releasedVersion.toString().substring( 0, dashIndex );
                }
                else
                {
                    releasedVersionValue = releasedVersion.toString();
                }
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( propertyPrefix + ".version = " + releasedVersionValue );
                }
                project.getProperties().put( propertyPrefix + ".version", releasedVersionValue );
            }

        }
        catch ( ArtifactMetadataRetrievalException e )
        {
            if ( getLog().isWarnEnabled() )
            {
                getLog().warn( "Failed to retrieve artifacts metadata, cannot resolve the released version" );
            }
        }
    }
}
