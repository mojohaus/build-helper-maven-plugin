package org.codehaus.mojo;

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

}
