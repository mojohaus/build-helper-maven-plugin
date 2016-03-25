File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert !text.contains('define property propname-package = "outofdate"') : "Erroneously defined property propname-package = \"outofdate\""
assert text.contains('define property propname-package = "uptodate"') : "Failed to define property propname-package = \"uptodate\""

return true;
