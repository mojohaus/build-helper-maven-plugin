File file = new File(basedir, "build.log");
assert file.exists();

String text = file.getText("utf-8");

def FS = File.separator

assert 2 == text.count("org${FS}codehaus${FS}mojo${FS}remove-project-artifact-it removed.")  : 'removed log should be present twice'
