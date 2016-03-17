File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert text.contains('define property propname-flatten = "outofdate"') : "Failed to define property propname-flatten = \"outofdate\""
assert !text.contains('define property propname-flatten = "uptodate"') : "Erroneously defined property propname-flatten = \"uptodate\""

assert text.contains('define property propname-glob = "outofdate"') : "Failed to define property propname-glob = \"outofdate\""
assert !text.contains('define property propname-glob = "uptodate"') : "Erroneously defined property propname-glob = \"uptodate\""

assert text.contains('define property propname-identity = "outofdate"') : "Failed to define property propname-identity = \"outofdate\""
assert !text.contains('define property propname-identity = "uptodate"') : "Erroneously defined property propname-identity = \"uptodate\""

assert text.contains('define property propname-merge = "outofdate"') : "Failed to define property propname-merge = \"outofdate\""
assert !text.contains('define property propname-merge = "uptodate"') : "Erroneously defined property propname-merge = \"uptodate\""

assert text.contains('define property propname-package = "outofdate"') : "Failed to define property propname-package = \"outofdate\""
assert !text.contains('define property propname-package = "uptodate"') : "Erroneously defined property propname-package = \"uptodate\""

assert text.contains('define property propname-unpackage = "outofdate"') : "Failed to define property propname-unpackage = \"outofdate\""
assert !text.contains('define property propname-unpackage = "uptodate"') : "Erroneously defined property propname-unpackage = \"uptodate\""

return true;
