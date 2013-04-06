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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
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

    private static final Integer MAX_PORT_NUMBER = 65536;

    private static final Object lock = new Object();

    /**
     * A List to property names to be placed in Maven project
     * 
     * @since 1.2
     */
    @Parameter( required = true )
    private String[] portNames = new String[0];

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
     * @since 1.2
     */
    @Component
    private MavenProject project;

    public void execute()
        throws MojoExecutionException
    {
        Properties properties = project.getProperties();

        if ( outputFile != null )
        {
            properties = new Properties();
        }

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
        else
        {
            // Might be synchronizing a bit too largely, but at least that defensive approach should prevent
            // threading issues (essentially possible while put/getting the plugin ctx to get the reserved ports).
            synchronized ( lock )
            {
                int min = getNextPortNumber();
                for ( int port = min;; ++port )
                {
                    if ( port > maxPortNumber )
                    {
                        throw new MojoExecutionException( "Unable to find an available port between " + minPortNumber
                            + " and " + maxPortNumber );
                    }
                    try
                    {
                        ServerSocket serverSocket = new ServerSocket( port );
                        return serverSocket;
                    }
                    catch ( IOException ioe )
                    {
                        getLog().debug( "Tried binding to port " + port + " without success. Trying next port.", ioe );
                    }
                }
            }
        }
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
}
