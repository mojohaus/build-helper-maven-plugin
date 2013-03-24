File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert text.contains('define property propname = "hi \u00A0\u00A0\u00A0"') , "text=<"+text+">"

return true;
