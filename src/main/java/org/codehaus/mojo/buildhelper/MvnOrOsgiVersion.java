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
	private String majorVersionOriginal;
	private String minorVersionOriginal;
	private String incrementalVersionOriginal;
	private String buildNumberOriginal;
	private final boolean preserveZero;


	public MvnOrOsgiVersion(String version, boolean preserveZero) {
		this.preserveZero = preserveZero;
		parseVersion(version);
	}

	public MvnOrOsgiVersion(String version) {
		this(version, false);
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
			// Original
			this.majorVersionOriginal = m.group("major");
			this.minorVersionOriginal = m.group("minor") == null ? null : m.group("minor").substring(1);;
			this.incrementalVersionOriginal = m.group("incr") == null ? null : m.group("incr").substring(1);
			this.buildNumberOriginal = m.group("build") == null ? null : m.group("build").substring(1);
			this.qualifier = m.group("qualifier") == null ? null : m.group("qualifier").replaceFirst("^[.-]", "");

			// int value
			this.majorVersion = this.majorVersionOriginal == null ? null : valueOf(this.majorVersionOriginal);
            this.minorVersion = this.minorVersionOriginal == null ? null : valueOf(this.minorVersionOriginal);
            this.incrementalVersion = this.incrementalVersionOriginal == null ? null : valueOf(this.incrementalVersionOriginal);
            this.buildNumber = this.buildNumberOriginal == null ? null : valueOf(this.buildNumberOriginal);
        }
	}

	public String getNextMajorVersion() {
		final int major = getMajorVersion();
		final int nextMajor = major+1;
		if (preserveZero && (major > 0 && major < 9) && this.majorVersionOriginal.startsWith("0")) {
			return "0"+nextMajor;
		} else {
			return String.valueOf(nextMajor);
		}
	}

	public String getNextMinorVersion() {
		final int minor = getMinorVersion();
		final int nextMinor = minor+1;
		if (preserveZero && (minor > 0 && minor < 9) && this.minorVersionOriginal.startsWith("0")) {
			return "0"+nextMinor;
		} else {
			return String.valueOf(nextMinor);
		}
	}

	public String getNextIncrementalVersion() {
		final int incr = getIncrementalVersion();
		final int nextIncr = incr+1;
		if (preserveZero && (incr > 0 && incr < 9) && this.incrementalVersionOriginal.startsWith("0")) {
			return "0"+nextIncr;
		} else {
			return String.valueOf(nextIncr);
		}
	}

	public String getNextBuildNumber() {
		final int build = getBuildNumber();
		final int nextBuild = build+1;
		if (preserveZero && (build > 0 && build < 9) && this.buildNumberOriginal.startsWith("0")) {
			return "0"+nextBuild;
		} else {
			return String.valueOf(nextBuild);
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
