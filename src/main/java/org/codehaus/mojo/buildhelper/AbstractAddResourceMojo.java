package org.codehaus.mojo.buildhelper;

import java.io.File;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

public abstract class AbstractAddResourceMojo
    extends AbstractMojo
{
    /**
     * Additional source directories.
     * 
     * @parameter
     * @required
     */
    private Resource[] resources;

    /**
     * The maven project
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Main plugin execution
     */
    public void execute()
        throws MojoExecutionException
    {        
        for ( int i = 0; i < resources.length; ++i )
        {
            
            // Check for relative paths in the resource configuration.
            // http://maven.apache.org/plugin-developers/common-bugs.html#Resolving_Relative_Paths
            File resourceDir = new File( resources[i].getDirectory() );
            if ( ! resourceDir.isAbsolute() )
            {
                resourceDir = new File( project.getBasedir(), resources[i].getDirectory() );
                resources[i].setDirectory( resourceDir.getAbsolutePath() );
            }
            
            addResource ( resources[i] );
        }
    }
    
    /**
     * Add the resource to the project.
     * 
     * @param resource
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
