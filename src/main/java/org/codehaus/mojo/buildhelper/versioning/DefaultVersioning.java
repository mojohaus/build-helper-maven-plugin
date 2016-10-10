package org.codehaus.mojo.buildhelper.versioning;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.Artifact;

/**
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
public class DefaultVersioning
    implements Versioning
{

    private VersionInformation vi;

    private String version;

    private boolean snapshot;

    public DefaultVersioning( String version )
    {
        this.version = version;
//        if ( version.endsWith( "-" + Artifact.SNAPSHOT_VERSION ) )
//        {
//            this.vi =
//                new VersionInformation( version.substring( 0, version.length() - Artifact.SNAPSHOT_VERSION.length() ) );
//            this.snapshot = true;
//        }
//        else
//        {
            this.vi = new VersionInformation( version );
//            this.snapshot = false;
//        }

    }

    public String getVersion()
    {
        return this.version;
    }

    public boolean isSnapshot()
    {
        return this.snapshot;
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
    public int getBuildNumber()
    {
        return this.vi.getBuildNumber();
    }

    @Override
    public String getQualifier()
    {
        return this.vi.getQualifier();
    }

}
