File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert !text.contains('define property propname-flatten = "outofdate"') : "Erroneously defined property propname-flatten = \"outofdate\""
assert text.contains('define property propname-flatten = "uptodate"') : "Failed to define property propname-flatten = \"uptodate\""

assert !text.contains('define property propname-glob = "outofdate"') : "Erroneously defined property propname-glob = \"outofdate\""
assert text.contains('define property propname-glob = "uptodate"') : "Failed to define property propname-glob = \"uptodate\""

assert !text.contains('define property propname-identity = "outofdate"') : "Erroneously defined property propname-identity = \"outofdate\""
assert text.contains('define property propname-identity = "uptodate"') : "Failed to define property propname-identity = \"uptodate\""

assert !text.contains('define property propname-merge = "outofdate"') : "Erroneously defined property propname-merge = \"outofdate\""
assert text.contains('define property propname-merge = "uptodate"') : "Failed to define property propname-merge = \"uptodate\""

assert !text.contains('define property propname-package = "outofdate"') : "Erroneously defined property propname-package = \"outofdate\""
assert text.contains('define property propname-package = "uptodate"') : "Failed to define property propname-package = \"uptodate\""

assert !text.contains('define property propname-unpackage = "outofdate"') : "Erroneously defined property propname-unpackage = \"outofdate\""
assert text.contains('define property propname-unpackage = "uptodate"') : "Failed to define property propname-unpackage = \"uptodate\""

return true;
