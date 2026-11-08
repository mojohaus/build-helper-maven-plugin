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

import java.lang.reflect.Field;
import java.util.Arrays;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    /** Was the regex-invalid IT. */
    @Test
    void invalidRegexFailsTheBuild() {
        config.setValue("1-SNAPSHOT");
        config.setRegex("-S(NAPSHOT");
        config.setReplacement("-$1");
        config.setFailIfNoMatch(false);

        assertThrows(MojoExecutionException.class, () -> mojo.execute(config));
    }

    /** Was the regex-nomatch IT. */
    @Test
    void noMatchFailsWhenRequired() {
        config.setValue("1-SNAPSHOT");
        config.setRegex("NOT HERE");
        config.setReplacement("Substitute");
        config.setFailIfNoMatch(true);

        MojoFailureException e = assertThrows(MojoFailureException.class, () -> mojo.execute(config));
        assertEquals("No match to regex 'NOT HERE' found in '1-SNAPSHOT'.", e.getMessage());
    }

    /** Was the regex-no-match-keep-original-value-mbuildhelper-54 IT. */
    @Test
    void noMatchKeepsTheOriginalValue() throws Exception {
        config.setValue("originalvalue");
        config.setRegex("nomatch");
        config.setReplacement("");
        config.setFailIfNoMatch(false);

        assertReplacement("originalvalue");
    }

    /** Was the regex-replacement-literal and regex-multi ITs: the regex-properties goal applies every setting. */
    @Test
    void regexPropertiesAppliesEverySetting() throws Exception {
        RegexPropertiesMojo multi = new RegexPropertiesMojo();
        multi.project = new MavenProject();
        set(
                multi,
                "regexPropertySettings",
                Arrays.asList(
                        setting("multiple.literal", "foo", "^(.*)$", "C:\\projects\\$1\\target\\", true),
                        setting("multiple.default", "foo", "^(.*)$", "$1-bar", false),
                        setting("build.version", "1-SNAPSHOT", "-SNAPSHOT", "-RC", false)));

        multi.execute();

        assertEquals(
                "C:\\projects\\$1\\target\\", multi.getProject().getProperties().getProperty("multiple.literal"));
        assertEquals("foo-bar", multi.getProject().getProperties().getProperty("multiple.default"));
        assertEquals("1-RC", multi.getProject().getProperties().getProperty("build.version"));
    }

    /** Was the regex-replacement-literal IT: a literal Windows path, a group reference and an empty literal. */
    @Test
    void singleSettingsFromTheLiteralReplacementIt() throws Exception {
        config.setValue("foo");
        config.setRegex("^(.*)$");
        config.setReplacement("C:\\projects\\$1\\target\\");
        config.setReplacementLiteral(true);
        assertReplacement("C:\\projects\\$1\\target\\");

        config.setValue("foo");
        config.setReplacement("$1-bar");
        config.setReplacementLiteral(false);
        assertReplacement("foo-bar");

        config.setValue("foo");
        config.setRegex("^.*$");
        config.setReplacement(null);
        config.setReplacementLiteral(true);
        assertReplacement("");
    }

    private static RegexPropertySetting setting(
            String name, String value, String regex, String replacement, boolean literal) {
        RegexPropertySetting setting = new RegexPropertySetting();
        setting.setName(name);
        setting.setValue(value);
        setting.setRegex(regex);
        setting.setReplacement(replacement);
        setting.setReplacementLiteral(literal);
        setting.setFailIfNoMatch(false);
        return setting;
    }

    private static void set(Object target, String name, Object value) throws ReflectiveOperationException {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private void assertReplacement(String expected) throws Exception {
        mojo.execute(config);

        assertEquals(expected, mojo.getProject().getProperties().getProperty("result"));
    }
}
