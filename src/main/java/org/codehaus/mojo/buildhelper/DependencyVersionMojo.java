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

import org.apache.maven.model.Dependency;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import java.util.List;

/**
 * Store the version of all dependencies in properties prefixed with <code>dependency</code>.
 * The property name is formed as: <code>prefix + groupId + separator + artifactId</code>
 * E.g. property named <code>dependency.com.foo:bar</code> with a value of <code>1.0.5</code>
 *
 * @author <a href="jordan@jordanzimmerman.com">Jordan Zimmerman</a>
 * @since 1.9.2
 */
@Mojo(name = "dependency-version", defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class DependencyVersionMojo
    extends AbstractDefinePropertyMojo
{
    /**
     * The property prefix
     */
    @Parameter(defaultValue = "dependency.")
    private String prefix;

    /**
     * The separator between the GroupId and ArtifactId
     */
    @Parameter(defaultValue = ".")
    private String separator;

    /**
     * If true, properties for the the dependency management section
     * are also set
     */
    @Parameter(defaultValue = "false")
    private boolean includeDependencyManagement;

    /**
     * Main plugin execution
     */
    public void execute()
    {
        if ( includeDependencyManagement )
        {
            apply(getProject().getDependencyManagement().getDependencies());
        }

        //noinspection unchecked
        apply(getProject().getDependencies());
    }

    private void apply(List<Dependency> dependencies)
    {
        for ( Dependency dependency : dependencies )
        {
            String name = prefix + dependency.getGroupId() + separator + dependency.getArtifactId();
            defineProperty(name, dependency.getVersion());
        }
    }
}