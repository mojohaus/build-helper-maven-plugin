package org.codehaus.mojo;

import java.io.File;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class PassReservedPortToSureFireTest
    extends TestCase
{
    public void testCheckSystemProperties()
    {
        String port = System.getProperty( "port1" );
        assertThat( Integer.parseInt( port ) ).isGreaterThan( 0 );
    }

    public void testCheckEnvironment()
    {
        String port = System.getenv( "port1" );
        assertThat( Integer.parseInt( port ) ).isGreaterThan( 0 );
    }

    public void testCheckEnvironmentUsingMavenProject()
    {
        String env = System.getenv( "targetDir" );
        
        String expectEnv = new File( System.getProperty( "basedir", "." ), "target" ).getAbsolutePath();
        
        assertThat( env ).isEqualTo( expectEnv );
    }

}
