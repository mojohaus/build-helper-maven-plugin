package org.codehaus.mojo.buildhelper.versioning;

/**
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
public class DefaultVersioning
    implements Versioning
{

    private VersionInformation vi;

    private String version;

    public DefaultVersioning( String version )
    {
        this.version = version;
        this.vi = new VersionInformation( version );

    }

    public String getVersion()
    {
        return this.version;
    }

    @Override
    public int getMajor()
    {
        return this.vi.getMajor();
    }

    @Override
    public int getMinor()
    {
        return this.vi.getMinor();
    }

    @Override
    public int getPatch()
    {
        return this.vi.getPatch();
    }

    @Override
    public String getAsOSGiVersion()
    {
        StringBuffer osgiVersion = new StringBuffer();
        osgiVersion.append( this.getMajor() );
        osgiVersion.append( "." + this.getMinor() );
        osgiVersion.append( "." + this.getPatch() );

        if ( this.getQualifier() != null || this.getBuildNumber() != 0 )
        {
            osgiVersion.append( "." );

            if ( this.getBuildNumber() != 0 )
            {
                osgiVersion.append( this.getBuildNumber() );
            }
            if ( this.getQualifier() != null )
            {
                // Do not allow having "." in it cause it's not allowed in OSGi.
                String qualifier = this.getQualifier().replaceAll( "\\.", "_" );
                osgiVersion.append( qualifier );
            }
        }

        return osgiVersion.toString();
    }

    @Override
    public long getBuildNumber()
    {
        return this.vi.getBuildNumber();
    }

    @Override
    public String getQualifier()
    {
        return this.vi.getQualifier();
    }

}
