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
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * Attach additional artifacts to be installed and deployed.
 *
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id$
 * @since 1.0
 */
@Mojo( name = "attach-artifact", defaultPhase = LifecyclePhase.PACKAGE /*, threadSafe = true ? TODO investigate MBUILDHELPER-43 */ )
public class AttachArtifactMojo
    extends AbstractMojo
{
    /**
     * Attach an array of artifacts to the project.
     */
    @Parameter( required = true )
    private Artifact [] artifacts;

    /**
     * This project's base directory.
     *
     * @since 1.5
     */
    @Parameter( defaultValue = "${basedir}" )
    private String basedir;

    /**
     * The Maven Session.
     *
     * @since 1.5
     */
    @Component
    private MavenSession mavenSession;

    @Component
    private MavenProject project;

    /**
     * Maven ProjectHelper.
     */
    @Component
    private MavenProjectHelper projectHelper;

    /**
     * This will cause the execution to be run only at the top of a given module
     * tree. That is, run in the project contained in the same folder where the
     * mvn execution was launched.
     *
     * @since 1.5
     */
    @Parameter( property = "buildhelper.runOnlyAtExecutionRoot", defaultValue = "false" )
    private boolean runOnlyAtExecutionRoot;

    /**
     * This allows to skip the attach execution in case it is known that the corresponding file does not exists.
     * For exemple, when the previous ant-run task is skipped with a unless.
     * 
     * @since 1.6
     */
    @Parameter( property = "buildhelper.skipAttach", defaultValue = "false" )
    private boolean skipAttach;
    
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        if ( skipAttach )
        {
            getLog().info( "Skip attaching artifacts" );
            return;
        }
        
        // Run only at the execution root
        if ( runOnlyAtExecutionRoot && !isThisTheExecutionRoot() )
        {
            getLog().info( "Skip attaching artifacts in this project because it's not the Execution Root" );
        }
        else
        {
            this.validateArtifacts();

            for ( Artifact artifact : artifacts )
            {
                projectHelper.attachArtifact( this.project,
                                              artifact.getType(),
                                              artifact.getClassifier(),
                                              artifact.getFile() );
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
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "Root Folder:" + mavenSession.getExecutionRootDirectory() );
            getLog().debug( "Current Folder:" + basedir );
            
        }
        boolean result = mavenSession.getExecutionRootDirectory().equalsIgnoreCase( basedir.toString() );
        if ( getLog().isDebugEnabled() )
        {
            if ( result )
            {
                getLog().debug( "This is the execution root." );
            }
            else
            {
                getLog().debug( "This is NOT the execution root." );
            }
        }
        return result;
    }

    private void validateArtifacts()
        throws MojoFailureException
    {
        // check unique of types and classifiers
        Set<String> extensionClassifiers = new HashSet<String>();
        for ( Artifact artifact : artifacts )
        {
            String extensionClassifier = artifact.getType() + ":" + artifact.getClassifier();

            if ( !extensionClassifiers.add( extensionClassifier ) )
            {
                throw new MojoFailureException( "The artifact with same type and classifier: "
                                                + extensionClassifier + " is used more than once." );
            }
        }
    }
}
