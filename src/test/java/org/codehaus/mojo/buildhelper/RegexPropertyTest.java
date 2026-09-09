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

import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegexPropertyTest {
    private RegexPropertyMojo mojo;
    private RegexPropertySetting config;

    @BeforeEach
    void setUp() {
        mojo = new RegexPropertyMojo();
        mojo.project = new MavenProject();
        config = new RegexPropertySetting();
        config.setName("result");
        config.setValue("foo foo");
        config.setRegex("(foo)");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(
            strings = {"C:\\projects\\foobar\\target", "$1", "${name}", "C:\\$1\\target\\", "\\", "$", "plain text"})
    void preservesLiteralReplacements(String replacement) throws Exception {
        config.setReplacement(replacement);
        config.setReplacementLiteral(true);

        String literal = replacement == null ? "" : replacement;
        assertReplacement(literal + " " + literal);
    }

    @Test
    void interpretsReplacementSyntaxByDefault() throws Exception {
        config.setReplacement("\\$1-$1");

        assertReplacement("$1-foo $1-foo");
    }

    @Test
    void interpretsReplacementSyntaxWhenLiteralIsFalse() throws Exception {
        config.setReplacement("\\$1-$1");
        config.setReplacementLiteral(false);

        assertReplacement("$1-foo $1-foo");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void removesMatchesWithEmptyReplacementByDefault(String replacement) throws Exception {
        config.setReplacement(replacement);

        assertReplacement(" ");
    }

    @Test
    void preservesEmptyMatchBehaviorWithLiteralReplacement() throws Exception {
        config.setValue("foo");
        config.setRegex(".*");
        config.setReplacement("C:\\target");
        config.setReplacementLiteral(true);

        assertReplacement("C:\\targetC:\\target");
    }

    private void assertReplacement(String expected) throws Exception {
        mojo.execute(config);

        assertEquals(expected, mojo.getProject().getProperties().getProperty("result"));
    }
}
