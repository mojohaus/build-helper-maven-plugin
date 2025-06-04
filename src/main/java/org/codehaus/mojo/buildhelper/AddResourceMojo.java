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

import org.apache.maven.api.ProjectScope;
import org.apache.maven.api.plugin.annotations.Mojo;
import org.apache.maven.api.plugin.annotations.Parameter;

/**
 * Add more resource directories to the POM.
 *
 * @author Paul Gier
 * @since 1.3
 */
@Mojo(name = "add-resource", defaultPhase = "generate-resources")
public class AddResourceMojo extends AbstractAddResourceMojo {

    /**
     * Skip plugin execution.
     *
     * @since 3.5.0
     */
    @Parameter(property = "buildhelper.addresource.skip", defaultValue = "false")
    private boolean skipAddResource;

    /**
     * If a resource directory does not exist, do not add it as a root.
     *
     * @since 3.5.0
     */
    @Parameter(property = "buildhelper.addresource.skipIfMissing", defaultValue = "false")
    private boolean skipAddResourceIfMissing;

    @Override
    protected boolean isSkipIfMissing() {
        return skipAddResourceIfMissing;
    }

    @Override
    protected boolean isSkip() {
        return skipAddResource;
    }

    @Override
    protected ProjectScope getProjectScope() {
        return ProjectScope.MAIN;
    }
}
