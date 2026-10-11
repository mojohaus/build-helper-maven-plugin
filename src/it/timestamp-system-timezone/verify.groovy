File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert text.contains('define property system.current = "+14:00"')
assert text.contains('define property system.build = "+14:00"')
assert text.contains('define property default.timezone = "Z"')
assert text.contains('define property explicit.timezone = "-07:00"')

return true;
