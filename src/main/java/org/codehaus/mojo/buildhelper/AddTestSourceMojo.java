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
import org.apache.maven.api.plugin.annotations.Mojo;
import org.apache.maven.api.plugin.annotations.Parameter;
import org.apache.maven.api.services.ProjectManager;

/**
 * Add test source directories to the POM.
 *
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @since 1.0
 */
@Mojo(name = "add-test-source", defaultPhase = "generate-test-sources")
public class AddTestSourceMojo extends AbstractMojo {

    /**
     * Additional test source directories.
     *
     * @since 1.0
     */
    @Parameter(required = true)
    private Path[] sources;

    /**
     * Skip plugin execution.
     *
     * @since 3.5.0
     */
    @Parameter(property = "buildhelper.addtestsource.skip", defaultValue = "false")
    private boolean skipAddTestSource;

    /**
     * If a directory does not exist, do not add it as a test source root.
     *
     * @since 3.5.0
     */
    @Parameter(property = "buildhelper.addtestsource.skipIfMissing", defaultValue = "false")
    private boolean skipAddTestSourceIfMissing;

    @Inject
    private Project project;

    @Inject
    private Session session;

    public void execute() {
        if (skipAddTestSource) {
            if (getLog().isInfoEnabled()) {
                getLog().info("Skipping plugin execution!");
            }
            return;
        }

        for (Path source : sources) {
            if (skipAddTestSourceIfMissing && !Files.exists(source)) {
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Skipping directory: " + source + ", because it does not exist.");
                }
            } else {
                ProjectManager projectManager = session.getService(ProjectManager.class);
                projectManager.addCompileSourceRoot(project, ProjectScope.TEST, source.toAbsolutePath());
                if (getLog().isInfoEnabled()) {
                    getLog().info("Test Source directory: " + source + " added.");
                }
            }
        }
    }
}
