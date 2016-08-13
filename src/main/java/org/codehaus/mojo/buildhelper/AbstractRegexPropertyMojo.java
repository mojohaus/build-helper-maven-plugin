package org.codehaus.mojo.buildhelper;

import java.util.Locale;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

public abstract class AbstractRegexPropertyMojo
    extends AbstractDefinePropertyMojo
{

    protected void execute( RegexPropertySetting config )
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            config.validate();
        }
        catch ( IllegalArgumentException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

        Pattern pattern;
        try
        {
            pattern = Pattern.compile( config.getRegex() );
        }
        catch ( PatternSyntaxException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        Matcher matcher = pattern.matcher( config.getValue() );

        if ( matcher.find() )
        {
            // if the string replacement is empty, we define the value replacement to empty.
            config.setValue( ( StringUtils.isNotEmpty( config.getReplacement() )
                            ? matcher.replaceAll( config.getReplacement() ) : matcher.replaceAll( "" ) ) );
        }
        else
        {
            if ( config.isFailIfNoMatch() )
            {
                throw new MojoFailureException( "No match to regex '" + config.getRegex() + "' found in '"
                    + config.getValue() + "'." );
            }
            else
            {
                getLog().info( "No match to regex '" + config.getRegex() + "' found in '" + config.getValue() + "'. "
                    + "The initial value '" + config.getValue() + "' is left as-is..." );
            }
        }

        if ( config.isToLowerCase() )
        {
            config.setValue( config.getValue().toLowerCase( Locale.getDefault() ) );
        }

        if ( config.isToUpperCase() )
        {
            config.setValue( config.getValue().toUpperCase( Locale.getDefault() ) );
        }

        defineProperty( config.getName(), config.getValue() );

    }
}
