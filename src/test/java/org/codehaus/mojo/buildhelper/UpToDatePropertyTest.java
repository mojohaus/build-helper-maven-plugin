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

import javax.inject.Inject;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.api.plugin.testing.Basedir;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The uptodate-property and uptodate-properties goals, configured by {@code src/test/resources/uptodate/*.xml} and run
 * on file trees that {@link FixtureUtils} builds from {@code src/test/resources/uptodate/*.properties}, with the same
 * relative timestamps the integration tests used.
 */
@MojoTest
class UpToDatePropertyTest {

    @Inject
    private MavenProject project;

    @Test
    @Basedir("target/uptodate/uptodate-flatten")
    @InjectMojo(goal = "uptodate-property", pom = "classpath:/uptodate/uptodate-flatten.xml")
    void flatten(UpToDatePropertyMojo mojo) throws Exception {
        fixture("uptodate-flatten");

        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-flatten"));
    }

    @Test
    @Basedir("target/uptodate/uptodate-glob")
    @InjectMojo(goal = "uptodate-property", pom = "classpath:/uptodate/uptodate-glob.xml")
    void glob(UpToDatePropertyMojo mojo) throws Exception {
        fixture("uptodate-glob");

        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-glob"));
    }

    @Test
    @Basedir("target/uptodate/uptodate-identity")
    @InjectMojo(goal = "uptodate-property", pom = "classpath:/uptodate/uptodate-identity.xml")
    void identity(UpToDatePropertyMojo mojo) throws Exception {
        fixture("uptodate-identity");

        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-identity"));
    }

    @Test
    @Basedir("target/uptodate/uptodate-merge")
    @InjectMojo(goal = "uptodate-property", pom = "classpath:/uptodate/uptodate-merge.xml")
    void merge(UpToDatePropertyMojo mojo) throws Exception {
        fixture("uptodate-merge");

        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-merge"));
    }

    @Test
    @Basedir("target/uptodate/uptodate-multi")
    @InjectMojo(goal = "uptodate-properties", pom = "classpath:/uptodate/uptodate-multi.xml")
    void multi(UpToDatePropertiesMojo mojo) throws Exception {
        fixture("uptodate-multi");

        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-flatten"));
        assertEquals("uptodate", project.getProperties().getProperty("propname-glob"));
        assertEquals("uptodate", project.getProperties().getProperty("propname-identity"));
        assertEquals("uptodate", project.getProperties().getProperty("propname-merge"));
        assertEquals("uptodate", project.getProperties().getProperty("propname-package"));
        assertEquals("uptodate", project.getProperties().getProperty("propname-unpackage"));
    }

    @Test
    @Basedir("target/uptodate/uptodate-multi-negative")
    @InjectMojo(goal = "uptodate-properties", pom = "classpath:/uptodate/uptodate-multi-negative.xml")
    void multiNegative(UpToDatePropertiesMojo mojo) throws Exception {
        fixture("uptodate-multi-negative");

        mojo.execute();

        assertEquals("outofdate", project.getProperties().getProperty("propname-flatten"));
        assertEquals("outofdate", project.getProperties().getProperty("propname-glob"));
        assertEquals("outofdate", project.getProperties().getProperty("propname-identity"));
        assertEquals("outofdate", project.getProperties().getProperty("propname-merge"));
        assertEquals("outofdate", project.getProperties().getProperty("propname-package"));
        assertEquals("outofdate", project.getProperties().getProperty("propname-unpackage"));
    }

    @Test
    @Basedir("target/uptodate/uptodate-package")
    @InjectMojo(goal = "uptodate-property", pom = "classpath:/uptodate/uptodate-package.xml")
    void packageMapper(UpToDatePropertyMojo mojo) throws Exception {
        fixture("uptodate-package");

        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-package"));
    }

    @Test
    @Basedir("target/uptodate/uptodate-unpackage")
    @InjectMojo(goal = "uptodate-property", pom = "classpath:/uptodate/uptodate-unpackage.xml")
    void unpackage(UpToDatePropertyMojo mojo) throws Exception {
        fixture("uptodate-unpackage");

        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-unpackage"));
    }

    private void fixture(String name) throws Exception {
        File basedir = project.getBasedir();
        FileUtils.deleteDirectory(basedir);
        FixtureUtils.createResources(
                basedir,
                new File("src/test/resources/uptodate/" + name + ".properties"),
                "UTF-8",
                System.currentTimeMillis());
    }
}
