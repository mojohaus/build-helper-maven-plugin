
File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert text.contains("major: 20")
assert text.contains("minor: 4")
assert text.contains("incremental: 0")
assert text.contains("buildnumber: 0")
assert text.contains("qualifier: 0-SNAPSHOT")

assert text.contains("define property parsedVersion.majorVersion = \"20\"")
assert text.contains("define property parsedVersion.minorVersion = \"4\"")
assert text.contains("define property parsedVersion.incrementalVersion = \"0\"")
assert text.contains("define property parsedVersion.nextMajorVersion = \"21\"")
assert text.contains("define property parsedVersion.nextMinorVersion = \"5\"")
assert text.contains("define property parsedVersion.nextIncrementalVersion = \"1\"")
assert text.contains("define property parsedVersion.nextBuildNumber = \"1\"")
assert text.contains("define property parsedVersion.qualifier = \"0-SNAPSHOT\"")
assert text.contains("define property parsedVersion.buildNumber = \"0\"")
assert text.contains("define property parsedVersion.osgiVersion = \"20.4.0.0-SNAPSHOT\"")

return true;
