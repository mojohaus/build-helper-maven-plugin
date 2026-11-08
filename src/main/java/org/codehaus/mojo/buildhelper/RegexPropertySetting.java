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

import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

public class RegexPropertySetting {
    /**
     * The property to set.
     */
    @Parameter(required = true)
    private String name;

    /**
     * The pre-transformation value.
     */
    @Parameter(required = true)
    private String value;

    /**
     * The regex to replace. Mutually exclusive with {@code replacements}.
     */
    @Parameter
    private String regex;

    /**
     * An ordered list of regex and replacement pairs, applied in sequence, each to the result of the previous one.
     * Mutually exclusive with {@code regex} and {@code replacement}.
     *
     * @since 3.8.0
     */
    @Parameter
    private List<RegexReplacement> replacements = new ArrayList<>();

    /**
     * The replacement.
     */
    @Parameter(defaultValue = "")
    private String replacement = "";

    /**
     * Whether to treat the replacement as a literal string. If true, backslashes and dollar signs have no special meaning
     * in the replacement.
     *
     * @since 3.6.2
     */
    @Parameter(defaultValue = "false")
    private boolean replacementLiteral;

    /**
     * Whether to fail if no match is found.
     */
    @Parameter(defaultValue = "true")
    private boolean failIfNoMatch = true;

    /**
     * Change the case of the value to upper case if set to true.
     */
    @Parameter(defaultValue = "false")
    private boolean toUpperCase = false;

    /**
     * Change the case of the value to lower case if set to true.
     */
    @Parameter(defaultValue = "false")
    private boolean toLowerCase = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public List<RegexReplacement> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<RegexReplacement> replacements) {
        this.replacements = replacements == null ? new ArrayList<>() : replacements;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public boolean isReplacementLiteral() {
        return replacementLiteral;
    }

    public void setReplacementLiteral(boolean replacementLiteral) {
        this.replacementLiteral = replacementLiteral;
    }

    public boolean isFailIfNoMatch() {
        return failIfNoMatch;
    }

    public void setFailIfNoMatch(boolean failIfNoMatch) {
        this.failIfNoMatch = failIfNoMatch;
    }

    public boolean isToUpperCase() {
        return toUpperCase;
    }

    public void setToUpperCase(boolean toUpperCase) {
        this.toUpperCase = toUpperCase;
    }

    public boolean isToLowerCase() {
        return toLowerCase;
    }

    public void setToLowerCase(boolean toLowerCase) {
        this.toLowerCase = toLowerCase;
    }

    public void validate() {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name required");
        }

        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("value required");
        }

        if (replacements != null && !replacements.isEmpty()) {
            if (!StringUtils.isEmpty(regex) || !StringUtils.isEmpty(replacement)) {
                throw new IllegalArgumentException(
                        "either replacements or regex/replacement can be set, but not both.");
            }
            for (RegexReplacement entry : replacements) {
                if (entry == null || StringUtils.isBlank(entry.getRegex())) {
                    throw new IllegalArgumentException("regex required in each of the replacements");
                }
            }
        } else if (StringUtils.isBlank(regex)) {
            throw new IllegalArgumentException("regex or replacements required");
        }

        if (toLowerCase && toUpperCase) {
            throw new IllegalArgumentException("either toUpperCase or toLowerCase can be set, but not both.");
        }
    }
}
