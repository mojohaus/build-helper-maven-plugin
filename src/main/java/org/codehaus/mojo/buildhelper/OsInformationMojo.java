package org.codehaus.mojo.buildhelper;

/*
 * The MIT License
 * 
 * Copyright (c) 2004, The Codehaus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.Os;

/**
 * Set the os properties which contain name, family, version and architecture
 * of the operation system.
 *
 * <pre>
 *   [propertyPrefix].name
 *   [propertyPrefix].family
 *   [propertyPrefix].version
 *   [propertyPrefix].arch
 * </pre>
 * 
 * Where the propertyPrefix is the string set in the mojo parameter.
 *
 * 
 * @author <a href="kama@soebes.de">Karl-Heinz Marbaise</a>
 * @since 1.9
 */
@Mojo( name = "os", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true )
public class OsInformationMojo
    extends AbstractDefinePropertyMojo
{
    /**
     * Prefix string to use for the set of the operation system properties.
     */
    @Parameter( defaultValue = "os" )
    private String propertyPrefix;

    private void definePropertyWithPrefix ( String name, String value )
    {
        defineProperty( propertyPrefix + '.' + name, value );
    }

    public void execute()
        throws MojoExecutionException
    {

    	definePropertyWithPrefix ( "name", Os.OS_NAME);
    	definePropertyWithPrefix ( "family", Os.OS_FAMILY);
    	definePropertyWithPrefix ( "version", Os.OS_VERSION);
    	definePropertyWithPrefix ( "arch", Os.OS_ARCH);

    }
}
