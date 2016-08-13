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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Define one or many properties as a result of a Beanshell script invocation. Like
 * <a href="http://docs.codehaus.org/display/GMAVEN/Executing+Groovy+Code">gmaven-plugin</a>, some variables are
 * defined:
 * <ul>
 * <li><code>project</code>: the actual Maven project,</li>
 * <li><code>session</code>: the executing <code>MavenSession</code>,</li>
 * <li><code>settings</code>: the executing <code>Settings</code>.</li>
 * </ul>
 *
 * @author Hervé Boutemy
 * @since 1.8
 */
@Mojo( name = "bsh-property", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true )
public class BeanshellPropertyMojo
    extends AbstractDefinePropertyMojo
{
    @Parameter( required = true )
    private String source;

    /**
     * List of property names to get from script context after execution. Can be omitted if no property needs to be
     * defined, just the script execution.
     */
    @Parameter
    private String[] properties;

    /**
     * The Maven Session.
     */
    @Parameter( readonly = true, defaultValue = "${session}" )
    private MavenSession mavenSession;

    /**
     * The Maven Settings.
     */
    @Parameter( readonly = true, defaultValue = "${settings}" )
    private Settings settings;

    /**
     * Main plugin execution
     */
    public void execute()
        throws MojoFailureException
    {
        Interpreter interpreter = new Interpreter();

        set( interpreter, "project", getProject() );
        set( interpreter, "session", mavenSession );
        set( interpreter, "settings", settings );

        try
        {
            interpreter.eval( source );
        }
        catch ( EvalError ee )
        {
            MojoFailureException mfe =
                new MojoFailureException( "error during Beanshell script execution: " + ee.getMessage() );
            mfe.initCause( ee );
            throw mfe;
        }

        if ( properties != null )
        {
            for ( String property : properties )
            {
                Object value;
                try
                {
                    value = interpreter.get( property );

                    if ( value != null )
                    {
                        defineProperty( property, value.toString() );
                    }
                }
                catch ( EvalError ee )
                {
                    MojoFailureException mfe = new MojoFailureException( "cannot get Beanshell global variable '"
                        + property + "': " + ee.getMessage() );
                    mfe.initCause( ee );
                    throw mfe;
                }
            }
        }
    }

    private void set( Interpreter interpreter, String name, Object value )
        throws MojoFailureException
    {
        try
        {
            interpreter.set( name, value );
        }
        catch ( EvalError ee )
        {
            MojoFailureException mfe = new MojoFailureException( "cannot define Beanshell global variable '" + name
                + "': " + ee.getMessage() );
            mfe.initCause( ee );
            throw mfe;
        }
    }
}
