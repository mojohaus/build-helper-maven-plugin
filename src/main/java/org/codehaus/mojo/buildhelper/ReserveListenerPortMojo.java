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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.api.Project;
import org.apache.maven.api.Session;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.plugin.MojoException;
import org.apache.maven.api.plugin.annotations.Mojo;
import org.apache.maven.api.plugin.annotations.Parameter;
import org.apache.maven.api.services.ProjectManager;
import org.codehaus.plexus.util.io.CachingOutputStream;

/**
 * Reserve a list of random and not in use network ports and place them in a configurable project properties.
 *
 * @since 1.2
 */
@Mojo(name = "reserve-network-port", defaultPhase = "process-test-classes")
public class ReserveListenerPortMojo extends AbstractMojo {
    private static final String BUILD_HELPER_RESERVED_PORTS = "BUILD_HELPER_MIN_PORT";

    private static final int FIRST_NON_ROOT_PORT_NUMBER = 1024;

    private static final Integer MAX_PORT_NUMBER = 65535;

    private static final Object LOCK = new Object();

    /**
     * A List of property names to be placed in Maven project. At least one of {@code #urls} or {@code #portNames} has
     * to be specified.
     *
     * @since 1.2
     */
    @Parameter
    private List<String> portNames = List.of();

    /**
     * A List of urls to resource where list of name could be found. Can be in form of classpath:com/myproject/names.txt
     * . At least one of {@code #urls} or {@code #portNames} has to be specified.
     *
     * @since 1.11
     */
    @Parameter
    private List<String> urls = List.of();

    /**
     * Output file to write the generated properties to. if not given, they are written to Maven project
     *
     * @since 1.2
     */
    @Parameter
    private Path outputFile;

    /**
     * Specify this if you want the port be chosen with a number higher than that one.
     * <p>
     * If {@link #maxPortNumber} is specified, defaults to {@value #FIRST_NON_ROOT_PORT_NUMBER}.
     * </p>
     *
     * @since 1.8
     */
    @Parameter
    private Integer minPortNumber;

    /**
     * Specify this if you want the port be chosen with a number lower than that one.
     *
     * @since 1.8
     */
    @Parameter
    private Integer maxPortNumber;

    /**
     * Specify true or false if you want the port selection randomized.
     *
     * @since 1.10
     */
    @Parameter
    private boolean randomPort;

    @Inject
    private Session session;

    @Inject
    private Project project;

    @Override
    public void execute() throws MojoException {
        Properties properties = new Properties();

        loadUrls();

        // Reserve the entire block of ports to guarantee we don't get the same port twice
        final List<ServerSocket> sockets = new ArrayList<ServerSocket>();
        try {
            for (String portName : portNames) {
                try {
                    final ServerSocket socket = getServerSocket();
                    sockets.add(socket);
                    final String unusedPort = Integer.toString(socket.getLocalPort());
                    ProjectManager projectManager = session.getService(ProjectManager.class);
                    projectManager.setProperty(project, portName, unusedPort);
                    properties.put(portName, unusedPort);
                    getReservedPorts().add(socket.getLocalPort());
                    this.getLog().info("Reserved port " + unusedPort + " for " + portName);
                } catch (IOException e) {
                    throw new MojoException("Error getting an available port from system", e);
                }
            }

            // Write the file -- still hold onto the ports
            if (outputFile != null) {
                createOutputDirectoryIfNotExist(outputFile);
                try (OutputStream os = new CachingOutputStream(outputFile)) {
                    properties.store(os, null);
                } catch (Exception e) {
                    throw new MojoException(e);
                }
            }
        } finally {
            // Now free all the ports
            for (ServerSocket socket : sockets) {
                final int localPort = socket.getLocalPort();
                try {
                    socket.close();
                } catch (IOException e) {
                    this.getLog().error("Cannot free reserved port " + localPort);
                }
            }
        }
    }

    private void createOutputDirectoryIfNotExist(Path outputFile) {
        Path parentDirectory = getCanonicalPath(outputFile.toAbsolutePath()).getParent();

        if (!Files.exists(parentDirectory)) {
            getLog().debug("Trying to create directories: " + parentDirectory);
            try {
                Files.createDirectories(parentDirectory);
            } catch (IOException e) {
                getLog().error("Failed to create folders " + parentDirectory);
            }
        }
    }

    private static Path getCanonicalPath(Path path) {
        try {
            return path.toRealPath();
        } catch (IOException e) {
            return getCanonicalPath(path.getParent()).resolve(path.getFileName());
        }
    }

    private ServerSocket getServerSocket() throws IOException, MojoException {
        if (minPortNumber == null && maxPortNumber != null) {
            getLog().debug("minPortNumber unspecified: using default value " + FIRST_NON_ROOT_PORT_NUMBER);
            minPortNumber = FIRST_NON_ROOT_PORT_NUMBER;
        }
        if (minPortNumber != null && maxPortNumber == null) {
            getLog().debug("maxPortNumber unspecified: using default value " + MAX_PORT_NUMBER);
            maxPortNumber = MAX_PORT_NUMBER;
        }
        if (minPortNumber == null && maxPortNumber == null) {
            return new ServerSocket(0);
        }
        if (randomPort) {
            synchronized (LOCK) {
                List<Integer> availablePorts = randomPortList();
                for (Iterator<Integer> iterator = availablePorts.iterator(); iterator.hasNext(); ) {
                    int port = iterator.next();
                    ServerSocket serverSocket = reservePort(port);
                    iterator.remove();
                    if (serverSocket != null) {
                        return serverSocket;
                    }
                }
                throw new MojoException(
                        "Unable to find an available port between " + minPortNumber + " and " + maxPortNumber);
            }
        } else {
            // Might be synchronizing a bit too largely, but at least that defensive approach should prevent
            // threading issues (essentially possible while put/getting the plugin ctx to get the reserved ports).
            synchronized (LOCK) {
                int min = getNextPortNumber();
                for (int port = min; ; ++port) {
                    ServerSocket serverSocket = reservePort(port);
                    if (serverSocket != null) {
                        return serverSocket;
                    }
                }
            }
        }
    }

    private List<Integer> randomPortList() {
        int difference = maxPortNumber - minPortNumber + 1;
        List<Integer> portList = new ArrayList<Integer>(difference);
        List<Integer> reservedPorts = getReservedPorts();
        for (int i = 0; i < difference; i++) {
            int port = minPortNumber + i;
            if (!reservedPorts.contains(port)) {
                portList.add(minPortNumber + i);
            }
        }
        Collections.shuffle(portList);
        return portList;
    }

    public ServerSocket reservePort(int port) throws MojoException {
        if (port > maxPortNumber) {
            throw new MojoException(
                    "Unable to find an available port between " + minPortNumber + " and " + maxPortNumber);
        }
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            getLog().info("Port assigned " + port);
            return serverSocket;
        } catch (IOException ioe) {
            getLog().info("Tried binding to port " + port + " without success. Trying next port.", ioe);
        }
        return null;
    }

    private int getNextPortNumber() {
        assert minPortNumber != null;

        List<Integer> reservedPorts = getReservedPorts();
        int nextPort = -1;
        if (reservedPorts.isEmpty()) {
            nextPort = minPortNumber;
        } else {
            nextPort = findAvailablePortNumber(minPortNumber, reservedPorts);
        }
        reservedPorts.add(nextPort);
        getLog().debug("Next port: " + nextPort);
        return nextPort;
    }

    @SuppressWarnings("unchecked")
    private List<Integer> getReservedPorts() {
        Map<String, Object> pluginContext = session.getPluginContext(project);
        List<Integer> reservedPorts = (List<Integer>) pluginContext.get(BUILD_HELPER_RESERVED_PORTS);
        if (reservedPorts == null) {
            reservedPorts = new ArrayList<>();
            pluginContext.put(BUILD_HELPER_RESERVED_PORTS, reservedPorts);
        }
        return reservedPorts;
    }

    /**
     * Returns the first number available, starting at portNumberStartingPoint that's not already in the reservedPorts
     * list.
     *
     * @param portNumberStartingPoint first port number to start from.
     * @param reservedPorts the ports already reserved.
     * @return first number available not in the given list, starting at the given parameter.
     */
    private int findAvailablePortNumber(Integer portNumberStartingPoint, List<Integer> reservedPorts) {
        assert portNumberStartingPoint != null;
        int candidate = portNumberStartingPoint;
        while (reservedPorts.contains(candidate)) {
            candidate++;
        }
        return candidate;
    }

    private void loadUrls() throws MojoException {
        List<String> names = new ArrayList<>(this.portNames);
        for (String url : urls) {
            names.addAll(load(new UrlResource(url)));
        }
        this.portNames = names;
    }

    private List<String> load(UrlResource resource) throws MojoException {
        if (resource.canBeOpened()) {
            return loadPortNamesFromResource(resource);
        } else {
            throw new MojoException("Port names could not be loaded from \"" + resource + "\"");
        }
    }

    private List<String> loadPortNamesFromResource(UrlResource resource) throws MojoException {
        if (getLog().isDebugEnabled()) {
            getLog().debug("Loading port names from " + resource);
        }
        try (InputStream stream = resource.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            List<String> names = reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .toList();
            if (getLog().isDebugEnabled()) {
                getLog().debug("Loaded port names " + names);
            }
            return names;
        } catch (IOException e) {
            throw new MojoException("Error reading port names from \"" + resource + "\"", e);
        }
    }

    private class UrlResource {
        private static final String CLASSPATH_PREFIX = "classpath:";

        private static final String SLASH_PREFIX = "/";

        private final URL url;

        private boolean isMissingClasspathResouce = false;

        private String classpathUrl;

        private InputStream stream;

        UrlResource(String url) throws MojoException {
            if (url.startsWith(CLASSPATH_PREFIX)) {
                String resource = url.substring(CLASSPATH_PREFIX.length());
                if (resource.startsWith(SLASH_PREFIX)) {
                    resource = resource.substring(1);
                }
                this.url = getClass().getClassLoader().getResource(resource);
                if (this.url == null) {
                    if (getLog().isDebugEnabled()) {
                        getLog().debug("Can not load classpath resouce \"" + url + "\"");
                    }
                    isMissingClasspathResouce = true;
                    classpathUrl = url;
                }
            } else {
                try {
                    this.url = new URL(url);
                } catch (MalformedURLException e) {
                    throw new MojoException("Badly formed URL " + url + " - " + e.getMessage());
                }
            }
        }

        public InputStream getInputStream() throws IOException {
            if (stream == null) {
                stream = openStream();
            }
            return stream;
        }

        public boolean canBeOpened() {
            if (isMissingClasspathResouce) {
                return false;
            }
            try {
                openStream().close();
            } catch (IOException e) {
                return false;
            }
            return true;
        }

        private InputStream openStream() throws IOException {
            return new BufferedInputStream(url.openStream());
        }

        @Override
        public String toString() {
            if (!isMissingClasspathResouce) {
                return "URL " + url.toString();
            }
            return classpathUrl;
        }
    }
}
