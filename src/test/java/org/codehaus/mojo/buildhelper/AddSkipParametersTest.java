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

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The {@code skip*} and {@code skip*IfMissing} parameters of the add-source and add-resource goals.
 */
class AddSkipParametersTest {

    @TempDir
    File basedir;

    private MavenProject project;

    private File missing;

    private final RecordingLog log = new RecordingLog();

    @BeforeEach
    void setUp() {
        project = new MavenProject();
        missing = new File(basedir, "not-existing");
    }

    @Test
    void addSourceSkip() throws Exception {
        AddSourceMojo mojo = new AddSourceMojo();
        configure(mojo, "sources", new File[] {missing}, "skipAddSource", true);

        mojo.execute();

        assertTrue(log.contains("Skipping plugin execution!"), log.toString());
        assertEquals(0, project.getCompileSourceRoots().size());
    }

    @Test
    void addSourceSkipIfMissing() throws Exception {
        AddSourceMojo mojo = new AddSourceMojo();
        configure(mojo, "sources", new File[] {missing}, "skipAddSourceIfMissing", true);

        mojo.execute();

        assertTrue(log.contains("Skipping directory: " + missing + ", because it does not exist."), log.toString());
        assertEquals(0, project.getCompileSourceRoots().size());
    }

    @Test
    void addTestSourceSkip() throws Exception {
        AddTestSourceMojo mojo = new AddTestSourceMojo();
        configure(mojo, "sources", new File[] {missing}, "skipAddTestSource", true);

        mojo.execute();

        assertTrue(log.contains("Skipping plugin execution!"), log.toString());
        assertEquals(0, project.getTestCompileSourceRoots().size());
    }

    @Test
    void addTestSourceSkipIfMissing() throws Exception {
        AddTestSourceMojo mojo = new AddTestSourceMojo();
        configure(mojo, "sources", new File[] {missing}, "skipAddTestSourceIfMissing", true);

        mojo.execute();

        assertTrue(log.contains("Skipping directory: " + missing + ", because it does not exist."), log.toString());
        assertEquals(0, project.getTestCompileSourceRoots().size());
    }

    @Test
    void addResourceSkip() throws Exception {
        AddResourceMojo mojo = new AddResourceMojo();
        configure(mojo, "resources", new Resource[] {missingResource()}, "skipAddResource", true);

        mojo.execute();

        assertTrue(log.contains("Skipping plugin execution!"), log.toString());
        assertEquals(0, project.getResources().size());
    }

    @Test
    void addResourceSkipIfMissing() throws Exception {
        AddResourceMojo mojo = new AddResourceMojo();
        configure(mojo, "resources", new Resource[] {missingResource()}, "skipAddResourceIfMissing", true);

        mojo.execute();

        assertTrue(log.contains("Skipping directory: " + missing + ", because it does not exist."), log.toString());
        assertEquals(0, project.getResources().size());
    }

    @Test
    void addTestResourceSkip() throws Exception {
        AddTestResourceMojo mojo = new AddTestResourceMojo();
        configure(mojo, "resources", new Resource[] {missingResource()}, "skipAddTestResource", true);

        mojo.execute();

        assertTrue(log.contains("Skipping plugin execution!"), log.toString());
        assertEquals(0, project.getTestResources().size());
    }

    @Test
    void addTestResourceSkipIfMissing() throws Exception {
        AddTestResourceMojo mojo = new AddTestResourceMojo();
        configure(mojo, "resources", new Resource[] {missingResource()}, "skipAddTestResourceIfMissing", true);

        mojo.execute();

        assertTrue(log.contains("Skipping directory: " + missing + ", because it does not exist."), log.toString());
        assertEquals(0, project.getTestResources().size());
    }

    private Resource missingResource() {
        Resource resource = new Resource();
        resource.setDirectory(missing.getAbsolutePath());
        resource.setTargetPath("does-not-matter");
        return resource;
    }

    private void configure(AbstractMojo mojo, String items, Object value, String skipField, boolean skip)
            throws ReflectiveOperationException {
        mojo.setLog(log);
        set(mojo, "project", project);
        set(mojo, items, value);
        set(mojo, skipField, skip);
    }

    private static void set(Object target, String name, Object value) throws ReflectiveOperationException {
        for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException e) {
                // look in the superclass
            }
        }
        throw new NoSuchFieldException(name);
    }

    /** Records info and debug messages, with debug enabled as under {@code -X}. */
    private static final class RecordingLog extends SystemStreamLog {
        private final List<String> messages = new ArrayList<>();

        @Override
        public boolean isDebugEnabled() {
            return true;
        }

        @Override
        public void debug(CharSequence content) {
            messages.add(content.toString());
        }

        @Override
        public void info(CharSequence content) {
            messages.add(content.toString());
        }

        boolean contains(String message) {
            return messages.contains(message);
        }

        @Override
        public String toString() {
            return messages.toString();
        }
    }
}
