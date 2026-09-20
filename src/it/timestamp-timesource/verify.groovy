import java.io.*;
import java.util.*;

try
{
    File module1File = new File( basedir, "module-1/target/classes/test.properties" );
    File module2File = new File( basedir, "module-2/target/classes/test.properties" );

    Properties module1Props = new Properties();
    module1Props.load( new FileInputStream( module1File ) );
    Properties module2Props = new Properties();
    module2Props.load( new FileInputStream( module2File ) );

    String module1ModuleVersion = module1Props.getProperty( "module.version" );
    String module1SessionVersion = module1Props.getProperty( "session.version" );
    String module2ModuleVersion = module2Props.getProperty( "module.version" );
    String module2SessionVersion = module2Props.getProperty( "session.version" );

    if ( module1ModuleVersion.indexOf( "${module.build.timestamp}" ) >= 0
         || module2ModuleVersion.indexOf( "${module.build.timestamp}" ) >= 0)
    {
        System.err.println( "Module timestamp not set" );
        return false;
    }
    else if ( module1SessionVersion.indexOf( "${session.build.timestamp}" ) >= 0
              || module2SessionVersion.indexOf( "${session.build.timestamp}" ) >= 0)
    {
        System.err.println( "Session timestamp not set" );
        return false;
    }

    if ( module1ModuleVersion.equals( module2ModuleVersion ) )
    {
        System.err.println( "Module timestamps should not match by design" );
        return false;
    }
    else if ( !module1SessionVersion.equals( module2SessionVersion ) )
    {
        System.err.println( "Session timestamps should match by design" );
        return false;
    }
}
catch( Throwable t )
{
    t.printStackTrace();
    return false;
}

return true;
