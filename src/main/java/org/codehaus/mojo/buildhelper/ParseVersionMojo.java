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

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.util.Properties;

/**
 * Parse a version string and set properties containing the component parts of the version.  This mojo sets the
 * following properties:
 *
 * <pre>
 *   [propertyPrefix].majorVersion
 *   [propertyPrefix].minorVersion
 *   [propertyPrefix].incrementalVersion
 *   [propertyPrefix].qualifier
 *   [propertyPrefix].buildNumber
 * </pre>
 * Where the propertyPrefix is the string set in the mojo parameter.  Note that the behaviour of the
 * parsing is determined by org.apache.maven.artifact.versioning.DefaultArtifactVersion
 *
 * An osgi compatible version will also be created and made available through the property:
 * <pre>
 *   [propertyPrefix].osgiVersion
 * </pre>
 * This version is simply the original version string with the first instance of '-' replaced by '.'
 * For example, 1.0.2-beta-1 will be converted to 1.0.2.beta-1
 *
 * @author pgier
 * @version $Id$
 * @goal parse-version
 * @phase validate
 * @since 1.3
 *
 */
public class ParseVersionMojo
    extends AbstractMojo
{

    /**
     * The Maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The version string to parse.
     *
     * @parameter default-value="${project.version}"
     */
    private String versionString;

    /**
     * Prefix string to use for the set of version properties.
     *
     * @parameter default-value="parsedVersion"
     */
    private String propertyPrefix;

    /**
     * Execute the mojo. This sets the version properties on the project.
     *
     * @throws MojoExecutionException if the plugin execution fails.
     */
    public void execute()
        throws MojoExecutionException
    {

        parseVersion (versionString, project.getProperties() );

    }

    /**
     * Parse a version String and add the components to a properties object.
     *
     * @param version
     * @param props
     */
    public void parseVersion( String version, Properties props)
    {
        ArtifactVersion artifactVersion = new DefaultArtifactVersion( version );

        if ( artifactVersion.getQualifier() != null && artifactVersion.getQualifier().equals( version ) )
        {
            // This means the version parsing failed, so try osgi format.
            getLog().debug("The version is not in the regular format, will try OSGi format instead");
            artifactVersion = new OsgiArtifactVersion( version );
        }
        props.setProperty( propertyPrefix + ".majorVersion", Integer.toString( artifactVersion.getMajorVersion() ) );
        props.setProperty( propertyPrefix + ".minorVersion", Integer.toString( artifactVersion.getMinorVersion() ) );
        props.setProperty( propertyPrefix + ".incrementalVersion",
                           Integer.toString( artifactVersion.getIncrementalVersion() ) );

        props.setProperty( propertyPrefix + ".nextMajorVersion", Integer.toString( artifactVersion.getMajorVersion() + 1 ) );
        props.setProperty( propertyPrefix + ".nextMinorVersion", Integer.toString( artifactVersion.getMinorVersion() + 1 ) );
        props.setProperty( propertyPrefix + ".nextIncrementalVersion",
                           Integer.toString( artifactVersion.getIncrementalVersion() + 1 ) );

        String qualifier = artifactVersion.getQualifier();
        if (qualifier == null)
        {
            qualifier = "";
        }
        props.setProperty( propertyPrefix + ".qualifier", qualifier );

        props.setProperty( propertyPrefix + ".buildNumber", Integer.toString( artifactVersion.getBuildNumber() ) );

        // Replace the first instance of "-" to create an osgi compatible version string.
        String osgiVersion = getOsgiVersion( artifactVersion );
        props.setProperty( propertyPrefix + ".osgiVersion", osgiVersion );
    }

    public void setPropertyPrefix( String prefix )
    {
        this.propertyPrefix = prefix;
    }

    /**
     * Make an osgi compatible version String from an ArtifactVersion
     * @param version
     * @return
     */
    public String getOsgiVersion( ArtifactVersion version )
    {
        if ( version.toString().equals( version.getQualifier() ))
        {
            return version.toString();
        }

        StringBuffer osgiVersion = new StringBuffer();
        osgiVersion.append( version.getMajorVersion() );
        osgiVersion.append( "." + version.getMinorVersion() );
        osgiVersion.append( "." + version.getIncrementalVersion() );
        if ( version.getQualifier() != null || version.getBuildNumber() != 0 )
        {
            osgiVersion.append( "." );
        }
        if ( version.getQualifier() != null )
        {
            osgiVersion.append( version.getQualifier() );
        }
        if ( version.getBuildNumber() != 0 )
        {
            osgiVersion.append( version.getBuildNumber() );
        }
        return osgiVersion.toString();
    }
}
