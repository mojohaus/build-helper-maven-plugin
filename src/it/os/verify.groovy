File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

//Currently a test can only check the output of ant plugin
//but not the function directory, cause
//this would make the whole IT os dependant.
assert text.contains("[echo] os.name=")
assert text.contains("[echo] os.version=")
assert text.contains("[echo] os.family=")
assert text.contains("[echo] os.arch=")
return true;
