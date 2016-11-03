package org.codehaus.mojo.buildhelper;/*
										* Copyright 2015 the original author or authors.
										*
										* Licensed under the Apache License, Version 2.0 (the "License");
										* you may not use this file except in compliance with the License.
										* You may obtain a copy of the License at
										*
										* http://www.apache.org/licenses/LICENSE-2.0
										*
										* Unless required by applicable law or agreed to in writing, software
										* distributed under the License is distributed on an "AS IS" BASIS,
										* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
										* See the License for the specific language governing permissions and
										* limitations under the License.
										*/

import org.junit.Ignore;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.valueOf;

public class RegexpTest {

	static void print(Pattern p, String version) {
        final Matcher m = p.matcher(version);

        if (m.matches()) {
            Integer major = m.group("major") == null ? 0 : valueOf(m.group("major"));
            Integer minor = m.group("minor") == null ? 0 : valueOf(m.group("minor").substring(1));
            Integer incr = m.group("incr") == null ? 0 : valueOf(m.group("incr").substring(1));
            Integer build = m.group("build") == null ? 0 : valueOf(m.group("build").substring(1));
            String qualifier = m.group("qualifier") == null ? null : m.group("qualifier").replaceFirst("^[.-]", "");

            System.out.printf("Version: %-20s", version);
            System.out.print(" ==> Major: "+major);
            if (minor != null)
                System.out.print(", Minor: "+minor);
            if (incr != null)
                System.out.print(", Incr: "+incr);
            if (build != null)
                System.out.print(", build: "+build);
            if (qualifier != null)
                System.out.print(", Qualifier: "+qualifier);
            System.out.println("");

        }
        else
        {
            System.out.println("NO MATCHING");
        }
    }

	@Test
    @Ignore
	public void testRegexp() {
		Pattern p = Pattern.compile(
				"(?<major>\\d+)?(?<minor>\\.\\d+)?(?<incr>\\.\\d+)?(?<build>-\\d+)?(?<qualifier>[\\.-]?[a-zA-Z][a-zA-Z0-9_-]*)?");

        print(p, "1");
        print(p, "1.2");
        print(p, "1.2.3");
        print(p, "1.2.3-1");
        print(p, "1-SNAPSHOT");
        print(p, "1.2-SNAPSHOT");
        print(p, "1.2.3-SNAPSHOT");
        print(p, "1.2.3-1-SNAPSHOT");
        System.out.println("");
        print(p, "01");
        print(p, "1.02");
        print(p, "1.2.03");
        print(p, "1.2.3-01");
        print(p, "01.02.03-01-SNAPSHOT");


        System.out.println("");
        print(p, "junk");

        System.out.println("");
        print(p, "2.3.4-beta-5");

        System.out.println("");
        print(p, "2.3.4.beta_5");

        System.out.println("");
        print(p, "2.3.4.beta_5");


    }
}
