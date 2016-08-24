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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;

/**
 * Reserve a list of random and not in use network ports and place them in a configurable project properties.
 *
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id: ReserveListnerPortMojo.java 6754 2008-04-13 15:14:04Z dantran $
 * @since 1.2
 */
@Mojo( name = "reserve-network-port", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES, threadSafe = true )
public class ReserveListenerPortMojo
    extends AbstractMojo
{
    private static final String BUILD_HELPER_RESERVED_PORTS = "BUILD_HELPER_MIN_PORT";

    private static final Integer FIRST_NON_ROOT_PORT_NUMBER = 1024;

    private static final Integer MAX_PORT_NUMBER = 65535;

    private static final Object lock = new Object();

    /**
     * A List to property names to be placed in Maven project. At least one of {@code #urls} or {@code #portNames} has
     * to be specified.
     *
     * @since 1.2
     */
    @Parameter
    private String[] portNames = new String[0];

    /**
     * A List of urls to resource where list of name could be found. Can be in form of classpath:com/myproject/names.txt
     * . At least one of {@code #urls} or {@code #portNames} has to be specified.
     * 
     * @since 1.11
     */
    @Parameter
    private String[] urls = new String[0];

    /**
     * Output file to write the generated properties to. if not given, they are written to Maven project
     *
     * @since 1.2
     */
    @Parameter
    private File outputFile;

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
     * <p>
     * </p>
     *
     * @since 1.10
     */
    @Parameter
    private boolean randomPort;

    /**
     * @since 1.2
     */
    @Parameter( readonly = true, defaultValue = "${project}" )
    private MavenProject project;

    @Override
    public void execute()
        throws MojoExecutionException
    {
        Properties properties = project.getProperties();

        if ( outputFile != null )
        {
            properties = new Properties();
        }

        loadUrls();

        // Reserve the entire block of ports to guarantee we don't get the same port twice
        final List<ServerSocket> sockets = new ArrayList<ServerSocket>();
        try
        {
            for ( String portName : portNames )
            {
                try
                {
                    final ServerSocket socket = getServerSocket();
                    sockets.add( socket );

                    final String unusedPort = Integer.toString( socket.getLocalPort() );
                    properties.put( portName, unusedPort );
                    getReservedPorts().add( socket.getLocalPort() );
                    this.getLog().info( "Reserved port " + unusedPort + " for " + portName );
                }
                catch ( IOException e )
                {
                    throw new MojoExecutionException( "Error getting an available port from system", e );
                }
            }

            // Write the file -- still hold onto the ports
            if ( outputFile != null )
            {
                OutputStream os = null;
                try
                {
                    os = new FileOutputStream( outputFile );
                    properties.store( os, null );
                }
                catch ( Exception e )
                {
                    throw new MojoExecutionException( e.getMessage() );
                }
                finally
                {
                    IOUtil.close( os );
                }
            }
        }
        finally
        {
            // Now free all the ports
            for ( ServerSocket socket : sockets )
            {
                final int localPort = socket.getLocalPort();
                try
                {
                    socket.close();
                }
                catch ( IOException e )
                {
                    this.getLog().error( "Cannot free reserved port " + localPort );
                }
            }
        }
    }

    private ServerSocket getServerSocket()
        throws IOException, MojoExecutionException
    {
        if ( minPortNumber == null && maxPortNumber != null )
        {
            getLog().debug( "minPortNumber unspecified: using default value " + FIRST_NON_ROOT_PORT_NUMBER );
            minPortNumber = FIRST_NON_ROOT_PORT_NUMBER;
        }
        if ( minPortNumber != null && maxPortNumber == null )
        {
            getLog().debug( "maxPortNumber unspecified: using default value " + MAX_PORT_NUMBER );
            maxPortNumber = MAX_PORT_NUMBER;
        }
        if ( minPortNumber == null && maxPortNumber == null )
        {
            return new ServerSocket( 0 );
        }
        if ( randomPort )
        {
            synchronized ( lock )
            {
                List<Integer> availablePorts = randomPortList();
                for ( Iterator<Integer> iterator = availablePorts.iterator(); iterator.hasNext(); )
                {
                    int port = iterator.next();
                    ServerSocket serverSocket = reservePort( port );
                    iterator.remove();
                    if ( serverSocket != null )
                    {
                        return serverSocket;
                    }
                }
                throw new MojoExecutionException( "Unable to find an available port between " + minPortNumber + " and "
                    + maxPortNumber );
            }
        }
        else
        {
            // Might be synchronizing a bit too largely, but at least that defensive approach should prevent
            // threading issues (essentially possible while put/getting the plugin ctx to get the reserved ports).
            synchronized ( lock )
            {
                int min = getNextPortNumber();

                for ( int port = min;; ++port )
                {

                    ServerSocket serverSocket = reservePort( port );
                    if ( serverSocket != null )
                    {
                        return serverSocket;
                    }
                }
            }
        }
    }

    private List<Integer> randomPortList()
    {

        int difference = maxPortNumber - minPortNumber + 1;
        List<Integer> portList = new ArrayList<Integer>( difference );
        List<Integer> reservedPorts = getReservedPorts();
        for ( int i = 0; i < difference; i++ )
        {
            int port = minPortNumber + i;
            if ( !reservedPorts.contains( port ) )
            {
                portList.add( minPortNumber + i );
            }
        }
        Collections.shuffle( portList );
        return portList;
    }

    public ServerSocket reservePort( int port )
        throws MojoExecutionException
    {

        if ( port > maxPortNumber )
        {
            throw new MojoExecutionException( "Unable to find an available port between " + minPortNumber + " and "
                + maxPortNumber );
        }
        try
        {
            ServerSocket serverSocket = new ServerSocket( port );
            getLog().info( "Port assigned" + port );
            return serverSocket;
        }
        catch ( IOException ioe )
        {
            getLog().info( "Tried binding to port " + port + " without success. Trying next port.", ioe );
        }
        return null;
    }

    private int getNextPortNumber()
    {
        assert minPortNumber != null;

        List<Integer> reservedPorts = getReservedPorts();
        int nextPort = -1;
        if ( reservedPorts.isEmpty() )
        {
            nextPort = minPortNumber;
        }
        else
        {
            nextPort = findAvailablePortNumber( minPortNumber, reservedPorts );
        }
        reservedPorts.add( nextPort );
        getLog().debug( "Next port: " + nextPort );
        return nextPort;
    }

    @SuppressWarnings( "unchecked" )
    private List<Integer> getReservedPorts()
    {

        List<Integer> reservedPorts = (List<Integer>) getPluginContext().get( BUILD_HELPER_RESERVED_PORTS );
        if ( reservedPorts == null )
        {
            reservedPorts = new ArrayList<Integer>();
            getPluginContext().put( BUILD_HELPER_RESERVED_PORTS, reservedPorts );
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
    private int findAvailablePortNumber( Integer portNumberStartingPoint, List<Integer> reservedPorts )
    {
        assert portNumberStartingPoint != null;
        int candidate = portNumberStartingPoint;
        while ( reservedPorts.contains( candidate ) )
        {
            candidate++;
        }
        return candidate;
    }

    private void loadUrls()
        throws MojoExecutionException
    {
        for ( String url : urls )
        {
            load( new UrlResource( url ) );
        }
    }

    private void load( UrlResource resource )
        throws MojoExecutionException
    {
        if ( resource.canBeOpened() )
        {
            loadPortNamesFromResource( resource );
        }
        else
        {
            throw new MojoExecutionException( "Port names could not be loaded from \"" + resource + "\"" );
        }
    }

    private void loadPortNamesFromResource( UrlResource resource )
        throws MojoExecutionException
    {
        try
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Loading port names from " + resource );
            }
            final InputStream stream = resource.getInputStream();

            try
            {
                BufferedReader reader = new BufferedReader( new InputStreamReader( stream ) );
                List<String> names = new ArrayList<String>();
                String line;
                while ( ( line = reader.readLine() ) != null )
                {
                    line = line.trim();
                    if ( !line.isEmpty() && !line.replace( " ", "" ).startsWith( "#" ) )
                    {
                        names.add( line );
                    }
                }
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "Loaded port names " + names );
                }
                String[] tPortNames = names.toArray( new String[portNames.length + names.size()] );
                if ( portNames.length > 0 )
                {
                    System.arraycopy( portNames, 0, tPortNames, names.size(), portNames.length );
                }
                portNames = tPortNames;
            }
            finally
            {
                stream.close();
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error reading port names from \"" + resource + "\"", e );
        }
    }

    private class UrlResource
    {
        private static final String CLASSPATH_PREFIX = "classpath:";

        private static final String SLASH_PREFIX = "/";

        private final URL url;

        private boolean isMissingClasspathResouce = false;

        private String classpathUrl;

        private InputStream stream;

        public UrlResource( String url )
            throws MojoExecutionException
        {
            if ( url.startsWith( CLASSPATH_PREFIX ) )
            {
                String resource = url.substring( CLASSPATH_PREFIX.length(), url.length() );
                if ( resource.startsWith( SLASH_PREFIX ) )
                {
                    resource = resource.substring( 1, resource.length() );
                }
                this.url = getClass().getClassLoader().getResource( resource );
                if ( this.url == null )
                {
                    if ( getLog().isDebugEnabled() )
                    {
                        getLog().debug( "Can not load classpath resouce \"" + url + "\"" );
                    }
                    isMissingClasspathResouce = true;
                    classpathUrl = url;
                }
            }
            else
            {
                try
                {
                    this.url = new URL( url );
                }
                catch ( MalformedURLException e )
                {
                    throw new MojoExecutionException( "Badly formed URL " + url + " - " + e.getMessage() );
                }
            }
        }

        public InputStream getInputStream()
            throws IOException
        {
            if ( stream == null )
            {
                stream = openStream();
            }
            return stream;
        }

        public boolean canBeOpened()
        {
            if ( isMissingClasspathResouce )
            {
                return false;
            }
            try
            {
                openStream().close();
            }
            catch ( IOException e )
            {
                return false;
            }
            return true;
        }

        private InputStream openStream()
            throws IOException
        {
            return new BufferedInputStream( url.openStream() );
        }

        @Override
        public String toString()
        {
            if ( !isMissingClasspathResouce )
            {
                return "URL " + url.toString();
            }
            return classpathUrl;
        }
    }
}
