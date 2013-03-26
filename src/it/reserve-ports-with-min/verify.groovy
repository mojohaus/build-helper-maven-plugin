File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert text.contains("port1=30000")
assert text.contains("port2=30001")
assert text.contains("port3=30002")
assert text.contains("port4=30003")
assert text.contains("port5=30004")
assert text.contains("port6=30005")
assert text.contains("port7=40000")
assert text.contains("port8=40001")
assert text.contains("port9=40002")
return true;
