File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert text =~ /\[DEBUG\] define property os\.name = "(.*)"/
assert text =~ /\[DEBUG\] define property os\.family = "(.*)"/
assert text =~ /\[DEBUG\] define property os\.version = "(.*)"/
assert text =~ /\[DEBUG\] define property os\.arch = "(.*)"/

assert text.contains("[echo] os.name=")
assert text.contains("[echo] os.version=")
assert text.contains("[echo] os.family=")
assert text.contains("[echo] os.arch=")
return true;
