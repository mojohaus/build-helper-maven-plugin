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
import javax.inject.Inject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoParameter;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * The {@code skip*} and {@code skip*IfMissing} parameters of the add-source and add-resource goals.
 */
@MojoTest
class AddSkipParametersTest {

    private static final String SOURCES = "classpath:/add-skip/sources.xml";

    private static final String RESOURCES = "classpath:/add-skip/resources.xml";

    @Inject
    private MavenProject project;

    @Inject
    private Log log;

    private List<String> compileSourceRoots;

    private List<String> testCompileSourceRoots;

    private int resources;

    private int testResources;

    @BeforeEach
    void setUp() {
        doReturn(true).when(log).isDebugEnabled();
        compileSourceRoots = new ArrayList<>(project.getCompileSourceRoots());
        testCompileSourceRoots = new ArrayList<>(project.getTestCompileSourceRoots());
        resources = project.getResources().size();
        testResources = project.getTestResources().size();
    }

    @Test
    @InjectMojo(goal = "add-source", pom = SOURCES)
    @MojoParameter(name = "skipAddSource", value = "true")
    void addSourceSkip(AddSourceMojo mojo) {
        mojo.execute();

        verify(log).info("Skipping plugin execution!");
        assertEquals(compileSourceRoots, project.getCompileSourceRoots());
    }

    @Test
    @InjectMojo(goal = "add-source", pom = SOURCES)
    @MojoParameter(name = "skipAddSourceIfMissing", value = "true")
    void addSourceSkipIfMissing(AddSourceMojo mojo) {
        mojo.execute();

        verify(log).debug("Skipping directory: " + missing() + ", because it does not exist.");
        assertEquals(compileSourceRoots, project.getCompileSourceRoots());
    }

    @Test
    @InjectMojo(goal = "add-test-source", pom = SOURCES)
    @MojoParameter(name = "skipAddTestSource", value = "true")
    void addTestSourceSkip(AddTestSourceMojo mojo) {
        mojo.execute();

        verify(log).info("Skipping plugin execution!");
        assertEquals(testCompileSourceRoots, project.getTestCompileSourceRoots());
    }

    @Test
    @InjectMojo(goal = "add-test-source", pom = SOURCES)
    @MojoParameter(name = "skipAddTestSourceIfMissing", value = "true")
    void addTestSourceSkipIfMissing(AddTestSourceMojo mojo) {
        mojo.execute();

        verify(log).debug("Skipping directory: " + missing() + ", because it does not exist.");
        assertEquals(testCompileSourceRoots, project.getTestCompileSourceRoots());
    }

    @Test
    @InjectMojo(goal = "add-resource", pom = RESOURCES)
    @MojoParameter(name = "skipAddResource", value = "true")
    void addResourceSkip(AddResourceMojo mojo) throws Exception {
        mojo.execute();

        verify(log).info("Skipping plugin execution!");
        assertEquals(resources, project.getResources().size());
    }

    @Test
    @InjectMojo(goal = "add-resource", pom = RESOURCES)
    @MojoParameter(name = "skipAddResourceIfMissing", value = "true")
    void addResourceSkipIfMissing(AddResourceMojo mojo) throws Exception {
        mojo.execute();

        verify(log).debug("Skipping directory: " + missing() + ", because it does not exist.");
        assertEquals(resources, project.getResources().size());
    }

    @Test
    @InjectMojo(goal = "add-test-resource", pom = RESOURCES)
    @MojoParameter(name = "skipAddTestResource", value = "true")
    void addTestResourceSkip(AddTestResourceMojo mojo) throws Exception {
        mojo.execute();

        verify(log).info("Skipping plugin execution!");
        assertEquals(testResources, project.getTestResources().size());
    }

    @Test
    @InjectMojo(goal = "add-test-resource", pom = RESOURCES)
    @MojoParameter(name = "skipAddTestResourceIfMissing", value = "true")
    void addTestResourceSkipIfMissing(AddTestResourceMojo mojo) throws Exception {
        mojo.execute();

        verify(log).debug("Skipping directory: " + missing() + ", because it does not exist.");
        assertEquals(testResources, project.getTestResources().size());
    }

    private File missing() {
        return new File(project.getBasedir(), "not-existing");
    }
}
