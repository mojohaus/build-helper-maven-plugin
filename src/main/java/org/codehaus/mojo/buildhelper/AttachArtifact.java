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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * Attach additional artifacts to be installed and deployed.
 *
 * @goal attach-artifact
 * @phase package
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id$
 * @since 1.0
 */
public class AttachArtifact
    extends AbstractMojo
{
    /**
     * Attach an array of artifacts to the project.
     *
     * @parameter
     * @required
     */
    private Artifact [] artifacts;

    /**
     * This project's base directory.
     *
     * @parameter expression="${basedir}"
     * @required
     * @since 1.5
     */
    private String basedir;

    /**
     * The Maven Session.
     *
     * @parameter expression="${session}"
     * @readonly
     * @required
     * @since 1.5
     */
    private MavenSession mavenSession;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Maven ProjectHelper.
     *
     * @component
     * @readonly
     */
    private MavenProjectHelper projectHelper;

    /**
     * This will cause the execution to be run only at the top of a given module
     * tree. That is, run in the project contained in the same folder where the
     * mvn execution was launched.
     *
     * @parameter expression="${buildhelper.runOnlyAtExecutionRoot}" default-value="false"
     * @since 1.5
     */
    private boolean runOnlyAtExecutionRoot;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        // Run only at the execution root
        if ( runOnlyAtExecutionRoot && !isThisTheExecutionRoot() )
        {
            getLog().info( "Skip attaching artifacts in this project because it's not the Execution Root" );
        }
        else
        {
            this.validateArtifacts();

            for ( int i = 0 ; i < this.artifacts.length; ++ i )
            {
                projectHelper.attachArtifact( this.project,
                                              this.artifacts[i].getType(),
                                              this.artifacts[i].getClassifier(),
                                              this.artifacts[i].getFile() );
            }
        }

    }

    /**
     * Returns <code>true</code> if the current project is located at the
     * Execution Root Directory (where mvn was launched).
     *
     * @return <code>true</code> if the current project is at the Execution Root
     */
    private boolean isThisTheExecutionRoot()
    {
        getLog().debug( "Root Folder:" + mavenSession.getExecutionRootDirectory() );
        getLog().debug( "Current Folder:" + basedir );
        boolean result = mavenSession.getExecutionRootDirectory().equalsIgnoreCase( basedir.toString() );
        if ( result )
        {
            getLog().debug( "This is the execution root." );
        }
        else
        {
            getLog().debug( "This is NOT the execution root." );
        }
        return result;
    }

    private void validateArtifacts()
        throws MojoFailureException
    {
        // check unique of types and classifiers
        Set extensionClassifiers = new HashSet();
        for ( int i = 0; i < this.artifacts.length; ++i )
        {
            Artifact artifact = this.artifacts[i];

            String extensionClassifier = artifact.getType() + ":" + artifact.getClassifier();

            if ( !extensionClassifiers.add( extensionClassifier  ) )
            {
                throw new MojoFailureException( "The artifact with same type and classifier: "
                                                + extensionClassifier + " is used more than once." );
            }

        }
    }
}
