File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");



assert text.contains("skipAddTestResourceIfMissing = true");
assert text.contains("Skipping directory: ");
assert text.contains("build-helper-maven-plugin/target/it/add-test-resource-skip-if-missing/not-existing, because it does not exist.");

return true;
