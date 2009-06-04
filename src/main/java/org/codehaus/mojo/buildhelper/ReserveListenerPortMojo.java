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
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;

/**
 * Reserve a list of random and not in used network ports and place them
 * in a configurable project properties.
 *
 * @goal reserve-network-port
 * @phase process-test-classes
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id: ReserveListnerPortMojo.java 6754 2008-04-13 15:14:04Z dantran $
 * @since 1.2
 */
public class ReserveListenerPortMojo
    extends AbstractMojo
{

    /**
     * A List to property names to be placed in maven project
     * @parameter
     * @required
     * @since 1.2
     *
     */
    private String [] portNames = new String[0];
    
    /**
     * Output file to write the generated properties to.
     * if not given, they are written to maven project
     * @parameter
     * @since 1.2
     */
    private File outputFile;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @since 1.2
     */
    private MavenProject project;


    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        Properties properties = project.getProperties();
        
        if ( outputFile != null )
        {
            properties = new Properties();
        }
        
        for ( int i = 0 ; i < portNames.length; ++i )
        {
            String unusedPort = Integer.toString( getNextAvailablePort()  );
            properties.put( portNames[i], unusedPort );
            this.getLog().info( "Reserved port " + unusedPort + " for " + portNames[i] );
        }
        
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

    private int getNextAvailablePort()
        throws MojoExecutionException
    {
        int unusedPort = 0;
        try
        {
            ServerSocket socket = new ServerSocket( 0 );
            unusedPort = socket.getLocalPort();
            socket.close();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error getting an available port from system", e );
        }

        return unusedPort;
    }

}
