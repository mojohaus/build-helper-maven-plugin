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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public abstract class AbstractRegexPropertyMojo extends AbstractDefinePropertyMojo {

    protected void execute(RegexPropertySetting config) throws MojoExecutionException, MojoFailureException {
        try {
            config.validate();
        } catch (IllegalArgumentException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

        List<RegexReplacement> steps = config.getReplacements();
        if (steps == null || steps.isEmpty()) {
            steps = Collections.singletonList(new RegexReplacement(config.getRegex(), config.getReplacement()));
        }

        String value = config.getValue();
        for (RegexReplacement step : steps) {
            value = applyReplacement(config, step, value);
        }
        config.setValue(value);

        if (config.isToLowerCase()) {
            config.setValue(config.getValue().toLowerCase(Locale.getDefault()));
        }

        if (config.isToUpperCase()) {
            config.setValue(config.getValue().toUpperCase(Locale.getDefault()));
        }

        defineProperty(config.getName(), config.getValue());
    }

    private String applyReplacement(RegexPropertySetting config, RegexReplacement step, String value)
            throws MojoExecutionException, MojoFailureException {
        Pattern pattern;
        try {
            pattern = Pattern.compile(step.getRegex());
        } catch (PatternSyntaxException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            String replacement = step.getReplacement() == null ? "" : step.getReplacement();
            if (config.isReplacementLiteral()) {
                replacement = Matcher.quoteReplacement(replacement);
            }
            return matcher.replaceAll(replacement);
        }
        if (config.isFailIfNoMatch()) {
            throw new MojoFailureException("No match to regex '" + step.getRegex() + "' found in '" + value + "'.");
        }
        getLog().info("No match to regex '" + step.getRegex() + "' found in '" + value + "'. " + "The initial value '"
                + value + "' is left as-is...");
        return value;
    }
}
