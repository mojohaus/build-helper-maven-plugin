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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * A utility class for managing test resources.
 *
 * @author Adrian Price <a href="mailto:demonfiddler@virginmedia.com">demonfiddler@virginmedia.com</a>
 * @since 1.12
 */
public final class FixtureUtils
{
    /**
     * Creates test resources defined by the specification file <code>fixture.properties</code>. The method creates all
     * the files named by the properties, optionally setting the modification timestamp by adding the number of seconds
     * specified by the property value (which must be an integer) to the current system time.
     * 
     * @param basePath The path to the base directory against which to resolve relative paths (including
     *            <code>fixture.properties</code>).
     * @throws IOException If unable to create a file or a missing parent directory.
     * @throws NumberFormatException If a non-empty property value cannot be parsed as an integer.
     */
    public static void createResources( String basePath )
        throws IOException
    {
        File baseDir = new File( basePath );
        createResources( baseDir, "fixture.properties", "UTF-8", System.currentTimeMillis() );
    }

    /**
     * Creates test resources defined by a specification file. The method creates all the files named by the properties,
     * optionally setting the modification timestamp by adding the number of seconds specified by the property value
     * (which must be an integer) to a base timestamp.
     * 
     * @param baseDir The base directory against which to resolve relative paths.
     * @param propertiesPath The path (relative or absolute) to a <code>Properties</code> file whose property names are
     *            file paths (relative or absolute) and whose optional property values represent relative modification
     *            timestamps in seconds.
     * @param baseTimestamp The base timestamp to use, in milliseconds since midnight on the 1st January 1970 GMT.
     * @param encoding Encoding.
     * @throws IOException If unable to create a file or a missing parent directory.
     * @throws NumberFormatException If a non-empty property value cannot be parsed as an integer.
     */
    public static void createResources( File baseDir, String propertiesPath, String encoding, long baseTimestamp )
        throws IOException
    {
        createResources( baseDir, new File( baseDir, propertiesPath ), encoding, baseTimestamp );
    }

    /**
     * Creates test resources defined by a specification file. The method creates all the files named by the properties,
     * optionally setting the modification timestamp by adding the number of seconds specified by the property value
     * (which must be an integer) to a base timestamp.
     * 
     * @param baseDir The base directory against which to resolve relative paths.
     * @param propertiesFile A <code>Properties</code> file whose property names are file paths (relative or absolute)
     *            and whose optional property values represent relative modification timestamps in seconds.
     * @param encoding Encoding.
     * @param baseTimestamp The base timestamp to use, in milliseconds since midnight on the 1st January 1970 GMT.
     * @throws IOException If unable to create a file or a missing parent directory.
     * @throws NumberFormatException If a non-empty property value cannot be parsed as an integer.
     */
    public static void createResources( File baseDir, File propertiesFile, String encoding, long baseTimestamp )
        throws IOException
    {
        Properties properties = new Properties();
        properties.load( new FileReader( propertiesFile ) );
        createResources( baseDir, properties, baseTimestamp );
    }

    /**
     * Creates test resources defined by a specification file. The method creates all the files named by the properties,
     * optionally setting the modification timestamp by adding the number of seconds specified by the property value
     * (which must be an integer) to a base timestamp.
     * 
     * @param baseDir The base directory against which to resolve relative paths.
     * @param properties A <code>Properties</code> object whose property names are file paths (relative or absolute) and
     *            whose optional property values represent relative modification timestamps in seconds.
     * @param baseTimestamp The base timestamp to use, in milliseconds since midnight on the 1st January 1970 GMT.
     * @throws IOException If unable to create a file or a missing parent directory.
     * @throws NumberFormatException If a non-empty property value cannot be parsed as an integer.
     */
    public static void createResources( File baseDir, Properties properties, long baseTimestamp )
        throws IOException
    {
        for ( String propertyName : properties.stringPropertyNames() )
        {
            File file = new File( baseDir, propertyName );
            File parent = file.getParentFile();
            if ( parent != null )
                parent.mkdirs();
            file.createNewFile();

            // If required, set the last modification timestamp.
            String propertyValue = properties.getProperty( propertyName );
            if ( !propertyValue.isEmpty() )
            {
                int offset = Integer.parseInt( propertyValue );
                long ts = baseTimestamp + ( offset * 1000 );
                file.setLastModified( ts );
                assert file.lastModified() == ts : "failed to set last modified timestamp for "
                    + file.getCanonicalPath();
            }
        }
    }

    private FixtureUtils()
    {
    }
}
