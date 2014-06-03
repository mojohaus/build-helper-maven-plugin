package org.codehaus.mojo.buildhelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Sets a property by applying a regex replacement rule to a supplied value.
 * This is similar to regex-property goal with support  for multiple regex settings using
 * RegexPropertyConfig
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
    private List<RegexPropertyConfig> settings = new ArrayList<RegexPropertyConfig>();


    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        for ( RegexPropertyConfig config: settings )
        {
            this.execute( config );
        }
    }

}
