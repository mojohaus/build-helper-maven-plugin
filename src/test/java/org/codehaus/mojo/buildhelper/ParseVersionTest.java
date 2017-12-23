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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test
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
        {
            // Test a junk version string
            mojo.parseVersion( "junk" );

            assertThat( props.getProperty( "parsed.majorVersion" ) ).isEqualTo( "0" );
            assertThat( props.getProperty( "parsed.minorVersion" ) ).isEqualTo( "0" );
            assertThat( props.getProperty( "parsed.incrementalVersion" ) ).isEqualTo( "0" );
            assertThat( props.getProperty( "parsed.qualifier" ) ).isEqualTo( "junk" );
            assertThat( props.getProperty( "parsed.buildNumber" ) ).isEqualTo( "0" );
            assertThat( props.getProperty( "parsed.osgiVersion" ) ).isEqualTo( "0.0.0.junk" );
        }

        @Test
        public void testBasicMavenVersionString()
            throws Exception
        {
            // Test a basic maven version string
            mojo.parseVersion( "1.0.0" );

            assertThat( props.getProperty( "parsed.majorVersion" ) ).isEqualTo( "1" );
            assertThat( props.getProperty( "parsed.minorVersion" ) ).isEqualTo( "0" );
            assertThat( props.getProperty( "parsed.incrementalVersion" ) ).isEqualTo( "0" );
            assertThat( props.getProperty( "parsed.qualifier" ) ).isEqualTo( "" );
            assertThat( props.getProperty( "parsed.buildNumber" ) ).isEqualTo( "0" );
            assertThat( props.getProperty( "parsed.osgiVersion" ) ).isEqualTo( "1.0.0" );
        }

        @Test
        public void testVersionStringWithQualifier()
        {
            // Test a version string with qualifier
            mojo.parseVersion( "2.3.4-beta-5" );

            assertThat( props.getProperty( "parsed.majorVersion" ) ).isEqualTo( "2" );
            assertThat( props.getProperty( "parsed.minorVersion" ) ).isEqualTo( "3" );
            assertThat( props.getProperty( "parsed.incrementalVersion" ) ).isEqualTo( "4" );
            assertThat( props.getProperty( "parsed.qualifier" ) ).isEqualTo( "beta-5" );
            assertThat( props.getProperty( "parsed.buildNumber" ) ).isEqualTo( "0" );
            assertThat( props.getProperty( "parsed.osgiVersion" ) ).isEqualTo( "2.3.4.beta-5" );
        }

        @Test
        public void testOSGiVersionStringWithQualifier()
        {
            // Test an osgi version string
            mojo.parseVersion( "2.3.4.beta_5" );

            assertThat( props.getProperty( "parsed.majorVersion" ) ).isEqualTo( "2" );
            assertThat( props.getProperty( "parsed.minorVersion" ) ).isEqualTo( "3" );
            assertThat( props.getProperty( "parsed.incrementalVersion" ) ).isEqualTo( "4" );
            assertThat( props.getProperty( "parsed.qualifier" ) ).isEqualTo( "beta_5" );
            assertThat( props.getProperty( "parsed.buildNumber" ) ).isEqualTo( "0" );
            assertThat( props.getProperty( "parsed.osgiVersion" ) ).isEqualTo( "2.3.4.beta_5" );
        }

        @Test
        public void testSnapshotVersion()
        {
            // Test a snapshot version string
            mojo.parseVersion( "1.2.3-SNAPSHOT" );

            assertThat( props.getProperty( "parsed.majorVersion" ) ).isEqualTo( "1" );
            assertThat( props.getProperty( "parsed.minorVersion" ) ).isEqualTo( "2" );
            assertThat( props.getProperty( "parsed.incrementalVersion" ) ).isEqualTo( "3" );
            assertThat( props.getProperty( "parsed.qualifier" ) ).isEqualTo( "SNAPSHOT" );
            assertThat( props.getProperty( "parsed.buildNumber" ) ).isEqualTo( "0" );
            assertThat( props.getProperty( "parsed.osgiVersion" ) ).isEqualTo( "1.2.3.SNAPSHOT" );
        }

        @Test
        public void testSnapshotVersion2()
        {
            // Test a snapshot version string
            mojo.parseVersion( "2.0.17-SNAPSHOT" );

            assertThat( props.getProperty( "parsed.majorVersion" ) ).isEqualTo( "2" );
            assertThat( props.getProperty( "parsed.minorVersion" ) ).isEqualTo( "0" );
            assertThat( props.getProperty( "parsed.incrementalVersion" ) ).isEqualTo( "17" );
            assertThat( props.getProperty( "parsed.qualifier" ) ).isEqualTo( "SNAPSHOT" );
            assertThat( props.getProperty( "parsed.buildNumber" ) ).isEqualTo( "0" );
            assertThat( props.getProperty( "parsed.osgiVersion" ) ).isEqualTo( "2.0.17.SNAPSHOT" );
        }

        @Test
        public void testVersionStringWithBuildNumber()
        {
            // Test a version string with a build number
            mojo.parseVersion( "1.2.3-4" );

            assertThat( props.getProperty( "parsed.majorVersion" ) ).isEqualTo( "1" );
            assertThat( props.getProperty( "parsed.minorVersion" ) ).isEqualTo( "2" );
            assertThat( props.getProperty( "parsed.incrementalVersion" ) ).isEqualTo( "3" );
            assertThat( props.getProperty( "parsed.qualifier" ) ).isEqualTo( "" );
            assertThat( props.getProperty( "parsed.buildNumber" ) ).isEqualTo( "4" );
            assertThat( props.getProperty( "parsed.osgiVersion" ) ).isEqualTo( "1.2.3.4" );

        }

        @Test
        public void testSnapshotVersionStringWithBuildNumber()
        {
            // Test a version string with a build number
            mojo.parseVersion( "1.2.3-4-SNAPSHOT" );

            assertThat( props.getProperty( "parsed.majorVersion" ) ).isEqualTo( "1" );
            assertThat( props.getProperty( "parsed.minorVersion" ) ).isEqualTo( "2" );
            assertThat( props.getProperty( "parsed.incrementalVersion" ) ).isEqualTo( "3" );
            assertThat( props.getProperty( "parsed.qualifier" ) ).isEqualTo( "-SNAPSHOT" );
            assertThat( props.getProperty( "parsed.buildNumber" ) ).isEqualTo( "4" );
            assertThat( props.getProperty( "parsed.osgiVersion" ) ).isEqualTo( "1.2.3.4-SNAPSHOT" );
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

            assertThat( props.getProperty( "parsed.nextMajorVersion" ) ).isEqualTo( "1" );
            assertThat( props.getProperty( "parsed.nextMinorVersion" ) ).isEqualTo( "1" );
            assertThat( props.getProperty( "parsed.nextIncrementalVersion" ) ).isEqualTo( "1" );
            assertThat( props.getProperty( "parsed.nextBuildNumber" ) ).isEqualTo( "1" );
        }

        @Test
        public void testBasicMavenVersion()
        {
            mojo.parseVersion( "1.0.0" );

            assertThat( props.getProperty( "parsed.nextMajorVersion" ) ).isEqualTo( "2" );
            assertThat( props.getProperty( "parsed.nextMinorVersion" ) ).isEqualTo( "1" );
            assertThat( props.getProperty( "parsed.nextIncrementalVersion" ) ).isEqualTo( "1" );
            assertThat( props.getProperty( "parsed.nextBuildNumber" ) ).isEqualTo( "1" );
        }

        @Test
        public void testVersionStringWithQualifier()
        {
            mojo.parseVersion( "2.3.4-beta-5" );

            assertThat( props.getProperty( "parsed.nextMajorVersion" ) ).isEqualTo( "3" );
            assertThat( props.getProperty( "parsed.nextMinorVersion" ) ).isEqualTo( "4" );
            assertThat( props.getProperty( "parsed.nextIncrementalVersion" ) ).isEqualTo( "5" );
            assertThat( props.getProperty( "parsed.nextBuildNumber" ) ).isEqualTo( "1" );
        }

        @Test
        public void testOSGiVersion()
        {
            mojo.parseVersion( "2.3.4.beta_5" );

            assertThat( props.getProperty( "parsed.nextMajorVersion" ) ).isEqualTo( "3" );
            assertThat( props.getProperty( "parsed.nextMinorVersion" ) ).isEqualTo( "4" );
            assertThat( props.getProperty( "parsed.nextIncrementalVersion" ) ).isEqualTo( "5" );
            assertThat( props.getProperty( "parsed.nextBuildNumber" ) ).isEqualTo( "1" );
        }

        @Test
        public void testSnapshotVersion()
        {
            // Test a snapshot version string
            mojo.parseVersion( "1.2.3-SNAPSHOT" );

            assertThat( props.getProperty( "parsed.nextMajorVersion" ) ).isEqualTo( "2" );
            assertThat( props.getProperty( "parsed.nextMinorVersion" ) ).isEqualTo( "3" );
            assertThat( props.getProperty( "parsed.nextIncrementalVersion" ) ).isEqualTo( "4" );
            assertThat( props.getProperty( "parsed.nextBuildNumber" ) ).isEqualTo( "1" );
        }

        @Test
        public void testSnapshotVersion2()
        {
            // Test a snapshot version string
            mojo.parseVersion( "2.0.17-SNAPSHOT" );

            assertThat( props.getProperty( "parsed.nextMajorVersion" ) ).isEqualTo( "3" );
            assertThat( props.getProperty( "parsed.nextMinorVersion" ) ).isEqualTo( "1" );
            assertThat( props.getProperty( "parsed.nextIncrementalVersion" ) ).isEqualTo( "18" );
            assertThat( props.getProperty( "parsed.nextBuildNumber" ) ).isEqualTo( "1" );
        }

        @Test
        public void testVersionStringWithBuildNumber()
        {
            mojo.parseVersion( "1.2.3-4" );

            assertThat( props.getProperty( "parsed.nextMajorVersion" ) ).isEqualTo( "2" );
            assertThat( props.getProperty( "parsed.nextMinorVersion" ) ).isEqualTo( "3" );
            assertThat( props.getProperty( "parsed.nextIncrementalVersion" ) ).isEqualTo( "4" );
            assertThat( props.getProperty( "parsed.nextBuildNumber" ) ).isEqualTo( "5" );
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

            assertThat( props.getProperty( "formatted.majorVersion" ) ).isEqualTo( "00" );
            assertThat( props.getProperty( "formatted.minorVersion" ) ).isEqualTo( "00" );
            assertThat( props.getProperty( "formatted.incrementalVersion" ) ).isEqualTo( "00" );
            assertThat( props.getProperty( "formatted.buildNumber" ) ).isEqualTo( "00" );

            assertThat( props.getProperty( "formatted.nextMajorVersion" ) ).isEqualTo( "01" );
            assertThat( props.getProperty( "formatted.nextMinorVersion" ) ).isEqualTo( "01" );
            assertThat( props.getProperty( "formatted.nextIncrementalVersion" ) ).isEqualTo( "01" );
            assertThat( props.getProperty( "formatted.nextBuildNumber" ) ).isEqualTo( "01" );
        }

        @Test
        public void testBasicMavenVersion()
        {
            mojo.parseVersion( "1.0.0" );

            assertThat( props.getProperty( "formatted.majorVersion" ) ).isEqualTo( "01" );
            assertThat( props.getProperty( "formatted.minorVersion" ) ).isEqualTo( "00" );
            assertThat( props.getProperty( "formatted.incrementalVersion" ) ).isEqualTo( "00" );
            assertThat( props.getProperty( "formatted.buildNumber" ) ).isEqualTo( "00" );

            assertThat( props.getProperty( "formatted.nextMajorVersion" ) ).isEqualTo( "02" );
            assertThat( props.getProperty( "formatted.nextMinorVersion" ) ).isEqualTo( "01" );
            assertThat( props.getProperty( "formatted.nextIncrementalVersion" ) ).isEqualTo( "01" );
            assertThat( props.getProperty( "formatted.nextBuildNumber" ) ).isEqualTo( "01" );
        }

        @Test
        public void testVersionStringWithQualifier()
        {
            mojo.parseVersion( "2.3.4-beta-5" );

            assertThat( props.getProperty( "formatted.majorVersion" ) ).isEqualTo( "02" );
            assertThat( props.getProperty( "formatted.minorVersion" ) ).isEqualTo( "03" );
            assertThat( props.getProperty( "formatted.incrementalVersion" ) ).isEqualTo( "04" );
            assertThat( props.getProperty( "formatted.buildNumber" ) ).isEqualTo( "00" );

            assertThat( props.getProperty( "formatted.nextMajorVersion" ) ).isEqualTo( "03" );
            assertThat( props.getProperty( "formatted.nextMinorVersion" ) ).isEqualTo( "04" );
            assertThat( props.getProperty( "formatted.nextIncrementalVersion" ) ).isEqualTo( "05" );
            assertThat( props.getProperty( "formatted.nextBuildNumber" ) ).isEqualTo( "01" );

        }

        @Test
        public void testOSGiVersion()
        {
            mojo.parseVersion( "2.3.4.beta_5" );

            assertThat( props.getProperty( "formatted.majorVersion" ) ).isEqualTo( "02" );
            assertThat( props.getProperty( "formatted.minorVersion" ) ).isEqualTo( "03" );
            assertThat( props.getProperty( "formatted.incrementalVersion" ) ).isEqualTo( "04" );
            assertThat( props.getProperty( "formatted.buildNumber" ) ).isEqualTo( "00" );

            assertThat( props.getProperty( "formatted.nextMajorVersion" ) ).isEqualTo( "03" );
            assertThat( props.getProperty( "formatted.nextMinorVersion" ) ).isEqualTo( "04" );
            assertThat( props.getProperty( "formatted.nextIncrementalVersion" ) ).isEqualTo( "05" );
            assertThat( props.getProperty( "formatted.nextBuildNumber" ) ).isEqualTo( "01" );

        }

        @Test
        public void testSnapshotVersion()
        {
            // Test a snapshot version string
            mojo.parseVersion( "1.2.3-SNAPSHOT" );

            assertThat( props.getProperty( "formatted.majorVersion" ) ).isEqualTo( "01" );
            assertThat( props.getProperty( "formatted.minorVersion" ) ).isEqualTo( "02" );
            assertThat( props.getProperty( "formatted.incrementalVersion" ) ).isEqualTo( "03" );
            assertThat( props.getProperty( "formatted.buildNumber" ) ).isEqualTo( "00" );

            assertThat( props.getProperty( "formatted.nextMajorVersion" ) ).isEqualTo( "02" );
            assertThat( props.getProperty( "formatted.nextMinorVersion" ) ).isEqualTo( "03" );
            assertThat( props.getProperty( "formatted.nextIncrementalVersion" ) ).isEqualTo( "04" );
            assertThat( props.getProperty( "formatted.nextBuildNumber" ) ).isEqualTo( "01" );

        }

        @Test
        public void testSnapshotVersion2()
        {
            // Test a snapshot version string
            mojo.parseVersion( "2.0.17-SNAPSHOT" );

            assertThat( props.getProperty( "formatted.majorVersion" ) ).isEqualTo( "02" );
            assertThat( props.getProperty( "formatted.minorVersion" ) ).isEqualTo( "00" );
            assertThat( props.getProperty( "formatted.incrementalVersion" ) ).isEqualTo( "17" );
            assertThat( props.getProperty( "formatted.buildNumber" ) ).isEqualTo( "00" );

            assertThat( props.getProperty( "formatted.nextMajorVersion" ) ).isEqualTo( "03" );
            assertThat( props.getProperty( "formatted.nextMinorVersion" ) ).isEqualTo( "01" );
            assertThat( props.getProperty( "formatted.nextIncrementalVersion" ) ).isEqualTo( "18" );
            assertThat( props.getProperty( "formatted.nextBuildNumber" ) ).isEqualTo( "01" );
        }

        @Test
        public void testVersionStringWithBuildNumber()
        {
            mojo.parseVersion( "1.2.3-4" );

            assertThat( props.getProperty( "formatted.majorVersion" ) ).isEqualTo( "01" );
            assertThat( props.getProperty( "formatted.minorVersion" ) ).isEqualTo( "02" );
            assertThat( props.getProperty( "formatted.incrementalVersion" ) ).isEqualTo( "03" );
            assertThat( props.getProperty( "formatted.buildNumber" ) ).isEqualTo( "04" );

            assertThat( props.getProperty( "formatted.nextMajorVersion" ) ).isEqualTo( "02" );
            assertThat( props.getProperty( "formatted.nextMinorVersion" ) ).isEqualTo( "03" );
            assertThat( props.getProperty( "formatted.nextIncrementalVersion" ) ).isEqualTo( "04" );
            assertThat( props.getProperty( "formatted.nextBuildNumber" ) ).isEqualTo( "05" );
        }

    }

}
