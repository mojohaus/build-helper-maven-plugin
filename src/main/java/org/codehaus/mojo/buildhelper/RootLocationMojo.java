package org.codehaus.mojo.buildhelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingResult;

/**
 * This goal will get the location of the root folder within a multi module build as a property {@code rootlocation}.
 *
 * @author Karl Heinz Marbaise
 * @since 3.0.0
 */
@Mojo( name = "rootlocation", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true, requiresProject = true )
public class RootLocationMojo
    extends AbstractDefinePropertyMojo
{

    @Parameter( defaultValue = "${session}", readonly = true, required = true )
    private MavenSession session;
    @Component
    private ProjectBuilder projectBuilder;

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
    public void execute() throws MojoFailureException
    {
        if ( runOnlyAtExecutionRoot && !getProject().isExecutionRoot() )
        {
            getLog().info( "Skip getting the rootlocation in this project because it's not the Execution Root" );
        }
        else
        {
            try {
                MavenProject topLevelProject = getLocalRoot(project);
                defineProperty( rootLocationProperty, topLevelProject.getBasedir().getAbsolutePath() );
            }
            catch (IOException ex) {
                throw new MojoFailureException("Unable to detect root location: " + ex.getMessage(), ex);
            }
        }
    }

    /**
     * Finds the local root of the specified project.
     *
     * @param project The project to find the local root for.
     * @return The local root project (this may be the current project)
     */
    private MavenProject getLocalRoot( final MavenProject project ) throws IOException
    {
        MavenProject currentProject = project;
        MavenProject localRootProject = project;

        List<File> parentDirs = new ArrayList<>();
        getAllParentDirectories( project.getBasedir(), parentDirs );

        for (File parentDir : parentDirs) {
            getLog().debug( "Checking to see if " + parentDir + " is an aggregator parent" );
            File parent = new File( parentDir, "pom.xml" );
            if ( parent.isFile() )
            {
                try
                {
                    final ProjectBuildingResult result = projectBuilder.build( parent, session.getProjectBuildingRequest() );
                    final MavenProject parentProject = result.getProject();
                    final String currentProjectCanonicalPath = currentProject.getBasedir().getCanonicalPath();
                    if ( getAllChildModules( parentProject ).contains( currentProjectCanonicalPath ) )
                    {
                        getLog().debug( parentDir + " is an aggregator parent of current project " );
                        localRootProject = parentProject;
                        currentProject = parentProject;
                    }
                    else
                    {
                        getLog().debug( parentDir + " is not an aggregator parent of current project ("+getAllChildModules( parentProject )+"/"+currentProjectCanonicalPath+") " );
                    }
                }
                catch ( ProjectBuildingException e )
                {
                    getLog().warn( e );
                }
            }
        }

        getLog().debug( "Local aggregation root is " + localRootProject.getBasedir() );
        return localRootProject;
    }

    private void getAllParentDirectories( File directory, List<File> parents ) {
        File parent = directory.getParentFile();
        if ( parent != null && parent.isDirectory() ) {
            parents.add( parent );
            getAllParentDirectories( parent, parents );
        }
    }

    /**
     * Returns a set of all child modules for a project, including any defined in profiles (ignoring profile activation).
     *
     * @param project The project.
     * @return the set of all child modules of the project (canonical paths).
     */
    private Set<String> getAllChildModules( MavenProject project ) throws IOException
    {
        Model model = project.getOriginalModel();
        Set<String> paths = new TreeSet<>(getChildModuleCanoncialPath(project, model.getModules()));
        for ( Profile profile : model.getProfiles() )
        {
            paths.addAll( getChildModuleCanoncialPath( project, profile.getModules() ));
        }
        return paths;
    }
    
    private Set<String> getChildModuleCanoncialPath( MavenProject project, List<String> modules ) throws IOException {
        Set<String> paths = new TreeSet<>();
        for (String module : modules) {
            File file = new File( project.getBasedir(), module );
            paths.add(file.getCanonicalPath());
        }
        return paths;
    }

}