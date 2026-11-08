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
import java.util.Arrays;

import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The uptodate-property and uptodate-properties goals on the file trees in {@code src/test/resources/uptodate}, built
 * by {@link FixtureUtils} with the same relative timestamps the integration tests used.
 */
class UpToDatePropertyTest {

    @TempDir
    File tempDir;

    private MavenProject project;

    @BeforeEach
    void setUp() {
        project = new MavenProject();
    }

    /** Was the uptodate-flatten IT. */
    @Test
    void flatten() throws Exception {
        File base = fixture("uptodate-flatten");
        UpToDatePropertySetting setting = setting(
                "propname-flatten",
                "uptodate",
                "outofdate",
                fileSet(
                        base,
                        "src/main/resources/source",
                        "src/main/resources/target",
                        new String[] {"dir*/**"},
                        new String[] {"**/*X*", "**/*Y/**"},
                        mapper("flatten", null, null)));

        UpToDatePropertyMojo mojo = new UpToDatePropertyMojo();
        set(mojo, "project", project);
        set(mojo, "name", setting.getName());
        set(mojo, "value", setting.getValue());
        set(mojo, "elseValue", setting.getElse());
        set(mojo, "fileSet", setting.getFileSet());
        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-flatten"));
    }

    /** Was the uptodate-glob IT. */
    @Test
    void glob() throws Exception {
        File base = fixture("uptodate-glob");
        UpToDatePropertySetting setting = setting(
                "propname-glob",
                "uptodate",
                "outofdate",
                fileSet(
                        base,
                        "src/main/resources/source",
                        "src/main/resources/target",
                        new String[] {"dir*/**"},
                        new String[] {"**/*X*", "**/*Y/**"},
                        mapper("glob", "*.s", "*.t")));

        UpToDatePropertyMojo mojo = new UpToDatePropertyMojo();
        set(mojo, "project", project);
        set(mojo, "name", setting.getName());
        set(mojo, "value", setting.getValue());
        set(mojo, "elseValue", setting.getElse());
        set(mojo, "fileSet", setting.getFileSet());
        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-glob"));
    }

    /** Was the uptodate-identity IT. */
    @Test
    void identity() throws Exception {
        File base = fixture("uptodate-identity");
        UpToDatePropertySetting setting = setting(
                "propname-identity",
                "uptodate",
                "outofdate",
                fileSet(
                        base,
                        "src/main/resources/source",
                        "src/main/resources/target",
                        new String[] {"dir*/**"},
                        new String[] {"**/*X*", "**/*Y/**"},
                        null));

        UpToDatePropertyMojo mojo = new UpToDatePropertyMojo();
        set(mojo, "project", project);
        set(mojo, "name", setting.getName());
        set(mojo, "value", setting.getValue());
        set(mojo, "elseValue", setting.getElse());
        set(mojo, "fileSet", setting.getFileSet());
        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-identity"));
    }

    /** Was the uptodate-merge IT. */
    @Test
    void merge() throws Exception {
        File base = fixture("uptodate-merge");
        UpToDatePropertySetting setting = setting(
                "propname-merge",
                "uptodate",
                "outofdate",
                fileSet(
                        base,
                        "src/main/resources/source",
                        "src/main/resources/target",
                        new String[] {"dir*/**"},
                        new String[] {"**/*X*", "**/*Y/**"},
                        mapper("merge", "file*.s", "todir/outfile.t")));

        UpToDatePropertyMojo mojo = new UpToDatePropertyMojo();
        set(mojo, "project", project);
        set(mojo, "name", setting.getName());
        set(mojo, "value", setting.getValue());
        set(mojo, "elseValue", setting.getElse());
        set(mojo, "fileSet", setting.getFileSet());
        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-merge"));
    }

    /** Was the uptodate-multi IT. */
    @Test
    void multi() throws Exception {
        File base = fixture("uptodate-multi");
        UpToDatePropertiesMojo mojo = new UpToDatePropertiesMojo();
        set(mojo, "project", project);
        set(
                mojo,
                "upToDatePropertySettings",
                Arrays.asList(
                        setting(
                                "propname-flatten",
                                "uptodate",
                                "outofdate",
                                fileSet(
                                        base,
                                        "src/main/resources/source",
                                        "src/main/resources/target/flatten",
                                        new String[] {"dir*/**"},
                                        new String[] {"**/*X*", "**/*Y/**"},
                                        mapper("flatten", null, null))),
                        setting(
                                "propname-glob",
                                "uptodate",
                                "outofdate",
                                fileSet(
                                        base,
                                        "src/main/resources/source",
                                        "src/main/resources/target/glob",
                                        new String[] {"dir*/**"},
                                        new String[] {"**/*X*", "**/*Y/**"},
                                        mapper("glob", "*.s", "*.t"))),
                        setting(
                                "propname-identity",
                                "uptodate",
                                "outofdate",
                                fileSet(
                                        base,
                                        "src/main/resources/source",
                                        "src/main/resources/target/identity",
                                        new String[] {"dir*/**"},
                                        new String[] {"**/*X*", "**/*Y/**"},
                                        null)),
                        setting(
                                "propname-merge",
                                "uptodate",
                                "outofdate",
                                fileSet(
                                        base,
                                        "src/main/resources/source",
                                        "src/main/resources/target/merge",
                                        new String[] {"dir*/**"},
                                        new String[] {"**/*X*", "**/*Y/**"},
                                        mapper("merge", "file*.s", "todir/outfile.t"))),
                        setting(
                                "propname-package",
                                "uptodate",
                                "outofdate",
                                fileSet(
                                        base,
                                        "src/main/resources/source",
                                        "src/main/resources/target/package",
                                        new String[] {"dir*/**"},
                                        new String[] {"**/*X*", "**/*Y/**"},
                                        mapper("package", "*.s", "*.t"))),
                        setting(
                                "propname-unpackage",
                                "uptodate",
                                "outofdate",
                                fileSet(
                                        base,
                                        "src/main/resources/source/unpackage",
                                        "src/main/resources/target/unpackage",
                                        new String[] {"dir*.s"},
                                        new String[] {"*X*", "*Y*"},
                                        mapper("unpackage", "*.s", "*.t")))));
        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-flatten"));
        assertEquals("uptodate", project.getProperties().getProperty("propname-glob"));
        assertEquals("uptodate", project.getProperties().getProperty("propname-identity"));
        assertEquals("uptodate", project.getProperties().getProperty("propname-merge"));
        assertEquals("uptodate", project.getProperties().getProperty("propname-package"));
        assertEquals("uptodate", project.getProperties().getProperty("propname-unpackage"));
    }

    /** Was the uptodate-multi-negative IT. */
    @Test
    void multiNegative() throws Exception {
        File base = fixture("uptodate-multi-negative");
        UpToDatePropertiesMojo mojo = new UpToDatePropertiesMojo();
        set(mojo, "project", project);
        set(
                mojo,
                "upToDatePropertySettings",
                Arrays.asList(
                        setting(
                                "propname-flatten",
                                "uptodate",
                                "outofdate",
                                fileSet(
                                        base,
                                        "src/main/resources/source",
                                        "src/main/resources/target/flatten",
                                        new String[] {"dir*/**"},
                                        new String[] {"**/*X*", "**/*Y/**"},
                                        mapper("flatten", null, null))),
                        setting(
                                "propname-glob",
                                "uptodate",
                                "outofdate",
                                fileSet(
                                        base,
                                        "src/main/resources/source",
                                        "src/main/resources/target/glob",
                                        new String[] {"dir*/**"},
                                        new String[] {"**/*X*", "**/*Y/**"},
                                        mapper("glob", "*.s", "*.t"))),
                        setting(
                                "propname-identity",
                                "uptodate",
                                "outofdate",
                                fileSet(
                                        base,
                                        "src/main/resources/source",
                                        "src/main/resources/target/identity",
                                        new String[] {"dir*/**"},
                                        new String[] {"**/*X*", "**/*Y/**"},
                                        null)),
                        setting(
                                "propname-merge",
                                "uptodate",
                                "outofdate",
                                fileSet(
                                        base,
                                        "src/main/resources/source",
                                        "src/main/resources/target/merge",
                                        new String[] {"dir*/**"},
                                        new String[] {"**/*X*", "**/*Y/**"},
                                        mapper("merge", "file*.s", "todir/outfile.t"))),
                        setting(
                                "propname-package",
                                "uptodate",
                                "outofdate",
                                fileSet(
                                        base,
                                        "src/main/resources/source",
                                        "src/main/resources/target/package",
                                        new String[] {"dir*/**"},
                                        new String[] {"**/*X*", "**/*Y/**"},
                                        mapper("package", "*.s", "*.t"))),
                        setting(
                                "propname-unpackage",
                                "uptodate",
                                "outofdate",
                                fileSet(
                                        base,
                                        "src/main/resources/source/unpackage",
                                        "src/main/resources/target/unpackage",
                                        new String[] {"dir*.s"},
                                        new String[] {"*X*", "*Y*"},
                                        mapper("unpackage", "*.s", "*.t")))));
        mojo.execute();

        assertEquals("outofdate", project.getProperties().getProperty("propname-flatten"));
        assertEquals("outofdate", project.getProperties().getProperty("propname-glob"));
        assertEquals("outofdate", project.getProperties().getProperty("propname-identity"));
        assertEquals("outofdate", project.getProperties().getProperty("propname-merge"));
        assertEquals("outofdate", project.getProperties().getProperty("propname-package"));
        assertEquals("outofdate", project.getProperties().getProperty("propname-unpackage"));
    }

    /** Was the uptodate-package IT. */
    @Test
    void packageMapper() throws Exception {
        File base = fixture("uptodate-package");
        UpToDatePropertySetting setting = setting(
                "propname-package",
                "uptodate",
                "outofdate",
                fileSet(
                        base,
                        "src/main/resources/source",
                        "src/main/resources/target",
                        new String[] {"dir*/**"},
                        new String[] {"**/*X*", "**/*Y/**"},
                        mapper("package", "*.s", "*.t")));

        UpToDatePropertyMojo mojo = new UpToDatePropertyMojo();
        set(mojo, "project", project);
        set(mojo, "name", setting.getName());
        set(mojo, "value", setting.getValue());
        set(mojo, "elseValue", setting.getElse());
        set(mojo, "fileSet", setting.getFileSet());
        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-package"));
    }

    /** Was the uptodate-unpackage IT. */
    @Test
    void unpackage() throws Exception {
        File base = fixture("uptodate-unpackage");
        UpToDatePropertySetting setting = setting(
                "propname-unpackage",
                "uptodate",
                "outofdate",
                fileSet(
                        base,
                        "src/main/resources/source",
                        "src/main/resources/target",
                        new String[] {"dir*.s"},
                        new String[] {"*X*", "*Y*"},
                        mapper("unpackage", "*.s", "*.t")));

        UpToDatePropertyMojo mojo = new UpToDatePropertyMojo();
        set(mojo, "project", project);
        set(mojo, "name", setting.getName());
        set(mojo, "value", setting.getValue());
        set(mojo, "elseValue", setting.getElse());
        set(mojo, "fileSet", setting.getFileSet());
        mojo.execute();

        assertEquals("uptodate", project.getProperties().getProperty("propname-unpackage"));
    }

    private File fixture(String name) throws Exception {
        File base = new File(tempDir, name);
        FixtureUtils.createResources(
                base,
                new File("src/test/resources/uptodate/" + name + ".properties"),
                "UTF-8",
                System.currentTimeMillis());
        return base;
    }

    private static UpToDatePropertySetting setting(String name, String value, String elseValue, FileSet fileSet) {
        UpToDatePropertySetting setting = new UpToDatePropertySetting();
        setting.setName(name);
        setting.setValue(value);
        setting.setElse(elseValue);
        setting.setFileSet(fileSet);
        return setting;
    }

    private static FileSet fileSet(
            File base, String directory, String outputDirectory, String[] includes, String[] excludes, Mapper mapper) {
        FileSet fileSet = new FileSet();
        fileSet.setDirectory(new File(base, directory).getPath());
        fileSet.setOutputDirectory(new File(base, outputDirectory).getPath());
        for (String include : includes) {
            fileSet.addInclude(include);
        }
        for (String exclude : excludes) {
            fileSet.addExclude(exclude);
        }
        if (mapper != null) {
            // without one, FileSet keeps the identity mapper it starts with, as when the POM has no <mapper>
            fileSet.setMapper(mapper);
        }
        return fileSet;
    }

    private static Mapper mapper(String type, String from, String to) {
        Mapper mapper = new Mapper();
        mapper.setType(type);
        mapper.setFrom(from);
        mapper.setTo(to);
        return mapper;
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
}
