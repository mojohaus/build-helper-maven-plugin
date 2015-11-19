File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

// FIXME: That IT could fail if the port is already taken
// Only check if the reserved values are *higher* than 1024?
assert text.contains("port1=9000")
assert text.contains("port2=9001")
assert text.contains("port3=9002")
return true;
