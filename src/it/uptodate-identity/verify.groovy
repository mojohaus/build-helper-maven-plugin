File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert !text.contains('define property propname-identity = "outofdate"') : "Erroneously defined property propname-identity = \"outofdate\""
assert text.contains('define property propname-identity = "uptodate"') : "Failed to define property propname-identity = \"uptodate\""

return true;
