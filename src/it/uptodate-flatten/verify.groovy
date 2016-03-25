File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert !text.contains('define property propname-flatten = "outofdate"') : "Erroneously defined property propname-flatten = \"outofdate\""
assert text.contains('define property propname-flatten = "uptodate"') : "Failed to define property propname-flatten = \"uptodate\""

return true;
