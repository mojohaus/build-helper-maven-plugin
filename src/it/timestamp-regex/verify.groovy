Properties p = new Properties()
new File( basedir, "target/classes/test.properties" ).withInputStream { p.load( it ) }
String value = p.getProperty( "version" )
assert value != null : "version property missing"
assert !value.contains( '${build.version}' ) : "Timestamp not set"
assert value =~ /\d\d:\d\d \d\d-\d\d-\d\d\d\d/ : "Formatted timestamp not in property, version is " + value
