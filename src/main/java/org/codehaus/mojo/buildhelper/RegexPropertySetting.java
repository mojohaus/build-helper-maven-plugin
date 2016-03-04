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

import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

public class RegexPropertySetting
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

    /**
     * Change the case of the value to upper case if set to true.
     */
    @Parameter( defaultValue = "false" )
    private boolean toUpperCase = false;

    /**
     * Change the case of the value to lower case if set to true.
     */
    @Parameter( defaultValue = "false" )
    private boolean toLowerCase = false;

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

    public boolean isToUpperCase()
    {
        return toUpperCase;
    }

    public void setToUpperCase( boolean toUpperCase )
    {
        this.toUpperCase = toUpperCase;
    }

    public boolean isToLowerCase()
    {
        return toLowerCase;
    }

    public void setToLowerCase( boolean toLowerCase )
    {
        this.toLowerCase = toLowerCase;
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

        if ( toLowerCase && toUpperCase )
        {
            throw new IllegalArgumentException( "either toUpperCase or toLowerCase can be set, but not both." );
        }
    }

}
