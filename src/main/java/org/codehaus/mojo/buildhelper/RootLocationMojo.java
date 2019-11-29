package org.codehaus.mojo.buildhelper;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.annotation.Nonnull;

/**
 * This goal will get the location of the root folder within a multi module build as a property {@code rootlocation}.
 *
 * @author Karl Heinz Marbaise
 * @since 3.0.0
 */
@Mojo( name = "rootlocation", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true )
public class RootLocationMojo
    extends AbstractDefinePropertyMojo
{

    @Parameter( defaultValue = "${session}", readonly = true, required = true )
    private MavenSession session;

    /**
     * This will cause the execution to be run only at the top of a given module tree.
     */
    @Parameter( property = "buildhelper.runOnlyAtExecutionRoot", defaultValue = "false" )
    private boolean runOnlyAtExecutionRoot;

    /**
     * The name of the property in which to store the root location.
     */
    @Parameter( defaultValue = "rootlocation" )
    private String rootLocationProperty;

    /**
     * Main plugin execution
     */
    public void execute()
    {
        if ( runOnlyAtExecutionRoot && !getProject().isExecutionRoot() )
        {
            getLog().info( "Skip getting the rootlocation in this project because it's not the Execution Root" );
        }
        else
        {
            MavenProject topLevelProject = findHighestParentProject(session.getTopLevelProject());
            defineProperty( rootLocationProperty, topLevelProject.getBasedir().getAbsolutePath() );
        }
    }

    private MavenProject findHighestParentProject(@Nonnull MavenProject project)
    {
        // search up model hierarchy to find the highest basedir location
        MavenProject parent = project;
        while (parent.getParent() != null)
        {
            if (parent.getParent().getBasedir() == null)
            {
                // we've hit a parent that was resolved. Stop going higher up in the hierarchy
                break;
            }
            parent = parent.getParent();
        }
        return parent;
    }

}