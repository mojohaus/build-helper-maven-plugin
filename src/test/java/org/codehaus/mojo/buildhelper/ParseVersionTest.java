package org.codehaus.mojo.buildhelper;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.Properties;

import junit.framework.TestCase;

public class ParseVersionTest extends TestCase
{
    public void testParseVersion() throws Exception
    {
        ParseVersionMojo mojo = new ParseVersionMojo();
        mojo.setPropertyPrefix( "parsed" );
        Properties props = new Properties();

        // Test a junk version string
        mojo.parseVersion( "junk", props );

        assertEquals( "0", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "0", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "0", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "junk", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "0", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "junk", props.getProperty( "parsed.osgiVersion" ) );

        // Test a basic maven version string
        mojo.parseVersion( "1.0.0", props );

        assertEquals( "1", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "0", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "0", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "",  props.getProperty( "parsed.qualifier" ) );
        assertEquals( "0", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "1.0.0", props.getProperty( "parsed.osgiVersion" ) );

        // Test a version string with qualifier
        mojo.parseVersion( "2.3.4-beta-5", props );

        assertEquals( "2", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "4", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "beta-5", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "0", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "2.3.4.beta-5", props.getProperty( "parsed.osgiVersion" ) );

        // Test an osgi version string
        mojo.parseVersion( "2.3.4.beta_5", props );

        assertEquals( "2", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "4", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "beta_5", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "0", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "2.3.4.beta_5", props.getProperty( "parsed.osgiVersion" ) );

        // Test a snapshot version string
        mojo.parseVersion( "1.2.3-SNAPSHOT", props );

        assertEquals( "1", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "2", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "SNAPSHOT", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "0", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "1.2.3.SNAPSHOT", props.getProperty( "parsed.osgiVersion" ) );

        // Test a snapshot version string
        mojo.parseVersion( "2.0.17-SNAPSHOT", props );

        assertEquals( "2", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "0", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "17", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "SNAPSHOT", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "0", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "2.0.17.SNAPSHOT", props.getProperty( "parsed.osgiVersion" ) );

        // Test a version string with a build number
        mojo.parseVersion( "1.2.3-4", props );

        assertEquals( "1", props.getProperty( "parsed.majorVersion" ) );
        assertEquals( "2", props.getProperty( "parsed.minorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.incrementalVersion" ) );
        assertEquals( "", props.getProperty( "parsed.qualifier" ) );
        assertEquals( "4", props.getProperty( "parsed.buildNumber" ) );
        assertEquals( "1.2.3.4", props.getProperty( "parsed.osgiVersion" ) );

    }

    public void testParseVersionNext() throws Exception
    {
        ParseVersionMojo mojo = new ParseVersionMojo();
        mojo.setPropertyPrefix( "parsed" );
        Properties props = new Properties();

        // Test a junk version string
        mojo.parseVersion( "junk", props );

        assertEquals( "1", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "1", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "1", props.getProperty( "parsed.nextIncrementalVersion" ) );

        // Test a basic maven version string
        mojo.parseVersion( "1.0.0", props );

        assertEquals( "2", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "1", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "1", props.getProperty( "parsed.nextIncrementalVersion" ) );

        // Test a version string with qualifier
        mojo.parseVersion( "2.3.4-beta-5", props );

        assertEquals( "3", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "4", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "5", props.getProperty( "parsed.nextIncrementalVersion" ) );

        // Test an osgi version string
        mojo.parseVersion( "2.3.4.beta_5", props );

        assertEquals( "3", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "4", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "5", props.getProperty( "parsed.nextIncrementalVersion" ) );

        // Test a snapshot version string
        mojo.parseVersion( "1.2.3-SNAPSHOT", props );

        assertEquals( "2", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "4", props.getProperty( "parsed.nextIncrementalVersion" ) );

        // Test a snapshot version string
        mojo.parseVersion( "2.0.17-SNAPSHOT", props );

        assertEquals( "3", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "1", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "18", props.getProperty( "parsed.nextIncrementalVersion" ) );

        // Test a version string with a build number
        mojo.parseVersion( "1.2.3-4", props );

        assertEquals( "2", props.getProperty( "parsed.nextMajorVersion" ) );
        assertEquals( "3", props.getProperty( "parsed.nextMinorVersion" ) );
        assertEquals( "4", props.getProperty( "parsed.nextIncrementalVersion" ) );
    }
}
