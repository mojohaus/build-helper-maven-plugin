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

import org.apache.maven.RepositoryUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepositoryManager;

/**
 * Remove project's artifacts from local repository. Useful to keep only one copy of large local snapshot, for example:
 * installer, for disk space optimization purpose.
 *
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @since 1.1
 * @deprecated There is a similar goal:
 * <a href="https://maven.apache.org/plugins/maven-dependency-plugin/purge-local-repository-mojo.html">dependency:purge-local-repository</a>
 * By the way such goal should by not used with Maven 3/4 anymore.
 */
@Deprecated
@Mojo(name = "remove-project-artifact", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
public class RemoveLocalArtifactMojo extends AbstractMojo {

    /**
     * When true, remove all built artifacts including all versions. When false, remove all built artifacts of this
     * project version.
     *
     * @since 1.1
     */
    @Parameter(defaultValue = "true", property = "buildhelper.removeAll")
    private boolean removeAll;

    /**
     * Indicates whether the build will continue even if there are removal errors.
     *
     * @since 1.6
     */
    @Parameter(defaultValue = "true", property = "buildhelper.failOnError")
    private boolean failOnError;

    /**
     * @since 1.1
     */
    @Parameter(readonly = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(readonly = true, defaultValue = "${repositorySystemSession}")
    private RepositorySystemSession repoSession;

    public void execute() throws MojoExecutionException {
        LocalRepositoryManager lrm = repoSession.getLocalRepositoryManager();

        String artifactPath = lrm.getPathForLocalArtifact(RepositoryUtils.toArtifact(project.getArtifact()));
        File repoBasedir = lrm.getRepository().getBasedir();

        File localArtifactFile = new File(repoBasedir, artifactPath);
        File localArtifactDirectory = localArtifactFile.getParentFile();

        if (removeAll) {
            localArtifactDirectory = localArtifactDirectory.getParentFile();
        }

        try {
            FileUtils.deleteDirectory(localArtifactDirectory);

            if (getLog().isInfoEnabled()) {
                getLog().info(localArtifactDirectory.getAbsolutePath() + " removed.");
            }
        } catch (IOException e) {
            final String failureMessage = "Cannot delete " + localArtifactDirectory;
            if (failOnError) {
                throw new MojoExecutionException(failureMessage);
            } else {
                getLog().warn(failureMessage);
            }
        }
    }
}
