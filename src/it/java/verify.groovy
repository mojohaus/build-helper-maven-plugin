File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert text =~ /\[DEBUG\] define property build\.environment\.java\.runtime\.name = "(.*)"/

assert text =~ /\[DEBUG\] define property build\.environment\.java\.runtime\.name = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.java\.runtime\.version = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.java\.specification\.name = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.java\.specification\.vendor = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.java\.specification\.version = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.java\.vendor = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.java\.version = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.java\.vm\.name = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.java\.vm\.info = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.java\.vm\.vendor = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.java\.vm\.version = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.java\.vm\.specification\.name = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.java\.vm\.specification\.vendor = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.java\.vm\.specification\.version = "(.*)"/
assert text =~ /\[DEBUG\] define property build\.environment\.sun\.management\.compiler = "(.*)"/

//Currently other checks are not possible, cause based on a value
//the IT will be plattform dependant.
assert text.contains("[echo] build.environment.java.version=")
assert text.contains("[echo] build.environment.java.specification.name=")
assert text.contains("[echo] build.environment.java.specification.version")
assert text.contains("[echo] build.environment.java.runtime.name")
assert text.contains("[echo] build.environment.java.runtime.version")
assert text.contains("[echo] build.environment.java.vendor=")
assert text.contains("[echo] build.environment.java.vm.name=")
assert text.contains("[echo] build.environment.java.vm.info=")
assert text.contains("[echo] build.environment.java.vm.specification.name=")
assert text.contains("[echo] build.environment.java.vm.specification.vendor=")
assert text.contains("[echo] build.environment.java.vm.specification.version=")
assert text.contains("[echo] build.environment.sun.management.compiler=")

return true;
