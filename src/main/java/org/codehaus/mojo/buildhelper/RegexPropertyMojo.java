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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Sets a property by applying a regex replacement rule to a supplied value.
 *
 * @author Stephen Connolly
 * @phase validate
 * @goal regex-property
 * @description Sets a property after applying a regex
 * @since 1.7
 */
public class RegexPropertyMojo
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
     * The property to set.
     *
     * @parameter
     * @required
     */
    private String name;

    /**
     * The pre-transformation value.
     *
     * @parameter
     * @required
     */
    private String value;

    /**
     * The regex to replace.
     *
     * @parameter
     * @required
     */
    private String regex;

    /**
     * The replacement.
     *
     * @parameter
     * @required
     */
    private String replacement;

    /**
     * Whether to fail if no match is found.
     *
     * @parameter default-value="true"
     */
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
        getLog().info( "Setting property '" + name + "' to '" + value + "'." );
        project.getProperties().setProperty( name, value );
    }

}
