import java.io.*;
import java.util.*;

try
{
    File file = new File( basedir, "target/classes/test.properties" );
    Properties p = new Properties();
    p.load( new FileInputStream( file ) );
    String value = p.getProperty( "version" );

    if ( value.indexOf( "${timestamp}" ) >= 0 )
    {
        System.err.println( "Timestamp not set" );
        return false;
    }
}
catch( Throwable t )
{
    t.printStackTrace();
    return false;
}

return true;
