File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert text.contains("port1=")
assert text.contains("port2=")
assert text.contains("port3=")

return true;
