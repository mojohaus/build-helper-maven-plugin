File file = new File(basedir, 'build.log')

assert file.exists()

String text = file.getText('utf-8')

assert !text.contains('NullPointerException'): 'NullPointerException in log is unexpected'
assert text.contains('Missing (or evaluated to empty value) configuration for resource directory. Offending resource'): 'Customized exception message is expected in log'
