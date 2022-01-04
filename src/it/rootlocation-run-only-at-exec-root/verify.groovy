File file = new File( basedir, "build.log" )
assert file.exists()

String text = file.getText("utf-8")

def rootFolderName = (text =~ /(?ms)(.*?)define property rootlocation = "(.*?)[\/\\]([^\/\\"]+)"(.*?)/)[0][3]
assert rootFolderName == "rootlocation-run-only-at-exec-root"

assert text.contains("Skip getting the rootlocation in this project because it's not the Execution Root")

return true
