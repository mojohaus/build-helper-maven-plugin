package org.codehaus.mojo.buildhelper;/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class ParseVersionTest
{
    private Properties props;

    private ParseVersionMojo mojo;

    @BeforeMethod
    public void beforeMethod()
    {
        props = new Properties();
        mojo = new FakeParseVersionMojo( props );
        mojo.setPropertyPrefix( "parsed" );
        mojo.setPreserveZero(false);

    }

    @Test
    public void testJunkVersion()
        throws Exception
    {
        // Test a junk version string
        mojo.parseVersion( "junk" );

        assertEquals( "0", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "0", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "0", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "junk", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "0", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "junk", props.getProperty( "parsed.osgiVersion" ) );
    }

    @Test
    public void testBasicMavenVersionString()
        throws Exception
    {
        // Test a basic maven version string
        mojo.parseVersion( "1.0.0" );

        assertEquals( "1", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "0", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "0", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "0", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "1.0.0", props.getProperty( "parsed.osgiVersion" ) );
    }

    @Test
    public void testVersionStringWithQualifier()
    {
        // Test a version string with qualifier
        mojo.parseVersion( "2.3.4-beta-5" );

        assertEquals( "2", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "4", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "beta-5", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "0", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "2.3.4.beta-5", props.getProperty( "parsed.osgiVersion" ) );
    }

    @Test
    public void testOSGiVersionStringWithQualifier()
    {
        // Test an osgi version string
        mojo.parseVersion( "2.3.4.beta_5" );

        assertEquals( "2", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "4", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "beta_5", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "0", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "2.3.4.beta_5", props.getProperty( "parsed.osgiVersion" ) );
    }

    @Test
    public void testSnapshotVersion()
    {
        // Test a snapshot version string
        mojo.parseVersion( "1.2.3-SNAPSHOT" );

        assertEquals( "1", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "2", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "SNAPSHOT", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "0", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "1.2.3.SNAPSHOT", props.getProperty( "parsed.osgiVersion" ) );
    }

    @Test
    public void testSnapshotVersion2()
    {
        // Test a snapshot version string
        mojo.parseVersion( "2.0.17-SNAPSHOT" );

        assertEquals( "2", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "0", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "17", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "SNAPSHOT", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "0", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "2.0.17.SNAPSHOT", props.getProperty( "parsed.osgiVersion" ) );
    }

    @Test
    public void testVersionStringWithBuildNumber()
    {
        // Test a version string with a build number
        mojo.parseVersion( "1.2.3-4" );

        assertEquals( "1", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "2", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "4", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "1.2.3.4", props.getProperty( "parsed.osgiVersion" ) );

    }

    @Test
    public void testSnapshotVersionStringWithBuildNumber()
    {
        // Test a version string with a build number
        mojo.parseVersion( "1.2.3-4-SNAPSHOT" );

        assertEquals( "1", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "2", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "SNAPSHOT", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "4", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "1.2.3.4.SNAPSHOT", props.getProperty( "parsed.osgiVersion" ) );
    }

}
