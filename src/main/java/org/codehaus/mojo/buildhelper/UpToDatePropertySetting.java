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
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.plexus.util.StringUtils;

/**
 * Holds settings for <code>AbstractUpToDateProperty</code> subclasses.
 *
 * @author Adrian Price <a href="mailto:demonfiddler@virginmedia.com">demonfiddler@virginmedia.com</a>
 * @since 1.12
 */
public final class UpToDatePropertySetting
{
    /**
     * A set of source files.
     */
    @Parameter
    private FileSet fileSet;

    /**
     * The name of the property to set.
     */
    @Parameter( required = true )
    private String name;

    /**
     * The property value to set if the up-to-date condition is fulfilled.
     */
    @Parameter( defaultValue = "true" )
    private String value = "true";

    /**
     * The property value to set if the up-to-date condition is not fulfilled.
     */
    @Parameter( alias = "else" )
    private String elseValue;

    public UpToDatePropertySetting()
    {
    }

    public String getName()
    {
        return name;
    }

    public FileSet getFileSet()
    {
        return fileSet;
    }

    public String getValue()
    {
        return value;
    }

    public void setFileSet( FileSet fileSet )
    {
        this.fileSet = fileSet;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getElse()
    {
        return elseValue;
    }

    public void setElse( String elseValue )
    {
        this.elseValue = elseValue;
    }

    void validate()
    {
        if ( StringUtils.isBlank( name ) )
        {
            throw new IllegalArgumentException( "name required" );
        }

        if ( StringUtils.isBlank( value ) )
        {
            throw new IllegalArgumentException( "value required" );
        }

        if ( StringUtils.equals( value, elseValue ) )
        {
            throw new IllegalArgumentException( "value and else cannot be the same" );
        }

        if ( fileSet == null )
        {
            throw new IllegalArgumentException( "fileSet required" );
        }

        if ( StringUtils.isBlank( fileSet.getDirectory() ) )
        {
            throw new IllegalArgumentException( "directory required for " + fileSet );
        }

        if ( fileSet.getMapper() == null )
        {
            throw new IllegalArgumentException( "mapper required for " + fileSet );
        }
    }
}