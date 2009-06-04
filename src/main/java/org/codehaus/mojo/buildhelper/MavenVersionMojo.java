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

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Store the maven core version in a property <code>maven.version</code>.
 * 
 * @goal maven-version
 * @phase validate
 * @author pgier
 * @since 1.3
 */
public class MavenVersionMojo
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
     * The RuntimeInforamtion for the current instance of maven.
     * 
     * @component
     */
    private RuntimeInformation runtime;

    /**
     * The name of the property in which to store the version of maven.
     * 
     * @parameter default-value="maven.version"
     */
    private String versionProperty;

    /**
     * Main plugin execution
     * 
     * @throws MojoExecutionException if the plugin execution fails.
     */
    public void execute()
        throws MojoExecutionException
    {
        ArtifactVersion mavenVersion = runtime.getApplicationVersion();

        getLog().debug( "Retrieved maven version: " + mavenVersion.toString() );

        if ( project != null )
        {
            project.getProperties().put( versionProperty, mavenVersion.toString() );
        }
    }

}