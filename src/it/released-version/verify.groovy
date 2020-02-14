File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

// its difficult to check the positove case with an existing released version in an IT - so currently we test only the case when no version is detected
assert text.contains("No released version found.")

return true;
