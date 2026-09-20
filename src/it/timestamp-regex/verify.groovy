import java.io.*;
import java.util.*;
import java.util.regex.*;

try
{
    File file = new File( basedir, "target/classes/test.properties" );
    Properties p = new Properties();
    p.load( new FileInputStream( file ) );
    String value = p.getProperty( "version" );

    if ( value.indexOf( "${build.version}" ) >= 0 )
    {
        System.err.println( "Timestamp not set" );
        return false;
    }
    Pattern regex = Pattern.compile( "\\d\\d:\\d\\d \\d\\d-\\d\\d-\\d\\d\\d\\d" );
    if ( !regex.matcher( value ).find() )
    {
        System.err.println( "Formatted timestamp not in property" );
        return false;
    }
}
catch( Throwable t )
{
    t.printStackTrace();
    return false;
}

return true;
