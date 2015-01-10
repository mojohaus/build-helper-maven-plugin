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

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Abstract Mojo for adding Resources
 */
public abstract class AbstractAddResourceMojo
    extends AbstractMojo
{
    /**
     * Additional resource directories.
     */
    @Parameter( required = true )
    private Resource[] resources;

    /**
     * The maven project
     */
    @Parameter( readonly = true, defaultValue = "${project}" )
    private MavenProject project;

    /**
     * Main plugin execution
     */
    public void execute()
    {
        for ( Resource resource : resources )
        {
            // Check for relative paths in the resource configuration.
            // http://maven.apache.org/plugin-developers/common-bugs.html#Resolving_Relative_Paths
            File resourceDir = new File( resource.getDirectory() );
            if ( !resourceDir.isAbsolute() )
            {
                resourceDir = new File( project.getBasedir(), resource.getDirectory() );
                resource.setDirectory( resourceDir.getAbsolutePath() );
            }

            addResource( resource );
        }
    }

    /**
     * Add the resource to the project.
     *
     * @param resource the resource to add
     */
    public abstract void addResource( Resource resource );

    /**
     * Get the current project instance.
     *
     * @return the project
     */
    public MavenProject getProject()
    {
        return this.project;
    }
}
