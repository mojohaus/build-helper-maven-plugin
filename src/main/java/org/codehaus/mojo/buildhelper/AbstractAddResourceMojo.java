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

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.api.Project;
import org.apache.maven.api.ProjectScope;
import org.apache.maven.api.Session;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.plugin.annotations.Parameter;
import org.apache.maven.api.services.ProjectManager;
import org.codehaus.mojo.buildhelper.utils.Resource;

/**
 * Abstract Mojo for adding Resources
 */
public abstract class AbstractAddResourceMojo extends AbstractMojo {
    /**
     * Additional resource directories.
     */
    @Parameter(required = true)
    private Resource[] resources;

    /**
     * The maven project
     */
    @Inject
    private Project project;

    @Inject
    private Session session;

    /**
     * Main plugin execution
     */
    public void execute() {
        if (isSkip()) {
            if (getLog().isInfoEnabled()) {
                getLog().info("Skipping plugin execution!");
            }
            return;
        }

        for (Resource resource : resources) {
            // Check for relative paths in the resource configuration.
            // http://maven.apache.org/plugin-developers/common-bugs.html#Resolving_Relative_Paths
            Path resourceDir = project.getBasedir().resolve(resource.getDirectory());
            resource.setDirectory(resourceDir.toAbsolutePath().toString());

            if (isSkipIfMissing() && !Files.exists(resourceDir)) {
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Skipping directory: " + resourceDir + ", because it does not exist.");
                }
            } else {
                addResource(resource);
            }
        }
    }

    public void addResource(Resource resource) {
        getProjectManager().addResource(getProject(), getProjectScope(), newResource(resource));
        if (getLog().isDebugEnabled()) {
            getLog().debug((getProjectScope() == ProjectScope.MAIN ? "Added resource: " : "Added test resource: ")
                    + resource.getDirectory());
        }
    }

    static org.apache.maven.api.model.Resource newResource(Resource res) {
        return org.apache.maven.api.model.Resource.newBuilder()
                .directory(res.getDirectory())
                .filtering(Boolean.toString(res.isFiltering()))
                .excludes(res.getExcludes())
                .includes(res.getIncludes())
                .mergeId(res.getMergeId())
                .targetPath(res.getTargetPath())
                .build();
    }

    protected abstract boolean isSkipIfMissing();

    protected abstract boolean isSkip();

    protected abstract ProjectScope getProjectScope();

    /**
     * Get the current project instance.
     *
     * @return the project
     */
    public Project getProject() {
        return this.project;
    }

    public ProjectManager getProjectManager() {
        return session.getService(ProjectManager.class);
    }
}
