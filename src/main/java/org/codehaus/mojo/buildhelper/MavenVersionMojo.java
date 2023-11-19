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

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.rtinfo.RuntimeInformation;

/**
 * Store the maven core version in a property <code>maven.version</code>.
 *
 * @author pgier
 * @since 1.3
 * @deprecated Maven since version {@code 3.0.4} has such property build in:
 *         <a href="https://issues.apache.org/jira/browse/MNG-4112">MNG-4112</a>.
 *         So goal can be removed.
 */
@Deprecated
@Mojo(name = "maven-version", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true)
public class MavenVersionMojo extends AbstractDefinePropertyMojo {

    /**
     * The RuntimeInforamtion for the current instance of Maven.
     */
    @Component
    private RuntimeInformation runtime;

    /**
     * The name of the property in which to store the version of Maven.
     */
    @Parameter(defaultValue = "maven.version")
    private String versionProperty;

    /**
     * Main plugin execution
     */
    public void execute() {
        defineProperty(versionProperty, runtime.getMavenVersion());
    }
}
