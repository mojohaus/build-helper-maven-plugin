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

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
public class VersionInformationTest
{

    private VersionInformation createVersion( String version )
    {
        return new VersionInformation( version );
    }

    private void assertVersion( VersionInformation v, int major, int minor, int patch, int buildNumber,
                                String qualifier )
    {
        assertThat( v.getMajor() ).isEqualTo( major );
        assertThat( v.getMinor() ).isEqualTo( minor );
        assertThat( v.getPatch() ).isEqualTo( patch );
        assertThat( v.getBuildNumber() ).isEqualTo( buildNumber );
        assertThat( v.getQualifier() ).isEqualTo( qualifier );
    }

    // @formatter:off
	@DataProvider
	public Object[][] createVersions() {
		return new Object[][] { 
			{ "1", 1, 0, 0, 0, null }, 
			{ "1-23", 1, 0, 0, 23, null },
			{ "1-23.heger", 1, 0, 0, 23, ".heger" }, 
			{ "1.2-45", 1, 2, 0, 45, null },
			{ "1.2-45.qual", 1, 2, 0, 45, ".qual" }, 
			{ "1.2-45-qual", 1, 2, 0, 45, "-qual" },
			{ "3.2", 3, 2, 0, 0, null }, 
			{ "5.7.1", 5, 7, 1, 0, null }, 
			{ "1.9.04-0012", 1, 9, 4, 12, null },
			{ "01.9.04-0012", 1, 9, 4, 12, null }, 
			{ "20.03.5-22", 20, 3, 5, 22, null },
			{ "20.03.5-056", 20, 3, 5, 56, null }, 
			{ "20.4.06.0-SNAPSHOT", 20, 4, 6, 0, "0-SNAPSHOT" },
			{ "1.2.3-SNAPSHOT", 1, 2, 3, 0, "SNAPSHOT" }, 
			{ "1.2.3-01-SNAPSHOT", 1, 2, 3, 1, "-SNAPSHOT" }, 
			{ "1.2.3-1-SNAPSHOT", 1, 2, 3, 1, "-SNAPSHOT" },
			{ "20.03.5.2016060708", 20, 3, 5, 0, "2016060708" },
			{ "20.03.5-23-2016060708", 20, 3, 5, 23, "-2016060708" },
			{ "20.03.5-23.2016060708", 20, 3, 5, 23, ".2016060708" },
			{ "20.03.5-111.anton", 20, 3, 5, 111, ".anton" }, 
			{ "20.03.5.22-SNAPSHOT", 20, 3, 5, 0, "22-SNAPSHOT" },
			{ "20.03.5.XYZ.345", 20, 3, 5, 0, "XYZ.345" }, 
			{ "20-88.03.5.XYZ.345", 20, 0, 0, 88, ".03.5.XYZ.345" },
			{ "20.4-88.03.5.XYZ.345", 20, 4, 0, 88, ".03.5.XYZ.345" },
			{ "020.04-88.03.5.XYZ.345", 20, 4, 0, 88, ".03.5.XYZ.345" },
			{ "20.7.12-88.03.5.XYZ.345", 20, 7, 12, 88, ".03.5.XYZ.345" },
			{ "20.03.5-111-34-anton", 20, 3, 5, 111, "-34-anton" },
			{ "20.03.5-111-34.anton", 20, 3, 5, 111, "-34.anton" }, 
			{ "junk", 0, 0, 0, 0, "junk" }, 
			{ "2.3.4-beta_5", 2, 3, 4, 0, "beta_5" }, 
			{ "2.3.4.beta_5", 2, 3, 4, 0, "beta_5" } 
		};
	}
	// @formatter:on

    @Test( dataProvider = "createVersions" )
    public void checkVersions( String version, int major, int minor, int patch, int buildNumber, String qualifier )
    {
        VersionInformation vi = createVersion( version );
        assertVersion( vi, major, minor, patch, buildNumber, qualifier );
    }

}
