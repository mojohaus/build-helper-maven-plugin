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
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ResourceLock(Resources.TIME_ZONE)
class TimestampPropertyMojoTest {
    private TimeZone originalTimeZone;
    private TimestampPropertyMojo mojo;

    @BeforeEach
    void setUp() throws Exception {
        originalTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
        mojo = new TimestampPropertyMojo();
        mojo.project = new MavenProject();
        setParameter("name", "build.date");
        setParameter("pattern", "yyyy-MM-dd");
        setParameter("locale", "en");
        setParameter("timeSource", "build");
        DefaultMavenExecutionRequest request = new DefaultMavenExecutionRequest();
        request.setStartTime(Date.from(Instant.parse("2021-02-04T01:00:00Z")));
        setParameter("mavenSession", new MavenSession(null, null, request, new DefaultMavenExecutionResult()));
    }

    @AfterEach
    void restoreTimeZone() {
        TimeZone.setDefault(originalTimeZone);
    }

    @ParameterizedTest
    @ValueSource(strings = {"system", "SYSTEM", "SyStEm"})
    void usesSystemTimeZoneForBuildDate(String timeZone) throws Exception {
        setParameter("timeZone", timeZone);

        assertTimestamp("2021-02-03");
    }

    @Test
    void usesSystemTimeZoneForCurrentTime() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+14:00"));
        setParameter("timeZone", "system");
        setParameter("timeSource", "current");
        setParameter("pattern", "XXX");

        assertTimestamp("+14:00");
    }

    @Test
    void readsSystemTimeZoneAtExecution() throws Exception {
        setParameter("timeZone", "system");
        assertTimestamp("2021-02-03");

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+14:00"));

        assertTimestamp("2021-02-04");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"GMT", "UTC", "invalid-time-zone"})
    void preservesGmtFallback(String timeZone) throws Exception {
        setParameter("timeZone", timeZone);

        assertTimestamp("2021-02-04");
    }

    @ParameterizedTest
    @CsvSource({"America/Los_Angeles, 2021-02-03 17:00 -08:00", "Asia/Tokyo, 2021-02-04 10:00 +09:00"})
    void usesExplicitTimeZone(String timeZone, String expected) throws Exception {
        setParameter("timeZone", timeZone);
        setParameter("pattern", "yyyy-MM-dd HH:mm XXX");

        assertTimestamp(expected);
    }

    private void assertTimestamp(String expected) throws Exception {
        mojo.execute();

        assertEquals(expected, mojo.getProject().getProperties().getProperty("build.date"));
    }

    private void setParameter(String name, Object value) throws Exception {
        Field field = TimestampPropertyMojo.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(mojo, value);
    }
}
