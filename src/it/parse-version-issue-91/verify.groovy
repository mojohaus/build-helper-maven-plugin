
File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

// versionString given on command line. see invoker.properties.
assert text.contains("(f) versionString = 10.0.2-4545-RC1")
//
assert text.contains("major: 10")
assert text.contains("minor: 0")
assert text.contains("incremental: 2")
assert text.contains("buildnumber: 4545")
assert text.contains("qualifier: -RC1")

assert text.contains("define property parsedVersion.majorVersion = \"10\"")
assert text.contains("define property parsedVersion.minorVersion = \"0\"")
assert text.contains("define property parsedVersion.incrementalVersion = \"2\"")
assert text.contains("define property parsedVersion.buildNumber = \"4545\"")
assert text.contains("define property parsedVersion.nextMajorVersion = \"11\"")
assert text.contains("define property parsedVersion.nextMinorVersion = \"1\"")
assert text.contains("define property parsedVersion.nextIncrementalVersion = \"3\"")
assert text.contains("define property parsedVersion.nextBuildNumber = \"4546\"")
assert text.contains("define property formattedVersion.majorVersion = \"10\"")
assert text.contains("define property formattedVersion.minorVersion = \"00\"")
assert text.contains("define property formattedVersion.incrementalVersion = \"02\"")
assert text.contains("define property formattedVersion.buildNumber = \"4545\"")
assert text.contains("define property formattedVersion.nextMajorVersion = \"11\"")
assert text.contains("define property formattedVersion.nextMinorVersion = \"01\"")
assert text.contains("define property formattedVersion.nextIncrementalVersion = \"03\"")
assert text.contains("define property formattedVersion.nextBuildNumber = \"4546\"")
assert text.contains("define property parsedVersion.qualifier = \"-RC1\"")
assert text.contains("define property parsedVersion.qualifier? = \"--RC1\"")
assert text.contains("define property parsedVersion.osgiVersion = \"10.0.2.4545-RC1\"")

return true;