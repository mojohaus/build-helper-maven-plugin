package org.codehaus.mojo;

import java.io.File;

import junit.framework.TestCase;

public class PassReservedPortToSureFireTest
    extends TestCase
{
    public void testCheckSystemProperties()
        throws Exception
    {
        String port = System.getProperty( "port1" );
        Integer.parseInt( port );
    }

    public void testCheckEnvironment()
        throws Exception
    {
        String port = System.getenv( "port1" );
        Integer.parseInt( port );
    }

    public void testCheckEnvironmentUsingMavenProject()
        throws Exception
    {
        String env = System.getenv( "targetDir" );
        
        String expectEnv = new File( System.getProperty( "basedir", "." ), "target" ).getAbsolutePath();
        
        assertEquals( expectEnv, env );
    }

}
