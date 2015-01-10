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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Sets a property by applying a regex replacement rule to a supplied value. This is similar to regex-property goal with
 * support for multiple regex settings using RegexPropertyConfig
 *
 * @since 1.9
 */
@Mojo( name = "regex-properties", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true )
public class RegexPropertiesMojo
    extends AbstractRegexPropertyMojo
{
    /**
     * List of RegexPropertyConfig to apply the regex
     */
    @Parameter( required = false )
    private List<RegexPropertySetting> regexPropertySettings = new ArrayList<RegexPropertySetting>();

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        for ( RegexPropertySetting setting : regexPropertySettings )
        {
            this.execute( setting );
        }
    }

}
