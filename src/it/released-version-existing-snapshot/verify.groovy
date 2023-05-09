File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

// we have SNAPSHOT on list
assert text.matches('(?ms)^.*Resolved versions: .*-SNAPSHOT.*$')

// but released version is not a SNAPSHOT
assert text.matches( '(?ms)^.*Released version: .*$')
assert !text.matches( '(?ms)^.*Released version: .*-SNAPSHOT.*$')
