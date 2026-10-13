File file = new File( basedir, "build.log" );
assert file.exists();

String text = file.getText("utf-8");

// reserve-network-port skips ports another process holds, so assert the guarantees rather than exact numbers
def port = { String name ->
    def m = text =~ /${name}=(\d+)/
    assert m.find() : "no ${name} in build.log"
    m.group(1) as int
}
def ports = (1..9).collect { port("port${it}") }

// each execution starts at its minPortNumber
ports[0..5].each { assert it >= 30000 }
ports[6..8].each { assert it >= 40000 }
// every port is reserved once, and in order within a build
assert ports.unique(false).size() == 9
assert ports[0..5] == ports[0..5].sort(false)
assert ports[6..8] == ports[6..8].sort(false)
return true;
