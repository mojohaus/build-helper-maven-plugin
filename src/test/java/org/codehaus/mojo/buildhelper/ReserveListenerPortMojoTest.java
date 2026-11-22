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
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoParameter;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The {@code reserve-network-port} goal. A port another process, or a test running in parallel, holds is skipped, so
 * the tests assert ranges, order and uniqueness and never a fixed port.
 */
@MojoTest
class ReserveListenerPortMojoTest {

    private static final String NAMES = "classpath:/reserve-port/names.xml";

    private static final String URLS = "classpath:/reserve-port/urls.xml";

    @Inject
    private MavenProject project;

    @TempDir
    private File tempDir;

    @Test
    @InjectMojo(goal = "reserve-network-port", pom = NAMES)
    void reservesADistinctPortForEveryName(ReserveListenerPortMojo mojo) throws Exception {
        mojo.setPluginContext(new HashMap<>());

        mojo.execute();

        List<Integer> ports = ports(project.getProperties(), "port1", "port2", "port3");
        assertEquals(3, new HashSet<>(ports).size(), "ports " + ports);
        ports.forEach(port -> assertTrue(port > 0 && port <= 65535, "port " + port));
    }

    @Test
    @InjectMojo(goal = "reserve-network-port", pom = NAMES)
    @MojoParameter(name = "minPortNumber", value = "9000")
    @MojoParameter(name = "maxPortNumber", value = "9005")
    @MojoParameter(name = "randomPort", value = "false")
    void reservesPortsInSequenceFromTheMinimum(ReserveListenerPortMojo mojo) throws Exception {
        mojo.setPluginContext(new HashMap<>());

        mojo.execute();

        assertAscendingWithin(ports(project.getProperties(), "port1", "port2", "port3"), 9000, 9005);
    }

    @Test
    @InjectMojo(goal = "reserve-network-port", pom = NAMES)
    @MojoParameter(name = "minPortNumber", value = "9000")
    @MojoParameter(name = "maxPortNumber", value = "9005")
    @MojoParameter(name = "randomPort", value = "true")
    void reservesRandomPortsWithinTheRange(ReserveListenerPortMojo mojo) throws Exception {
        mojo.setPluginContext(new HashMap<>());

        mojo.execute();

        List<Integer> ports = ports(project.getProperties(), "port1", "port2", "port3");
        assertEquals(3, new HashSet<>(ports).size(), "ports " + ports);
        ports.forEach(port -> assertTrue(port >= 9000 && port <= 9005, "port " + port));
    }

    @Test
    @InjectMojo(goal = "reserve-network-port", pom = NAMES)
    @MojoParameter(name = "minPortNumber", value = "30000")
    void reservesPortsAboveTheMinimumAndNeverTwiceAcrossExecutions(ReserveListenerPortMojo mojo) throws Exception {
        // executions of one build share the plugin context
        mojo.setPluginContext(new HashMap<>());
        mojo.execute();
        set(mojo, "portNames", new String[] {"port4", "port5", "port6"});
        mojo.execute();
        set(mojo, "portNames", new String[] {"port7", "port8", "port9"});
        set(mojo, "minPortNumber", 40000);
        mojo.execute();

        Properties properties = project.getProperties();
        List<Integer> first = ports(properties, "port1", "port2", "port3", "port4", "port5", "port6");
        List<Integer> second = ports(properties, "port7", "port8", "port9");
        assertAscendingWithin(first, 30000, 65535);
        assertAscendingWithin(second, 40000, 65535);
    }

    @Test
    @InjectMojo(goal = "reserve-network-port", pom = NAMES)
    @MojoParameter(name = "maxPortNumber", value = "10000")
    void reservesPortsFromTheFirstNonRootPortWhenOnlyAMaximumIsGiven(ReserveListenerPortMojo mojo) throws Exception {
        mojo.setPluginContext(new HashMap<>());

        mojo.execute();

        assertAscendingWithin(ports(project.getProperties(), "port1", "port2", "port3"), 1024, 10000);
    }

    @Test
    @InjectMojo(goal = "reserve-network-port", pom = NAMES)
    void writesThePortsToTheOutputFileInsteadOfTheProject(ReserveListenerPortMojo mojo) throws Exception {
        File outputFile = new File(tempDir, "target/ports.properties");
        set(mojo, "outputFile", outputFile);
        mojo.setPluginContext(new HashMap<>());

        mojo.execute();

        Properties written = new Properties();
        try (InputStream input = Files.newInputStream(outputFile.toPath())) {
            written.load(input);
        }
        List<Integer> ports = ports(written, "port1", "port2", "port3");
        assertEquals(3, new HashSet<>(ports).size(), "ports " + ports);
        assertNull(project.getProperties().getProperty("port1"));
    }

    @Test
    @InjectMojo(goal = "reserve-network-port", pom = URLS)
    void readsThePortNamesFromTheUrls(ReserveListenerPortMojo mojo) throws Exception {
        mojo.setPluginContext(new HashMap<>());

        mojo.execute();

        List<Integer> ports = ports(project.getProperties(), "port1", "port2", "port3", "port4", "port5", "port6");
        assertEquals(6, new HashSet<>(ports).size(), "ports " + ports);
    }

    @Test
    @InjectMojo(goal = "reserve-network-port", pom = URLS)
    void readsThePortNamesFromFileUrlsAndTheConfiguredNames(ReserveListenerPortMojo mojo) throws Exception {
        File names = new File(tempDir, "names.txt");
        Files.write(names.toPath(), "# Port names\nfromFile\n".getBytes("UTF-8"));
        set(mojo, "urls", new String[] {names.toURI().toString()});
        set(mojo, "portNames", new String[] {"configured"});
        mojo.setPluginContext(new HashMap<>());

        mojo.execute();

        List<Integer> ports = ports(project.getProperties(), "fromFile", "configured");
        assertEquals(2, new HashSet<>(ports).size(), "ports " + ports);
        assertFalse(project.getProperties().containsKey("# Port names"));
        assertNull(project.getProperties().getProperty("port1"));
    }

    private static List<Integer> ports(Properties properties, String... names) {
        return Stream.of(names)
                .map(name -> {
                    String value = properties.getProperty(name);
                    assertTrue(value != null, "no port for " + name + " in " + properties);
                    return Integer.valueOf(value);
                })
                .collect(Collectors.toList());
    }

    private static void assertAscendingWithin(List<Integer> ports, int min, int max) {
        Set<Integer> distinct = new HashSet<>(ports);
        assertEquals(ports.size(), distinct.size(), "ports " + ports);
        for (int i = 0; i < ports.size(); i++) {
            assertTrue(ports.get(i) >= min && ports.get(i) <= max, "port " + ports.get(i) + " in " + ports);
            assertTrue(i == 0 || ports.get(i) > ports.get(i - 1), "ascending " + ports);
        }
    }

    private static void set(ReserveListenerPortMojo mojo, String field, Object value) throws Exception {
        Field f = ReserveListenerPortMojo.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(mojo, value);
    }
}
