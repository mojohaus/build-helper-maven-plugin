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

    @Test
    void appliesReplacementsInOrder() throws Exception {
        config.setValue("1.0-SNAPSHOT");
        config.setRegex(null);
        config.setReplacements(Arrays.asList(
                new RegexReplacement("-SNAPSHOT", "-RC"),
                new RegexReplacement("\\.", "_"),
                new RegexReplacement("RC$", "$0$0")));

        assertReplacement("1_0-RCRC");
    }

    @Test
    void laterReplacementSeesTheResultOfTheEarlierOne() throws Exception {
        config.setValue("a");
        config.setRegex(null);
        config.setReplacements(Arrays.asList(new RegexReplacement("a", "b"), new RegexReplacement("b", "c")));

        assertReplacement("c");
    }

    @Test
    void replacementsHonourFailIfNoMatchPerEntry() {
        config.setValue("a");
        config.setRegex(null);
        config.setReplacements(Arrays.asList(new RegexReplacement("a", "b"), new RegexReplacement("zzz", "c")));

        MojoFailureException e = assertThrows(MojoFailureException.class, () -> mojo.execute(config));
        assertEquals("No match to regex 'zzz' found in 'b'.", e.getMessage());
    }

    @Test
    void replacementsSkipNonMatchingEntryWhenAllowed() throws Exception {
        config.setValue("a");
        config.setRegex(null);
        config.setFailIfNoMatch(false);
        config.setReplacements(Arrays.asList(new RegexReplacement("zzz", "c"), new RegexReplacement("a", "b")));

        assertReplacement("b");
    }

    @Test
    void replacementsAndRegexAreMutuallyExclusive() {
        config.setReplacements(Arrays.asList(new RegexReplacement("a", "b")));

        MojoExecutionException e = assertThrows(MojoExecutionException.class, () -> mojo.execute(config));
        assertEquals("either replacements or regex/replacement can be set, but not both.", e.getMessage());
    }

    @Test
    void replacementsAndReplacementAreMutuallyExclusive() {
        config.setRegex(null);
        config.setReplacement("x");
        config.setReplacements(Arrays.asList(new RegexReplacement("a", "b")));

        assertThrows(MojoExecutionException.class, () -> mojo.execute(config));
    }

    @Test
    void replacementEntryNeedsARegex() {
        config.setRegex(null);
        config.setReplacements(Arrays.asList(new RegexReplacement(" ", "b")));

        assertThrows(MojoExecutionException.class, () -> mojo.execute(config));
    }

    @Test
    void regexOrReplacementsIsRequired() {
        config.setRegex(null);

        MojoExecutionException e = assertThrows(MojoExecutionException.class, () -> mojo.execute(config));
        assertEquals("regex or replacements required", e.getMessage());
    }

    private void assertReplacement(String expected) throws Exception {
        mojo.execute(config);

        assertEquals(expected, mojo.getProject().getProperties().getProperty("result"));
    }
}
