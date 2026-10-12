Properties p = new Properties()
new File( basedir, "target/classes/test.properties" ).withInputStream { p.load( it ) }
String value = p.getProperty( "version" )
assert value != null : "version property missing"
assert !value.contains( '${timestamp}' ) : "Timestamp not set"
