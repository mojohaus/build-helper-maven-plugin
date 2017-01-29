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
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.mappers.MapperException;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.util.StringUtils;

/**
 * Abstract base for setting properties according to whether sets of source and object resources are respectively up to
 * date with each other.
 *
 * @author Adrian Price <a href="mailto:demonfiddler@virginmedia.com">demonfiddler@virginmedia.com</a>
 * @since 1.12
 */
abstract class AbstractUpToDatePropertyMojo
    extends AbstractDefinePropertyMojo
{
    protected AbstractUpToDatePropertyMojo()
    {
    }

    protected void execute( UpToDatePropertySetting config )
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            config.validate();
        }
        catch ( IllegalArgumentException e )
        {
            throw new MojoExecutionException( "Invalid UpToDateProperty configuration", e );
        }

        // Check that all target file(s) are up to date with respect to their corresponding source files.
        boolean upToDate = true;
        FileSet fileSet = config.getFileSet();
        if ( fileSet != null )
        {
            try
            {
                FileSetManager fileSetManager = new FileSetManager( getLog() );
                Map<String, String> includedFiles = fileSetManager.mapIncludedFiles( fileSet );

                // Treat a file set that yields no files as intrinsically out of date.
                upToDate = !includedFiles.isEmpty();

                for ( Entry<String, String> entry : includedFiles.entrySet() )
                {
                    // If targetFile is out of date WRT srcFile, note the fact and stop processing.
                    File srcFile = getFile( fileSet, false, entry.getKey() );
                    File targetFile = getFile( fileSet, true, entry.getValue() );
                    upToDate = isUpToDate( srcFile, targetFile );

                    if ( getLog().isDebugEnabled() )
                    {
                        try
                        {
                            StringBuilder msg = new StringBuilder( targetFile.getCanonicalPath() );
                            if ( !targetFile.exists() )
                                msg.append( " (nonexistent)" );
                            msg.append( "\n\tis " ).append( upToDate ? "up to date"
                                            : "out of date" ).append( " with respect to \n\t" ).append( srcFile.getCanonicalPath() );
                            if ( !srcFile.exists() )
                                msg.append( " (nonexistent)" );

                            getLog().debug( msg );
                        }
                        catch ( IOException e )
                        {
                            // Just a log entry so not fatal.
                        }
                    }

                    if ( !upToDate )
                        break;
                }
            }
            catch ( MapperException e )
            {
                throw new MojoExecutionException( "", e );
            }
        }

        // Set the property to the appropriate value, depending on whether target files are up to date WRT source files.
        if ( upToDate )
            defineProperty( config.getName(), config.getValue().trim() );
        else if ( !StringUtils.isBlank( config.getElse() ) )
            defineProperty( config.getName(), config.getElse().trim() );
    }

    private File getFile( FileSet fileSet, boolean useOutputDirectory, String path )
    {
        String baseDir = useOutputDirectory && !StringUtils.isBlank( fileSet.getOutputDirectory() )
                        ? fileSet.getOutputDirectory() : fileSet.getDirectory();
        return path == null ? null : new File( baseDir, path );
    }

    private boolean isUpToDate( File srcFile, File targetFile )
    {
        return srcFile != null && srcFile.exists()
            && ( targetFile == null || srcFile.lastModified() <= targetFile.lastModified() );
    }
}
