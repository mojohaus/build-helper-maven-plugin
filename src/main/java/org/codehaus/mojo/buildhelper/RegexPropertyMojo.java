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

/**
 * Sets a property by applying a regex replacement rule to a supplied value.
 *
 * @author Stephen Connolly
 * @since 1.7
 */
@Mojo( name = "regex-property", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true )
public class RegexPropertyMojo
    extends AbstractRegexPropertyMojo
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
    private String replacement;

    /**
     * Whether to fail if no match is found.
     */
    @Parameter( defaultValue = "true" )
    private boolean failIfNoMatch;

    /**
     * Make the value of the resulting property upper case.
     * 
     * @since 1.11
     */
    @Parameter( defaultValue = "false" )
    private boolean toUpperCase;

    /**
     * Make the value of the resulting property lower case.
     * 
     * @since 1.11
     */
    @Parameter( defaultValue = "false" )
    private boolean toLowerCase;

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        RegexPropertySetting config = new RegexPropertySetting();
        config.setName( name );
        config.setValue( value );
        config.setRegex( regex );
        config.setReplacement( replacement );
        config.setFailIfNoMatch( failIfNoMatch );
        config.setToLowerCase( toLowerCase );
        config.setToUpperCase( toUpperCase );

        this.execute( config );

    }

}
