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

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Sets multiple properties according to whether multiple sets of source and target resources are respectively up to
 * date.
 *
 * @author Adrian Price <a href="mailto:demonfiddler@virginmedia.com">demonfiddler@virginmedia.com</a>
 * @since 1.12
 */
@Mojo( name = "uptodate-properties", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true )
public class UpToDatePropertiesMojo
    extends AbstractUpToDatePropertyMojo
{
    /**
     * List of <code>UpToDatePropertySetting</code>s to apply.
     */
    @Parameter( required = false )
    private List<UpToDatePropertySetting> upToDatePropertySettings;

    /**
     * Disables the plug-in execution.
     */
    @Parameter( property = "buildhelper.uptodateproperties.skip", defaultValue = "false" )
    private boolean skip;

    /** {@inheritDoc} */
    @Override
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( skip )
        {
            getLog().info( "uptodate-properties is skipped." );
            return;
        }

        if ( upToDatePropertySettings != null )
        {
            for ( UpToDatePropertySetting config : upToDatePropertySettings )
            {
                this.execute( config );
            }
        }
    }
}
