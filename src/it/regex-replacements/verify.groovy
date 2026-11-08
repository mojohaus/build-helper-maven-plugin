Properties p = new Properties()
new File( basedir, "target/classes/test.properties" ).withInputStream { p.load( it ) }
assert p.getProperty( "version" ) == "1-RC2" : "Replacements not applied by regex-property, version is " + p.getProperty( "version" )
assert p.getProperty( "other" ) == "1-GA2" : "Replacements not applied by regex-properties, other is " + p.getProperty( "other" )
