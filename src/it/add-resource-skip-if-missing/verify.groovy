File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

assert text.contains("skipAddResourceIfMissing = true");
assert text.contains("Skipping directory: ");
assert text.contains("not-existing, because it does not exist");

return true;
