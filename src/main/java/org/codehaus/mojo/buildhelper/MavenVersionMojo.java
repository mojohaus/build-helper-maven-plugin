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
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Store the maven core version in a property <code>maven.version</code>.
 * 
 * @goal maven-version
 * @phase validate
 * @author pgier
 * @since 1.3
 * 
 */
public class MavenVersionMojo
    extends AbstractMojo
{

    private final static String VERSION_PROPERTY = "maven.version";

    /**
     * The Maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     */
    private RuntimeInformation runtime;

    public void execute()
        throws MojoExecutionException
    {
        ArtifactVersion mavenVersion = runtime.getApplicationVersion();

        getLog().debug( "Retrieved maven version: " + mavenVersion.toString() );

        if ( project != null )
        {
            project.getProperties().put( VERSION_PROPERTY, mavenVersion.toString() );
        }
    }

}