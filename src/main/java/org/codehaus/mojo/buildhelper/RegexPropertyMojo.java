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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Sets a property by applying a regex replacement rule to a supplied value.
 *
 * @author Stephen Connolly
 * @since 1.7
 */
@Mojo( name = "regex-property", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true )
public class RegexPropertyMojo
    extends AbstractDefinePropertyMojo
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
    @Parameter( required = true )
    private String replacement;

    /**
     * Whether to fail if no match is found.
     */
    @Parameter( defaultValue = "true" )
    private boolean failIfNoMatch;

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        Pattern pattern;
        try
        {
            pattern = Pattern.compile( regex );
        }
        catch ( PatternSyntaxException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        Matcher matcher = pattern.matcher( value );
        if ( !matcher.find() )
        {
            if ( failIfNoMatch )
            {
                throw new MojoFailureException( "No match to regex '" + regex + "' found in '" + value + "'." );
            }
            else
            {
                getLog().info( "No match to regex '" + regex + "' found in '" + value + "'." );
            }
        }
        else
        {
            value = matcher.replaceAll( replacement );
        }

        defineProperty( name, value );
    }

}
