Properties module1Props = new Properties()
new File( basedir, "module-1/target/classes/test.properties" ).withInputStream { module1Props.load( it ) }
Properties module2Props = new Properties()
new File( basedir, "module-2/target/classes/test.properties" ).withInputStream { module2Props.load( it ) }

String module1ModuleVersion = module1Props.getProperty( "module.version" )
String module1SessionVersion = module1Props.getProperty( "session.version" )
String module2ModuleVersion = module2Props.getProperty( "module.version" )
String module2SessionVersion = module2Props.getProperty( "session.version" )

assert module1ModuleVersion != null && module2ModuleVersion != null : "module.version property missing"
assert module1SessionVersion != null && module2SessionVersion != null : "session.version property missing"
assert !module1ModuleVersion.contains( '${module.build.timestamp}' ) : "Module timestamp not set in module-1"
assert !module2ModuleVersion.contains( '${module.build.timestamp}' ) : "Module timestamp not set in module-2"
assert !module1SessionVersion.contains( '${session.build.timestamp}' ) : "Session timestamp not set in module-1"
assert !module2SessionVersion.contains( '${session.build.timestamp}' ) : "Session timestamp not set in module-2"
assert module1ModuleVersion != module2ModuleVersion : "Module timestamps should not match by design"
assert module1SessionVersion == module2SessionVersion : "Session timestamps should match by design"
