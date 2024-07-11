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

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.maven.api.Session;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.plugin.MojoException;
import org.apache.maven.api.plugin.annotations.Mojo;
import org.apache.maven.api.plugin.annotations.Parameter;
import org.apache.maven.api.settings.Settings;

/**
 * Define one or many properties as a result of a Beanshell script invocation. Like
 * <a href="http://docs.codehaus.org/display/GMAVEN/Executing+Groovy+Code">gmaven-plugin</a>, some variables are
 * defined:
 * <ul>
 * <li><code>project</code>: the actual Maven project,</li>
 * <li><code>session</code>: the executing <code>Session</code>,</li>
 * <li><code>settings</code>: the executing <code>Settings</code>.</li>
 * <li><code>log</code>: the logger of the Mojo (see {@link AbstractMojo#getLog()}).</li>
 * </ul>
 *
 * @author Hervé Boutemy
 * @since 1.8
 */
@Mojo(name = "bsh-property", defaultPhase = "validate")
public class BeanshellPropertyMojo extends AbstractDefinePropertyMojo {
    @Parameter(required = true)
    private String source;

    /**
     * List of property names to get from script context after execution. Can be omitted if no property needs to be
     * defined, just the script execution.
     */
    @Parameter
    private String[] properties;

    /**
     * The Maven Session.
     */
    @Inject
    private Session session;

    /**
     * The Maven Settings.
     */
    @Parameter(readonly = true, defaultValue = "${session.settings}")
    private Settings settings;

    /**
     * Main plugin execution
     */
    public void execute() throws MojoException {
        Interpreter interpreter = new Interpreter();

        set(interpreter, "project", getProject());
        set(interpreter, "session", session);
        set(interpreter, "settings", settings);
        set(interpreter, "log", getLog());

        try {
            interpreter.eval(source);
        } catch (EvalError ee) {
            MojoException mfe = new MojoException("error during Beanshell script execution: " + ee.getMessage());
            mfe.initCause(ee);
            throw mfe;
        }

        if (properties != null) {
            for (String property : properties) {
                Object value;
                try {
                    value = interpreter.get(property);

                    if (value != null) {
                        defineProperty(property, value.toString());
                    }
                } catch (EvalError ee) {
                    MojoException mfe = new MojoException(
                            "cannot get Beanshell global variable '" + property + "': " + ee.getMessage());
                    mfe.initCause(ee);
                    throw mfe;
                }
            }
        }
    }

    private void set(Interpreter interpreter, String name, Object value) throws MojoException {
        try {
            interpreter.set(name, value);
        } catch (EvalError ee) {
            MojoException mfe =
                    new MojoException("cannot define Beanshell global variable '" + name + "': " + ee.getMessage());
            mfe.initCause(ee);
            throw mfe;
        }
    }
}
