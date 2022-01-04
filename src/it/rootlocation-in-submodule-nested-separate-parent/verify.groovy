File file = new File( basedir, "build.log" )
assert file.exists()

String text = file.getText("utf-8")

def rootFolderName = (text =~ /(?ms)(.*?)define property rootlocation = "(.*?)[\/\\]([^\/\\"]+)"(.*?)/)[0][3]
assert rootFolderName == "rootlocation-in-submodule-nested-separate-parent"

return true
