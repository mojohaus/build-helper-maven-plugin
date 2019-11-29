File file = new File( basedir, "build.log" )
assert file.exists()

String text = file.getText("utf-8")

assert text.matches('(?ms)(.*?)define property rootlocation = "(.*)/rootlocation"(.*?)')

return true