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
import org.apache.maven.shared.model.fileset.FileSet;

/**
 * Sets a property according to whether one set of resources is up to date with respect to another.
 *
 * @author Adrian Price <a href="mailto:demonfiddler@virginmedia.com">demonfiddler@virginmedia.com</a>
 * @since 1.12
 */
@Mojo( name = "uptodate-property", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true )
public class UpToDatePropertyMojo
    extends AbstractUpToDatePropertyMojo
{
    /**
     * The name of the property to set.
     */
    @Parameter( required = true )
    private String name;

    /**
     * Disables the plug-in execution.
     */
    @Parameter( property = "buildhelper.uptodateproperty.skip", defaultValue = "false" )
    private boolean skip;

    /**
     * The file set to check.
     */
    @Parameter( required = true )
    private FileSet fileSet;

    /**
     * The property value to set if the up-to-date condition is fulfilled.
     */
    @Parameter( defaultValue = "true" )
    private String value;

    /**
     * The property value to set if the up-to-date condition is not fulfilled.
     */
    @Parameter( alias = "else" )
    private String elseValue;

    /** {@inheritDoc} */
    @Override
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( skip )
        {
            getLog().info( "uptodate-property is skipped." );
            return;
        }

        UpToDatePropertySetting config = new UpToDatePropertySetting();
        config.setName( name );
        config.setValue( value );
        config.setElse( elseValue );
        config.setFileSet( fileSet );
        execute( config );
    }
}
