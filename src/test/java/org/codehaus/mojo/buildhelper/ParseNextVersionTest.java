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

public class ParseNextVersionTest
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
    {
        mojo.parseVersion( "junk" );

        assertEquals( "1", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "1", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "1", props.getProperty( "parsed.nextIncrementalVersion" ) );
    }

    @Test
    public void testBasicMavenVersion()
    {
        mojo.parseVersion( "1.0.0" );

        assertEquals( "2", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "1", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "1", props.getProperty( "parsed.nextIncrementalVersion" ) );
    }

    @Test
    public void testVersionStringWithQualifier()
    {
        mojo.parseVersion( "2.3.4-beta-5" );

        assertEquals( "3", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "4", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "5", props.getProperty( "parsed.nextIncrementalVersion" ) );
    }

    @Test
    public void testOSGiVersion()
    {
        mojo.parseVersion( "2.3.4.beta_5" );

        assertEquals( "3", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "4", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "5", props.getProperty( "parsed.nextIncrementalVersion" ) );
    }

    @Test
    public void testSnapshotVersion()
    {
        // Test a snapshot version string
        mojo.parseVersion( "1.2.3-SNAPSHOT" );

        assertEquals( "2", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "4", props.getProperty( "parsed.nextIncrementalVersion" ) );
    }

    @Test
    public void testSnapshotVersion2()
    {
        // Test a snapshot version string
        mojo.parseVersion( "2.0.17-SNAPSHOT" );

        assertEquals( "3", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "1", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "18", props.getProperty( "parsed.nextIncrementalVersion" ) );
    }

    @Test
    public void testVersionStringWithBuildNumber()
    {
        mojo.parseVersion( "1.2.3-4" );

        assertEquals( "2", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "4", props.getProperty( "parsed.nextIncrementalVersion" ) );
        assertEquals( "5", props.getProperty( "parsed.nextBuildNumber" ) );
    }

    @Test
    public void testVersionStringWithNumberStartingWithZero()
    {
        mojo.parseVersion( "01.02.03-04" );

        assertEquals( "2", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "4", props.getProperty( "parsed.nextIncrementalVersion" ) );
        assertEquals( "5", props.getProperty( "parsed.nextBuildNumber" ) );
    }

    @Test
    public void testVersionStringWithNumberStartingWithZeroAndPreserveTrue()
    {
        mojo.setPreserveZero( true );
        mojo.parseVersion( "01.02.03-04" );

        assertEquals( "02", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "03", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "04", props.getProperty( "parsed.nextIncrementalVersion" ) );
        assertEquals( "05", props.getProperty( "parsed.nextBuildNumber" ) );
    }

    @Test
    public void testVersionStringWithNumberStartingWithZeroAndPreserveTrueCornerCase1()
    {
        mojo.setPreserveZero( true );

        mojo.parseVersion( "1.09.03-10" );
        assertEquals( "2", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "10", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "04", props.getProperty( "parsed.nextIncrementalVersion" ) );
        assertEquals( "11", props.getProperty( "parsed.nextBuildNumber" ) );
    }

    @Test
    public void testVersionStringWithNumberStartingWithZeroAndPreserveTrueCornerCase2()
    {
        mojo.setPreserveZero( true );

        mojo.parseVersion( "0.09.0-10" );
        assertEquals( "1", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "10", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "1", props.getProperty( "parsed.nextIncrementalVersion" ) );
        assertEquals( "11", props.getProperty( "parsed.nextBuildNumber" ) );
    }

}
