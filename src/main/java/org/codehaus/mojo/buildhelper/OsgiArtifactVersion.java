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

import java.util.StringTokenizer;

import org.apache.maven.artifact.versioning.ArtifactVersion;

/**
 * Default implementation of artifact versioning.
 *
 */
public class OsgiArtifactVersion
    implements ArtifactVersion
{
    private Integer majorVersion;

    private Integer minorVersion;

    private Integer incrementalVersion;

    private Integer buildNumber;

    private String qualifier;

    public OsgiArtifactVersion( String version )
    {
        parseVersion( version );
    }

    public int compareTo( Object o )
    {
        OsgiArtifactVersion otherVersion = (OsgiArtifactVersion) o;

        int result = compareIntegers( majorVersion, otherVersion.majorVersion );
        if ( result == 0 )
        {
            result = compareIntegers( minorVersion, otherVersion.minorVersion );
        }
        if ( result == 0 )
        {
            result = compareIntegers( incrementalVersion, otherVersion.incrementalVersion );
        }
        if ( result == 0 )
        {
            if ( buildNumber != null || otherVersion.buildNumber != null )
            {
                result = compareIntegers( buildNumber, otherVersion.buildNumber );
            }
            else if ( qualifier != null )
            {
                if ( otherVersion.qualifier != null )
                {
                    if ( qualifier.length() > otherVersion.qualifier.length() &&
                        qualifier.startsWith( otherVersion.qualifier ) )
                    {
                        // here, the longer one that otherwise match is considered older
                        result = -1;
                    }
                    else if ( qualifier.length() < otherVersion.qualifier.length() &&
                        otherVersion.qualifier.startsWith( qualifier ) )
                    {
                        // here, the longer one that otherwise match is considered older
                        result = 1;
                    }
                    else
                    {
                        result = qualifier.compareTo( otherVersion.qualifier );
                    }
                }
                else
                {
                    // otherVersion has no qualifier but we do - that's newer
                    result = -1;
                }
            }
            else if ( otherVersion.qualifier != null )
            {
                // otherVersion has a qualifier but we don't, we're newer
                result = 1;
            }
        }
        return result;
    }

    private int compareIntegers( Integer i1, Integer i2 )
    {
        if ( i1 == null ? i2 == null : i1.equals( i2 ) )
        {
            return 0;
        }
        else if ( i1 == null )
        {
            return -1;
        }
        else if ( i2 == null )
        {
            return 1;
        }
        else
        {
            return i1.intValue() - i2.intValue();
        }
    }

    public int getMajorVersion()
    {
        return majorVersion != null ? majorVersion.intValue() : 0;
    }

    public int getMinorVersion()
    {
        return minorVersion != null ? minorVersion.intValue() : 0;
    }

    public int getIncrementalVersion()
    {
        return incrementalVersion != null ? incrementalVersion.intValue() : 0;
    }

    public int getBuildNumber()
    {
        return buildNumber != null ? buildNumber.intValue() : 0;
    }

    public String getQualifier()
    {
        return qualifier;
    }

    public void parseVersion( String version )
    {
        boolean fallback = false;
        
        if ( version.indexOf( '.' ) == -1 )
        {
            fallback = true;
        }
        else
        {
        
            StringTokenizer tok = new StringTokenizer( version, "." );
            try
            {
                majorVersion = getNextIntegerToken( tok );
                if ( tok.hasMoreTokens() )
                {
                    minorVersion = getNextIntegerToken( tok );
                }
                if ( tok.hasMoreTokens() )
                {
                    incrementalVersion = getNextIntegerToken( tok );
                }
                if ( tok.hasMoreTokens() )
                {
                    qualifier = tok.nextToken();
                }
            }
            catch ( NumberFormatException e )
            {
                fallback = true;
            }
        }
        if ( fallback )
        {
            // qualifier is the whole version
            qualifier = version;
            majorVersion = null;
            minorVersion = null;
            incrementalVersion = null;
        }
        
    }

    private static Integer getNextIntegerToken( StringTokenizer tok )
    {
        String s = tok.nextToken();
        if ( s.length() > 1 && s.startsWith( "0" ) )
        {
            throw new NumberFormatException( "Number part has a leading 0: '" + s + "'" );
        }
        return Integer.valueOf( s );
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        if ( majorVersion != null )
        {
            buf.append( majorVersion );
        }
        if ( minorVersion != null )
        {
            buf.append( "." );
            buf.append( minorVersion );
        }
        if ( incrementalVersion != null )
        {
            buf.append( "." );
            buf.append( incrementalVersion );
        }
        if ( qualifier != null )
        {
            if ( buf.length() > 0 )
            {
                buf.append( "." );
            }
            buf.append( qualifier );
        }
        return buf.toString();
    }
}
