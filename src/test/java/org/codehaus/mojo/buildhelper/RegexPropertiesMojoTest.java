package org.codehaus.mojo.buildhelper;

/*
 * The MIT License
 *
 * Copyright (c) MojoHaus and Contributors
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
/**
 * The {@code skip*} and {@code skip*IfMissing} parameters of the add-source and add-resource goals.
 */
/**
 * The {@code skip*} and {@code skip*IfMissing} parameters of the add-source and add-resource goals.
 */
import javax.inject.Inject;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The regex-properties goal applies every setting it is configured with.
 */
@MojoTest
class RegexPropertiesMojoTest {

    @Inject
    private MavenProject project;

    @Test
    @InjectMojo(goal = "regex-properties", pom = "classpath:/regex/regex-properties.xml")
    void appliesEverySetting(RegexPropertiesMojo mojo) throws Exception {
        mojo.execute();

        assertEquals("C:\\projects\\$1\\target\\", project.getProperties().getProperty("multiple.literal"));
        assertEquals("foo-bar", project.getProperties().getProperty("multiple.default"));
        assertEquals("1-RC", project.getProperties().getProperty("build.version"));
    }
}
