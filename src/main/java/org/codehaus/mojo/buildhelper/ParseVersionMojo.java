package org.codehaus.mojo.buildhelper;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.mojo.buildhelper.versioning.DefaultVersioning;

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
 * Where the propertyPrefix is the string set in the mojo parameter. The parsing of the above is based on the following
 * format of the version:
 * 
 * <pre>
 *  &lt;majorversion [&gt; . &lt;minorversion [&gt; . &lt;incrementalversion ] ] [&gt; - &lt;buildnumber | qualifier ]&gt;
 * </pre>
 * 
 * It will be tried to parse the version as an OSGi version. It this is successful the following property will be set
 * accordingly. If this is not possible a warning will be emitted.
 * 
 * <pre>
 *   [propertyPrefix].osgiVersion
 * </pre>
 * 
 * This goal also sets the following properties:
 * 
 * <pre>
 *   [propertyPrefix].nextMajorVersion
 *   [propertyPrefix].nextMinorVersion
 *   [propertyPrefix].nextIncrementalVersion
 *   [propertyPrefix].nextBuildNumber
 * </pre>
 * 
 * This goal also sets the following properties:
 * 
 * <pre>
 *   [formattedPropertyPrefix].majorVersion
 *   [formattedPropertyPrefix].minorVersion
 *   [formattedPropertyPrefix].incrementalVersion
 *   [formattedPropertyPrefix].buildNumber
 * </pre>
 * 
 * This goal also sets the following properties:
 * 
 * <pre>
 *   [formattedPropertyPrefix].nextMajorVersion
 *   [formattedPropertyPrefix].nextMinorVersion
 *   [formattedPropertyPrefix].nextIncrementalVersion
 *   [formattedPropertyPrefix].nextBuildNumber
 * </pre>
 * 
 * The above properties contain simply incremented versions of the parsed version informations. Those can now be used to
 * update the version of your project via the following to the next Major version:
 * 
 * <pre>
 *   mvn build-helper:parse-version versions:set \
 *      -DnewVersion=\${parsedVersion.nextMajorVersion}.0.0 \
 *      versions:commit
 * </pre>
 *
 * It can of course being used to increment the minor version:
 * 
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
     * Prefix string to use for the set of formatted version properties.
     * @since 3.0.0
     */
    @Parameter( defaultValue = "formattedVersion" )
    private String formattedPropertyPrefix;
    
    /**
     * This can be used to make a particular format of the major number possible like padding it with zeros etc.
     * 
     * @since 3.0.0
     * @see https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html#syntax
     */
    @Parameter( defaultValue = "%02d" )
    private String formatMajor;

    /**
     * @since 3.0.0
     * @see https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html#syntax
     */
    @Parameter( defaultValue = "%02d" )
    private String formatMinor;

    /**
     * @since 3.0.0
     * @see https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html#syntax
     */
    @Parameter( defaultValue = "%02d" )
    private String formatIncremental;

    /**
     * @since 3.0.0
     * @see https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html#syntax
     */
    @Parameter( defaultValue = "%02d" )
    private String formatBuildNumber;

    /**
     * Execute the mojo. This sets the version properties on the project.
     */
    public void execute()
    {
        parseVersion( versionString );
    }

    private void defineVersionProperty( String name, String value )
    {
        defineProperty( propertyPrefix + '.' + name, value );
    }

    private void defineFormattedVersionProperty( String name, String value )
    {
        defineProperty( formattedPropertyPrefix + '.' + name, value );
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
        DefaultVersioning artifactVersion = new DefaultVersioning( version );

        getLog().debug( "Parsed Version" );
        getLog().debug( "         major: " + artifactVersion.getMajor() );
        getLog().debug( "         minor: " + artifactVersion.getMinor() );
        getLog().debug( "   incremental: " + artifactVersion.getPatch() );
        getLog().debug( "   buildnumber: " + artifactVersion.getBuildNumber() );
        getLog().debug( "     qualifier: " + artifactVersion.getQualifier() );

        defineVersionProperty( "majorVersion", artifactVersion.getMajor() );
        defineVersionProperty( "minorVersion", artifactVersion.getMinor() );
        defineVersionProperty( "incrementalVersion", artifactVersion.getPatch() );
        defineVersionProperty( "buildNumber", artifactVersion.getBuildNumber() );

        defineVersionProperty( "nextMajorVersion", artifactVersion.getMajor() + 1 );
        defineVersionProperty( "nextMinorVersion", artifactVersion.getMinor() + 1 );
        defineVersionProperty( "nextIncrementalVersion", artifactVersion.getPatch() + 1 );
        defineVersionProperty( "nextBuildNumber", artifactVersion.getBuildNumber() + 1 );

        defineFormattedVersionProperty( "majorVersion", String.format( formatMajor, artifactVersion.getMajor() ) );
        defineFormattedVersionProperty( "minorVersion", String.format( formatMinor, artifactVersion.getMinor() ) );
        defineFormattedVersionProperty( "incrementalVersion", String.format( formatIncremental, artifactVersion.getPatch() ) );
        defineFormattedVersionProperty( "buildNumber", String.format( formatBuildNumber, artifactVersion.getBuildNumber() ));

        defineFormattedVersionProperty( "nextMajorVersion", String.format( formatMajor, artifactVersion.getMajor() + 1 ));
        defineFormattedVersionProperty( "nextMinorVersion", String.format( formatMinor, artifactVersion.getMinor() + 1 ));
        defineFormattedVersionProperty( "nextIncrementalVersion", String.format( formatIncremental, artifactVersion.getPatch() + 1 ));
        defineFormattedVersionProperty( "nextBuildNumber", String.format( formatBuildNumber, artifactVersion.getBuildNumber() + 1 ));
        
        String osgi = artifactVersion.getAsOSGiVersion();

        String qualifier = artifactVersion.getQualifier();
        if ( qualifier == null )
        {
            qualifier = "";
        }

        defineVersionProperty( "qualifier", qualifier );

        defineVersionProperty( "osgiVersion", osgi );
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
     * @param formattedPropertyPrefix The prefix used for formatted properties.
     */
    public void setFormattedPropertyPrefix( String formattedPropertyPrefix )
    {
        this.formattedPropertyPrefix = formattedPropertyPrefix;
    }

    /**
     * @param formatMajor Set the format for major part.
     */
    public void setFormatMajor( String formatMajor )
    {
        this.formatMajor = formatMajor;
    }

    /**
     * @param formatMinor Set the format for minor part.
     */
    public void setFormatMinor( String formatMinor )
    {
        this.formatMinor = formatMinor;
    }

    /**
     * @param formatIncremental Set format for incremental part.
     */
    public void setFormatIncremental( String formatIncremental )
    {
        this.formatIncremental = formatIncremental;
    }

    /**
     * @param formatBuildNumber Set the format for the buildNumber part.
     */
    public void setFormatBuildNumber( String formatBuildNumber )
    {
        this.formatBuildNumber = formatBuildNumber;
    }

}
