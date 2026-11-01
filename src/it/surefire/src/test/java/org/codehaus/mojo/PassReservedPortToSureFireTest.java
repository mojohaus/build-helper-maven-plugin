package org.codehaus.mojo;

import java.io.File;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PassReservedPortToSureFireTest
{
    @Test
    public void testCheckSystemProperties()
        throws Exception
    {
        String port = System.getProperty( "port1" );
        Integer.parseInt( port );
    }

    @Test
    public void testCheckEnvironment()
        throws Exception
    {
        String port = System.getenv( "port1" );
        Integer.parseInt( port );
    }

    @Test
    public void testCheckEnvironmentUsingMavenProject()
        throws Exception
    {
        String env = System.getenv( "targetDir" );
        
        String expectEnv = new File( System.getProperty( "basedir", "." ), "target" ).getAbsolutePath();
        
        assertEquals( expectEnv, env );
    }

}
