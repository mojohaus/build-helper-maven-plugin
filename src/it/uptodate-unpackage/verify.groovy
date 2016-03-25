File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert !text.contains('define property propname-unpackage = "outofdate"') : "Erroneously defined property propname-unpackage = \"outofdate\""
assert text.contains('define property propname-unpackage = "uptodate"') : "Failed to define property propname-unpackage = \"uptodate\""

return true;
