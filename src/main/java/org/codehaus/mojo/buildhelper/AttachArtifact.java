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

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;


/**
 * Attach additional artifacts to be installed and deployed
 * @goal attach-artifact
 * @phase package
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id$
 */


public class AttachArtifact
    extends AbstractMojo
{
    
    /**
     * Attach an array of artifact to the project
     * @parameter 
     * @required
     */
  
    private Artifact [] artifacts;    

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Maven ProjectHelper
     * @component
     * @readonly
     */
    private MavenProjectHelper projectHelper;    

    public void execute()
        throws MojoExecutionException, MojoFailureException
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
