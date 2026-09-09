File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert text.contains('define property single.literal = "C:\\projects\\$1\\target\\"')
assert text.contains('define property single.default = "foo-bar"')
assert text.contains('define property single.empty = ""')
assert text.contains('define property multiple.literal = "C:\\projects\\$1\\target\\"')
assert text.contains('define property multiple.default = "foo-bar"')

return true;
