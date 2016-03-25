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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

/**
 * Parse a version string and set properties containing the component parts of the version. This mojo sets the following
 * properties:
 *
 * <pre>
 *   [propertyPrefix].majorVersion
 *   [propertyPrefix].minorVersion
 *   [propertyPrefix].incrementalVersion
 *   [propertyPrefix].qualifier
 *   [propertyPrefix].buildNumber
 * </pre>
 * 
 * Where the propertyPrefix is the string set in the mojo parameter. Note that the behaviour of the parsing is
 * determined by org.apache.maven.artifact.versioning.DefaultArtifactVersion An osgi compatible version will also be
 * created and made available through the property:
 * 
 * <pre>
 *   [propertyPrefix].osgiVersion
 * </pre>
 * 
 * This version is simply the original version string with the first instance of '-' replaced by '.' For example,
 * 1.0.2-beta-1 will be converted to 1.0.2.beta-1
 *
 * This goal also sets the following properties:
 * 
 * <pre>
 *   [propertyPrefix].nextMajorVersion
 *   [propertyPrefix].nextMinorVersion
 *   [propertyPrefix].nextIncrementalVersion
 * </pre>
 * 
 * The above properties contain simply incremented versions
 * of the parsed version informations. Those can now be used 
 * to update the version of your project via the following to
 * the next Major version:
 * 
 * <pre>
 *   mvn build-helper:parse-version versions:set \
 *      -DnewVersion=\${parsedVersion.nextMajorVersion}.0.0 \
 *      versions:commit
 * </pre>
 *
 * It can of course being used to increment the minor version:
 * <pre>
 *   mvn build-helper:parse-version versions:set \
 *      -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.nextMinorVersion}.0 \
 *      versions:commit
 * </pre>
 *
 * This can make an upgrade of the versions of your project very convenient.
 * 
 * @author pgier
 * @version $Id$
 * @since 1.3
 */
@Mojo( name = "parse-version", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true )
public class ParseVersionMojo
    extends AbstractDefinePropertyMojo
{

    /**
     * The version string to parse.
     */
    @Parameter( defaultValue = "${project.version}" )
    private String versionString;

    /**
     * Prefix string to use for the set of version properties.
     */
    @Parameter( defaultValue = "parsedVersion" )
    private String propertyPrefix;

    /**
     * Execute the mojo. This sets the version properties on the project.
     *
     * @throws MojoExecutionException if the plugin execution fails.
     */
    public void execute()
    {
        parseVersion( versionString );
    }

    private void defineVersionProperty( String name, String value )
    {
        defineProperty( propertyPrefix + '.' + name, value );
    }

    private void defineVersionProperty( String name, int value )
    {
        defineVersionProperty( name, Integer.toString( value ) );
    }

    /**
     * Parse a version String and add the components to a properties object.
     *
     * @param version the version to parse
     */
    public void parseVersion( String version )
    {
        ArtifactVersion artifactVersion = new DefaultArtifactVersion( version );

        ArtifactVersion releaseVersion = artifactVersion;
        if ( ArtifactUtils.isSnapshot( version ) )
        {
            // work around for MBUILDHELPER-69
            releaseVersion =
                new DefaultArtifactVersion( StringUtils.substring( version, 0, version.length()
                    - Artifact.SNAPSHOT_VERSION.length() - 1 ) );
        }

        if ( version.equals( artifactVersion.getQualifier() ) )
        {
            // This means the version parsing failed, so try osgi format.
            getLog().debug( "The version is not in the regular format, will try OSGi format instead" );
            artifactVersion = new OsgiArtifactVersion( version );
        }

        defineVersionProperty( "majorVersion", artifactVersion.getMajorVersion() );
        defineVersionProperty( "minorVersion", artifactVersion.getMinorVersion() );
        defineVersionProperty( "incrementalVersion", artifactVersion.getIncrementalVersion() );
        defineVersionProperty( "nextMajorVersion", artifactVersion.getMajorVersion() + 1 );
        defineVersionProperty( "nextMinorVersion", artifactVersion.getMinorVersion() + 1 );
        defineVersionProperty( "nextIncrementalVersion", artifactVersion.getIncrementalVersion() + 1 );

        String qualifier = artifactVersion.getQualifier();
        if ( qualifier == null )
        {
            qualifier = "";
        }
        defineVersionProperty( "qualifier", qualifier );

        defineVersionProperty( "buildNumber", releaseVersion.getBuildNumber() ); // see MBUILDHELPER-69

        // Replace the first instance of "-" to create an osgi compatible version string.
        String osgiVersion = getOsgiVersion( artifactVersion );
        defineVersionProperty( "osgiVersion", osgiVersion );
    }

    /**
     * Set property name prefix.
     *
     * @param prefix The prefix to be used.
     */
    public void setPropertyPrefix( String prefix )
    {
        this.propertyPrefix = prefix;
    }

    /**
     * Make an osgi compatible version String from an ArtifactVersion
     * 
     * @param version The artifact version.
     * @return The OSGi version as string.
     */
    public String getOsgiVersion( ArtifactVersion version )
    {
        if ( version.toString().equals( version.getQualifier() ) )
        {
            return version.toString();
        }

        StringBuilder osgiVersion = new StringBuilder();
        osgiVersion.append( version.getMajorVersion() );
        osgiVersion.append( "." + version.getMinorVersion() );
        osgiVersion.append( "." + version.getIncrementalVersion() );

        if ( version.getQualifier() != null || version.getBuildNumber() != 0 )
        {
            osgiVersion.append( "." );

            if ( version.getQualifier() != null )
            {
                osgiVersion.append( version.getQualifier() );
            }
            if ( version.getBuildNumber() != 0 )
            {
                osgiVersion.append( version.getBuildNumber() );
            }
        }

        return osgiVersion.toString();
    }
}
