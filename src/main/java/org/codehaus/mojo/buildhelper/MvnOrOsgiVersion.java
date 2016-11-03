package org.codehaus.mojo.buildhelper;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.valueOf;

public class MvnOrOsgiVersion implements ArtifactVersion {
    Pattern p = Pattern.compile(
            "(?<major>\\d+)?(?<minor>\\.\\d+)?(?<incr>\\.\\d+)?(?<build>-\\d+)?(?<qualifier>[\\.-]?[a-zA-Z][a-zA-Z0-9_-]*)?");
	private Integer majorVersion;
	private Integer minorVersion;
	private Integer incrementalVersion;
	private Integer buildNumber;
	private String qualifier;
	private ComparableVersion comparable;

	public MvnOrOsgiVersion(String version) {
		parseVersion(version);
	}

	@Override
	public int hashCode() {
		return 11 + comparable.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof ArtifactVersion)) {
			return false;
		}

		return compareTo(other) == 0;
	}

	public int compareTo(Object o) {
		MvnOrOsgiVersion otherVersion = (MvnOrOsgiVersion) o;
		return this.comparable.compareTo(otherVersion.comparable);
	}

	public int getMajorVersion() {
		return majorVersion != null ? majorVersion : 0;
	}

	public int getMinorVersion() {
		return minorVersion != null ? minorVersion : 0;
	}

	public int getIncrementalVersion() {
		return incrementalVersion != null ? incrementalVersion : 0;
	}

	public int getBuildNumber() {
		return buildNumber != null ? buildNumber : 0;
	}

	public String getQualifier() {
		return qualifier;
	}

	public final void parseVersion(String version) {
		comparable = new ComparableVersion(version);
        final Matcher m = p.matcher(version);

        if (m.matches()) {
            this.majorVersion = m.group("major") == null ? null : valueOf(m.group("major"));
            this.minorVersion = m.group("minor") == null ? null : valueOf(m.group("minor").substring(1));
            this.incrementalVersion = m.group("incr") == null ? null : valueOf(m.group("incr").substring(1));
            this.buildNumber = m.group("build") == null ? null : valueOf(m.group("build").substring(1));
            this.qualifier = m.group("qualifier") == null ? null : m.group("qualifier").replaceFirst("^[.-]", "");
        }
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		if (majorVersion != null) {
			buf.append(majorVersion);
		}
		if (minorVersion != null) {
			buf.append(".");
			buf.append(minorVersion);
		}
		if (incrementalVersion != null) {
			buf.append(".");
			buf.append(incrementalVersion);
		}
		if (buildNumber != null) {
			buf.append("-");
			buf.append(buildNumber);
		}
		else if (qualifier != null) {
			if (buf.length() > 0) {
				buf.append("-");
			}
			buf.append(qualifier);
		}
		return buf.toString();
	}
}
