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
import java.io.IOException;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

/**
 * Remove project's artifacts from local repository. Useful to keep only one copy of large local snapshot, 
 * for example: installer, for disk space optimization purpose. 
 *
 * @goal remove-project-artifact
 * @phase package
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id$
 * @since 1.1
 */
public class RemoveLocalArtifactMojo
    extends AbstractMojo
{

    /**
     * When true, remove all built artifacts including all versions.
     * When false, remove all built artifacts of this project version.
     *
     * @parameter default-value="true" expression="${buildhelper.removeAll}"
     * @since 1.1
     *
     */
    private boolean removeAll;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @since 1.1
     */
    private MavenProject project;

    /**
     * @parameter default-value="${localRepository}"
     * @required
     * @readonly
     * @since 1.1
     */
    private ArtifactRepository localRepository;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        File localArtifactFile =
            new File( localRepository.getBasedir(), localRepository.pathOf( project.getArtifact() ) );

        File localArtifactDirectory = localArtifactFile.getParentFile();

        if ( removeAll )
        {
            localArtifactDirectory = localArtifactDirectory.getParentFile();
        }

        try
        {
            FileUtils.deleteDirectory( localArtifactDirectory );
        }
        catch ( IOException e )
        {
            throw new MojoFailureException( "Cannot delete " + localArtifactDirectory );
        }

        this.getLog().info( localArtifactDirectory.getAbsolutePath() + " removed." );

    }
}
