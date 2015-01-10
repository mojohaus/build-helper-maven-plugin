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

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Add more source directories to the POM.
 *
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id$
 * @since 1.0
 */
@Mojo( name = "add-source", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true )
public class AddSourceMojo
    extends AbstractMojo
{
    /**
     * Additional source directories.
     *
     * @since 1.0
     */
    @Parameter( required = true )
    private File[] sources;

    /**
     * @since 1.0
     */
    @Parameter( readonly = true, defaultValue = "${project}" )
    private MavenProject project;

    public void execute()
    {
        for ( File source : sources )
        {
            this.project.addCompileSourceRoot( source.getAbsolutePath() );
            if ( getLog().isInfoEnabled() )
            {
                getLog().info( "Source directory: " + source + " added." );
            }
        }
    }
}
