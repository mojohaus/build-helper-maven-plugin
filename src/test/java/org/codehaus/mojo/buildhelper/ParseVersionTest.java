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

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ParseVersionTest
{
    private static class TestParseVersionMojo
        extends ParseVersionMojo
    {
        private Properties properties;

        public TestParseVersionMojo( Properties properties )
        {
            this.properties = properties;
        }

        protected void defineProperty( String name, String value )
        {
            properties.put( name, value );
        }
    }

    public class TestParseVersion
    {
        private Properties props;

        private ParseVersionMojo mojo;

        @BeforeMethod
        public void beforeClass()
        {
            props = new Properties();
            mojo = new TestParseVersionMojo( props );
            mojo.setPropertyPrefix( "parsed" );

            mojo.setFormattedPropertyPrefix( "formatted" );
            // The settings should in line with the given defaultValues of the parameters
            // in the mojo definition.
            mojo.setFormatMajor( "%02d" );
            mojo.setFormatMinor( "%02d" );
            mojo.setFormatIncremental( "%02d" );
            mojo.setFormatBuildNumber( "%02d" );
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
            assertEquals( "0.0.0.junk", props.getProperty( "parsed.osgiVersion" ) );
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
            assertEquals( "-SNAPSHOT", props.getProperty( "parsed.qualifier" ) );
            assertEquals( "4", props.getProperty( "parsed.buildNumber" ) );
            assertEquals( "1.2.3.4-SNAPSHOT", props.getProperty( "parsed.osgiVersion" ) );
        }

    }

    public class TestParseNextVersion
    {
        private Properties props;

        private ParseVersionMojo mojo;

        @BeforeMethod
        public void beforeClass()
        {
            props = new Properties();
            mojo = new TestParseVersionMojo( props );
            mojo.setPropertyPrefix( "parsed" );
            mojo.setFormattedPropertyPrefix( "formatted" );
            // The settings should in line with the given defaultValues of the parameters
            // in the mojo definition.
            mojo.setFormatMajor( "%02d" );
            mojo.setFormatMinor( "%02d" );
            mojo.setFormatIncremental( "%02d" );
            mojo.setFormatBuildNumber( "%02d" );
        }

        @Test
        public void testJunkVersion()
        {
            mojo.parseVersion( "junk" );

            assertEquals( "1", props.getProperty( "parsed.nextMajorVersion" ) );
            assertEquals( "1", props.getProperty( "parsed.nextMinorVersion" ) );
            assertEquals( "1", props.getProperty( "parsed.nextIncrementalVersion" ) );
            assertEquals( "1", props.getProperty( "parsed.nextBuildNumber" ) );
        }

        @Test
        public void testBasicMavenVersion()
        {
            mojo.parseVersion( "1.0.0" );

            assertEquals( "2", props.getProperty( "parsed.nextMajorVersion" ) );
            assertEquals( "1", props.getProperty( "parsed.nextMinorVersion" ) );
            assertEquals( "1", props.getProperty( "parsed.nextIncrementalVersion" ) );
            assertEquals( "1", props.getProperty( "parsed.nextBuildNumber" ) );
        }

        @Test
        public void testVersionStringWithQualifier()
        {
            mojo.parseVersion( "2.3.4-beta-5" );

            assertEquals( "3", props.getProperty( "parsed.nextMajorVersion" ) );
            assertEquals( "4", props.getProperty( "parsed.nextMinorVersion" ) );
            assertEquals( "5", props.getProperty( "parsed.nextIncrementalVersion" ) );
            assertEquals( "1", props.getProperty( "parsed.nextBuildNumber" ) );
        }

        @Test
        public void testOSGiVersion()
        {
            mojo.parseVersion( "2.3.4.beta_5" );

            assertEquals( "3", props.getProperty( "parsed.nextMajorVersion" ) );
            assertEquals( "4", props.getProperty( "parsed.nextMinorVersion" ) );
            assertEquals( "5", props.getProperty( "parsed.nextIncrementalVersion" ) );
            assertEquals( "1", props.getProperty( "parsed.nextBuildNumber" ) );
        }

        @Test
        public void testSnapshotVersion()
        {
            // Test a snapshot version string
            mojo.parseVersion( "1.2.3-SNAPSHOT" );

            assertEquals( "2", props.getProperty( "parsed.nextMajorVersion" ) );
            assertEquals( "3", props.getProperty( "parsed.nextMinorVersion" ) );
            assertEquals( "4", props.getProperty( "parsed.nextIncrementalVersion" ) );
            assertEquals( "1", props.getProperty( "parsed.nextBuildNumber" ) );
        }

        @Test
        public void testSnapshotVersion2()
        {
            // Test a snapshot version string
            mojo.parseVersion( "2.0.17-SNAPSHOT" );

            assertEquals( "3", props.getProperty( "parsed.nextMajorVersion" ) );
            assertEquals( "1", props.getProperty( "parsed.nextMinorVersion" ) );
            assertEquals( "18", props.getProperty( "parsed.nextIncrementalVersion" ) );
            assertEquals( "1", props.getProperty( "parsed.nextBuildNumber" ) );
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

    }

    
    public class TestFormattedVersion
    {
        private Properties props;

        private ParseVersionMojo mojo;

        @BeforeMethod
        public void beforeClass()
        {
            props = new Properties();
            mojo = new TestParseVersionMojo( props );
            mojo.setPropertyPrefix( "parsed" );
            mojo.setFormattedPropertyPrefix( "formatted" );
            // The settings should in line with the given defaultValues of the parameters
            // in the mojo definition.
            mojo.setFormatMajor( "%02d" );
            mojo.setFormatMinor( "%02d" );
            mojo.setFormatIncremental( "%02d" );
            mojo.setFormatBuildNumber( "%02d" );
        }

        @Test
        public void testJunkVersion()
        {
            mojo.parseVersion( "junk" );

            assertEquals( "00", props.getProperty( "formatted.majorVersion" ) );
            assertEquals( "00", props.getProperty( "formatted.minorVersion" ) );
            assertEquals( "00", props.getProperty( "formatted.incrementalVersion" ) );
            assertEquals( "00", props.getProperty( "formatted.buildNumber" ) );

            assertEquals( "01", props.getProperty( "formatted.nextMajorVersion" ) );
            assertEquals( "01", props.getProperty( "formatted.nextMinorVersion" ) );
            assertEquals( "01", props.getProperty( "formatted.nextIncrementalVersion" ) );
            assertEquals( "01", props.getProperty( "formatted.nextBuildNumber" ) );
        }

        @Test
        public void testBasicMavenVersion()
        {
            mojo.parseVersion( "1.0.0" );

            assertEquals( "01", props.getProperty( "formatted.majorVersion" ) );
            assertEquals( "00", props.getProperty( "formatted.minorVersion" ) );
            assertEquals( "00", props.getProperty( "formatted.incrementalVersion" ) );
            assertEquals( "00", props.getProperty( "formatted.buildNumber" ) );

            assertEquals( "02", props.getProperty( "formatted.nextMajorVersion" ) );
            assertEquals( "01", props.getProperty( "formatted.nextMinorVersion" ) );
            assertEquals( "01", props.getProperty( "formatted.nextIncrementalVersion" ) );
            assertEquals( "01", props.getProperty( "formatted.nextBuildNumber" ) );
        }

        @Test
        public void testVersionStringWithQualifier()
        {
            mojo.parseVersion( "2.3.4-beta-5" );

            assertEquals( "02", props.getProperty( "formatted.majorVersion" ) );
            assertEquals( "03", props.getProperty( "formatted.minorVersion" ) );
            assertEquals( "04", props.getProperty( "formatted.incrementalVersion" ) );
            assertEquals( "00", props.getProperty( "formatted.buildNumber" ) );

            assertEquals( "03", props.getProperty( "formatted.nextMajorVersion" ) );
            assertEquals( "04", props.getProperty( "formatted.nextMinorVersion" ) );
            assertEquals( "05", props.getProperty( "formatted.nextIncrementalVersion" ) );
            assertEquals( "01", props.getProperty( "formatted.nextBuildNumber" ) );

        }

        @Test
        public void testOSGiVersion()
        {
            mojo.parseVersion( "2.3.4.beta_5" );

            assertEquals( "02", props.getProperty( "formatted.majorVersion" ) );
            assertEquals( "03", props.getProperty( "formatted.minorVersion" ) );
            assertEquals( "04", props.getProperty( "formatted.incrementalVersion" ) );
            assertEquals( "00", props.getProperty( "formatted.buildNumber" ) );

            assertEquals( "03", props.getProperty( "formatted.nextMajorVersion" ) );
            assertEquals( "04", props.getProperty( "formatted.nextMinorVersion" ) );
            assertEquals( "05", props.getProperty( "formatted.nextIncrementalVersion" ) );
            assertEquals( "01", props.getProperty( "formatted.nextBuildNumber" ) );

        }

        @Test
        public void testSnapshotVersion()
        {
            // Test a snapshot version string
            mojo.parseVersion( "1.2.3-SNAPSHOT" );

            assertEquals( "01", props.getProperty( "formatted.majorVersion" ) );
            assertEquals( "02", props.getProperty( "formatted.minorVersion" ) );
            assertEquals( "03", props.getProperty( "formatted.incrementalVersion" ) );
            assertEquals( "00", props.getProperty( "formatted.buildNumber" ) );

            assertEquals( "02", props.getProperty( "formatted.nextMajorVersion" ) );
            assertEquals( "03", props.getProperty( "formatted.nextMinorVersion" ) );
            assertEquals( "04", props.getProperty( "formatted.nextIncrementalVersion" ) );
            assertEquals( "01", props.getProperty( "formatted.nextBuildNumber" ) );

        }

        @Test
        public void testSnapshotVersion2()
        {
            // Test a snapshot version string
            mojo.parseVersion( "2.0.17-SNAPSHOT" );

            assertEquals( "02", props.getProperty( "formatted.majorVersion" ) );
            assertEquals( "00", props.getProperty( "formatted.minorVersion" ) );
            assertEquals( "17", props.getProperty( "formatted.incrementalVersion" ) );
            assertEquals( "00", props.getProperty( "formatted.buildNumber" ) );

            assertEquals( "03", props.getProperty( "formatted.nextMajorVersion" ) );
            assertEquals( "01", props.getProperty( "formatted.nextMinorVersion" ) );
            assertEquals( "18", props.getProperty( "formatted.nextIncrementalVersion" ) );
            assertEquals( "01", props.getProperty( "formatted.nextBuildNumber" ) );
        }

        @Test
        public void testVersionStringWithBuildNumber()
        {
            mojo.parseVersion( "1.2.3-4" );

            assertEquals( "01", props.getProperty( "formatted.majorVersion" ) );
            assertEquals( "02", props.getProperty( "formatted.minorVersion" ) );
            assertEquals( "03", props.getProperty( "formatted.incrementalVersion" ) );
            assertEquals( "04", props.getProperty( "formatted.buildNumber" ) );

            assertEquals( "02", props.getProperty( "formatted.nextMajorVersion" ) );
            assertEquals( "03", props.getProperty( "formatted.nextMinorVersion" ) );
            assertEquals( "04", props.getProperty( "formatted.nextIncrementalVersion" ) );
            assertEquals( "05", props.getProperty( "formatted.nextBuildNumber" ) );
        }

    }

}
