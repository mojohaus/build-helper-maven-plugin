File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

// assert latest release of org.apache.continuum:continuum - it's in apache attic, there will be no new releases any more
assert text.contains("myReleasedVersion.version=1.3.8")
assert text.contains("myReleasedVersion.majorVersion=1")
assert text.contains("myReleasedVersion.minorVersion=3")
assert text.contains("myReleasedVersion.incrementalVersion=8")

return true;
