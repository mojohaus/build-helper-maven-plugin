package org.codehaus.mojo.buildhelper;

import org.apache.maven.api.di.Inject;
import org.apache.maven.api.plugin.Log;
import org.apache.maven.api.plugin.Mojo;

public abstract class AbstractMojo implements Mojo {

    @Inject
    Log log;

    public Log getLog() {
        return log;
    }
}
