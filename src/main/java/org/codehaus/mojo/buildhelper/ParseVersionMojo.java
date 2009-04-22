package org.codehaus.mojo.buildhelper;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file 
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied.  See the License for the 
 * specific language governing permissions and limitations 
 * under the License.
 */

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

import java.util.Properties;

/**
 * Parse a version string and set properties containing the component parts of the version.  This mojo sets the 
 * following properties:
 * 
 * <pre>
 *   [propertyPrefix].majorVersion
 *   [propertyPrefix].minorVersion
 *   [propertyPrefix].incrementalVersion
 *   [propertyPrefix].qualifier
 *   [propertyPrefix].buildNumber
 * </pre>
 * Where the propertyPrefix is the string set in the mojo parameter.  Note that the behaviour of the
 * parsing is determined by org.apache.maven.artifact.versioning.DefaultArtifactVersion
 * 
 * An osgi compatible version will also be created and made available through the property:
 * <pre>
 *   [propertyPrefix].osgiVersion
 * </pre>
 * This version is simply the original version string with the first instance of '-' replaced by '.'
 * For example, 1.0.2-beta-1 will be converted to 1.0.2.beta-1
 * 
 * @author pgier
 * @version $Id$
 * @goal parse-version
 * @phase validate
 * @since 1.3
 * 
 */
public class ParseVersionMojo
    extends AbstractMojo
{

    /**
     * The Maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The version string to parse.
     * 
     * @parameter default-value="${project.version}"
     */
    private String versionString;

    /**
     * Prefix string to use for the set of version properties.
     * 
     * @parameter default-value="parsedVersion"
     */
    private String propertyPrefix;

    /**
     * Execute the mojo. This sets the version properties on the project.
     */
    public void execute()
        throws MojoExecutionException
    {
        
        ArtifactVersion artifactVersion = new DefaultArtifactVersion( versionString );

        Properties props = project.getProperties();

        props.setProperty( propertyPrefix + ".majorVersion", Integer.toString( artifactVersion.getMajorVersion() ) );
        props.setProperty( propertyPrefix + ".minorVersion", Integer.toString( artifactVersion.getMinorVersion() ) );
        props.setProperty( propertyPrefix + ".incrementalVersion",
                           Integer.toString( artifactVersion.getIncrementalVersion() ) );
        props.setProperty( propertyPrefix + ".qualifier", artifactVersion.getQualifier() );
        props.setProperty( propertyPrefix + ".buildNumber", Integer.toString( artifactVersion.getBuildNumber() ) );

        // Replace the first instance of "-" to create an osgi compatible version string.
        String osgiVersion = StringUtils.replaceOnce( versionString, '-', '.' );
        props.setProperty( propertyPrefix + ".osgiVersion", osgiVersion );
    }
}
