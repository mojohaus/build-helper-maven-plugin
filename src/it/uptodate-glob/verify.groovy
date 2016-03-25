File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert !text.contains('define property propname-glob = "outofdate"') : "Erroneously defined property propname-glob = \"outofdate\""
assert text.contains('define property propname-glob = "uptodate"') : "Failed to define property propname-glob = \"uptodate\""

return true;
