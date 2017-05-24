File targetFolder = new File ( basedir, "target" );

File propsFile = new File( targetFolder, "ports.properties" );
assert propsFile.exists()

Properties p = new Properties ();

p.load (new FileInputStream(propsFile));

assert p.getProperty ('port1') != null;
assert p.getProperty ('port2') != null;
assert p.getProperty ('port3') != null;

return true;
