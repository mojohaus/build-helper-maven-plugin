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

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Add more test resource directories to the POM.
 *
 * @goal add-test-resource
 * @phase generate-test-resources
 * @author Paul Gier
 * @version $Id$
 * @since 1.3
 */
public class AddTestResourceMojo
    extends AbstractMojo
{
    /**
     * Additional source directories.
     *
     * @parameter 
     * @required
     */
    private Resource [] resources;

    /**
     * The maven project
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;


    public void execute()
        throws MojoExecutionException
    {        
        for ( int i = 0; i < resources.length; ++i )
        {
            this.project.addResource( resources[i] );
                
            this.getLog().debug( "Test Resource: " + this.resources[i].getDirectory() + " added." );              
        }
    }
}
