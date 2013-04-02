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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

/**
 * Set the java properties which contain the information about java runtime, java specification etc. which has been used
 * during the build.
 * 
 * <pre>
 *   [propertyPrefix].java.runtime.name
 *   [propertyPrefix].java.runtime.version
 *   [propertyPrefix].java.specification.name
 *   [propertyPrefix].java.specification.vendor
 *   [propertyPrefix].java.specification.version
 *   [propertyPrefix].java.version
 *   [propertyPrefix].java.vendor
 *   [propertyPrefix].java.vm.name
 *   [propertyPrefix].java.vm.info
 *   [propertyPrefix].java.vm.version
 *   [propertyPrefix].java.vm.vendor
 *   [propertyPrefix].java.vm.specification.name
 *   [propertyPrefix].java.vm.specification.vendor
 *   [propertyPrefix].java.vm.specification.version
 *   [propertyPrefix].sun.management.compiler
 * </pre>
 * 
 * Where the propertyPrefix is the string set in the mojo parameter.
 * 
 * @author <a href="codehaus@soebes.de">Karl-Heinz Marbaise</a>
 * @since 1.9
 */
@Mojo( name = "java", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true )
public class JavaInformationMojo
    extends AbstractDefinePropertyMojo
{

    private enum BuildEnvironment
    {
        JAVA_RUNTIME_NAME( "java.runtime.name" ),
        JAVA_RUNTIME_VERSION( "java.runtime.version" ),

        JAVA_SPECIFICATION_NAME( "java.specification.name" ),
        JAVA_SPECIFICATION_VENDOR( "java.specification.vendor" ),
        JAVA_SPECIFICATION_VERSION( "java.specification.version" ),

        JAVA_VENDOR( "java.vendor" ),
        JAVA_VERSION( "java.version" ),

        JAVA_VM_NAME( "java.vm.name" ),
        JAVA_VM_INFO( "java.vm.info" ),
        JAVA_VM_VENDOR( "java.vm.vendor" ),
        JAVA_VM_VERSION( "java.vm.version" ),

        JAVA_VM_SPECIFICATION_NAME( "java.vm.specification.name" ),
        JAVA_VM_SPECIFICATION_VENDOR( "java.vm.specification.vendor" ),
        JAVA_VM_SPECIFICATION_VERSION( "java.vm.specification.version" ),

        SUN_MANAGEMENT_COMPILER( "sun.management.compiler" );

        private String value;

        public String getValue()
        {
            return this.value;
        }

        private BuildEnvironment( String name )
        {
            this.value = name;
        }
    }

    /**
     * The maven session
     */
    @Component
    private MavenSession session;

    /**
     * Prefix string to use for the set of the operation system properties.
     */
    @Parameter( defaultValue = "build.environment" )
    private String propertyPrefix;

    private void definePropertyWithPrefix( String name, String value )
    {
        defineProperty( propertyPrefix + '.' + name, value );
    }

    private String getPropertyIfExists( final Properties properties, final String propertyName )
    {
        final String value = properties.getProperty( propertyName );
        // @TODO: Think about the value in case of an not defined property? Better suggestions?
        String result = "";
        if ( StringUtils.isNotBlank( value ) )
        {
            result = value;
        }

        return result;
    }

    public void execute()
        throws MojoExecutionException
    {

        final Properties executionProperties = session.getExecutionProperties();
        for ( BuildEnvironment item : BuildEnvironment.values() )
        {
            definePropertyWithPrefix( item.getValue(), getPropertyIfExists( executionProperties, item.getValue() ) );
        }
    }
}
