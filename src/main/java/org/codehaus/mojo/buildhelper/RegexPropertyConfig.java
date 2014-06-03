package org.codehaus.mojo.buildhelper;

import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

public class RegexPropertyConfig
{
    /**
     * The property to set.
     */
    @Parameter( required = true )
    private String name;

    /**
     * The pre-transformation value.
     */
    @Parameter( required = true )
    private String value;

    /**
     * The regex to replace.
     */
    @Parameter( required = true )
    private String regex;

    /**
     * The replacement.
     */
    @Parameter( defaultValue = "" )
    private String replacement = "";

    /**
     * Whether to fail if no match is found.
     */
    @Parameter( defaultValue = "true" )
    private boolean failIfNoMatch = true;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getRegex()
    {
        return regex;
    }

    public void setRegex( String regex )
    {
        this.regex = regex;
    }

    public String getReplacement()
    {
        return replacement;
    }

    public void setReplacement( String replacement )
    {
        this.replacement = replacement;
    }

    public boolean isFailIfNoMatch()
    {
        return failIfNoMatch;
    }

    public void setFailIfNoMatch( boolean failIfNoMatch )
    {
        this.failIfNoMatch = failIfNoMatch;
    }

    public void validate()
    {
        if ( StringUtils.isBlank( name ) )
        {
            throw new IllegalArgumentException( "name required" );
        }

        if ( StringUtils.isBlank( value ) )
        {
            throw new IllegalArgumentException( "value required" );
        }

        if ( StringUtils.isBlank( regex ) )
        {
            throw new IllegalArgumentException( "regex required" );
        }
    }

}
