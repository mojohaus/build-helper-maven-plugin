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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Retrieve number of CPUs with project factor, and place it under a configurable project property
 *
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @since 1.9
 */
@Mojo( name = "cpu-count", defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true )
public class CpuCountMojo
    extends AbstractDefinePropertyMojo
{

    /**
     * The name of the property in which to store the CPU count.
     */
    @Parameter( defaultValue = "cpu.count" )
    private String cpuCount;

    /**
     * Projection factor.
     */
    @Parameter( defaultValue = "1.0" )
    private float factor;

    public void execute()
        throws MojoExecutionException
    {
        float count = Runtime.getRuntime().availableProcessors() * factor;
        if ( count < 1 )
        {
            count = 1;
        }

        defineProperty( this.cpuCount, Integer.toString( (int) count ) );
        this.getLog().info( "CPU count: " + this.getProject().getProperties().getProperty( cpuCount ) );
    }
}
